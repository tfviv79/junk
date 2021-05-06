package llparser.parse;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;


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
    public static TSpace sp(String s) {
        return new TSpace(s);
    }

    public static TContainer list(String s) {
        return new TContainer(s);
    }

    // 非末端トークン
    public static class TContainer extends Token {
        private List<Token> tokens = new ArrayList<>();
        public TContainer(String v) {
            super(v);
        }

        public TContainer add(Token token) {
            tokens.add(token);
            return this;
        }

        public Optional<Token> get(final int i) {
            if (i < 0  || tokens.size() <= i) {
                return Optional.empty();
            }
            return Optional.ofNullable(tokens.get(i));
        }

        public Iterator<Token> iter() {
            return tokens.iterator();
        }
    }


    // 末端トークン
    public static abstract class TTerminal extends Token {
        public TTerminal(String v) {
            super(v);
        }
    }


    public static class TString extends TTerminal {
        public TString(String v) {
            super(v);
        }
    }
    public static class TSpace extends TTerminal {
        public TSpace(String v) {
            super(v);
        }
    }
}
