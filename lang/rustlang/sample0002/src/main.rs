use std::ops::Add;

#[derive(Debug)]
struct S1 {
	val: i32,
}

impl Add for S1 {
	type Output = Self;

	fn add(self, rsh: Self) -> Self {
        S1 { val: self.val + rsh.val }
    }
}

#[derive(Debug)]
struct S3 {
	val: i32,
}

impl Add for S3 {
	type Output = Self;

	fn add(self, rsh: Self) -> Self {
        Self { val: self.val + rsh.val }
    }
}

impl <'a> Add<&'a S3> for S3 {
	type Output = S3;

	fn add(self, rsh: &S3) -> S3 {
        S3 { val: self.val + rsh.val }
    }
}


fn s001() {
    let s1 = S1{val: 3};
    let s2 = S1{val: 5};
    let s3 = s1 + s2;
    println!("s3 = {:?}", s3);
}

fn s002() {
    let mut v: Vec<S1> = Vec::new();
    for i in 1..10 {
        v.push(S1{val:i});
    }
    let mut s3 = S1{val: 0};
    // for vv in v.iter() { //compile error: iter -> borrow value, but add need move out
    for vv in v.into_iter() {
        s3 = s3 + vv;
    }
    println!("s3 = {:?}", s3);
    // println!("v = {:?}", v); // compile error: moveout
}

fn s003() {
    let mut v: Vec<S3> = Vec::new();
    for i in 1..10 {
        v.push(S3{val:i});
    }
    let mut s3 = S3{val: 0};
    for vv in v.iter() {
        s3 = s3 + vv;
    }
    println!("s3 = {:?}", s3);
    println!("v = {:?}", v);
}

fn main() {
    s001();
    s002();
    s003();
}
