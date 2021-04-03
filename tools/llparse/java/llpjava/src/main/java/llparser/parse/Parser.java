package llparser.parse;

import llparser.io.Input;
import llparser.utils.Result;

public interface Parser<T extends Token> {
    Result<T, ParseError> parse(Input in);
}
