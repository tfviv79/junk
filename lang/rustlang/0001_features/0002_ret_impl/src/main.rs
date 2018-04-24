// ref:  http://github.com/rust-lang/rfcs/blob/master/text/1522-conservative-impl-trait.md
// ref:  http://qiita.com/takayahilton/items/ebc19d72a8004fb8e233
fn foo(n:u32) -> impl Iterator<Item=u32> {
    (0..n).map(|x| x*100)
}

fn main() {
    for x in foo(3) {
        println!("Hello, world! {}", x);
    }
}
