package Tokenizer;


import java.util.ArrayList;

public class IterateTokenizer implements Tokenizer {
    private enum instruction {
        add, nand, lw, sw, beq, jalr, halt, noop
    }

    private final String src; // source code to tokenize
    private String next; // the next token (char)
    private String prev; // the previous token (char)
    private int pos; // current position in the source
    private boolean isFirst = true; // used to check if it's first of new line
    private final ArrayList<ArrayList<String>> mappingInstruction; // mapping instruction
    private boolean isFirstLine = true; //used to check if it's first line of source code
    private int filledField = 0; //used to track how many field is filled
    private int line = 0;

    /**
     * Check if given string is in enum
     *
     * @param s given string
     * @return true if given string is in enum
     */
    private boolean findEnum(String s) {
        for (instruction i : instruction.values()) {
            if (i.name().equals(s)) {
                return true;
            }
        }
        return false;
    }

    /**
     * @return mapping instruction
     */
    public ArrayList<ArrayList<String>> getMappingInstruction() {
        return mappingInstruction;
    }


    /**
     * Constructor
     *
     * @param src source code to tokenize
     */
    public IterateTokenizer(String src) {
        this.src = src;
        pos = 0;
        mappingInstruction = new ArrayList<>(); // initialize mapping instruction
        computeNext(); // start computation of next token
    }

    @Override
    public boolean hasNext() {
        return next != null;
    }

    @Override
    public String peek() {
        if (next == null)
            throw new TokenizerException.NoToken(prev);
        return next;
    }

    @Override
    public boolean peek(String s) {
        if (!hasNext()) {
            return false;
        } else {
            return next.equals(s);
        }
    }

    @Override
    public String consume() {
        if (!hasNext()) {
            throw new TokenizerException.NoToken(prev);
        } else {
            String result = next;
            computeNext();
            return result;
        }
    }

    /**
     * Consume the next token and check if it is the same as the given string
     *
     * @param s given string
     * @return true if it is the same as the given string
     */
    @Override
    public boolean consume(String s) {
        if (!hasNext()) {
            throw new TokenizerException.NoToken(prev);
        } else {
            if (next.equals(s)) {
                computeNext();
                return true;
            } else {
                return false;
            }
        }
    }

    /**
     * Skip the comment
     */
    private void processSingleLineComment() {
        while (pos < src.length() && src.charAt(pos) != '\n') {
            pos++;
        }
        //next line
        pos++;
        filledField = 0;
        line++;
        isFirst = true;
        mappingInstruction.add(new ArrayList<>());
    }


    /**
     * Skip the whitespace
     */
    private void processWhiteSpace() {
        while (pos < src.length() && Character.isWhitespace(src.charAt(pos)) && src.charAt(pos) != '\n') {
            pos++;
        }
    }

    /**
     * Skip the whitespace and comment
     */
    private void processSkipSpaceAndComment() {
        if (src.charAt(pos) == '\n') { // if there is no comment prepare for next line
            pos = pos < src.length() + 1 ? pos + 1 : pos;
            line++;
            filledField = 0;
            isFirst = true;
            mappingInstruction.add(new ArrayList<>());
        } else { // if there is comment skip it
            processSingleLineComment();
        }
    }

    /**
     * Main method to compute next token
     */
    private void computeNext() {
        if (src == null) return;
        StringBuilder sb = new StringBuilder(); // use string builder to build string because it's easier to append or insert, and it's mutable and faster than string

        //if first line of source code, create arraylist just for first line
        if (isFirstLine) {
            mappingInstruction.add(new ArrayList<>());
            isFirstLine = false;
        }

        //ignore whitespace
        processWhiteSpace();

        //if end of file
        if (pos == src.length()) {
            prev = next;
            next = null;
            return;
        }

        if (filledField != 3) { // It's not comment part
            while (pos < src.length() && !Character.isWhitespace(src.charAt(pos))) { // append char to construct string of that word
                sb.append(src.charAt(pos));
                pos++;
            }
//            .fill command
            if (sb.toString().equals(".fill")) { // if it's .fill command
                mappingInstruction.get(line).add(sb.toString()); // add .fill to mapping instruction
                filledField = 2; //update filled field to 2 because .fill need only one field after it
            } else if (prev != null && prev.equals(".fill")) { // if it's field after .fill
                mappingInstruction.get(line).add(sb.toString());
                filledField = 3; //update for tell that is end of field
            } else {
                if (!findEnum(sb.toString()) && isFirst) { // It's label
                    if (pos < src.length() && Character.isDigit(src.charAt(pos))) { // if label start with number
                        throw new TokenizerException.BadCharacter(src.charAt(pos));
                    } else { // It's label
                        mappingInstruction.get(line).add(sb.toString());
                        isFirst = false;
                    }
                } else if (findEnum(sb.toString())) { // It's instruction
                    if (sb.toString().equals("noop") || sb.toString().equals("halt")) { // if it's noop or halt
                        mappingInstruction.get(line).add(sb.toString());
                        processWhiteSpace();
                        processSkipSpaceAndComment();
                    } else if (sb.toString().equals("jalr")) { // if it's jalr
                        mappingInstruction.get(line).add(sb.toString());
                        isFirst = false;
                        filledField = 1;
                    } else { // if it's other instruction, it needs 3 field type R,I
                        mappingInstruction.get(line).add(sb.toString());
                        isFirst = false;
                    }
                } else { // It's field
                    mappingInstruction.get(line).add(sb.toString());
                    filledField++;
                    isFirst = false;
                }
            }
        } else { // process comment of .fill
            processWhiteSpace();
            processSkipSpaceAndComment();
        }


        prev = next;
        next = sb.toString();
    }
}
