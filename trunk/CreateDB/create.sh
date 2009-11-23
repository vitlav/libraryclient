#!/bin/sh
rm -f ./lib.db;
cp original/empty.db ./lib.db;

for t in libavtorname libavtor libbook;
do

echo "DOWNLOADING $t TABLE (MySQL script)";

rm -f lib.$t.sql.gz;
wget http://lib.rus.ec/sql/lib.$t.sql.gz;

echo "PREPARING $t IMPORT SCRIPT";
rm -f $t.sql;
rm -f ~tmp.sql;
regex="s/)\s*,\s*(/);\ninsert into $t values (/g";
zcat lib.$t.sql.gz | grep INSERT > ~tmp.sql;
sed "$regex" ~tmp.sql | sed "s/\\\'/''/g" > $t.sql;

echo "IMPORTING $t TABLE. BE PATIENT...";
sqlite3 lib.db < $t.sql;
echo "IMPORTED!";

rm -f $t.sql;

done

echo "OPTIMIZING DATABASE...";
sqlite3 lib.db < ./db_scripts/clean_db.sql;


rm -f ~tmp.sql;

echo "DONE!";
echo "Copy lib.db file to your /sdcard folder";