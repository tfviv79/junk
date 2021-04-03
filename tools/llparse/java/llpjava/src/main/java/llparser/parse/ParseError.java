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

    public static enum Error {
        Syntax
    }

    public static ParseError syntax(Pos pos, String msg) {
        return new ParseError(Error.Syntax, pos, msg);
    }
}

