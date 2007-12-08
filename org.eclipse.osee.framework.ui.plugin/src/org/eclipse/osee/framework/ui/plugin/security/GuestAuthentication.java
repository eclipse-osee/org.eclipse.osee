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
package org.eclipse.osee.framework.ui.plugin.security;

import org.eclipse.osee.framework.ui.plugin.security.OseeAuthentication.AuthenticationStatus;
import org.eclipse.osee.framework.ui.plugin.security.UserCredentials.UserCredentialEnum;

/**
 * @author Roberto E. Escobar
 */
public class GuestAuthentication extends AbstractAuthentication implements IAuthenticationStrategy {

   private static GuestAuthentication instance = null;

   public static final String DEFAULT_USER_NAME = "Guest";
   public static final String DEFAULT_USER_ID = "99999998";

   private GuestAuthentication() {
      super();
   }

   public static GuestAuthentication getInstance() {
      if (instance == null) {
         instance = new GuestAuthentication();
      }
      return instance;
   }

   /*
    * (non-Javadoc)
    * 
    * @see osee.plugin.core.security.IAuthentication#authenticate(java.lang.String, java.lang.String,
    *      java.lang.String)
    */
   public boolean authenticate(String userName, String password, String domain) {
      clear();
      userCredentials.setFieldAndValidity(UserCredentialEnum.Id, true, DEFAULT_USER_ID);
      userCredentials.setFieldAndValidity(UserCredentialEnum.Name, true, DEFAULT_USER_NAME);
      authenticationStatus = AuthenticationStatus.Success;
      return true;
   }

   /*
    * (non-Javadoc)
    * 
    * @see osee.plugin.core.security.IAuthentication#isNetworked()
    */
   public boolean isNetworked() {
      return false;
   }

   /*
    * (non-Javadoc)
    * 
    * @see osee.plugin.core.security.IAuthentication#isLoginAllowed()
    */
   public boolean isLoginAllowed() {
      return true;
   }

}
