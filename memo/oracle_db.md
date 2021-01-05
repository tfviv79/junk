select * from session_privs;

explain plan for xxxxx;
select * from table(dbms_xplan.display());


= sqlplus

set autocommit off
set exitcommit off

set linesize 1000
set pagesize 0

== tsv output

set long 20000000
set longc 20000000
set flush off
set trimespool on
set linesize 32767
set feedback off
set echo off
set termout off

alter session set nls_date_format = 'YYYY-MM-DD HH24:MI:SS';
spool hoge.tsv

select hoge || chr(9) || poge from table_name;
spool off
