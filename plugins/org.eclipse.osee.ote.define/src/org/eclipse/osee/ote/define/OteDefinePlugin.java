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
package org.eclipse.osee.ote.define;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class OteDefinePlugin implements BundleActivator {
   public static final String PLUGIN_ID = "org.eclipse.osee.ote.define";

   @Override
   public void start(BundleContext context) throws Exception {
      // do nothing
   }

   @Override
   public void stop(BundleContext context) throws Exception {
      // do nothing
   }
}