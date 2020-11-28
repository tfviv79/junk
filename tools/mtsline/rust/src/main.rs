use std::collections::BTreeMap;

use anyhow;
use chrono::{Duration, Local};
use lazy_static::lazy_static;
use regex;

mod cli {
    use clap::{App, Arg};

    #[derive(Debug)]
    pub struct Opts {
        pub filenames: Vec<String>,
        pub ago_days: i64,
    }

    pub fn parse_cli_opt() -> Opts {
        let app = App::new(clap::crate_name!())
            .version("0.1.0")
            .author("tfviv79")
            .about("time line counter")
            .arg(
                Arg::with_name("ago_days")
                    .short("n")
                    .long("ago_days")
                    .help("days ago")
                    .takes_value(true)
                    .default_value("5"),
            )
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
            ago_days: matches
                .value_of("ago_days")
                .map(|x| x.parse::<i64>().unwrap())
                .unwrap(),
        }
    }
}

#[derive(Debug)]
struct Ctx {
    ago_days: i64,
}

#[derive(Debug)]
struct Stat {
    date: String,
    category: String,
    kind: Vec<String>,
    time: f32,
    note: String,
}

const INDENT_UNIT: &str = "    ";

impl Stat {
    fn keys(&self) -> Vec<String> {
        // maximum keys level => 5.
        // 1: year-month
        // 2: day
        // 3: @category
        // 4: kind 1
        // 5: kind 2
        let mut ret = Vec::new();
        ret.push(self.date[..7].to_string());
        ret.push(self.date[8..10].to_string());
        if self.category != "" {
            ret.push(self.category.to_string());
            if self.kind.len() != 0 {
                ret.append(&mut self.kind.clone());
            }
        }
        ret
    }
}

struct StatMap {
    map: BTreeMap<String, StatMap>,
    stats: Vec<Stat>,
    sum: Option<f32>,
}
impl StatMap {
    fn new() -> StatMap {
        StatMap {
            map: BTreeMap::new(),
            stats: Vec::new(),
            sum: None,
        }
    }

    fn notes(&self) -> String {
        let mut notes = self
            .stats
            .iter()
            .map(|x| x.note.clone())
            .collect::<Vec<String>>();
        notes.sort();
        notes.dedup();
        notes.join(",")
    }

    fn sum(&self) -> f32 {
        if self.sum.is_some() {
            return self.sum.unwrap();
        }
        0.0f32
    }

    fn calc_sums(&mut self) -> f32 {
        if self.sum.is_some() {
            return self.sum.unwrap();
        }

        let mut sum = 0.0f32;
        sum += self.map.values_mut().map(|x| x.calc_sums()).sum::<f32>();
        sum += self.stats.iter().map(|x| x.time).sum::<f32>();
        self.sum = Some(sum);
        sum
    }

    fn update(&mut self, stat: Stat) {
        let keys = stat.keys();
        self.update_keys(stat, keys.iter());
    }

    fn update_keys<'a>(&mut self, stat: Stat, mut keys: impl Iterator<Item = &'a String>) {
        let key = keys.next();
        if key.is_none() {
            self.stats.push(stat);
            return;
        }
        let key = key.unwrap();
        let entry = self.map.entry(key.to_string());
        entry
            .or_insert_with(|| StatMap::new())
            .update_keys(stat, keys);
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
    // key level maximum is 5 (because kind's max is 2)
    let cate_kind: Vec<&str> = m[2].splitn(3, ":").collect();
    if cate_kind.len() < 2 {
        return None;
    }
    let category = cate_kind[0].to_string();
    let kind = cate_kind[1..].iter().map(|x| x.to_string()).collect();
    let time: f32 = (&m[1]).parse().unwrap();
    let note = m[3].trim().to_string();

    Some(Stat {
        date: yymmdd.to_string(),
        category,
        kind,
        time,
        note,
    })
}

fn parse_file(_ctx: &Ctx, filename: String, map: &mut StatMap) -> anyhow::Result<()> {
    for line in std::fs::read_to_string(&filename)?.split("\n") {
        let stat = parse_line(line);
        stat.map(|x| map.update(x));
    }

    Ok(())
}
fn print_out_detail(ctx: &Ctx, map: &StatMap) {
    let today = Local::today();
    let ago_days = today - Duration::days(ctx.ago_days);
    let max_date: &str = &ago_days.format("%Y-%m-%d").to_string();
    print_out_detail_key(ctx, "", 1, map, max_date);
}
fn print_out_detail_key<'a>(
    ctx: &Ctx,
    prefix: &'a str,
    level: i32,
    map: &StatMap,
    max_date: &'a str,
) {
    if level < 3 {
        for (key, value) in &map.map {
            let title = format!("{}{}{}", prefix, if level == 2 { "-" } else { "" }, key);
            print_out_detail_key(ctx, &title, level + 1, value, max_date);
        }
        return;
    }
    if prefix < max_date {
        return;
    }
    print!("{} {:6.2}(", prefix, map.sum());
    for (index, (key, value)) in map.map.iter().enumerate() {
        let sep = if index == 0 { "" } else { ", " };
        print!("{}{}[{:6.2}]", sep, key, value.sum());
    }
    println!(")");
    for (key, value) in &map.map {
        print_out_detail_data(ctx, INDENT_UNIT, &key, value);
    }
}

fn print_out_detail_data<'a>(ctx: &Ctx, indent: &'a str, prefix: &'a str, map: &StatMap) {
    println!("{}[{:6.2}] {}", indent, map.sum(), prefix);
    for (key, value) in &map.map {
        if value.map.is_empty() {
            println!(
                "{}{}[{:6.2}] {}: {}",
                indent,
                INDENT_UNIT,
                value.sum(),
                key,
                value.notes()
            );
        } else {
            let new_indent = format!("{}{}", indent, INDENT_UNIT);
            let new_prefix = format!("{}", key);
            print_out_detail_data(ctx, &new_indent, &new_prefix, value);
        }
    }
}

fn print_out_summary(ctx: &Ctx, map: &StatMap) {
    print_out_summary_key(ctx, 1, map);
}

fn print_out_summary_key<'a>(ctx: &Ctx, level: i32, map: &StatMap) {
    if level > 2 {
        return;
    }

    const INDENT_BY: usize = 10;

    for (idx, (key, value)) in map.map.iter().enumerate() {
        if level == 1 {
            let title = key;
            print!("{} {:6.2}", title, value.sum());
            print_out_summary_key(ctx, level + 1, value);
        } else if level == 2 {
            let title = key;
            if idx % INDENT_BY == 0 {
                println!("")
            }
            let indent = if idx % INDENT_BY == 0 {
                INDENT_UNIT
            } else {
                ""
            };
            print!("{}{}[{:5.2}]  ", indent, title, value.sum());
        }
    }
    if level == 2 {
        println!("");
    }
}

fn main() -> anyhow::Result<()> {
    let opts: cli::Opts = cli::parse_cli_opt();
    let mut map = StatMap::new();
    let ctx = Ctx {
        ago_days: opts.ago_days,
    };

    for filename in opts.filenames {
        parse_file(&ctx, filename, &mut map)?;
    }
    map.calc_sums();

    print_out_detail(&ctx, &map);
    print_out_summary(&ctx, &map);

    Ok(())
}
