select
	jobid,
	jobstart,
	jobend,
	"name",
	"descriptor",
	customer_category as category,
	invoice_id as invoices,
	payed,
	notes
from
	(
	select
		r.rspp_jobid as jobid, r.jobstart, r.jobend, invoice_id, (payed is true) as payed
	from
		(
		select
			inv.rspp_id, inv.rspp_start, invoice_id, payed
		from
			(
			select
				ri.rspp_id , ri.rspp_start , string_agg(ri.invoice_id, ', ') as invoice_id
			from
				deadlines.rspp_invoices ri
			group by
				ri.rspp_id, ri.rspp_start) inv, (
			select
				ri.rspp_id , ri.rspp_start , bool_and(i.payed) as payed
			from
				deadlines.rspp_invoices ri, invoices.invoices i
			where
				ri.invoice_id = i.invoiceid
			group by
				ri.rspp_id , ri.rspp_start ) payed
		where
			payed.rspp_id = inv.rspp_id
			and payed.rspp_start = inv.rspp_start) as uni
	right outer join deadlines.rspp r on
		r.rspp_jobid = uni.rspp_id
		and r.jobstart = uni.rspp_start) invoiceInfo,
	jobs.jobs j,
	accounts.accounts a
left join deadlines.rssp_account_details rn on
	a.fiscalcode = rn.fiscalcode
where
	j.jobs_id = jobid
	and j.customer = a.fiscalcode
order by lower("name"), jobend desc