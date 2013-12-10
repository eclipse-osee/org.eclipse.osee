/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.rest.client;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.osee.rest.client.internal.StandaloneModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;

/**
 * Class to use when using the API in a non-OSGI environment
 * 
 * @author Roberto E. Escobar
 */
public final class OseeRestClientStandaloneSetup {

   private OseeRestClientStandaloneSetup() {
      // Utility class
   }

   public static <T> T createClient(Class<T> api, OseeClientConfig config, Module... modules) {
      Injector injector = createInjector(config, modules);
      return injector.getInstance(api);
   }

   private static Injector createInjector(OseeClientConfig config, Module... modules) {
      List<Module> moduleList = new ArrayList<Module>();
      moduleList.add(new StandaloneModule(config));
      for (Module module : modules) {
         moduleList.add(module);
      }
      return Guice.createInjector(moduleList);
   }
}
