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
package org.eclipse.osee.framework.core.data;

/**
 * @author Donald G. Dunne
 */
public class OseeSession {

   private String sessionId;
   private String machineName;
   private String userId;
   private String machineIp;

   public OseeSession(String id, String machineName, String userId, String machineIp) {
      this.sessionId = id;
      this.machineName = machineName;
      this.machineIp = machineIp;
      this.userId = userId;
   }

   @Override
   public String toString() {
      return String.format("SessionId:[%s]\tUserId:[%s]\tMachine:[%s]\tIP:[%s]", sessionId, userId, machineName,
            machineIp);
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

   public String getSessionId() {
      return sessionId;
   }

}
