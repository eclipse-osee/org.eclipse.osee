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
package org.eclipse.osee.framework.core.internal;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

/**
 * @author Roberto E. Escobar
 */
public class Activator implements BundleActivator {
   public static final String PLUGIN_ID = "org.eclipse.osee.framework.core";
   private static BundleContext bundleContext;

   @Override
   public void start(BundleContext context) throws Exception {
      Activator.bundleContext = context;
   }

   @Override
   public void stop(BundleContext context) throws Exception {
      Activator.bundleContext = null;
   }

   public static BundleContext getBundleContext() {
      return Activator.bundleContext;
   }
}
