package parsing;

import scanning.Token;

import java.util.*;

public class FunctionClass {

    private Node body;
    private String funName;
    private String returnType;
    private Map<String, String> symbolTable;
//    private int paramNum = 0;
    private ArrayList<String> paramList;
    private String lastFuncCall;
    private Node decoratedBody;

    private ArrayList<String> ops = new ArrayList<>(Arrays.asList("+", "-", "*", "/", "^"));
    private ArrayList<String> relOps = new ArrayList<>(Arrays.asList(">", "<", "<=", ">=", "==", "!="));
    private ArrayList<String> lowerCase = new ArrayList<>(Arrays.asList("a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z"));
    private ArrayList<String> digits = new ArrayList<>(Arrays.asList("1", "2", "3", "4", "5", "6", "7", "8", "9", "0"));
    private ArrayList<String> signs = new ArrayList<>(Arrays.asList("+", "-", ""));
    private ArrayList<String> types = new ArrayList<>(Arrays.asList("String", "Double", "Integer", "Void"));




    public FunctionClass(List<Token> oneLine){
        this.body = new Node("f_stmt", null);
        this.symbolTable = new HashMap<>();
        this.paramList = new ArrayList<>();
        this.returnType = oneLine.remove(0).getTokenName();
        this.funName = oneLine.remove(0).getTokenName();
        this.decoratedBody = new Node(this.funName, null);

        if (!(oneLine.get(0).getTokenName().equals("("))){
            System.out.println("Syntax Error: Missing bracket " + "\"" +oneLine.get(0).getLine()+ "\" (" + " " + oneLine.get(0).getFileName()+ ":" + oneLine.get(0).getLineNo() + ")");
            System.exit(0);
        } else {
            oneLine.remove(0);
        }

        String paramType = null;
        String paramName = null;

        while (!(oneLine.isEmpty()) && !(oneLine.get(0).getTokenName().equals(")"))){
            if (types.contains(oneLine.get(0).getTokenName())){
                paramType = oneLine.remove(0).getTokenName();
            } else {
                System.out.println("Syntax Error: Invalid input type for the function " + "\"" +oneLine.get(0).getLine()+ "\" (" + " " + oneLine.get(0).getFileName()+ ":" + oneLine.get(0).getLineNo() + ")");
                System.exit(0);
            }

            if (isId(oneLine.get(0).getTokenName()) && !(this.symbolTable.containsKey(oneLine.get(0).getTokenName()))){
                paramName = oneLine.remove(0).getTokenName();
            } else {
                System.out.println("Syntax Error: Invalid input for the function" + "\"" +oneLine.get(0).getLine()+ "\" (" + " " + oneLine.get(0).getFileName()+ ":" + oneLine.get(0).getLineNo() + ")");
                System.exit(0);
            }

            this.symbolTable.put(paramName, paramType);
            this.paramList.add(paramName);

            if (!(oneLine.get(0).getTokenName().equals(")")) && !(oneLine.get(0).getTokenName().equals(","))) {
                System.out.println("Syntax Error: Missing bracket" + "\"" + oneLine.get(0).getLine() + "\" (" + " " + oneLine.get(0).getFileName() + ":" + oneLine.get(0).getLineNo() + ")");
                System.exit(0);
            } else if (oneLine.get(0).getTokenName().equals(",")){
                oneLine.remove(0);
            }
//            } else {
//                oneLine.remove(0);
//            }
//            paramNum++;
        }

        if (!(oneLine.get(0).getTokenName().equals(")"))){
            System.out.println("Syntax Error: Missing bracket" + "\"" +oneLine.get(0).getLine()+ "\" (" + " " + oneLine.get(0).getFileName()+ ":" + oneLine.get(0).getLineNo() + ")");
            System.exit(0);
        } else {
            oneLine.remove(0);
        }
        if (!(oneLine.get(0).getTokenName().equals("{"))){

            System.out.println("Syntax Error: Missing bracket" + "\"" +oneLine.get(0).getLine()+ "\" (" + " " + oneLine.get(0).getFileName()+ ":" + oneLine.get(0).getLineNo() + ")");
            System.exit(0);
        } else {
            oneLine.remove(0);
        }
        expandNode(body, oneLine, false);
        if (!(oneLine.get(0).getTokenName().equals("}"))){
            System.out.println("Syntax Error: Missing bracket" + "\"" +oneLine.get(0).getLine()+ "\" (" + " " + oneLine.get(0).getFileName()+ ":" + oneLine.get(0).getLineNo() + ")");
            System.exit(0);
        } else {
            oneLine.remove(0);
        }
    }

    public String getFunName(){
        return funName;
    }

    public int getParamNum() {
        return paramList.size();
    }

