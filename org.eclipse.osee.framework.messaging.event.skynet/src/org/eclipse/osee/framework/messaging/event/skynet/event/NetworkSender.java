/*
 * Created on Sep 13, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.messaging.event.skynet.event;

/**
 * @author Donald G. Dunne
 */
public class NetworkSender {

   public Object sourceObject;
   public String sessionId;
   public String machineName;
   public String userId;
   public String machineIp;

   public NetworkSender(Object sourceObject, String sessionId, String machineName, String userId, String machineIp) {
      this.sessionId = sessionId;
      this.sourceObject = sourceObject;
      this.machineName = machineName;
      this.userId = userId;
      this.machineIp = machineIp;
   }

}
