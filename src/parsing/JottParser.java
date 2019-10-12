package parsing;

import scanning.Token;

import java.lang.reflect.Type;
import java.util.*;

public class JottParser {

    public static Map<String, String> symbolTable = new HashMap<>();

    private ArrayList<String> ops = new ArrayList<>(Arrays.asList("+", "-", "*", "/"));
    private ArrayList<String> lowerCase = new ArrayList<>(Arrays.asList("a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z"));
    private ArrayList<String> digits = new ArrayList<>(Arrays.asList("1", "2", "3", "4", "5", "6", "7", "8", "9", "0"));
    private ArrayList<String> signs = new ArrayList<>(Arrays.asList("+", "-", ""));

    public Node parseTake2(List<Token> tokenList) {
        Node root = new Node("program", null);
        Node firstSmt = new Node("stmt_list", root);
        root.addChild(firstSmt);
        int lineCount = 1;
        Node branchStart = firstSmt;

        while (!tokenList.isEmpty()) {
            List<Token> oneLine = new ArrayList<>();
//            while (!(tokenList.isEmpty()) && tokenList.get(0).getLineNo() == lineCount) {
//                oneLine.add(tokenList.remove(0));
//            }
            //Using ; as a parameter to move to the next statement
            while (!(tokenList.isEmpty()) && !(tokenList.get(0).getTokenName().equals(";"))){
                oneLine.add(tokenList.remove(0));
            }
            oneLine.add(tokenList.remove(0));       //; check
            List<String[]> treeBranch = checkedGrammar("stmt", oneLine);
            Node statement = new Node("stmt", branchStart);
            branchStart.addChild(statement);
            Node next = statement;

            for (int j = treeBranch.size() - 1; j >= 0; j--) {
                for (String grammar : treeBranch.get(j)) {
                    Node newChild = new Node(grammar, next);
                    next.addChild(newChild);
                    expandNode(newChild, oneLine);
                }
                next = next.getChild(0);
            }
            lineCount++;
            Node newStart = new Node("stmt_list", branchStart);
            branchStart.addChild(newStart);
            branchStart = newStart;
        }

        branchStart.addChild(new Node("", branchStart));

        root.addChild(new Node("$$", root));
        return root;
    }

    private List<String[]> checkedGrammar(String currGrammar, List<Token> oneLine){
        String[][] grammars = JottGrammar.grammar.get(currGrammar);
        for (String[] grammarOpt: grammars){
            if(!(JottGrammar.grammar.containsKey(grammarOpt[0]))){
                if(grammarOpt[0].equals(oneLine.get(0).getTokenName())){
                    List<String[]> leaf = new ArrayList<>();
                    leaf.add(grammarOpt);
                    return leaf;
                }
                else{
                    continue;
                }
            }
            else if (grammarOpt[0].equals("expr")){
                List<String[]> leaf = new ArrayList<>();
                leaf.add(grammarOpt);
                return leaf;
            }
            else{
                List<String[]> check = checkedGrammar(grammarOpt[0], oneLine);
                if (check != null){
                    check.add(grammarOpt);
                    return check;
                }
            }
        }
        return null;
    }

    private void expandNode (Node newChild, List<Token> oneLine) {
        if (!(JottGrammar.grammar.containsKey(newChild.getData()))){
            if (newChild.getData().equals(oneLine.get(0).getTokenName())){
                newChild.setData(oneLine.get(0));
                oneLine.remove(0);
            }
        }
        else{
            //check if the ID already exists??????????
            if(newChild.getData().equals("id")){
                Token idToken = oneLine.remove(0);
                String idString = idToken.getTokenName();
                List<String> chars = new ArrayList<>();
                for (String[] l: JottGrammar.grammar.get("l_char")){
                    chars.add(l[0]);
                }
                boolean isFirstLower = false;
                for (String c: chars){
                    if (idString.substring(0, 1).equals(c)){
                        isFirstLower = true;
                        break;
                    }
                }
                boolean isValidId = true;
                if (isFirstLower) {
                    for (String[] u: JottGrammar.grammar.get("u_char")){
                        chars.add(u[0]);
                    }
                    for (String[] d: JottGrammar.grammar.get("digit")){
                        chars.add(d[0]);
                    }
                    for (int i = 1; i < idString.length(); i++) {
                        if (!chars.contains(idString.substring(i, i + 1))) {
                            isValidId = false;
                            break;
                        }
                    }
                }
                if (isFirstLower && isValidId){
                    Node idChild = new Node(idToken, newChild);
                    newChild.addChild(idChild);
                    //symbol Table stuff
                    if (newChild.getParent().getData().equals("asmt")){
                        Token dataType = (Token)newChild.getParent().getChild(0).getData();
                        symbolTable.put(idToken.getTokenName(), dataType.getTokenName());
                    }
                }
                else{
                    System.out.println("Error 2");
                }
            }
            else if (newChild.getData().equals("expr")){
                Expr(newChild, oneLine);
            }
            else if (newChild.getData().equals("i_expr")){
                iExprParse(newChild, oneLine, true, false);
            }
            else if (newChild.getData().equals("d_expr")){
                dExprParse(newChild, oneLine, true, false);
            }
            else if (newChild.getData().equals("s_expr")){
                sExprParse(newChild, oneLine);
            }
            else if (newChild.getData().equals("end_statement")){
                Node endChild = new Node(oneLine.remove(0), newChild);
                newChild.addChild(endChild);
            }
            else if (newChild.getData().equals("start_paren")){
                Node sParenChild = new Node(oneLine.remove(0), newChild);
                newChild.addChild(sParenChild);
            }
            else if (newChild.getData().equals("end_paren")){
                Node eParenChild = new Node(oneLine.remove(0), newChild);
                newChild.addChild(eParenChild);
            }
        }
    }

