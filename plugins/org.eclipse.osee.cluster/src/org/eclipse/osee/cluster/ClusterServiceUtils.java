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
package org.eclipse.osee.cluster;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Roberto E. Escobar
 */
public final class ClusterServiceUtils {

   private ClusterServiceUtils() {
      // Utility class
   }

   public static Map<String, Object> toMap(String componentName, String contextName) {
      Map<String, Object> data = new HashMap<>();
      data.put("component.name", componentName);
      data.put("context.name", contextName);
      return data;
   }

   public static String getContextName(Map<String, Object> properties) {
      String contextName = (String) properties.get("context.name");
      if (!isValid(contextName)) {
         contextName = getComponentName(properties);
      }
      return normalize(contextName);
   }

   public static String getComponentName(Map<String, Object> properties) {
      return (String) properties.get("component.name");
   }

   private static boolean isValid(String value) {
      return value != null && value.length() > 0;
   }

   private static String normalize(String contextName) {
      return contextName != null && !contextName.startsWith("/") ? "/" + contextName : contextName;
   }

   public static String getConfigurationURL(Map<String, Object> properties) {
      return (String) properties.get(ClusterConstants.CLUSTER_CONFIG_URL);
   }

}
