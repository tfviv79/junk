extern crate rand;

use rand::{Rng,ThreadRng};

pub struct Dice {
    min:i32,
    max:i32,
    randgen: ThreadRng,
}

impl Dice {
    fn new(max:i32) -> Self {
        return Dice{
            min:1,
            max:max,
            randgen:rand::thread_rng(),
        };
    }

    fn thw(&mut self) -> i32 {
        self.randgen.gen_range(self.min, self.max + 1)
    }
}

pub struct Dices {
    ps: Vec<Dice>,
}

impl <'a> Dices {
    fn new(num:i32, max:i32) -> Box<Self> {
        let mut ps: Vec<Dice> = Vec::new();
        for _n in 1..(num+1) {
            ps.push(Dice::new(max))
        }
        Box::new(Dices{ps: ps})
    }

    fn thw(&mut self) -> Vec<i32> {
        let mut val: Vec<i32> = Vec::new();
        for d in self.ps.iter_mut() {
            val.push(d.thw());
        }
        val
    }
}


fn main() {
    let mut d = Dices::new(5, 6);
    let mut sum = 0;
    let mut count = 0;
    for n in 1..101 {
        let r = d.thw();
        for c in r.iter() {
            count += 1;
            sum += c;
        }
        println!("{}th {:?}", n, r);
    }
    let avg: f32 = (sum as f32)/(count as f32);
    println!("{} {} {}", sum, count, avg);
}
