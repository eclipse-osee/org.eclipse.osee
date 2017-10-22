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
package org.eclipse.osee.framework.server.ide.internal;

import java.util.Collection;
import org.eclipse.osee.framework.core.server.OseeHttpServlet;
import org.osgi.service.http.HttpService;

/**
 * @author Roberto E. Escobar
 */
public final class ServletUtil {

   private ServletUtil() {
      // Utility Class
   }

   private static String normalizeContext(String contextName) {
      return !contextName.startsWith("/") ? "/" + contextName : contextName;
   }

   public static void register(HttpService httpService, OseeHttpServlet servlet, String context) {
      try {
         String contextName = normalizeContext(context);
         httpService.registerServlet(contextName, servlet, null, null);
         System.out.println(String.format("Registered servlet '%s'", contextName));
      } catch (Exception ex) {
         throw new RuntimeException(ex);
      }
   }

   public static void unregister(HttpService httpService, Collection<String> contexts) {
      for (String context : contexts) {
         String contextName = normalizeContext(context);
         if (httpService != null) {
            httpService.unregister(contextName);
         }
         System.out.println(String.format("De-registering servlet '%s'", contextName));
      }
   }
}
