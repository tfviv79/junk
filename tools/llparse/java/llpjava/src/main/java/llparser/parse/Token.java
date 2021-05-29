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

    @Override
    public String toString() {
        return v;
    }


    public static Token of(char c) {
        return new Token("" + c);
    }
    public static Token of(String s) {
        return new Token(s);
    }
    public static NamedToken of(String name, Token token) {
        return new NamedToken(name, token);
    }

    public static ListToken list() {
        return new ListToken();
    }

    // 非末端トークン
    public static class ListToken extends Token {
        private List<Token> tokens = new ArrayList<>();
        public ListToken() {
            super("");
        }

        public ListToken add(Token token) {
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

    public static class NamedToken extends Token {
        public final Token token;
        public NamedToken(String name, Token token) {
            super(name);
            this.token = token;
        }

        public String name() {
            return v;
        }
    }
}
