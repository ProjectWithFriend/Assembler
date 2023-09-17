package Tokenizer;

public class IterateTokenizer implements Tokenizer {
    private final String src;
    private String next;
    private String prev;
    private int pos;

    private boolean isFirst = true;

    public IterateTokenizer(String src) {
        this.src = src;
        pos = 0;
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
    }

    private void processWhiteSpace() {
        while (pos < src.length() && Character.isWhitespace(src.charAt(pos))) {
            pos++;
        }
    }


    private void computeNext() {
        if (src == null) return;
        StringBuilder sb = new StringBuilder();

        //ignore whitespace
        processWhiteSpace();

        if (pos == src.length()) {
            prev = next;
            next = null;
            return;
        }

        char c = src.charAt(pos);
        if (c == '#') { //comment
            processSingleLineComment();
            isFirst = true;
            pos++;
            processWhiteSpace();
        }

        if (isFirst && pos < src.length() && Character.isDigit(src.charAt(pos))) {
            throw new TokenizerException.BadCharacter(src.charAt(pos));
        }
        while (pos < src.length() && !Character.isWhitespace(src.charAt(pos))) {
            sb.append(src.charAt(pos));
            pos++;
        }

        prev = next;
        next = sb.toString();
        isFirst = pos < src.length() && src.charAt(pos) == '\n';
    }
}
