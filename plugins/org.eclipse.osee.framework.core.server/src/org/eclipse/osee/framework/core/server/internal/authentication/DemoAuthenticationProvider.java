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

import java.util.Arrays;
import java.util.List;
import org.eclipse.osee.framework.core.data.IUserToken;
import org.eclipse.osee.framework.core.data.OseeCredential;
import org.eclipse.osee.framework.core.data.TokenFactory;
import org.eclipse.osee.framework.core.server.AbstractAuthenticationProvider;

/**
 * @author Roberto E. Escobar
 */
public class DemoAuthenticationProvider extends AbstractAuthenticationProvider {

   private final boolean autoAuthenticate = true;
   public static final IUserToken Joe_Smith =
      TokenFactory.createUserToken(61106791L, "ABNRvbZxXHICYklfslwA", "Joe Smith", "", "3333", true, false, false);
   public static final IUserToken Kay_Jones =
      TokenFactory.createUserToken(5896672L, "ABNRvuB8x3VARkkn3YAA", "Kay Jones", "", "4444", true, false, false);
   public static final IUserToken Jason_Michael =
      TokenFactory.createUserToken(277990, "ABNRvuHWtXAdxbG3mUAA", "Jason Michael", "", "5555", true, false, false);
   public static final IUserToken Alex_Kay =
      TokenFactory.createUserToken(8006939L, "ABNRvuKDIWOcPDe4X0wA", "Alex Kay", "", "6666", true, false, false);
   public static final IUserToken Inactive_Steve =
      TokenFactory.createUserToken(5808093, "ABNRvuRG6jKwKnEoX4gA", "Inactive Steve", "", "7777", false, false, false);
   public static List<IUserToken> values = Arrays.asList(Joe_Smith, Kay_Jones, Jason_Michael, Alex_Kay, Inactive_Steve);

   @Override
   public IUserToken asOseeUserId(OseeCredential credential) {
      for (IUserToken token : values) {
         if (credential.getUserName().equals(token.getName().toLowerCase())) {
            IUserToken userToken = getUserTokenFromOseeDb(token.getName());
            if (userToken != null) {
               return userToken;
            } else {
               return createUserToken(true, token.getName(), token.getUserId(), "", true);
            }
         }
      }
      return createUserToken(true, Joe_Smith.getName(), Joe_Smith.getUserId(), "", true);
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
