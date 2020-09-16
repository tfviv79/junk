use std::collections::BTreeMap;

use anyhow;
use lazy_static::lazy_static;
use regex;

mod cli {
    use clap::{App, Arg};

    #[derive(Debug)]
    pub struct Opts {
        pub filenames: Vec<String>,
    }

    pub fn parse_cli_opt() -> Opts {
        let app = App::new(clap::crate_name!())
            .version("0.1.0")
            .author("tfviv79")
            .about("time line counter")
            .arg(
                Arg::with_name("filenames")
                    .help("target filenames")
                    .multiple(true)
                    .required(true),
            );

        let matches = app.get_matches();
        Opts {
            filenames: matches
                .values_of("filenames")
                .unwrap()
                .map(|x| x.to_string())
                .collect(),
        }
    }
}

#[derive(Debug)]
struct Stat {
    date: String,
    category: String,
    kind: String,
    time: f32,
}

impl Stat {
    fn key(&self, lvl: i32) -> String {
        let key = format!("{}", self.date);
        let sep = "-";

        if lvl == 1 || (self.category == "" && self.kind == "") {
            return key;
        }

        let key = format!("{}{}{}", key, sep, self.category);
        if lvl == 2 || self.kind == "" {
            return key;
        }
        format!("{}{}{}", key, sep, self.kind)
    }

    fn update(&mut self, time: f32) {
        self.time += time
    }
}

struct StatMap {
    map: BTreeMap<String, Stat>,
}
impl StatMap {
    fn new() -> StatMap {
        StatMap {
            map: BTreeMap::new(),
        }
    }

    fn update(&mut self, stat: Stat) {
        let key = stat.key(0);
        let entry = self.map.entry(key);
        entry.and_modify(|e| e.update(stat.time)).or_insert(stat);
    }
}

fn parse_line(line: &str) -> Option<Stat> {
    use regex::Regex;
    lazy_static! {
        static ref PTN: Regex = Regex::new(r"^\s*(\[[0-9]+.*)").unwrap();
        static ref PTNV: Regex = Regex::new(r"^s*\[([0-9-]+)").unwrap();
    }
    if !PTN.is_match(line) {
        return None;
    }

    let m = PTN.captures(line)?;
    let s = &m[1];
    let m: Vec<String> = s.split("\t").map(|x| x.to_string()).collect();
    let mdate = PTNV.captures(&m[0])?;
    let yymmdd = &mdate[1];
    let cate_kind: Vec<&str> = m[2].split(":").collect();
    if cate_kind.len() < 2 {
        return None;
    }
    let category = cate_kind[0].to_string();
    let kind = cate_kind[1].to_string();
    let time: f32 = (&m[1]).parse().unwrap();

    Some(Stat {
        date: yymmdd.to_string(),
        category,
        kind,
        time,
    })
}

fn parse_file(filename: String, map: &mut StatMap) -> anyhow::Result<()> {
    for line in std::fs::read_to_string(&filename)?.split("\n") {
        let stat = parse_line(line);
        stat.map(|x| map.update(x));
    }

    Ok(())
}

fn print_out(map: &StatMap) {
    for (key, value) in &map.map {
        println!("{:25} {:6.2}", key, value.time);
    }
}

fn calc_stat(map: &StatMap) -> StatMap {
    let mut out_data = StatMap::new();
    for (key, value) in &map.map {
        let new_key = &key[0..7];
        out_data.update(Stat {
            date: new_key.to_string(),
            category: value.category.clone(),
            kind: "".to_string(),
            time: value.time,
        });
    }
    out_data
}

fn main() -> anyhow::Result<()> {
    let opts: cli::Opts = cli::parse_cli_opt();
    let mut map = StatMap::new();

    for filename in opts.filenames {
        parse_file(filename, &mut map)?;
    }
    let out_data = calc_stat(&map);

    print_out(&out_data);

    Ok(())
}