    private boolean isInteger(String str){
        for (int i=0; i<str.length(); i++){
            if (!this.digits.contains(str.substring(i, i+1))){
                return false;
            }
        }
        return true;
    }

    private boolean isDouble(String str){
        int decimalIndex = str.indexOf(".");
        if ((decimalIndex == -1)||(decimalIndex + 1) == str.length()){
            return false;
        }
        for (int i=0; i<decimalIndex - 1; i++){
            if (!this.digits.contains(str.substring(i, i+1))){
                return false;
            }
        }
        for (int i=decimalIndex + 1; i<str.length(); i++){
            if (!this.digits.contains(str.substring(i, i+1))){
                return false;
            }
        }
        return true;
    }

    private boolean isString(String str){
        List<String> chars = new ArrayList<>();
        for (String[] l: JottGrammar.grammar.get("l_char")){
            chars.add(l[0]);
        }
        for (String[] u: JottGrammar.grammar.get("u_char")){
            chars.add(u[0]);
        }
        for (String[] d: JottGrammar.grammar.get("digit")){
            chars.add(d[0]);
        }
        chars.add(" ");
        if (!str.substring(0, 1).equals("\"") && !str.substring(str.length() - 1).equals("\"")){
            return false;
        }
        for (int i=1; i< str.length() - 1; i++){
            if (!chars.contains(str.substring(i, i+1))){
                return false;
            }
        }
        return true;
    }

//    private void iExprParse (Node parent, List<Token> tokenList){
//        boolean flag = false;
//        //Checking for the positive number or var at position 0
//        if (tokenList.size() > 1 && this.ops.contains(tokenList.get(1).getTokenName())){
//            //The classic ID check
//            if (this.lowerCase.contains(tokenList.get(0).getTokenName().substring(0, 1)) && symbolTable.containsKey(tokenList.get(0))){
//                Node iExpr = new Node("i_expr", parent);
//                parent.addChild(iExpr);
//                Node id = new Node("id", iExpr);
//                iExpr.addChild(id);
//                Node number = new Node(tokenList.remove(0), id);
//                id.addChild(number);
//                iExprParse(iExpr, tokenList);
//            }
//            //The classic Integer check
//            else if (isInteger(tokenList.get(0).getTokenName())){
//                Node iExpr = new Node("i_expr", parent);
//                parent.addChild(iExpr);
//                Node newInt = new Node("int", iExpr);
//                iExpr.addChild(newInt);
//                Node number = new Node(tokenList.remove(0), newInt);
//                newInt.addChild(number);
//                iExprParse(iExpr, tokenList);
//            }
//            //The classic op check
//            else if (this.ops.contains(tokenList.get(0).getTokenName().substring(0,1))){
//                Node op = new Node("op", parent);
//                parent.addChild(op);
//                Node opValue = new Node(tokenList.remove(0), op);
//                op.addChild(opValue);
//                iExprParse(parent, tokenList);
//            }
//        }
//        //checking if the op is a op
//        else if (this.ops.contains(tokenList.get(0).getTokenName().substring(0,1))){
//            Node op = new Node("op", parent);
//            parent.addChild(op);
//            Node opValue = new Node(tokenList.remove(0), op);
//            op.addChild(opValue);
//            iExprParse(op, tokenList);
//        }
//        //checking if the op is a sign
//        else if (this.ops.contains(tokenList.get(0).getTokenName().substring(0, 1)) && isInteger(tokenList.get(1).getTokenName())){
//            Token sign = tokenList.remove(0);
//            Token integer = tokenList.remove(0);
//            Token signedInt = new Token(sign.getTokenName() + integer.getTokenName(), integer.getLineNo());
//            Node iExpr = new Node("i_expr", parent);
//            parent.addChild(iExpr);
//            Node newInt = new Node("int", iExpr);
//            iExpr.addChild(newInt);
//            Node number = new Node(signedInt, newInt);
//            newInt.addChild(number);
//            if (!tokenList.isEmpty() && !tokenList.get(0).getTokenName().substring(0, 1).equals(";")){
//                iExprParse(iExpr, tokenList);
//            }
//        }
//        //checking if the element is a var or ID
//        else if (this.lowerCase.contains(tokenList.get(0).getTokenName().substring(0, 1)) && symbolTable.containsKey(tokenList.get(0))){
//            Node iExpr = new Node("i_expr", parent);
//            parent.addChild(iExpr);
//            Node id = new Node(tokenList.remove(0), iExpr);
//            iExpr.addChild(id);
//        }
//        //checking if the element is an integer
//        else if (isInteger(tokenList.get(0).getTokenName())){
//            Node iExpr = new Node("i_expr", parent);
//            parent.addChild(iExpr);
//            Node newInt = new Node("int", iExpr);
//            iExpr.addChild(newInt);
//            Node number = new Node(tokenList.remove(0), newInt);
//            newInt.addChild(number);
//        }
//        else {
//            System.out.println("ERROR!!!!!");
//        }
//    }

