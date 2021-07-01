import mysql.connector
import os

db = mysql.connector.connect(
    host=os.getenv('DDB_DB_HOST'),
    user=os.getenv('DDB_DB_USERNAME'),
    password=os.getenv('DDB_DB_PASSWORD'),
    database=os.getenv('DDB_DB_NAME')
)

query = 'SELECT user_id, content FROM `Messages`'

cursor = db.cursor()

cursor.execute(query)

events = cursor.fetchall()

freq = {}

for (uid, content) in events:
    count = content.count('😌')
    if count > 0:
        if uid not in freq:
            freq[uid] = 0
        freq[uid] += count

od = dict(sorted(freq.items(), key=lambda item: item[1]))

print(od)
