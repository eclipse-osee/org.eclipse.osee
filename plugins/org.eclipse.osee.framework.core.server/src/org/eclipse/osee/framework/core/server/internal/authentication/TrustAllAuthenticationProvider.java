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

package org.eclipse.osee.framework.core.server.internal.authentication;

import org.eclipse.osee.framework.core.data.OseeCredential;
import org.eclipse.osee.framework.core.data.UserToken;
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
            userToken = createUserToken(userName, userName, "", true);
         }
      }
      return userToken;
   }

}
