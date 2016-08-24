Notes on running OSEE client/server
Switching between different initializations of OSEE may cause the demo DB to have incorrect data. 
To address this problem, delete the hsql database directories (both hsql and attr - usually located in your home folder)
between runs, especially if you are changing way the database has been initialized between runs.
Each integration test will initialize the proper kind of database for the type of test, and will create a database for you.
*** if you are planning on running your own client with a server, you have to run the proper initialization yourself ***
For the OSEE_IDE_[localhost].launch: the AtsClient_Integration_Test_suite needs to be run to get all of the ATS types initialized.
