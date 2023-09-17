package Tokenizer;

public class TokenizerException extends RuntimeException  {
    public TokenizerException(String message) {
        super(message);
    }

    public static class NoToken extends TokenizerException {
        private static String tailing(String after) {
            if (after != null)
                return String.format(", after '%s'", after);
            else
                return "";
        }

        public NoToken(String after) {
            super("expected more token" + tailing(after));
        }
    }

    public static class BadCharacter extends TokenizerException {
        public BadCharacter(char got) {
            super("Bad character " + got);
        }
    }
}
