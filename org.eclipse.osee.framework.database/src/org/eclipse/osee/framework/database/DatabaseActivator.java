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
package org.eclipse.osee.framework.database;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

/**
 * The main plug-in class to be used in the desktop.
 * 
 * @author Ryan D. Brooks
 */
public class DatabaseActivator implements BundleActivator {

   // The shared instance.
   private static DatabaseActivator plugin;

   /**
    * Returns the shared instance.
    */
   public static DatabaseActivator getInstance() {
      return plugin;
   }

   public Bundle getBundle() {
      return bundle;
   }

   private Bundle bundle;

   /* (non-Javadoc)
    * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
    */
   @Override
   public void start(BundleContext context) throws Exception {
      bundle = context.getBundle();
      plugin = this;
   }

   /* (non-Javadoc)
    * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
    */
   @Override
   public void stop(BundleContext context) throws Exception {
      plugin = null;
   }
}