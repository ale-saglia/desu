#!/usr/bin/python

import locale
import calendar

import psycopg2

import argparse

import smtplib
from email.message import EmailMessage

import yaml
from pathlib import Path
from datetime import datetime

global cfg


def main():
    parser = argparse.ArgumentParser(
        description=
        'A server side script to create a mail notification for DESU')
    parser.add_argument(
        '-m',
        action='store',
        dest='module',
        help='Modules you want to run with the script',
    )
    parser.add_argument(
        '-e',
        action='append',
        dest='mailAddresses',
        default=[],
        help='All the email you want the result to be sented to',
    )

    if parser.parse_args().module == "accounting":
        mailSender(accountingModule(parser.parse_args().mailAddresses))
    elif parser.parse_args().module == "management":
        mailSender(managementModule(parser.parse_args().mailAddresses))
    else:
        print("Invalid or missing module name")


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


def accountingModule(emailAdresses):
    conn = connect()

    subject = "Incarichi da fatturare per il mese di " + calendar.month_name[
        datetime.now().month].capitalize()
    msg = ""

    #Create message portion for regular current Invoice
    invoiceInMonth = getInvoiceForCurrentMonth(conn)
    if invoiceInMonth:
        msg += "Ecco gli incarichi da fatturare durante questo mese"
        for row in invoiceInMonth:
            msg += "\nRagione sociale: " + row[0]
            msg += "\n"
            msg += "Scadenza " + row[1].strftime('%d/%m/%Y')
            msg += "\n"
            if (row[2] != None):
                msg += "Note: " + row[2]
    msg = msg.strip()

    #Create message portion for rspp of previous months without an invoice yet
    expiredRSPP = getJobExpiredWithoutInvoice(conn)
    if expiredRSPP:
        msg += "\n\nSono state inoltre trovati i seguenti incarichi scaduti e senza fattura\n"
        for row in expiredRSPP:
            msg += "Ragione sociale: " + row[0]
            msg += "\n"
            msg += "Scadenza " + row[1].strftime('%d/%m/%Y')
            msg += "\n"
            if (row[2] != None):
                msg += "Note: " + row[2] + "\n"
            msg += "\n"
    msg = msg.strip()

    print(subject)
    print(msg)

    if (msg != ""):
        mail = EmailMessage()
        mail.set_content(msg)
        mail['Subject'] = subject

        mail['From'] = cfg["Email"]["mailFrom"]
        mail['To'] = emailAdresses
        return mail
    else:
        exit()


def managementModule(emailAdresses):
    conn = connect()

    subject = "Incarichi in scadenza nei prossimi " + str(
        cfg["General"]["daysAdvance"]) + " giorni."
    msg = ""

    expiringRSPP = getJobInDealine(conn)
    if expiringRSPP:
        msg += "I seguenti incarichi sono scaduti o scadranno nei prossimi " + str(
            cfg["General"]["daysAdvance"]) + " giorni\n\n"
        for row in expiringRSPP:
            msg += "Ragione sociale: " + row[0]
            msg += "\n"
            msg += "Scadenza " + row[1].strftime('%d/%m/%Y')
            msg += "\n"
            if (row[2] != None):
                msg += "Note: " + row[2] + "\n"
            msg += "\n"
    msg = msg.strip()

    if (msg != ""):
        mail = EmailMessage()
        mail.set_content(msg)
        mail['Subject'] = subject

        mail['From'] = cfg["Email"]["mailFrom"]
        mail['To'] = emailAdresses
        return mail
    else:
        exit()


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
            "select a.\"name\", r.jobend, rad.notes from deadlines.rspp r, jobs.jobs j, accounts.accounts a left join deadlines.rssp_account_details rad on rad.fiscalcode = a.fiscalcode where r.rspp_jobid = j.jobs_id and j.customer = a.fiscalcode and r.jobend <= now() and not exists ( select 1 from deadlines.rspp_invoices ri where r.rspp_jobid = ri.rspp_id and r.jobstart = ri.rspp_start) and a.fiscalcode not in ( select customer from deadlines.rspp_invoices_months rim where extract (month from CURRENT_DATE) = any(rim.months)) "
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
        print("Mail sent")
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
