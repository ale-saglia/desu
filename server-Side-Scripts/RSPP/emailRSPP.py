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

        query = ("select tab.\"name\", tab.jobend, tab.invoiceid, tab.notes from (select a.\"name\" as name, a.fiscalcode as fiscalcode , r.jobend as jobend, rn.notes as notes, r.invoiceid from accounts.accounts a, jobs.jobs j, deadlines.rspp r, deadlines.rspp_notes rn where r.rspp_jobid = j.jobs_id and j.customer = a.fiscalcode and r.jobend between current_date and current_date + interval '" +
                 str(cfg["General"]["daysAdvance"]) + "' and r.invoiceid is null ) as tab left join deadlines.rspp_notes rn on rn.fiscalcode = tab.fiscalcode")
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

        query = ("select tab.\"name\", tab.jobend, tab.notes from (select a.\"name\" as name, a.fiscalcode as fiscalcode , r.jobend as jobend, rn.notes as notes from accounts.accounts a, jobs.jobs j, deadlines.rspp r, deadlines.rspp_notes rn where r.rspp_jobid = j.jobs_id and j.customer = a.fiscalcode and r.jobend < current_date and r.invoiceid is null ) as tab left join deadlines.rspp_notes rn on rn.fiscalcode = tab.fiscalcode ")
        cursor.execute(query)

        # Row 0 should contain account name and row 1 should contains the deadline
        results = list(cursor.fetchall())

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
            str(cfg["General"]["daysAdvance"]) + " giorni"
        for row in resultSet:
            msg += "Ragione sociale: " + row[0]
            msg += "\n"
            msg += "Scadenza" + row[1].strftime('%d/%m/%Y')
            msg += "\n"
            msg += "Codice fattura: " + row[2]
            msg += "\n"
            msg += "Note: " + row[3]
            msg += "\n\n"
    msg = msg.strip()

    resultSet = getJobExpiredWithoutInvoice(conn)
    if resultSet:
        if (msg != ""):
            msg += "\n\n"

        msg += "Sono stati trovati i seguenti incarichi scaduti e privi di indicazioni sulla fattura\n\n"
        for row in resultSet:
            msg += "Ragione sociale: " + row[0]
            msg += "\n"
            msg += "Scaduto il " + row[1].strftime('%d/%m/%Y')
            msg += "\n"
            msg += "Note: " + row[2]
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
    try:
        server = smtplib.SMTP_SSL(
            cfg["Email"]["smtpServer"], cfg["Email"]["smtpPort"])
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
