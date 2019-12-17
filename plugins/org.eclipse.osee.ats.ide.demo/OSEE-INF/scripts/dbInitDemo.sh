#!/bin/bash
if [ "$#" -ne 2 ]; then
  echo "Usage: dbInitDemo.sh git_directory port_number"
  echo " "
  echo "   Did you put in the git directory and the port_number?"
  echo " "
  echo "   May need to manually rename/delete attr folder, production location is defined in the configured json file"
  echo "   NOTE: Rename first so you don't have to wait for delete"
  echo "   The import script will seem to hang on dbinit if this is not done"
  echo "   To DB Init the demo database:"
  echo "      Edit the <install_location>/etc/osee.<dbtype>.json, make sure the flag jdbc.client.is.production is set to false"
  echo "      Run application server"
  echo "      ./dbinitDemo.sh /c/UserData/git_one <app server port>"
  echo "      Restart the application server(s)"
  echo "      Launch client and test"
  echo " "
  exit
fi
GIT_DIR=$1
PORT=$2
ATS_TYPES=$1'/org.eclipse.osee/plugins/org.eclipse.osee.ats.ide/OSEE-INF/orcsTypes/OseeTypes_ATS.osee'
DEMO_TYPES=$1'/org.eclipse.osee/plugins/org.eclipse.osee.ats.ide.demo/OSEE-INF/orcsTypes/OseeTypes_Demo.osee'

echo "Git directory =" $GIT_DIR
echo "Port =" $PORT
echo "ATS Types =" $ATS_TYPES
echo "DEMO Types =" $DEMO_TYPES

echo "Starting..."

echo "Merging ATS types files..."
# Write ATS types file to new file
out='/c/UserData/Joined_Types.osee'

if test -f "$out"; then 
  rm $out
fi

file=$ATS_TYPES
while read line
do
   echo "$line" >> $out
done < $file

echo " " >> $out

# Merge other specific types to new file
count=1
file=$DEMO_TYPES
while read line
do
   if [ $count -gt 1 ]
   then
      echo "$line" >> $out
   fi
   let count=count+1
done < $file

echo "Are you sure you want to initialize the database connected to the Server running at localhost:$PORT?"
echo "Note: if you plan to re-initialize a production database"
echo "you have to set the jdbc.client.is.production flag in the json file to false"
read -p "Start initialization [y] or [n]: " -n 1 -r
echo    # (optional) move to a new line
if [[ $REPLY =~ ^[Yy]$ ]]
then
   echo "DB Initing..."
   curl -X POST -H "osee.account.id: 11" -H "Content-Type: text/plain" http://localhost:$PORT/orcs/datastore/initialize --data-binary "@/c/UserData/Joined_Types.osee"

   curl -X POST -H "osee.account.id: 11" -H "Content-Type: text/plain" http://localhost:$PORT/orcs/datastore/synonyms 

   echo "ATS Initing..."
   curl -X PUT -H "osee.account.id: 11" -H "Content-Type: text/plain" http://localhost:$PORT/ats/config/init/ats
   
   echo "Creating Demo Branches..."
   curl -X POST -H "osee.account.id: 11" -H "Content-Type: text/plain" http://localhost:$PORT/orcs/datastore/initialize/createDemoBranches
   
   echo "Demo DB Initing..."
   curl -X PUT -H "osee.account.id: 11" -H "Content-Type: text/plain" http://localhost:$PORT/ats/config/init/demo
   
   echo "Clearing ATS Cache..."
   curl -X GET -H "osee.account.id: 11" -H "Content-Type: text/plain" http://localhost:$PORT/ats/config/clearcache
   echo " "
fi
echo "Completed!"


