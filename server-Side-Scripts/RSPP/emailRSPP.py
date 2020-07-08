import psycopg2
import smtplib
from datetime import date, timedelta

import yaml
from pathlib import Path

global cfg

def main():
    queryResult = getJobInDealine(connect())
    if queryResult is None:
        exit()

    for row in queryResult:
        print("Name", row[0])
        print("Deadline", row[1])
        print("\n")

    msg = mailComposer(queryResult)
    mailSender(msg)

def connect():
    """ Connect to the PostgreSQL database server """
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

        query = 'SELECT a."name", r.jobend FROM deadlines.rspp r, accounts.accounts a where r.fiscalcode = a.fiscalcode and r.jobend between current_date and CURRENT_DATE + INTERVAL \'14 day\' ORDER by r.jobend asc '
        cursor.execute(query)

        # Row 0 should contain account name and row 1 should contains the deadline
        results = list(cursor.fetchall())
        
        if results is not None:
            return results
        else:
            return

    except psycopg2.Error as error:
            print("Failed to read data from table", error)
    finally:
        if (conn):
            conn.close()
            print("The database connection is closed")

def mailComposer(queryResult):
    msg = "\"\"\""
    msg += "\nSubject: RSPP in scadenza tra il " + date.today().strftime('%d/%m/%Y') + " e il " + (date.today() + timedelta(days=1)).strftime('%d/%m/%Y') + "\n\n"
    for row in queryResult:
        msg += "Ragione sociale: " + row[0]
        msg += "\n"
        msg += "Scadenza" + row[1].strftime('%d/%m/%Y')
        msg += "\n\n"

    msg += "\"\"\""
    return msg

def mailSender(message):
    try:
        server = smtplib.SMTP(cfg["Email"]["smtpServer"])
        server.ehlo()
        server.starttls()

        server.login(cfg["Email"]["user"], cfg["Email"]["password"])

        server.sendmail(cfg["Email"]["mailFrom"], cfg["Email"]["mailTo"], message)
    except:
        print ('Something went wrong...')
    
    finally:
        server.quit()

if __name__ == "__main__":
    confPath = Path(__file__).resolve().parents[1] / 'conf.yaml'
    with open(confPath, 'r') as f:
        cfg = yaml.load(f)
    print(cfg)

    main()