    public boolean checkParamType (int index, String exprType){
        String name = paramList.get(index);
        String paramType = symbolTable.get(name);
        if ((paramType.equals("Integer") && exprType.equals("i_expr")) || (paramType.equals("Double") && exprType.equals("d_expr")) || (paramType.equals("String") && exprType.equals("s_expr"))){
            return true;
        } else {
            return false;
        }
    }

    public ArrayList<String> getParamList(){
        return paramList;
    }

    public String getReturnType() {
        return returnType;
    }

    public Node getBody(){
        return body;
    }

    public Node getDecoratedBody(){
        return decoratedBody;
    }

    public Map<String, String> getSymbolTable(){
        return symbolTable;
    }

    @Override
    public String toString() {
        body.inorderPrint();
        return super.toString();
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
            }else if (grammarOpt[0].equals("r_asmt") && (symbolTable.containsKey(oneLine.get(0).getTokenName()) || JottParser.symbolTable.containsKey(oneLine.get(0).getTokenName()))){
                List<String[]> leaf = new ArrayList<>();
                leaf.add(grammarOpt);
                return leaf;
            }
            else if (grammarOpt[0].equals("f_call") && isId(oneLine.get(0).getTokenName()) && oneLine.get(1).getTokenName().equals("=")){
                continue;
            }
            else if (grammarOpt[0].equals("f_call") && (JottParser.funcList.containsKey(oneLine.get(0).getTokenName()) || oneLine.get(0).getTokenName().equals(this.funName))){
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
                String idTokenType;
                if (this.symbolTable.containsKey(id_Token.getTokenName())){
                    idTokenType = this.symbolTable.get(id_Token.getTokenName());
                } else {
                    idTokenType = JottParser.symbolTable.get(id_Token.getTokenName());
                }
                if (idTokenType.equals("Integer") && !expr.getChild(0).getData().equals("i_expr")){
                    System.out.println("Syntax Error: Type Mismatch: Expected Integer, "+ "\"" + id_Token.getLine() +"\" (" + id_Token.getFileName() +":" + id_Token.getLineNo() +")");
                    System.exit(-1);
                }
                else if (idTokenType.equals("Double") && !expr.getChild(0).getData().equals("d_expr")){
                    System.out.println("Syntax Error: Type Mismatch: Expected Double, "+ "\"" + id_Token.getLine() +"\" (" + id_Token.getFileName() +":" + id_Token.getLineNo() +")");
                    System.exit(-1);
                }
                else if (idTokenType.equals("String") && !expr.getChild(0).getData().equals("s_expr")){
                    System.out.println("Syntax Error: Type Mismatch: Expected String, "+ "\"" + id_Token.getLine() +"\" (" + id_Token.getFileName() +":" + id_Token.getLineNo() +")");
                    System.exit(-1);
                }
            }
            else if (newChild.getData().equals("expr")){
                Expr(newChild, oneLine);
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
            else if (newChild.getData().equals("f_call")){
                String[] gram;
                if (JottParser.funcList.containsKey(oneLine.get(0).getTokenName()) || this.funName.equals(oneLine.get(0).getTokenName())){
                    lastFuncCall = oneLine.get(0).getTokenName();
                    FunctionClass tempFunc;
                    if (JottParser.funcList.containsKey(oneLine.get(0).getTokenName())){
                        tempFunc = JottParser.funcList.get(oneLine.get(0).getTokenName());
                    } else {
                        tempFunc = this;
                    }
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
                    FunctionClass lastFunc;
                    if (JottParser.funcList.containsKey(oneLine.get(0).getTokenName())){
                        lastFunc = JottParser.funcList.get(oneLine.get(0).getTokenName());
                    } else {
                        lastFunc = this;
                    }
                    if (oneLine.get(0).getTokenName().equals(",") && lastFunc.checkParamType(counter, (String)expr.getChild(0).getData())) {
                        Node comma = new Node(oneLine.remove(0), next);
                        next.addChild(comma);
                        Node fc_p_list = new Node("fc_p_list", next);
                        next.addChild(fc_p_list);
                        next = fc_p_list;
                        counter++;
                    } else if (!lastFunc.checkParamType(counter, (String)expr.getChild(0).getData()) ) {
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
//                if (oneLine.get(0).getTokenName().equals(",")){
//                    Node comma = new Node(oneLine.remove(0), newChild);
//                    newChild.addChild(comma);
//                    Node fc_p_list = new Node("fc_p_list", newChild);
//                    newChild.addChild(fc_p_list);
//                    expandNode(fc_p_list, oneLine, isFor);
//                } else if (!oneLine.get(0).getTokenName().equals(")")){
//                    System.out.println("Syntax Error: Missing \")\", "+ "\"" + oneLine.get(0).getLine() +"\" (" + oneLine.get(0).getFileName() +":" + oneLine.get(0).getLineNo() +")");
//                    System.exit(-1);
//                }
            }
            else if (newChild.getData().equals("f_stmt")){
                if (oneLine.get(0).getTokenName().equals("return")){
                    Node endFstmt = new Node(oneLine.remove(0), newChild);
                    newChild.addChild(endFstmt);
                    Node fExpr = new Node("expr", newChild);
                    newChild.addChild(fExpr);
                    expandNode(fExpr, oneLine, isFor);
                    if (((fExpr.getChild(0).getData()).equals("i_expr") && returnType.equals("Integer")) ||
                            ((fExpr.getChild(0).getData()).equals("d_expr") && returnType.equals("Double")) ||
                            ((fExpr.getChild(0).getData()).equals("s_expr") && returnType.equals("String"))){
                        Node fEndStmt = new Node("end_statement", newChild);
                        newChild.addChild(fEndStmt);
                        expandNode(fEndStmt,oneLine, isFor);
                    } else {
                        System.out.println("Syntax Error: Type Mismatch " + oneLine.get(0).getLine() + "\" (" + oneLine.get(0).getFileName() + ":" + oneLine.get(0).getLineNo() + ")");
                        System.exit(-1);
                    }
                } else if (returnType.equals("Void") && oneLine.get(0).getTokenName().equals("}") && oneLine.size() == 1){
                    Node fEndStmt = new Node("", newChild);
                    newChild.addChild(fEndStmt);
//                    expandNode(fEndStmt,oneLine, isFor);
                    return;
                } else {
                    Node stmt = new Node("stmt", newChild);
                    newChild.addChild(stmt);
                    expandNode(stmt, oneLine, isFor);
                    Node fStmt = new Node("f_stmt", newChild);
                    newChild.addChild(fStmt);
                    expandNode(fStmt, oneLine, isFor);
                }
            }
            else if (newChild.getData().equals("stmt")){
                List<String[]> chosenGrammar = checkedGrammar("stmt", oneLine);
//                for (String[] s: chosenGrammar){
//                    for (String str: s){
//                        System.out.println(str);
//                    }
//                }
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
            else if (this.lowerCase.contains(tokenList.get(0).getTokenName().substring(0, 1)) && JottParser.symbolTable.containsKey(tokenList.get(0).getTokenName()) && JottParser.symbolTable.get(tokenList.get(0).getTokenName()).equals("Integer")){
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
            else if (this.lowerCase.contains(tokenList.get(0).getTokenName().substring(0, 1)) && JottParser.funcList.containsKey(tokenList.get(0).getTokenName()) && JottParser.funcList.get(tokenList.get(0).getTokenName()).getReturnType().equals("Integer")){
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
            else if (this.lowerCase.contains(tokenList.get(0).getTokenName().substring(0, 1)) && JottParser.symbolTable.containsKey(tokenList.get(0).getTokenName()) && JottParser.symbolTable.get(tokenList.get(0).getTokenName()).equals("Double")){
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
            else if (this.lowerCase.contains(tokenList.get(0).getTokenName().substring(0, 1)) && JottParser.funcList.containsKey(tokenList.get(0).getTokenName()) && JottParser.funcList.get(tokenList.get(0).getTokenName()).getReturnType().equals("Double")){
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
        else if (this.lowerCase.contains(tokenList.get(0).getTokenName().substring(0, 1)) && JottParser.symbolTable.containsKey(tokenList.get(0).getTokenName()) && JottParser.symbolTable.get(tokenList.get(0).getTokenName()).equals("String")){
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
        else if (this.lowerCase.contains(tokenList.get(0).getTokenName().substring(0, 1)) && JottParser.funcList.containsKey(tokenList.get(0).getTokenName()) && JottParser.funcList.get(tokenList.get(0).getTokenName()).getReturnType().equals("String")){
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
        if (this.lowerCase.contains(tokenList.get(0).getTokenName().substring(0, 1)) && (symbolTable.containsKey(tokenList.get(0).getTokenName()) || JottParser.symbolTable.containsKey(tokenList.get(0).getTokenName()))){
            String exprType;
            if (symbolTable.containsKey(tokenList.get(0).getTokenName())){
                exprType = symbolTable.get(tokenList.get(0).getTokenName());
            } else {
                exprType = JottParser.symbolTable.get(tokenList.get(0).getTokenName());
            }
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
            System.out.println("Invalid Syntax, " + "\"" + tokenList.get(0).getLine() + "\" (" + tokenList.get(0).getFileName() + ":" +tokenList.get(0).getLineNo() +")");
            System.exit(-1);
        }

    }

}
