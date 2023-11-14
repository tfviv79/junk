extern crate base;

fn main() {
    println!("Hello, world!");
    println!("hoge {:?}", base::S1::new(1));
    println!("hoge {:?}", base::S2(2));
}
