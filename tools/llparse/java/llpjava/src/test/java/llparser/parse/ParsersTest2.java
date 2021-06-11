package llparser.parse;

import org.junit.jupiter.api.Test;


import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;

import llparser.io.Input;
import llparser.io.InputString;
import llparser.utils.Result;

public class ParsersTest2 {
    @Test
    public void testSyntax() {
        Parser p =
            Parsers.many(
                    Parsers.chain(
                        "import"
                        , Parsers.sp.drop()
                        , Parsers.word("{").drop()
                        , Parsers.sp.drop()
                        , Parsers.chain(
                            Parsers.id
                            , Parsers.many(
                                Parsers.chain(
                                    Parsers.sp.drop()
                                    , Parsers.word(",").drop()
                                    , Parsers.sp.drop()
                                    , Parsers.id
                                    )
                              )
                            , Parsers.sp.drop()
                            , Parsers.word("}").drop()
                        ).flatR().name("names")
                    ).flat()
                ).name("imports")
            ;

        Input in = new InputString("import {a, b, c} from 'd/e'\nimport * as f from './g/h'\nfunction main(i, j) {console.log(i, j)}");
        Result<Token, ParseError> val = p.parse(in);
        assertEquals(true, val.isOk());
        val.map(v -> {
            assertEquals("imports", v.name);
            Token t = v.get(0).get();
            List<Token> tc = toList(t.tokens());
            assertEquals("import", tc.get(0).v);
            Token tci = tc.get(1);
            assertEquals("names", tci.name);
            List<Token> tcin = toList(tci.tokens());
            assertEquals("a", tcin.get(0).v);
            assertEquals("b", tcin.get(1).v);
            assertEquals("c", tcin.get(2).v);
            return v;
        });
    }


    private static List<Token> toList(Iterable<Token> t) {
        List<Token> ret = new ArrayList<>();
        if (t != null) {
            t.iterator().forEachRemaining(ret::add);
        }
        return ret;
    }
}
