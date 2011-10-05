
How To Build using Maven

To build OSEE.X modulde execute:
mvn verify

To build OSEE.X modulde with debug output execute:
mvn -X verify

To create an aggregate test report execute:
mvn surefire-report:report-only -Daggregate=true

These commands can be combined:
mvn verify surefire-report:report-only -Daggregate=true

An aggregate report will be in the target/site folder

## Currently, Tycho creates aggregate test reports inside the test bundles 
## not in a single location
## 
