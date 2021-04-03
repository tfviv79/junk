package llparser.parse;

import org.junit.jupiter.api.Test;


import static org.junit.jupiter.api.Assertions.assertEquals;

import llparser.io.Input;
import llparser.io.InputString;
import llparser.utils.Result;

public class ParsersTest {
    @Test
    public void testStr() {
        Input in = new InputString("123456789");
        Parser<Token.TString> p = Parsers.word("123");
        Result<Token.TString, ParseError> val = p.parse(in);
        assertEquals(true, val.isOk());
        val.map(v -> {
            assertEquals("123", v.v);
            return v;
        });
    }

    @Test
    public void testStrNg() {
        Input in = new InputString("456789");
        Parser<Token.TString> p = Parsers.word("123");
        Result<Token.TString, ParseError> val = p.parse(in);
        assertEquals(false, val.isOk());
        val.mapErr(err -> {
            assertEquals(1, err.pos.seek());
            assertEquals("123<->4", err.msg);
            return err;
        });
    }
}
