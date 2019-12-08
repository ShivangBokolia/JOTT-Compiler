package parsing;

import scanning.JottScanner;

import java.util.HashMap;
import java.util.Map;

public class JottGrammar {

    public static Map<String, String[][]> grammar = new HashMap<>();

    public static void buildGrammar(){
        grammar.put("program", new String[][]{{"stmt_list", "$$"}});
        grammar.put("stmt_list", new String[][]{{"stmt", "stmt_list"},{""}}); //"" basically means empty string or epsilon
        grammar.put("start_paren", new String[][]{{"("}});
        grammar.put("end_paren", new String[][]{{")"}});
        grammar.put("end_statement", new String[][]{{";"}});
        grammar.put("char", new String[][]{{"l_char"}, {"u_char"}, {"digit"}});
        grammar.put("l_char", new String[][]{{"a"}, {"b"}, {"c"}, {"d"}, {"e"}, {"f"}, {"g"}, {"h"}, {"i"}, {"j"}, {"k"}, {"l"}, {"m"}, {"n"}, {"o"}, {"p"}, {"q"}, {"r"}, {"s"}, {"t"}, {"u"}, {"v"}, {"w"}, {"x"}, {"y"}, {"z"}});
        grammar.put("u_char", new String[][]{{"A"}, {"B"}, {"C"}, {"D"}, {"E"}, {"F"}, {"G"}, {"H"}, {"I"}, {"J"}, {"K"}, {"L"}, {"M"}, {"N"}, {"O"}, {"P"}, {"Q"}, {"R"}, {"S"}, {"T"}, {"U"}, {"V"}, {"W"}, {"X"}, {"Y"}, {"Z"}});
        grammar.put("digit", new String[][]{{"0"}, {"1"}, {"2"}, {"3"}, {"4"}, {"5"}, {"6"}, {"7"}, {"8"}, {"9"}});
        grammar.put("sign", new String[][]{{"-"}, {"+"}, {""}});
        grammar.put("id", new String[][]{{"l_char", "char*"}});
        grammar.put("stmt", new String[][]{{"print_stmt"}, {"asmt"}, {"f_call", "end_statement"},
                {"r_asmt", "end_statement"},
                {"if", "start_paren", "expr", "end_paren", "{", "b_stmt_list", "}", "else", "{", "b_stmt_list", "}"},
                {"if", "(", "expr", ")", "{", "b_stmt_list", "}"},
                {"while", "start_paren", "i_expr", "end_paren", "{", "b_stmt_list", "}"},
                {"for", "start_paren", "asmt", "i_expr", ";", "r_asmt", "end_paren", "{", "b_stmt_list", "}"},
                {"expr", "end_statement"},
                {"type", "id", "start_paren", "p_list", "end_paren", "{", "f_stmt", "}"},
                {"type", "id", "start_paren", "end_paren", "{", "f_stmt", "}"}
        });
        grammar.put("fc_p_list", new String[][]{{"expr"}, {"expr", ",", "fc_p_list"}});
        grammar.put("p_list", new String[][]{{"type", "id"}, {"type", "id", ",", "p_list"}});
        grammar.put("expr", new String[][]{{"i_expr"}, {"d_expr"}, {"s_expr"}, {"id"}});
        grammar.put("print_stmt", new String[][]{{"print", "start_paren", "expr", "end_paren", "end_statement"}});
        grammar.put("asmt", new String[][]{{"Double", "id", "=", "d_expr", "end_statement"}, {"Integer", "id", "=", "i_expr", "end_statement"}, {"String", "id", "=", "s_expr", "end_statement"}});
        grammar.put("op", new String[][]{{"+"}, {"*"}, {"/"}, {"-"}, {"^"}, {"rel_op"}});
        grammar.put("dbl", new String[][]{{"sign", "digit*", ".", "digit", "digit*"}, {"f_call"}});
        grammar.put("d_expr", new String[][]{{"id"}, {"dbl"}, {"dbl", "op", "dbl"}, {"dbl", "op", "d_expr"}, {"d_expr", "op", "dbl"}, {"d_expr", "op", "d_expr"}});
        grammar.put("int", new String[][]{{"sign", "digit", "digit*"}, {"f_call"}});
        grammar.put("i_expr", new String[][]{{"id"}, {"int"}, {"int", "op", "int"}, {"int", "op", "i_expr"}, {"i_expr", "op", "int"}, {"i_expr", "op", "i_expr"}});
        grammar.put("i_exp", new String[][]{{"i_expr", "rel_op", "i_expr"}, {"s_expr", "rel_op", "s_expr"}, {"d_expr", "rel_op", "d_expr"}});
        grammar.put("str_literal", new String[][]{{"\"str\""}});
        grammar.put("str", new String[][]{{"char", "str"}, {"space", "str"}, {""}, {"f_call"}});
        grammar.put("s_expr", new String[][]{{"str_literal"}, {"id"}, {"concat", "start_paren", "s_expr", ",", "s_expr", "end_paren"}, {"charAt", "start_paren", "s_expr", ",", "i_expr", "end_paren"}, {"s_expr", "op", "s_expr"}});
        grammar.put("rel_op", new String[][]{{">"}, {"<"}, {">="}, {"<="}, {"=="}, {"!="}});
        grammar.put("b_stmt_list", new String[][]{{"b_stmt", "b_stmt_list"}, {""}});
        grammar.put("b_stmt", new String[][]{{"f_call", "end_statement"}, {"r_asmt", "end_statement"}, {"print_stmt"}, {"if", "start_paren", "expr", "end_paren", "{", "b_stmt_list", "}", "else", "{", "b_stmt_list", "}"},
                {"if", "(", "expr", ")", "{", "b_stmt_list", "}"},
                {"while", "start_paren", "i_expr", "end_paren", "{", "b_stmt_list", "}"},
                {"for", "start_paren", "asmt", "i_expr", ";", "r_asmt", "end_paren", "{", "b_stmt_list", "}"}, {"expr", "end_statement"}});
        grammar.put("r_asmt", new String[][]{{"id", "=", "expr"}});
        grammar.put("type", new String[][]{{"Void"}, {"String"}, {"Double"}, {"Integer"}});
        grammar.put("f_call", new String[][]{{"id", "start_paren", "fc_p_list", "end_paren"}, {"id", "start_paren", "end_paren"}});
        grammar.put("f_stmt", new String[][]{{"stmt", "f_stmt"}, {"return", "expr", "end_statement"}, {""}});
    }
}
