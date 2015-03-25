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

package org.eclipse.osee.framework.jini;

import org.eclipse.core.runtime.Plugin;
import org.osgi.framework.BundleContext;

/**
 * The main plug-in class to be used in the desktop.
 */
public class JiniPlugin extends Plugin {

   private static JiniPlugin plugin;

   /**
    * The constructor.
    */
   public JiniPlugin() {
      JiniPlugin.plugin = this;
   }

   /**
    * This method is called when the plug-in is stopped
    */
   @Override
   public void stop(BundleContext context) throws Exception {
      super.stop(context);
      plugin = null;
   }

   /**
    * Returns the shared instance.
    */
   public static JiniPlugin getInstance() {
      return plugin;
   }

}
