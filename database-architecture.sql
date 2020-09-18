--
-- PostgreSQL database dump
--

-- Dumped from database version 12.4 (Ubuntu 12.4-0ubuntu0.20.04.1)
-- Dumped by pg_dump version 12.2

-- Started on 2020-09-18 09:21:52

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

--
-- TOC entry 5 (class 2615 OID 16386)
-- Name: accounts; Type: SCHEMA; Schema: -; Owner: admin
--

CREATE SCHEMA accounts;


ALTER SCHEMA accounts OWNER TO admin;

--
-- TOC entry 6 (class 2615 OID 16394)
-- Name: deadlines; Type: SCHEMA; Schema: -; Owner: admin
--

CREATE SCHEMA deadlines;


ALTER SCHEMA deadlines OWNER TO admin;

--
-- TOC entry 9 (class 2615 OID 16422)
-- Name: invoices; Type: SCHEMA; Schema: -; Owner: admin
--

CREATE SCHEMA invoices;


ALTER SCHEMA invoices OWNER TO admin;

--
-- TOC entry 10 (class 2615 OID 16538)
-- Name: jobs; Type: SCHEMA; Schema: -; Owner: admin
--

CREATE SCHEMA jobs;


ALTER SCHEMA jobs OWNER TO admin;

SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- TOC entry 205 (class 1259 OID 16388)
-- Name: accounts; Type: TABLE; Schema: accounts; Owner: admin
--

CREATE TABLE accounts.accounts (
    fiscalcode character varying NOT NULL,
    name character varying NOT NULL,
    numbervat character varying,
    atecocode character varying,
    legal_address character varying,
    customer_category character varying NOT NULL,
    descriptor character varying
);


ALTER TABLE accounts.accounts OWNER TO admin;

--
-- TOC entry 208 (class 1259 OID 16539)
-- Name: accounts_categories; Type: TABLE; Schema: accounts; Owner: admin
--

CREATE TABLE accounts.accounts_categories (
    categories character varying NOT NULL,
    extended character varying,
    description character varying
);


ALTER TABLE accounts.accounts_categories OWNER TO admin;

--
-- TOC entry 209 (class 1259 OID 16552)
-- Name: accounts_contact; Type: TABLE; Schema: accounts; Owner: admin
--

CREATE TABLE accounts.accounts_contact (
    fiscalcode character varying NOT NULL,
    description character varying,
    value character varying NOT NULL
);


ALTER TABLE accounts.accounts_contact OWNER TO admin;

--
-- TOC entry 210 (class 1259 OID 16558)
-- Name: accounts_contact_categories; Type: TABLE; Schema: accounts; Owner: admin
--

CREATE TABLE accounts.accounts_contact_categories (
    categories character varying NOT NULL,
    description character varying NOT NULL
);


ALTER TABLE accounts.accounts_contact_categories OWNER TO admin;

--
-- TOC entry 211 (class 1259 OID 16579)
-- Name: accounts_fiscaldata; Type: TABLE; Schema: accounts; Owner: admin
--

CREATE TABLE accounts.accounts_fiscaldata (
    fiscalcode character varying NOT NULL
);


ALTER TABLE accounts.accounts_fiscaldata OWNER TO admin;

--
-- TOC entry 206 (class 1259 OID 16395)
-- Name: rspp; Type: TABLE; Schema: deadlines; Owner: admin
--

CREATE TABLE deadlines.rspp (
    rspp_jobid character varying NOT NULL,
    jobstart date NOT NULL,
    jobend date NOT NULL,
    invoiceid character varying
);


ALTER TABLE deadlines.rspp OWNER TO admin;

--
-- TOC entry 218 (class 1259 OID 16744)
-- Name: rspp_invoices; Type: TABLE; Schema: deadlines; Owner: admin
--

CREATE TABLE deadlines.rspp_invoices (
    rspp_id character varying NOT NULL,
    rspp_start date NOT NULL,
    invoice_id character varying NOT NULL
);


