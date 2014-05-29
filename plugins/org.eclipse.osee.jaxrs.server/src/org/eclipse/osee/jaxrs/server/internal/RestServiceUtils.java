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
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.osgi.framework.ServiceReference;

/**
 * @author Roberto E. Escobar
 */
public final class RestServiceUtils {

   private RestServiceUtils() {
      // Utility class
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

   public static String getApplicationPath(String componentName, Application application) {
      String applicationPath = getApplicationPath(application);
      return Strings.isValid(applicationPath) ? applicationPath : normalize(componentName);
   }

   public static String getApplicationPath(Application application) {
      String toReturn = null;
      Class<? extends Application> clazz = application.getClass();
      ApplicationPath applicationPath = clazz.getAnnotation(ApplicationPath.class);
      if (applicationPath != null) {
         toReturn = normalize(applicationPath.value());
      }
      return toReturn;
   }

   public static boolean hasPath(String value) {
      String toCheck = value;
      if (toCheck != null) {
         toCheck = toCheck.trim();
      }
      if (toCheck.startsWith("/")) {
         toCheck = toCheck.substring(1);
      }
      return Strings.isValid(toCheck);
   }

   private static boolean hasResources(Application app) {
      return !isNullOrEmpty(app.getClasses()) || !isNullOrEmpty(app.getSingletons());
   }

   private static String normalize(String contextName) {
      return contextName != null && !contextName.startsWith("/") ? "/" + contextName : contextName;
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
