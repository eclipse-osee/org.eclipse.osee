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

package org.eclipse.osee.jaxrs.server.security;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.Provider;
import org.apache.cxf.jaxrs.ext.MessageContext;
import org.apache.cxf.rs.security.oauth2.common.AccessTokenValidation;
import org.apache.cxf.rs.security.oauth2.filters.OAuthRequestFilter;
import org.apache.cxf.rs.security.oauth2.provider.AccessTokenValidator;
import org.apache.cxf.rs.security.oauth2.provider.OAuthServiceException;
import org.apache.cxf.rs.security.oauth2.utils.OAuthConstants;
import org.apache.cxf.security.SecurityContext;
import org.eclipse.osee.framework.core.JaxRsApi;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.jaxrs.server.internal.security.oauth2.OAuthUtil;

/**
 * Filter used to protect resource server end-points. This filter is used when the resource server is not located in the
 * same JVM as the authorization server. When a request is processed, the resource server will contact the authorization
 * server and validate the access token provided by the request through the HTTP authorization header.
 *
 * @author Roberto E. Escobar
 */
@Provider
public class JaxRsOAuthResourceServerFilter implements ContainerRequestFilter {

   private final OAuthRequestFilter delegate;

   private JaxRsOAuthResourceServerFilter(OAuthRequestFilter delegate) {
      this.delegate = delegate;
   }

   public void setAudienceIsEndpointAddress(boolean audienceIsEndpointAddress) {
      delegate.setAudienceIsEndpointAddress(audienceIsEndpointAddress);
   }

   public void setCheckFormData(boolean checkFormData) {
      delegate.setCheckFormData(checkFormData);
   }

   public void setRealm(String realm) {
      delegate.setRealm(realm);
   }

   public void setUseUserSubject(boolean useUserSubject) {
      delegate.setUseUserSubject(useUserSubject);
   }

   public void setAudiences(List<String> audiences) {
      delegate.setAudience(audiences.toString());
   }

   @Override
   public void filter(ContainerRequestContext context) {
      delegate.filter(context);
   };

   public static Builder newBuilder(JaxRsApi jaxRsApi) {
      return new Builder(jaxRsApi);
   }

   public static class Builder {
      public static final long MAX_TOKEN_CACHE_EVICT_TIMEOUT_MILLIS = 24L * 60L * 60L * 1000L; // one day

      private final JaxRsApi jaxRsApi;

      private String resourceServerKey;
      private String resourceServerSecret;
      private String validationServerUri;

      public Builder(JaxRsApi jaxRsApi) {
         this.jaxRsApi = jaxRsApi;
      }

      public JaxRsOAuthResourceServerFilter build() {
         ClientAccessTokenValidator validator = newTokenValidator();
         return build(validator);
      }

      public JaxRsOAuthResourceServerFilter build(int cacheMaxSize, long cacheEvictTimeoutMillis) {
         ClientAccessTokenValidator validator = newCachingTokenValidator(cacheMaxSize, cacheEvictTimeoutMillis);
         return build(validator);
      }

      private JaxRsOAuthResourceServerFilter build(ClientAccessTokenValidator validator) {
         validator.setTarget(jaxRsApi.newTargetPasswd(validationServerUri, resourceServerKey, resourceServerSecret));

         OAuth2RequestFilter filter = new OAuth2RequestFilter();
         filter.setTokenValidator(validator);
         return new JaxRsOAuthResourceServerFilter(filter);
      }

      public Builder serverUri(String validationServerUri) {
         this.validationServerUri = validationServerUri;
         return this;
      }

      public Builder serverKey(String resourceServerKey) {
         this.resourceServerKey = resourceServerKey;
         return this;
      }

      public Builder serverSecret(String resourceServerSecret) {
         this.resourceServerSecret = resourceServerSecret;
         return this;
      }

      private static ClientAccessTokenValidator newTokenValidator() {
         return new ClientAccessTokenValidator() {
            @Override
            public AccessTokenValidation validateAccessToken(MessageContext mc, final String authScheme,
               final String accessToken) throws OAuthServiceException {
               return getRemoteTokenValidation(authScheme, accessToken);
            }

            @Override
            public AccessTokenValidation validateAccessToken(MessageContext mc, String authScheme, String accessToken,
               MultivaluedMap<String, String> values) throws OAuthServiceException {
               return null;
            }
         };
      }

      private static ClientAccessTokenValidator newCachingTokenValidator(int cacheMaxSize,
         long cacheEvictTimeoutMillis) {
         Conditions.checkExpressionFailOnTrue(cacheMaxSize <= 0, "Token Cache max size must be greater than 0");
         Conditions.checkExpressionFailOnTrue(cacheEvictTimeoutMillis > MAX_TOKEN_CACHE_EVICT_TIMEOUT_MILLIS,
            "Token cache evict timeout exceeds max - [%s]", Lib.asTimeString(MAX_TOKEN_CACHE_EVICT_TIMEOUT_MILLIS));
         Conditions.checkExpressionFailOnTrue(cacheEvictTimeoutMillis <= 0,
            "Token cache evict timeout must be greater than 0");

         final Cache<String, AccessTokenValidation> cache = CacheBuilder.newBuilder()//
            .maximumSize(cacheMaxSize)//
            .expireAfterWrite(cacheEvictTimeoutMillis, TimeUnit.MILLISECONDS)//
            .build();

         return new ClientAccessTokenValidator() {
            @Override
            public AccessTokenValidation validateAccessToken(MessageContext mc, final String authScheme,
               final String accessToken) throws OAuthServiceException {
               try {
                  return cache.get(accessToken, new Callable<AccessTokenValidation>() {
                     @Override
                     public AccessTokenValidation call() {
                        return getRemoteTokenValidation(authScheme, accessToken);
                     }
                  });
               } catch (Exception ex) {
                  throw new OAuthServiceException("Error validating access token", ex.getCause());
               }
            }

            @Override
            public AccessTokenValidation validateAccessToken(MessageContext mc, String authScheme, String accessToken,
               MultivaluedMap<String, String> values) throws OAuthServiceException {
               return null;
            }
         };
      }

      private static class OAuth2RequestFilter extends OAuthRequestFilter {

         private volatile boolean useUserSubject;

         @Override
         public void setUseUserSubject(boolean useUserSubject) {
            super.setUseUserSubject(useUserSubject);
            this.useUserSubject = useUserSubject;
         }

         @Override
         protected SecurityContext createSecurityContext(HttpServletRequest request,
            AccessTokenValidation accessTokenV) {
            return OAuthUtil.getSecurityContext(accessTokenV, useUserSubject);
         }

      }

      private static abstract class ClientAccessTokenValidator implements AccessTokenValidator {

         private WebTarget target;

         public AccessTokenValidation validateAccessToken(MessageContext mc, String authScheme, String accessToken)
            throws OAuthServiceException {
            return null;
         }

         @Override
         public List<String> getSupportedAuthorizationSchemes() {
            return Collections.singletonList(OAuthConstants.ALL_AUTH_SCHEMES);
         }

         public void setTarget(WebTarget target) {
            this.target = target;
         }

         protected AccessTokenValidation getRemoteTokenValidation(String authScheme, String accessToken) {
            Form form = new Form();
            form.param(OAuthConstants.AUTHORIZATION_SCHEME_TYPE, authScheme);
            form.param(OAuthConstants.AUTHORIZATION_SCHEME_DATA, accessToken);
            return target.request().post(Entity.form(form), AccessTokenValidation.class);
         }
      }
   }
}