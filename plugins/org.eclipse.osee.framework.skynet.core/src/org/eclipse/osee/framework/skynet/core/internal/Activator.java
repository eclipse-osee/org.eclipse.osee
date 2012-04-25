/*******************************************************************************
 * Copyright (c) 2009 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.skynet.core.internal;

import org.eclipse.osee.framework.core.client.ClientSessionManager;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

/**
 * @author Ryan D. Brooks
 */
public class Activator implements BundleActivator {
   public static final String PLUGIN_ID = "org.eclipse.osee.framework.skynet.core";

   private static Activator instance;

   @Override
   public void start(BundleContext context) throws Exception {
      instance = this;
      ClientSessionManager.class.getCanonicalName();
   }

   @Override
   public void stop(BundleContext context) throws Exception {
      instance = null;
   }

   public static Activator getInstance() {
      return instance;
   }

}