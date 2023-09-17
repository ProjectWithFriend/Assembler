import Tokenizer.Tokenizer;
import Tokenizer.IterateTokenizer;

// Press Shift twice to open the Search Everywhere dialog and type `show whitespaces`,
// then press Enter. You can now see whitespace characters in your code.
public class Main {

    public static void main(String[] args) {
        tokenizer test = new IterateTokenizer("test");
        System.out.println(test.comsume);
    }
}