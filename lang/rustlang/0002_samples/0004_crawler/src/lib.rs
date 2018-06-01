extern crate reqwest;
extern crate rusqlite;
extern crate scraper;

mod data;
pub mod htmlparser;
pub use data::WebInfo;

use std::collections::hash_map::DefaultHasher;
use std::fs::DirBuilder;
use std::fs::File;
use std::hash::{Hash, Hasher};
use std::io::Read;
use std::io::Write;
use std::path::Path;
use std::time::SystemTime;
use std::time::Duration;

/// crawler module
fn now() -> SystemTime {
    SystemTime::now()
}

pub struct Config {
    pub prev_access_time: SystemTime,
    pub interval: Duration,
    pub worker_num: u32,
}

impl Config {
    pub fn new(ts_ms: u64, worker_num: u32) -> Config {
        Config {
            prev_access_time: SystemTime::UNIX_EPOCH,
            interval: Duration::from_millis(ts_ms),
            worker_num: worker_num,
        }
    }

    pub fn access_wait(&mut self) {
        let remain = now().duration_since(self.prev_access_time).unwrap();
        if remain < self.interval {
            std::thread::sleep(self.interval - remain);
            self.prev_access_time = now();
        }
    }

    pub fn interval(&self) -> std::time::Duration {
        self.interval
    }
}

pub struct Crawler<'a> {
    pub base_url: String,
    pub prev_fetch_time_ms: u64,
    pub config: &'a Config,
}

impl <'a> Crawler<'a> {
    pub fn new(base_url: String, config: &'a Config) -> Crawler {
        Crawler {
            base_url: base_url,
            config: config,
            prev_fetch_time_ms: 0,
        }
    }

    pub fn fetch(&self) -> Result<String, std::io::Error> {
        let response = from(reqwest::get(self.base_url.as_str()));
        let mut body = response?;
        let text = from(body.text())?;

        Ok(text)
    }
}

fn from<T>(v: Result<T, reqwest::Error>) -> std::io::Result<T> {
    match v {
        Ok(e) => Ok(e),
        Err(e) => Err(std::io::Error::new(std::io::ErrorKind::Other, e)),
    }
}

fn calc_hash(val: &String) -> u64 {
    let mut hasher = DefaultHasher::new();
    val.hash(&mut hasher);
    hasher.finish()
}


fn fetch_from_web(url: &String, config: &mut Config) -> std::io::Result<String> {
    config.access_wait();
    let crawler = Crawler::new(url.to_string(), config);
    crawler.fetch()
}

pub fn exec_fetch_data(url: &String, config: &mut Config) -> std::io::Result<String> {
    let hash_num = format!("{code:016X}", code = calc_hash(url));
    let fname_str = format!("data/{}/{}.html", hash_num[0..4].to_string(), hash_num);
    let fname = Path::new(&fname_str);

    let ret = if !fname.exists() {
        fname.parent().map(|p| {
            if !p.exists() {
                DirBuilder::new().recursive(true).create(p)
            } else {
                Ok(())
            }
        });
        let v = fetch_from_web(url, config)?;
        let mut fp = File::create(fname)?;
        fp.write_all(v.as_bytes())?;
        v
    } else {
        let metadata = std::fs::metadata(fname)?;
        if metadata.modified()? + config.interval()*60 <= now() {
            let v = fetch_from_web(url, config)?;
            let mut fp = File::create(fname)?;
            fp.write_all(v.as_bytes())?;
            v
        } else {
            let mut v = String::new();
            let mut fp = File::open(fname)?;
            fp.read_to_string(&mut v)?;
            v
        }
    };
    Ok(ret)
}
