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

import java.io.InputStream;
import org.eclipse.osee.framework.core.exception.OseeWrappedException;

/**
 * @author Roberto E. Escobar
 */
public class OseeCredential extends OseeClientInfo {
   private static final long serialVersionUID = 4583587251351958961L;
   private static final String USERNAME = "userName";
   private static final String PASSWORD = "password";
   private static final String DOMAIN = "domain";
   private static final String AUTHENTICATION_PROTOCOL = "authenticationProtocol";

   public OseeCredential() {
      super();
   }

   public void setUserName(String userName) {
      this.backingData.put(USERNAME, userName);
   }

   public void setPassword(String password) {
      this.backingData.put(PASSWORD, password);
   }

   public void setDomain(String domain) {
      this.backingData.put(DOMAIN, domain);
   }

   public String getUserName() {
      return getString(USERNAME);
   }

   public String getPassword() {
      return getString(PASSWORD);
   }

   public String getDomain() {
      return getString(DOMAIN);
   }

   public String getAuthenticationProtocol() {
      return getString(AUTHENTICATION_PROTOCOL);
   }

   public void setAuthenticationProtocol(String protocol) {
      this.backingData.put(AUTHENTICATION_PROTOCOL, protocol);
   }

   /**
    * Create new instance from XML input
    * 
    * @param OseeCredential the new instance
    * @throws OseeWrappedException
    */
   public static OseeCredential fromXml(InputStream inputStream) throws OseeWrappedException {
      OseeCredential session = new OseeCredential();
      session.loadfromXml(inputStream);
      return session;
   }
}
