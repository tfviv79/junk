package llparser.parse;

import java.util.function.Function;

import llparser.utils.Result;

public final class Parsers {
    public static Parser<Token.TString> any = (in) -> {
        return Result.ok(Token.of(in.peek()));
    };

    public static Function<String, Parser<Token.TString>> word = (word) -> (in) -> {
        return Result.ok(Token.of(in.peek()));
    };
}
