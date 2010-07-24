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
package org.eclipse.osee.sos;

import org.eclipse.osgi.framework.console.CommandProvider;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class Activator implements BundleActivator {
   private BundleContext myContext;
   private static Activator instance;

   @Override
   public void start(BundleContext context) throws Exception {
      myContext = context;
      instance = this;
      myContext.registerService(CommandProvider.class.getName(), new SosCommand(), null);
   }

   @Override
   public void stop(BundleContext context) throws Exception {
      instance = null;
   }

   public BundleContext getContext() {
      return myContext;
   }

   public static Activator getInstance() {
      return Activator.instance;
   }
}
