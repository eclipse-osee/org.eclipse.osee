/*********************************************************************
 * Copyright (c) 2013 Boeing
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

package org.eclipse.osee.authentication.admin.internal;

import org.eclipse.osee.authentication.admin.AuthenticationRequest;

/**
 * @author Roberto E. Escobar
 */
public class AuthenticationImpl implements AuthenticationRequest {

   private final String scheme;
   private final String userName;
   private final String password;

   public AuthenticationImpl(String scheme, String userName, String password) {
      super();
      this.scheme = scheme;
      this.userName = userName;
      this.password = password;
   }

   @Override
   public String getUserName() {
      return userName;
   }

   @Override
   public String getPassword() {
      return password;
   }

   @Override
   public String getScheme() {
      return scheme;
   }

   @Override
   public String toString() {
      return "AuthenticationImpl [scheme=" + scheme + ", userName=" + userName + ", password=[********]]";
   }

}
