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

/**
 * @author Roberto E. Escobar
 */
public abstract class AbstractAuthentication implements IAuthentication {

   protected AuthenticationStatus authenticationStatus;
   protected UserCredentials userCredentials;

   protected AbstractAuthentication() {
      super();
      userCredentials = new UserCredentials();
      clear();
   }

   public AuthenticationStatus getAuthenticationStatus() {
      return authenticationStatus;
   }

   public UserCredentials getCredentials() {
      return userCredentials;
   }

   public void clear() {
      authenticationStatus = AuthenticationStatus.NoResponse;
      userCredentials.clear();
   }

   public boolean isAuthenticated() {
      return authenticationStatus.equals(AuthenticationStatus.Success);
   }

}
