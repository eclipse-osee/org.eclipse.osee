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
package org.eclipse.osee.jaxrs.server.internal.security.oauth2.provider.adapters;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.io.InputSupplier;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import org.apache.cxf.helpers.IOUtils;
import org.apache.cxf.io.CachedOutputStream;
import org.apache.cxf.rs.security.oauth2.common.Client;
import org.apache.cxf.rs.security.oauth2.common.UserSubject;
import org.apache.cxf.rs.security.oauth2.utils.OAuthUtils;
import org.eclipse.osee.account.admin.OseePrincipal;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.jaxrs.server.internal.security.oauth2.provider.ClientProvider;
import org.eclipse.osee.jaxrs.server.internal.security.oauth2.provider.SubjectProvider;
import org.eclipse.osee.jaxrs.server.internal.security.oauth2.provider.endpoints.ClientFormData;
import org.eclipse.osee.jaxrs.server.security.JaxRsOAuthStorage;
import org.eclipse.osee.jaxrs.server.security.OAuthClient;

/**
 * @author Roberto E. Escobar
 */
public class ClientProviderImpl implements ClientProvider {

   private static final int MAX_LOGOS = 10;
   private static final long EVICT_AFTER_30_MINS = 30L * 60L * 1000L;

   private final LoadingCache<String, CachedOutputStream> logosCache;
   private final SubjectProvider subjectProvider;
   private final JaxRsOAuthStorage storage;

   public ClientProviderImpl(SubjectProvider subjectProvider, JaxRsOAuthStorage storage) {
      super();
      this.subjectProvider = subjectProvider;
      this.storage = storage;
      this.logosCache = newLogoCache(MAX_LOGOS, EVICT_AFTER_30_MINS);
   }

   @Override
   public long getClientId(Client client) {
      long clientUuid = -1;
      if (client instanceof ApplicationClient) {
         ApplicationClient appClient = (ApplicationClient) client;
         clientUuid = appClient.getClientUuid();
      } else {
         String clientKey = client.getClientId();
         clientUuid = storage.getClientUuidByKey(clientKey);
      }
      return clientUuid;
   }

   @Override
   public Client getClient(String clientId) {
      OAuthClient data = storage.getClientByClientKey(clientId);

      ApplicationClient client = null;
      if (data != null) {
         UserSubject subject = subjectProvider.getSubjectById(data.getSubjectId());

         client = new ApplicationClient(data.getClientUuid(), data.getSubjectId(), data.getGuid());
         client.setSubject(subject);
         client.setApplicationName(data.getApplicationName());
         client.setApplicationDescription(data.getApplicationDescription());
         client.setApplicationWebUri(data.getApplicationWebUri());
         client.setProperties(data.getProperties());
         client.setConfidential(data.isConfidential());

         client.setClientId(data.getClientId());
         client.setClientSecret(data.getClientSecret());

         client.setRegisteredAudiences(data.getRegisteredAudiences());
         client.setAllowedGrantTypes(data.getAllowedGrantTypes());
         client.setRegisteredScopes(data.getRegisteredScopes());
         client.setRedirectUris(data.getRedirectUris());
         client.setApplicationCertificates(data.getApplicationCertificates());
         client.setApplicationLogoUri(data.getApplicationLogoUri());
         client.setApplicationLogoSupplier(data.getApplicationLogoSupplier());
      }
      return client;
   }

