/*******************************************************************************
 * Copyright (c) 2010 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package lba.ote.server.core;


public class OteUtil {

   public static String generateBundleVersionString(String bundleSpecificVersion, String symbolicName, String version, byte[] md5){
      StringBuilder sb = new StringBuilder();
      if(bundleSpecificVersion != null){
         sb.append(bundleSpecificVersion);
         sb.append("_");
      }
      sb.append(symbolicName);
      sb.append("_");
      sb.append(version);
      sb.append("_");
      for(byte b:md5){
         sb.append(String.format("%X", b));
      }
      return sb.toString();
   }
}
