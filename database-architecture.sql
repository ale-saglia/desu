--
-- PostgreSQL database dump
--

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
-- TOC entry 225 (class 1259 OID 16904)
-- Name: asbestos_advice; Type: TABLE; Schema: deadlines; Owner: admin
--

CREATE TABLE deadlines.asbestos_advice (
    jobid character varying NOT NULL,
    jobstart date NOT NULL,
    jobend date NOT NULL,
    invoice_override boolean
);


ALTER TABLE deadlines.asbestos_advice OWNER TO admin;

--
-- TOC entry 223 (class 1259 OID 16865)
-- Name: asbestos_advice_account_details; Type: TABLE; Schema: deadlines; Owner: admin
--

CREATE TABLE deadlines.asbestos_advice_account_details (
    fiscalcode character varying NOT NULL,
    notes character varying,
    expired boolean DEFAULT false,
    anticipated boolean DEFAULT false NOT NULL
);


ALTER TABLE deadlines.asbestos_advice_account_details OWNER TO admin;

--
-- TOC entry 226 (class 1259 OID 16933)
-- Name: asbestos_advice_invoices; Type: TABLE; Schema: deadlines; Owner: admin
--

CREATE TABLE deadlines.asbestos_advice_invoices (
    job_id character varying NOT NULL,
    job_start date NOT NULL,
    invoice_id character varying NOT NULL
);


ALTER TABLE deadlines.asbestos_advice_invoices OWNER TO admin;

--
-- TOC entry 224 (class 1259 OID 16875)
-- Name: asbestos_advice_invoices_months; Type: TABLE; Schema: deadlines; Owner: admin
--

CREATE TABLE deadlines.asbestos_advice_invoices_months (
    customer character varying NOT NULL,
    months integer[]
);


ALTER TABLE deadlines.asbestos_advice_invoices_months OWNER TO admin;

--
-- TOC entry 206 (class 1259 OID 16395)
-- Name: rspp; Type: TABLE; Schema: deadlines; Owner: admin
--

