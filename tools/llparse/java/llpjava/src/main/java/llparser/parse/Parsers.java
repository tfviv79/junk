package llparser.parse;

import llparser.io.Input;
import llparser.io.Pos;
import llparser.utils.Result;

public final class Parsers {
    // スペーストークン
    public static Parser<Token> named(final String name, final Parser<Token> p) {
        return (final Input in) -> {
            return p.parse(in).map(x -> Token.of(name, x));
        };
    }

    // スペーストークン
    public static Parser<Token> sp = (final Input in) -> {
        Pos p = in.pos();
        final StringBuilder sb = new StringBuilder();
        char inChar = in.peek();
        while(isSp(inChar)) {
            sb.append(inChar);
            inChar = in.peek();
            p = in.pos();
        }
        in.back(p);
        return Result.ok(Token.of(sb.toString()));
    };
    // ID token
    public static Parser<Token> id = (final Input in) -> {
        Pos p = in.pos();
        final StringBuilder sb = new StringBuilder();
        char inChar = in.peek();
        while(isId(inChar, sb.length() == 0)) {
            sb.append(inChar);
            inChar = in.peek();
            p = in.pos();
        }
        in.back(p);
        return Result.ok(Token.of(sb.toString()));
    };

    // 指定文字列トークン
    public static Parser<Token> word(final String word) {
        return (in) -> {
            final Pos p = in.pos();
            boolean ok = true;
            final StringBuilder sb = new StringBuilder();

            for (final char ch : word.toCharArray()) {
                final char inChar = in.peek();
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
    @SafeVarargs
    public static <T extends Token> Parser<Token.ListToken> chain(final Parser<T> p1, final Parser<T> ... plist) {
        return (in) -> {
            final Pos pos = in.pos();
            final Result<T, ParseError> r1 = p1.parse(in);
            return r1.then(ok1 -> {
                final Token.ListToken ret = Token.list().add(ok1);
                for (final Parser<T> p2 : plist) {
                    final Result<T, ParseError> r2 = p2.parse(in);
                    if (r2.isOk()) {
                        ret.add(r2.ok());
                    } else {
                        in.back(pos);
                        return r2.map(x->null);
                    }

                }
                return Result.ok(ret);
            });
        };
    }

    // 分岐パーサー
    public static <T extends Token> Parser<T> or(final Parser<T> p1, final Parser<T> p2) {
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

    // ０以上
    public static <T extends Token> Parser<Token.ListToken> many(final Parser<T> p) {
        return (in) -> {
            Pos pos = in.pos();
            final Token.ListToken l = Token.list();
            Result<T, ParseError> r1 = p.parse(in);
            if (r1.isErr()) {
                in.back(pos);
                return Result.ok(l);
            }
            l.add(r1.ok());

            while(r1.isOk()) {
                pos = in.pos();
                r1 = p.parse(in);
                if (r1.isOk()) {
                    l.add(r1.ok());
                }
            }
            in.back(pos);
            return Result.ok(l);
        };
    }


    // スペース判定
    private static boolean isSp(final char ch) {
        return ch == ' ' || ch == '\t' || ch == '\r' || ch == '\n';
    }
    // ID文字判定
    private static boolean isId(final char ch, final boolean isFirst) {
        return (!isFirst && '0' <= ch && ch <= '9')
            || ('a' <= ch && ch <= 'z')
            || ('A' <= ch && ch <= 'Z')
            || ('_' == ch )
            ;
    }
}
