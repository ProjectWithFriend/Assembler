package Tokenizer;

public interface Tokenizer {
    boolean hasNext();

    String peek();

    boolean peek(String s);

    String consume();

    boolean consume(String s);

    int getLine();
}