    private void iExprParse (Node parent, List<Token> tokenList, boolean begin, boolean isOp){
        if (begin || isOp){
            if (signs.contains(tokenList.get(0).getTokenName())){
                Token sign = tokenList.remove(0);
                Token integer = tokenList.remove(0);
                Token signedInt = new Token(sign.getTokenName()+integer.getTokenName(), integer.getLineNo());
                Node newInt = new Node("int", parent);
                parent.addChild(newInt);
                Node number = new Node(signedInt, newInt);
                newInt.addChild(number);
                if (!tokenList.isEmpty() && !tokenList.get(0).getTokenName().substring(0, 1).equals(";") && !tokenList.get(0).getTokenName().substring(0, 1).equals(")")){
                    iExprParse(parent, tokenList, false, false);
                }
            }
            else if (isInteger(tokenList.get(0).getTokenName())){
                Node newInt = new Node("int", parent);
                parent.addChild(newInt);
                Node number = new Node(tokenList.remove(0), newInt);
                newInt.addChild(number);
                if (!tokenList.isEmpty() && !tokenList.get(0).getTokenName().substring(0, 1).equals(";") && !tokenList.get(0).getTokenName().substring(0, 1).equals(")")){
                    iExprParse(parent, tokenList, false, false);
                }
            }
            else if (this.lowerCase.contains(tokenList.get(0).getTokenName().substring(0, 1)) && symbolTable.containsKey(tokenList.get(0).getTokenName())){
                Node id = new Node("id", parent);
                parent.addChild(id);
                Node number = new Node(tokenList.remove(0), id);
                id.addChild(number);
                if (!tokenList.isEmpty() && !tokenList.get(0).getTokenName().substring(0, 1).equals(";") && !tokenList.get(0).getTokenName().substring(0, 1).equals(")")){
                    iExprParse(parent, tokenList, false, false);
                }
            }
            else{
                System.out.println("ERRRORRROROORORORORORORORO");
            }
        }
        else if (ops.contains(tokenList.get(0).getTokenName()) && !isOp){
            Node op = new Node("op", parent);
            parent.addChild(op);
            Node opNode = new Node(tokenList.remove(0), op);
            op.addChild(opNode);
            Node iExpr = new Node("i_expr", parent);
            parent.addChild(iExpr);
            iExprParse(iExpr, tokenList, false, true);
        }
        else{
            System.out.println("SOME ERROROROROR");
        }
    }