CREATE TABLE deadlines.rspp (
    rspp_jobid character varying NOT NULL,
    jobstart date NOT NULL,
    jobend date NOT NULL,
    invoice_override boolean
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
-- TOC entry 219 (class 1259 OID 16766)
-- Name: rspp_invoices_months; Type: TABLE; Schema: deadlines; Owner: admin
--

CREATE TABLE deadlines.rspp_invoices_months (
    customer character varying NOT NULL,
    months integer[]
);


ALTER TABLE deadlines.rspp_invoices_months OWNER TO admin;

--
-- TOC entry 215 (class 1259 OID 16653)
-- Name: rssp_account_details; Type: TABLE; Schema: deadlines; Owner: admin
--

CREATE TABLE deadlines.rssp_account_details (
    fiscalcode character varying NOT NULL,
    notes character varying,
    expired boolean DEFAULT false,
    anticipated boolean DEFAULT false NOT NULL
);


ALTER TABLE deadlines.rssp_account_details OWNER TO admin;

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
-- TOC entry 228 (class 1259 OID 16982)
-- Name: expired_without_invoice; Type: VIEW; Schema: deadlines; Owner: admin
--

CREATE VIEW deadlines.expired_without_invoice AS
 SELECT main.jobid,
    main.jobstart,
    main.jobend,
    main.customer,
    main.months,
    main.notes,
    main.anticipated,
    main.expired,
    main.invoice_override,
    main.jobs_category,
    main.jobs_type,
    main.jobs_description,
    main.fiscalcode,
    main.name,
    main.numbervat,
    main.atecocode,
    main.legal_address,
    main.customer_category,
    main.descriptor,
    jp.cig,
    jp.decree_number,
    jp.decree_date
   FROM (( SELECT db_main.jobid,
            db_main.jobstart,
            db_main.jobend,
            j.customer,
            db_details.months,
            db_details.notes,
            db_details.anticipated,
            db_details.expired,
            db_details.invoice_override,
            j.jobs_category,
            j.jobs_type,
            j.jobs_description,
            a.fiscalcode,
            a.name,
            a.numbervat,
            a.atecocode,
            a.legal_address,
            a.customer_category,
            a.descriptor
           FROM ( SELECT db11.jobid,
                    db11.jobstart,
                    db11.jobend
                   FROM (( SELECT r.rspp_jobid AS jobid,
                            r.jobstart,
                            r.jobend,
                            array_length(rim.months, 1) AS invoice_number
                           FROM deadlines.rspp r,
                            jobs.jobs j_1,
                            deadlines.rspp_invoices_months rim
                          WHERE ((r.invoice_override IS NOT TRUE) AND ((j_1.jobs_id)::text = (r.rspp_jobid)::text) AND ((j_1.customer)::text = (rim.customer)::text))
                        UNION
                         SELECT aa.jobid,
                            aa.jobstart,
                            aa.jobend,
                            array_length(aaim.months, 1) AS invoice_number
                           FROM deadlines.asbestos_advice aa,
                            jobs.jobs j_1,
                            deadlines.asbestos_advice_invoices_months aaim
                          WHERE ((aa.invoice_override IS NOT TRUE) AND ((j_1.jobs_id)::text = (aa.jobid)::text) AND ((j_1.customer)::text = (aaim.customer)::text))) db11
                     LEFT JOIN ( SELECT aai.job_id AS jobid,
                            aai.job_start AS jobstart,
                            count(aai.invoice_id) AS invoice_count
                           FROM deadlines.asbestos_advice_invoices aai
                          GROUP BY aai.job_id, aai.job_start
                        UNION
                         SELECT ri.rspp_id AS jobid,
                            ri.rspp_start AS jobstart,
                            count(ri.invoice_id) AS invoice_count
                           FROM deadlines.rspp_invoices ri
                          GROUP BY ri.rspp_id, ri.rspp_start) db12 ON ((((db11.jobid)::text = (db12.jobid)::text) AND (db11.jobstart = db12.jobstart))))
                  WHERE (((db11.jobend + '3 mons'::interval) < (date_trunc('MONTH'::text, now()))::date) AND ((db12.invoice_count IS NULL) OR (db12.invoice_count < db11.invoice_number)))) db_main,
            ( SELECT aa.jobid,
                    aa.jobstart,
                    aaim.months,
                    aaad.notes,
                    aaad.anticipated,
                    aaad.expired,
                    aa.invoice_override,
                    j_1.jobs_category,
                    j_1.jobs_type,
                    j_1.jobs_description
                   FROM deadlines.asbestos_advice aa,
                    deadlines.asbestos_advice_account_details aaad,
                    deadlines.asbestos_advice_invoices_months aaim,
                    jobs.jobs j_1
                  WHERE (((aa.jobid)::text = (j_1.jobs_id)::text) AND ((aaad.fiscalcode)::text = (j_1.customer)::text) AND ((aaim.customer)::text = (j_1.customer)::text))
                UNION
                 SELECT r.rspp_jobid AS jobid,
                    r.jobstart,
                    rim.months,
                    rad.notes,
                    rad.anticipated,
                    rad.expired,
                    r.invoice_override,
                    j_1.jobs_category,
                    j_1.jobs_type,
                    j_1.jobs_description
                   FROM deadlines.rspp r,
                    deadlines.rssp_account_details rad,
                    deadlines.rspp_invoices_months rim,
                    jobs.jobs j_1
                  WHERE (((r.rspp_jobid)::text = (j_1.jobs_id)::text) AND ((rad.fiscalcode)::text = (j_1.customer)::text) AND ((rim.customer)::text = (j_1.customer)::text))) db_details,
            jobs.jobs j,
            accounts.accounts a
          WHERE (((db_main.jobid)::text = (j.jobs_id)::text) AND ((a.fiscalcode)::text = (j.customer)::text) AND ((db_main.jobid)::text = (db_details.jobid)::text) AND (db_main.jobstart = db_details.jobstart))) main
     LEFT JOIN jobs.jobs_pa jp ON (((main.jobid)::text = (jp.jobs_id)::text)));


ALTER TABLE deadlines.expired_without_invoice OWNER TO admin;

--
-- TOC entry 229 (class 1259 OID 16987)
-- Name: current_month_invoices; Type: VIEW; Schema: deadlines; Owner: admin
--

CREATE VIEW deadlines.current_month_invoices AS
 SELECT main.jobid,
    main.jobstart,
    main.jobend,
    main.customer,
    main.months,
    main.notes,
    main.anticipated,
    main.expired,
    main.invoice_override,
    main.jobs_category,
    main.jobs_type,
    main.jobs_description,
    main.fiscalcode,
    main.name,
    main.numbervat,
    main.atecocode,
    main.legal_address,
    main.customer_category,
    main.descriptor,
    jp.jobs_id,
    jp.cig,
    jp.decree_number,
    jp.decree_date
   FROM (( SELECT valid_invoices.jobid,
            valid_invoices.jobstart,
            valid_invoices.jobend,
            valid_invoices.customer,
            valid_invoices.months,
            valid_invoices.notes,
            valid_invoices.anticipated,
            valid_invoices.expired,
            valid_invoices.invoice_override,
            valid_invoices.jobs_category,
            valid_invoices.jobs_type,
            valid_invoices.jobs_description,
            valid_invoices.fiscalcode,
            valid_invoices.name,
            valid_invoices.numbervat,
            valid_invoices.atecocode,
            valid_invoices.legal_address,
            valid_invoices.customer_category,
            valid_invoices.descriptor
           FROM ( SELECT dd.jobid,
                    dd.jobstart,
                    dd.jobend,
                    dd.customer,
                    dd.months,
                    dd.notes,
                    dd.anticipated,
                    dd.expired,
                    dd.invoice_override,
                    dd.jobs_category,
                    dd.jobs_type,
                    dd.jobs_description,
                    a.fiscalcode,
                    a.name,
                    a.numbervat,
                    a.atecocode,
                    a.legal_address,
                    a.customer_category,
                    a.descriptor
                   FROM ( SELECT j.jobs_id AS jobid,
                            r.jobstart,
                            r.jobend,
                            j.customer,
                            rim.months,
                            rad.notes,
                            rad.anticipated,
                            rad.expired,
                            r.invoice_override,
                            j.jobs_category,
                            j.jobs_type,
                            j.jobs_description
                           FROM deadlines.rspp r,
                            deadlines.rspp_invoices_months rim,
                            deadlines.rssp_account_details rad,
                            jobs.jobs j
                          WHERE (((r.rspp_jobid)::text = (j.jobs_id)::text) AND ((rim.customer)::text = (j.customer)::text) AND ((rad.fiscalcode)::text = (j.customer)::text))
                        UNION
                         SELECT aa.jobid,
                            aa.jobstart,
                            aa.jobend,
                            j.customer,
                            aaim.months,
                            aaad.notes,
                            aaad.anticipated,
                            aaad.expired,
                            aa.invoice_override,
                            j.jobs_category,
                            j.jobs_type,
                            j.jobs_description
                           FROM deadlines.asbestos_advice aa,
                            deadlines.asbestos_advice_invoices_months aaim,
                            deadlines.asbestos_advice_account_details aaad,
                            jobs.jobs j
                          WHERE (((aa.jobid)::text = (j.jobs_id)::text) AND ((aaim.customer)::text = (j.customer)::text) AND ((aaad.fiscalcode)::text = (j.customer)::text))) dd,
                    accounts.accounts a
                  WHERE (((a.fiscalcode)::text = (dd.customer)::text) AND (((dd.jobid)::text, dd.jobstart) IN ( SELECT db2.jobid,
                            db2.jobstart
                           FROM (( SELECT dat.jobid,
                                    dat.jobstart,
                                    count(dat.invoice_id) AS invoice_count
                                   FROM ( SELECT ri.invoice_id,
    ri.rspp_id AS jobid,
    ri.rspp_start AS jobstart
   FROM deadlines.rspp_invoices ri
UNION
 SELECT aai.invoice_id,
    aai.job_id AS jobid,
    aai.job_start AS jobstart
   FROM deadlines.asbestos_advice_invoices aai) dat
                                  GROUP BY dat.jobid, dat.jobstart) db1
                             RIGHT JOIN ( SELECT aa.jobid,
                                    aa.jobstart,
                                    aa.jobend,
                                    array_length(aaim.months, 1) AS invoice_number
                                   FROM deadlines.asbestos_advice aa,
                                    jobs.jobs j,
                                    deadlines.asbestos_advice_invoices_months aaim
                                  WHERE (((aa.jobid)::text = (j.jobs_id)::text) AND ((aaim.customer)::text = (j.customer)::text))
                                UNION
                                 SELECT r.rspp_jobid AS jobid,
                                    r.jobstart,
                                    r.jobend,
                                    array_length(rim.months, 1) AS invoice_number
                                   FROM deadlines.rspp r,
                                    jobs.jobs j,
                                    deadlines.rspp_invoices_months rim
                                  WHERE (((r.rspp_jobid)::text = (j.jobs_id)::text) AND ((rim.customer)::text = (j.customer)::text))) db2 ON ((((db1.jobid)::text = (db2.jobid)::text) AND (db2.jobstart = db2.jobstart))))
                          WHERE ((db1.invoice_count IS NULL) OR ((db1.invoice_count < db2.invoice_number) AND (NOT (((db2.jobid)::text, db2.jobstart) IN ( SELECT aa.jobid,
                                    aa.jobstart
                                   FROM deadlines.asbestos_advice aa
                                  WHERE (aa.invoice_override IS TRUE)
                                UNION
                                 SELECT r.rspp_jobid AS jobid,
                                    r.jobstart
                                   FROM deadlines.rspp r
                                  WHERE (r.invoice_override IS TRUE)))))))))) valid_invoices
          WHERE ((date_part('month'::text, (date_trunc('MONTH'::text, now()))::date) = ANY ((valid_invoices.months)::double precision[])) AND (((valid_invoices.anticipated IS TRUE) AND ((date_trunc('MONTH'::text, now()))::date >= (valid_invoices.jobstart + '5 mons'::interval))) OR ((valid_invoices.anticipated IS NOT TRUE) AND ((date_trunc('MONTH'::text, now()))::date <= (valid_invoices.jobstart + '5 mons'::interval)))) AND (NOT (((valid_invoices.jobid)::text, valid_invoices.jobstart) IN ( SELECT ewi.jobid,
                    ewi.jobstart
                   FROM deadlines.expired_without_invoice ewi))))) main
     LEFT JOIN jobs.jobs_pa jp ON (((main.jobid)::text = (jp.jobs_id)::text)));


ALTER TABLE deadlines.current_month_invoices OWNER TO admin;

--
-- TOC entry 222 (class 1259 OID 16808)
-- Name: expiring_next_60_days; Type: VIEW; Schema: deadlines; Owner: admin
--

CREATE VIEW deadlines.expiring_next_60_days AS
 SELECT main.name,
    main.job_end,
    rad.notes
   FROM (( SELECT a.name,
            a.fiscalcode,
            max(r.jobend) AS job_end
           FROM accounts.accounts a,
            jobs.jobs j,
            deadlines.rspp r
          WHERE (((j.customer)::text = (a.fiscalcode)::text) AND ((j.jobs_id)::text = (r.rspp_jobid)::text) AND (r.jobend < (now() + '60 days'::interval day)) AND (NOT ((a.name)::text IN ( SELECT a_1.name
                   FROM accounts.accounts a_1,
                    jobs.jobs j_1,
                    deadlines.rspp r_1
                  WHERE ((r_1.jobend > (now() + '60 days'::interval day)) AND ((j_1.jobs_id)::text = (r_1.rspp_jobid)::text) AND ((a_1.fiscalcode)::text = (j_1.customer)::text))))) AND (NOT ((a.fiscalcode)::text IN ( SELECT rn.fiscalcode
                   FROM deadlines.rssp_account_details rn
                  WHERE (rn.expired IS TRUE)))))
          GROUP BY a.name, a.fiscalcode
          ORDER BY a.name) main
     LEFT JOIN deadlines.rssp_account_details rad ON (((rad.fiscalcode)::text = (main.fiscalcode)::text)));


ALTER TABLE deadlines.expiring_next_60_days OWNER TO admin;

--
-- TOC entry 221 (class 1259 OID 16799)
-- Name: missing_invoice_month_customer; Type: VIEW; Schema: deadlines; Owner: admin
--

CREATE VIEW deadlines.missing_invoice_month_customer AS
 SELECT DISTINCT a.name AS "Clienti con mese mancante"
   FROM accounts.accounts a,
    jobs.jobs j,
    deadlines.rspp r
  WHERE ((NOT ((a.fiscalcode)::text IN ( SELECT rim.customer AS fiscalcode
           FROM deadlines.rspp_invoices_months rim
          WHERE (array_length(rim.months, 1) > 0)))) AND ((a.fiscalcode)::text = (j.customer)::text) AND ((r.rspp_jobid)::text = (j.jobs_id)::text))
  ORDER BY a.name;


ALTER TABLE deadlines.missing_invoice_month_customer OWNER TO admin;

--
-- TOC entry 227 (class 1259 OID 16951)
-- Name: noise_vib_eval; Type: TABLE; Schema: deadlines; Owner: admin
--

CREATE TABLE deadlines.noise_vib_eval (
    customer character varying NOT NULL,
    job character varying NOT NULL,
    noise_eval boolean NOT NULL,
    vib_eval boolean NOT NULL,
    date date NOT NULL
);


ALTER TABLE deadlines.noise_vib_eval OWNER TO admin;

--
-- TOC entry 220 (class 1259 OID 16794)
-- Name: rspp_not_updated; Type: VIEW; Schema: deadlines; Owner: admin
--

CREATE VIEW deadlines.rspp_not_updated AS
 SELECT a.name AS "Ragione Sociale",
    max(r.jobend) AS "Scaduto il"
   FROM accounts.accounts a,
    jobs.jobs j,
    deadlines.rspp r
  WHERE (((j.customer)::text = (a.fiscalcode)::text) AND ((j.jobs_id)::text = (r.rspp_jobid)::text) AND (r.jobend < now()) AND (NOT ((a.name)::text IN ( SELECT a_1.name
           FROM accounts.accounts a_1,
            jobs.jobs j_1,
            deadlines.rspp r_1
          WHERE ((r_1.jobend > now()) AND ((j_1.jobs_id)::text = (r_1.rspp_jobid)::text) AND ((a_1.fiscalcode)::text = (j_1.customer)::text))))) AND (NOT ((a.fiscalcode)::text IN ( SELECT rn.fiscalcode
           FROM deadlines.rssp_account_details rn
          WHERE (rn.expired IS TRUE)))))
  GROUP BY a.name
  ORDER BY a.name;


ALTER TABLE deadlines.rspp_not_updated OWNER TO admin;

--
-- TOC entry 3138 (class 0 OID 0)
-- Dependencies: 220
-- Name: VIEW rspp_not_updated; Type: COMMENT; Schema: deadlines; Owner: admin
--

COMMENT ON VIEW deadlines.rspp_not_updated IS 'Mostra gli incarichi RSPP scaduti che non sono stati rinnovati (e non sono stati segnalati come scaduti)';


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
    description character varying,
    taxable real
);


