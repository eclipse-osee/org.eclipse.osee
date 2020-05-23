/*********************************************************************
 * Copyright (c) 2014 Boeing
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

package org.eclipse.osee.config.admin.internal;

import java.io.File;
import java.net.URI;
import java.util.Dictionary;
import java.util.Enumeration;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.osgi.service.cm.Configuration;

/**
 * @author Roberto E. Escobar
 */
public final class ConfigUtil {

   private ConfigUtil() {
      // Utility class
   }

   public static String getDefaultConfig() {
      return System.getProperty(ConfigManagerConstants.CONFIGURATION_URI, "");
   }

   public static void writeConfig(Configuration config, StringBuilder buffer) {
      buffer.append("PID = ").append(config.getPid()).append("\n");
      String factoryPid = config.getFactoryPid();
      if (factoryPid != null) {
         buffer.append("FactoryPID = ").append(factoryPid).append("\n");
      }
      String location = config.getBundleLocation();
      location = location != null ? location : " < unbound > ";

      buffer.append("Bundle-Location : ").append(location).append("\n");

      buffer.append("Contents :\n");
      Dictionary<String, Object> dict = config.getProperties();
      if (dict != null) {
         Enumeration<String> keys = dict.keys();
         while (keys.hasMoreElements()) {
            Object key = keys.nextElement();
            Object value = dict.get(key);
            buffer.append("\t").append(key).append("=").append(value).append("\n");
         }
      }
   }

   public static URI asUri(String configUri) {
      URI toReturn = null;
      if (Strings.isValid(configUri)) {
         if (!configUri.contains("://")) {
            configUri = configUri.replaceAll("\\\\", "/");
            if (configUri.startsWith("file:/")) {
               toReturn = URI.create(configUri);
            } else {
               File file = new File(configUri);
               toReturn = file.toURI();
            }
         } else {
            toReturn = URI.create(configUri);
         }
      }
      return toReturn;
   }
}
