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

import org.eclipse.osee.framework.core.data.OseeCredential;
import org.eclipse.osee.framework.core.data.UserToken;
import org.eclipse.osee.framework.core.enums.DemoUsers;
import org.eclipse.osee.framework.core.server.AbstractAuthenticationProvider;

/**
 * @author Roberto E. Escobar
 */
public class DemoAuthenticationProvider extends AbstractAuthenticationProvider {
   private final boolean autoAuthenticate = true;

   @Override
   public UserToken asOseeUserId(OseeCredential credential) {
      for (UserToken token : DemoUsers.values()) {
         if (credential.getUserName().equals(token.getName().toLowerCase())) {
            UserToken userToken = getUserTokenFromOseeDb(token.getName());
            if (userToken != null) {
               return userToken;
            } else {
               return createUserToken(true, token.getName(), token.getUserId(), "", true);
            }
         }
      }
      return createUserToken(true, DemoUsers.Joe_Smith.getName(), DemoUsers.Joe_Smith.getUserId(), "joe@boeing.com",
         true);
   }

   @Override
   public boolean authenticate(OseeCredential credential) {
      return autoAuthenticate;
   }

   @Override
   public String getProtocol() {
      return "demo";
   }

}
