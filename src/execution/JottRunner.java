package execution;

import parsing.Node;
import scanning.Token;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JottRunner {

    private static Map<String, String> valueTable = new HashMap<>();

    private int evaluateInteger (Node valueNode){
        int accum = 0;
        if (valueTable.containsKey(((Token)valueNode.getChild(0).getData()).getTokenName())){
            int idValue = Integer.parseInt(valueTable.get(((Token)valueNode.getChild(0).getData()).getTokenName()));
            accum += idValue;
        }
        else{
            accum += Integer.parseInt(((Token)valueNode.getChild(0).getData()).getTokenName());
        }
        List<Node> values = valueNode.getChildren();
        for (int i=1; i<values.size(); i+=2){
            String op = (((Token)valueNode.getChild(i).getData()).getTokenName());
            int operand;
            if (valueTable.containsKey(((Token)valueNode.getChild(i+1).getData()).getTokenName())) {
                operand = Integer.parseInt(valueTable.get(((Token)valueNode.getChild(i+1).getData()).getTokenName()));
            } else{
                operand = Integer.parseInt(((Token)valueNode.getChild(i+1).getData()).getTokenName());
            }
            if (op.equals("+")){
                accum += operand;
            }else if (op.equals("-")){
                accum -= operand;
            }else if (op.equals("*")){
                accum *= operand;
            }else if (op.equals("/")){
                accum /= operand;
            }else if (op.equals("^")){
                accum = (int)Math.pow(accum, operand);
            }
        }
        return accum;
    }

    private double evaluateDouble (Node valueNode){
        double accum = 0.0;
        if (valueTable.containsKey(((Token)valueNode.getChild(0).getData()).getTokenName())){
            double idValue = Double.parseDouble(valueTable.get(((Token)valueNode.getChild(0).getData()).getTokenName()));
            accum += idValue;
        }
        else{
            accum += Double.parseDouble(((Token)valueNode.getChild(0).getData()).getTokenName());
        }
        List<Node> values = valueNode.getChildren();
        for (int i=1; i<values.size(); i+=2){
            String op = (((Token)valueNode.getChild(i).getData()).getTokenName());
            double operand;
            if (valueTable.containsKey(((Token)valueNode.getChild(i+1).getData()).getTokenName())){
                operand = Double.parseDouble(valueTable.get(((Token)valueNode.getChild(i+1).getData()).getTokenName()));
            }else {
                operand = Double.parseDouble(((Token) valueNode.getChild(i + 1).getData()).getTokenName());
            }
            if (op.equals("+")){
                accum += operand;
            }else if (op.equals("-")){
                accum -= operand;
            }else if (op.equals("*")){
                accum *= operand;
            }else if (op.equals("/")){
                accum /= operand;
            }else if (op.equals("^")){
                accum = Math.pow(accum, operand);
            }
        }
        return accum;
    }

    private String evaluateString (Node valueNode){
        if (valueTable.containsKey(((Token) valueNode.getChild(0).getData()).getTokenName())){
            String idValue = valueTable.get(((Token) valueNode.getChild(0).getData()).getTokenName());
            return idValue;
        }
        else if ((((Token) valueNode.getChild(0).getData()).getTokenName()).equals("concat")){
            String string1 = evaluateString(valueNode.getChild(0).getChild(0));
            String string2 = evaluateString(valueNode.getChild(0).getChild(1));
            return string1 + string2;
        }
        else if ((((Token) valueNode.getChild(0).getData()).getTokenName()).equals("charAt")){
            String string1 = evaluateString(valueNode.getChild(0).getChild(0));
            int index = evaluateInteger(valueNode.getChild(0).getChild(1));
            return String.valueOf(string1.charAt(index));
        }
        else{
            String literalValue = (((Token) valueNode.getChild(0).getData()).getTokenName());
            return literalValue.substring(1, literalValue.length() - 1);
        }
    }

    public void runCode (Node decoratedTreeRoot){
        List<Node> statements = decoratedTreeRoot.getChildren();
        for (Node statement: statements){
            if (statement.getData().equals("i_decl")){
                int idValue = evaluateInteger(statement.getChild(1));
                String id = ((Token)statement.getChild(0).getData()).getTokenName();
                valueTable.put(id, String.valueOf(idValue));
            }
            else if (statement.getData().equals("d_decl")){
                double idValue = evaluateDouble(statement.getChild(1));
                String id = ((Token)statement.getChild(0).getData()).getTokenName();
                valueTable.put(id, String.valueOf(idValue));
            }
            else if (statement.getData().equals("s_decl")){
                String idValue = evaluateString(statement.getChild(1));
                String id = ((Token)statement.getChild(0).getData()).getTokenName();
                valueTable.put(id, String.valueOf(idValue));
            }
            else if (statement.getData().equals("exprs")){
                String exprsType = ((String)statement.getChild(0).getData());
                if (exprsType.equals("i_expres")){
                    evaluateInteger(statement.getChild(0).getChild(0));
                }
                else if (exprsType.equals("d_expres")){
                    evaluateDouble(statement.getChild(0).getChild(0));
                }
                else if (exprsType.equals("s_expres")){
                    evaluateString(statement.getChild(0).getChild(0));
                }
            }
            else if (statement.getData().equals("print_expres")){
                String exprsType = ((String)statement.getChild(0).getData());
                if (exprsType.equals("i_expres")){
                    System.out.println(evaluateInteger(statement.getChild(0).getChild(0)));
                }
                else if (exprsType.equals("d_expres")){
                    System.out.println(evaluateDouble(statement.getChild(0).getChild(0)));
                }
                else if (exprsType.equals("s_expres")){
                    System.out.println(evaluateString(statement.getChild(0).getChild(0)));
                }
            }
        }
    }

}