ALTER TABLE invoices.invoices OWNER TO admin;

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
-- TOC entry 2925 (class 2606 OID 16546)
-- Name: accounts_categories accounts_categories_pk; Type: CONSTRAINT; Schema: accounts; Owner: admin
--

ALTER TABLE ONLY accounts.accounts_categories
    ADD CONSTRAINT accounts_categories_pk PRIMARY KEY (categories);


--
-- TOC entry 2929 (class 2606 OID 16565)
-- Name: accounts_contact_categories accounts_contact_categories_pk; Type: CONSTRAINT; Schema: accounts; Owner: admin
--

ALTER TABLE ONLY accounts.accounts_contact_categories
    ADD CONSTRAINT accounts_contact_categories_pk PRIMARY KEY (categories);


--
-- TOC entry 2927 (class 2606 OID 16572)
-- Name: accounts_contact accounts_contact_pk; Type: CONSTRAINT; Schema: accounts; Owner: admin
--

ALTER TABLE ONLY accounts.accounts_contact
    ADD CONSTRAINT accounts_contact_pk PRIMARY KEY (fiscalcode, value);


--
-- TOC entry 2931 (class 2606 OID 16586)
-- Name: accounts_fiscaldata accounts_fiscaldata_pk; Type: CONSTRAINT; Schema: accounts; Owner: admin
--

