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
import java.net.URLEncoder;
import java.util.Map;
import java.util.logging.Level;
import org.eclipse.osee.framework.core.client.CoreClientActivator;
import org.eclipse.osee.framework.core.client.OseeClientProperties;
import org.eclipse.osee.framework.core.client.internal.OseeApplicationServer;
import org.eclipse.osee.framework.core.exception.OseeArbitrationServerException;
import org.eclipse.osee.framework.db.connection.exception.OseeDataStoreException;
import org.eclipse.osee.framework.logging.OseeLog;

/**
 * @author Roberto E. Escobar
 */
public class HttpUrlBuilder {
   private static final String urlPrefixFormat = "http://%s:%s/";
   private static HttpUrlBuilder instance = null;

   private HttpUrlBuilder() {
   }

   public static HttpUrlBuilder getInstance() {
      if (instance == null) {
         instance = new HttpUrlBuilder();
      }
      return instance;
   }

   private String encode(String value) throws UnsupportedEncodingException {
      return URLEncoder.encode(value, "UTF-8");
   }

   public String getParametersAsEncodedUrl(Map<String, String> keyValues) throws UnsupportedEncodingException {
      StringBuilder sb = new StringBuilder();
      for (String key : keyValues.keySet()) {
         sb.append(encode(key));
         sb.append("=");
         sb.append(encode(keyValues.get(key)));
         sb.append("&");
      }
      if (sb.length() - 1 >= 0) {
         // Delete the last unnecessary '&'
         sb.deleteCharAt(sb.length() - 1);
      }
      return sb.toString();
   }

   public String getLocalServerPrefix(String serviceName) {
      int port = HttpServer.getPortByServiceName(serviceName);
      if (port == -1) {
         throw new IllegalStateException(
               "Http Server was not launched by this workbench - Ensure port was set correctly");
      }
      return String.format(urlPrefixFormat, HttpServer.getLocalServerAddress(), port);
   }

   private String buildUrl(String prefix, String context, String parameters) {
      StringBuilder sb = new StringBuilder();
      sb.append(prefix);
      sb.append(context);
      sb.append("?");
      sb.append(parameters);
      return sb.toString();
   }

   public String getUrlForLocalSkynetHttpServer(String context, Map<String, String> parameters) {
      try {
         return buildUrl(getSkynetHttpLocalServerPrefix(), context, getParametersAsEncodedUrl(parameters));
      } catch (UnsupportedEncodingException ex) {
         OseeLog.log(CoreClientActivator.class, Level.SEVERE, ex);
      }
      return null;
   }

   public String getSkynetHttpLocalServerPrefix() {
      return getLocalServerPrefix(HttpServer.DEFAULT_SERVICE_NAME);
   }

   public String getApplicationServerPrefix() throws OseeDataStoreException, OseeArbitrationServerException {
      String address = OseeApplicationServer.getOseeApplicationServer();
      if (address.endsWith("/") != true) {
         address += "/";
      }
      return address;
   }

   public String getArbitrationServerPrefix() throws OseeDataStoreException {
      String address = OseeClientProperties.getOseeArbitrationServer();
      if (address.endsWith("/") != true) {
         address += "/";
      }
      return address;
   }

   public String getOsgiServletServiceUrl(String context, Map<String, String> parameters) throws OseeDataStoreException {
      try {
         return buildUrl(getApplicationServerPrefix(), context, getParametersAsEncodedUrl(parameters));
      } catch (UnsupportedEncodingException ex) {
         throw new OseeDataStoreException(ex);
      }
   }

   public String getOsgiArbitrationServiceUrl(String context, Map<String, String> parameters) throws OseeDataStoreException {
      try {
         return buildUrl(getArbitrationServerPrefix(), context, getParametersAsEncodedUrl(parameters));
      } catch (UnsupportedEncodingException ex) {
         throw new OseeDataStoreException(ex);
      }
   }
}
