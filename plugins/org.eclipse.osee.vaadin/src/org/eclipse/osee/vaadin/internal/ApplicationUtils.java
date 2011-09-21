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
package org.eclipse.osee.vaadin.internal;

import java.util.Dictionary;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import org.eclipse.osee.vaadin.ApplicationConstants;
import org.eclipse.osee.vaadin.ApplicationFactory;
import org.osgi.framework.ServiceReference;
import com.vaadin.terminal.gwt.server.Constants;

/**
 * @author Roberto E. Escobar
 */
public final class ApplicationUtils {

   private ApplicationUtils() {
      // Utility class
   }

   public static String getContextName(ServiceReference<?> reference) {
      String contextName = (String) reference.getProperty(ApplicationConstants.APP_CONTEXT_NAME);
      if (!isValid(contextName)) {
         contextName = getComponentName(reference);
      }
      return normalize(contextName);
   }

   public static String getComponentName(ServiceReference<?> reference) {
      return (String) reference.getProperty(ApplicationConstants.APP_COMPONENT_NAME);
   }

   public static void checkValid(ApplicationFactory application) throws Exception {
      if (application == null) {
         throw new IllegalStateException("ApplicationFactory service was null");
      }
   }

   public static String normalize(String contextName) {
      return contextName != null && !contextName.startsWith("/") ? "/" + contextName : contextName;
   }

   private static boolean isValid(String value) {
      return value != null && value.length() > 0;
   }

   public static Dictionary<String, String> getConfigParams(Map<String, Object> properties) {
      String productionMode = (String) properties.get(Constants.SERVLET_PARAMETER_PRODUCTION_MODE);
      Dictionary<String, String> initParams = new Hashtable<String, String>();
      if (productionMode != null) {
         initParams.put(Constants.SERVLET_PARAMETER_PRODUCTION_MODE, productionMode);
      }
      return initParams;
   }

   public static Map<String, String> toMap(ServiceReference<ApplicationFactory> reference) {
      Map<String, String> data = new HashMap<String, String>();
      for (String key : reference.getPropertyKeys()) {
         Object object = reference.getProperty(key);
         data.put(key, String.valueOf(object));
      }
      return data;
   }
}
