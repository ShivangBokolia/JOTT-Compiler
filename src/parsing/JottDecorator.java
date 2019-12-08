package parsing;

import scanning.Token;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JottDecorator {

    private void getIValues (Node valueNode, Node iExpr){

        if (iExpr.getChild(0).getData().equals("int")) {
            if (iExpr.getChild(0).getChildren().size() != 0 && iExpr.getChild(0).getChild(0).getData().equals("f_call")){
                Node fcall = iExpr.getChild(0).getChild(0);
                Node expr = null;
                Node funcCall = new Node("func_call", valueNode);
                valueNode.addChild(funcCall);
                Node funcName = new Node(((Token)fcall.getChild(0).getChild(0).getData()).getTokenName(), funcCall);
                funcCall.addChild(funcName);
                int paramNum = JottParser.funcList.get(((Token)fcall.getChild(0).getChild(0).getData()).getTokenName()).getParamNum();
                Node paramExpr = new Node("param_expr", funcCall);
                funcCall.addChild(paramExpr);
                if (paramNum > 0){
                    expr = fcall.getChild(2).getChild(0);
                }
                for (int i=0; i< paramNum; i++){
                    Node exprs = new Node("exprs", paramExpr);
                    paramExpr.addChild(exprs);
                    String exprsType = (String) expr.getChild(0).getData();
                    if (exprsType.equals("i_expr")) {
                        Node iExpres = new Node("i_expres", exprs);
                        exprs.addChild(iExpres);
                        Node value = new Node("value", iExpres);
                        iExpres.addChild(value);
                        getIValues(value, expr.getChild(0));
                    } else if (exprsType.equals("d_expr")) {
                        Node dExpres = new Node("d_expres", exprs);
                        exprs.addChild(dExpres);
                        Node value = new Node("value", dExpres);
                        dExpres.addChild(value);
                        getDValues(value, expr.getChild(0));
                    } else {
                        Node sExpres = new Node("s_expres", exprs);
                        exprs.addChild(sExpres);
                        Node value = new Node("value", sExpres);
                        sExpres.addChild(value);
                        getSValues(value, expr.getChild(0));
                    }
                    if (i != paramNum-1){
                        expr = expr.getParent().getChild(2).getChild(0);
                    }
                }


            } else {
                Node terminal = new Node(iExpr.getChild(0).getChild(0).getData(), valueNode);
                valueNode.addChild(terminal);
            }
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
            if (dExpr.getChild(0).getChildren().size() != 0 && dExpr.getChild(0).getChild(0).getData().equals("f_call")){
                Node fcall = dExpr.getChild(0).getChild(0);
                Node expr = null;
                Node funcCall = new Node("func_call", valueNode);
                valueNode.addChild(funcCall);
                Node funcName = new Node(((Token)fcall.getChild(0).getChild(0).getData()).getTokenName(), funcCall);
                funcCall.addChild(funcName);
                int paramNum = JottParser.funcList.get(((Token)fcall.getChild(0).getChild(0).getData()).getTokenName()).getParamNum();
                Node paramExpr = new Node("param_expr", funcCall);
                funcCall.addChild(paramExpr);
                if (paramNum > 0){
                    expr = fcall.getChild(2).getChild(0);
                }
                for (int i=0; i< paramNum; i++){
                    Node exprs = new Node("exprs", paramExpr);
                    paramExpr.addChild(exprs);
                    String exprsType = (String) expr.getChild(0).getData();
                    if (exprsType.equals("i_expr")) {
                        Node iExpres = new Node("i_expres", exprs);
                        exprs.addChild(iExpres);
                        Node value = new Node("value", iExpres);
                        iExpres.addChild(value);
                        getIValues(value, expr.getChild(0));
                    } else if (exprsType.equals("d_expr")) {
                        Node dExpres = new Node("d_expres", exprs);
                        exprs.addChild(dExpres);
                        Node value = new Node("value", dExpres);
                        dExpres.addChild(value);
                        getDValues(value, expr.getChild(0));
                    } else {
                        Node sExpres = new Node("s_expres", exprs);
                        exprs.addChild(sExpres);
                        Node value = new Node("value", sExpres);
                        sExpres.addChild(value);
                        getSValues(value, expr.getChild(0));
                    }
                    if (i != paramNum-1){
                        expr = expr.getParent().getChild(2).getChild(0);
                    }
                }
            } else {
                Node terminal = new Node(dExpr.getChild(0).getChild(0).getData(), valueNode);
                valueNode.addChild(terminal);
            }
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
            if (sExpr.getChild(0).getChildren().size() != 0 && sExpr.getChild(0).getChild(0).getData().equals("f_call")){
                Node fcall = sExpr.getChild(0).getChild(0);
                Node expr = null;
                Node funcCall = new Node("func_call", valueNode);
                valueNode.addChild(funcCall);
                Node funcName = new Node(((Token)fcall.getChild(0).getChild(0).getData()).getTokenName(), funcCall);
                funcCall.addChild(funcName);
                int paramNum = JottParser.funcList.get(((Token)fcall.getChild(0).getChild(0).getData()).getTokenName()).getParamNum();
                Node paramExpr = new Node("param_expr", funcCall);
                funcCall.addChild(paramExpr);
                if (paramNum > 0){
                    expr = fcall.getChild(2).getChild(0);
                }
                for (int i=0; i< paramNum; i++){
                    Node exprs = new Node("exprs", paramExpr);
                    paramExpr.addChild(exprs);
                    String exprsType = (String) expr.getChild(0).getData();
                    if (exprsType.equals("i_expr")) {
                        Node iExpres = new Node("i_expres", exprs);
                        exprs.addChild(iExpres);
                        Node value = new Node("value", iExpres);
                        iExpres.addChild(value);
                        getIValues(value, expr.getChild(0));
                    } else if (exprsType.equals("d_expr")) {
                        Node dExpres = new Node("d_expres", exprs);
                        exprs.addChild(dExpres);
                        Node value = new Node("value", dExpres);
                        dExpres.addChild(value);
                        getDValues(value, expr.getChild(0));
                    } else {
                        Node sExpres = new Node("s_expres", exprs);
                        exprs.addChild(sExpres);
                        Node value = new Node("value", sExpres);
                        sExpres.addChild(value);
                        getSValues(value, expr.getChild(0));
                    }
                    if (i != paramNum-1){
                        expr = expr.getParent().getChild(2).getChild(0);
                    }
                }
            }
            else {
                Node terminal = new Node(sExpr.getChild(0).getChild(0).getData(), valueNode);
                valueNode.addChild(terminal);
            }
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

//    public void decorateParseTree(Node parseTreeParent, Node decorateParseTreeRoot){
//        if (parseTreeParent.getData().equals("asmt")){
//            Token idToken = (Token)parseTreeParent.getChild(1).getChild(0).getData();
//            String idType = ((Token)parseTreeParent.getChild(0).getData()).getTokenName();
//            Node decl;
//            if (idType.equals("Integer")){
//                decl = new Node("i_decl", decorateParseTreeRoot);
//                decorateParseTreeRoot.addChild(decl);
//            }
//            else if (idType.equals("Double")){
//                decl = new Node("d_decl", decorateParseTreeRoot);
//                decorateParseTreeRoot.addChild(decl);
//            }
//            else{
//                decl = new Node("s_decl", decorateParseTreeRoot);
//                decorateParseTreeRoot.addChild(decl);
//            }
//            Node idNode = new Node(idToken, decl);
//            decl.addChild(idNode);
//            Node value = new Node("value", decl);
//            decl.addChild(value);
//            if (idType.equals("Integer")){
//                getIValues(value, parseTreeParent.getChild(3));
//            }
//            else if (idType.equals("Double")){
//                getDValues(value, parseTreeParent.getChild(3));
//            }
//            else {
//                getSValues(value, parseTreeParent.getChild(3));
//            }
//        }
//        else if (parseTreeParent.getData().equals("r_asmt")){
//            Token idToken = (Token)parseTreeParent.getChild(0).getChild(0).getData();
//            String idType = JottParser.symbolTable.get(idToken.getTokenName());
//            Node asgn;
//            if (idType.equals("Integer")){
//                asgn = new Node("i_asgn", decorateParseTreeRoot);
//                decorateParseTreeRoot.addChild(asgn);
//            }
//            else if (idType.equals("Double")){
//                asgn = new Node("d_asgn", decorateParseTreeRoot);
//                decorateParseTreeRoot.addChild(asgn);
//            }
//            else{
//                asgn = new Node("s_asgn", decorateParseTreeRoot);
//                decorateParseTreeRoot.addChild(asgn);
//            }
//            Node idNode = new Node(idToken, asgn);
//            asgn.addChild(idNode);
//            Node value = new Node("value", asgn);
//            asgn.addChild(value);
//            if (idType.equals("Integer")){
//                getIValues(value, parseTreeParent.getChild(2).getChild(0));
//            }
//            else if (idType.equals("Double")){
//                getDValues(value, parseTreeParent.getChild(2).getChild(0));
//            }
//            else {
//                getSValues(value, parseTreeParent.getChild(2).getChild(0));
//            }
//        }
//        else if (parseTreeParent.getData().equals("print_stmt")){
//            Node printExpres = new Node("print_expres", decorateParseTreeRoot);
//            decorateParseTreeRoot.addChild(printExpres);
//            String exprsType = (String)parseTreeParent.getChild(2).getChild(0).getData();
//            if (exprsType.equals("i_expr")){
//                Node iExpres = new Node("i_expres", printExpres);
//                printExpres.addChild(iExpres);
//                Node value = new Node("value", iExpres);
//                iExpres.addChild(value);
//                getIValues(value, parseTreeParent.getChild(2).getChild(0));
//            }
//            else if (exprsType.equals("d_expr")){
//                Node dExpres = new Node("d_expres", printExpres);
//                printExpres.addChild(dExpres);
//                Node value = new Node("value", dExpres);
//                dExpres.addChild(value);
//                getDValues(value, parseTreeParent.getChild(2).getChild(0));
//            }
//            else {
//                Node sExpres = new Node("s_expres", printExpres);
//                printExpres.addChild(sExpres);
//                Node value = new Node("value", sExpres);
//                sExpres.addChild(value);
//                getSValues(value, parseTreeParent.getChild(2).getChild(0));
//            }
//        }
//        else if (parseTreeParent.getData().equals("expr") && parseTreeParent.getParent().getData().equals("stmt")){
//            Node exprs = new Node("exprs", decorateParseTreeRoot);
//            decorateParseTreeRoot.addChild(exprs);
//            String exprsType = (String)parseTreeParent.getChild(0).getData();
//            if (exprsType.equals("i_expr")){
//                Node iExpres = new Node("i_expres", exprs);
//                exprs.addChild(iExpres);
//                Node value = new Node("value", iExpres);
//                iExpres.addChild(value);
//                getIValues(value, parseTreeParent.getChild(0));
//            }
//            else if (exprsType.equals("d_expr")){
//                Node dExpres = new Node("d_expres", exprs);
//                exprs.addChild(dExpres);
//                Node value = new Node("value", dExpres);
//                dExpres.addChild(value);
//                getDValues(value, parseTreeParent.getChild(0));
//            }
//            else {
//                Node sExpres = new Node("s_expres", exprs);
//                exprs.addChild(sExpres);
//                Node value = new Node("value", sExpres);
//                sExpres.addChild(value);
//                getSValues(value, parseTreeParent.getChild(0));
//            }
//        }
//
//        List<Node> children = parseTreeParent.getChildren();
//        for (Node child: children){
//            decorateParseTree(child, decorateParseTreeRoot);
//        }
//    }
//}

    public void decorateParseTree(Node pTStmtList, Node decorateParseTreeRoot, FunctionClass fclass){
        if (fclass != null && (pTStmtList.getChild(0).getData() instanceof Token) && ((Token)pTStmtList.getChild(0).getData()).getTokenName().equals("return")){
            Node parseTreeParent = pTStmtList.getChild(1);
            Node ret = new Node("return", decorateParseTreeRoot);
            decorateParseTreeRoot.addChild(ret);
            Node exprs = new Node("exprs", ret);
            ret.addChild(exprs);
            String exprsType = (String) parseTreeParent.getChild(0).getData();
            if (exprsType.equals("i_expr")) {
                Node iExpres = new Node("i_expres", exprs);
                exprs.addChild(iExpres);
                Node value = new Node("value", iExpres);
                iExpres.addChild(value);
                getIValues(value, parseTreeParent.getChild(0));
            } else if (exprsType.equals("d_expr")) {
                Node dExpres = new Node("d_expres", exprs);
                exprs.addChild(dExpres);
                Node value = new Node("value", dExpres);
                dExpres.addChild(value);
                getDValues(value, parseTreeParent.getChild(0));
            } else {
                Node sExpres = new Node("s_expres", exprs);
                exprs.addChild(sExpres);
                Node value = new Node("value", sExpres);
                sExpres.addChild(value);
                getSValues(value, parseTreeParent.getChild(0));
            }
        }
        else if(!pTStmtList.getChild(0).getData().equals("")) {
            Node parseTreeParent = pTStmtList.getChild(0).getChild(0);
            if (parseTreeParent.getData().equals("asmt")) {
                Token idToken = (Token) parseTreeParent.getChild(1).getChild(0).getData();
                String idType = ((Token) parseTreeParent.getChild(0).getData()).getTokenName();
                Node decl;
                if (idType.equals("Integer")) {
                    decl = new Node("i_decl", decorateParseTreeRoot);
                    decorateParseTreeRoot.addChild(decl);
                } else if (idType.equals("Double")) {
                    decl = new Node("d_decl", decorateParseTreeRoot);
                    decorateParseTreeRoot.addChild(decl);
                } else {
                    decl = new Node("s_decl", decorateParseTreeRoot);
                    decorateParseTreeRoot.addChild(decl);
                }
                Node idNode = new Node(idToken, decl);
                decl.addChild(idNode);
                Node value = new Node("value", decl);
                decl.addChild(value);
                if (idType.equals("Integer")) {
                    getIValues(value, parseTreeParent.getChild(3));
                } else if (idType.equals("Double")) {
                    getDValues(value, parseTreeParent.getChild(3));
                } else {
                    getSValues(value, parseTreeParent.getChild(3));
                }
            } else if (parseTreeParent.getData().equals("r_asmt")) {
                Map<String, String> symbolTable;
                Token idToken = (Token) parseTreeParent.getChild(0).getChild(0).getData();
                if (fclass != null && fclass.getSymbolTable().containsKey(idToken.getTokenName())){
                    symbolTable = fclass.getSymbolTable();
                } else {
                    symbolTable = JottParser.symbolTable;
                }
                String idType = symbolTable.get(idToken.getTokenName());
                Node asgn;
                if (idType.equals("Integer")) {
                    asgn = new Node("i_asgn", decorateParseTreeRoot);
                    decorateParseTreeRoot.addChild(asgn);
                } else if (idType.equals("Double")) {
                    asgn = new Node("d_asgn", decorateParseTreeRoot);
                    decorateParseTreeRoot.addChild(asgn);
                } else {
                    asgn = new Node("s_asgn", decorateParseTreeRoot);
                    decorateParseTreeRoot.addChild(asgn);
                }
                Node idNode = new Node(idToken, asgn);
                asgn.addChild(idNode);
                Node value = new Node("value", asgn);
                asgn.addChild(value);
                if (idType.equals("Integer")) {
                    getIValues(value, parseTreeParent.getChild(2).getChild(0));
                } else if (idType.equals("Double")) {
                    getDValues(value, parseTreeParent.getChild(2).getChild(0));
                } else {
                    getSValues(value, parseTreeParent.getChild(2).getChild(0));
                }
            } else if (parseTreeParent.getData().equals("print_stmt")) {
                Node printExpres = new Node("print_expres", decorateParseTreeRoot);
                decorateParseTreeRoot.addChild(printExpres);
                String exprsType = (String) parseTreeParent.getChild(2).getChild(0).getData();
                if (exprsType.equals("i_expr")) {
                    Node iExpres = new Node("i_expres", printExpres);
                    printExpres.addChild(iExpres);
                    Node value = new Node("value", iExpres);
                    iExpres.addChild(value);
                    getIValues(value, parseTreeParent.getChild(2).getChild(0));
                } else if (exprsType.equals("d_expr")) {
                    Node dExpres = new Node("d_expres", printExpres);
                    printExpres.addChild(dExpres);
                    Node value = new Node("value", dExpres);
                    dExpres.addChild(value);
                    getDValues(value, parseTreeParent.getChild(2).getChild(0));
                } else {
                    Node sExpres = new Node("s_expres", printExpres);
                    printExpres.addChild(sExpres);
                    Node value = new Node("value", sExpres);
                    sExpres.addChild(value);
                    getSValues(value, parseTreeParent.getChild(2).getChild(0));
                }
            } else if (parseTreeParent.getData().equals("f_call")){
                Node expr = null;
                Node funcCall = new Node("func_call", decorateParseTreeRoot);
                decorateParseTreeRoot.addChild(funcCall);
                Node funcName = new Node(((Token)parseTreeParent.getChild(0).getChild(0).getData()).getTokenName(), funcCall);
                funcCall.addChild(funcName);
                int paramNum = JottParser.funcList.get(((Token)parseTreeParent.getChild(0).getChild(0).getData()).getTokenName()).getParamNum();
                Node paramExpr = new Node("param_expr", funcCall);
                funcCall.addChild(paramExpr);
                if (paramNum > 0){
                    expr = parseTreeParent.getChild(2).getChild(0);
                }
                for (int i=0; i< paramNum; i++){
                    Node exprs = new Node("exprs", paramExpr);
                    paramExpr.addChild(exprs);
                    String exprsType = (String) expr.getChild(0).getData();
                    if (exprsType.equals("i_expr")) {
                        Node iExpres = new Node("i_expres", exprs);
                        exprs.addChild(iExpres);
                        Node value = new Node("value", iExpres);
                        iExpres.addChild(value);
                        getIValues(value, expr.getChild(0));
                    } else if (exprsType.equals("d_expr")) {
                        Node dExpres = new Node("d_expres", exprs);
                        exprs.addChild(dExpres);
                        Node value = new Node("value", dExpres);
                        dExpres.addChild(value);
                        getDValues(value, expr.getChild(0));
                    } else {
                        Node sExpres = new Node("s_expres", exprs);
                        exprs.addChild(sExpres);
                        Node value = new Node("value", sExpres);
                        sExpres.addChild(value);
                        getSValues(value, expr.getChild(0));
                    }
                    if (i != paramNum-1){
                        expr = expr.getParent().getChild(2).getChild(0);
                    }
                }
            }
            else if (parseTreeParent.getData().equals("expr") && parseTreeParent.getParent().getData().equals("stmt")) {
                Node exprs = new Node("exprs", decorateParseTreeRoot);
                decorateParseTreeRoot.addChild(exprs);
                String exprsType = (String) parseTreeParent.getChild(0).getData();
                if (exprsType.equals("i_expr")) {
                    Node iExpres = new Node("i_expres", exprs);
                    exprs.addChild(iExpres);
                    Node value = new Node("value", iExpres);
                    iExpres.addChild(value);
                    getIValues(value, parseTreeParent.getChild(0));
                } else if (exprsType.equals("d_expr")) {
                    Node dExpres = new Node("d_expres", exprs);
                    exprs.addChild(dExpres);
                    Node value = new Node("value", dExpres);
                    dExpres.addChild(value);
                    getDValues(value, parseTreeParent.getChild(0));
                } else {
                    Node sExpres = new Node("s_expres", exprs);
                    exprs.addChild(sExpres);
                    Node value = new Node("value", sExpres);
                    sExpres.addChild(value);
                    getSValues(value, parseTreeParent.getChild(0));
                }
            }
            //if statement

            else if ((parseTreeParent.getData() instanceof Token) && (((Token) parseTreeParent.getData()).getTokenName().equals("if")) && (parseTreeParent.getParent().getChildren().size() <= 7)){
                Node if_decl = new Node("if_decl", decorateParseTreeRoot);
                decorateParseTreeRoot.addChild(if_decl);
                Node exprs = new Node("exprs", if_decl);
                if_decl.addChild(exprs);
                String exprsType = (String) pTStmtList.getChild(0).getChild(2).getChild(0).getData();
                if (exprsType.equals("i_expr")) {
                    Node iExpres = new Node("i_expres", exprs);
                    exprs.addChild(iExpres);
                    Node value = new Node("value", iExpres);
                    iExpres.addChild(value);
                    getIValues(value, pTStmtList.getChild(0).getChild(2).getChild(0));
                } else if (exprsType.equals("d_expr")) {
                    Node dExpres = new Node("d_expres", exprs);
                    exprs.addChild(dExpres);
                    Node value = new Node("value", dExpres);
                    dExpres.addChild(value);
                    getDValues(value, pTStmtList.getChild(0).getChild(2).getChild(0));
                } else {
                    Node sExpres = new Node("s_expres", exprs);
                    exprs.addChild(sExpres);
                    Node value = new Node("value", sExpres);
                    sExpres.addChild(value);
                    getSValues(value, pTStmtList.getChild(0).getChild(2).getChild(0));
                }
                Node if_body = new Node("if_body", if_decl);
                if_decl.addChild(if_body);
                decorateParseTree(pTStmtList.getChild(0).getChild(5), if_body, fclass);
            }

            //if and else

            else if ((parseTreeParent.getData() instanceof Token) && (((Token) parseTreeParent.getData()).getTokenName().equals("if")) && (parseTreeParent.getParent().getChildren().size() > 7)){
                Node if_else = new Node("if_else", decorateParseTreeRoot);
                decorateParseTreeRoot.addChild(if_else);
                Node if_decl = new Node("if_decl", if_else);
                if_else.addChild(if_decl);
                Node exprs = new Node("exprs", if_decl);
                if_decl.addChild(exprs);
                String exprsType = (String) pTStmtList.getChild(0).getChild(2).getChild(0).getData();
                if (exprsType.equals("i_expr")) {
                    Node iExpres = new Node("i_expres", exprs);
                    exprs.addChild(iExpres);
                    Node value = new Node("value", iExpres);
                    iExpres.addChild(value);
                    getIValues(value, pTStmtList.getChild(0).getChild(2).getChild(0));
                } else if (exprsType.equals("d_expr")) {
                    Node dExpres = new Node("d_expres", exprs);
                    exprs.addChild(dExpres);
                    Node value = new Node("value", dExpres);
                    dExpres.addChild(value);
                    getDValues(value, pTStmtList.getChild(0).getChild(2).getChild(0));
                } else {
                    Node sExpres = new Node("s_expres", exprs);
                    exprs.addChild(sExpres);
                    Node value = new Node("value", sExpres);
                    sExpres.addChild(value);
                    getSValues(value, pTStmtList.getChild(0).getChild(2).getChild(0));
                }
                Node if_body = new Node("if_body", if_decl);
                if_decl.addChild(if_body);
                decorateParseTree(pTStmtList.getChild(0).getChild(5), if_body, fclass);

                //Else:

                Node else_decl = new Node("else_decl", if_else);
                if_else.addChild(else_decl);
                Node else_body = new Node("else_body", else_decl);
                else_decl.addChild(else_body);
                decorateParseTree(pTStmtList.getChild(0).getChild(9), else_body, fclass);
            }

            // While Loop

            else if ((parseTreeParent.getData() instanceof Token) && (((Token) parseTreeParent.getData()).getTokenName().equals("while"))){
                Node while_decl = new Node("while_decl", decorateParseTreeRoot);
                decorateParseTreeRoot.addChild(while_decl);
                Node exprs = new Node("exprs", while_decl);
                while_decl.addChild(exprs);
                Node iExpres = new Node("i_expres", exprs);
                exprs.addChild(iExpres);
                Node value = new Node("value", iExpres);
                iExpres.addChild(value);
                getIValues(value, pTStmtList.getChild(0).getChild(2));
                Node while_body = new Node("while_body", while_decl);
                while_decl.addChild(while_body);
                decorateParseTree(pTStmtList.getChild(0).getChild(5), while_body, fclass);
            }

            //For Loop

            else if ((parseTreeParent.getData() instanceof Token) && (((Token) parseTreeParent.getData()).getTokenName().equals("for"))){
                Node for_decl = new Node("for_decl", decorateParseTreeRoot);
                decorateParseTreeRoot.addChild(for_decl);
                //i declaration
                Node for_asgn = new Node("for_asgn", for_decl);
                for_decl.addChild(for_asgn);
                Token idToken = (Token) pTStmtList.getChild(0).getChild(2).getChild(1).getChild(0).getData();
                String idType = ((Token) pTStmtList.getChild(0).getChild(2).getChild(0).getData()).getTokenName();
                Node decl = new Node("i_decl", for_asgn);
                for_asgn.addChild(decl);
                Node idNode = new Node(idToken, decl);
                decl.addChild(idNode);
                Node value = new Node("value", decl);
                decl.addChild(value);
                getIValues(value, parseTreeParent.getParent().getChild(2).getChild(3));


                //loop condition
                Node exprs = new Node("exprs", for_decl);
                for_decl.addChild(exprs);
                Node iExpres = new Node("i_expres", exprs);
                exprs.addChild(iExpres);
                Node for_value = new Node("for_value", iExpres);
                iExpres.addChild(for_value);
                getIValues(for_value, pTStmtList.getChild(0).getChild(3));

                //Iteration
                Token for_idToken = (Token) parseTreeParent.getParent().getChild(5).getChild(0).getChild(0).getData();
                Node asgn;
                asgn = new Node("for_i_asgn", for_decl);
                for_decl.addChild(asgn);
                Node for_idNode = new Node(for_idToken, asgn);
                asgn.addChild(for_idNode);
                Node for_value_2 = new Node("for_value_2", asgn);
                asgn.addChild(for_value_2);
                getIValues(for_value_2, parseTreeParent.getParent().getChild(5).getChild(2).getChild(0));

                //Body
                Node for_body = new Node("for_body", for_decl);
                for_decl.addChild(for_body);
                decorateParseTree(pTStmtList.getChild(0).getChild(8), for_body, fclass);
            }
            else if (parseTreeParent.getData() instanceof FunctionClass){
                FunctionClass func = (FunctionClass)parseTreeParent.getData();
                decorateParseTree(func.getBody(), func.getDecoratedBody(), func);
            }


            decorateParseTree(pTStmtList.getChild(1), decorateParseTreeRoot, fclass);
        }
    }
}
