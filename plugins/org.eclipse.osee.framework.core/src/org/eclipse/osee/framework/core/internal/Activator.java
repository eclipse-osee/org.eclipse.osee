/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

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
