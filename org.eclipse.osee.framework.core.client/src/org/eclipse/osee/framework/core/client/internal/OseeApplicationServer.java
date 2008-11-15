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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.osee.framework.core.client.CoreClientActivator;
import org.eclipse.osee.framework.core.client.OseeClientProperties;
import org.eclipse.osee.framework.core.client.server.HttpUrlBuilder;
import org.eclipse.osee.framework.core.data.OseeCodeVersion;
import org.eclipse.osee.framework.core.data.OseeServerContext;
import org.eclipse.osee.framework.core.data.OseeServerInfo;
import org.eclipse.osee.framework.core.exception.OseeArbitrationServerException;
import org.eclipse.osee.framework.db.connection.exception.OseeDataStoreException;
import org.eclipse.osee.framework.jdk.core.util.HttpProcessor;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.jdk.core.util.HttpProcessor.AcquireResult;
import org.eclipse.osee.framework.logging.BaseStatus;
import org.eclipse.osee.framework.logging.OseeLog;

/**
 * @author Andrew M. Finkbeiner
 */
public class OseeApplicationServer {

   private static String oseeServer = null;
   private static boolean isServerAlive = false;
   private static OseeServerInfo serverInfo = null;
   private static final String ArbitrationService = "Arbitration Server";
   private static final String ApplicationServer = "Application Server";

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
      checkAndUpdateStatus();
      return isServerAlive;
   }

   private static void checkAndUpdateStatus() {
      isServerAlive = false;
      if (serverInfo == null) {
         String overrideValue = OseeClientProperties.getOseeApplicationServer();
         if (Strings.isValid(overrideValue)) {
            serverInfo = fromString(overrideValue);
         } else {
            serverInfo = getOseeServerAddress();
         }
         if (serverInfo != null) {
            oseeServer = String.format("http://%s:%s/", serverInfo.getServerAddress(), serverInfo.getPort());
         }
      }
      DateFormat format = new SimpleDateFormat("MM/dd/yyyy hh:mm a");
      if (oseeServer == null) {
         OseeLog.reportStatus(new BaseStatus(ApplicationServer, Level.SEVERE, "Application server address was null"));
      } else {
         isServerAlive = checkStatus();
         if (isServerAlive) {
            OseeLog.reportStatus(new BaseStatus(ApplicationServer, Level.INFO, "%s [%s] Running Since: %s", oseeServer,
                  serverInfo.getVersion(), format.format(serverInfo.getDateStarted())));
         }
      }
   }

   private static boolean checkStatus() {
      boolean canConnect = false;
      HttpURLConnection connection = null;
      try {
         URL url = new URL(oseeServer);
         connection = (HttpURLConnection) url.openConnection();
         connection.connect();
         canConnect = true;
      } catch (Exception ex) {
         OseeLog.reportStatus(new BaseStatus(ApplicationServer, Level.SEVERE, ex));
      } finally {
         if (connection != null) {
            connection.disconnect();
         }
      }
      return canConnect;
   }

   public static OseeServerInfo fromString(String value) {
      Pattern pattern = Pattern.compile("http://(.*):(\\d+)");
      Matcher matcher = pattern.matcher(value);
      if (matcher.find()) {
         String address = matcher.group(1);
         int port = Integer.valueOf(matcher.group(2));
         return new OseeServerInfo("OVERRIDE", address, port, "OVERRIDE", new Timestamp(new Date().getTime()), true);
      }
      return null;
   }

   private static OseeServerInfo getOseeServerAddress() {
      OseeServerInfo oseeServerInfo = null;
      ByteArrayOutputStream outputStream = null;
      InputStream inputStream = null;
      try {
         Map<String, String> parameters = new HashMap<String, String>();
         parameters.put("version", OseeCodeVersion.getVersion());
         String url =
               HttpUrlBuilder.getInstance().getOsgiArbitrationServiceUrl(OseeServerContext.LOOKUP_CONTEXT, parameters);

         outputStream = new ByteArrayOutputStream();
         AcquireResult result = HttpProcessor.acquire(new URL(url), outputStream);
         try {
            OseeLog.reportStatus(new BaseStatus(ArbitrationService, Level.INFO, "%s",
                  HttpUrlBuilder.getInstance().getArbitrationServerPrefix()));
         } catch (OseeDataStoreException ex) {
            OseeLog.log(CoreClientActivator.class, Level.SEVERE, ex);
         }
         if (result.getCode() == HttpURLConnection.HTTP_OK) {
            inputStream = new ByteArrayInputStream(outputStream.toByteArray());
            oseeServerInfo = OseeServerInfo.fromXml(inputStream);
         }
      } catch (Exception ex) {
         OseeLog.log(CoreClientActivator.class, Level.SEVERE, ex);
         OseeLog.reportStatus(new BaseStatus(ArbitrationService, Level.SEVERE, ex,
               "Error requesting application server for version [%s]", OseeCodeVersion.getVersion()));
      } finally {
         if (inputStream != null) {
            try {
               inputStream.close();
            } catch (IOException ex) {
               OseeLog.log(CoreClientActivator.class, Level.SEVERE, ex);
            }
         }
         if (outputStream != null) {
            try {
               outputStream.close();
            } catch (IOException ex) {
               OseeLog.log(CoreClientActivator.class, Level.SEVERE, ex);
            }
         }
      }
      return oseeServerInfo;
   }
}
