/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.internal;

import java.util.Map;
import org.eclipse.osee.framework.core.dsl.integration.AccessModelInterpreter;
import org.eclipse.osee.framework.core.dsl.integration.OseeDslAccessModel;
import org.eclipse.osee.framework.core.dsl.integration.OseeDslProvider;
import org.eclipse.osee.framework.core.model.access.AccessModel;
import org.eclipse.osee.framework.core.services.ConfigurationManagement;
import org.eclipse.osee.framework.core.util.AbstractTrackingHandler;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

/**
 * @author Roberto E. Escobar
 */
public class AtsConfigurationManagementRegHandler extends AbstractTrackingHandler {

   private static final Class<?>[] SERVICE_DEPENDENCIES = new Class<?>[] {//
      AccessModelInterpreter.class, // 
      };

   private ServiceRegistration registration;

   @Override
   public Class<?>[] getDependencies() {
      return SERVICE_DEPENDENCIES;
   }

   @Override
   public void onActivate(BundleContext context, Map<Class<?>, Object> services) {
      AccessModelInterpreter interpreter = getService(AccessModelInterpreter.class, services);
      OseeDslProvider dslProvider = new AtsAccessOseeDslProvider();
      AccessModel accessModel = new OseeDslAccessModel(interpreter, dslProvider);
      ConfigurationManagement cmService = new AtsConfigurationManagement(accessModel);
      registration = context.registerService(ConfigurationManagement.class.getName(), cmService, null);
   }

   @Override
   public void onDeActivate() {
      if (registration != null) {
         registration.unregister();
      }
   }

}
