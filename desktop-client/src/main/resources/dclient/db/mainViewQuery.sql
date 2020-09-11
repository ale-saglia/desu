SELECT jobid, jobstart, jobend, "name", "descriptor", category, n2.invoiceid, payed, notes
FROM   (SELECT jobid, jobstart, jobend, "name", "descriptor", category, invoiceid, notes
		from (SELECT r.rspp_jobid AS jobid, r.jobstart AS jobstart, r.jobend AS jobend, j.customer AS fiscalcode,
				a."name" AS "name", a."descriptor" AS "descriptor", a.customer_category AS category,
				r.invoiceid AS invoiceid
				from accounts.accounts a, deadlines.rspp r, jobs.jobs j
				WHERE  r.rspp_jobid = j.jobs_id AND j.customer = a.fiscalcode
				ORDER BY "name", jobstart) AS n1
		LEFT JOIN deadlines.rspp_notes rn ON n1.fiscalcode = rn.fiscalcode) AS n2
LEFT JOIN invoices.invoices i ON i.invoiceid = n2.invoiceid  ORDER BY "name", jobend DESC 