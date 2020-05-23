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

package org.eclipse.osee.authentication.admin;

/**
 * @author Roberto E. Escobar
 */
public final class AuthenticationRequestBuilder {

   private String scheme;
   private String userName;
   private String password;

   private AuthenticationRequestBuilder() {
      //
   }

   public static AuthenticationRequestBuilder newBuilder() {
      return new AuthenticationRequestBuilder();
   }

   public AuthenticationRequestBuilder scheme(String scheme) {
      this.scheme = scheme;
      return this;
   }

   public AuthenticationRequestBuilder userName(String userName) {
      this.userName = userName;
      return this;
   }

   public AuthenticationRequestBuilder password(String password) {
      this.password = password;
      return this;
   }

   public AuthenticationRequest build() {
      return new AuthenticationRequestImpl(scheme, userName, password);
   }

   private static final class AuthenticationRequestImpl implements AuthenticationRequest {

      private final String userName;
      private final String password;
      private final String scheme;

      public AuthenticationRequestImpl(String scheme, String userName, String password) {
         super();
         this.scheme = scheme;
         this.userName = userName;
         this.password = password;
      }

      @Override
      public String getScheme() {
         return scheme;
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
      public String toString() {
         return "AuthenticationRequestImpl [scheme=" + scheme + ", userName=" + userName + ", password=[*****]]";
      }

   }
}
