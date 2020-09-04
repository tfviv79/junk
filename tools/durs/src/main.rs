use std::env;
use std::fs;
use std::io;
use std::path::{Path, PathBuf};
use std::process;

use anyhow;
use anyhow::anyhow as anyhowerr;
use getopts::Options;
use signal_hook;

fn print_usage(program: &str, opts: &Options) {
    let brief = format!("Usage: {} [options] <path> [<path> ...]", program);
    print!("{}", opts.usage(&brief));
}

struct OutputData {
    name: String,
    bytes: u64,
}
impl OutputData {
    pub fn bytes_comma(&self) -> String {
        let unit = 1024; // kIB
        let fmt = format!("{}", self.bytes / unit);
        let mut buf = String::new();
        let mut digit = 0;
        for c in fmt.chars().rev() {
            digit += 1;
            if digit != 1 && digit % 3 == 1 {
                buf.push(',');
            }
            buf.push(c);
        }
        return buf.chars().rev().collect::<String>();
    }
}

struct Entry {
    name: String,
    bytes: u64,
    last: bool, /* true: file, false: directory */
                // entries: Option<Vec<Entry>>,
}

enum OutputOrder {
    UsageSize,
    Name,
}
impl OutputOrder {
    fn new(k: String) -> Self {
        if k == "name" {
            OutputOrder::Name
        } else {
            OutputOrder::UsageSize
        }
    }
}

struct Config {
    paths: Vec<PathBuf>,
    depth: u16,
    output_order: OutputOrder,
}

fn init_opts() -> Options {
    let mut options = Options::new();

    options.optflagopt("o", "order", "output order (size, name)", "ORDER");
    options
}

struct CommandLineOptions {
    paths: Vec<PathBuf>,
    output_order: String,
}
impl CommandLineOptions {
    fn new() -> anyhow::Result<Self> {
        let args: Vec<String> = env::args().collect();
        let program = args[0].clone();

        let options = init_opts();
        let opt = match options.parse(&args[1..]) {
            Ok(m) => m,
            Err(err) => {
                print_usage(&program, &options);
                return Err(anyhowerr!("###ERR###{}", err.to_string()));
            }
        };

        let mut paths: Vec<PathBuf> = Vec::new();
        if opt.free.len() == 0 {
            if let Some(dir_list) = try_read_dir(Path::new("./")) {
                for sub_entry in dir_list {
                    if let Some(sub_path) = path_from_entry(sub_entry) {
                        paths.push(sub_path);
                    }
                }
            }
        } else {
            for opt in &opt.free {
                let mut path = PathBuf::new();
                path.push(&opt);
                paths.push(path);
            }
        }

        for p in &paths {
            if !p.exists() {
                return Err(anyhowerr!(format!("path {:?} doesn't exist", p)));
            }
        }

        let output_order = opt.opt_str("order").unwrap_or("size".to_string());

        Ok(CommandLineOptions {
            paths,
            output_order,
        })
    }
}

fn print_io_error(path: &Path, err: io::Error) {
    eprintln!("Couldn't read {:?} ({:?})", path, err.kind());
}

fn try_is_symlink(path: &Path) -> bool {
    let metadata = path.symlink_metadata();
    metadata.is_ok() && metadata.unwrap().file_type().is_symlink()
}

fn try_read_dir(path: &Path) -> Option<fs::ReadDir> {
    if try_is_symlink(path) {
        return None;
    }

    match path.read_dir() {
        Ok(dir_list) => Some(dir_list),
        Err(err) => {
            print_io_error(path, err);
            None
        }
    }
}

fn path_from_entry(entry: Result<fs::DirEntry, io::Error>) -> Option<std::path::PathBuf> {
    match entry {
        Ok(entry) => Some(entry.path()),
        Err(err) => {
            eprintln!("Couldn't read entry ({:?})", err.kind());
            None
        }
    }
}

fn get_bytes(path: &Path) -> u64 {
    match fs::metadata(path) {
        Ok(metadata) => metadata.len(),
        Err(err) => {
            print_io_error(path, err);
            0
        }
    }
}

fn file_name_from_path(path: &Path) -> String {
    path.file_name()
        .map(|name| name.to_str().unwrap_or("./"))
        .unwrap_or("./")
        .to_string()
}

fn build_entry(path: &Path, cfg: &Config, depth: u16) -> Option<Entry> {
    let new_depth = if depth > 0 { depth - 1 } else { 0 };
    let can_depth = depth >= 1;

    if !can_depth {
        return None;
    }

    let name = file_name_from_path(path);
    if path.is_dir() {
        if let Some(dir_list) = try_read_dir(path) {
            let mut total: u64 = 0;
            let mut vec: Vec<Entry> = Vec::new();

            for sub_entry in dir_list {
                if let Some(sub_path) = path_from_entry(sub_entry) {
                    if let Some(new_entry) = build_entry(&sub_path, cfg, new_depth) {
                        total += new_entry.bytes;
                        vec.push(new_entry);
                    }
                }
            }

            return Some(Entry {
                name,
                bytes: total,
                last: false,
                // entries: Some(vec),
            });
        } else {
            return None;
        }
    } else {
        let bytes = get_bytes(path);
        return Some(Entry {
            name,
            bytes,
            last: true,
            // entries: None,
        });
    }
}

fn build_outputdata(entries: Vec<Entry>, _cfg: &Config) -> Vec<OutputData> {
    let mut ret = Vec::new();
    let mut total_bytes = 0;
    let mut file_only_bytes = 0;

    for entry in entries {
        total_bytes += entry.bytes;
        if entry.last {
            file_only_bytes += entry.bytes;
        } else {
            ret.push(OutputData {
                name: entry.name,
                bytes: entry.bytes,
            });
        }
    }

    if file_only_bytes != 0 {
        ret.push(OutputData {
            name: "[file]".to_string(),
            bytes: total_bytes,
        });
    }

    ret.push(OutputData {
        name: "[total]".to_string(),
        bytes: total_bytes,
    });
    return ret;
}

fn print_entries(entries: Vec<Entry>, cfg: &Config) {
    let mut data = build_outputdata(entries, cfg);
    match cfg.output_order {
        OutputOrder::Name => data.sort_unstable_by(|a, b| b.name.cmp(&a.name).reverse()),
        OutputOrder::UsageSize => data.sort_unstable_by(|a, b| b.bytes.cmp(&a.bytes).reverse()),
    }

    for one in data {
        println!("{:>20} kiB {}", one.bytes_comma(), one.name);
    }
}

fn run(cfg: Config) {
    let mut entries: Vec<Entry> = Vec::new();
    let paths = cfg.paths.clone();

    for path in paths {
        if let Some(e) = build_entry(path.as_path(), &cfg, cfg.depth) {
            entries.push(e);
        }
    }
    print_entries(entries, &cfg);
}

fn main() -> anyhow::Result<()> {
    let _signal = unsafe {
        //
        signal_hook::register(signal_hook::SIGTERM, || process::exit(0))
    };

    let arg_opt = CommandLineOptions::new()?;
    let cfg = Config {
        paths: arg_opt.paths,
        depth: u16::MAX,
        output_order: OutputOrder::new(arg_opt.output_order),
    };

    run(cfg);

    Ok(())
}
