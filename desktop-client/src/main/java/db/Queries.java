package db;

public class Queries {

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