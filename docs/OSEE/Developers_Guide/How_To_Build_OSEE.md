# How To Build OSEE

1. Download and Install Maven 3.9
    - https://maven.apache.org/download.cgi

2. Ensure the system environment variable "JAVA_HOME" is properly pointing to JDK11.

3. Ensure the environment variable "JAVA_TOOL_OPTIONS" contains "-Dfile.encoding=UTF-8" to prevent improper encoding.
	- [Temporary] Git Bash: export JAVA_TOOL_OPTIONS="-Dfile.encoding=UTF-8"
	- [Temporary] CMD: set JAVA_TOOL_OPTIONS=-Dfile.encoding=UTF-8
	- [Permanent] Windows: CMD -> setx JAVA_TOOL_OPTIONS "-Dfile.encoding=UTF-8" -> start new CMD or Git Bash session

4. Download and install Chrome, Node.js 18, and @pnpm/exe.
	- https://github.com/nodejs/release?tab=readme-ov-file

5. Run the following commands to test and build the web code.
	- `cd plugins/org.eclipse.osee.web`
	- `npx -p @angular/cli ng test --browsers=ChromeHeadlessNoSandbox --watch=false`
	- `npx -p @angular/cli ng build -c ${ANGULAR_BUILD_TYPE}`
	- ANGULAR_BUILD_TYPE should be "forced_sso_java_release" for a production style build that requires authorization, "demo_java_release" for a demo style build that allows authorization to be set manually in a browser, or "okta_java_release" that integrates the web build with Okta.
	- The Okta build will require plugins/org.eclipse.osee.web/src/okta.ts to be set with the correct values for your Okta instance prior to building.

6. Copy build artifacts from org.eclipse.osee.web to org.eclipse.osee.web.deploy.
	- `cp -r plugins/org.eclipse.osee.web/dist/osee/production/. plugins/org.eclipse.osee.web.deploy/OSEE-INF/web/dist`

7. Download org.eclipse.ip.p2-1.0.0.zip from the [Louie-Maven Repository](https://github.com/Louie-Maven/ip) or by using the command below:
	- `curl -kLJO https://raw.githubusercontent.com/Louie-Maven/ip/main/org.eclipse.ip.p2-1.0.0.zip`

8. Take the org.eclipse.ip.p2-1.0.0.zip file and place it in the same directory as the org.eclipse.osee local repository you wish to run local builds for, not inside it. There is no need to unzip this file.

9. Acquire any necessary HTTPS certificates. They may include, but are not limited to those below.
	1. Navigate to:
		1. http://download.eclipse.org/jetty/updates/jetty-bundles-9.x/9.4.44.v20210927/
		2. https://repo.maven.apache.org/maven2/
	2. Download the PEM (cert) from each of the sites.
		1. [In Firefox] Lock Icon In Address Bar -> Connection Secure -> More Information -> View Certificate -> Miscellaneous -> Download -> PEM (cert)
	3. Add certificates to java keystore by running this command in Git Bash for each certificate:
		1. <KEYTOOL_LOCATION> -importcert -keystore <KEYSTORE_LOCATION> -storepass secret -alias <ALIAS_NAME> -file <CERTIFICATE_LOCATION>
		2. Example: /c/Tools/JDK11/bin/keytool.exe -importcert -keystore /c/Users/<username>/.keystore/keystore.jks -storepass secret -alias maven-ci -file /c/Tools/maven/apache-maven-3.9.0/certs/maven-ci-msc-com.pem
    4. If you experience any "Unable to find valid certification path to requested target" errors during the following steps, please repeat the above three steps to acquire and add the certificate for any missing resources.

10. Navigate to org.eclipse.osee/plugins/org.eclipse.osee.parent/extras and run the following command in Git Bash:
`mvn clean verify -Dnot_ci_eclipse=true -Dexternal-eclipse-ip-site=../../../../org.eclipse.ip.p2-1.0.0.zip`

11. Navigate to org.eclipse.osee/plugins/org.eclipse.osee.dep.parent and run the following command in Git Bash:
`mvn clean verify -Declipse-ip-site=file:../../../org.eclipse.ip/org.eclipse.ip.p2/target/repository`

12. You should now be able to run a local build by:
	1. Navigating to org.eclipse.osee/plugins/org.eclipse.osee.parent.
    2. Running `mvn clean verify` in Git Bash to build with tests or `mvn clean verify -DskipTests` to build without tests.
		1. Server Location:
    	`plugins/org.eclipse.osee.server.p2/target/org.eclipse.osee.server.runtime.zip`
			1. Unzip and run `./runHsql.sh`.
		2. Client Location For Windows:
		`plugins/org.eclipse.osee.client.all.product/target/products/org.eclipse.osee.ide.id-win32.win32.x86_64.zip`
			1. Unzip and run executable.
		3. Client Location For Linux:
		`plugins/org.eclipse.osee.client.all.product/target/products/org.eclipse.osee.ide.id-linux.gtk.x86_64.tar.gz`
			1. Unzip and run executable.