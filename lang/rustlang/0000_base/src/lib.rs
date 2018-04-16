//! samples crate
//! ```
//! use base::*;
//! assert_eq!(version(), "0.0.1")
//! ```


macro_rules! ignore {
	($x:expr) => {
		{
			let _ = $x;
			()
		}
	}
}


/// samples function
/// ```
/// use base::*;
/// assert_eq!(version(), "0.0.1")
/// ```
pub fn version<'a>() -> &'a str {
    "0.0.1"
}

#[cfg(test)]
mod test {
    use super::*;

    #[test]
    fn test_version() {
        assert_eq!(version(), "0.0.1");
    }

    #[test]
    #[ignore]
    fn test_version_duplicate() {
        assert_eq!(version(), "0.0.1");
    }
}
