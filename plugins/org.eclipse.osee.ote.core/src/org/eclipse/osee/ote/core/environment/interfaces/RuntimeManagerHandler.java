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
package org.eclipse.osee.ote.core.environment.interfaces;

import java.util.Map;
import org.eclipse.osee.framework.core.util.AbstractTrackingHandler;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.packageadmin.PackageAdmin;

/**
 * @author Andrew M. Finkbeiner
 *
 */
public class RuntimeManagerHandler extends AbstractTrackingHandler {
   private final static Class<?>[] SERVICE_DEPENDENCIES =
      new Class<?>[] {PackageAdmin.class};
   private ServiceRegistration serviceRegistration;
   
   @Override
   public Class<?>[] getDependencies() {
      return SERVICE_DEPENDENCIES;
   }

   @Override
   public void onActivate(BundleContext context, Map<Class<?>, Object> services) {
      PackageAdmin packageAdmin = getService(PackageAdmin.class, services);
      AbstractRuntimeManager runtimeManager = new AbstractRuntimeManager(packageAdmin, context);
      serviceRegistration = context.registerService(IRuntimeLibraryManager.class.getName(), runtimeManager, null);
   }

   @Override
   public void onDeActivate() {
      serviceRegistration.unregister();
   }
}
