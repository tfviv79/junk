package llparser.io;

public class Pos {
    private int line;
    private int row;
    private long seek;
    private boolean hasLf = false;

    public Pos() {
        line = 1;
        row = 0;
        seek = 0L;
    }

    public long seek() {
        return seek;
    }
    public int line() {
        return line;
    }
    public int row() {
        return row;
    }

    public long seekInc() {
        row++;
        return seek++;
    }

    public char peek(final char ch) {
        if (hasLf) {
            line++;
            row = 0;
            hasLf = false;
        }
        seekInc();
        if (ch == '\n')  {
            hasLf = true;
        }
        return ch;
    }
}