ALTER TABLE ONLY accounts.accounts_fiscaldata
    ADD CONSTRAINT accounts_fiscaldata_pk PRIMARY KEY (fiscalcode);


--
-- TOC entry 2917 (class 2606 OID 16434)
-- Name: accounts accounts_pk; Type: CONSTRAINT; Schema: accounts; Owner: admin
--

ALTER TABLE ONLY accounts.accounts
    ADD CONSTRAINT accounts_pk PRIMARY KEY (fiscalcode);


--
-- TOC entry 2919 (class 2606 OID 16784)
-- Name: accounts accounts_un; Type: CONSTRAINT; Schema: accounts; Owner: admin
--

ALTER TABLE ONLY accounts.accounts
    ADD CONSTRAINT accounts_un UNIQUE (numbervat);


--
-- TOC entry 2955 (class 2606 OID 16940)
-- Name: asbestos_advice_invoices asbestos_advice_invoices_pk; Type: CONSTRAINT; Schema: deadlines; Owner: admin
--

ALTER TABLE ONLY deadlines.asbestos_advice_invoices
    ADD CONSTRAINT asbestos_advice_invoices_pk PRIMARY KEY (job_id, job_start, invoice_id);


--
-- TOC entry 2953 (class 2606 OID 16911)
-- Name: asbestos_advice asbestos_advice_pk; Type: CONSTRAINT; Schema: deadlines; Owner: admin
--

ALTER TABLE ONLY deadlines.asbestos_advice
    ADD CONSTRAINT asbestos_advice_pk PRIMARY KEY (jobid, jobstart);


--
-- TOC entry 2957 (class 2606 OID 16968)
-- Name: noise_vib_eval noise_vib_eval_pk; Type: CONSTRAINT; Schema: deadlines; Owner: admin
--

ALTER TABLE ONLY deadlines.noise_vib_eval
    ADD CONSTRAINT noise_vib_eval_pk PRIMARY KEY (customer, job);


--
-- TOC entry 2947 (class 2606 OID 16782)
-- Name: rspp_invoices_months rspp_invoices_months_pk; Type: CONSTRAINT; Schema: deadlines; Owner: admin
--

ALTER TABLE ONLY deadlines.rspp_invoices_months
    ADD CONSTRAINT rspp_invoices_months_pk PRIMARY KEY (customer);


--
-- TOC entry 2951 (class 2606 OID 16882)
-- Name: asbestos_advice_invoices_months rspp_invoices_months_pk_1; Type: CONSTRAINT; Schema: deadlines; Owner: admin
--

ALTER TABLE ONLY deadlines.asbestos_advice_invoices_months
    ADD CONSTRAINT rspp_invoices_months_pk_1 PRIMARY KEY (customer);


--
-- TOC entry 2945 (class 2606 OID 16815)
-- Name: rspp_invoices rspp_invoices_pk; Type: CONSTRAINT; Schema: deadlines; Owner: admin
--

