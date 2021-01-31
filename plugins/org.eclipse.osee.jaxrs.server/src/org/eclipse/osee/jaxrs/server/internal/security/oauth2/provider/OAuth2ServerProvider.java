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

package org.eclipse.osee.jaxrs.server.internal.security.oauth2.provider;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.ws.rs.core.Application;
import org.apache.cxf.rs.security.oauth2.grants.AbstractGrantHandler;
import org.apache.cxf.rs.security.oauth2.grants.clientcred.ClientCredentialsGrantHandler;
import org.apache.cxf.rs.security.oauth2.grants.code.AuthorizationCodeGrantHandler;
import org.apache.cxf.rs.security.oauth2.grants.code.CodeVerifierTransformer;
import org.apache.cxf.rs.security.oauth2.grants.code.DigestCodeVerifier;
import org.apache.cxf.rs.security.oauth2.grants.owner.ResourceOwnerGrantHandler;
import org.apache.cxf.rs.security.oauth2.grants.owner.ResourceOwnerLoginHandler;
import org.apache.cxf.rs.security.oauth2.grants.refresh.RefreshTokenGrantHandler;
import org.apache.cxf.rs.security.oauth2.provider.AccessTokenGrantHandler;
import org.apache.cxf.rs.security.oauth2.provider.AccessTokenValidator;
import org.apache.cxf.rs.security.oauth2.provider.OAuthDataProvider;
import org.apache.cxf.rs.security.oauth2.services.AbstractAccessTokenValidator;
import org.apache.cxf.rs.security.oauth2.services.AbstractOAuthService;
import org.apache.cxf.rs.security.oauth2.services.AbstractTokenService;
import org.apache.cxf.rs.security.oauth2.services.AccessTokenService;
import org.apache.cxf.rs.security.oauth2.services.AccessTokenValidatorService;
import org.apache.cxf.rs.security.oauth2.services.AuthorizationCodeGrantService;
import org.apache.cxf.rs.security.oauth2.services.ImplicitGrantService;
import org.apache.cxf.rs.security.oauth2.services.RedirectionBasedGrantService;
import org.apache.cxf.rs.security.oauth2.services.TokenRevocationService;
import org.apache.cxf.rs.security.oauth2.tokens.hawk.HawkAccessTokenValidator;
import org.apache.cxf.rs.security.oauth2.tokens.hawk.NonceStore;
import org.apache.cxf.rs.security.oauth2.tokens.hawk.NonceVerifier;
import org.apache.cxf.rs.security.oauth2.tokens.hawk.NonceVerifierImpl;
import org.eclipse.osee.jaxrs.server.internal.JaxRsConstants;
import org.eclipse.osee.jaxrs.server.internal.JaxRsResourceManager;
import org.eclipse.osee.jaxrs.server.internal.applications.JaxRsApplicationRegistry;
import org.eclipse.osee.jaxrs.server.internal.security.oauth2.provider.adapters.ClientProviderImpl;
import org.eclipse.osee.jaxrs.server.internal.security.oauth2.provider.adapters.OAuthEncryption;
import org.eclipse.osee.jaxrs.server.internal.security.oauth2.provider.adapters.SubjectProviderImpl;
import org.eclipse.osee.jaxrs.server.internal.security.oauth2.provider.endpoints.AbstractClientService;
import org.eclipse.osee.jaxrs.server.internal.security.oauth2.provider.endpoints.AuthorizationCodeEndpoint;
import org.eclipse.osee.jaxrs.server.internal.security.oauth2.provider.endpoints.ClientEndpoint;
import org.eclipse.osee.jaxrs.server.internal.security.oauth2.provider.endpoints.ClientRegistrationEndpoint;
import org.eclipse.osee.jaxrs.server.internal.security.oauth2.provider.endpoints.ImplicitGrantEndpoint;
import org.eclipse.osee.jaxrs.server.internal.security.oauth2.provider.writers.AuthorizationDataHtmlWriter;
import org.eclipse.osee.jaxrs.server.internal.security.oauth2.provider.writers.ClientRegistrationDataHtmlWriter;
import org.eclipse.osee.jaxrs.server.internal.security.oauth2.provider.writers.ClientRegistrationResponseHtmlWriter;
import org.eclipse.osee.jaxrs.server.internal.security.oauth2.provider.writers.OOBAuthorizationResponseHtmlWriter;
import org.eclipse.osee.jaxrs.server.security.JaxRsAuthenticator;
import org.eclipse.osee.jaxrs.server.security.JaxRsOAuth;
import org.eclipse.osee.jaxrs.server.security.JaxRsOAuthStorage;
import org.eclipse.osee.jaxrs.server.security.JaxRsSessionProvider;
import org.eclipse.osee.logger.Log;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

