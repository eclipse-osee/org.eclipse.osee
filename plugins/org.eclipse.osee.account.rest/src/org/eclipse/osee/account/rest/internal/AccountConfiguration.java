/*********************************************************************
 * Copyright (c) 2018 Boeing
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

package org.eclipse.osee.account.rest.internal;

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
}