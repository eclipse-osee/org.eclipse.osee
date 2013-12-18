/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
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
