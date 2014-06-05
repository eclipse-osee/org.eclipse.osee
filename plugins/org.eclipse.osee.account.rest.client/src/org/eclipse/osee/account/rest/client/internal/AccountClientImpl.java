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
package org.eclipse.osee.account.rest.client.internal;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriBuilder;
import org.eclipse.osee.account.rest.client.AccountClient;
import org.eclipse.osee.account.rest.model.AccountActiveData;
import org.eclipse.osee.account.rest.model.AccountContexts;
import org.eclipse.osee.account.rest.model.AccountDetailsData;
import org.eclipse.osee.account.rest.model.AccountInfoData;
import org.eclipse.osee.account.rest.model.AccountInput;
import org.eclipse.osee.account.rest.model.AccountLoginData;
import org.eclipse.osee.account.rest.model.AccountPreferencesData;
import org.eclipse.osee.account.rest.model.AccountPreferencesInput;
import org.eclipse.osee.account.rest.model.AccountSessionData;
import org.eclipse.osee.account.rest.model.AccountSessionDetailsData;
import org.eclipse.osee.account.rest.model.SubscriptionData;
import org.eclipse.osee.framework.jdk.core.type.ResultSet;
import org.eclipse.osee.framework.jdk.core.type.ResultSets;
import org.eclipse.osee.jaxrs.client.OseeClientProperties;
import org.eclipse.osee.jaxrs.client.WebClientProvider;
import com.google.inject.Inject;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;

/**
 * @author Roberto E. Escobar
 */
public class AccountClientImpl implements AccountClient {

   private WebClientProvider clientProvider;
   private URI serverUri;

   @Inject
   public void setWebClientProvider(WebClientProvider clientProvider) {
      this.clientProvider = clientProvider;
   }

   public void start() {
      String appServer = OseeClientProperties.getApplicationServerAddress();
      serverUri = URI.create(appServer);
   }

   public void stop() {
      //
   }

   private UriBuilder newBuilder() {
      return UriBuilder.fromUri(serverUri).path(AccountContexts.ACCOUNTS_BASE);
   }

   private <T> T get(URI uri, Class<T> clazz) {
      WebResource resource = clientProvider.createResource(uri);
      try {
         return resource.accept(MediaType.APPLICATION_JSON_TYPE).get(clazz);
      } catch (UniformInterfaceException ex) {
         throw clientProvider.handleException(ex);
      }
   }

   @Override
   public AccountSessionData login(String scheme, String username, String password) {
      URI uri = newBuilder()//
      .path(AccountContexts.ACCOUNTS)//
      .path(AccountContexts.ACCOUNT_LOGIN)//
      .build();

      AccountLoginData data = new AccountLoginData();
      data.setUsername(username);
      data.setPassword(password);
      data.setScheme(scheme);

      WebResource resource = clientProvider.createResource(uri);
      try {
         return resource.post(AccountSessionData.class, data);
      } catch (UniformInterfaceException ex) {
         throw clientProvider.handleException(ex);
      }
   }

   @Override
   public boolean logout(AccountSessionData session) {
      URI uri = newBuilder()//
      .path(AccountContexts.ACCOUNTS)//
      .path(AccountContexts.ACCOUNT_LOGOUT)//
      .build();

      WebResource resource = clientProvider.createResource(uri);
      int status;
      try {
         ClientResponse response = resource.post(ClientResponse.class, session);
         status = response.getStatus();
      } catch (UniformInterfaceException ex) {
         ClientResponse clientResponse = ex.getResponse();
         status = clientResponse.getStatus();
         if (Status.NOT_MODIFIED.getStatusCode() != status) {
            throw clientProvider.handleException(ex);
         }
      }
      return Status.OK.getStatusCode() == status;
   }

   @Override
   public AccountInfoData createAccount(String userName, AccountInput input) {
      URI uri = newBuilder()//
      .path(AccountContexts.ACCOUNTS)//
      .path(AccountContexts.ACCOUNT_USERNAME_TEMPLATE)//
      .build(userName);

      WebResource resource = clientProvider.createResource(uri);
      try {
         AccountInfoData data =
            resource.accept(MediaType.APPLICATION_JSON_TYPE).type(MediaType.APPLICATION_JSON_TYPE).post(
               AccountInfoData.class, input);
         return data;
      } catch (UniformInterfaceException ex) {
         throw clientProvider.handleException(ex);
      }
   }

   @Override
   public boolean deleteAccount(String accountId) {
      URI uri = newBuilder()//
      .path(AccountContexts.ACCOUNTS)//
      .path(AccountContexts.ACCOUNT_ID_TEMPLATE)//
      .build(accountId);
      WebResource resource = clientProvider.createResource(uri);

      ClientResponse response;
      try {
         response = resource.delete(ClientResponse.class);
      } catch (UniformInterfaceException ex) {
         throw clientProvider.handleException(ex);
      }
      int status = response.getStatus();
      return Status.OK.getStatusCode() == status;
   }

   @Override
   public ResultSet<AccountSessionDetailsData> getAccountSessionDataByUniqueField(String accountId) {
      URI uri = newBuilder()//
      .path(AccountContexts.ACCOUNTS)//
      .path(AccountContexts.ACCOUNT_ID_TEMPLATE) //
      .path(AccountContexts.ACCOUNT_SESSSIONS)//
      .build(accountId);
      AccountSessionDetailsData[] data = get(uri, AccountSessionDetailsData[].class);
      return ResultSets.newResultSet(data);
   }

   @Override
   public ResultSet<AccountInfoData> getAllAccounts() {
      URI uri = newBuilder()//
      .path(AccountContexts.ACCOUNTS)//
      .build();
      AccountInfoData[] accounts = get(uri, AccountInfoData[].class);
      return ResultSets.newResultSet(accounts);
   }