ALTER TABLE deadlines.rspp_invoices OWNER TO admin;

--
-- TOC entry 215 (class 1259 OID 16653)
-- Name: rspp_notes; Type: TABLE; Schema: deadlines; Owner: admin
--

CREATE TABLE deadlines.rspp_notes (
    fiscalcode character varying NOT NULL,
    notes character varying
);


ALTER TABLE deadlines.rspp_notes OWNER TO admin;

--
-- TOC entry 207 (class 1259 OID 16532)
-- Name: invoices; Type: TABLE; Schema: invoices; Owner: admin
--

CREATE TABLE invoices.invoices (
    invoiceid character varying NOT NULL,
    number integer,
    emission date,
    type character varying,
    payed boolean NOT NULL,
    description character varying
);


ALTER TABLE invoices.invoices OWNER TO admin;

--
-- TOC entry 214 (class 1259 OID 16613)
-- Name: jobs; Type: TABLE; Schema: jobs; Owner: admin
--

CREATE TABLE jobs.jobs (
    jobs_id character varying NOT NULL,
    jobs_category character varying,
    jobs_type character varying,
    jobs_description character varying,
    customer character varying
);


ALTER TABLE jobs.jobs OWNER TO admin;

--
-- TOC entry 216 (class 1259 OID 16706)
-- Name: jobs_pa; Type: TABLE; Schema: jobs; Owner: admin
--

CREATE TABLE jobs.jobs_pa (
    jobs_id character varying NOT NULL,
    cig character varying,
    decree_number integer,
    decree_date date
);


ALTER TABLE jobs.jobs_pa OWNER TO admin;

--
-- TOC entry 217 (class 1259 OID 16719)
-- Name: PA datas; Type: VIEW; Schema: jobs; Owner: admin
--

CREATE VIEW jobs."PA datas" AS
 SELECT jp.jobs_id AS job_id,
    a.name,
    jp.cig,
    jp.decree_number,
    jp.decree_date
   FROM accounts.accounts a,
    jobs.jobs_pa jp,
    jobs.jobs j
  WHERE (((a.fiscalcode)::text = (j.customer)::text) AND ((j.jobs_id)::text = (jp.jobs_id)::text));


ALTER TABLE jobs."PA datas" OWNER TO admin;

--
-- TOC entry 213 (class 1259 OID 16600)
-- Name: job_types; Type: TABLE; Schema: jobs; Owner: admin
--

CREATE TABLE jobs.job_types (
    category character varying NOT NULL,
    types character varying NOT NULL
);


ALTER TABLE jobs.job_types OWNER TO admin;

--
-- TOC entry 212 (class 1259 OID 16592)
-- Name: jobs_categories; Type: TABLE; Schema: jobs; Owner: admin
--

CREATE TABLE jobs.jobs_categories (
    categories character varying NOT NULL
);


ALTER TABLE jobs.jobs_categories OWNER TO admin;

--
-- TOC entry 2869 (class 2606 OID 16546)
-- Name: accounts_categories accounts_categories_pk; Type: CONSTRAINT; Schema: accounts; Owner: admin
--

ALTER TABLE ONLY accounts.accounts_categories
    ADD CONSTRAINT accounts_categories_pk PRIMARY KEY (categories);


--
-- TOC entry 2873 (class 2606 OID 16565)
-- Name: accounts_contact_categories accounts_contact_categories_pk; Type: CONSTRAINT; Schema: accounts; Owner: admin
--

ALTER TABLE ONLY accounts.accounts_contact_categories
    ADD CONSTRAINT accounts_contact_categories_pk PRIMARY KEY (categories);


--
-- TOC entry 2871 (class 2606 OID 16572)
-- Name: accounts_contact accounts_contact_pk; Type: CONSTRAINT; Schema: accounts; Owner: admin
--

