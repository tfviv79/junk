use scraper::{ElementRef, Html, Selector};

use data::WebInfo;
use std::io;

pub fn parse(url: &String, body: String) -> io::Result<WebInfo> {
    let doc = Html::parse_document(&body);
    let selector = Selector::parse(".listArea .list .ListBoxwrap").unwrap();
    let sel_date = Selector::parse("dl.title dd time").unwrap();
    let sel_link = Selector::parse("a").unwrap();
    let title_tag = Selector::parse("dl.title dt").unwrap();
    for node in doc.select(&selector).last() {
        println!("node={0}", node.html());
        let n_date = node.select(&sel_date).next().unwrap();
        let n_title = node.select(&title_tag).next().unwrap();
        let n_link = node.select(&sel_link).next().unwrap();
        let n_link_href = n_link.value().attr("href").unwrap();
        return Ok(WebInfo::new(
            url.clone(),
            n_link_href.to_string(),
            to_text(n_title),
            to_text(n_date),
        ));
    }
    from("no links")
}

fn to_text(node: ElementRef) -> String {
    let val = node.text().collect::<Vec<_>>();
    let ret = val.join("");
    if node.value().name() == "a" {
        ret
    } else {
        ret.replace("\n", "")
    }
}

fn from<T>(v: &'static str) -> io::Result<T> {
    Err(io::Error::new(io::ErrorKind::Other, v))
}
