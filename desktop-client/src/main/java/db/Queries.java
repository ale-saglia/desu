package db;

public class Queries {

    /*
     * Query to return elements for RSPP gui select * from (select r.rspp_jobid as
     * jobid, r.jobstart as jobstart, r.jobstart as jobend, a."name" as "name",
     * a.customer_category as category, r.invoiceid as invoiceid, i.payed as payed
     * from accounts.accounts a, deadlines.rspp r, invoices.invoices i, jobs.jobs j
     * where r.invoiceid = i.invoiceid union select r.rspp_jobid as jobid,
     * r.jobstart as jobstart, r.jobstart as jobend, a."name" as "name",
     * a.customer_category as category, null as invoiceid, false as payed from
     * accounts.accounts a, deadlines.rspp r, jobs.jobs j where r.invoiceid is null)
     * as tab ORDER BY "name", jobend
     */
    public static String getRSPPtable() {
        return (
            "   SELECT *  "  + 
            "   FROM   (SELECT r.rspp_jobid        AS jobid,  "  + 
            "                  r.jobstart          AS jobstart,  "  + 
            "                  r.jobstart          AS jobend,  "  + 
            "                  a.\"name\"          AS \"name\",  "  + 
            "                  a.customer_category AS category,  "  + 
            "                  r.invoiceid         AS invoiceid,  "  + 
            "                  i.payed             AS payed  "  + 
            "           FROM   accounts.accounts a,  "  + 
            "                  deadlines.rspp r,  "  + 
            "                  invoices.invoices i,  "  + 
            "                  jobs.jobs j  "  + 
            "           WHERE  r.invoiceid = i.invoiceid  "  + 
            "           UNION  "  + 
            "           SELECT r.rspp_jobid        AS jobid,  "  + 
            "                  r.jobstart          AS jobstart,  "  + 
            "                  r.jobstart          AS jobend,  "  + 
            "                  a.\"name\"          AS \"name\",  "  + 
            "                  a.customer_category AS category,  "  + 
            "                  NULL                AS invoiceid,  "  + 
            "                  false               AS payed  "  + 
            "           FROM   accounts.accounts a,  "  + 
            "                  deadlines.rspp r,  "  + 
            "                  jobs.jobs j  "  + 
            "           WHERE  r.invoiceid IS NULL) AS tab  "  + 
            "   ORDER  BY \"name\",  "  + 
            "            jobend    " 
        );
    }

}