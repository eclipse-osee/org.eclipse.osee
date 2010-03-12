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
import org.eclipse.osee.framework.core.client.CoreClientActivator;
import org.eclipse.osee.framework.core.client.OseeClientProperties;
import org.eclipse.osee.framework.core.client.internal.OseeApplicationServer;
import org.eclipse.osee.framework.core.exception.OseeArbitrationServerException;
import org.eclipse.osee.framework.core.exception.OseeDataStoreException;
import org.eclipse.osee.framework.core.exception.OseeStateException;
import org.eclipse.osee.framework.jdk.core.util.HttpUrlBuilder;
import org.eclipse.osee.framework.logging.OseeLog;

/**
 * @author Roberto E. Escobar
 */
public class HttpUrlBuilderClient {
   private static final String urlPrefixFormat = "http://%s:%s/";
   private static final HttpUrlBuilderClient instance = new HttpUrlBuilderClient();

   private HttpUrlBuilderClient() {
   }

   public static HttpUrlBuilderClient getInstance() {
      return instance;
   }

   public String getUrlForLocalSkynetHttpServer(String context, Map<String, String> parameters) throws OseeStateException {
      try {
         return HttpUrlBuilder.createURL(getHttpLocalServerPrefix(), context, parameters);
      } catch (UnsupportedEncodingException ex) {
         OseeLog.log(CoreClientActivator.class, Level.SEVERE, ex);
      }
      return null;
   }

   public String getHttpLocalServerPrefix() throws OseeStateException {
      int port = HttpServer.getDefaultServicePort();
      if (port == -1) {
         throw new OseeStateException("Http Server was not launched by this workbench - Ensure port was set correctly");
      }
      return String.format(urlPrefixFormat, HttpServer.getLocalServerAddress(), port);
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
         return HttpUrlBuilder.createURL(getApplicationServerPrefix(), context, parameters);
      } catch (UnsupportedEncodingException ex) {
         throw new OseeDataStoreException(ex);
      }
   }

   public String getOsgiArbitrationServiceUrl(String context, Map<String, String> parameters) throws OseeDataStoreException {
      try {
         return HttpUrlBuilder.createURL(getArbitrationServerPrefix(), context, parameters);
      } catch (UnsupportedEncodingException ex) {
         throw new OseeDataStoreException(ex);
      }
   }
}
