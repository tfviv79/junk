struct A {
    i: i32
}

impl A {
    pub fn inc(&mut self) -> i32 {
        self.i = self.i + 1;
        return self.i
    }
}

fn main() {
    exec1();
    exec2();
}

fn exec1() {
    let mut a = A{i:0};
    println!("exec1 0={:?}", a.i);
    a.inc();
    println!("exec1 1={:?}", a.i);
}

fn exec2() {
    let mut a = A{i:0};
    println!("exec2 0={:?}", a.i);
	exec2_inner(&mut a);
    println!("exec2 1={:?}", a.i);
}

fn exec2_inner(a: &mut A) {
    a.inc();
}