ALTER TABLE ONLY accounts.accounts_contact
    ADD CONSTRAINT accounts_contact_pk PRIMARY KEY (fiscalcode, value);


--
-- TOC entry 2875 (class 2606 OID 16586)
-- Name: accounts_fiscaldata accounts_fiscaldata_pk; Type: CONSTRAINT; Schema: accounts; Owner: admin
--

ALTER TABLE ONLY accounts.accounts_fiscaldata
    ADD CONSTRAINT accounts_fiscaldata_pk PRIMARY KEY (fiscalcode);


--
-- TOC entry 2863 (class 2606 OID 16434)
-- Name: accounts accounts_pk; Type: CONSTRAINT; Schema: accounts; Owner: admin
--

ALTER TABLE ONLY accounts.accounts
    ADD CONSTRAINT accounts_pk PRIMARY KEY (fiscalcode);


--
-- TOC entry 2887 (class 2606 OID 16761)
-- Name: rspp_invoices rspp_invoices_pk; Type: CONSTRAINT; Schema: deadlines; Owner: admin
--

ALTER TABLE ONLY deadlines.rspp_invoices
    ADD CONSTRAINT rspp_invoices_pk PRIMARY KEY (rspp_id, rspp_start, invoice_id);


--
-- TOC entry 2889 (class 2606 OID 16765)
-- Name: rspp_invoices rspp_invoices_un; Type: CONSTRAINT; Schema: deadlines; Owner: admin
--

ALTER TABLE ONLY deadlines.rspp_invoices
    ADD CONSTRAINT rspp_invoices_un UNIQUE (invoice_id);


--
-- TOC entry 2883 (class 2606 OID 16665)
-- Name: rspp_notes rspp_notes_pk; Type: CONSTRAINT; Schema: deadlines; Owner: admin
--

ALTER TABLE ONLY deadlines.rspp_notes
    ADD CONSTRAINT rspp_notes_pk PRIMARY KEY (fiscalcode);


--
-- TOC entry 2865 (class 2606 OID 16668)
-- Name: rspp rspp_pk; Type: CONSTRAINT; Schema: deadlines; Owner: admin
--

ALTER TABLE ONLY deadlines.rspp
    ADD CONSTRAINT rspp_pk PRIMARY KEY (rspp_jobid, jobstart);


--
-- TOC entry 2867 (class 2606 OID 16673)
-- Name: invoices invoices_pk; Type: CONSTRAINT; Schema: invoices; Owner: admin
--

ALTER TABLE ONLY invoices.invoices
    ADD CONSTRAINT invoices_pk PRIMARY KEY (invoiceid);


--
-- TOC entry 2879 (class 2606 OID 16612)
-- Name: job_types job_type_pk; Type: CONSTRAINT; Schema: jobs; Owner: admin
--

ALTER TABLE ONLY jobs.job_types
    ADD CONSTRAINT job_type_pk PRIMARY KEY (category, types);


--
-- TOC entry 2877 (class 2606 OID 16599)
-- Name: jobs_categories jobs_categories_pk; Type: CONSTRAINT; Schema: jobs; Owner: admin
--

ALTER TABLE ONLY jobs.jobs_categories
    ADD CONSTRAINT jobs_categories_pk PRIMARY KEY (categories);


--
-- TOC entry 2885 (class 2606 OID 16718)
-- Name: jobs_pa jobs_pa_pk; Type: CONSTRAINT; Schema: jobs; Owner: admin
--

ALTER TABLE ONLY jobs.jobs_pa
    ADD CONSTRAINT jobs_pa_pk PRIMARY KEY (jobs_id);


--
-- TOC entry 2881 (class 2606 OID 16625)
-- Name: jobs jobs_pk; Type: CONSTRAINT; Schema: jobs; Owner: admin
--

ALTER TABLE ONLY jobs.jobs
    ADD CONSTRAINT jobs_pk PRIMARY KEY (jobs_id);


