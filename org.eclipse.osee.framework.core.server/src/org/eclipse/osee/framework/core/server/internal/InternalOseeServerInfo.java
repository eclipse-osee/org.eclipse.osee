/*
 * Created on Oct 28, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.core.server.internal;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.Timestamp;
import org.eclipse.osee.framework.core.data.OseeServerInfo;
import org.eclipse.osee.framework.jdk.core.util.time.GlobalTime;

/**
 * @author Roberto E. Escobar
 */
class InternalOseeServerInfo extends OseeServerInfo {

   private static final String OSGI_PORT_PROPERTY = "org.osgi.service.http.port";

   /**
    * @param serverAddress
    * @param port
    * @param version
    * @param dateStarted
    * @param isAcceptingRequests
    */
   protected InternalOseeServerInfo(String serverAddress, int port, String version, Timestamp dateStarted, boolean isAcceptingRequests) {
      super(serverAddress, port, version, dateStarted, isAcceptingRequests);
   }

   void setAcceptingRequests(boolean value) {
      properties.put(IS_ACCEPTING_REQUESTS, value);
   }

   static OseeServerInfo createFromLocalInfo() {
      String serverAddress = "127.0.0.1";
      try {
         serverAddress = InetAddress.getLocalHost().getCanonicalHostName();
      } catch (UnknownHostException ex) {
      }
      int port = Integer.valueOf(System.getProperty(OSGI_PORT_PROPERTY, "-1"));

      String version = "hello";

      return new InternalOseeServerInfo(serverAddress, port, version, GlobalTime.GreenwichMeanTimestamp(), false);
   }

   static OseeServerInfo createFromData(String serverAddress, int port, String version, Timestamp dateStarted, boolean isAcceptingRequests) {
      return new InternalOseeServerInfo(serverAddress, port, version, dateStarted, isAcceptingRequests);
   }
}
