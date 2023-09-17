package Tokenizer;

public class IterateTokenizer implements Tokenizer {
    private final String src;
    private String next;
    private String prev;
    private int pos;
    private int line;

    public IterateTokenizer(String src) {
        this.src = src;
        pos = 0;
        line = 1;
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

    @Override
    public int getLine() {
        return line;
    }

    private void processSingleLineComment() {
        while (pos < src.length() && src.charAt(pos) != '\n') {
            pos++;
        }
    }

    private boolean ignoreCharacter(char c) {
        return Character.isWhitespace(c) || c == '#' || c == '"';
    }
    private boolean isLetter(char c) {
        return Character.isLetter(c) || c == '_';
    }

    private void computeNext() {
        if (src == null) return;
        StringBuilder sb = new StringBuilder();
        while (pos < src.length() && ignoreCharacter(src.charAt(pos))) {
            if (src.charAt(pos) == '\n')
                line++;
            if (src.charAt(pos) == '#')
                processSingleLineComment();
            else
                pos++;
        }

        if (pos == src.length()) {
            prev = next;
            next = null;
            return;
        }
        char c = src.charAt(pos);
        if (Character.isDigit(c)) {
            while (pos < src.length() && Character.isDigit(src.charAt(pos))) {
                sb.append(src.charAt(pos));
                pos++;
            }
        } else if (isLetter(c) || c == '_') {
            while (pos < src.length() && isLetter(src.charAt(pos))) {
                sb.append(src.charAt(pos));
                pos++;
            }
        } else if ("()+-*/%^{}=".contains(String.valueOf(c))) {
            sb.append(src.charAt(pos));
            pos++;
        } else {
            throw new TokenizerException.BadCharacter(c);
        }
        prev = next;
        next = sb.toString();
    }
}
