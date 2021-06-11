package llparser.parse;

import llparser.io.Input;
import llparser.io.Pos;
import llparser.utils.Result;

public final class Parsers {
    private static void debug(String fmt, Object ... args) {
        System.out.println(String.format(fmt, args));
    }
    // ネームドトークンに
    public static Parser named(final String name, final Parser p) {
        return (final Input in) -> {
            return p.parse(in).map(x -> x.name(name));
        };
    }

    // スペーストークン
    public static Parser sp = (final Input in) -> {
        Pos p = in.pos();
        final StringBuilder sb = new StringBuilder();
        char inChar = in.peek();
        while(isSp(inChar)) {
            sb.append(inChar);
            p = in.pos();
            inChar = in.peek();
        }
        in.back(p);
        debug("match spaces [%s]:[%c]", sb.toString(), inChar);
        return Result.ok(Token.of(sb.toString()));
    };
    // ID token
    public static Parser id = (final Input in) -> {
        Pos p = in.pos();
        final StringBuilder sb = new StringBuilder();
        char inChar = in.peek();
        while(isId(inChar, sb.length() == 0)) {
            sb.append(inChar);
            p = in.pos();
            inChar = in.peek();
        }
        in.back(p);
        debug("match id [%s]:[%c]", sb.toString(), inChar);
        return Result.ok(Token.of(sb.toString()));
    };

    // 指定文字列トークン
    public static Parser word(final String word) {
        return (in) -> {
            final Pos p = in.pos();
            boolean ok = true;
            final StringBuilder sb = new StringBuilder();

            for (final char ch : word.toCharArray()) {
                final char inChar = in.peek();
                sb.append(inChar);
                if (inChar != ch) {
                    ok = false;
                    debug("unmatch words %s %s", word, inChar);
                    break;
                }
            }
            if (ok) {
                debug("match words %s ", word);
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
    public static Parser chain(final Object obj1, final Object ... objs) {
        return (in) -> {
            final Pos pos = in.pos();
            final Result<Token, ParseError> r1 = w(obj1).parse(in);
            debug("chain is %s", r1.isOk());
            return r1.then(ok1 -> {
                debug("chain 1st step %s", ok1);
                Token ret = Token.list(ok1);
                for (final Object obj : objs) {
                    final Result<Token, ParseError> r2 = w(obj).parse(in);
                    if (r2.isOk()) {
                        ret = ret.add(r2.ok());
                        debug("chain step %s", ret);
                    } else {
                        debug("chain bad %s %s", ret, in.pos());
                        in.back(pos);
                        return r2.map(x->null);
                    }

                }
                return Result.ok(ret);
            });
        };
    }

    // 分岐パーサー
    public static Parser or(final Parser p1, final Parser p2) {
        return (in) -> {
            final Pos pos = in.pos();
            final Result<Token, ParseError> r1 = p1.parse(in);
            if (r1.isOk()) {
                return r1;
            }
            in.back(pos);
            final Result<Token, ParseError> r2 = p2.parse(in);
            if (r2.isOk()) {
                return r2;
            }
            return r2.mapErr(e -> e.chain(r1.ng()));
        };
    }

    // ０以上
    public static Parser many(final Parser p) {
        return (in) -> {
            Pos pos = in.pos();
            Token l = Token.list();
            Result<Token, ParseError> r1 = p.parse(in);
            if (r1.isErr()) {
                in.back(pos);
                return Result.ok(l);
            }
            l = l.add(r1.ok());

            while(r1.isOk()) {
                pos = in.pos();
                r1 = p.parse(in);
                if (r1.isOk()) {
                    l = l.add(r1.ok());
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

    // 簡便なビルダー
    private static Parser w(final Object obj) {
        if (obj instanceof String) {
            return word(obj.toString());
        } else if (obj instanceof Parser) {
            @SuppressWarnings("unchecked")
            final Parser p = (Parser)obj;
            return p;
        } else {
            throw new IllegalArgumentException("obj=" + obj);
        }
    }
}
