extern crate crawler;
extern crate rusqlite;
extern crate scraper;

use crawler::htmlparser;
use crawler::Config;

fn main() -> std::io::Result<()> {
	let mut urls: Vec<String> = vec![];
	for arg in std::env::args().skip(1) {
		urls.push(arg.to_string());
	}
    exec(urls)
}

fn exec(urls: Vec<String>) -> std::io::Result<()> {
    let mut config = Config::new(60 * 1000, 2);
    for url in urls {
        println!("url={}", url);
        exec_each(&(url), &mut config)?;
    }
    Ok(())
}

fn exec_each(url: &String, config: &mut Config) -> std::io::Result<()> {
    let html = crawler::exec_fetch_data(url, config)?;
    let r = htmlparser::parse(url, html)?;
    r.save();

    Ok(())
}
