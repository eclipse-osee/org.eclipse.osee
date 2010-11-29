COPY pgpass.conf "%SystemDrive%\%HOMEPATH%\Application Data\postgresql\pgpass.conf"
CMD /K "C:\Program Files\PostgreSQL\8.3\bin\psql.exe" -a -e -f osee.sql -U postgres