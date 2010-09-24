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
import java.util.List;
import org.eclipse.core.runtime.Platform;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.osgi.framework.Bundle;

/**
 * @author Ryan D. Brooks
 */
public class PluginUtil {
   private final String pluginId;

   public PluginUtil(String pluginId) {
      this.pluginId = pluginId;
   }

   /**
    * returns a File to from the default persistent storage area provided for the bundle by the Framework (.ie.)
    * myworkspace/.metadata/.plugins/org.eclipse.pde.core/myPlugin/...
    */
   public File getPluginStoreFile(String path) {
      Bundle bundle = Platform.getBundle(pluginId);
      return bundle.getBundleContext().getDataFile(path);
   }

   /**
    * finds a resource in the plugin bundle and writes it out to the default persistent storage area as a regular file
    * 
    * @return Return plugin file reference
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
      Bundle bundle = Platform.getBundle(pluginId);
      return bundle.getEntry(resource).openStream();
   }

   public List<URL> getInputStreams(String directory, String pattern, boolean recurse) {
      Bundle bundle = Platform.getBundle(pluginId);
      Enumeration<?> enumeration = bundle.findEntries(directory, pattern, recurse);
      List<URL> inputs = new ArrayList<URL>();
      while (enumeration.hasMoreElements()) {
         inputs.add(((URL) enumeration.nextElement()));
      }
      return inputs;
   }

}
