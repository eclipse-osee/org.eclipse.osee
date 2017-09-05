/*******************************************************************************
 * Copyright (c) 2017 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.core.internal.util;

import java.io.File;
import java.net.URL;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

/**
 * @author Donald G. Dunne
 */
public class OseeResource {

   private static final String OSEE_INF = "OSEE-INF/";

   /**
    * @return Contents of resource starting at path org.eclipse.osee.ats.rest/OSEE-INF
    */
   public static String getResource(String path) throws Exception {
      Bundle bundle = FrameworkUtil.getBundle(OseeResource.class);
      URL url = bundle.getEntry(OSEE_INF + path);
      return Lib.inputStreamToString(url.openStream());
   }

   public static File getResourceAsFile(String path) {
      Bundle bundle = FrameworkUtil.getBundle(OseeResource.class);
      URL url = bundle.getEntry(OSEE_INF + path);
      try {
         URL fileUrl = FileLocator.toFileURL(url);
         return new File(fileUrl.toURI().getPath());
      } catch (Exception ex) {
         throw new OseeCoreException(ex, "Error getting resource [%s] as file", path);
      }
   }

}
