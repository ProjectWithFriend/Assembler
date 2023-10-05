package Tokenizer;

import java.util.ArrayList;

public interface Tokenizer {
    boolean hasNext(); //To check if there is any token left

    String peek(); //To peek the next token

    boolean peek(String s); //To peek the next token and check if it is the same as the given string

    String consume(); //To consume the next token

    boolean consume(String s); //To consume the next token and check if it is the same as the given string

    ArrayList<ArrayList<String>> getMappingInstruction(); //To get the mapping instruction
}
