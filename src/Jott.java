import execution.JottRunner;
import parsing.JottDecorator;
import parsing.JottGrammar;
import parsing.JottParser;
import parsing.Node;
import scanning.JottScanner;
import scanning.Token;

import java.util.List;
import java.util.Map;

public class Jott {

    public static void main(String [] args){
//        System.out.println(args[0]);
        if (args.length != 3){
            System.out.println("Usage: java Jott filename");
            System.exit(-1);
        }
        JottScanner jottScanner = new JottScanner(args[2]);
        List<Token> result = jottScanner.scanFile();
        JottGrammar.buildGrammar();
        JottParser parser = new JottParser();
        Node root = parser.parseTake2(result);
        Node decoratedTreeRoot = new Node("program", null);
        JottDecorator decoratoredTree = new JottDecorator();
        decoratoredTree.decorateParseTree(root, decoratedTreeRoot);

        JottRunner runner = new JottRunner();
        runner.runCode(decoratedTreeRoot);
    }
}