   @Override
   public Client createClient(UriInfo uriInfo, OseePrincipal principal, final ClientFormData data) {
      String guid = data.getGuid();
      long clientUuid = Lib.generateUuid();
      long subjectId = subjectProvider.getSubjectId(data.getUserSubject());

      ApplicationClient client = new ApplicationClient(clientUuid, subjectId, guid);
      client.setSubject(data.getUserSubject());
      client.setApplicationName(data.getName());
      client.setApplicationDescription(data.getDescription());
      client.setApplicationWebUri(data.getWebUri());
      client.setProperties(data.getProperties());
      client.setConfidential(data.isConfidential());

      /**
       * generate credentials
       */
      String clientId = OAuthUtils.generateRandomTokenKey();
      client.setClientId(clientId);

      if (client.isConfidential()) {
         String clientSecret = OAuthUtils.generateRandomTokenKey();
         client.setClientSecret(clientSecret);
      }

      /**
       * <pre>
       * Additional Security options:
       *  - restrict resource server
       *  - restrict allowed grant types
       *  - register allowed scopes/permissions
       *  - restrict redirect/callback URIs
       *  - authenticate using certificate
       * </pre>
       */
      client.setRegisteredAudiences(data.getAllowedAudiences());
      client.setAllowedGrantTypes(data.getAllowedGrantTypes());
      client.setRegisteredScopes(data.getAllowedScopes());
      client.setRedirectUris(data.getRedirectUris());
      client.setApplicationCertificates(data.getCertificates());
      client.setApplicationLogoUri(data.getLogoUri());
      if (data.isLogoAvailable()) {
         InputSupplier<InputStream> logoSupplier = new InputSupplier<InputStream>() {
            @Override
            public InputStream getInput() {
               return data.getLogoContent();
            }
         };
         client.setApplicationLogoSupplier(logoSupplier);
      }
      storage.storeClient(principal, client);
      return client;
   }

   @Override
   public URI getClientLogoUri(UriInfo uriInfo, Client client) {
      String clientGuid;
      if (client instanceof ApplicationClient) {
         ApplicationClient appClient = (ApplicationClient) client;
         clientGuid = appClient.getGuid();
      } else {
         OAuthClient authClient = storage.getClientByClientKey(client.getClientId());
         clientGuid = authClient.getGuid();
      }
      return UriBuilder.fromUri(uriInfo.getBaseUri()).path("client").path("{client-guid}").path("logo").build(
         clientGuid);
   }

   @Override
   public InputSupplier<InputStream> getClientLogoSupplier(UriInfo uriInfo, String applicationGuid) {
      OAuthClient client = storage.getClientByClientGuid(applicationGuid);
      InputSupplier<InputStream> supplier = null;
      if (client.hasApplicationLogoSupplier()) {
         supplier = client.getApplicationLogoSupplier();
      } else {
         String logoUri = client.getApplicationLogoUri();
         if (Strings.isValid(logoUri)) {
            supplier = newSupplier(logoUri);
         } else {
            // provide default image if available;
         }
      }
      return supplier;
   }

   private InputSupplier<InputStream> newSupplier(final String uri) {
      return new InputSupplier<InputStream>() {

         @Override
         public InputStream getInput() throws IOException {
            CachedOutputStream cos;
            try {
               cos = logosCache.get(uri);
            } catch (ExecutionException ex) {
               throw new IOException(ex);
            }
            return cos.getInputStream();
         }
      };
   }

   private static LoadingCache<String, CachedOutputStream> newLogoCache(int cacheMaxSize, long cacheEvictTimeoutMillis) {
      return newCache(new CacheLoader<String, CachedOutputStream>() {

         @Override
         public CachedOutputStream load(String uri) throws Exception {
            CachedOutputStream cos = new CachedOutputStream();
            InputStream inputStream = null;
            try {
               URL url = new URL(uri);
               inputStream = new BufferedInputStream(url.openStream());
               IOUtils.copy(inputStream, cos);
            } finally {
               Lib.close(inputStream);
            }
            return cos;
         }

      }, cacheMaxSize, cacheEvictTimeoutMillis);
   }

   private static <K, V> LoadingCache<K, V> newCache(CacheLoader<K, V> loader, int cacheMaxSize, long cacheEvictTimeoutMillis) {
      return CacheBuilder.newBuilder()//
         .maximumSize(cacheMaxSize)//
         .expireAfterWrite(cacheEvictTimeoutMillis, TimeUnit.MILLISECONDS)//
         .build(loader);
   }
}