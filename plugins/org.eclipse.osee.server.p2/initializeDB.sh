#!/bin/sh

# Check if the script is already running in a subprocess
if [ -z "$in_subprocess" ]; then
    in_subprocess=y
    export in_subprocess
    "$0" "$@"
    exit
fi

# Execute the curl commands
curl --location --request POST "http://localhost:8089/orcs/datastore/initialize" \
    --header "Content-Type: application/json" \
    --data-raw "{\"id\":\"11\", \"name\":\"OSEE\", \"userId\":\"11\", \"active\":true, \"email\":\"osee@gmail.com\", \"loginIds\":[\"11\"]}"

curl --location --request POST "http://localhost:8089/orcs/datastore/synonyms" \
    --header "Content-Type: text/plain" \
    --header "osee.account.id: 11"

curl --location --request PUT "http://localhost:8089/ats/config/init/ats" \
    --header "Content-Type: text/plain" \
    --header "osee.account.id: 11" \
    --header "Authorization: 11"

curl --location --request PUT "http://localhost:8089/ats/config/init/demo" \
    --header "Content-Type: text/plain" \
    --header "osee.account.id: 11" \
    --header "Authorization: 11"

curl --location --request GET "http://localhost:8089/ats/config/clearcache" \
    --header "Content-Type: text/plain" \
    --header "osee.account.id: 11" \
    --header "Authorization: 11"

# Pause functionality
echo
read -p "Press [Enter] key to continue..."