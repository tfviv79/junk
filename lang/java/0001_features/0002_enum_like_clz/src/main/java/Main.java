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
        System.out.println("V2 name is " + B.V2.name());
        System.out.println("V2 name is " + B.V2.name());
        for (B v : B.values) {
            System.out.println("cls v name is " + v.name());
            System.out.println("      id   is " + v.id());
            System.out.println("      clz  is " + v.clz());
            System.out.println("      note is " + v.note());
        }
    }


    // base class for enum like data.
    public static abstract class A {
        @SuppressWarnings("unchecked")
        protected <T extends A> T c(String id, Class<?> clz) {
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

        public static  <T extends A> List<T> values(Class<T> clz) {
            try {
                List<T> ret = new ArrayList<>();
                for ( Field f : clz.getFields()) {
                    // only values is equals this class.
                    if (!f.getType().equals(clz)) {
                        continue;
                    }
                    if(Modifier.isStatic(f.getModifiers())) {
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
    public static final class B extends A {
        // must call order B's method -> A's method.
        public static final B V1 = new B().note("HOGE").c("hoge", String.class);
        public static final B V2 = new B().note("POGE").c("poge", Integer.class);
        // all values list
        public static final List<B> values = values(B.class);

        private String note;
        private B note(String n) { this.note = n; return this;}
        public String note() {return this.note;}
    }
}
