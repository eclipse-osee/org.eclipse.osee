/*
 * Created on Aug 3, 2011
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.core.client.internal;

import java.sql.Timestamp;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.osee.framework.core.client.OseeClientProperties;
import org.eclipse.osee.framework.core.data.OseeServerInfo;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.util.Conditions;
import org.eclipse.osee.framework.jdk.core.util.Strings;

class ApplicationServer extends OseeServer {
   private OseeServerInfo serverInfo;
   private String oseeServer;
   private boolean overrideArbitration = false;

   public ApplicationServer() {
      super("Application Server");
   }

   public boolean hasServerInfo() {
      return serverInfo != null;
   }

   public OseeServerInfo getServerInfo() {
      return serverInfo;
   }

   public void setServerInfo(OseeServerInfo serverInfo) {
      this.serverInfo = serverInfo;
   }

   public String getOseeServer() {
      if (oseeServer == null && serverInfo != null) {
         oseeServer = String.format("http://%s:%s/", serverInfo.getServerAddress(), serverInfo.getPort());
      }
      return oseeServer;
   }

   public void setOseeServer(String oseeServer) {
      this.oseeServer = oseeServer;
   }

   public void validate() throws OseeCoreException {
      Conditions.checkNotNullOrEmpty(oseeServer, "resource server address");
   }

   @Override
   public void reset() {
      super.reset();
      oseeServer = null;
      String overrideValue = OseeClientProperties.getOseeApplicationServer();
      if (Strings.isValid(overrideValue)) {
         overrideArbitration = true;
         serverInfo = setFromString(overrideValue);
      }
   }

   public boolean isOverrideArbitration() {
      return overrideArbitration;
   }

   public OseeServerInfo setFromString(String value) {
      OseeServerInfo toReturn = null;
      String rawAddress = value;
      if (rawAddress.startsWith("http")) {
         rawAddress = value.replace("http://", "");
      }
      Pattern pattern = Pattern.compile("(.*):(\\d+)");
      Matcher matcher = pattern.matcher(rawAddress);
      if (matcher.find()) {
         String address = matcher.group(1);
         int port = Integer.valueOf(matcher.group(2));
         toReturn =
            new OseeServerInfo("OVERRIDE", address, port, new String[] {"OVERRIDE"},
               new Timestamp(new Date().getTime()), true);
      }
      return toReturn;
   }

   @Override
   public String report() {
      String errorStr = super.report();
      if (!Strings.isValid(errorStr)) {
         errorStr = "";
      }
      System.setProperty("osee.application.server.error", errorStr);
      return errorStr;
   }

}
