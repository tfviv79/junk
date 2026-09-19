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
