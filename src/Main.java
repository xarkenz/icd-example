import error.CompilerError;
import token.TokenScanner;

import java.io.FileReader;
import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        String filename = "test_program.txt";

        try (FileReader reader = new FileReader(filename)) {
            try {
                TokenScanner scanner = new TokenScanner(reader);

                while (scanner.scanToken() != null) {
                    System.out.println(scanner.getToken());
                }
            }
            catch (CompilerError error) {
                error.printStackTrace();
            }
        }
    }
}