/*********************************************************************
 * Copyright (c) 2018 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.framework.core.util;

import java.io.File;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;

/**
 * Helper class to quickly access OSEE files in OSEE-INF directory
 *
 * @author Donald G. Dunne
 */
public class OseeInf {

   public static final String ROOT_DIR = "OSEE-INF/";

   public static InputStream getResourceAsStream(String path, Class<?> classFromBundle) {
      return OsgiUtil.getResourceAsStream(classFromBundle, ROOT_DIR + path);
   }

   /**
    * Uses the default classloader for the provided object to load the resource.
    * 
    * @param filePath File path of resource to load. Sometimes the classloader will assume OSEE-INF is the root dir of
    * the path and sometimes it doesn't. If the resource doesn't exist at the root then the OSEE-INF dir is prepended to
    * the path and tried again.
    * @param obj Object whose classloader is to be used
    * @return The resource found at the classloader root or in OSEE-INF, in that order.
    * @throws OseeCoreException If resource not found
    */
   public static InputStream getResourceAsStream(String filePath, Object obj) throws OseeCoreException {
      ClassLoader classLoader = obj.getClass().getClassLoader();
      InputStream resourceAsStream = classLoader.getResourceAsStream(filePath);
      if (resourceAsStream == null) {
         resourceAsStream = classLoader.getResourceAsStream(ROOT_DIR + filePath);
      }

      if (resourceAsStream != null) {
         return resourceAsStream;
      } else {
         throw new OseeCoreException("Resource not found at %s nor %s", filePath, ROOT_DIR + filePath);
      }
   }

   public static String getResourceContents(String path, Class<?> classFromBundle) {
      return OsgiUtil.getResourceAsString(classFromBundle, ROOT_DIR + path);
   }

   public static File getResourceAsFile(String path, Class<?> clazz) {
      try {
         URL url = getResourceAsUrl(path, clazz);
         String uri = new URI(url.toString().replace(" ", "%20")).getPath();
         return new File(uri);
      } catch (Exception ex) {
         throw OseeCoreException.wrap(ex);
      }
   }

   public static URL getResourceAsUrl(String path, Class<?> clazz) {
      return OsgiUtil.getResourceAsUrl(clazz, ROOT_DIR + path);
   }
}