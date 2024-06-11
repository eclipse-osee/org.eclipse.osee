# PostgreSQL

### Download And Install PostgreSQL

**Note:** If you would rather utilize docker for a simplified setup process, please see the alternate [PostgreSQL Docker instructions](#alternative-postgresql-installation-using-docker-desktop).

Download the latest PostgreSQL version for you operating system from <http://www.postgresql.org/download>. PostgreSQL 16.3 for Windows can be downloaded directly using this [download link](https://sbp.enterprisedb.com/getfile.jsp?fileid=1259105).

After downloading, follow the PostgreSQL installation wizard instructions. Unless required, do not change the default port number (5432)

### Configure PostgreSQL Connections
By default, the PostgreSQL database server is configured to allow only local connections. If remote connections are to be allowed, edit postgresql.conf and pg_hba.conf to set the necessary permissions.

To setup an unsecured database instance:
1.  Set `listen_addresses = '*'` in `postgresql.conf` (located in the `\PostgreSQL\[version]\data` directory)
2.  Set `host all all 0.0.0.0/0 trust` in `pg_hba.conf` (located in the `\PostgreSQL\[version]\data` directory)
3.  You may need to set all METHODs to trust


### Configure PostgreSQL for OSEE
1.  Launch pgAdmin (in windows Start→All
    Programs→PostgreSQL\*→pgAdmin \*)
2.  Double click on PostgreSQL Database Server (listed under Servers
    on the left hand side)
    1.  If you are prompted for a password type the password
        selected during installation (user should be postgres by
        default)
3.  Create an `osee` user
    1.  Right-click on Login/Group Roles (in the tree on the left
        hand side) and select `Create → Login/Group Role...`
    2.  Enter the following in the dialog:
        1.  General Tab
            1.  Name: osee
        2.  Definition Tab
            1.  Password: osee
        3.  Privileges Tab
            1.  Can login? Yes
            2.  Superuser: Yes
            3.  Create roles? Yes
            4.  Create databases: Yes
            5.  Update catalog? Yes
    3.  Click 'Save'
    4.  You should now have an `osee` user under Login Roles
4.  Expand the `Databases` item in the tree
    1.  Create the `osee` database by right-clicking on `Databases`
        and selecting `Create → Database...`
    2.  Enter the following in the dialog:
        1.  General Tab
            1.  Database: osee
            2.  Owner: osee
        2.  Definition Tab
            1.  Encoding: UTF-8
    3.  Click 'Save'
    4.  You should now have an `osee` Database under Databases
5.  Click on `osee` and then expand it, then expand `Schemas`
    1.  Create the `osee` schema:
        1.  Right click on `Schemas` and select `Create →
            Schema...`
        2.  Enter the following in the dialog:
            1.  General Tab
                1.  Name: osee
                2.  Owner: osee
        3.  Click 'Save'
        4.  You should now have an `osee` schema under schemas
6.  Enable view that provides database statistics
    1.  Uncomment (Remove '#') and set `shared_preload_libraries = 'pg_stat_statements'` in `postgresql.conf`
    2.  Run this query against the osee database: `create extension pg_stat_statements;`
    3.  Restart the postgres database (e.g. Windows: command prompt navigate to Postgres bin and run      restart command on postgres exe)
7.  The relational database is now configured. Proceed to the [OSEE Server](#osee-server) instructions.

# Alternative: PostgreSQL Installation Using Docker Desktop

### Download And Install Docker Desktop

Download and install the appropriate version of Docker Desktop for your operating system from the [Docker Desktop Webpage](https://www.docker.com/products/docker-desktop/)

### Pull the PostgreSQL Docker Image

Open a Command Prompt window: `Start → search "Command Prompt"`

Pull a docker image for PostgreSQL 16.3 by running the following command in Command Prompt.
```
docker pull postgres:16.3
```

### Run a PostgreSQL Docker Container With Temporary Data

Run the following command in Command Prompt.

```
docker run --name osee-postgres -e POSTGRES_PASSWORD=osee -e POSTGRES_USER=osee -e POSTGRES_DB=osee -e POSTGRES_HOST_AUTH_METHOD=trust -d postgres:16.3
```
**Note:** Running a PostgreSQL Docker Container in this configuration will store data within the container. In other words, if the container is destroyed, so is the data it contained. If you wish to run a container that uses a persistent data volume that will exist independently from the container, please run using the persistent data configuration below.

### Run a PostgreSQL Docker Container With Persistent Data

Create a folder named `postgresql_data` in your documents directory.

Run the following command in Command Prompt after replacing `YOUR_USER_HERE` with your username.

```
docker run --name osee-postgres -e POSTGRES_PASSWORD=osee -e POSTGRES_USER=osee -e POSTGRES_DB=osee -e POSTGRES_HOST_AUTH_METHOD=trust -e PGDATA=/var/lib/postgresql/data/pgdata -v C:\Users\YOUR_USER_HERE\Documents\postgresql_data:/var/lib/postgresql/data -d postgres:16.3
```

Alternatively, you can create a data folder in another location by simply replace the path to the left of the ':' with the path to your folder.

# OSEE Server

### Download The Server
Download and install the `Latest Demo Application Server Build` from the [OSEE Downloads Page](https://eclipse.dev/osee/downloads/).

Alternatively you can use this [direct link](https://ci.eclipse.org/osee/job/osee_ple_demo/lastSuccessfulBuild/artifact/org.eclipse.osee/plugins/org.eclipse.osee.server.p2/target/org.eclipse.osee.server.runtime.zip) to download the demo application server.

### Extract The Server

Navigate to your downloads directory, right click `org.eclipse.osee.server.runtime.zip`, and extract using your preferred extraction program (WinZip, 7-Zip, etc.).

### Run The Server

If you are using Windows machine, double click the `runPostgrSqlLocal.bat` file in the extracted `org.eclipse.osee.server.runtime` directory. This should open Command Prompt and display the running server's log output.

If you are using a Linux machine, run the equivalent `runPostgreSqlLocal.sh` shell script in your terminal.

**Note:** Closing your terminal window will stop the server.

### Initialize The Database

If you are using Windows machine, double click the `initializeDB.bat` file in the extracted `org.eclipse.osee.server.runtime` directory. This should open Command Prompt and run database initialization tasks through the OSEE server.

If you are using a Linux machine, run the equivalent `initializeDB.sh` shell script in your terminal.

# OSEE Website

### Navigate To The Website

In your browser of choice, navigate to the [OSEE Website](http://localhost:8089/osee/ple). When running locally, the website will exist at `http://localhost:8089/osee/ple`.

You will be presented with convenient navigation buttons to various OSEE features. Additionally, clicking the hamburger (three stacked lines) icon on the top left of your screen, you will be presented with additional navigation options that are accessible from anywhere in the site.

### Configure Browser For Demo Authentication

In your browser, open developer tools (F12) and navigate to the local storage section.

Firefox: `F12 → Storage → Local Storage → http://localhost:8089`
Edge and Chrome: `F12 → Application → Storage → Local Storage → http://localhost:8089`


Add the following row:
- key: `osee.account.id` value: `3333`

You should now be able to authenticate with the demo server and view data. If you receive a 401 error, either the local storage was not set correctly in the browser or the database was not properly initialized.

**Note:** Authenticating this way is only possible when running a demo application server. A production application server will not permit this.

### Convenience Links
- [Artifact Explorer](http://localhost:8089/osee/ple/artifact/explorer)
- [Message Interface Modeler (MIM)](http://localhost:8089/osee/ple/messaging)
- [Product Line Configuration (PL Config)](http://localhost:8089/osee/ple/artifact/explorer)


