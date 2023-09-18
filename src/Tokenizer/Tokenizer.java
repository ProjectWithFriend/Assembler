package Tokenizer;

import java.util.ArrayList;

public interface Tokenizer {
    boolean hasNext();

    String peek();

    boolean peek(String s);

    String consume();

    boolean consume(String s);

    ArrayList<ArrayList<String>> getMappingInstruction();
}
