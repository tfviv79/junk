#![feature(test)]

extern crate test;
extern crate base;

use base::*;
use test::Bencher;

#[bench]
fn bench_version(b: &mut Bencher) {
    b.iter(|| version());
}
