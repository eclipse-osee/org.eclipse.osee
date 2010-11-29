cp pgpass_mac.conf ~/.pgpass
/Library/PostgreSQL/8.3/bin/psql -a -e -f ./osee.sql -U postgres