   @Override
   public AccountDetailsData getAccountDetailsByUniqueField(String accountId) {
      URI uri = newBuilder()//
      .path(AccountContexts.ACCOUNTS)//
      .path(AccountContexts.ACCOUNT_ID_TEMPLATE)//
      .build(accountId);
      return get(uri, AccountDetailsData.class);
   }

   @Override
   public AccountPreferencesData getAccountPreferencesByUniqueField(String accountId) {
      URI uri = newBuilder()//
      .path(AccountContexts.ACCOUNTS)//
      .path(AccountContexts.ACCOUNT_ID_TEMPLATE)//
      .path(AccountContexts.ACCOUNT_PREFERENCES)//
      .build(accountId);
      AccountPreferencesData data = get(uri, AccountPreferencesData.class);
      return data;
   }

   @Override
   public boolean setAccountActive(String accountId, boolean active) {
      URI uri = newBuilder()//
      .path(AccountContexts.ACCOUNTS)//
      .path(AccountContexts.ACCOUNT_ID_TEMPLATE)//
      .path(AccountContexts.ACCOUNT_ACTIVE)//
      .build(accountId);
      WebResource resource = clientProvider.createResource(uri);
      boolean result;
      if (active) {
         result = setAccountActive(resource);
      } else {
         result = setAccountInActive(resource);
      }
      return result;
   }

   @Override
   public boolean isAccountActive(String accountId) {
      URI uri = newBuilder()//
      .path(AccountContexts.ACCOUNTS)//
      .path(AccountContexts.ACCOUNT_ID_TEMPLATE)//
      .path(AccountContexts.ACCOUNT_ACTIVE)//
      .build(accountId);
      AccountActiveData data = get(uri, AccountActiveData.class);
      return data.isActive();
   }

   private boolean setAccountActive(WebResource resource) {
      int status;
      try {
         ClientResponse response = resource.put(ClientResponse.class);
         status = response.getStatus();
      } catch (UniformInterfaceException ex) {
         ClientResponse clientResponse = ex.getResponse();
         status = clientResponse.getStatus();
         if (Status.NOT_MODIFIED.getStatusCode() != status) {
            throw clientProvider.handleException(ex);
         }
      }
      return Status.OK.getStatusCode() == status;
   }

   private boolean setAccountInActive(WebResource resource) {
      int status;
      try {
         ClientResponse response = resource.delete(ClientResponse.class);
         status = response.getStatus();
      } catch (UniformInterfaceException ex) {
         ClientResponse clientResponse = ex.getResponse();
         status = clientResponse.getStatus();
         if (Status.NOT_MODIFIED.getStatusCode() != status) {
            throw clientProvider.handleException(ex);
         }
      }
      return Status.OK.getStatusCode() == status;
   }

   @Override
   public boolean setAccountPreferences(String accountId, Map<String, String> preferences) {
      URI uri = newBuilder()//
      .path(AccountContexts.ACCOUNTS)//
      .path(AccountContexts.ACCOUNT_ID_TEMPLATE)//
      .path(AccountContexts.ACCOUNT_PREFERENCES)//
      .build(accountId);

      AccountPreferencesInput input = new AccountPreferencesInput();
      input.setMap(preferences);

      WebResource resource = clientProvider.createResource(uri);
      int status;
      try {
         ClientResponse response = resource.put(ClientResponse.class, input);
         status = response.getStatus();
      } catch (UniformInterfaceException ex) {
         ClientResponse clientResponse = ex.getResponse();
         status = clientResponse.getStatus();
         if (Status.NOT_MODIFIED.getStatusCode() != status) {
            throw clientProvider.handleException(ex);
         }
      }
      return Status.OK.getStatusCode() == status;
   }

   private ResultSet<SubscriptionData> getSubscriptionsForAccount(String userId) {
      URI uri =
         newBuilder().path(AccountContexts.ACCOUNTS).path("subscriptions").path("for-account").path("{account-id}").build(
            userId);
      SubscriptionData[] data = get(uri, SubscriptionData[].class);
      return ResultSets.newResultSet(data);
   }

   private UriBuilder newUnsubscribeBuilder() {
      return newBuilder().path(AccountContexts.ACCOUNTS).path("unsubscribe").path("ui").path("{subscription-uuid}");
   }

   @Override
   public ResultSet<UnsubscribeInfo> getUnsubscribeUris(String userUuid, Collection<String> groupNames) {
      ResultSet<UnsubscribeInfo> toReturn = ResultSets.emptyResultSet();
      ResultSet<SubscriptionData> results = getSubscriptionsForAccount(userUuid);
      if (!results.isEmpty()) {
         List<UnsubscribeInfo> infos = new ArrayList<UnsubscribeInfo>();
         UriBuilder builder = newUnsubscribeBuilder();
         for (SubscriptionData subscription : results) {
            if (subscription.isActive() && groupNames.contains(subscription.getName())) {
               String name = subscription.getName();
               URI unsubscribeUri = builder.build(subscription.getGuid());
               infos.add(newUnsubscribeInfo(name, unsubscribeUri));
            }
         }
         toReturn = ResultSets.newResultSet(infos);
      }
      return ResultSets.newResultSet(toReturn);
   }

   private UnsubscribeInfo newUnsubscribeInfo(final String subscriptionName, final URI unsubscribeUri) {
      return new UnsubscribeInfo() {

         @Override
         public String getName() {
            return subscriptionName;
         }

         @Override
         public URI getUnsubscribeUri() {
            return unsubscribeUri;
         }
      };
   }
}
