/*********************************************************************
 * Copyright (c) 2014 Boeing
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

package org.eclipse.osee.jaxrs.client;

import javax.ws.rs.client.WebTarget;

/**
 * @author Roberto E. Escobar
 */
public final class JaxRsClient {

   public interface JaxRsClientFactory {

      <T> T newProxy(JaxRsClientConfig config, String url, Class<T> clazz);

      WebTarget newWebTarget(JaxRsClientConfig config, String url);

      WebTarget newWebTarget(String url);

      <T> T newProxy(String url, Class<T> clazz);

      JaxRsClientConfig copyDefaultConfig();
   }

   private static Long accountId;
   private static Long clientId;

   public static Long getAccountId() {
      return accountId;
   }

   public static void setAccountId(Long accountId) {
      JaxRsClient.accountId = accountId;
   }

   public static Long getClientId() {
      return clientId;
   }

   public static void setClientId(Long clientId) {
      JaxRsClient.clientId = clientId;
   }
}