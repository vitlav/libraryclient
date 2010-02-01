#!/bin/bash

files=(libavtorname libavtor libbook b.annotations b.annotations_pics a.annotations a.annotations_pics);
tables=(libavtorname libavtor libbook libbannotations libbpics libaannotations libapics);

#files=(b.annotations b.annotations_pics a.annotations a.annotations_pics);
#tables=(libbannotations libbpics libaannotations libapics);


rm -f ./lib.db;

sqlite3 lib.db < ./original/dump.sql

for i in ${!files[*]}
do

t=${files[$i]}
table=${tables[$i]}

echo "DOWNLOADING $t DUMP (MySQL script)";

rm -f lib.$t.sql.gz;
wget http://93.174.93.47/sql/lib.$t.sql.gz;

echo "PREPARING $table IMPORT SCRIPT";

rm -f $t.sql;
rm -f ~tmp.sql;

regex="s/)\s*,\s*(/);\ninsert into $table values (/g";
zcat lib.$t.sql.gz | grep INSERT > ~tmp.sql;
sed "$regex" ~tmp.sql | sed "s/\\\'/''/g" > $t.sql;

echo "IMPORTING $table TABLE. BE PATIENT...";
sqlite3 lib.db < $t.sql;
echo "DONE!";

rm -f $t.sql;

done

echo "OPTIMIZING DATABASE...";
sqlite3 lib.db < ./db_scripts/clean_db.sql;


rm -f ~tmp.sql;

echo "DONE!";

echo "DOWNLOADING IMAGES";
wget http://93.174.93.47/sql/libattachedfiles.zip

echo "Copy lib.db and libattachedfiles.zip files to your /sdcard folder";