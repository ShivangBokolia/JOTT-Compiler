import parsing.JottGrammar;
import parsing.JottParser;
import parsing.Node;
import scanning.JottScanner;
import scanning.Token;

import java.util.List;
import java.util.Map;

public class Main {

    public static void main(String [] args){
        if (args.length < 3){
            System.out.println("Usage: java Jott " + args[2]);
            System.exit(-1);
        }
        JottScanner jottScanner = new JottScanner(args[2]);
        List<Token> result = jottScanner.scanFile();
//        for(Token r: result){
//            System.out.println(r);
//        }

        JottGrammar.buildGrammar();

        JottParser parser = new JottParser();
        Node root = parser.parseTake2(result);
        root.inorderPrint();

//        System.out.println(JottParser.symbolTable);
    }
}
