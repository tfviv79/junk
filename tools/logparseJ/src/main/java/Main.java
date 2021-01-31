import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;
import java.time.Duration;

import lombok.Data;

@Data
public class Main {
    private static DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
    public static void main(String[] args) throws IOException {

        for (String arg : args) {
            parseLogs(arg);
        }

    }

    private static void parseLogs(String path) throws IOException {
        final File pathFile = new File(path);
        if (pathFile.isFile()) {
            parseLogsFile(pathFile);
            return;
        }
        for (File p : pathFile.listFiles())  {
            if (p.isDirectory()) {
                continue;
            }

            parseLogsFile(p);
        }
    }

    private static void parseLogsFile(File path) throws IOException {
        System.err.println("parse file " + path);
        RecordMap recordMap = new RecordMap();
        try (
                InputStream is = new FileInputStream(path);
                BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
            String line = null;
            long lineno = 0;
            while((line = br.readLine()) != null) {
                lineno++;
                parseLogLine(recordMap, lineno, line);
            }
        }
        for (List<String> record : recordMap.out()) {
            System.out.printf("%s\t%s\n", record.get(0), record.get(1));
        }
    }

    private static void parseLogLine(RecordMap m, long lineno, String line) {
        final String date = line.substring(1, 20);
        Record r = new Record();
        r.setSdate(date);
        r.setThreadName("1"); // FIXME fixed
        r.setKey("hoge");
        m.add(r);
    }

    private static class RecordMap {
        // thread -> keyword -> record
        private Map<String, Map<String, Record>> records;

        private List<Record> finishedRecords;

        public RecordMap() {
            records = new HashMap<>();
            finishedRecords = new ArrayList<>();
        }

        public void add(Record record) {
            final String threadName = record.getThreadName();
            Map<String, Record> r;
            if (records.containsKey(threadName)) {
                r = records.get(threadName);
            } else {
                r = new HashMap<String, Record>();
                records.put(threadName, r);
            }

            final String key = record.getKey();
            if (r.containsKey(key)) {
                Record rec = r.get(key);
                rec.setEdate(record.getSdate());
                finishedRecords.add(rec);
                r.remove(key);
                if (r.size() == 0) {
                    records.remove(threadName);
                }
            } else {
                r.put(key, record);
            }
        }

        @Override
        public String toString() {
            return finishedRecords.toString();
        }
        public List<List<String>> out() {
            List<List<String>> ret = new ArrayList<>();
            for (Record rec : finishedRecords) {
                List<String> dur = rec.getTime();
                ret.add(dur);
            }
            return ret;
        }
    }

    @Data
    public static class Record {
        private String sdate;
        private String edate;
        private long duration;
        private String threadName;
        private String key;

        public List<String> getTime() {
            List<String> ret = new ArrayList<>(2);
            LocalDateTime s = LocalDateTime.parse(sdate, FMT);
            LocalDateTime e = LocalDateTime.parse(edate, FMT);
            Duration difftime = Duration.between(s, e);

            ret.add("" + difftime.getSeconds());
            ret.add(key);
            return ret;
        }
    }
}