ALTER TABLE ONLY deadlines.rspp_invoices
    ADD CONSTRAINT rspp_invoices_pk PRIMARY KEY (rspp_id, rspp_start, invoice_id);


--
-- TOC entry 2941 (class 2606 OID 16665)
-- Name: rssp_account_details rspp_notes_pk; Type: CONSTRAINT; Schema: deadlines; Owner: admin
--

ALTER TABLE ONLY deadlines.rssp_account_details
    ADD CONSTRAINT rspp_notes_pk PRIMARY KEY (fiscalcode);


--
-- TOC entry 2949 (class 2606 OID 16874)
-- Name: asbestos_advice_account_details rspp_notes_pk_1; Type: CONSTRAINT; Schema: deadlines; Owner: admin
--

ALTER TABLE ONLY deadlines.asbestos_advice_account_details
    ADD CONSTRAINT rspp_notes_pk_1 PRIMARY KEY (fiscalcode);


--
-- TOC entry 2921 (class 2606 OID 16668)
-- Name: rspp rspp_pk; Type: CONSTRAINT; Schema: deadlines; Owner: admin
--

ALTER TABLE ONLY deadlines.rspp
    ADD CONSTRAINT rspp_pk PRIMARY KEY (rspp_jobid, jobstart);


--
-- TOC entry 2923 (class 2606 OID 16673)
-- Name: invoices invoices_pk; Type: CONSTRAINT; Schema: invoices; Owner: admin
--

ALTER TABLE ONLY invoices.invoices
    ADD CONSTRAINT invoices_pk PRIMARY KEY (invoiceid);


--
-- TOC entry 2935 (class 2606 OID 16612)
-- Name: job_types job_type_pk; Type: CONSTRAINT; Schema: jobs; Owner: admin
--

ALTER TABLE ONLY jobs.job_types
    ADD CONSTRAINT job_type_pk PRIMARY KEY (category, types);


--
-- TOC entry 2937 (class 2606 OID 16786)
-- Name: job_types job_types_un; Type: CONSTRAINT; Schema: jobs; Owner: admin
--

ALTER TABLE ONLY jobs.job_types
    ADD CONSTRAINT job_types_un UNIQUE (types);


--
-- TOC entry 2933 (class 2606 OID 16599)
-- Name: jobs_categories jobs_categories_pk; Type: CONSTRAINT; Schema: jobs; Owner: admin
--

ALTER TABLE ONLY jobs.jobs_categories
    ADD CONSTRAINT jobs_categories_pk PRIMARY KEY (categories);


--
-- TOC entry 2943 (class 2606 OID 16718)
-- Name: jobs_pa jobs_pa_pk; Type: CONSTRAINT; Schema: jobs; Owner: admin
--

ALTER TABLE ONLY jobs.jobs_pa
    ADD CONSTRAINT jobs_pa_pk PRIMARY KEY (jobs_id);


--
-- TOC entry 2939 (class 2606 OID 16625)
-- Name: jobs jobs_pk; Type: CONSTRAINT; Schema: jobs; Owner: admin
--

ALTER TABLE ONLY jobs.jobs
    ADD CONSTRAINT jobs_pk PRIMARY KEY (jobs_id);


--
-- TOC entry 2961 (class 2606 OID 16566)
-- Name: accounts_contact accounts_contact_fk; Type: FK CONSTRAINT; Schema: accounts; Owner: admin
--

ALTER TABLE ONLY accounts.accounts_contact
    ADD CONSTRAINT accounts_contact_fk FOREIGN KEY (description) REFERENCES accounts.accounts_contact_categories(categories) ON UPDATE CASCADE;


--
-- TOC entry 2962 (class 2606 OID 16587)
-- Name: accounts_fiscaldata accounts_fiscaldata_fk; Type: FK CONSTRAINT; Schema: accounts; Owner: admin
--

ALTER TABLE ONLY accounts.accounts_fiscaldata
    ADD CONSTRAINT accounts_fiscaldata_fk FOREIGN KEY (fiscalcode) REFERENCES accounts.accounts(fiscalcode) ON UPDATE CASCADE;


--
-- TOC entry 2958 (class 2606 OID 16547)
-- Name: accounts accounts_fk; Type: FK CONSTRAINT; Schema: accounts; Owner: admin
--

ALTER TABLE ONLY accounts.accounts
    ADD CONSTRAINT accounts_fk FOREIGN KEY (customer_category) REFERENCES accounts.accounts_categories(categories) ON UPDATE CASCADE;


--
-- TOC entry 2972 (class 2606 OID 16912)
-- Name: asbestos_advice asbestos_advice_fk; Type: FK CONSTRAINT; Schema: deadlines; Owner: admin
--

ALTER TABLE ONLY deadlines.asbestos_advice
    ADD CONSTRAINT asbestos_advice_fk FOREIGN KEY (jobid) REFERENCES jobs.jobs(jobs_id) ON UPDATE CASCADE;


--
-- TOC entry 2973 (class 2606 OID 16941)
-- Name: asbestos_advice_invoices asbestos_advice_invoices_fk; Type: FK CONSTRAINT; Schema: deadlines; Owner: admin
--

ALTER TABLE ONLY deadlines.asbestos_advice_invoices
    ADD CONSTRAINT asbestos_advice_invoices_fk FOREIGN KEY (job_id, job_start) REFERENCES deadlines.asbestos_advice(jobid, jobstart) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- TOC entry 2974 (class 2606 OID 16946)
-- Name: asbestos_advice_invoices asbestos_advice_invoices_fk_1; Type: FK CONSTRAINT; Schema: deadlines; Owner: admin
--

ALTER TABLE ONLY deadlines.asbestos_advice_invoices
    ADD CONSTRAINT asbestos_advice_invoices_fk_1 FOREIGN KEY (invoice_id) REFERENCES invoices.invoices(invoiceid) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- TOC entry 2975 (class 2606 OID 16957)
