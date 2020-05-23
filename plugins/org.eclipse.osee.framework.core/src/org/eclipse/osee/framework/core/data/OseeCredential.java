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

package org.eclipse.osee.framework.core.data;

/**
 * @author Roberto E. Escobar
 * @author Donald G. Dunne
 */
public class OseeCredential extends IdeClientSession {
   private String userName = "userName";
   private String password = "password";

   public OseeCredential() {
      super();
   }

   public void setUserName(String userName) {
      this.userName = userName;
   }

   public void setPassword(String password) {
      this.password = password;
   }

   public String getUserName() {
      return userName;
   }

   public String getPassword() {
      return password;
   }

   @Override
   public String toString() {
      return "OseeCredential [userName=" + userName + ", password=" + password + ", getUserName()=" + getUserName() + ", getPassword()=" + //
         getPassword() + ", getClientAddress()=" + getClientAddress() + ", getClientPort()=" + getClientPort() + ", getUserId()=" + getUserId() + //
         ", getClientVersion()=" + getClientVersion() + ", getSessionId()=" + getSessionId() + ", getCreatedOn()=" + getCreatedOn() + //
         ", getSessionLog()=" + getSessionLog() + ", getAuthenticationProtocol()=" + getAuthenticationProtocol() + ", getId()=" + getId() + //
         ", getClientName()=" + getClientName() + ", useOracleHints()=" + getUseOracleHints() + "]";
   }

}