--
-- TOC entry 2894 (class 2606 OID 16566)
-- Name: accounts_contact accounts_contact_fk; Type: FK CONSTRAINT; Schema: accounts; Owner: admin
--

ALTER TABLE ONLY accounts.accounts_contact
    ADD CONSTRAINT accounts_contact_fk FOREIGN KEY (description) REFERENCES accounts.accounts_contact_categories(categories) ON UPDATE CASCADE;


--
-- TOC entry 2895 (class 2606 OID 16587)
-- Name: accounts_fiscaldata accounts_fiscaldata_fk; Type: FK CONSTRAINT; Schema: accounts; Owner: admin
--

ALTER TABLE ONLY accounts.accounts_fiscaldata
    ADD CONSTRAINT accounts_fiscaldata_fk FOREIGN KEY (fiscalcode) REFERENCES accounts.accounts(fiscalcode) ON UPDATE CASCADE;


--
-- TOC entry 2890 (class 2606 OID 16547)
-- Name: accounts accounts_fk; Type: FK CONSTRAINT; Schema: accounts; Owner: admin
--

ALTER TABLE ONLY accounts.accounts
    ADD CONSTRAINT accounts_fk FOREIGN KEY (customer_category) REFERENCES accounts.accounts_categories(categories) ON UPDATE CASCADE;


--
-- TOC entry 2892 (class 2606 OID 16684)
-- Name: rspp invoice_fk; Type: FK CONSTRAINT; Schema: deadlines; Owner: admin
--

ALTER TABLE ONLY deadlines.rspp
    ADD CONSTRAINT invoice_fk FOREIGN KEY (invoiceid) REFERENCES invoices.invoices(invoiceid) ON UPDATE CASCADE;


--
-- TOC entry 2891 (class 2606 OID 16646)
-- Name: rspp rspp_fk; Type: FK CONSTRAINT; Schema: deadlines; Owner: admin
--

ALTER TABLE ONLY deadlines.rspp
    ADD CONSTRAINT rspp_fk FOREIGN KEY (rspp_jobid) REFERENCES jobs.jobs(jobs_id) ON UPDATE CASCADE;


--
-- TOC entry 2902 (class 2606 OID 16750)
-- Name: rspp_invoices rspp_invoices_fk; Type: FK CONSTRAINT; Schema: deadlines; Owner: admin
--

ALTER TABLE ONLY deadlines.rspp_invoices
    ADD CONSTRAINT rspp_invoices_fk FOREIGN KEY (rspp_id, rspp_start) REFERENCES deadlines.rspp(rspp_jobid, jobstart) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- TOC entry 2903 (class 2606 OID 16755)
-- Name: rspp_invoices rspp_invoices_fk_1; Type: FK CONSTRAINT; Schema: deadlines; Owner: admin
--

ALTER TABLE ONLY deadlines.rspp_invoices
    ADD CONSTRAINT rspp_invoices_fk_1 FOREIGN KEY (invoice_id) REFERENCES invoices.invoices(invoiceid) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- TOC entry 2900 (class 2606 OID 16659)
-- Name: rspp_notes rspp_notes_fk; Type: FK CONSTRAINT; Schema: deadlines; Owner: admin
--

ALTER TABLE ONLY deadlines.rspp_notes
    ADD CONSTRAINT rspp_notes_fk FOREIGN KEY (fiscalcode) REFERENCES accounts.accounts(fiscalcode) ON UPDATE CASCADE;


--
-- TOC entry 2893 (class 2606 OID 16701)
-- Name: invoices invoices_fk; Type: FK CONSTRAINT; Schema: invoices; Owner: admin
--

ALTER TABLE ONLY invoices.invoices
    ADD CONSTRAINT invoices_fk FOREIGN KEY (type) REFERENCES accounts.accounts_categories(categories) ON UPDATE CASCADE;


--
-- TOC entry 2897 (class 2606 OID 16631)
-- Name: jobs accounts_fk; Type: FK CONSTRAINT; Schema: jobs; Owner: admin
--

