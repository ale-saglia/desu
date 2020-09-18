import psycopg2

import smtplib
from email.message import EmailMessage

from datetime import date, timedelta

import yaml
from pathlib import Path

global cfg


def main():
    mailText = mailComposer()
    mailSender(mailText)


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


def getJobInDealine(conn):
    try:
        if conn is None:
            return
        cursor = conn.cursor()

        query = (
            "select uni.\"name\", uni.jobend, uni.invoice_id, rn.notes from(select * from ( select r.rspp_jobid, r.jobstart, r.jobend, string_agg(ri.invoice_id, ', ') as invoice_id from deadlines.rspp_invoices ri right outer join deadlines.rspp r on r.rspp_jobid = ri.rspp_id and r.jobstart = ri.rspp_start group by r.rspp_jobid, r.jobstart) as inv_data, accounts.accounts a, jobs.jobs j where a.fiscalcode = j.customer and j.jobs_id = inv_data.rspp_jobid and jobend between current_date and current_date + interval '"
            + str(cfg["General"]["daysAdvance"]) +
            " DAYS') as uni left join deadlines.rspp_notes rn on rn.fiscalcode = uni.fiscalcode "
        )
        cursor.execute(query)

        # Row 0 should contain account name and row 1 should contains the deadline
        results = list(cursor.fetchall())

        print(results)

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
            "select uni.\"name\", uni.jobend, rn.notes from( select * from ( select r.rspp_jobid, r.jobstart, r.jobend, string_agg(ri.invoice_id, ', ') as invoice_id from deadlines.rspp_invoices ri right outer join deadlines.rspp r on r.rspp_jobid = ri.rspp_id and r.jobstart = ri.rspp_start group by r.rspp_jobid, r.jobstart) as inv_data, accounts.accounts a, jobs.jobs j where a.fiscalcode = j.customer and j.jobs_id = inv_data.rspp_jobid and jobend < current_date and invoice_id isnull) as uni left join deadlines.rspp_notes rn on rn.fiscalcode = uni.fiscalcode "
        )
        cursor.execute(query)

        # Row 0 should contain account name and row 1 should contains the deadline
        results = list(cursor.fetchall())

        print(results)

        if results is not None:
            return results
        else:
            return

    except psycopg2.Error as error:
        print("Failed to read data from table", error)


def mailComposer():
    msg = ""
    conn = connect()

    resultSet = getJobInDealine(conn)
    if resultSet:
        msg += "Ecco gli incarichi in scadenza nei prossimi " + \
            str(cfg["General"]["daysAdvance"]) + " giorni\n"
        for row in resultSet:
            msg += "Ragione sociale: " + row[0]
            msg += "\n"
            msg += "Scadenza" + row[1].strftime('%d/%m/%Y')
            msg += "\n"
            msg += "Codice fattura: " + row[2]
            msg += "\n"
            if (row[3] != None):
                msg += "Note: " + row[3]
            else:
                msg += "Note: " + ""
            msg += "\n\n"
    msg = msg.strip()

    resultSet = getJobExpiredWithoutInvoice(conn)
    if resultSet:
        if (msg != ""):
            msg += "\n\n"
        else:
            msg += "Non sono stati trovati incarichi in scadenza nel periodo in questione\n\n"

        msg += "Sono stati trovati i seguenti incarichi scaduti e privi di indicazioni sulla fattura\n"
        for row in resultSet:
            msg += "Ragione sociale: " + row[0]
            msg += "\n"
            msg += "Scaduto il " + row[1].strftime('%d/%m/%Y')
            msg += "\n"
            if (row[2] != None):
                msg += "Note: " + row[2]
            else:
                msg += "Note: " + ""
            msg += "\n\n"
    msg = msg.strip()

    if (msg == ""):
        exit()

    if (conn):
        conn.close()
        print("The database connection is closed")

    message = EmailMessage()
    message.set_content(msg)
    message['Subject'] = "RSPP in scadenza tra il " + date.today().strftime('%d/%m/%Y') + \
        " e il " + \
        (date.today() + timedelta(cfg["General"]
                                  ["daysAdvance"])).strftime('%d/%m/%Y')
    return message


def mailSender(message):
    print(message)
    try:
        server = smtplib.SMTP(cfg["Email"]["smtpServer"],
                                  cfg["Email"]["smtpPort"])
        server.ehlo()
        server.starttls()
        server.ehlo                         
        server.login(cfg["Email"]["user"], cfg["Email"]["password"])

        message['From'] = cfg["Email"]["mailFrom"]
        message['To'] = cfg["Email"]["mailTo"]

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

    main()
