

use std::io::{Error, ErrorKind};
use std::env;

fn main() -> Result<(), Error> {
	let val = env::args().nth(1).unwrap_or("2".to_string()).parse::<i32>();
    println!("Hello, world! {:?}", val);
	match val {
		Ok(_) => Ok(()),
			// no output 
			// command return status = 0 
		Err(x)  => Err(Error::new(ErrorKind::Other, x))
		// $ ./target/debug/main_return_result 100.0
		// Hello, world! Err(ParseIntError { kind: InvalidDigit })
		// Error: Custom { kind: Other, error: ParseIntError { kind: InvalidDigit } }
			// command return status = 1 

	}
}
