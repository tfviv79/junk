package llparser.io;

public interface Input {
    Pos pos();
    char peek();
    void back(Pos p);
}
