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

import java.util.HashMap;
import java.util.Map;
import org.eclipse.osee.ats.access.AtsBranchAccessManager;
import org.eclipse.osee.framework.core.dsl.integration.AccessModelInterpreter;
import org.eclipse.osee.framework.core.dsl.integration.OseeDslAccessModel;
import org.eclipse.osee.framework.core.dsl.integration.OseeDslProvider;
import org.eclipse.osee.framework.core.model.access.AccessModel;
import org.eclipse.osee.framework.core.services.CmAccessControl;
import org.eclipse.osee.framework.core.util.AbstractTrackingHandler;
import org.eclipse.osee.framework.core.util.ServiceBindType;
import org.eclipse.osee.framework.skynet.core.event.OseeEventManager;
import org.eclipse.osee.framework.skynet.core.event.listener.IEventListener;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

/**
 * @author Roberto E. Escobar
 */
public class AtsCmAccessControlRegHandler extends AbstractTrackingHandler {

   private final Map<Class<?>, ServiceBindType> serviceDeps = new HashMap<Class<?>, ServiceBindType>();

   private ServiceRegistration registration;
   private IEventListener listener;

   private AtsBranchAccessManager atsBranchObjectManager;

   public AtsCmAccessControlRegHandler() {
      serviceDeps.put(AccessModelInterpreter.class, ServiceBindType.SINGLETON);
   }

   @Override
   public Class<?>[] getDependencies() {
      return null;
   }

   @Override
   public Map<Class<?>, ServiceBindType> getConfiguredDependencies() {
      return serviceDeps;
   }

   @Override
   public void onActivate(BundleContext context, Map<Class<?>, Object> services) {
      AccessModelInterpreter interpreter = getService(AccessModelInterpreter.class, services);

      OseeDslProvider dslProvider = new AtsAccessOseeDslProvider();
      AccessModel accessModel = new OseeDslAccessModel(interpreter, dslProvider);

      atsBranchObjectManager = new AtsBranchAccessManager();
      CmAccessControl cmService = new AtsCmAccessControl(accessModel, atsBranchObjectManager);
      registration = context.registerService(CmAccessControl.class.getName(), cmService, null);

      listener = new OseeDslProviderUpdateListener(dslProvider);
      OseeEventManager.addListener(listener);
   }

   @Override
   public void onDeActivate() {
      if (listener != null) {
         OseeEventManager.removeListener(listener);
         if (atsBranchObjectManager != null) {
            atsBranchObjectManager.dispose();
         }
         listener = null;
      }
      if (registration != null) {
         registration.unregister();
      }
   }

}