-- Name: noise_vib_eval noise_vib_eval_fk; Type: FK CONSTRAINT; Schema: deadlines; Owner: admin
--

ALTER TABLE ONLY deadlines.noise_vib_eval
    ADD CONSTRAINT noise_vib_eval_fk FOREIGN KEY (customer) REFERENCES accounts.accounts(fiscalcode) ON UPDATE CASCADE;


--
-- TOC entry 2976 (class 2606 OID 16962)
-- Name: noise_vib_eval noise_vib_eval_job_fk; Type: FK CONSTRAINT; Schema: deadlines; Owner: admin
--

ALTER TABLE ONLY deadlines.noise_vib_eval
    ADD CONSTRAINT noise_vib_eval_job_fk FOREIGN KEY (job) REFERENCES jobs.jobs(jobs_id) ON UPDATE CASCADE;


--
-- TOC entry 2959 (class 2606 OID 16646)
-- Name: rspp rspp_fk; Type: FK CONSTRAINT; Schema: deadlines; Owner: admin
--

ALTER TABLE ONLY deadlines.rspp
    ADD CONSTRAINT rspp_fk FOREIGN KEY (rspp_jobid) REFERENCES jobs.jobs(jobs_id) ON UPDATE CASCADE;


--
-- TOC entry 2969 (class 2606 OID 16750)
-- Name: rspp_invoices rspp_invoices_fk; Type: FK CONSTRAINT; Schema: deadlines; Owner: admin
--

ALTER TABLE ONLY deadlines.rspp_invoices
    ADD CONSTRAINT rspp_invoices_fk FOREIGN KEY (rspp_id, rspp_start) REFERENCES deadlines.rspp(rspp_jobid, jobstart) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- TOC entry 2970 (class 2606 OID 16755)
-- Name: rspp_invoices rspp_invoices_fk_1; Type: FK CONSTRAINT; Schema: deadlines; Owner: admin
--

ALTER TABLE ONLY deadlines.rspp_invoices
    ADD CONSTRAINT rspp_invoices_fk_1 FOREIGN KEY (invoice_id) REFERENCES invoices.invoices(invoiceid) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- TOC entry 2971 (class 2606 OID 16774)
-- Name: rspp_invoices_months rspp_invoices_months_fk; Type: FK CONSTRAINT; Schema: deadlines; Owner: admin
--

ALTER TABLE ONLY deadlines.rspp_invoices_months
    ADD CONSTRAINT rspp_invoices_months_fk FOREIGN KEY (customer) REFERENCES accounts.accounts(fiscalcode) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- TOC entry 2967 (class 2606 OID 16659)
-- Name: rssp_account_details rspp_notes_fk; Type: FK CONSTRAINT; Schema: deadlines; Owner: admin
--

ALTER TABLE ONLY deadlines.rssp_account_details
    ADD CONSTRAINT rspp_notes_fk FOREIGN KEY (fiscalcode) REFERENCES accounts.accounts(fiscalcode) ON UPDATE CASCADE;


--
-- TOC entry 2960 (class 2606 OID 16701)
-- Name: invoices invoices_fk; Type: FK CONSTRAINT; Schema: invoices; Owner: admin
--

ALTER TABLE ONLY invoices.invoices
    ADD CONSTRAINT invoices_fk FOREIGN KEY (type) REFERENCES accounts.accounts_categories(categories) ON UPDATE CASCADE;


--
-- TOC entry 2964 (class 2606 OID 16631)
-- Name: jobs accounts_fk; Type: FK CONSTRAINT; Schema: jobs; Owner: admin
--

ALTER TABLE ONLY jobs.jobs
    ADD CONSTRAINT accounts_fk FOREIGN KEY (customer) REFERENCES accounts.accounts(fiscalcode) ON UPDATE CASCADE;


--
-- TOC entry 2965 (class 2606 OID 16636)
-- Name: jobs categories_fk; Type: FK CONSTRAINT; Schema: jobs; Owner: admin
--

ALTER TABLE ONLY jobs.jobs
    ADD CONSTRAINT categories_fk FOREIGN KEY (jobs_category) REFERENCES jobs.jobs_categories(categories) ON UPDATE CASCADE;


--
-- TOC entry 2963 (class 2606 OID 16606)
-- Name: job_types job_type_fk; Type: FK CONSTRAINT; Schema: jobs; Owner: admin
--

ALTER TABLE ONLY jobs.job_types
    ADD CONSTRAINT job_type_fk FOREIGN KEY (category) REFERENCES jobs.jobs_categories(categories) ON UPDATE CASCADE;


--
-- TOC entry 2968 (class 2606 OID 16712)
-- Name: jobs_pa jobs_pa_fk; Type: FK CONSTRAINT; Schema: jobs; Owner: admin
--

ALTER TABLE ONLY jobs.jobs_pa
    ADD CONSTRAINT jobs_pa_fk FOREIGN KEY (jobs_id) REFERENCES jobs.jobs(jobs_id) ON UPDATE CASCADE;


--
-- TOC entry 2966 (class 2606 OID 16641)
-- Name: jobs type_fk; Type: FK CONSTRAINT; Schema: jobs; Owner: admin
--

ALTER TABLE ONLY jobs.jobs
    ADD CONSTRAINT type_fk FOREIGN KEY (jobs_category, jobs_type) REFERENCES jobs.job_types(category, types) ON UPDATE CASCADE;


--
-- TOC entry 3114 (class 0 OID 0)
-- Dependencies: 5
-- Name: SCHEMA accounts; Type: ACL; Schema: -; Owner: admin
--

GRANT USAGE ON SCHEMA accounts TO dclient;
GRANT USAGE ON SCHEMA accounts TO "user";
GRANT USAGE ON SCHEMA accounts TO mod;
GRANT USAGE ON SCHEMA accounts TO script;


