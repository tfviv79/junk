package llparser.parse;

import llparser.io.Input;
import llparser.io.Pos;
import llparser.utils.Result;

public final class Parsers {
    // スペーストークン
    public static Parser<Token.TSpace> sp = (Input in) -> {
        Pos p = in.pos();
        StringBuilder sb = new StringBuilder();
        char inChar = in.peek();
        while(isSp(inChar)) {
            sb.append(inChar);
            inChar = in.peek();
            p = in.pos();
        }
        in.back(p);
        return Result.ok(Token.sp(sb.toString()));
    };

    // 指定文字列トークン
    public static Parser<Token.TString> word(final String word) {
        return (in) -> {
            final Pos p = in.pos();
            boolean ok = true;
            StringBuilder sb = new StringBuilder();

            for (char ch : word.toCharArray()) {
                char inChar = in.peek();
                sb.append(inChar);
                if (inChar != ch) {
                    ok = false;
                    break;
                }
            }
            if (ok) {
                return Result.ok(Token.of(word));
            } else {
                final Pos ngPos = in.pos();
                in.back(p);
                return Result.ng(ParseError.syntax(ngPos, word + "<->" + sb));
            }
        };
    }

    // 複合パーサー
    public static <T extends Token> Parser<Token.TContainer> chain(String name, Parser<T> p1, Parser<T> p2) {
        return (in) -> {
            final Result<T, ParseError> r1 = p1.parse(in);
            return r1.then(ok1 -> {
                final Result<T, ParseError> r2 = p2.parse(in);
                return r2.then(ok2 -> {
                    Token.TContainer ret = Token.list(name).add(ok1).add(ok2);
                    return Result.ok(ret);
                });
            });
        };
    }

    // 分岐パーサー
    public static <T extends Token> Parser<T> or(Parser<T> p1, Parser<T> p2) {
        return (in) -> {
            final Pos pos = in.pos();
            final Result<T, ParseError> r1 = p1.parse(in);
            if (r1.isOk()) {
                return r1;
            }
            in.back(pos);
            final Result<T, ParseError> r2 = p2.parse(in);
            if (r2.isOk()) {
                return r2;
            }
            return r2.mapErr(e -> e.chain(r1.ng()));
        };
    }


    // スペース判定
    private static boolean isSp(char ch) {
        return ch == ' ' || ch == '\t' || ch == '\r' || ch == '\n';
    }
}
