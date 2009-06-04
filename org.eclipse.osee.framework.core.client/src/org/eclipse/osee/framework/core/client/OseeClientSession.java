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
package org.eclipse.osee.framework.core.client;

import java.io.Serializable;

/**
 * @author Donald G. Dunne
 */
public class OseeClientSession implements Serializable {

   private static final long serialVersionUID = 6322394150666846304L;

   private String id;
   private String machineName;
   private String userId;
   private String machineIp;
   private String clientVersion;
   private String authenticationProtocol;
   private int port;

   public OseeClientSession(String id, String machineName, String userId, String machineIp, int port, String clientVersion, String authenticationProtocol) {
      this.id = id;
      this.machineName = machineName;
      this.machineIp = machineIp;
      this.userId = userId;
      this.clientVersion = clientVersion;
      this.port = port;
      this.authenticationProtocol = authenticationProtocol;
   }

   @Override
   public String toString() {
      return String.format(
            "Session:[%s] User Id:[%s] Version:[%s] Machine Name:[%s] Ip:[%s] Port:[%s] AuthenticationProtocol:[%s]",
            id, userId, clientVersion, machineName, machineIp, port, authenticationProtocol);
   }

   public String getAuthenticationProtocol() {
      return authenticationProtocol;
   }

   public int getPort() {
      return port;
   }

   /**
    * @return the client version
    */
   public String getVersion() {
      return clientVersion;
   }

   /**
    * @return the id
    */
   public String getId() {
      return id;
   }

   /**
    * @return the machineName
    */
   public String getMachineName() {
      return machineName;
   }

   /**
    * @return the userId
    */
   public String getUserId() {
      return userId;
   }

   /**
    * @return the machineIp
    */
   public String getMachineIp() {
      return machineIp;
   }

}
