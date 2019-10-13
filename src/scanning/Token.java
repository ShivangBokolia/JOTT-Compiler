package scanning;

public class Token {
    private String tokenName;
    private int lineNo;
    private String fileName;
    private String line;

    public Token(String tokenName, int lineNo, String fileName, String line){
        this.tokenName = tokenName;
        this.lineNo = lineNo;
        this.fileName = fileName;
        this.line = line;
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

    public String getFileName(){
        return fileName;
    }

    public String getLine(){
        return line;
    }
}
