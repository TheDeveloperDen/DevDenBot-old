import json
import os

import mysql.connector

file = open('stats.json', 'r')
data = json.loads(file.read())  # load stats file

db = mysql.connector.connect(
    host=os.getenv('DDB_DB_HOST'),
    user=os.getenv('DDB_DB_USERNAME'),
    password=os.getenv('DDB_DB_PASSWORD'),
    database=os.getenv('DDB_DB_NAME')
)

cursor = db.cursor()

for uid, properties in data.items():
    xp = properties['xp']
    level = properties['level']
    bumps = properties['bumps']
    cursor.execute('INSERT INTO Users(id, xp, level, bumps) VALUES (%s, %s, %s, %s)', (
        uid, xp, level, bumps
    ))  # yeah this will fail if there are duplicate entries, i don't care

db.commit()
