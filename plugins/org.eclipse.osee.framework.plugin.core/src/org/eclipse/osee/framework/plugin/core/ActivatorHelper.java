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
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.osgi.framework.BundleContext;

/**
 * @author Ryan D. Brooks
 */
public class ActivatorHelper {
   private static Map<String, Plugin> pluginIdToOseePlugin = new HashMap<String, Plugin>();
   private final BundleContext context;
   private final Plugin plugin;

   /**
    * 
    */
   public ActivatorHelper(BundleContext context, Plugin plugin) {
      pluginIdToOseePlugin.put(plugin.getBundle().getSymbolicName(), plugin);
      this.context = context;
      this.plugin = plugin;
   }

   /**
    * returns a File to from the default persistent storage area provided for the bundle by the Framework (.ie.)
    * myworkspace/.metadata/.plugins/org.eclipse.pde.core/myPlugin/...
    */
   public File getPluginStoreFile(String path) {
      return context.getDataFile(path);
   }

   /**
    * finds a resource in the plugin bundle and writes it out to the default persistent storage area as a regular file
    * 
    * @param path
    * @return Return plugin file reference
    * @throws IOException
    */
   public File getPluginFile(String path) throws IOException {
      File result = getPluginStoreFile(path);
      result.getParentFile().mkdirs();
      InputStream inStream = getInputStream(path);
      Lib.inputStreamToFile(inStream, result);
      if (!result.exists()) {
         return null;
      }
      return result;
   }

   public InputStream getInputStream(String resource) throws IOException {
      return plugin.getBundle().getEntry(resource).openStream();
   }

   public List<URL> getInputStreams(String directory, String pattern, boolean recurse) throws IOException {
      Enumeration<?> enumeration = plugin.getBundle().findEntries(directory, pattern, recurse);
      List<URL> inputs = new ArrayList<URL>();
      while (enumeration.hasMoreElements()) {
         inputs.add(((URL) enumeration.nextElement()));
      }
      return inputs;
   }
}
