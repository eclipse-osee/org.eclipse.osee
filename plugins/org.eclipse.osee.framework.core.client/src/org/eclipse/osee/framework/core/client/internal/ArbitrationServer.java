/*
 * Created on Aug 3, 2011
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.core.client.internal;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import org.eclipse.osee.framework.core.client.CoreClientActivator;
import org.eclipse.osee.framework.core.client.server.HttpUrlBuilderClient;
import org.eclipse.osee.framework.core.data.OseeCodeVersion;
import org.eclipse.osee.framework.core.data.OseeServerContext;
import org.eclipse.osee.framework.core.data.OseeServerInfo;
import org.eclipse.osee.framework.core.util.HttpProcessor;
import org.eclipse.osee.framework.core.util.HttpProcessor.AcquireResult;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLog;

class ArbitrationServer extends OseeServer {

   public ArbitrationServer() {
      super("Arbitration Server");
   }

   public void acquireApplicationServer(ApplicationServer applicationServer) {
      if (applicationServer.isOverrideArbitration()) {
         set(Level.INFO, null, "Arbitration Overridden");
         setAlive(true);
      }
      reset();
      ByteArrayOutputStream outputStream = null;
      InputStream inputStream = null;
      AcquireResult result = null;
      try {
         Map<String, String> parameters = new HashMap<String, String>();
         parameters.put("version", OseeCodeVersion.getVersion());
         String url =
            HttpUrlBuilderClient.getInstance().getOsgiArbitrationServiceUrl(OseeServerContext.LOOKUP_CONTEXT,
               parameters);

         outputStream = new ByteArrayOutputStream();
         result = HttpProcessor.acquire(new URL(url), outputStream);
      } catch (Exception ex) {
         OseeLog.log(CoreClientActivator.class, Level.SEVERE, ex);
         set(Level.SEVERE, ex, "Error connecting - " + ex.getLocalizedMessage());
         applicationServer.reset();
         applicationServer.set(Level.SEVERE, null, "Arbitration Server Unavailable");
         return;
      }
      try {
         set(Level.INFO, null, HttpUrlBuilderClient.getInstance().getArbitrationServerPrefix());
         if (result.getCode() == HttpURLConnection.HTTP_OK) {
            inputStream = new ByteArrayInputStream(outputStream.toByteArray());
            applicationServer.setServerInfo(OseeServerInfo.fromXml(inputStream));
            set(Level.INFO, null, HttpUrlBuilderClient.getInstance().getArbitrationServerPrefix());
         } else {
            String arbitrationServerMessage = result.getResult();
            if (!Strings.isValid(arbitrationServerMessage)) {
               arbitrationServerMessage =
                  String.format("Error requesting application server for version [%s]", OseeCodeVersion.getVersion());
            }
            applicationServer.set(Level.SEVERE, null, arbitrationServerMessage);
         }
      } catch (Exception ex) {
         set(Level.SEVERE, ex, "Error retrieving application server  - " + ex.getLocalizedMessage());
         applicationServer.reset();
         applicationServer.set(Level.SEVERE, null, "Arbitration Server Error");
      } finally {
         if (inputStream != null) {
            try {
               inputStream.close();
            } catch (IOException ex) {
               OseeLog.log(CoreClientActivator.class, Level.SEVERE, ex);
            }
         }
         try {
            outputStream.close();
         } catch (IOException ex) {
            OseeLog.log(CoreClientActivator.class, Level.SEVERE, ex);
         }
      }
   }

   @Override
   public String report() {
      String errorStr = super.report();
      if (!Strings.isValid(errorStr)) {
         errorStr = "";
      }
      System.setProperty("osee.arbitration.server.error", errorStr);
      return errorStr;
   }

}
