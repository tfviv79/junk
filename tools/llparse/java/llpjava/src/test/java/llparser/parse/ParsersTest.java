package llparser.parse;

import org.junit.jupiter.api.Test;


import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Optional;

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

    @Test
    public void testChain() {
        Input in = new InputString("123456789");
        Parser<Token.TString> p1 = Parsers.word("123");
        Parser<Token.TString> p2 = Parsers.word("456");
        Result<Token.TContainer, ParseError> val = Parsers.chain("ren", p1, p2).parse(in);
        assertEquals(true, val.isOk());
        val.map(v -> {
            assertEquals("ren", v.v);
            Optional<Token> r = v.get(0);
            assertEquals(true, r.isPresent());
            assertEquals("123", r.get().v);

            r = v.get(1);
            assertEquals(true, r.isPresent());
            assertEquals("456", r.get().v);
            return v;
        });
    }

    @Test
    public void testOr1() {
        Input in = new InputString("123456789");
        Parser<Token.TString> p1 = Parsers.word("123");
        Parser<Token.TString> p2 = Parsers.word("456");
        Result<Token.TString, ParseError> val = Parsers.or(p1, p2).parse(in);
        assertEquals(true, val.isOk());
        val.map(v -> {
            assertEquals("123", v.v);
            return v;
        });
    }

    @Test
    public void testOr2() {
        Input in = new InputString("456123789");
        Parser<Token.TString> p1 = Parsers.word("123");
        Parser<Token.TString> p2 = Parsers.word("456");
        Result<Token.TString, ParseError> val = Parsers.or(p1, p2).parse(in);
        assertEquals(true, val.isOk());
        val.map(v -> {
            assertEquals("456", v.v);
            return v;
        });
    }
}
