package llparser.parse;

import llparser.io.Pos;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ParseError {
    public final Error err;
    public final Pos pos;
    public final String msg;
    public final ParseError other;

    public static enum Error {
        Syntax, EndOfInput,
    }

    public ParseError chain(ParseError other) {
        return new ParseError(err, pos, msg, other);
    }

    public static ParseError syntax(Pos pos, String msg) {
        return new ParseError(Error.Syntax, pos, msg, null);
    }

    public static ParseError end(Pos pos) {
        return new ParseError(Error.EndOfInput, pos, "", null);
    }

}

