#[derive(Debug)]
enum List {
    Cons(i32, Box<List>),
    Nil,
}


use List::Cons;
use List::Nil;

fn main() {
    let b = Box::new(5);
    println!("b = {}", b);

    let b = Cons(1,
        Box::new(Cons(2,
            Box::new(Cons(3,
                Box::new(Nil)
            ))
        ))
    );
    println!("b = {:?}", b);
}
