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

import org.eclipse.osee.framework.core.data.UserToken;
import org.eclipse.osee.framework.core.data.OseeCredential;
import org.eclipse.osee.framework.core.enums.SystemUser;
import org.eclipse.osee.framework.core.server.AbstractAuthenticationProvider;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * @author Roberto E. Escobar
 */
public class TrustAllAuthenticationProvider extends AbstractAuthenticationProvider {

   @Override
   public boolean authenticate(OseeCredential credential) {
      return true;
   }

   @Override
   public String getProtocol() {
      return "trustAll";
   }

   @Override
   public UserToken asOseeUserId(OseeCredential credential) {
      UserToken userToken = SystemUser.Anonymous;
      String userName = credential.getUserName();
      if (Strings.isValid(userName)) {
         userToken = getUserTokenFromOseeDb(userName);
         if (userToken == null) {
            userToken = createUserToken(true, userName, userName, "", true);
         }
      }
      return userToken;
   }

}
