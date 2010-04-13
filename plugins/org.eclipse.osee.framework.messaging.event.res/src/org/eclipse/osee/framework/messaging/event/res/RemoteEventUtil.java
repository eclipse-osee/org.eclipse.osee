/*
 * Created on Apr 7, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.messaging.event.res;

import org.eclipse.osee.framework.messaging.event.res.msgs.RemoteNetworkSender1;

/**
 * @author Donald G. Dunne
 */
public class RemoteEventUtil {

   public static RemoteNetworkSender1 getNetworkSender(Object sourceObject, String sessionId, String machineName, String userId, String machineIp, int port, String clientVersion) {
      RemoteNetworkSender1 networkSender = new RemoteNetworkSender1();
      networkSender.setSourceObject(String.valueOf(sourceObject));
      networkSender.setSourceObject(sessionId);
      networkSender.setMachineName(machineName);
      networkSender.setUserId(userId);
      networkSender.setMachineIp(machineIp);
      networkSender.setPort(port);
      networkSender.setClientVersion(clientVersion);
      return networkSender;
   }
}
