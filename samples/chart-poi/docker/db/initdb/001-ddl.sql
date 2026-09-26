-- https://users.nber.org/~rdehejia/data/cps_controls.dta
CREATE TABLE cps_controls (
    id SERIAL PRIMARY KEY,
    treat smallint NOT NULL,
    age smallint NOT NULL,
    education smallint NOT NULL,
    black boolean NOT NULL,
    hispanic boolean NOT NULL,
    married boolean NOT NULL,
    nodegree boolean NOT NULL,
    re74 double precision NOT NULL,
    re75 double precision NOT NULL,
    re78 double precision NOT NULL
);

CREATE OR REPLACE VIEW re (id, year, re) 
AS 
SELECT id, 1974, re74 FROM cps_controls
UNION ALL SELECT id, 1975, re75 FROM cps_controls
UNION ALL SELECT id, 1978, re78 FROM cps_controls;

CREATE TABLE re1 (
    id integer,
    year smallint NOT NULL,
    re double precision NOT NULL,
    re_rank smallint generated always as (ceil(re/2500)) NOT NULL,
    PRIMARY KEY (id, year)
);