--
-- TOC entry 3115 (class 0 OID 0)
-- Dependencies: 6
-- Name: SCHEMA deadlines; Type: ACL; Schema: -; Owner: admin
--

GRANT USAGE ON SCHEMA deadlines TO dclient;
GRANT USAGE ON SCHEMA deadlines TO "user";
GRANT USAGE ON SCHEMA deadlines TO mod;
GRANT USAGE ON SCHEMA deadlines TO script;


--
-- TOC entry 3116 (class 0 OID 0)
-- Dependencies: 9
-- Name: SCHEMA invoices; Type: ACL; Schema: -; Owner: admin
--

GRANT USAGE ON SCHEMA invoices TO dclient;
GRANT USAGE ON SCHEMA invoices TO "user";
GRANT USAGE ON SCHEMA invoices TO mod;


--
-- TOC entry 3117 (class 0 OID 0)
-- Dependencies: 10
-- Name: SCHEMA jobs; Type: ACL; Schema: -; Owner: admin
--

GRANT USAGE ON SCHEMA jobs TO dclient;
GRANT USAGE ON SCHEMA jobs TO "user";
GRANT USAGE ON SCHEMA jobs TO mod;
GRANT USAGE ON SCHEMA jobs TO script;


--
-- TOC entry 3118 (class 0 OID 0)
-- Dependencies: 205
-- Name: TABLE accounts; Type: ACL; Schema: accounts; Owner: admin
--

GRANT SELECT,INSERT,UPDATE ON TABLE accounts.accounts TO dclient;
GRANT SELECT ON TABLE accounts.accounts TO script;
GRANT SELECT ON TABLE accounts.accounts TO "user";
GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE accounts.accounts TO mod;


--
-- TOC entry 3119 (class 0 OID 0)
-- Dependencies: 208
-- Name: TABLE accounts_categories; Type: ACL; Schema: accounts; Owner: admin
--

GRANT SELECT ON TABLE accounts.accounts_categories TO dclient;
GRANT SELECT ON TABLE accounts.accounts_categories TO script;
GRANT SELECT ON TABLE accounts.accounts_categories TO "user";
GRANT SELECT,UPDATE ON TABLE accounts.accounts_categories TO mod;


--
-- TOC entry 3120 (class 0 OID 0)
-- Dependencies: 209
-- Name: TABLE accounts_contact; Type: ACL; Schema: accounts; Owner: admin
--

GRANT SELECT,INSERT,UPDATE ON TABLE accounts.accounts_contact TO dclient;
GRANT SELECT ON TABLE accounts.accounts_contact TO script;
GRANT SELECT ON TABLE accounts.accounts_contact TO "user";
GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE accounts.accounts_contact TO mod;


--
-- TOC entry 3121 (class 0 OID 0)
-- Dependencies: 210
-- Name: TABLE accounts_contact_categories; Type: ACL; Schema: accounts; Owner: admin
--

GRANT SELECT ON TABLE accounts.accounts_contact_categories TO dclient;
GRANT SELECT ON TABLE accounts.accounts_contact_categories TO script;
GRANT SELECT ON TABLE accounts.accounts_contact_categories TO "user";
GRANT SELECT,UPDATE ON TABLE accounts.accounts_contact_categories TO mod;


--
-- TOC entry 3122 (class 0 OID 0)
-- Dependencies: 211
-- Name: TABLE accounts_fiscaldata; Type: ACL; Schema: accounts; Owner: admin
--

GRANT SELECT,INSERT,UPDATE ON TABLE accounts.accounts_fiscaldata TO dclient;
GRANT SELECT ON TABLE accounts.accounts_fiscaldata TO script;
GRANT SELECT ON TABLE accounts.accounts_fiscaldata TO "user";
GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE accounts.accounts_fiscaldata TO mod;


--
-- TOC entry 3123 (class 0 OID 0)
-- Dependencies: 225
-- Name: TABLE asbestos_advice; Type: ACL; Schema: deadlines; Owner: admin
--

GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE deadlines.asbestos_advice TO mod;


--
-- TOC entry 3124 (class 0 OID 0)
-- Dependencies: 223
-- Name: TABLE asbestos_advice_account_details; Type: ACL; Schema: deadlines; Owner: admin
--

GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE deadlines.asbestos_advice_account_details TO mod;


--
-- TOC entry 3125 (class 0 OID 0)
-- Dependencies: 226
-- Name: TABLE asbestos_advice_invoices; Type: ACL; Schema: deadlines; Owner: admin
--

GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE deadlines.asbestos_advice_invoices TO mod;


--
-- TOC entry 3126 (class 0 OID 0)
-- Dependencies: 224
-- Name: TABLE asbestos_advice_invoices_months; Type: ACL; Schema: deadlines; Owner: admin
--

GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE deadlines.asbestos_advice_invoices_months TO mod;


--
-- TOC entry 3127 (class 0 OID 0)
-- Dependencies: 206
-- Name: TABLE rspp; Type: ACL; Schema: deadlines; Owner: admin
--

GRANT SELECT,INSERT,UPDATE ON TABLE deadlines.rspp TO dclient;
GRANT SELECT ON TABLE deadlines.rspp TO script;
GRANT SELECT ON TABLE deadlines.rspp TO "user";
GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE deadlines.rspp TO mod;


--
-- TOC entry 3128 (class 0 OID 0)
-- Dependencies: 218
-- Name: TABLE rspp_invoices; Type: ACL; Schema: deadlines; Owner: admin
--

GRANT SELECT,INSERT,UPDATE ON TABLE deadlines.rspp_invoices TO dclient;
GRANT SELECT ON TABLE deadlines.rspp_invoices TO "user";
GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE deadlines.rspp_invoices TO mod;


--
-- TOC entry 3129 (class 0 OID 0)
-- Dependencies: 219
-- Name: TABLE rspp_invoices_months; Type: ACL; Schema: deadlines; Owner: admin
--

