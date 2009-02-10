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
