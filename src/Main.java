import cli.CompilerCommand;
import error.CompilerError;
import org.apache.commons.cli.ParseException;

public class Main {
    public static void main(String[] strings) throws ParseException, CompilerError {
        // Create a new instance of the compiler command and invoke it
        CompilerCommand command = new CompilerCommand();
        command.invoke(strings);
    }
}