/**
 * @author Roberto E. Escobar
 */
public class OAuth2ServerProvider {

   private static final String OAUTH2_APPLICATION_COMPONENT_NAME = qualify("application");

   private final Set<String> registeredProviders = new HashSet<>();
   private List<String> audiences;
   private OAuth2DataProvider dataProvider;
   private NonceVerifier nonceVerifier;

   private OAuth2RequestFilter filter;
   private List<AccessTokenGrantHandler> grantHandlers;
   private List<AccessTokenValidator> tokenValidators;
   private Set<Object> endpoints;
   private Application application;

   private Log logger;
   private JaxRsApplicationRegistry registry;
   private JaxRsResourceManager resourceManager;
   private JaxRsAuthenticator authenticator;
   private JaxRsSessionProvider sessionProvider;
   private JaxRsOAuthStorage storage;
   private SubjectProvider subjectProvider;

   private final AtomicBoolean wasRegistered = new AtomicBoolean();

   public void setLogger(Log logger) {
      this.logger = logger;
   }

   public void setJaxRsApplicationRegistry(JaxRsApplicationRegistry registry) {
      this.registry = registry;
   }

   public void setJaxRsResourceManager(JaxRsResourceManager resourceManager) {
      this.resourceManager = resourceManager;
   }

   public void setJaxRsAuthenticator(JaxRsAuthenticator authenticator) {
      this.authenticator = authenticator;
   }

   public void setJaxRsSessionProvider(JaxRsSessionProvider sessionProvider) {
      this.sessionProvider = sessionProvider;
   }

   public void setJaxRsOAuthStorage(JaxRsOAuthStorage storage) {
      this.storage = storage;
   }

   private static String qualify(String name) {
      return String.format("%s.security.oauth2.%s", JaxRsConstants.NAMESPACE, name);
   }

   private Bundle bundle;

   public void start(BundleContext bundleContext, Map<String, Object> props) {
      bundle = bundleContext.getBundle();
      update(props);
   }

   public void stop() {
      if (wasRegistered.getAndSet(false)) {
         deregister(registry);

         audiences = null;
         dataProvider = null;
         nonceVerifier = null;
         filter = null;
         grantHandlers = null;
         tokenValidators = null;
         endpoints = null;
         application = null;
      }
   }

   private void initialize(OAuth2Configuration config) {
      OAuthEncryption serializer = new OAuthEncryption();
      subjectProvider = new SubjectProviderImpl(logger, sessionProvider, authenticator, serializer);
      ClientProvider clientProvider = new ClientProviderImpl(subjectProvider, storage);

      audiences = Collections.emptyList();

      dataProvider = new OAuth2DataProvider(clientProvider, subjectProvider, serializer, storage);

      filter = new OAuth2RequestFilter(logger, resourceManager, subjectProvider);
      bind(filter, dataProvider);

      endpoints = new HashSet<>();
      endpoints.add(bind(new AccessTokenService(), dataProvider));
      endpoints.add(bind(new TokenRevocationService(), dataProvider));

      //@formatter:off
      endpoints.add(bind(new AuthorizationCodeEndpoint(clientProvider), dataProvider, subjectProvider));
      endpoints.add(bind(new ImplicitGrantEndpoint(clientProvider), dataProvider, subjectProvider));
      //@formatter:on

      endpoints.add(bind(new AccessTokenValidatorService(), dataProvider));
      endpoints.add(bind(new ClientRegistrationEndpoint(logger), clientProvider, subjectProvider));
      endpoints.add(bind(new ClientEndpoint(logger), clientProvider, subjectProvider));

      // Add OAuth2 application local Writers
      endpoints.add(new AuthorizationDataHtmlWriter());
      endpoints.add(new OOBAuthorizationResponseHtmlWriter());
      endpoints.add(new ClientRegistrationDataHtmlWriter());
      endpoints.add(new ClientRegistrationResponseHtmlWriter());

      application = new OAuth2Application(endpoints);

      grantHandlers = new ArrayList<>();
      grantHandlers.add(bind(new AuthorizationCodeGrantHandler(), dataProvider, new DigestCodeVerifier()));
      grantHandlers.add(bind(new ClientCredentialsGrantHandler(), dataProvider));
      grantHandlers.add(bind(new ResourceOwnerGrantHandler(), dataProvider, subjectProvider));
      grantHandlers.add(bind(new RefreshTokenGrantHandler(), dataProvider));

      tokenValidators = new ArrayList<>();
      if (config.isHawkTokenSupported()) {
         NonceVerifierImpl nonceVerifier = new NonceVerifierImpl();
         NonceStore nonceStore = null;
         nonceVerifier.setNonceStore(nonceStore);
         HawkAccessTokenValidator validator = new HawkAccessTokenValidator();
         validator.setDataProvider(dataProvider);
         validator.setNonceVerifier(nonceVerifier);
         tokenValidators.add(validator);
         this.nonceVerifier = nonceVerifier;
      }
   }

