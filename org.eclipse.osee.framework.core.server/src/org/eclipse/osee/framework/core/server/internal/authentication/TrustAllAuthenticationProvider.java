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
package org.eclipse.osee.framework.core.server.internal.authentication;

import org.eclipse.osee.framework.core.data.IOseeUserInfo;
import org.eclipse.osee.framework.core.data.OseeCredential;
import org.eclipse.osee.framework.core.data.SystemUser;
import org.eclipse.osee.framework.core.exception.OseeAuthenticationException;
import org.eclipse.osee.framework.core.server.IAuthenticationProvider;
import org.eclipse.osee.framework.core.server.UserDataStore;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * @author Roberto E. Escobar
 */
public class TrustAllAuthenticationProvider implements IAuthenticationProvider {

   @Override
   public boolean authenticate(OseeCredential credential) throws OseeAuthenticationException {
      return true;
   }

   @Override
   public String getProtocol() {
      return "trustAll";
   }

   @Override
   public IOseeUserInfo asOseeUserId(OseeCredential credential) throws OseeAuthenticationException {
      IOseeUserInfo oseeUserId = SystemUser.Guest;
      String userName = credential.getUserName();
      if (Strings.isValid(userName)) {
         oseeUserId = UserDataStore.getOseeUserFromOseeDb(userName);
         if (oseeUserId == null) {
            oseeUserId = UserDataStore.createUser(true, userName, userName, "", true);
         }
      }
      return oseeUserId;
   }

}
