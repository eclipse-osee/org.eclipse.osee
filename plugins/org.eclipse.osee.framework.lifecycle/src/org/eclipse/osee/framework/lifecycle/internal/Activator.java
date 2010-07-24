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
package org.eclipse.osee.framework.lifecycle.internal;

import org.eclipse.osee.framework.lifecycle.ILifecycleService;
import org.eclipse.osee.framework.lifecycle.LifecycleServiceImpl;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

/**
 * @author Roberto E. Escobar
 * @author Jeff C. Phillips
 */
public class Activator implements BundleActivator {
   public static final String PLUGIN_ID = "org.eclipse.osee.framework.lifecycle";
   private ServiceRegistration service;

   public Activator() {
   }

   @Override
   public void start(BundleContext context) throws Exception {
      service = context.registerService(ILifecycleService.class.getName(), new LifecycleServiceImpl(), null);
   }

   @Override
   public void stop(BundleContext context) throws Exception {
      service.unregister();
   }
}
