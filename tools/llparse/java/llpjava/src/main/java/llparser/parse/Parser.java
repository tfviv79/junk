package llparser.parse;

import java.util.ArrayList;
import java.util.List;
import llparser.io.Input;
import llparser.utils.Result;

public interface Parser {
    Result<Token, ParseError> parse(Input in);
    @SuppressWarnings("unchecked")
    default Parser name(String name) {
        return (in) -> {
            return parse(in).map(x -> x.name(name));
        };
    }

    default Parser drop() {
        return (in) -> {
            return parse(in).map(x -> Token.EMPTY);
        };
    }

    default Parser flat() {
        return (in) -> {
            return parse(in).map(x -> {
                if (x.v != null) {
                    return x;
                } else {
                    Token t = Token.list();

                    return flatton(t, x, false);
                }
            });
        };
    }

    default Parser flatR() {
        return (in) -> {
            return parse(in).map(x -> {
                if (x.v != null) {
                    return x;
                } else {
                    Token t = Token.list();

                    return flatton(t, x, true);
                }
            });
        };
    }

    // for higher java9, rewrite to private method.
    static Token flatton(Token accum, Token x, boolean recursive) {
        if (x.v != null) {
            return accum.add(x);
        } else {
            for (Token t : x.tokens()) {
                if (recursive) {
                    accum = flatton(accum, t, recursive);
                } else {
                    accum = accum.add(t);
                }
            }
            return accum;
        }
    }
}
