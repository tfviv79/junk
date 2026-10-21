package com.example.myapp.infra.db;

import java.util.ArrayList;
import java.util.List;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.example.myapp.domain.Raw;
import com.example.myapp.domain.ReHistogram;
import com.example.myapp.domain.ReHistogramWithRank;
import com.example.myapp.domain.StatsRepository;

@Repository
public class StatsRepositoryImpl implements StatsRepository {

    private final JdbcTemplate jdbc;

    public StatsRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbc = jdbcTemplate;
    }

	@Override
	public List<Raw> findRaws(int limit) {
        return jdbc.query("""
        SELECT 
            id
            , treat
            , age
            , education
            , black
            , hispanic
            , married
            , nodegree
            , re74
            , re75
            , re78
        FROM cps_controls
        LIMIT ?
        """, (rs, rowNum) -> new Raw(
            rs.getInt("treat")
            , rs.getInt("age")
            , rs.getInt("education")
            , rs.getBoolean("black")
            , rs.getBoolean("hispanic")
            , rs.getBoolean("married")
            , rs.getBoolean("nodegree")
            , rs.getDouble("re74")
            , rs.getDouble("re75")
            , rs.getDouble("re78")
        ),
        limit
        );
	}

    public List<ReHistogram> findReHistograms() {
        return jdbc.query("""
            select re_rank, count(id) from re1 group by re_rank order by re_rank;
            """, (rs, rowNum) -> ReHistogram.from(rs));
    }

    public Map<String, List<ReHistogramWithRank>> findReHistogramsWithRank(int rank) {
        List<ReHistogramWithRank> values = jdbc.query("""
            WITH tgt AS (
              SELECT cps.* FROM cps_controls cps
              JOIN (select distinct id from re1 where re_rank = ?) ids ON ids.id = cps.id
            )
            SELECT 'treat' name, treat key, COUNT(*) cnt FROM tgt GROUP BY treat
            UNION ALL SELECT 'age', age, COUNT(*) FROM tgt GROUP BY age
            UNION ALL SELECT 'education', education, COUNT(*) FROM tgt GROUP BY education
            UNION ALL SELECT 'black', case when black then 1 else 0 end, COUNT(*) FROM tgt GROUP BY black
            UNION ALL SELECT 'hispanic', case when hispanic then 1 else 0 end, COUNT(*) FROM tgt GROUP BY hispanic
            UNION ALL SELECT 'married', case when married then 1 else 0 end, COUNT(*) FROM tgt GROUP BY married
            UNION ALL SELECT 'nodegree', case when nodegree then 1 else 0 end, COUNT(*) FROM tgt GROUP BY nodegree
            ORDER BY name, key;
            """, (rs, rowNum) -> ReHistogramWithRank.from(rs)
            , rank
        );

        Map<String, List<ReHistogramWithRank>> m = new LinkedHashMap<>();

        for (ReHistogramWithRank r : values) {
            if (m.containsKey(r.name())) {
                m.get(r.name()).add(r);
            } else {
                List<ReHistogramWithRank> l = new ArrayList<>();
                l.add(r);
                m.put(r.name(), l);
            }
        }

        return m;
    }
}
