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
package org.eclipse.osee.framework.plugin.core;

import java.io.File;
import java.io.IOException;
import org.eclipse.core.runtime.Plugin;
import org.osgi.framework.BundleContext;

/**
 * @author Ryan D. Brooks
 */
public class OseeActivator extends Plugin {
   private ActivatorHelper helper;

   /**
    * The constructor.
    */
   protected OseeActivator() {
      super();
   }

   /**
    * finds a resource in the plugin bundle and writes it out to the default persistent storage area as a regualar file
    * 
    * @param path
    * @return Return plugin file reference
    * @throws IOException
    */
   public File getPluginFile(String path) throws IOException {
      return helper.getPluginFile(path);
   }

   /**
    * This method is called upon plug-in activation
    */
   public void start(BundleContext context) throws Exception {
      super.start(context);
      helper = new ActivatorHelper(context, this);
   }

}
