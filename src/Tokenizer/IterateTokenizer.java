package Tokenizer;


import java.util.ArrayList;

public class IterateTokenizer implements Tokenizer {
    private enum instruction {
        add, nand, lw, sw, beq, jalr, halt, noop
    }

    private final String src;
    private String next;
    private String prev;
    private int pos;
    private boolean isFirst = true;
    private final ArrayList<ArrayList<String>> mappingInstruction;
    private boolean isFirstLine = true;
    private int filledField = 0;
    private int line = 0;

    private boolean findEnum(String s) {
        for (instruction i : instruction.values()) {
            if (i.name().equals(s)) {
                return true;
            }
        }
        return false;
    }

    public ArrayList<ArrayList<String>> getMappingInstruction() {
        return mappingInstruction;
    }


    public IterateTokenizer(String src) {
        this.src = src;
        pos = 0;
        mappingInstruction = new ArrayList<>();
        computeNext();
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


    private void processWhiteSpace() {
        while (pos < src.length() && Character.isWhitespace(src.charAt(pos)) && src.charAt(pos) != '\n') {
            pos++;
        }
    }

    private void processSkipSpaceAndComment(){
        if (src.charAt(pos) == '\n') { // No comment
            pos = pos < src.length()+1 ? pos + 1 : pos;
            line++;
            filledField = 0;
            isFirst = true;
            mappingInstruction.add(new ArrayList<>());
        } else { // comment
            processSingleLineComment();
        }
    }

    private void computeNext() {
        if (src == null) return;
        StringBuilder sb = new StringBuilder();

        //if first line create arraylist just for first line
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
            while (pos < src.length() && !Character.isWhitespace(src.charAt(pos))) {
                sb.append(src.charAt(pos));
                pos++;
            }
//            .fill command
            if (sb.toString().equals(".fill")) {
                mappingInstruction.get(line).add(sb.toString());
                filledField = 2;
            }else if(prev != null && prev.equals(".fill")){
                mappingInstruction.get(line).add(sb.toString());
                filledField = 3;
            }else{

                if (!findEnum(sb.toString()) && isFirst) { // It's label
                    if (pos < src.length() && Character.isDigit(src.charAt(pos))) {
                        throw new TokenizerException.BadCharacter(src.charAt(pos));
                    } else { // It's label
                        mappingInstruction.get(line).add(sb.toString());
                        isFirst = false;
                    }
                } else if (findEnum(sb.toString())) { // It's instruction
                    if (sb.toString().equals("noop") || sb.toString().equals("halt")) {
                        mappingInstruction.get(line).add(sb.toString());
                        processWhiteSpace();
                        processSkipSpaceAndComment();
                    }else if(sb.toString().equals("jalr")){
                        mappingInstruction.get(line).add(sb.toString());
                        isFirst = false;
                        filledField = 1;
                    }
                    else{
                        mappingInstruction.get(line).add(sb.toString());
                        isFirst = false;
                    }
                } else { // It's field
                    mappingInstruction.get(line).add(sb.toString());
                    filledField++;
                    isFirst = false;
                }
            }
        } else { //
            processWhiteSpace();
            processSkipSpaceAndComment();
        }


        prev = next;
        next = sb.toString();
    }
}
