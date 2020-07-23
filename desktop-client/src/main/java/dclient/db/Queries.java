package dclient.db;

public class Queries {

    public static String getRSPPtable() {
        return (
            "SELECT *\r\n" + 
            "FROM   (SELECT r.rspp_jobid AS jobid,\r\n" + 
            "               r.jobstart   AS jobstart,\r\n" + 
            "               r.jobstart   AS jobend,\r\n" + 
            "               a.fiscalcode AS fiscalcode,\r\n" + 
            "               a.customer_category  as category,\r\n" + 
            "               a.\"name\"     AS \"name\",\r\n" + 
            "               r.invoiceid  AS invoiceid,\r\n" + 
            "               i.payed      AS payed\r\n" + 
            "        FROM   accounts.accounts a,\r\n" + 
            "               deadlines.rspp r,\r\n" + 
            "               invoices.invoices i,\r\n" + 
            "               jobs.jobs j\r\n" + 
            "        WHERE  r.invoiceid = i.invoiceid\r\n" + 
            "        UNION\r\n" + 
            "        SELECT r.rspp_jobid AS jobid,\r\n" + 
            "               r.jobstart   AS jobstart,\r\n" + 
            "               r.jobstart   AS jobend,\r\n" + 
            "               a.fiscalcode AS fiscalcode,\r\n" + 
            "               a.customer_category  as category,\r\n" + 
            "               a.\"name\"     AS \"name\",\r\n" + 
            "               NULL         AS invoiceid,\r\n" + 
            "               false        AS payed\r\n" + 
            "        FROM   accounts.accounts a,\r\n" + 
            "               deadlines.rspp r,\r\n" + 
            "               jobs.jobs j\r\n" + 
            "        WHERE  r.invoiceid IS NULL) AS tab\r\n" + 
            "       FULL OUTER JOIN deadlines.rspp_notes rn\r\n" + 
            "                    ON tab.fiscalcode = rn.fiscalcode\r\n" + 
            "ORDER  BY \"name\", jobend  " 
        );
    }
}