
package org.eclipse.osee.ats.dsl;

/**
 * Initialization support for running Xtext languages 
 * without equinox extension registry
 */
public class AtsDslStandaloneSetup extends AtsDslStandaloneSetupGenerated{

	public static void doSetup() {
		new AtsDslStandaloneSetup().createInjectorAndDoEMFRegistration();
	}
}

