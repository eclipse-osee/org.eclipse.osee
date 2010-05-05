
package org.eclipse.osee.framework;

/**
 * Initialization support for running Xtext languages 
 * without equinox extension registry
 */
public class OseeTypesStandaloneSetup extends OseeTypesStandaloneSetupGenerated{

	public static void doSetup() {
		new OseeTypesStandaloneSetup().createInjectorAndDoEMFRegistration();
	}
}

