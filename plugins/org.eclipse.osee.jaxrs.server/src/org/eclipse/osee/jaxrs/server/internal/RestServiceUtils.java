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
package org.eclipse.osee.jaxrs.server.internal;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import javax.ws.rs.core.Application;
import org.osgi.framework.ServiceReference;

/**
 * @author Roberto E. Escobar
 */
public final class RestServiceUtils {

   private RestServiceUtils() {
      // Utility class
   }

   public static String getContextName(ServiceReference<?> reference) {
      String contextName = (String) reference.getProperty("context.name");
      if (!isValid(contextName)) {
         contextName = getComponentName(reference);
      }
      return normalize(contextName);
   }

   public static String getComponentName(ServiceReference<?> reference) {
      return (String) reference.getProperty("component.name");
   }

   public static void checkValid(Application application) throws Exception {
      if (application == null) {
         throw new IllegalStateException("javax.ws.rs.Application service was null");
      }
      if (!hasResources(application)) {
         throw new IllegalStateException("javax.ws.rs.Application had no resources declared");
      }
   }

   private static boolean hasResources(Application app) {
      return !isNullOrEmpty(app.getClasses()) || !isNullOrEmpty(app.getSingletons());
   }

   private static String normalize(String contextName) {
      return contextName != null && !contextName.startsWith("/") ? "/" + contextName : contextName;
   }

   private static boolean isValid(String value) {
      return value != null && value.length() > 0;
   }

   private static boolean isNullOrEmpty(Collection<?> collection) {
      return collection == null || collection.isEmpty();
   }

   public static Map<String, String> toMap(String componentName, String contextName) {
      Map<String, String> data = new HashMap<String, String>();
      data.put("component.name", componentName);
      data.put("context.name", contextName);
      return data;
   }

}
