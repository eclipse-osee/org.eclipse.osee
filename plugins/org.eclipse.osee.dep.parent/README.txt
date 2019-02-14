1. Run IP bulid first
2. Edit your .bashrc and change MAVEN_OPTS to -Xmx3G
3. Execute this (or run runme.sh)
mvn clean verify -Declipse-ip-site=file:./../../../org.eclipse.ip/org.eclipse.ip.p2/target/repository

Failures
---------------------
- "URI is not hierarchical" or can't find directory target/repository
   - solution: you didn't run IP first