ALTER TABLE ONLY jobs.jobs
    ADD CONSTRAINT accounts_fk FOREIGN KEY (customer) REFERENCES accounts.accounts(fiscalcode) ON UPDATE CASCADE;


--
-- TOC entry 2898 (class 2606 OID 16636)
-- Name: jobs categories_fk; Type: FK CONSTRAINT; Schema: jobs; Owner: admin
--

ALTER TABLE ONLY jobs.jobs
    ADD CONSTRAINT categories_fk FOREIGN KEY (jobs_category) REFERENCES jobs.jobs_categories(categories) ON UPDATE CASCADE;


--
-- TOC entry 2896 (class 2606 OID 16606)
-- Name: job_types job_type_fk; Type: FK CONSTRAINT; Schema: jobs; Owner: admin
--

ALTER TABLE ONLY jobs.job_types
    ADD CONSTRAINT job_type_fk FOREIGN KEY (category) REFERENCES jobs.jobs_categories(categories) ON UPDATE CASCADE;


--
-- TOC entry 2901 (class 2606 OID 16712)
-- Name: jobs_pa jobs_pa_fk; Type: FK CONSTRAINT; Schema: jobs; Owner: admin
--

ALTER TABLE ONLY jobs.jobs_pa
    ADD CONSTRAINT jobs_pa_fk FOREIGN KEY (jobs_id) REFERENCES jobs.jobs(jobs_id) ON UPDATE CASCADE;


--
-- TOC entry 2899 (class 2606 OID 16641)
-- Name: jobs type_fk; Type: FK CONSTRAINT; Schema: jobs; Owner: admin
--

ALTER TABLE ONLY jobs.jobs
    ADD CONSTRAINT type_fk FOREIGN KEY (jobs_category, jobs_type) REFERENCES jobs.job_types(category, types) ON UPDATE CASCADE;


--
-- TOC entry 3036 (class 0 OID 0)
-- Dependencies: 5
-- Name: SCHEMA accounts; Type: ACL; Schema: -; Owner: admin
--

GRANT USAGE ON SCHEMA accounts TO dclient;
GRANT USAGE ON SCHEMA accounts TO script;


--
-- TOC entry 3037 (class 0 OID 0)
-- Dependencies: 6
-- Name: SCHEMA deadlines; Type: ACL; Schema: -; Owner: admin
--

GRANT USAGE ON SCHEMA deadlines TO dclient;
GRANT USAGE ON SCHEMA deadlines TO script;


--
-- TOC entry 3038 (class 0 OID 0)
-- Dependencies: 9
-- Name: SCHEMA invoices; Type: ACL; Schema: -; Owner: admin
--

GRANT USAGE ON SCHEMA invoices TO dclient;
GRANT USAGE ON SCHEMA invoices TO script;


--
-- TOC entry 3039 (class 0 OID 0)
-- Dependencies: 10
-- Name: SCHEMA jobs; Type: ACL; Schema: -; Owner: admin
--

GRANT USAGE ON SCHEMA jobs TO dclient;
GRANT USAGE ON SCHEMA jobs TO script;


--
-- TOC entry 3040 (class 0 OID 0)
-- Dependencies: 205
-- Name: TABLE accounts; Type: ACL; Schema: accounts; Owner: admin
--

GRANT SELECT,INSERT,UPDATE ON TABLE accounts.accounts TO dclient;
GRANT SELECT ON TABLE accounts.accounts TO script;


--
-- TOC entry 3041 (class 0 OID 0)
-- Dependencies: 208
-- Name: TABLE accounts_categories; Type: ACL; Schema: accounts; Owner: admin
--

GRANT SELECT ON TABLE accounts.accounts_categories TO dclient;
GRANT SELECT ON TABLE accounts.accounts_categories TO script;


