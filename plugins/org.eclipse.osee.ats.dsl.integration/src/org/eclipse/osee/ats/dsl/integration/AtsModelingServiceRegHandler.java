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
package org.eclipse.osee.ats.dsl.integration;

import java.util.Map;
import org.eclipse.osee.ats.dsl.atsDsl.AtsDslFactory;
import org.eclipse.osee.ats.workdef.IAtsModelingService;
import org.eclipse.osee.framework.core.services.IOseeCachingService;
import org.eclipse.osee.framework.core.services.IOseeCachingServiceFactory;
import org.eclipse.osee.framework.core.services.IOseeModelFactoryService;
import org.eclipse.osee.framework.core.util.AbstractTrackingHandler;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

/**
 * @author Donald G. Dunne
 */
public class AtsModelingServiceRegHandler extends AbstractTrackingHandler {

   private static final Class<?>[] SERVICE_DEPENDENCIES = new Class<?>[] {
      //
      IOseeCachingService.class, //
      IOseeModelFactoryService.class, //
      IOseeCachingServiceFactory.class //
      };

   private ServiceRegistration registration;

   @Override
   public Class<?>[] getDependencies() {
      return SERVICE_DEPENDENCIES;
   }

   @Override
   public void onActivate(BundleContext context, Map<Class<?>, Object> services) {
      IOseeModelFactoryService modelFactoryService = getService(IOseeModelFactoryService.class, services);
      IOseeCachingService cachingService = getService(IOseeCachingService.class, services);
      IOseeCachingServiceFactory cachingFactoryService = getService(IOseeCachingServiceFactory.class, services);

      IAtsModelingService service =
         new AtsModelingServiceImpl(modelFactoryService, cachingService, cachingFactoryService, AtsDslFactory.eINSTANCE);
      registration = context.registerService(IAtsModelingService.class.getName(), service, null);
   }

   @Override
   public void onDeActivate() {
      if (registration != null) {
         registration.unregister();
      }
   }

}
