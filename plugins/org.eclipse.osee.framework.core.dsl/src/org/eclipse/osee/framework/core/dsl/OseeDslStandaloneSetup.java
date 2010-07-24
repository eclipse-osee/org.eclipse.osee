package org.eclipse.osee.framework.core.dsl;

/**
 * Initialization support for running Xtext languages without equinox extension registry
 */
public class OseeDslStandaloneSetup extends OseeDslStandaloneSetupGenerated {

   public static void doSetup() {
      new OseeDslStandaloneSetup().createInjectorAndDoEMFRegistration();
   }
}
