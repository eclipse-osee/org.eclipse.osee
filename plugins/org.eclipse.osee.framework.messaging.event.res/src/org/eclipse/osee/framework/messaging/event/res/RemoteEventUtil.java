/*******************************************************************************
 * Copyright (c) 2010 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.messaging.event.res;

import org.eclipse.osee.framework.messaging.event.res.msgs.RemoteNetworkSender1;

/**
 * @author Donald G. Dunne
 */
public final class RemoteEventUtil {

   private RemoteEventUtil() {
      // Utility class
   }

   public static RemoteNetworkSender1 getNetworkSender(String sourceObject, String sessionId, String machineName, String userId, String machineIp, int port, String clientVersion) {
      RemoteNetworkSender1 networkSender = new RemoteNetworkSender1();
      networkSender.setSourceObject(sourceObject);
      networkSender.setSessionId(sessionId);
      networkSender.setMachineName(machineName);
      networkSender.setUserId(userId);
      networkSender.setMachineIp(machineIp);
      networkSender.setPort(port);
      networkSender.setClientVersion(clientVersion);
      return networkSender;
   }
}
