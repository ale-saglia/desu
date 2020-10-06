#!/usr/bin/python

import locale
import calendar

import psycopg2

import smtplib
from email.message import EmailMessage

import yaml
from pathlib import Path
from datetime import datetime

global cfg


def main():
    mailSender(accountModule())


def connect():
    # Connect to the PostgreSQL database server
    conn = None
    try:
        print('Connecting to the PostgreSQL database...')
        param = "host='" + cfg["Database"]["host"] + "' "
        param += "dbname='" + cfg["Database"]["database"] + "' "
        param += "user='" + cfg["Database"]["user"] + "' "
        param += "password='" + cfg["Database"]["password"] + "'"

        conn = psycopg2.connect(param)
    except (Exception, psycopg2.DatabaseError) as error:
        print(error)
    finally:
        return conn


def accountModule():
    conn = connect()

    subject = "Incarichi da fatturare per il mese di " + calendar.month_name[
        datetime.now().month].capitalize()
    msg = ""

    #Create message portion for regular current Invoice
    invoiceInMonth = getInvoiceForCurrentMonth(conn)
    if invoiceInMonth:
        msg += "Ecco gli incarichi da fatturare durante questo mese\n"
        for row in invoiceInMonth:
            msg += "Ragione sociale: " + row[0]
            msg += "\n"
            msg += "Scadenza " + row[1].strftime('%d/%m/%Y')
            msg += "\n"
            if (row[2] != None):
                msg += "Note: " + row[2]
    msg = msg.strip()

    #Create message portion for rspp of previous months without an invoice yet
    expiredRSPP = getJobExpiredWithoutInvoice(conn)
    if invoiceInMonth:
        msg += "\n\nSono state inoltre trovati i seguenti incarichi scaduti e senza fattura\n"
        for row in invoiceInMonth:
            msg += "Ragione sociale: " + row[0]
            msg += "\n"
            msg += "Scadenza " + row[1].strftime('%d/%m/%Y')
            msg += "\n"
            if (row[2] != None):
                msg += "Note: " + row[2]
            msg += "\n\n"
    msg = msg.strip()

    print(subject)
    print(msg)

    mail = EmailMessage()
    mail.set_content(msg)
    mail['Subject'] = subject

    message['From'] = cfg["Email"]["mailFrom"]
    message['To'] = cfg["Email"]["mailTo"]

    return mail


def getInvoiceForCurrentMonth(conn):
    try:
        if conn is None:
            return
        cursor = conn.cursor()

        query = (
            "select a.\"name\" as \"Ragione Sociale\", r.jobend as Scadenza, rad.notes as Note from deadlines.rspp r, deadlines.rspp_invoices_months rim, jobs.jobs j, accounts.accounts a left join deadlines.rssp_account_details rad on rad.fiscalcode = a.fiscalcode where r.rspp_jobid = j.jobs_id and a.fiscalcode = j.customer and j.customer = rim.customer and r.jobstart < now() and r.jobend > now() and extract (month from CURRENT_DATE) = any(rim.months) and rad.expired is not true order by a.\"name\" "
        )
        cursor.execute(query)

        # Row 0 should contain account name, row 1 should contains the deadline and row 2 should contain the notes or NULL if the note was not found
        results = list(cursor.fetchall())

        if results is not None:
            return results
        else:
            return

    except psycopg2.Error as error:
        print("Failed to read data from table", error)


def getJobInDealine(conn):
    try:
        if conn is None:
            return
        cursor = conn.cursor()

        query = (
            "select main.\"name\", main.job_end, rad.notes from ( select a.\"name\", a.fiscalcode , max(r.jobend) as job_end from accounts.accounts a, jobs.jobs j, deadlines.rspp r where j.customer = a.fiscalcode and j.jobs_id = r.rspp_jobid and r.jobend < (now() + interval '"
            + str(cfg["General"]["daysAdvance"]) +
            "' day) and not (a.name in ( select a.name from accounts.accounts a, jobs.jobs j, deadlines.rspp r where r.jobend > (now() + interval '"
            + str(cfg["General"]["daysAdvance"]) +
            "' day) and j.jobs_id = r.rspp_jobid and a.fiscalcode = j.customer)) and not (a.fiscalcode in ( select rn.fiscalcode from deadlines.rssp_account_details rn where rn.expired is true)) group by a.name, a.fiscalcode order by a.name) as main left join deadlines.rssp_account_details rad on rad.fiscalcode = main.fiscalcode "
        )
        cursor.execute(query)

        # Row 0 should contain account name and row 1 should contains the deadline
        results = list(cursor.fetchall())

        if results is not None:
            return results
        else:
            return

    except psycopg2.Error as error:
        print("Failed to read data from table", error)


def getJobExpiredWithoutInvoice(conn):
    try:
        if conn is None:
            return
        cursor = conn.cursor()

        query = (
            "select uni.\"name\", uni.jobend, rad.notes from( select * from ( select r.rspp_jobid, r.jobstart, r.jobend, string_agg(ri.invoice_id, ', ') as invoice_id from deadlines.rspp_invoices ri right outer join deadlines.rspp r on r.rspp_jobid = ri.rspp_id and r.jobstart = ri.rspp_start group by r.rspp_jobid, r.jobstart) as inv_data, accounts.accounts a, jobs.jobs j where a.fiscalcode = j.customer and j.jobs_id = inv_data.rspp_jobid and jobend < current_date and invoice_id isnull) as uni left join deadlines.rssp_account_details rad on rad.fiscalcode = uni.fiscalcode "
        )
        cursor.execute(query)

        # Row 0 should contain account name and row 1 should contains the deadline
        results = list(cursor.fetchall())

        if results is not None:
            return results
        else:
            return

    except psycopg2.Error as error:
        print("Failed to read data from table", error)


def mailSender(message):
    print(message)
    try:
        server = smtplib.SMTP(cfg["Email"]["smtpServer"],
                              cfg["Email"]["smtpPort"])
        server.ehlo()
        server.starttls()
        server.ehlo
        server.login(cfg["Email"]["user"], cfg["Email"]["password"])

        server.send_message(message)
        print("Mail sent to " + cfg["Email"]["mailTo"])
    except:
        print('Something went wrong...')
        return
    finally:
        server.quit()


if __name__ == "__main__":
    confPath = Path(__file__).resolve().parents[1] / 'conf.yaml'
    with open(confPath, 'r') as f:
        cfg = yaml.safe_load(f)

    locale.setlocale(locale.LC_ALL, cfg["General"]["locale"])
    main()
