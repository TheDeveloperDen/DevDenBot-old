import os

import mysql.connector

db = mysql.connector.connect(
    host=os.getenv('DDB_DB_HOST'),
    user=os.getenv('DDB_DB_USERNAME'),
    password=os.getenv('DDB_DB_PASSWORD'),
    database=os.getenv('DDB_DB_NAME')
)

cursor = db.cursor()

# get the entries from the events table
cursor.execute('SELECT * FROM Events')
events = cursor.fetchall()

for (_, event_type, data, date) in events:
    if event_type == 'USER_MESSAGE':
        cursor.execute(
            """
            INSERT INTO Messages(timestamp, channel_id, content, type, user_id)
            VALUES (%s, %s, 'No Data Available', 'CREATE', -1)
            """,
            (date, data))
        print('USER_MESSAGE inserted')

    elif event_type == 'USER_JOIN':
        cursor.execute(
            """
            INSERT INTO JoinLeaves(timestamp, user_id, username, type)
            VALUES (%s, %s, 'Unknown User', 'JOIN')  
            """,
            (date, data))
        print('USER_JOIN inserted')
    elif event_type == 'USER_LEAVE':
        cursor.execute(
            """
            INSERT INTO JoinLeaves(timestamp, user_id, username, type)
            VALUES (%s, %s, 'Unknown User', 'LEAVE')  
            """,
            (date, data))
        print('USER_LEAVE inserted')
    db.commit()