--
-- TOC entry 3042 (class 0 OID 0)
-- Dependencies: 209
-- Name: TABLE accounts_contact; Type: ACL; Schema: accounts; Owner: admin
--

GRANT SELECT,INSERT,UPDATE ON TABLE accounts.accounts_contact TO dclient;
GRANT SELECT ON TABLE accounts.accounts_contact TO script;


--
-- TOC entry 3043 (class 0 OID 0)
-- Dependencies: 210
-- Name: TABLE accounts_contact_categories; Type: ACL; Schema: accounts; Owner: admin
--

GRANT SELECT ON TABLE accounts.accounts_contact_categories TO dclient;
GRANT SELECT ON TABLE accounts.accounts_contact_categories TO script;


--
-- TOC entry 3044 (class 0 OID 0)
-- Dependencies: 211
-- Name: TABLE accounts_fiscaldata; Type: ACL; Schema: accounts; Owner: admin
--

GRANT SELECT,INSERT,UPDATE ON TABLE accounts.accounts_fiscaldata TO dclient;
GRANT SELECT ON TABLE accounts.accounts_fiscaldata TO script;


--
-- TOC entry 3045 (class 0 OID 0)
-- Dependencies: 206
-- Name: TABLE rspp; Type: ACL; Schema: deadlines; Owner: admin
--

GRANT SELECT,INSERT,UPDATE ON TABLE deadlines.rspp TO dclient;
GRANT SELECT ON TABLE deadlines.rspp TO script;


--
-- TOC entry 3046 (class 0 OID 0)
-- Dependencies: 218
-- Name: TABLE rspp_invoices; Type: ACL; Schema: deadlines; Owner: admin
--

GRANT SELECT,INSERT,UPDATE ON TABLE deadlines.rspp_invoices TO dclient;


--
-- TOC entry 3047 (class 0 OID 0)
-- Dependencies: 215
-- Name: TABLE rspp_notes; Type: ACL; Schema: deadlines; Owner: admin
--

GRANT SELECT,INSERT,UPDATE ON TABLE deadlines.rspp_notes TO dclient;
GRANT SELECT ON TABLE deadlines.rspp_notes TO script;


--
-- TOC entry 3048 (class 0 OID 0)
-- Dependencies: 207
-- Name: TABLE invoices; Type: ACL; Schema: invoices; Owner: admin
--

GRANT SELECT,INSERT,UPDATE ON TABLE invoices.invoices TO dclient;
GRANT SELECT ON TABLE invoices.invoices TO script;


--
-- TOC entry 3049 (class 0 OID 0)
-- Dependencies: 214
-- Name: TABLE jobs; Type: ACL; Schema: jobs; Owner: admin
--

GRANT SELECT,INSERT,UPDATE ON TABLE jobs.jobs TO dclient;
GRANT SELECT ON TABLE jobs.jobs TO script;


--
-- TOC entry 3050 (class 0 OID 0)
-- Dependencies: 216
-- Name: TABLE jobs_pa; Type: ACL; Schema: jobs; Owner: admin
--

GRANT SELECT,INSERT,UPDATE ON TABLE jobs.jobs_pa TO dclient;
GRANT SELECT ON TABLE jobs.jobs_pa TO script;


--
-- TOC entry 3051 (class 0 OID 0)
-- Dependencies: 213
-- Name: TABLE job_types; Type: ACL; Schema: jobs; Owner: admin
--

GRANT SELECT ON TABLE jobs.job_types TO dclient;
GRANT SELECT ON TABLE jobs.job_types TO script;


--
-- TOC entry 3052 (class 0 OID 0)
-- Dependencies: 212
-- Name: TABLE jobs_categories; Type: ACL; Schema: jobs; Owner: admin
--

GRANT SELECT ON TABLE jobs.jobs_categories TO dclient;
GRANT SELECT ON TABLE jobs.jobs_categories TO script;


-- Completed on 2020-09-18 09:21:53

--
-- PostgreSQL database dump complete
--

