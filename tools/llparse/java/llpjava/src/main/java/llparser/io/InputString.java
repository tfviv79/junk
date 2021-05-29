package llparser.io;


public class InputString implements Input {
    private String v;
    private Pos pos;
    private int length;
    public InputString(String v) {
        this.pos = new Pos();
        this.v = v;
        this.length = v.length();
    }

    @Override
    public Pos pos() {
        return pos.clone();
    }

    @Override
    public char peek() {
        int nextPos = (int)pos.seek();
        if (nextPos < length) {
            return pos.peek(v.charAt(nextPos));
        } else {
            return (char)0;
        }
    }

    @Override
    public void back(Pos p) {
        this.pos = p;
    }
}
