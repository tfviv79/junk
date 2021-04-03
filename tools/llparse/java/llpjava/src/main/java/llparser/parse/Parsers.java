package llparser.parse;

import llparser.utils.Result;
import llparser.io.Pos;

public final class Parsers {
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
    };
}
