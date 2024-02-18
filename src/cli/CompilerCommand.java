package cli;

import error.CompilerError;
import org.apache.commons.cli.*;
import token.TokenScanner;

import java.io.FileReader;
import java.io.IOException;
import java.util.Objects;

public class CompilerCommand {
    public static final String NAME = "icd-example";

    private final Options options;

    public CompilerCommand() {
        this.options = new Options();

        this.options.addOption("d", "debug", false, "enable debug output");
        this.options.addOption("h", "help", false, "print this message and exit");
        this.options.addOption("o", "outfile", true, "destination path for emitted LLVM-IR");
    }

    public void invoke(String[] strings) throws ParseException, CompilerError {
        CommandLineParser parser = new DefaultParser();
        CommandLine invocation = parser.parse(this.options, strings);

        if (invocation.hasOption('h')) {
            this.printHelp();
            return;
        }

//        String outfile = Objects.requireNonNullElse(invocation.getOptionValue('o'), "out.ll");
        String[] infiles = invocation.getArgs();

        for (String infile : infiles) {
            try (FileReader reader = new FileReader(infile)) {
                TokenScanner scanner = new TokenScanner(reader);

                while (scanner.scanToken() != null) {
                    System.out.println(scanner.getToken());
                }
            }
            catch (IOException cause) {
                throw new CompilerError("unable to open '" + infile + "'", cause);
            }
        }
    }

    public void printHelp() {
        new HelpFormatter().printHelp(NAME + " [options] infiles...", this.options);
    }
}
