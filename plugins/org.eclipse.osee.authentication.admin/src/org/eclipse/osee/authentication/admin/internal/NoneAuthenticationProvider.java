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

import java.util.Collections;
import org.eclipse.osee.authentication.admin.AuthenticatedUser;
import org.eclipse.osee.authentication.admin.AuthenticationProvider;
import org.eclipse.osee.authentication.admin.AuthenticationRequest;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * @author Roberto E. Escobar
 */
public class NoneAuthenticationProvider implements AuthenticationProvider {

   protected static final String AUTHENTICATION_TYPE = "None";

   @Override
   public String getAuthenticationScheme() {
      return AUTHENTICATION_TYPE;
   }

   @Override
   public AuthenticatedUser authenticate(AuthenticationRequest request) {
      String username = request.getUserName();
      return new AssumeAnyUser(username);
   }

   private static final class AssumeAnyUser implements AuthenticatedUser {

      private final String username;

      public AssumeAnyUser(String username) {
         super();
         this.username = username;
      }

      @Override
      public String getName() {
         return username;
      }

      @Override
      public String getDisplayName() {
         return username;
      }

      @Override
      public String getUserName() {
         return username;
      }

      @Override
      public String getEmailAddress() {
         return Strings.emptyString();
      }

      @Override
      public boolean isActive() {
         return true;
      }

      @Override
      public boolean isAuthenticated() {
         return true;
      }

      @Override
      public Iterable<String> getRoles() {
         return Collections.emptyList();
      }
   }
}
