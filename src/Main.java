import scanning.JottScanner;
import scanning.Token;

import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String [] args){
        if (args.length < 3){
            System.out.println("Usage: java Jott program.j");
            System.exit(0);
        }
        JottScanner jottScanner = new JottScanner(args[2]);
        List<Token> result = jottScanner.scanFile();
        for (Token t: result){
            System.out.println(t);
        }
    }
}
