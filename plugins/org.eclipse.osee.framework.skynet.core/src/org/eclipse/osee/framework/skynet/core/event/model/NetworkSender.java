/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.framework.skynet.core.event.model;

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

   @Override
   public String toString() {
      return "NetworkSender [source=" + sourceObject + ", sessionId=" + sessionId + ", machName=" + machineName + ", userId=" + userId + ", machIp=" + machineIp + ", clientVer=" + clientVersion + ", port=" + port + "]";
   }
}
