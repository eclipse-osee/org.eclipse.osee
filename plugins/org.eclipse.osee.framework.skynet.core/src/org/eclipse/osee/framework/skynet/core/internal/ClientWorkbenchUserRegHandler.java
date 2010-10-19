/*******************************************************************************
 * Copyright (c) 2010 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.skynet.core.internal;

import java.util.Map;
import org.eclipse.osee.framework.core.services.IOseeCachingService;
import org.eclipse.osee.framework.core.util.AbstractTrackingHandler;
import org.eclipse.osee.framework.plugin.core.IWorkbenchUserService;
import org.eclipse.osee.framework.skynet.core.WorkbenchUserService;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

public class ClientWorkbenchUserRegHandler extends AbstractTrackingHandler {

   private static final Class<?>[] DEPENDENCIES = new Class<?>[] {IOseeCachingService.class};

   private ServiceRegistration serviceRegistration;

   @Override
   public Class<?>[] getDependencies() {
      return DEPENDENCIES;
   }

   @Override
   public void onActivate(BundleContext context, Map<Class<?>, Object> services) {
      serviceRegistration =
         context.registerService(IWorkbenchUserService.class.getName(), new WorkbenchUserService(), null);
   }

   @Override
   public void onDeActivate() {
      if (serviceRegistration != null) {
         serviceRegistration.unregister();
      }
   }

}
