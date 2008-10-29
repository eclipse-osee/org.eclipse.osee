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
package org.eclipse.osee.framework.core.client.internal;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.osee.framework.core.client.server.HttpUrlBuilder;
import org.eclipse.osee.framework.core.data.OseeApplicationServerContext;
import org.eclipse.osee.framework.core.data.OseeCodeVersion;
import org.eclipse.osee.framework.core.data.OseeServerInfo;
import org.eclipse.osee.framework.core.exception.OseeArbitrationServerException;
import org.eclipse.osee.framework.jdk.core.util.HttpProcessor;
import org.eclipse.osee.framework.jdk.core.util.OseeProperties;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.jdk.core.util.HttpProcessor.AcquireResult;

/**
 * @author Andrew M. Finkbeiner
 */
public class OseeApplicationServer {

   private static String oseeServer = null;
   private static boolean serverStatus = false;
   private static OseeServerInfo serverInfo = null;

   private OseeApplicationServer() {
   }

   public static String getOseeApplicationServer() throws OseeArbitrationServerException {
      checkAndUpdateStatus();
      if (Strings.isValid(oseeServer) != true) {
         throw new OseeArbitrationServerException("Invalid resource server address");
      }
      return oseeServer;
   }

   public static boolean isApplicationServerAlive() {
      try {
         checkAndUpdateStatus();
      } catch (Exception ex) {
      }
      return serverStatus;
   }

   private static void checkAndUpdateStatus() throws OseeArbitrationServerException {
      try {
         if (serverInfo == null) {
            String overrideValue = OseeProperties.getInstance().getOseeApplicationServerOverride();
            if (Strings.isValid(overrideValue)) {
               serverInfo = fromString(overrideValue);
            } else {
               serverInfo = getOseeServerAddress();
            }
         }
      } finally {
         boolean canConnect = false;
         HttpURLConnection connection = null;
         try {
            URL url = new URL(oseeServer);
            connection = (HttpURLConnection) url.openConnection();
            connection.connect();
            canConnect = true;
         } catch (Exception ex) {
         } finally {
            if (connection != null) {
               connection.disconnect();
            }
         }
         if (canConnect != serverStatus) {
            serverStatus = canConnect;
            //            notifyConnectionListeners();
         }
      }
   }

   public static OseeServerInfo fromString(String value) {
      Pattern pattern = Pattern.compile("http://(.*):(\\d+)");
      Matcher matcher = pattern.matcher(value);
      if (matcher.find()) {
         String address = matcher.group(1);
         int port = Integer.valueOf(matcher.group(2));
         return new OseeServerInfo(address, port, "OVERRIDE", new Timestamp(new Date().getTime()), true);
      }
      return null;
   }

   private static OseeServerInfo getOseeServerAddress() throws OseeArbitrationServerException {
      OseeServerInfo oseeServerInfo = null;
      Map<String, String> parameters = new HashMap<String, String>();
      parameters.put("version", OseeCodeVersion.getVersion());

      ByteArrayOutputStream outputStream = null;
      InputStream inputStream = null;
      try {
         String url =
               HttpUrlBuilder.getInstance().getOsgiServletServiceUrl(OseeApplicationServerContext.LOOKUP_CONTEXT,
                     parameters);

         outputStream = new ByteArrayOutputStream();
         AcquireResult result = HttpProcessor.acquire(new URL(url), outputStream);
         if (result.getCode() == HttpURLConnection.HTTP_OK) {
            inputStream = new ByteArrayInputStream(outputStream.toByteArray());
            oseeServerInfo = OseeServerInfo.fromXml(inputStream);
         }
      } catch (Exception ex) {
         throw new OseeArbitrationServerException(ex);
      } finally {
         if (inputStream != null) {
            try {
               inputStream.close();
            } catch (IOException ex) {
               throw new OseeArbitrationServerException(ex);
            }
         }
         if (outputStream != null) {
            try {
               outputStream.close();
            } catch (IOException ex) {
               throw new OseeArbitrationServerException(ex);
            }
         }
      }
      return oseeServerInfo;
   }

}
