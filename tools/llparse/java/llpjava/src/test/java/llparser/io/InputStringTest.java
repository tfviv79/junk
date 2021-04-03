package llparser.io;

import org.junit.jupiter.api.Test;


import static org.junit.jupiter.api.Assertions.assertEquals;

public class InputStringTest {
    @Test
    public void testPeek() {
        Input in = new InputString("123456789");
        checkInput('1', 1, 1, 1, in);
        checkInput('2', 2, 1, 2, in);
        checkInput('3', 3, 1, 3, in);
    }

    @Test
    public void testPeekLf() {
        Input in = new InputString("12\n34");
        checkInput('1', 1, 1, 1, in);
        checkInput('2', 2, 1, 2, in);
        checkInput('\n', 3, 1, 3, in);
        checkInput('3', 4, 2, 1, in);
        checkInput('4', 5, 2, 2, in);
    }


    private void checkInput(char ch, long seek, int line, int row, Input in) {
        char v = in.peek();
        Pos pos = in.pos();
        assertEquals(ch, v, "char");
        assertEquals(seek, pos.seek(), "seek");
        assertEquals(line, pos.line(), "line");
        assertEquals(row, pos.row(), "row");
    }
}
