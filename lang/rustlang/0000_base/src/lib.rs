//! samples crate
//! ```
//! use base::*;
//! assert_eq!(version(), "0.0.1")
//! ```

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

#[derive(Debug)]
pub struct S1(i32);

impl S1 {
    pub fn new(n: i32) -> S1 {
        S1(n)
    }
}

#[derive(Debug)]
pub struct S2(pub i32);
