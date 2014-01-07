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
package org.eclipse.osee.authorization.admin;

import java.util.Date;

/**
 * @author Roberto E. Escobar
 */
public final class AuthorizationRequestBuilder {

   private boolean isSecure;
   private Date date;
   private String path;
   private String method;
   private String authType;

   private AuthorizationRequestBuilder() {
      //
   }

   public static AuthorizationRequestBuilder newBuilder() {
      return new AuthorizationRequestBuilder();
   }

   public AuthorizationRequest build() {
      return new AuthorizationRequestImpl(isSecure, date, path, method, authType);
   }

   public AuthorizationRequestBuilder secure(boolean isSecure) {
      this.isSecure = isSecure;
      return this;
   }

   public AuthorizationRequestBuilder date(Date date) {
      this.date = date;
      return this;
   }

   public AuthorizationRequestBuilder method(String method) {
      this.method = method;
      return this;
   }

   public AuthorizationRequestBuilder path(String path) {
      this.path = path;
      return this;
   }

   public AuthorizationRequestBuilder authorizationType(String authType) {
      this.authType = authType;
      return this;
   }

   private static final class AuthorizationRequestImpl implements AuthorizationRequest {

      private final boolean isSecure;
      private final Date requestDate;
      private final String path;
      private final String method;
      private final String authType;

      public AuthorizationRequestImpl(boolean isSecure, Date requestDate, String path, String method, String authType) {
         super();
         this.isSecure = isSecure;
         this.requestDate = requestDate;
         this.path = path;
         this.method = method;
         this.authType = authType;
      }

      @Override
      public boolean isSecure() {
         return isSecure;
      }

      @Override
      public Date getRequestDate() {
         return requestDate;
      }

      @Override
      public String getPath() {
         return path;
      }

      @Override
      public String getMethod() {
         return method;
      }

      @Override
      public String getAuthorizationType() {
         return authType;
      }

      @Override
      public String toString() {
         return "AuthorizationRequestImpl [isSecure=" + isSecure + ", requestDate=" + requestDate + ", path=" + path + ", method=" + method + ", authType=" + authType + "]";
      }

   }
}
