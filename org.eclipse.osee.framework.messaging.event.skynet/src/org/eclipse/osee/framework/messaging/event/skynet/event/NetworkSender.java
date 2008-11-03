/*
 * Created on Sep 13, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.messaging.event.skynet.event;

import java.io.Serializable;

/**
 * @author Donald G. Dunne
 */
public class NetworkSender implements Serializable {

   private static final long serialVersionUID = 1908598443523663604L;
   public Object sourceObject;
   public String sessionId;
   public String machineName;
   public String userId;
   public String machineIp;
   public String clientVersion;
   public int port;

   public NetworkSender(Object sourceObject, String sessionId, String machineName, String userId, String machineIp, int port, String clientVersion) {
      this.sessionId = sessionId;
      this.sourceObject = sourceObject;
      this.machineName = machineName;
      this.userId = userId;
      this.machineIp = machineIp;
      this.port = port;
      this.clientVersion = clientVersion;
   }
}
