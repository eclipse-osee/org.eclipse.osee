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
package org.eclipse.osee.framework.core.dsl.ui.integration.internal;

import java.util.Map;
import org.eclipse.osee.framework.core.dsl.integration.ArtifactDataProvider;
import org.eclipse.osee.framework.core.services.IOseeCachingService;
import org.eclipse.osee.framework.core.util.AbstractTrackingHandler;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

/**
 * @author Roberto E. Escobar
 */
public class ArtifactDataProviderServiceRegHandler extends AbstractTrackingHandler {

   private static final Class<?>[] SERVICE_DEPENDENCIES = new Class<?>[] {
   //
      IOseeCachingService.class, //
      };

   private ServiceRegistration registration;

   @Override
   public Class<?>[] getDependencies() {
      return SERVICE_DEPENDENCIES;
   }

   @Override
   public void onActivate(BundleContext context, Map<Class<?>, Object> services) {
      ArtifactDataProvider artifactDataProvider = new ArtifactDataAccessor();
      registration = context.registerService(ArtifactDataProvider.class.getName(), artifactDataProvider, null);
   }

   @Override
   public void onDeActivate() {
      if (registration != null) {
         registration.unregister();
      }
   }

}
