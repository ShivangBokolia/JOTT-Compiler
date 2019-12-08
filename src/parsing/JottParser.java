package parsing;

import scanning.Token;

import java.util.*;

public class JottParser {

    public static Map<String, String> symbolTable = new HashMap<>();
    public static Map<String, FunctionClass> funcList = new HashMap<>();

    private String lastFuncCall;

    private ArrayList<String> ops = new ArrayList<>(Arrays.asList("+", "-", "*", "/", "^"));
    private ArrayList<String> relOps = new ArrayList<>(Arrays.asList(">", "<", "<=", ">=", "==", "!="));
    private ArrayList<String> lowerCase = new ArrayList<>(Arrays.asList("a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z"));
    private ArrayList<String> digits = new ArrayList<>(Arrays.asList("1", "2", "3", "4", "5", "6", "7", "8", "9", "0"));
    private ArrayList<String> signs = new ArrayList<>(Arrays.asList("+", "-", ""));
    private ArrayList<String> types = new ArrayList<>(Arrays.asList("String", "Double", "Integer", "Void"));

    public Node parseTake2(List<Token> tokenList) {

        Node root = new Node("program", null);
        Node firstSmt = new Node("stmt_list", root);
        root.addChild(firstSmt);
        int lineCount = 1;
        Node branchStart = firstSmt;
        while (!tokenList.isEmpty()) {
            List<Token> oneLine = new ArrayList<>();
            //Using ; as a parameter to move to the next statement
            String line = tokenList.get(0).getLine();
            int lineNo = tokenList.get(0).getLineNo();
            String fileName = tokenList.get(0).getFileName();
//            while (!(tokenList.isEmpty()) && !(tokenList.get(0).getTokenName().equals(";"))) {
//                oneLine.add(tokenList.remove(0));
//            }
//            if ( tokenList.isEmpty() || !tokenList.get(0).getTokenName().equals(";")){
//                System.out.println("Syntax Error: Missing ; ," + "\"" +line + "\" (" +fileName + ":" + lineNo +")");
//                System.exit(-1);
//            }
//            oneLine.add(tokenList.remove(0));       //; check

            //New condition for checking for semicolon and {}
            int openParensCount = 0, closeParensCount = 0;
            boolean isFor = tokenList.get(0).getTokenName().equals("for");
            boolean isFuncDecl = types.contains(tokenList.get(0).getTokenName());
            boolean readId = false;
            boolean isFunc = false;
            while(!(tokenList.isEmpty())){
                if (isFuncDecl && readId && tokenList.get(0).getTokenName().equals("(")) {
                    isFunc = true;
                } else if (isFuncDecl && readId && tokenList.get(0).getTokenName().equals("=")){
                    isFuncDecl = false;
                }
                if ((openParensCount == closeParensCount) && (openParensCount > 0) && (closeParensCount > 0)){
                    if (tokenList.get(0).getTokenName().equals("else")){
                        openParensCount = 0;
                        closeParensCount = 0;
                    }else{
                        break;
                    }
                }
                else if (tokenList.get(0).getTokenName().equals(";") && (openParensCount == closeParensCount) && !isFor){
                    oneLine.add(tokenList.remove(0));
                    break;
                }
                else if (tokenList.get(0).getTokenName().equals("{")){
                    openParensCount++;
                }
                else if (tokenList.get(0).getTokenName().equals("}")){
                    closeParensCount++;
                }
                if (isFuncDecl) {
                    readId = isId(tokenList.get(0).getTokenName());
                }
                oneLine.add(tokenList.remove(0));
            }
            Node statement = new Node("stmt", branchStart);
            branchStart.addChild(statement);
            Node next = statement;


            if (isFunc){
                FunctionClass func = new FunctionClass(oneLine);
                funcList.put(func.getFunName(), func);
                Node function = new Node(func, next);
                next.addChild(function);
            } else {
                List<String[]> treeBranch = checkedGrammar("stmt", oneLine);
//                for (String[] tree: treeBranch){
//                    System.out.println("_________________");
//                    for (String t: tree)
//                        System.out.println(t);
//                }
                for (int j = treeBranch.size() - 1; j >= 0; j--) {
                    for (String grammar : treeBranch.get(j)) {
                        Node newChild = new Node(grammar, next);
                        next.addChild(newChild);
                        expandNode(newChild, oneLine, isFor);
                        if (oneLine.isEmpty()){
                            break;
                        }
                    }
                    next = next.getChild(0);
                }
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

    private boolean isId(String tokenName){
        String idString = tokenName;
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
            return true;
        }
        else{
            return false;
        }
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
            else if (grammarOpt[0].equals("f_call") && funcList.containsKey(oneLine.get(0).getTokenName())){
                List<String[]> leaf = new ArrayList<>();
                leaf.add(grammarOpt);
                return leaf;
            }
            else if (grammarOpt[0].equals("f_call") && isId(oneLine.get(0).getTokenName()) && oneLine.get(1).getTokenName().equals("=")){
                continue;
            }
            else if (grammarOpt[0].equals("r_asmt") && symbolTable.containsKey(oneLine.get(0).getTokenName())){
                List<String[]> leaf = new ArrayList<>();
                leaf.add(grammarOpt);
                return leaf;
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

    private void expandNode (Node newChild, List<Token> oneLine, boolean isFor) {
        if (!(JottGrammar.grammar.containsKey(newChild.getData()))){
            if (newChild.getData().equals(oneLine.get(0).getTokenName())){
                newChild.setData(oneLine.get(0));
                oneLine.remove(0);
            }
        }
        else{
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
                        if (symbolTable.containsKey(idToken.getTokenName())){
                            System.out.println("The id has already been defined; cannot define it again., " + "\"" + idToken.getLine() +"\" (" + idToken.getFileName()+ ":" + idToken.getLineNo() + ")");
                            System.exit(-1);
                        }
                        symbolTable.put(idToken.getTokenName(), dataType.getTokenName());
                    }
                }
                else{
                    System.out.println("Invalid ID Name, " + "\"" + idToken.getLine() + "\" (" + idToken.getFileName() + ":" +idToken.getLineNo() +")");
                    System.exit(-1);
                }
            }
            else if (newChild.getData().equals("r_asmt")){
                Node id = new Node("id", newChild);
                newChild.addChild(id);
                Token id_Token = oneLine.get(0);
                expandNode(id, oneLine, isFor);
                Node equal = new Node(oneLine.remove(0), newChild);
                newChild.addChild(equal);
                Node expr = new Node("expr", newChild);
                newChild.addChild(expr);
                expandNode(expr, oneLine, isFor);
                if ((symbolTable.get(id_Token.getTokenName()).equals("Integer") && !expr.getChild(0).getData().equals("i_expr"))){
                    System.out.println("Syntax Error: Type Mismatch: Expected Integer, "+ "\"" + id_Token.getLine() +"\" (" + id_Token.getFileName() +":" + id_Token.getLineNo() +")");
                    System.exit(-1);
                }
                else if ((symbolTable.get(id_Token.getTokenName()).equals("Double") && !expr.getChild(0).getData().equals("d_expr"))){
                    System.out.println("Syntax Error: Type Mismatch: Expected Double, "+ "\"" + id_Token.getLine() +"\" (" + id_Token.getFileName() +":" + id_Token.getLineNo() +")");
                    System.exit(-1);
                }
                else if ((symbolTable.get(id_Token.getTokenName()).equals("String") && !expr.getChild(0).getData().equals("s_expr"))){
                    System.out.println("Syntax Error: Type Mismatch: Expected String, "+ "\"" + id_Token.getLine() +"\" (" + id_Token.getFileName() +":" + id_Token.getLineNo() +")");
                    System.exit(-1);
                }
            }
            else if (newChild.getData().equals("expr")){
                Expr(newChild, oneLine);
            }
            else if (newChild.getData().equals("f_call")){
                String[] gram;
                if (funcList.containsKey(oneLine.get(0).getTokenName())){
                    lastFuncCall = oneLine.get(0).getTokenName();
                    FunctionClass tempFunc = funcList.get(oneLine.get(0).getTokenName());
                    if (tempFunc.getParamNum() == 0){
                        gram = JottGrammar.grammar.get("f_call")[1];
                    } else {
                        gram = JottGrammar.grammar.get("f_call")[0];
                    }
                    for (String g: gram){
                        Node f_gram = new Node(g, newChild);
                        newChild.addChild(f_gram);
                        expandNode(f_gram, oneLine, isFor);
                    }
                }
                else {
                    System.out.println("Error: Function missing, "+ "\"" + oneLine.get(0).getLine() +"\" (" + oneLine.get(0).getFileName() +":" + oneLine.get(0).getLineNo() +")");
                    System.exit(-1);
                }
            }
            else if (newChild.getData().equals("fc_p_list")){
                int counter = 0;
                Node next = newChild;
                while(!(oneLine.isEmpty()) && !(oneLine.get(0).getTokenName().equals(")"))) {
                    Node expr = new Node("expr", next);
                    next.addChild(expr);
                    expandNode(expr, oneLine, isFor);
                    if (oneLine.get(0).getTokenName().equals(",") && funcList.get(lastFuncCall).checkParamType(counter, (String)expr.getChild(0).getData())) {
                        Node comma = new Node(oneLine.remove(0), next);
                        next.addChild(comma);
                        Node fc_p_list = new Node("fc_p_list", next);
                        next.addChild(fc_p_list);
                        next = fc_p_list;
                        counter++;
                    } else if (!funcList.get(lastFuncCall).checkParamType(counter, (String)expr.getChild(0).getData())) {
                        System.out.println("Syntax Error: Type Mismatch " + oneLine.get(0).getLine() + "\" (" + oneLine.get(0).getFileName() + ":" + oneLine.get(0).getLineNo() + ")");
                        System.exit(-1);
                    }
                    else if (!(oneLine.get(0).getTokenName().equals(")")) && !(oneLine.get(0).getTokenName().equals(","))) {
                        System.out.println("Syntax Error: Missing bracket " + "\"" + oneLine.get(0).getLine() + "\" (" + " " + oneLine.get(0).getFileName() + ":" + oneLine.get(0).getLineNo() + ")");
                        System.exit(0);
                    }
                }
//                Node expr = new Node("expr", newChild);
//                newChild.addChild(expr);
//                expandNode(expr, oneLine, isFor);
//                if (!funcList.get(lastFuncCall).checkParamType(counter, (String)expr.getChild(0).getData())){
//                    System.out.println("Syntax Error: Type Mismatch" + oneLine.get(0).getLine() +"\" (" + oneLine.get(0).getFileName() +":" + oneLine.get(0).getLineNo() +")");
//                    System.exit(-1);
//                }
//                if (oneLine.get(0).getTokenName().equals(",")){
//                    Node comma = new Node(oneLine.remove(0), newChild);
//                    newChild.addChild(comma);
//                    Node fc_p_list = new Node("fc_p_list", newChild);
//                    newChild.addChild(fc_p_list);
////                    expandNode(fc_p_list, oneLine, isFor);
//                    while(oneLine.get(0).getTokenName().equals(")")){
//                        Node expr2 = new Node("expr", newChild);
//                        newChild.addChild(expr2);
//                        expandNode(expr2, oneLine, isFor);
//                        if (oneLine.get(0).getTokenName().equals(",")) {
//                            Node comma2 = new Node(oneLine.remove(0), newChild);
//                            newChild.addChild(comma2);
//                            Node fc_p_list2 = new Node("fc_p_list", newChild);
//                            newChild.addChild(fc_p_list2);
//                            oneLine.remove(0);
//                        }
//                    }
//                } else if (!oneLine.get(0).getTokenName().equals(")")){
//                    System.out.println("Syntax Error: Missing \")\", "+ "\"" + oneLine.get(0).getLine() +"\" (" + oneLine.get(0).getFileName() +":" + oneLine.get(0).getLineNo() +")");
//                    System.exit(-1);
//                }
            }
            else if (newChild.getData().equals("b_stmt_list")){
                if (oneLine.get(0).getTokenName().equals("}")){
                    Node endBstmt = new Node("", newChild);
                    newChild.addChild(endBstmt);
                } else {
                    Node startBstmt = new Node("b_stmt", newChild);
                    newChild.addChild(startBstmt);
                    expandNode(startBstmt, oneLine, isFor);
                    Node bStmtList = new Node("b_stmt_list", newChild);
                    newChild.addChild(bStmtList);
                    expandNode(bStmtList, oneLine, isFor);
                }
            }
            else if (newChild.getData().equals("b_stmt")){
                List<String[]> chosenGrammar = checkedGrammar("b_stmt", oneLine);
                Node next = newChild;
                boolean loop = oneLine.get(0).getTokenName().equals("for");
                for (int i = chosenGrammar.size() - 1; i >= 0; i--){
                    for (String grammar: chosenGrammar.get(i)){
                        Node newestChild = new Node(grammar, next);
                        next.addChild(newestChild);
                        expandNode(newestChild, oneLine, loop);
                        if (oneLine.isEmpty() || (!oneLine.get(0).getTokenName().equals("else") && grammar.equals("}"))){
                            break;
                        }
                    }
                    next = next.getChild(0);
                }

            }
            else if (newChild.getData().equals("asmt") && isFor){
                List<String[]> forStatement = checkedGrammar("asmt", oneLine);
                for (String grammar : forStatement.get(0)) {
                    Node newerChild = new Node(grammar, newChild);
                    newChild.addChild(newerChild);
                    expandNode(newerChild, oneLine, isFor);
                }
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

    private void iExprParse (Node parent, List<Token> tokenList, boolean begin, boolean isOp){
        if (begin || isOp){
            if (signs.contains(tokenList.get(0).getTokenName())){
                Token sign = tokenList.remove(0);
                Token integer = tokenList.remove(0);
                Token signedInt = new Token(sign.getTokenName()+integer.getTokenName(), integer.getLineNo(), integer.getFileName(), integer.getLine());
                Node newInt = new Node("int", parent);
                parent.addChild(newInt);
                Node number = new Node(signedInt, newInt);
                newInt.addChild(number);
                if (!tokenList.isEmpty() && !tokenList.get(0).getTokenName().substring(0, 1).equals(";") && !tokenList.get(0).getTokenName().substring(0, 1).equals(")") && !tokenList.get(0).getTokenName().substring(0, 1).equals(",")){
                    iExprParse(parent, tokenList, false, false);
                }
            }
            else if (isInteger(tokenList.get(0).getTokenName())){
                Node newInt = new Node("int", parent);
                parent.addChild(newInt);
                Node number = new Node(tokenList.remove(0), newInt);
                newInt.addChild(number);
                if (!tokenList.isEmpty() && !tokenList.get(0).getTokenName().substring(0, 1).equals(";") && !tokenList.get(0).getTokenName().substring(0, 1).equals(")") && !tokenList.get(0).getTokenName().substring(0, 1).equals(",")){
                    iExprParse(parent, tokenList, false, false);
                }
            }
            else if (this.lowerCase.contains(tokenList.get(0).getTokenName().substring(0, 1)) && symbolTable.containsKey(tokenList.get(0).getTokenName()) && symbolTable.get(tokenList.get(0).getTokenName()).equals("Integer")){
                Node iExpr = new Node("i_expr", parent);
                parent.addChild(iExpr);
                Node id = new Node("id", iExpr);
                iExpr.addChild(id);
                Node number = new Node(tokenList.remove(0), id);
                id.addChild(number);
                if (!tokenList.isEmpty() && !tokenList.get(0).getTokenName().substring(0, 1).equals(";") && !tokenList.get(0).getTokenName().substring(0, 1).equals(")") && !tokenList.get(0).getTokenName().substring(0, 1).equals(",")){
                    iExprParse(parent, tokenList, false, false);
                }
            }
            else if (this.lowerCase.contains(tokenList.get(0).getTokenName().substring(0, 1)) && funcList.containsKey(tokenList.get(0).getTokenName()) && funcList.get(tokenList.get(0).getTokenName()).getReturnType().equals("Integer")){
                Node newInt = new Node("int", parent);
                parent.addChild(newInt);
                Node f_call = new Node("f_call", newInt);
                newInt.addChild(f_call);
                expandNode(f_call, tokenList, false);
            }
            else{
                System.out.println("Syntax Error: Type Mismatch: Expected Integer, "+ "\"" + tokenList.get(0).getLine() +"\" (" + tokenList.get(0).getFileName() +":" + tokenList.get(0).getLineNo() +")");
                System.exit(-1);
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
        else if (relOps.contains(tokenList.get(0).getTokenName()) && !isOp){
            Node op = new Node("op", parent);
            parent.addChild(op);
            Node rel_op = new Node("rel_op", op);
            op.addChild(rel_op);
            Node opNode = new Node(tokenList.remove(0), rel_op);
            rel_op.addChild(opNode);
            Node iExpr = new Node("i_expr", parent);
            parent.addChild(iExpr);
            iExprParse(iExpr, tokenList, false, true);
        }
        else{
            System.out.println("Invalid Operator/Sign, "+ "\"" + tokenList.get(0).getLine() +"\" (" + tokenList.get(0).getFileName() +":" + tokenList.get(0).getLineNo() +")");
            System.exit(-1);
        }
    }

    private void dExprParse (Node parent, List<Token> tokenList, boolean begin, boolean isOp){
        if (begin || isOp){
            if (signs.contains(tokenList.get(0).getTokenName())){
                Token sign = tokenList.remove(0);
                Token dbl = tokenList.remove(0);
                Token signedDbl = new Token(sign.getTokenName()+dbl.getTokenName(), dbl.getLineNo(), dbl.getFileName(), dbl.getLine());
                Node newDbl = new Node("dbl", parent);
                parent.addChild(newDbl);
                Node number = new Node(signedDbl, newDbl);
                newDbl.addChild(number);
                if (!tokenList.isEmpty() && !tokenList.get(0).getTokenName().substring(0, 1).equals(";") && !tokenList.get(0).getTokenName().substring(0, 1).equals(")") && !tokenList.get(0).getTokenName().substring(0, 1).equals(",")){
                    dExprParse(parent, tokenList, false, false);
                }
            }
            else if (isDouble(tokenList.get(0).getTokenName())){
                Node newDbl = new Node("dbl", parent);
                parent.addChild(newDbl);
                Node number = new Node(tokenList.remove(0), newDbl);
                newDbl.addChild(number);
                if (!tokenList.isEmpty() && !tokenList.get(0).getTokenName().substring(0, 1).equals(";") && !tokenList.get(0).getTokenName().substring(0, 1).equals(")") && !tokenList.get(0).getTokenName().substring(0, 1).equals(",")){
                    dExprParse(parent, tokenList, false, false);
                }
            }
            else if (this.lowerCase.contains(tokenList.get(0).getTokenName().substring(0, 1)) && symbolTable.containsKey(tokenList.get(0).getTokenName()) && symbolTable.get(tokenList.get(0).getTokenName()).equals("Double")){
                Node dExpr = new Node("d_expr", parent);
                parent.addChild(dExpr);
                Node id = new Node("id", dExpr);
                dExpr.addChild(id);
                Node number = new Node(tokenList.remove(0), id);
                id.addChild(number);
                if (!tokenList.isEmpty() && !tokenList.get(0).getTokenName().substring(0, 1).equals(";") && !tokenList.get(0).getTokenName().substring(0, 1).equals(")") && !tokenList.get(0).getTokenName().substring(0, 1).equals(",")){
                    dExprParse(parent, tokenList, false, false);
                }
            }
            else if (this.lowerCase.contains(tokenList.get(0).getTokenName().substring(0, 1)) && funcList.containsKey(tokenList.get(0).getTokenName()) && funcList.get(tokenList.get(0).getTokenName()).getReturnType().equals("Double")){
                Node newDbl = new Node("dbl", parent);
                parent.addChild(newDbl);
                Node f_call = new Node("f_call", newDbl);
                newDbl.addChild(f_call);
                expandNode(f_call, tokenList, false);
            }
            else{
                System.out.println("Syntax Error: Type Mismatch: Expected Double, "+ "\"" + tokenList.get(0).getLine() +"\" (" + tokenList.get(0).getFileName() +":" + tokenList.get(0).getLineNo() +")");
                System.exit(-1);
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
        else if (relOps.contains(tokenList.get(0).getTokenName()) && !isOp){
            Node op = new Node("op", parent);
            parent.addChild(op);
            Node relOp = new Node("rel_op", op);
            op.addChild(relOp);
            Node opNode = new Node(tokenList.remove(0), relOp);
            relOp.addChild(opNode);
            Node dExpr = new Node("d_expr", parent);
            parent.addChild(dExpr);
            dExprParse(dExpr, tokenList, false, true);
        }
        else{
            System.out.println("Invalid Operator/Sign, "+ "\"" + tokenList.get(0).getLine() +"\" (" + tokenList.get(0).getFileName() +":" + tokenList.get(0).getLineNo() +")");
            System.exit(-1);
        }
    }

    private void sExprParse(Node parent, List<Token> tokenList){
        if (this.lowerCase.contains(tokenList.get(0).getTokenName().substring(0, 1)) && symbolTable.containsKey(tokenList.get(0).getTokenName()) && symbolTable.get(tokenList.get(0).getTokenName()).equals("String")){
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
                expandNode(newParent, tokenList, false);
            }
        }
        else if (this.lowerCase.contains(tokenList.get(0).getTokenName().substring(0, 1)) && funcList.containsKey(tokenList.get(0).getTokenName()) && funcList.get(tokenList.get(0).getTokenName()).getReturnType().equals("String")){
            Node newStr = new Node("str_literal", parent);
            parent.addChild(newStr);
            Node f_call = new Node("f_call", newStr);
            newStr.addChild(f_call);
            expandNode(f_call, tokenList, false);
        }
        else{
            System.out.println("Syntax Error: Type Mismatch: Expected String, "+ "\"" + tokenList.get(0).getLine() +"\" (" + tokenList.get(0).getFileName() +":" + tokenList.get(0).getLineNo() +")");
            System.exit(-1);
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
                System.out.println("Invalid Type for ID, "+ "\"" + tokenList.get(0).getLine() +"\" (" + tokenList.get(0).getFileName() +":" + tokenList.get(0).getLineNo() +")");
                System.exit(-1);
            }
        } else if (this.lowerCase.contains(tokenList.get(0).getTokenName().substring(0, 1)) && funcList.containsKey(tokenList.get(0).getTokenName())){
            String retType = funcList.get(tokenList.get(0).getTokenName()).getReturnType();
            if (retType.equals("Integer")){
                Node iExpr = new Node("i_expr", parent);
                parent.addChild(iExpr);
                iExprParse(iExpr, tokenList, true, false);
            } else if (retType.equals("Double")){
                Node dExpr = new Node("d_expr", parent);
                parent.addChild(dExpr);
                dExprParse(dExpr, tokenList, true, false);
            }
            else if (retType.equals("String")){
                Node sExpr = new Node("s_expr", parent);
                parent.addChild(sExpr);
                sExprParse(sExpr, tokenList);
            } else{
                System.out.println("Invalid Type for ID, "+ "\"" + tokenList.get(0).getLine() +"\" (" + tokenList.get(0).getFileName() +":" + tokenList.get(0).getLineNo() +")");
                System.exit(-1);
            }
        } else if (tokenList.get(0).getTokenName().equals("-")){
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
            System.out.println("Invalid Syntax, " + "\"" + tokenList.get(0).getLine() + "\" (" + tokenList.get(0).getFileName() + ":" +tokenList.get(0).getLineNo() +")");
            System.exit(-1);
        }

    }

}
