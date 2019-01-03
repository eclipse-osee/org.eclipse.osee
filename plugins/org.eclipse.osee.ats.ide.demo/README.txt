The org.eclipse.osee.ats.ide.config.demo plugin is a sample plugin that shows 
how to configure a company and it's teams to use ATS.  In addition, it provides
for the creation of Actions that can be used to test out ATS functionality, 
train users or for demo purposes where live program data shouldn't be used.

ATS does not REQUIRE that a plugin (like this one) be created in order to 
configure and track items.  ATS can be configured for new Teams, Actionable Items
through the normal Artifact framework during runtime.

However, in order to perform some of the advanced configuration of ATS such
as placing conditions on state transactions, adding new fields and extending
the ATS Navigator to add convenience items, a plugin must be created.
 
Steps to create a database and load actions for training or demo use:
1) Make sure this plugin is included in your workspace
2) Create the database using the config runtime for the appropriate db
3) Select the "OSEE Demo Database" selection during the database configuration
4) Upon completion of the database creation, kickoff a runtime OSEE against that database
5) XYZ Admin (and other XYZ convenience items should show in the ATS Navigator)

OSEE can be used at this point to create new Actions against XYZ configured items.

To create sample Actions written against XYZ configured items:
1) Under XYZ Admin - Demo Data, select "Populate Demo Actions" and confirm

This will create approx 38 sample Actions transitioned to different states and assigned
to fictitious users.

-----------------

OSEE Demo Installer

This installer will install the open source PostgreSQL (http://www.postgresql.org/) database, a demo version of the database and a copy of OSEE.  If you already have PostgreSQL or OSEE installed, it will un-install and re-install fresh.

- Ensure you have Java 1.5.0 installed (http://java.sun.com/j2se/1.5.0/)
- Download the installer zip to your harddrive
- Double-click to open and then double-click the self-extracting jar
- After un-packed, an OSEE Installer window will appear.  
- Accept the installation directory or choose your own
- If you already have PostGreSQL installed, it will be removed (NOTE: Windows will sometimes hang on the PostGreSQL un-install, simply kill and restart the installer and you should be ok
- After a successful installation, you should have an OSEE icon on your desktop

OSEE Demo Quickstart

- During launch OSEE, you will want to sign in with the name "Terry Stevens" or "Jason Baker" and with "osee" entered in the password and domain.  This will run your instance of OSEE as that person.

- After launch of OSEE, you will be in the Resource perspective
- Select Open Perspective and select Define
- The Artifact Explorer shows the requirements that have been imported and stored

- Select Open Perspective and select ATS
- Select "My World" in the ATS Navigator.  This shows all of the Actions (Change Requests) that are assigned to you

A more detailed description of the Demo will be provided at http://www.eclipse.org/osee/