    private void dExprParse (Node parent, List<Token> tokenList, boolean begin, boolean isOp){
        if (begin || isOp){
            if (signs.contains(tokenList.get(0).getTokenName())){
                Token sign = tokenList.remove(0);
                Token dbl = tokenList.remove(0);
                Token signedDbl = new Token(sign.getTokenName()+dbl.getTokenName(), dbl.getLineNo());
                Node newDbl = new Node("dbl", parent);
                parent.addChild(newDbl);
                Node number = new Node(signedDbl, newDbl);
                newDbl.addChild(number);
                if (!tokenList.isEmpty() && !tokenList.get(0).getTokenName().substring(0, 1).equals(";") && !tokenList.get(0).getTokenName().substring(0, 1).equals(")")){
                    dExprParse(parent, tokenList, false, false);
                }
            }
            else if (isDouble(tokenList.get(0).getTokenName())){
                Node newDbl = new Node("dbl", parent);
                parent.addChild(newDbl);
                Node number = new Node(tokenList.remove(0), newDbl);
                newDbl.addChild(number);
                if (!tokenList.isEmpty() && !tokenList.get(0).getTokenName().substring(0, 1).equals(";") && !tokenList.get(0).getTokenName().substring(0, 1).equals(")")){
                    dExprParse(parent, tokenList, false, false);
                }
            }
            else if (this.lowerCase.contains(tokenList.get(0).getTokenName().substring(0, 1)) && symbolTable.containsKey(tokenList.get(0).getTokenName())){
                Node id = new Node("id", parent);
                parent.addChild(id);
                Node number = new Node(tokenList.remove(0), id);
                id.addChild(number);
                if (!tokenList.isEmpty() && !tokenList.get(0).getTokenName().substring(0, 1).equals(";") && !tokenList.get(0).getTokenName().substring(0, 1).equals(")")){
                    dExprParse(parent, tokenList, false, false);
                }
            }
            else{
                System.out.println("ERRRORRROROORORORORORORORO");
            }
        }
        else if (ops.contains(tokenList.get(0).getTokenName()) && !isOp){
            Node op = new Node("op", parent);
            parent.addChild(op);
            Node opNode = new Node(tokenList.remove(0), op);
            op.addChild(opNode);
            Node dExpr = new Node("d_expr", parent);
            parent.addChild(dExpr);
            dExprParse(dExpr, tokenList, false, true);
        }
        else{
            System.out.println("SOME ERROROROROR");
        }
    }

//    private void dExprParse (Node parent, List<Token> tokenList){
//        if (tokenList.size() > 1 && this.ops.contains(tokenList.get(1).getTokenName())){
//            if (this.lowerCase.contains(tokenList.get(0).getTokenName().substring(0, 1)) && symbolTable.containsKey(tokenList.get(0))){
//                Node dExpr = new Node("d_expr", parent);
//                parent.addChild(dExpr);
//                Node id = new Node("id", dExpr);
//                dExpr.addChild(id);
//                Node number = new Node(tokenList.remove(0), id);
//                id.addChild(number);
//                dExprParse(dExpr, tokenList);
//            }
//            else if (isDouble(tokenList.get(0).getTokenName())){
//                Node dExpr = new Node("d_expr", parent);
//                parent.addChild(dExpr);
//                Node newDouble = new Node("dbl", dExpr);
//                dExpr.addChild(newDouble);
//                Node number = new Node(tokenList.remove(0), newDouble);
//                newDouble.addChild(number);
//                dExprParse(dExpr, tokenList);
//            }
//            else if (this.ops.contains(tokenList.get(0).getTokenName().substring(0,1))){
//                Node op = new Node("op", parent);
//                parent.addChild(op);
//                Node opValue = new Node(tokenList.remove(0), op);
//                op.addChild(opValue);
//                dExprParse(parent, tokenList);
//            }
//        }
//        else if (this.ops.contains(tokenList.get(0).getTokenName().substring(0, 1)) && isDouble(tokenList.get(1).getTokenName())){
//            Token sign = tokenList.remove(0);
//            Token dbl = tokenList.remove(0);
//            Token signedDouble = new Token(sign.getTokenName() + dbl.getTokenName(), dbl.getLineNo());
//            Node dExpr = new Node("d_expr", parent);
//            parent.addChild(dExpr);
//            Node newDouble = new Node("dbl", dExpr);
//            dExpr.addChild(newDouble);
//            Node number = new Node(signedDouble, newDouble);
//            newDouble.addChild(number);
//            if (!tokenList.isEmpty() && !tokenList.get(0).getTokenName().substring(0, 1).equals(";")){
//                dExprParse(dExpr, tokenList);
//            }
//        }
//        else if (this.lowerCase.contains(tokenList.get(0).getTokenName().substring(0, 1)) && symbolTable.containsKey(tokenList.get(0))){
//            Node dExpr = new Node("d_expr", parent);
//            parent.addChild(dExpr);
//            Node id = new Node(tokenList.remove(0), dExpr);
//            dExpr.addChild(id);
//        }
//        else if (isDouble(tokenList.get(0).getTokenName())){
//            Node dExpr = new Node("d_expr", parent);
//            parent.addChild(dExpr);
//            Node newDouble = new Node("dbl", dExpr);
//            dExpr.addChild(newDouble);
//            Node number = new Node(tokenList.remove(0), newDouble);
//            newDouble.addChild(number);
//        }
//        else if (this.ops.contains(tokenList.get(0).getTokenName().substring(0,1))){
//            Node op = new Node("op", parent);
//            parent.addChild(op);
//            Node opValue = new Node(tokenList.remove(0), op);
//            op.addChild(opValue);
//        }
//        else {
//            System.out.println("ERROR!!!!!");
//        }
//    }

