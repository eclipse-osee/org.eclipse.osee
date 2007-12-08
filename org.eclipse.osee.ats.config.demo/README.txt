The org.eclipse.osee.ats.config.demo plugin is a sample plugin that shows 
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

