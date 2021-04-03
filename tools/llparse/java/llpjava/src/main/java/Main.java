import java.util.Arrays;

import lombok.Data;

@Data
public class Main {
    public static void main(String[] args) {
        System.out.println("args=" + Arrays.toString(args));
    }
}
