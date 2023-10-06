import Parser.*;
import Tokenizer.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.Scanner;
public class Main {
    public static void main(String[] args) {
//        read input.txt file
        File file = new File("คอมบิ.txt");
        StringBuilder sb = new StringBuilder();
        try {
            Scanner scanner = new Scanner(file);
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                if(Objects.equals(line, "")) continue;
                sb.append(line);
                sb.append("\n");
            }
            scanner.close();
            String src = sb.toString();
            Tokenizer tokenizer = new IterateTokenizer(src);
            while(tokenizer.hasNext()) {
                tokenizer.consume();
            }

            Parser parser = new MappingParser(tokenizer.getMappingInstruction());

            Path path = Paths.get("output.txt");
            parser.PrintCode();
            String contents = parser.DecimalCode();

            Files.writeString(path, contents, StandardCharsets.UTF_8);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}