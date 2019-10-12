package scanning;

public class Token {
    private String tokenName;
    private int lineNo;
    //private int columnNo;

    public Token(String tokenName, int lineNo){
        this.tokenName = tokenName;
        this.lineNo = lineNo;
    }

    @Override
    public String toString() {
        return tokenName + ":" + lineNo;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Token){
            Token other = (Token)obj;
            return other.getTokenName().equals(this.getTokenName());
        }
        return false;
    }

    public String getTokenName() {
        return tokenName;
    }

    public int getLineNo() {
        return lineNo;
    }
}
