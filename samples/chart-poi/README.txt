
CREATE OR REPLACE VIEW re (id, year, re) 
AS 
SELECT id, 1974, re74 FROM cps_controls
UNION ALL SELECT id, 1975, re75 FROM cps_controls
UNION ALL SELECT id, 1978, re78 FROM cps_controls;


