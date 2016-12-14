This bundle exists to provide the IDE Client with
- An Http Service
- A connection to the application server
- A service to the client to provide information about the server / database


Things done
	- Moved ClientResource's ClientSession to shared orcs.model
	- Renamed ClientSession to IdeClientSession
	- 
	
Things to do
	- Rename bundle to core.ideclient
	- Remove OseeClientSession, replace with IdeClientSession
	- Have bundle initiate a socket connection between server and ideclient
