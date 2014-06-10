/*******************************************************************************
 * Copyright (c) 2014 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.jaxrs.client;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.eclipse.osee.jaxrs.client.internal.StandaloneModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;

/**
 * @author Roberto E. Escobar
 */
public final class JaxRsClientFactory {

   private JaxRsClientFactory() {
      // Utility class
   }

   public static JaxRsClient createClient(Map<String, Object> props) {
      return createClient(JaxRsClient.class, props);
   }

   public static <T> T createClient(Class<T> api, Map<String, Object> props, Module... modules) {
      Injector injector = createInjector(props, modules);
      return injector.getInstance(api);
   }

   private static Injector createInjector(Map<String, Object> props, Module... modules) {
      List<Module> moduleList = new ArrayList<Module>();
      moduleList.add(new StandaloneModule(props));
      for (Module module : modules) {
         moduleList.add(module);
      }
      return Guice.createInjector(moduleList);
   }
}
