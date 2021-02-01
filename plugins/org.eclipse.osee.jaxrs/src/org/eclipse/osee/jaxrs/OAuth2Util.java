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

package org.eclipse.osee.jaxrs;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import javax.ws.rs.ProcessingException;
import javax.ws.rs.core.Response;
import org.apache.cxf.rs.security.oauth2.client.HttpRequestProperties;
import org.apache.cxf.rs.security.oauth2.client.OAuthClientUtils;
import org.apache.cxf.rs.security.oauth2.common.ClientAccessToken;
import org.apache.cxf.rs.security.oauth2.provider.OAuthContextProvider;
import org.apache.cxf.rs.security.oauth2.provider.OAuthJSONProvider;

/**
 * @author Roberto E. Escobar
 */
public class OAuth2Util {

   public static final String OAUTH2_OOB_CALLBACK = "urn:ietf:wg:oauth:2.0:oob";

   private static volatile OAuthJSONProvider OAUTH_JSON_PROVIDER;
   private static volatile List<Object> PROVIDERS;

   private OAuth2Util() {
      // Utility
   }

   public static List<Object> getOAuthProviders() {
      if (PROVIDERS == null) {
         List<Object> providers = new ArrayList<>();
         providers.add(OAuth2Util.getOAuthJSONProvider());
         providers.add(new OAuthContextProvider());
         PROVIDERS = providers;
      }
      return PROVIDERS;
   }

   public static OAuthJSONProvider getOAuthJSONProvider() {
      if (OAUTH_JSON_PROVIDER == null) {
         OAUTH_JSON_PROVIDER = new OAuthJSONProvider();
      }
      return OAUTH_JSON_PROVIDER;
   }

   public static RuntimeException newException(String format, Object... data) {
      return newException(null, format, data);
   }

   public static RuntimeException newException(Throwable th, String format, Object... data) {
      String message = format;
      if (data != null && data.length > 0) {
         message = String.format(message, data);
      }
      return new ProcessingException(message, th);
   }

   public static RuntimeException toException(Response response, String message) {
      RuntimeException ex;
      if (JaxRsExceptions.isErrorResponse(response)) {
         ex = JaxRsExceptions.asOseeException(response);
      } else {
         String errorValue = "N/A";
         if (response.hasEntity()) {
            try {
               response.bufferEntity();
               errorValue = response.readEntity(String.class);
            } catch (Exception ex2) {
               // Do nothing;
            }
         }
         ex = newException("%s - errorValue [%s]", message, errorValue);
      }
      return ex;
   }

   public static String asAuthorizationHeader(ClientAccessToken token) {
      return OAuthClientUtils.createAuthorizationHeader(token);
   }

   public static String asAuthorizationHeader(ClientAccessToken token, String httpMethod, URI requestedUri) {
      HttpRequestProperties requestProperties = new HttpRequestProperties(requestedUri, httpMethod);
      return OAuthClientUtils.createAuthorizationHeader(token, requestProperties);
   }

   public static JaxRsConfirmAccessHandler newAcceptAllTokenHandler() {
      return new JaxRsConfirmAccessHandler() {

         @Override
         public ConfirmAccessResponse onConfirmAccess(ConfirmAccessRequest request) {
            return acceptAll(request);
         }
      };
   }

}