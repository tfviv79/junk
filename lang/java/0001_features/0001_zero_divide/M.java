public class M {
    public static void main(String[] argv) {
        System.out.println("1/0 = " + (1.0/0.0));
        // -> 1/0 = Infinity

        try {
            System.out.println("1/0 = " + (1/0));
        } catch (ArithmeticException ex) {
            // -> occurs exception
            System.out.println("1/0 = exception [" + ex.getMessage() + "]");
            // -> 1/0 = exception [/ by zero]
        }
    }
}
