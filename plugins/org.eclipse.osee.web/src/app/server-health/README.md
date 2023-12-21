# OSEE Server Health Manager

Admin pages for managing the application server health of OSEE.

## Table of Contents

- [Setup](#setup)
- [Features](#features)

## Setup

There are some **required** keys that must be set in the **osee_info** table before using this tool.

Populate **osee_info** table with:

|key|value (these are examples)|description|
|-|-|-|
|osee.health.servers|[servername1]:[port1],[servername1]:[port2],[servername2]:[port1],...|list of all servers with their port numbers|
|osee.health.curl.server|[servername1]:[port1]|designated server that will be used by all other servers to run machine-scoped commands such as 'top'|
|osee.activemq.url|[serverurl]/admin|url for activemq|
|osee.health.view.password (oracle only)||password used to set role necessary to access custom database roles (oracle only)|

If using oracle, there are two custom views that will need to be set up in order to populate database page:
    - osee_sql_monitoring
    - osee_db_tablespace_summary

## Features

1. Status
    - Table with list of server names with port numbers
    - Select any row to expand
        - Details
            - Table displaying specific server related details
        - Log
            - Log file for the specific server
        - Java
            - Table displaying java details for the server's VM or machine
        - Top
            - Load factor for the server's VM or machine
2. Balancers
    - Table with list of server names (no ports)
    - Select any row to expand
        - Embedded balancer configuration page (with button below to open page in new window)
3. Usage
    - Tables for usage statistics over the past 1 month:
        - Users
            - Table containing all users with their details
        - Sessions
            - Table containing all sessions with their details
        - Users by version name
            - Tables of users divided by OSEE version name
        - Users by version type
            - Tables of users divided by OSEE version type
4. Database
    - Tablespace table
        - Size and available space of OSEE tablespaces
    - SQL performance table
        - Performance of all sql statements executed in the database
5. ActiveMQ
    - Link to external web page for activeMQ configuration
6. Prometheus
    - Graphs for jvm and a few extra custom OSEE monitoring rest calls
7. HTTP Headers
    - List of communication headers for all http requests