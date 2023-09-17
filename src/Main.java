import Tokenizer.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
public class Main {
    public static void main(String[] args) {
//        read input.txt file
        File file = new File("input.txt");
        StringBuilder sb = new StringBuilder();
        try {
            Scanner scanner = new Scanner(file);
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                sb.append(line);
                sb.append("\n");
            }
            scanner.close();
            String src = sb.toString();
//            System.out.println(src);
            Tokenizer tokenizer = new IterateTokenizer(src);
            while(tokenizer.hasNext()) {
                System.out.println(tokenizer.consume());
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}