   public void update(Map<String, Object> props) {
      OAuth2Configuration config = OAuth2Configuration.fromProperties(props);
      if (config.isEnabled()) {
         if (!wasRegistered.getAndSet(true)) {
            initialize(config);
            register(registry, bundle);
         }
         configure(config);
      } else {
         stop();
      }
   }

   private void register(JaxRsApplicationRegistry registry, Bundle bundle) {
      for (Object object : JaxRsOAuth.getOAuthProviders()) {
         addProvider(registry, bundle, qualify(object.getClass().getSimpleName()), object);
      }
      registry.register(OAUTH2_APPLICATION_COMPONENT_NAME, bundle, application);
      addProvider(registry, bundle, qualify("filter"), filter);
   }

   private void addProvider(JaxRsApplicationRegistry registry, Bundle bundle, String name, Object object) {
      registeredProviders.add(name);
      registry.registerProvider(name, bundle, object);
   }

   private void deregister(JaxRsApplicationRegistry registry) {
      registry.deregister(OAUTH2_APPLICATION_COMPONENT_NAME);
      for (String componentName : registeredProviders) {
         registry.deregisterProvider(componentName);
      }
      registeredProviders.clear();
   }

   private void configure(OAuth2Configuration config) {
      configure(config, filter);
      configure(config, subjectProvider);
      configure(config, dataProvider);
      configure(config, nonceVerifier);

      for (AccessTokenValidator validator : tokenValidators) {
         configureObject(config, validator);
      }
      for (AccessTokenGrantHandler handler : grantHandlers) {
         configureObject(config, handler);
      }
      for (Object endpoint : endpoints) {
         configureObject(config, endpoint);
      }
   }

   private void configure(OAuth2Configuration config, OAuth2DataProvider provider) {
      provider.setRefreshTokenAllowed(config.isRefreshTokenAllowed());
      provider.setCodeGrantExpiration(config.getCodeGrantExpiration());
      provider.setAccessTokenExpiration(config.getAccessTokenExpiration());
      provider.setRefreshTokenExpiration(config.getRefreshTokenExpiration());

      provider.setSecretKeyAlgorithm(config.getSecretKeyAlgorithm());
      provider.setSecretKeyEncoded(config.getEncodedSecretKey());

      configureObject(config, provider);
   }

   private void configure(OAuth2Configuration config, SubjectProvider provider) {
      provider.setSessionTokenExpiration(config.getSessionTokenExpiration());
      provider.setSecretKeyAlgorithm(config.getSecretKeyAlgorithm());
      provider.setSecretKeyEncoded(config.getEncodedSecretKey());
   }

   private void configure(OAuth2Configuration config, NonceVerifier object) {
      if (object instanceof NonceVerifierImpl) {
         NonceVerifierImpl nonceVerifier = (NonceVerifierImpl) object;
         nonceVerifier.setAllowedWindow(config.getNonceAllowedWindow());
      }
      configureObject(config, object);
   }

   private void configure(OAuth2Configuration config, OAuth2RequestFilter filter) {
      filter.setAudienceIsEndpointAddress(config.isAudienceIsEndpointAddress());
      filter.setUseUserSubject(config.isUseUserSubject());
      filter.setCheckFormData(config.isFilterChecksFormDataForToken());
      filter.setRealm(config.getRealm());

      filter.setRedirectURI(config.getLoginRedirectURI());
      filter.setRedirectErrorURI(config.getLoginRedirectErrorURI());

      filter.setIgnoreBasePath(config.isIgnoreLoginRedirectBasePath());
      filter.setRealm(config.getRealm());
      configureObject(config, filter);
   }

