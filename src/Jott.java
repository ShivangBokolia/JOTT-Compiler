import execution.JottRunner;
import parsing.JottDecorator;
import parsing.JottGrammar;
import parsing.JottParser;
import parsing.Node;
import scanning.JottScanner;
import scanning.Token;

import java.util.List;

public class Jott {

    public static void main(String [] args){
        try {
            if (args.length != 1) {
                System.out.println("Usage: java Jott filename");
                System.exit(-1);
            }

            JottScanner jottScanner = new JottScanner(args[0]);
            List<Token> result = jottScanner.scanFile();
            JottGrammar.buildGrammar();
            JottParser parser = new JottParser();
            Node root = parser.parseTake2(result);
//            root.inorderPrint();
            Node decoratedTreeRoot = new Node("program", null);
            JottDecorator decoratoredTree = new JottDecorator();
            decoratoredTree.decorateParseTree(root.getChild(0), decoratedTreeRoot, null);


            JottRunner runner = new JottRunner();
            runner.runCode(decoratedTreeRoot, false);
        }catch(IndexOutOfBoundsException e){
            System.out.println("Invalid Syntax Here");
            System.exit(-1);
        }
    }
}
