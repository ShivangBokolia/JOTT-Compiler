package parsing;

import scanning.Token;

import java.util.ArrayList;
import java.util.List;

public class JottDecorator {

    private void getIValues (Node valueNode, Node iExpr){
        if (iExpr.getChild(0).getData().equals("int")) {
            Node terminal = new Node(iExpr.getChild(0).getChild(0).getData(), valueNode);
            valueNode.addChild(terminal);
        } else if (iExpr.getChild(0).getChild(0).getData().equals("id")) {
            Node terminal = new Node(iExpr.getChild(0).getChild(0).getChild(0).getData(), valueNode);
            valueNode.addChild(terminal);
        }
        if (iExpr.getChildren().size() > 1){
            Node op;
            if (iExpr.getChild(1).getChild(0).getData().equals("rel_op")){
                op = new Node(iExpr.getChild(1).getChild(0).getChild(0).getData(), valueNode);
            }else{
                op = new Node(iExpr.getChild(1).getChild(0).getData(), valueNode);
            }
            valueNode.addChild(op);
            getIValues(valueNode, iExpr.getChild(2));
        }
    }

    private void getDValues (Node valueNode, Node dExpr){
        if (dExpr.getChild(0).getData().equals("dbl")){
            Node terminal = new Node(dExpr.getChild(0).getChild(0).getData(), valueNode);
            valueNode.addChild(terminal);
        } else if (dExpr.getChild(0).getChild(0).getData().equals("id")){
            Node terminal = new Node(dExpr.getChild(0).getChild(0).getChild(0).getData(), valueNode);
            valueNode.addChild(terminal);
        }
        if (dExpr.getChildren().size() > 1){
            Node op;
            if (dExpr.getChild(1).getChild(0).getData().equals("rel_op")){
                op = new Node(dExpr.getChild(1).getChild(0).getChild(0).getData(), valueNode);
            } else {
                op = new Node(dExpr.getChild(1).getChild(0).getData(), valueNode);
            }
            valueNode.addChild(op);
            getDValues(valueNode, dExpr.getChild(2));
        }
    }

    private void getSValues (Node valueNode, Node sExpr){
        if (sExpr.getChild(0).getData().equals("id")){
            Node terminal = new Node(sExpr.getChild(0).getChild(0).getData(), valueNode);
            valueNode.addChild(terminal);
        }
        else if (sExpr.getChild(0).getData().equals("str_literal")){
            Node terminal = new Node(sExpr.getChild(0).getChild(0).getData(), valueNode);
            valueNode.addChild(terminal);
        }
        else if (((Token)sExpr.getChild(0).getData()).getTokenName().equals("concat")){
            Node concatNode = new Node(sExpr.getChild(0).getData(), valueNode);
            valueNode.addChild(concatNode);
            Node value1 = new Node("value", concatNode);
            concatNode.addChild(value1);
            Node value2 = new Node("value", concatNode);
            concatNode.addChild(value2);
            getSValues(value1, sExpr.getChild(2));
            getSValues(value2, sExpr.getChild(4));
        }
        else if (((Token)sExpr.getChild(0).getData()).getTokenName().equals("charAt")){
            Node charAtNode = new Node(sExpr.getChild(0).getData(), valueNode);
            valueNode.addChild(charAtNode);
            Node value1 = new Node("value", charAtNode);
            charAtNode.addChild(value1);
            Node value2 = new Node("value", charAtNode);
            charAtNode.addChild(value2);
            getSValues(value1, sExpr.getChild(2));
            getIValues(value2, sExpr.getChild(4));
        }
    }

