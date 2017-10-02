/*******************************************************************************
 * Copyright (c) 2014 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.jaxrs.server.internal.security.oauth2.rs;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import org.eclipse.osee.jaxrs.server.internal.JaxRsConstants;
import org.eclipse.osee.jaxrs.server.internal.applications.JaxRsApplicationRegistry;
import org.eclipse.osee.jaxrs.server.security.JaxRsOAuth;
import org.eclipse.osee.jaxrs.server.security.JaxRsOAuthResourceServerFilter;
import org.eclipse.osee.jaxrs.server.security.JaxRsOAuthResourceServerFilter.Builder;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

/**
 * @author Roberto E. Escobar
 */
public class OAuth2ResourceServer {

   private JaxRsApplicationRegistry registry;

   private final Set<String> registeredProviders = new HashSet<>();
   private final AtomicBoolean wasRegistered = new AtomicBoolean();
   private volatile JaxRsOAuthResourceServerFilter filter;
   private volatile List<String> audiences;
   private volatile Bundle bundle;

   public void setJaxRsApplicationRegistry(JaxRsApplicationRegistry registry) {
      this.registry = registry;
   }

   public void start(BundleContext bundleContext, Map<String, Object> props) {
      bundle = bundleContext.getBundle();
      update(props);
   }

   public void stop() {
      if (wasRegistered.getAndSet(false)) {
         deregister(registry);
         filter = null;
      }
   }

   public void update(Map<String, Object> props) {
      OAuth2ResourceServerConfig config = OAuth2ResourceServerConfig.fromProperties(props);
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

   private void initialize(OAuth2ResourceServerConfig config) {
      audiences = Collections.emptyList();

      Builder builder = JaxRsOAuthResourceServerFilter.newBuilder() //
         .serverKey(config.getResourceServerKey()) //
         .serverSecret(config.getResourceServerSecret())//
         .serverUri(config.getValidationServerUri());

      if (config.isCacheTokensAllowed()) {
         filter = builder.build(config.getTokenCacheMaxSize(), config.getTokenCacheEvictTimeoutMillis());
      } else {
         filter = builder.build();
      }
   }

   private void configure(OAuth2ResourceServerConfig config) {
      filter.setRealm(config.getRealm());
      filter.setAudienceIsEndpointAddress(config.isAudienceIsEndpointAddress());
      filter.setUseUserSubject(config.isUseUserSubject());
      filter.setCheckFormData(config.isFilterChecksFormDataForToken());
      filter.setAudiences(audiences);
   }

   private void register(JaxRsApplicationRegistry registry, Bundle bundle) {
      for (Object object : JaxRsOAuth.getOAuthProviders()) {
         addProvider(registry, bundle, qualify(object.getClass().getSimpleName()), object);
      }
      addProvider(registry, bundle, qualify("filter"), filter);
   }

   private void addProvider(JaxRsApplicationRegistry registry, Bundle bundle, String name, Object object) {
      registeredProviders.add(name);
      registry.registerProvider(name, bundle, object);
   }

   private void deregister(JaxRsApplicationRegistry registry) {
      for (String componentName : registeredProviders) {
         registry.deregisterProvider(componentName);
      }
      registeredProviders.clear();
   }

   private static String qualify(String name) {
      return String.format("%s.security.oauth2.client.%s", JaxRsConstants.NAMESPACE, name);
   }

}
