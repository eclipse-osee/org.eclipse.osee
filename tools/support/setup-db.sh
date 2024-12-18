curl --location 'http://localhost:8089/orcs/datastore/initialize' \
--header 'Accept-Encoding: application/zip' \
--header 'Authorization: Basic 3333' \
--header 'Content-Type: application/json' \
--data-raw '{
	"id": "11",
	"name": "OSEE",
	"userId": "11",
	"active": true,
	"email": "osee@gmail.com",
	"loginIds": ["11"]
}'
curl --location --request POST 'http://localhost:8089/orcs/datastore/synonyms' \
--header 'Accept-Encoding: application/zip' \
--header 'Authorization: Basic 3333'
curl --location --request PUT 'http://localhost:8089/ats/config/init/ats' \
--header 'Accept-Encoding: application/zip' \
--header 'Authorization: Basic 3333'
curl --location --request PUT 'http://localhost:8089/ats/config/init/demo' \
--header 'Accept-Encoding: application/zip' \
--header 'Authorization: Basic 3333'
curl --location 'http://localhost:8089/ats/config/clearcache' \
--header 'Accept-Encoding: application/zip' \
--header 'Authorization: Basic 3333'