    public void decorateParseTree(Node parseTreeParent, Node decorateParseTreeRoot){
        if (parseTreeParent.getData().equals("asmt")){
            Token idToken = (Token)parseTreeParent.getChild(1).getChild(0).getData();
            String idType = ((Token)parseTreeParent.getChild(0).getData()).getTokenName();
            Node decl;
            if (idType.equals("Integer")){
                decl = new Node("i_decl", decorateParseTreeRoot);
                decorateParseTreeRoot.addChild(decl);
            }
            else if (idType.equals("Double")){
                decl = new Node("d_decl", decorateParseTreeRoot);
                decorateParseTreeRoot.addChild(decl);
            }
            else{
                decl = new Node("s_decl", decorateParseTreeRoot);
                decorateParseTreeRoot.addChild(decl);
            }
            Node idNode = new Node(idToken, decl);
            decl.addChild(idNode);
            Node value = new Node("value", decl);
            decl.addChild(value);
            if (idType.equals("Integer")){
                getIValues(value, parseTreeParent.getChild(3));
            }
            else if (idType.equals("Double")){
                getDValues(value, parseTreeParent.getChild(3));
            }
            else {
                getSValues(value, parseTreeParent.getChild(3));
            }
        }
        else if (parseTreeParent.getData().equals("r_asmt")){
            Token idToken = (Token)parseTreeParent.getChild(0).getChild(0).getData();
            String idType = JottParser.symbolTable.get(idToken.getTokenName());
            Node asgn;
            if (idType.equals("Integer")){
                asgn = new Node("i_asgn", decorateParseTreeRoot);
                decorateParseTreeRoot.addChild(asgn);
            }
            else if (idType.equals("Double")){
                asgn = new Node("d_asgn", decorateParseTreeRoot);
                decorateParseTreeRoot.addChild(asgn);
            }
            else{
                asgn = new Node("s_asgn", decorateParseTreeRoot);
                decorateParseTreeRoot.addChild(asgn);
            }
            Node idNode = new Node(idToken, asgn);
            asgn.addChild(idNode);
            Node value = new Node("value", asgn);
            asgn.addChild(value);
            if (idType.equals("Integer")){
                getIValues(value, parseTreeParent.getChild(2).getChild(0));
            }
            else if (idType.equals("Double")){
                getDValues(value, parseTreeParent.getChild(2).getChild(0));
            }
            else {
                getSValues(value, parseTreeParent.getChild(2).getChild(0));
            }
        }
        else if (parseTreeParent.getData().equals("print_stmt")){
            Node printExpres = new Node("print_expres", decorateParseTreeRoot);
            decorateParseTreeRoot.addChild(printExpres);
            String exprsType = (String)parseTreeParent.getChild(2).getChild(0).getData();
            if (exprsType.equals("i_expr")){
                Node iExpres = new Node("i_expres", printExpres);
                printExpres.addChild(iExpres);
                Node value = new Node("value", iExpres);
                iExpres.addChild(value);
                getIValues(value, parseTreeParent.getChild(2).getChild(0));
            }
            else if (exprsType.equals("d_expr")){
                Node dExpres = new Node("d_expres", printExpres);
                printExpres.addChild(dExpres);
                Node value = new Node("value", dExpres);
                dExpres.addChild(value);
                getDValues(value, parseTreeParent.getChild(2).getChild(0));
            }
            else {
                Node sExpres = new Node("s_expres", printExpres);
                printExpres.addChild(sExpres);
                Node value = new Node("value", sExpres);
                sExpres.addChild(value);
                getSValues(value, parseTreeParent.getChild(2).getChild(0));
            }
        }
        else if (parseTreeParent.getData().equals("expr") && parseTreeParent.getParent().getData().equals("stmt")){
            Node exprs = new Node("exprs", decorateParseTreeRoot);
            decorateParseTreeRoot.addChild(exprs);
            String exprsType = (String)parseTreeParent.getChild(0).getData();
            if (exprsType.equals("i_expr")){
                Node iExpres = new Node("i_expres", exprs);
                exprs.addChild(iExpres);
                Node value = new Node("value", iExpres);
                iExpres.addChild(value);
                getIValues(value, parseTreeParent.getChild(0));
            }
            else if (exprsType.equals("d_expr")){
                Node dExpres = new Node("d_expres", exprs);
                exprs.addChild(dExpres);
                Node value = new Node("value", dExpres);
                dExpres.addChild(value);
                getDValues(value, parseTreeParent.getChild(0));
            }
            else {
                Node sExpres = new Node("s_expres", exprs);
                exprs.addChild(sExpres);
                Node value = new Node("value", sExpres);
                sExpres.addChild(value);
                getSValues(value, parseTreeParent.getChild(0));
            }
        }

        List<Node> children = parseTreeParent.getChildren();
        for (Node child: children){
            decorateParseTree(child, decorateParseTreeRoot);
        }
    }
}
