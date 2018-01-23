/*******************************************************************************
 * Copyright (c) 2018 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.account.admin;

import java.util.Map;

/**
 * @author Megumi Telles
 */
public class AccountConfiguration {

   public static final String NAMESPACE = "account";
   public static final String HTTP_HEADER_NAME = qualify("http.header.name");
   private String httpHeaderName;

   private AccountConfiguration() {
      //Builder Class
   }

   private static String qualify(String value) {
      return String.format("%s.%s", NAMESPACE, value);
   }

   public String getHttpHeaderName() {
      return httpHeaderName;
   }

   public void setHttpHeaderName(String httpHeaderName) {
      this.httpHeaderName = httpHeaderName;
   }

   public AccountConfiguration copy() {
      AccountConfiguration data = new AccountConfiguration();
      data.httpHeaderName = this.httpHeaderName;
      return data;
   }

   public static AccountConfigurationBuilder newBuilder() {
      return new AccountConfigurationBuilder();
   }

   public static AccountConfigurationBuilder fromProperties(Map<String, Object> props) {
      return newBuilder().properties(props);
   }

   public static AccountConfiguration newConfig(Map<String, Object> props) {
      return fromProperties(props).build();
   }

   public static final class AccountConfigurationBuilder {
      private final AccountConfiguration config = new AccountConfiguration();

      public AccountConfiguration build() {
         return config.copy();
      }

      public AccountConfigurationBuilder properties(Map<String, Object> props) {
         httpHeaderName(get(props, HTTP_HEADER_NAME, ""));
         return this;
      }

      public AccountConfigurationBuilder httpHeaderName(String httpHeaderName) {
         config.setHttpHeaderName(httpHeaderName);
         return this;
      }

      private static String get(Map<String, Object> props, String key, String defaultValue) {
         String toReturn = defaultValue;
         Object object = props != null ? props.get(key) : null;
         if (object != null) {
            toReturn = String.valueOf(object);
         }
         return toReturn;
      }

   }
}
