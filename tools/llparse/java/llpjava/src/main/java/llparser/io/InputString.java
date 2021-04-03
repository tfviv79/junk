package llparser.io;


public class InputString implements Input {
    private String v;
    private Pos pos;
    public InputString(String v) {
        this.pos = new Pos();
        this.v = v;
    }

    @Override
    public Pos pos() {
        return pos;
    }

    @Override
    public char peek() {
        return pos.peek(v.charAt((int)pos.seek()));
    }

}
