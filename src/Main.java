import Tokenizer.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
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
            Tokenizer tokenizer = new IterateTokenizer(src);
            while(tokenizer.hasNext()) {
                System.out.println(tokenizer.consume());
            }

            ArrayList<ArrayList<String>> mappingInstruction = tokenizer.getMappingInstruction();
            for (ArrayList<String> line : mappingInstruction) {
                for (String token : line) {
                    System.out.print(token + " ");
                }
                System.out.println();
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}