GRANT SELECT,INSERT,UPDATE ON TABLE deadlines.rspp_invoices_months TO dclient;
GRANT SELECT ON TABLE deadlines.rspp_invoices_months TO "user";
GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE deadlines.rspp_invoices_months TO mod;


--
-- TOC entry 3130 (class 0 OID 0)
-- Dependencies: 215
-- Name: TABLE rssp_account_details; Type: ACL; Schema: deadlines; Owner: admin
--

GRANT SELECT,INSERT,UPDATE ON TABLE deadlines.rssp_account_details TO dclient;
GRANT SELECT ON TABLE deadlines.rssp_account_details TO script;
GRANT SELECT,INSERT,UPDATE ON TABLE deadlines.rssp_account_details TO "user";
GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE deadlines.rssp_account_details TO mod;


--
-- TOC entry 3131 (class 0 OID 0)
-- Dependencies: 214
-- Name: TABLE jobs; Type: ACL; Schema: jobs; Owner: admin
--

GRANT SELECT,INSERT,UPDATE ON TABLE jobs.jobs TO dclient;
GRANT SELECT ON TABLE jobs.jobs TO script;
GRANT SELECT ON TABLE jobs.jobs TO "user";
GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE jobs.jobs TO mod;


--
-- TOC entry 3132 (class 0 OID 0)
-- Dependencies: 216
-- Name: TABLE jobs_pa; Type: ACL; Schema: jobs; Owner: admin
--

GRANT SELECT,INSERT,UPDATE ON TABLE jobs.jobs_pa TO dclient;
GRANT SELECT ON TABLE jobs.jobs_pa TO script;
GRANT SELECT ON TABLE jobs.jobs_pa TO "user";
GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE jobs.jobs_pa TO mod;


--
-- TOC entry 3133 (class 0 OID 0)
-- Dependencies: 228
-- Name: TABLE expired_without_invoice; Type: ACL; Schema: deadlines; Owner: admin
--

GRANT SELECT ON TABLE deadlines.expired_without_invoice TO mod;
GRANT SELECT ON TABLE deadlines.expired_without_invoice TO script;
GRANT SELECT ON TABLE deadlines.expired_without_invoice TO "user";


--
-- TOC entry 3134 (class 0 OID 0)
-- Dependencies: 229
-- Name: TABLE current_month_invoices; Type: ACL; Schema: deadlines; Owner: admin
--

GRANT SELECT ON TABLE deadlines.current_month_invoices TO mod;
GRANT SELECT ON TABLE deadlines.current_month_invoices TO script;
GRANT SELECT ON TABLE deadlines.current_month_invoices TO "user";


--
-- TOC entry 3135 (class 0 OID 0)
-- Dependencies: 222
-- Name: TABLE expiring_next_60_days; Type: ACL; Schema: deadlines; Owner: admin
--

GRANT SELECT ON TABLE deadlines.expiring_next_60_days TO "user";
GRANT SELECT ON TABLE deadlines.expiring_next_60_days TO mod;
GRANT SELECT ON TABLE deadlines.expiring_next_60_days TO script;


--
-- TOC entry 3136 (class 0 OID 0)
-- Dependencies: 221
-- Name: TABLE missing_invoice_month_customer; Type: ACL; Schema: deadlines; Owner: admin
--

GRANT SELECT ON TABLE deadlines.missing_invoice_month_customer TO "user";
GRANT SELECT ON TABLE deadlines.missing_invoice_month_customer TO mod;
GRANT SELECT ON TABLE deadlines.missing_invoice_month_customer TO script;


--
-- TOC entry 3137 (class 0 OID 0)
-- Dependencies: 227
-- Name: TABLE noise_vib_eval; Type: ACL; Schema: deadlines; Owner: admin
--

GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE deadlines.noise_vib_eval TO mod;


--
-- TOC entry 3139 (class 0 OID 0)
-- Dependencies: 220
-- Name: TABLE rspp_not_updated; Type: ACL; Schema: deadlines; Owner: admin
--

GRANT SELECT ON TABLE deadlines.rspp_not_updated TO "user";
GRANT SELECT ON TABLE deadlines.rspp_not_updated TO mod;
GRANT SELECT ON TABLE deadlines.rspp_not_updated TO script;


--
-- TOC entry 3140 (class 0 OID 0)
-- Dependencies: 207
-- Name: TABLE invoices; Type: ACL; Schema: invoices; Owner: admin
--

GRANT SELECT,INSERT,UPDATE ON TABLE invoices.invoices TO dclient;
GRANT SELECT ON TABLE invoices.invoices TO script;
GRANT SELECT ON TABLE invoices.invoices TO "user";
GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE invoices.invoices TO mod;


--
-- TOC entry 3141 (class 0 OID 0)
-- Dependencies: 217
-- Name: TABLE "PA datas"; Type: ACL; Schema: jobs; Owner: admin
--

GRANT SELECT ON TABLE jobs."PA datas" TO mod;


--
-- TOC entry 3142 (class 0 OID 0)
-- Dependencies: 213
-- Name: TABLE job_types; Type: ACL; Schema: jobs; Owner: admin
--

GRANT SELECT ON TABLE jobs.job_types TO dclient;
GRANT SELECT ON TABLE jobs.job_types TO script;
GRANT SELECT ON TABLE jobs.job_types TO "user";
GRANT SELECT,UPDATE ON TABLE jobs.job_types TO mod;


--
-- TOC entry 3143 (class 0 OID 0)
-- Dependencies: 212
-- Name: TABLE jobs_categories; Type: ACL; Schema: jobs; Owner: admin
--

GRANT SELECT ON TABLE jobs.jobs_categories TO dclient;
GRANT SELECT ON TABLE jobs.jobs_categories TO script;
GRANT SELECT ON TABLE jobs.jobs_categories TO "user";
GRANT SELECT,UPDATE ON TABLE jobs.jobs_categories TO mod;


-- Completed on 2021-05-18 17:25:01

--
-- PostgreSQL database dump complete
--

