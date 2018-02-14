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
import org.eclipse.osee.framework.core.data.OseeData;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

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

   /**
    * @param path plugin relative path to resource in plugin
    * @param resultPath osee data relative path of the resultant file
    * @return File object of the copied file
    * @throws IOException
    */
   public File copyPluginFileToOseeData(String path, String resultPath) throws IOException {
      InputStream inStream = getInputStream(path);
      File resultFile = OseeData.getFile(resultPath);
      Lib.inputStreamToFile(inStream, resultFile);
      return resultFile;
   }

   public InputStream getInputStream(String resource) throws IOException {
      Bundle bundle = Platform.getBundle(pluginId);
      return bundle.getEntry(resource).openStream();
   }

   public List<URL> getInputStreams(String directory, String pattern, boolean recurse) {
      Bundle bundle = Platform.getBundle(pluginId);
      Enumeration<?> enumeration = bundle.findEntries(directory, pattern, recurse);
      List<URL> inputs = new ArrayList<>();
      while (enumeration.hasMoreElements()) {
         inputs.add((URL) enumeration.nextElement());
      }
      return inputs;
   }

   public BundleContext getBundleContext() {
      Bundle bundle = Platform.getBundle(pluginId);
      return bundle.getBundleContext();
   }

}
