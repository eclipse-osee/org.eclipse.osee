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

package org.eclipse.osee.framework.skynet.core.linking;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.sql.SQLException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.osee.framework.db.connection.core.OseeApplicationServer;
import org.eclipse.osee.framework.plugin.core.config.ConfigUtil;
import org.eclipse.osee.framework.skynet.core.SkynetActivator;
import org.eclipse.osee.framework.skynet.core.exception.OseeDataStoreException;
import org.eclipse.osee.framework.skynet.core.preferences.PreferenceConstants;

/**
 * @author Roberto E. Escobar
 */
public class HttpUrlBuilder {
   private static final Logger logger = ConfigUtil.getConfigFactory().getLogger(HttpUrlBuilder.class);
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

   public String getRemoteServerPrefix() {
      IPreferenceStore preferenceStore = SkynetActivator.getInstance().getPreferenceStore();
      String remoteAddress = preferenceStore.getString(PreferenceConstants.OSEE_REMOTE_HTTP_SERVER);
      if (remoteAddress.endsWith("/") != true) {
         remoteAddress += "/";
      }
      return remoteAddress;
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
      // TODO clean exception handling
      try {
         return buildUrl(getSkynetHttpLocalServerPrefix(), context, getParametersAsEncodedUrl(parameters));
      } catch (UnsupportedEncodingException ex) {
         logger.log(Level.SEVERE, ex.toString(), ex);
      }
      return null;
   }

   public String getUrlForRemoteSkynetHttpServer(String context, Map<String, String> parameters) {
      // TODO clean exception handling
      try {
         return buildUrl(getRemoteServerPrefix(), context, getParametersAsEncodedUrl(parameters));
      } catch (UnsupportedEncodingException ex) {
         logger.log(Level.SEVERE, ex.toString(), ex);
      }
      return null;
   }

   public String getSkynetHttpLocalServerPrefix() {
      return getLocalServerPrefix(HttpServer.DEFAULT_SERVICE_NAME);
   }

   public String getApplicationServerPrefix() throws SQLException {
      String address = OseeApplicationServer.getOseeApplicationServer();
      if (address.endsWith("/") != true) {
         address += "/";
      }
      return address;
   }

   public String getOsgiServletServiceUrl(String context, Map<String, String> parameters) throws OseeDataStoreException {
      try {
         return buildUrl(getApplicationServerPrefix(), context, getParametersAsEncodedUrl(parameters));
      } catch (Exception ex) {
         throw new OseeDataStoreException(ex);
      }
   }
}
