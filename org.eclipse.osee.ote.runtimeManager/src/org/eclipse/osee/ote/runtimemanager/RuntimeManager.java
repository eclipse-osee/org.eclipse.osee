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
package org.eclipse.osee.ote.runtimemanager;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

/**
 * 
 * @author Robert A. Fisher
 */
public class RuntimeManager implements BundleActivator {
   private static RuntimeManager instance;
   private BundleContext context;
   
   public static RuntimeManager getDefault() {
      return instance;
   }

   
   public void start(BundleContext context) throws Exception {
      instance = this;
      this.context = context;
   }
   
   public void stop(BundleContext context) throws Exception {
      instance = null;
      this.context = null;
   }


   public BundleContext getContext() {
      return context;
   }
   
   
}
