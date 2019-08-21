/*******************************************************************************
 * Copyright (c) 2018 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.core.util;

import java.io.File;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

/**
 * Helper class to quickly access OSEE files in OSEE-INF directory
 *
 * @author Donald G. Dunne
 */
public class OseeInf {

   public static InputStream getResourceAsStream(String path, Class<?> clazz) {
      try {
         URL url = getResourceAsUrl(path, clazz);
         return url.openStream();
      } catch (Exception ex) {
         throw new OseeCoreException(ex, "Error getting resource [%s] as file", path);
      }
   }

   public static String getResourceContents(String path, Class<?> clazz) {
      try {
         return Lib.inputStreamToString(getResourceAsStream(path, clazz));
      } catch (Exception ex) {
         throw new OseeCoreException(ex, "Error getting resource [%s] as file", path);
      }
   }

   public static File getResourceAsFile(String path, Class<?> clazz) {
      try {
         URL url = getResourceAsUrl(path, clazz);
         String uri = new URI(url.toString().replace(" ", "%20")).getPath();
         return new File(uri);
      } catch (Exception ex) {
         throw new OseeCoreException(ex, "Error getting resource [%s] as file", path);
      }
   }

   public static URL getResourceAsUrl(String path, Class<?> clazz) {
      Bundle bundle = FrameworkUtil.getBundle(clazz);
      String inf = "OSEE-INF/" + path;

      URL url = null;
      try {
         url = bundle.getResource(inf);
      } catch (Exception ex) {
         System.out.println("Exception caught from class loader: " + clazz.getClassLoader().toString());
      }
      try {
         return FileLocator.toFileURL(url);
      } catch (Exception ex) {
         System.out.println("Exception caught at: " + inf + "  " + ex.getLocalizedMessage());
         throw new OseeCoreException(ex, "Error getting resource [%s] as file", path);
      }
   }

}