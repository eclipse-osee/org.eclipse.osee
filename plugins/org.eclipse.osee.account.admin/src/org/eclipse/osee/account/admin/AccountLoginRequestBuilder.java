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

package org.eclipse.osee.account.admin;

/**
 * @author Roberto E. Escobar
 */
public final class AccountLoginRequestBuilder {

   private String scheme;
   private String userName;
   private String password;

   private String accessDetails;
   private String remoteAddress;

   private AccountLoginRequestBuilder() {
      //
   }

   public static AccountLoginRequestBuilder newBuilder() {
      return new AccountLoginRequestBuilder();
   }

   public AccountLoginRequestBuilder scheme(String scheme) {
      this.scheme = scheme;
      return this;
   }

   public AccountLoginRequestBuilder userName(String userName) {
      this.userName = userName;
      return this;
   }

   public AccountLoginRequestBuilder password(String password) {
      this.password = password;
      return this;
   }

   public AccountLoginRequestBuilder accessedBy(String accessDetails) {
      this.accessDetails = accessDetails;
      return this;
   }

   public AccountLoginRequestBuilder remoteAddress(String remoteAddress) {
      this.remoteAddress = remoteAddress;
      return this;
   }

   public AccountLoginRequest build() {
      AccessDetails details = new LoginAccessDetailsImpl(accessDetails, remoteAddress);
      return new LoginAccountRequestImpl(scheme, userName, password, details);
   }

   private static final class LoginAccessDetailsImpl implements AccessDetails {

      private final String accessDetails;
      private final String remoteAddress;

      public LoginAccessDetailsImpl(String accessDetails, String remoteAddress) {
         super();
         this.accessDetails = accessDetails;
         this.remoteAddress = remoteAddress;
      }

      @Override
      public String getAccessDetails() {
         return accessDetails;
      }

      @Override
      public String getRemoteAddress() {
         return remoteAddress;
      }

   }

   private static final class LoginAccountRequestImpl implements AccountLoginRequest {

      private final String userName;
      private final String password;
      private final String scheme;
      private final AccessDetails accessDetails;

      public LoginAccountRequestImpl(String scheme, String userName, String password, AccessDetails accessDetails) {
         super();
         this.scheme = scheme;
         this.userName = userName;
         this.password = password;
         this.accessDetails = accessDetails;
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
      public AccessDetails getDetails() {
         return accessDetails;
      }

      @Override
      public String toString() {
         return "LoginAccountRequestImpl [scheme=" + scheme + ", userName=" + userName + ", password=[******]]";
      }
   }
}
