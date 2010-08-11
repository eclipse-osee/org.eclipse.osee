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

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArraySet;
import org.eclipse.osee.ats.access.IAtsAccessControlService;
import org.eclipse.osee.framework.core.dsl.integration.AccessModelInterpreter;
import org.eclipse.osee.framework.core.dsl.integration.OseeDslAccessModel;
import org.eclipse.osee.framework.core.dsl.integration.OseeDslProvider;
import org.eclipse.osee.framework.core.model.access.AccessModel;
import org.eclipse.osee.framework.core.services.CmAccessControl;
import org.eclipse.osee.framework.core.util.AbstractTrackingHandler;
import org.eclipse.osee.framework.core.util.ServiceBindType;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

/**
 * @author Roberto E. Escobar
 */
public class AtsCmAccessControlRegHandler extends AbstractTrackingHandler {

   private final Map<Class<?>, ServiceBindType> serviceDeps = new HashMap<Class<?>, ServiceBindType>();

   public AtsCmAccessControlRegHandler() {
      serviceDeps.put(AccessModelInterpreter.class, ServiceBindType.SINGLETON);
      serviceDeps.put(IAtsAccessControlService.class, ServiceBindType.SINGLETON);
   }
   private final Collection<IAtsAccessControlService> atsAccessServices =
      new CopyOnWriteArraySet<IAtsAccessControlService>();
   private ServiceRegistration registration;

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
      IAtsAccessControlService atsService = getService(IAtsAccessControlService.class, services);
      atsAccessServices.add(atsService);
      OseeDslProvider dslProvider = new AtsAccessOseeDslProvider();
      AccessModel accessModel = new OseeDslAccessModel(interpreter, dslProvider);
      CmAccessControl cmService = new AtsCmAccessControl(accessModel, atsAccessServices);
      registration = context.registerService(CmAccessControl.class.getName(), cmService, null);
   }

   @Override
   public void onDeActivate() {
      if (registration != null) {
         registration.unregister();
      }
   }

   @Override
   public void onServiceAdded(BundleContext context, Class<?> clazz, Object service) {
      atsAccessServices.add((IAtsAccessControlService) service);
   }

   @Override
   public void onServiceRemoved(BundleContext context, Class<?> clazz, Object service) {
      atsAccessServices.remove(service);
   }

}
