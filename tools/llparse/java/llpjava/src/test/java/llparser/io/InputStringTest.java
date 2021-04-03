package llparser.io;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import static org.junit.jupiter.api.Assertions.assertEquals;

public class InputStringTest {
    @Test
    public void testPeek() {
        Input in = new InputString("123456789");
        assertEquals('1', in.peek());
        checkPos(1, 1, 1, in.pos());
        assertEquals('2', in.peek());
        checkPos(2, 1, 2, in.pos());
        assertEquals('3', in.peek());
        checkPos(3, 1, 3, in.pos());
    }

    @Test
    public void testPeekLf() {
        Input in = new InputString("12\n34");
        assertEquals('1', in.peek());
        checkPos(1, 1, 1, in.pos());
        assertEquals('2', in.peek());
        checkPos(2, 1, 2, in.pos());
        assertEquals('\n', in.peek());
        checkPos(3, 1, 3, in.pos());
        assertEquals('3', in.peek());
        checkPos(4, 2, 1, in.pos());
        assertEquals('4', in.peek());
        checkPos(5, 2, 2, in.pos());
    }


    public void checkPos(long seek, int line, int row, Pos pos) {
        assertEquals(seek, pos.seek(), "seek");
        assertEquals(line, pos.line(), "line");
        assertEquals(row, pos.row(), "row");
    }
}
