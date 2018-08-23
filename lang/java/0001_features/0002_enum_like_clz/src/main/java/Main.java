import java.util.Arrays;
import java.util.Collections;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.ArrayList;


import lombok.Data;

@Data

public class Main {
    public static void main(String[] args) {
        System.out.println("args=" + Arrays.toString(args));
        System.out.println("V1 name is " + B.V1.name());
        System.out.println("V2 name is " + B.V2.name());
        for (B v : B.values) {
            System.out.println("cls v name is " + v.name());
            System.out.println("      id   is " + v.id());
            System.out.println("      clz  is " + v.clz());
            System.out.println("      note is " + v.note());
        }

        System.out.println("B.V3 == B.V3  -> " + cmp(B.V3, B.V3));
        System.out.println("B.V1 != B.V2  -> " + cmp(B.V1, B.V2));
        // System.out.println("B.V1 != C.V1  -> " + cmp(B.V1, C.V1)); // compile error
    }


    // base class for enum like data.
    public static abstract class A<T extends A<T>> {
        @SuppressWarnings("unchecked")
        protected T c(String id, Class<?> clz) {
            this.id = id;
            this.clz = clz;
            return (T)this;
        }

        private Class<?> clz;
        public final Class<?> clz() { return clz;}

        private String id;
        public final String id() { return id;}

        private String name = null;
        public final String name() {
            // we can use fields name like enum.
            if (name != null) {
                return name;
            }
            synchronized (this) {
                if (name != null) {
                    return name;
                }

                try {
                    Class<?> clz = getClass();
                    for ( Field f : clz.getFields()) {
                        if(Modifier.isStatic(f.getModifiers())) {
                            if (f.get(null) == this) {
                                name = f.getName();
                            }
                        }
                    }

                    if (name == null) {
                        name = "undefined";
                    }
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
                return name;
            }
        }

        public static  <T extends A<T>> List<T> values(Class<T> clz) {
            try {
                List<T> ret = new ArrayList<>();
                for ( Field f : clz.getFields()) {
                    // only values is equals this class.
                    if (!f.getType().equals(clz)) {
                        continue;
                    }
                    if(Modifier.isStatic(f.getModifiers())) {
                        @SuppressWarnings("unchecked")
                        T val = (T)f.get(null);
                        if (val != null && val.id() != null) {
                            ret.add(val);
                        } else {
                            throw new RuntimeException(clz.getName() + "." + f.getName() + "'s value is null.");
                        }
                    }
                }
                return Collections.unmodifiableList(ret);
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    // enum like class
    public static final class B extends A<B> {
        // must call order B's method -> A's method.
        public static final B V1 = new B().note("HOGE").c("hoge", String.class);
        public static final B V2 = new B().note("POGE").c("poge", Integer.class);
        public static final B V3 = new B().c("goge", Integer.class).note("GOGE");

        // all values list
        public static final List<B> values = values(B.class);

        private String note;
        private B note(String n) { this.note = n; return this;}
        public String note() {return this.note;}
    }
    // enum like class2
    public static final class C extends A<C> {
        // must call order B's method -> A's method.
        public static final C V1 = new C().note("HOGE").c("hoge", String.class);
        public static final C V2 = new C().note("POGE").c("poge", Integer.class);
        public static final C V3 = new C().c("goge", Integer.class).note("GOGE");

        // all values list
        public static final List<C> values = values(C.class);

        private String note;
        private C note(String n) { this.note = n; return this;}
        public String note() {return this.note;}
    }

    public static <T extends A<T>> boolean cmp(T o1, T o2) {
        return o1.equals(o2);
    }
}
