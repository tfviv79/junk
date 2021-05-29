package llparser.parse;

import org.junit.jupiter.api.Test;


import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Optional;

import llparser.io.Input;
import llparser.io.InputString;
import llparser.utils.Result;

public class ParsersTest2 {
    @Test
    public void testSyntax() {
        Parser<Token> p = 
            Parsers.many(
                    Parsers.chain(
                        Parsers.word("import"),
                        Parsers.sp,
                        Parsers.word("{"),
                        Parsers.id,
                        Parsers.many(
                            Parsers.chain(
                                Parsers.sp, Parsers.word(","),
                                Parsers.sp, Parsers.id
                                )),
                        Parsers.word("}")
                        )
                    )
            ;

        Input in = new InputString("import {a, b, c} from 'd/e'\nimport * as f from './g/h'\nfunction main(i, j) {console.log(i, j)}");
        Result<Token, ParseError> val = p.parse(in);
        assertEquals(true, val.isOk());
        val.map(v -> {
            assertEquals("123", v.v);
            return v;
        });
    }
}
