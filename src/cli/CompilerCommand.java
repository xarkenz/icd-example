package cli;

import codegen.Generator;
import error.CompilerError;
import org.apache.commons.cli.*;
import syntax.Parser;
import token.TokenScanner;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Objects;

/**
 * Class for handling command-line usage of the compiler.
 * @see CommandLine
 */
public class CompilerCommand {
    /**
     * The keyword representing the compiler command.
     */
    public static final String NAME = "icd-example";

    /**
     * The set of allowed options for this command.
     */
    private final Options options;

    public CompilerCommand() {
        this.options = new Options();

        // Add all the allowed options
        this.options.addOption("d", "debug", false, "enable debug output");
        this.options.addOption("h", "help", false, "print this message and exit");
        this.options.addOption("o", "outfile", true, "destination path for emitted LLVM-IR");
    }

    /**
     * Parse a command-line invocation of the command and execute the corresponding logic.
     * @param strings Array of strings passed in invocation of the command.
     * @throws ParseException Thrown if the command invocation could not be parsed properly.
     * @throws CompilerError Thrown if the compiler throws an error.
     */
    public void invoke(String[] strings) throws ParseException, CompilerError {
        // Parse the strings passed with the invocation
        CommandLineParser argParser = new DefaultParser();
        CommandLine invocation = argParser.parse(this.options, strings);

        if (invocation.hasOption('h')) {
            // Trigger the help menu and exit
            this.printHelp();
            return;
        }

        boolean enableDebug = invocation.hasOption('d');

        String outfile = Objects.requireNonNullElse(invocation.getOptionValue('o'), "out.ll");
        String[] infiles = invocation.getArgs();

        for (String infile : infiles) {
            try (FileReader reader = new FileReader(infile)) {
                // Create the scanner with file input
                TokenScanner scanner = new TokenScanner(reader);
                // Create the parser using the scanner
                Parser parser = new Parser(scanner);
                // Create a new writer for the output file
                try (FileWriter writer = new FileWriter(outfile)) {
                    // Run the pipeline to scan, parse, generate and emit to the output file
                    Generator.generate(writer, parser, infile, enableDebug);

                    if (enableDebug) {
                        System.out.println("Successfully written to '" + outfile + "'.");
                    }
                }
                catch (IOException cause) {
                    throw new CompilerError("unable to create file '" + outfile + "'", cause);
                }
            }
            catch (IOException cause) {
                throw new CompilerError("unable to open file '" + infile + "'", cause);
            }
        }
    }

    /**
     * Print the command help message to standard output.
     */
    public void printHelp() {
        new HelpFormatter().printHelp(NAME + " [options] infiles...", this.options);
    }
}