    private void sExprParse(Node parent, List<Token> tokenList){
        if (this.lowerCase.contains(tokenList.get(0).getTokenName().substring(0, 1)) && symbolTable.containsKey(tokenList.get(0).getTokenName())){
//            Node sExpr = new Node("s_expr", parent);
//            parent.addChild(sExpr);
//            Node id = new Node("id", sExpr);
//            sExpr.addChild(id);
//            Node number = new Node(tokenList.remove(0), id);
//            id.addChild(number);
//            dExprParse(sExpr, tokenList, true, false);
            Node id = new Node("id", parent);
            parent.addChild(id);
            Node idNode = new Node(tokenList.remove(0), id);
            id.addChild(idNode);
        }
        else if (isString(tokenList.get(0).getTokenName())){
            Node sLiteral = new Node("str_literal", parent);
            parent.addChild(sLiteral);
            Node strLiteral = new Node(tokenList.remove(0), sLiteral);
            sLiteral.addChild(strLiteral);
        }
        else if (tokenList.get(0).getTokenName().equals("concat") || tokenList.get(0).getTokenName().equals("charAt")){
            String[] grammars;
            if (tokenList.get(0).getTokenName().equals("concat")){
                grammars = JottGrammar.grammar.get("s_expr")[2];
            }else {
                grammars = JottGrammar.grammar.get("s_expr")[3];
            }
            for (String grammar: grammars){
                Node newParent = new Node(grammar, parent);
                parent.addChild(newParent);
                expandNode(newParent, tokenList);
            }
        }
        else{
            System.out.println("ERRORRRRRR");
        }
    }

    private void Expr(Node parent, List<Token> tokenList){
        if (this.lowerCase.contains(tokenList.get(0).getTokenName().substring(0, 1)) && symbolTable.containsKey(tokenList.get(0).getTokenName())){
            String exprType = symbolTable.get(tokenList.get(0).getTokenName());
            if (exprType.equals("Integer")){
                Node iExpr = new Node("i_expr", parent);
                parent.addChild(iExpr);
                iExprParse(iExpr, tokenList, true, false);
            }
            else if (exprType.equals("Double")){
                Node dExpr = new Node("d_expr", parent);
                parent.addChild(dExpr);
                dExprParse(dExpr, tokenList, true, false);
            }
            else if (exprType.equals("String")){
                Node sExpr = new Node("s_expr", parent);
                parent.addChild(sExpr);
                sExprParse(sExpr, tokenList);
            }else{
                System.out.println("NO CLUE EERROR!!!!");
            }
//            Node id = new Node("id", parent);
//            parent.addChild(id);
//            Node idNode = new Node(tokenList.remove(0), id);
//            id.addChild(idNode);
        }
        else if (tokenList.get(0).getTokenName().equals("-")){
            if (isInteger(tokenList.get(1).getTokenName())){
                Node iExpr = new Node("i_expr", parent);
                parent.addChild(iExpr);
                iExprParse(iExpr, tokenList, true, false);
            } else if (isDouble(tokenList.get(1).getTokenName())){
                Node dExpr = new Node("d_expr", parent);
                parent.addChild(dExpr);
                dExprParse(dExpr, tokenList, true, false);
            }
        }
        else if (isInteger(tokenList.get(0).getTokenName())){
            Node iExpr = new Node("i_expr", parent);
            parent.addChild(iExpr);
            iExprParse(iExpr, tokenList, true, false);
        } else if (isDouble(tokenList.get(0).getTokenName())){
            Node dExpr = new Node("d_expr", parent);
            parent.addChild(dExpr);
            dExprParse(dExpr, tokenList, true, false);
        } else if (isString(tokenList.get(0).getTokenName()) || tokenList.get(0).getTokenName().equals("concat") || tokenList.get(0).getTokenName().equals("charAt")) {
            Node sExpr = new Node("s_expr", parent);
            parent.addChild(sExpr);
            sExprParse(sExpr, tokenList);
        }
        else{
            System.out.println("ERRORRRORORORORO!!!!!!!!");
        }

    }

}
