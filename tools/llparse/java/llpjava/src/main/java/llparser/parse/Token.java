package llparser.parse;


public class Token {
    public final String v;
    private Token(String v) {
        this.v = v;
    }


    public static TString of(char c) {
        return new TString("" + c);
    }
    public static TString of(String s) {
        return new TString(s);
    }

    public static class TString extends Token {
        public TString(String v) {
            super(v);
        }
    }
}
