package llparser.parse;

import java.util.stream.Collectors;
import java.util.Optional;
import java.util.ArrayList;
import java.util.List;
import java.util.Collections;


public class Token {
    public static Token EMPTY = new Token(null, "", null);
    public final String name;
    public final String v;
    private final List<Token> tokens;

    private Token(String name, String v, List<Token> tokens) {
        if (v == null && tokens == null) {
            throw new IllegalArgumentException("both null(val, list)");
        }
        this.name = name;
        this.v = v;
        this.tokens = tokens;
    }
    private Token(String v) {
        this(null, v, null);
    }

    public Iterable<Token> tokens() {
        return tokens;
    }

    public Token name(String name) {
        return new Token(name, v, tokens);
    }

    public Optional<Token> get(int pos) {
        if (tokens == null) {
            return Optional.empty();
        } else {
            if (pos < tokens.size()) {
                return Optional.of(tokens.get(pos));
            } else {
                return Optional.empty();
            }
        }
    }

    public Token add(Token token) {
        if (tokens != null) {
            if (token != EMPTY) {
                List<Token> l = new ArrayList<>();
                l.addAll(tokens);
                l.add(token);
                return new Token(name, v, Collections.unmodifiableList(l));
            } else {
                return this;
            }
        } else {
            return list(this, token);
        }
    }

    @Override
    public String toString() {
        final String body;
        if (v != null) {
            body = v;
        } else if (tokens != null) {
            body = tokens.toString();
        } else {
            body = "null";
        }
        if (name != null) {
            return String.format("%s[%s]", name, body);
        } else {
            return body;
        }
    }


    public static Token of(char c) {
        return new Token("" + c);
    }

    public static Token of(String s) {
        if (s == null || s.isEmpty()) {
            return EMPTY;
        }
        return new Token(s);
    }

    public static Token list(Token ... tokens) {
        List<Token> t = List.of(tokens).stream()
                .filter(x -> x != EMPTY)
                .collect(Collectors.toList());
        return new Token(null, null, t);
    }
}
