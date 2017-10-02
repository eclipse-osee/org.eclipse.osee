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

package org.eclipse.osee.framework.core.client.server;

import java.io.UnsupportedEncodingException;
import java.util.Map;
import java.util.logging.Level;
import org.eclipse.osee.framework.core.client.CoreClientConstants;
import org.eclipse.osee.framework.core.client.OseeClientProperties;
import org.eclipse.osee.framework.core.client.internal.CoreClientActivator;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.OseeStateException;
import org.eclipse.osee.framework.jdk.core.util.HttpUrlBuilder;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLog;

/**
 * @author Roberto E. Escobar
 */
public final class HttpUrlBuilderClient {

   public static final String USE_CONNECTED_SERVER_URL_FOR_PERM_LINKS =
      CoreClientConstants.getBundleId() + "osee.use.connected.server.url.for.perm.links";

   private static final String urlPrefixFormat = "http://%s:%s/";
   private static final HttpUrlBuilderClient instance = new HttpUrlBuilderClient();

   private HttpUrlBuilderClient() {
      // Singleton
   }

   public static HttpUrlBuilderClient getInstance() {
      return instance;
   }

   public boolean isUseConnectedServerUrl() {
      boolean isUseConnectedServerUrl =
         CoreClientActivator.getInstance().getPluginPreferences().getBoolean(USE_CONNECTED_SERVER_URL_FOR_PERM_LINKS);
      return isUseConnectedServerUrl;
   }

   public String getUrlForLocalSkynetHttpServer(String context, Map<String, String> parameters) {
      try {
         return HttpUrlBuilder.createURL(getHttpLocalServerPrefix(), context, parameters);
      } catch (UnsupportedEncodingException ex) {
         OseeLog.log(CoreClientActivator.class, Level.SEVERE, ex);
      }
      return null;
   }

   public String getHttpLocalServerPrefix() {
      int port = HttpServer.getDefaultServicePort();
      if (port == -1) {
         throw new OseeStateException("Http Server was not launched by this workbench - Ensure port was set correctly");
      }
      return String.format(urlPrefixFormat, HttpServer.getLocalServerAddress(), port);
   }

   public String getApplicationServerPrefix() {
      String address = OseeClientProperties.getOseeApplicationServer();
      return normalize(address);
   }

   private static String normalize(String address) {
      String toReturn = address;
      if (Strings.isValid(toReturn) && !toReturn.endsWith("/")) {
         toReturn += "/";
      }
      return toReturn;
   }

   public String getOsgiServletServiceUrl(String context, Map<String, String> parameters) {
      String url = null;
      try {
         String applicationServerPrefix = getApplicationServerPrefix();
         if (Strings.isValid(applicationServerPrefix)) {
            url = HttpUrlBuilder.createURL(applicationServerPrefix, context, parameters);
         }
      } catch (UnsupportedEncodingException ex) {
         throw OseeCoreException.wrap(ex);
      }
      return url;
   }

}