   private void configureObject(OAuth2Configuration config, Object object) {
      if (object instanceof AbstractGrantHandler) {
         AbstractGrantHandler handler = (AbstractGrantHandler) object;
         handler.setCanSupportPublicClients(config.isCanSupportPublicClients());
         handler.setPartialMatchScopeValidation(config.isPartialMatchScopeValidation());
      }

      if (object instanceof RefreshTokenGrantHandler) {
         RefreshTokenGrantHandler handler = (RefreshTokenGrantHandler) object;
         handler.setPartialMatchScopeValidation(config.isPartialMatchScopeValidation());
      }

      if (object instanceof AbstractAccessTokenValidator) {
         AbstractAccessTokenValidator validator = (AbstractAccessTokenValidator) object;
         validator.setRealm(config.getRealm());

         validator.setAudiences(audiences);
         validator.setTokenValidators(tokenValidators);
      }

      if (object instanceof AbstractOAuthService) {
         AbstractOAuthService service = (AbstractOAuthService) object;
         service.setBlockUnsecureRequests(config.isBlockUnsecureRequests());
         service.setWriteOptionalParameters(config.isWriteOptionalParameters());
      }

      if (object instanceof AbstractTokenService) {
         AbstractTokenService tokenService = (AbstractTokenService) object;
         tokenService.setCanSupportPublicClients(config.isCanSupportPublicClients());
         tokenService.setWriteCustomErrors(config.isWriteCustomErrors());
      }

      if (object instanceof RedirectionBasedGrantService) {
         RedirectionBasedGrantService redirectService = (RedirectionBasedGrantService) object;
         redirectService.setPartialMatchScopeValidation(config.isPartialMatchScopeValidation());
         redirectService.setUseRegisteredRedirectUriIfPossible(config.isUseRegisteredRedirectUriIfPossible());
      }

      if (object instanceof AuthorizationCodeGrantService) {
         AuthorizationCodeGrantService codeGrantService = (AuthorizationCodeGrantService) object;
         codeGrantService.setCanSupportPublicClients(config.isCanSupportPublicClients());
      }

      if (object instanceof ImplicitGrantService) {
         ImplicitGrantService implicitGrant = (ImplicitGrantService) object;
         implicitGrant.setReportClientId(config.isReportClientId());
      }

      if (object instanceof AccessTokenService) {
         AccessTokenService accessTokenService = (AccessTokenService) object;
         accessTokenService.setAudiences(audiences);
         accessTokenService.setGrantHandlers(grantHandlers);
      }

      if (object instanceof AbstractClientService) {
         AbstractClientService clientService = (AbstractClientService) object;
         clientService.setBlockUnsecureRequests(config.isBlockUnsecureRequests());
      }
   }

   private static AbstractClientService bind(AbstractClientService object, ClientProvider dataProvider, SubjectProvider subjectProvider) {
      object.setDataProvider(dataProvider);
      object.setResourceOwnerNameProvider(subjectProvider);
      object.setSessionAuthenticityTokenProvider(subjectProvider);
      object.setSubjectCreator(subjectProvider);
      return object;
   }

   private static AbstractAccessTokenValidator bind(AbstractAccessTokenValidator object, OAuthDataProvider dataProvider) {
      object.setDataProvider(dataProvider);
      return object;
   }

   private static AbstractOAuthService bind(RedirectionBasedGrantService object, OAuthDataProvider dataProvider, SubjectProvider subjectProvider) {
      object.setResourceOwnerNameProvider(subjectProvider);
      object.setSessionAuthenticityTokenProvider(subjectProvider);
      object.setSubjectCreator(subjectProvider);
      bind(object, dataProvider);
      return object;
   }

   private static AuthorizationCodeGrantHandler bind(AuthorizationCodeGrantHandler object, OAuthDataProvider dataProvider, CodeVerifierTransformer codeVerifier) {
      object.setCodeVerifierTransformer(codeVerifier);
      bind(object, dataProvider);
      return object;
   }

   private static ResourceOwnerGrantHandler bind(ResourceOwnerGrantHandler handler, OAuthDataProvider dataProvider, ResourceOwnerLoginHandler loginHandler) {
      handler.setLoginHandler(loginHandler);
      bind(handler, dataProvider);
      return handler;
   }

   private static AbstractOAuthService bind(AbstractOAuthService object, OAuthDataProvider dataProvider) {
      object.setDataProvider(dataProvider);
      return object;
   }

   private static RefreshTokenGrantHandler bind(RefreshTokenGrantHandler object, OAuthDataProvider dataProvider) {
      object.setDataProvider(dataProvider);
      return object;
   }

   private static AbstractGrantHandler bind(AbstractGrantHandler object, OAuthDataProvider dataProvider) {
      object.setDataProvider(dataProvider);
      return object;
   }

}
