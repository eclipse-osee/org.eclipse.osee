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

import static org.eclipse.osee.account.rest.model.AccountContexts.ACCOUNTS;
import static org.eclipse.osee.account.rest.model.AccountContexts.ACCOUNT_ACTIVE;
import static org.eclipse.osee.account.rest.model.AccountContexts.ACCOUNT_ID_PARAM;
import static org.eclipse.osee.account.rest.model.AccountContexts.ACCOUNT_ID_TEMPLATE;
import static org.eclipse.osee.account.rest.model.AccountContexts.ACCOUNT_LOGIN;
import static org.eclipse.osee.account.rest.model.AccountContexts.ACCOUNT_LOGOUT;
import static org.eclipse.osee.account.rest.model.AccountContexts.ACCOUNT_PREFERENCES;
import static org.eclipse.osee.account.rest.model.AccountContexts.ACCOUNT_SESSSIONS;
import static org.eclipse.osee.account.rest.model.AccountContexts.ACCOUNT_USERNAME;
import static org.eclipse.osee.account.rest.model.AccountContexts.ACCOUNT_USERNAME_TEMPLATE;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriBuilder;
import org.eclipse.osee.account.rest.client.AccountClient;
import org.eclipse.osee.account.rest.model.AccountActiveData;
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
import org.eclipse.osee.jaxrs.client.JaxRsClient;
import org.eclipse.osee.jaxrs.client.JaxRsExceptions;

/**
 * @author Roberto E. Escobar
 */
public class AccountClientImpl implements AccountClient {

   private static final String OSEE_APPLICATION_SERVER = "osee.application.server";
   private WebTarget baseTarget;
   private WebTarget accountTarget;

   public void start(Map<String, Object> properties) {
      update(properties);
   }

   public void stop() {
      baseTarget = null;
      accountTarget = null;
   }

   public void update(Map<String, Object> properties) {
      JaxRsClient client = JaxRsClient.newBuilder().properties(properties).build();

      String address = properties != null ? (String) properties.get(OSEE_APPLICATION_SERVER) : null;
      if (address == null) {
         address = System.getProperty(OSEE_APPLICATION_SERVER, "");
      }

      URI uri = UriBuilder.fromUri(address).build();
      baseTarget = client.target(uri);
      accountTarget = baseTarget.path(ACCOUNTS);
   }

   @Override
   public AccountSessionData login(String scheme, String username, String password) {
      AccountLoginData data = new AccountLoginData();
      data.setUsername(username);
      data.setPassword(password);
      data.setScheme(scheme);

      WebTarget resource = accountTarget.path(ACCOUNT_LOGIN);
      try {
         return resource.request(MediaType.APPLICATION_JSON_TYPE).post(Entity.json(data), AccountSessionData.class);
      } catch (Exception ex) {
         throw JaxRsExceptions.asOseeException(ex);
      }
   }

   @Override
   public boolean logout(AccountSessionData session) {
      WebTarget resource = accountTarget.path(ACCOUNT_LOGOUT);
      try {
         Response response = resource.request().post(Entity.json(session));
         return Status.OK.getStatusCode() == response.getStatus();
      } catch (Exception ex) {
         throw JaxRsExceptions.asOseeException(ex);
      }
   }

   @Override
   public AccountInfoData createAccount(String userName, AccountInput input) {
      WebTarget resource = accountTarget.path(ACCOUNT_USERNAME_TEMPLATE).resolveTemplate(ACCOUNT_USERNAME, userName);
      try {
         return resource.request(MediaType.APPLICATION_JSON_TYPE).post(Entity.json(input), AccountInfoData.class);
      } catch (Exception ex) {
         throw JaxRsExceptions.asOseeException(ex);
      }
   }

   @Override
   public boolean deleteAccount(String accountId) {
      WebTarget resource = accountTarget.path(ACCOUNT_ID_TEMPLATE).resolveTemplate(ACCOUNT_ID_PARAM, accountId);
      try {
         Response response = resource.request().delete();
         return Status.OK.getStatusCode() == response.getStatus();
      } catch (Exception ex) {
         throw JaxRsExceptions.asOseeException(ex);
      }
   }

   @Override
   public ResultSet<AccountSessionDetailsData> getAccountSessionDataByUniqueField(String accountId) {
      WebTarget resource =
         accountTarget.path(ACCOUNT_ID_TEMPLATE).path(ACCOUNT_SESSSIONS).resolveTemplate(ACCOUNT_ID_PARAM, accountId);
      try {
         AccountSessionDetailsData[] data =
            resource.request(MediaType.APPLICATION_JSON_TYPE).get(AccountSessionDetailsData[].class);
         return ResultSets.newResultSet(data);
      } catch (Exception ex) {
         throw JaxRsExceptions.asOseeException(ex);
      }
   }

   @Override
   public ResultSet<AccountInfoData> getAllAccounts() {
      try {
         AccountInfoData[] accounts =
            accountTarget.request(MediaType.APPLICATION_JSON_TYPE).get(AccountInfoData[].class);
         return ResultSets.newResultSet(accounts);
      } catch (Exception ex) {
         throw JaxRsExceptions.asOseeException(ex);
      }
   }

   @Override
   public AccountDetailsData getAccountDetailsByUniqueField(String accountId) {
      WebTarget resource = accountTarget.path(ACCOUNT_ID_TEMPLATE).resolveTemplate(ACCOUNT_ID_PARAM, accountId);
      try {
         return resource.request(MediaType.APPLICATION_JSON_TYPE).get(AccountDetailsData.class);
      } catch (Exception ex) {
         throw JaxRsExceptions.asOseeException(ex);
      }
   }

   @Override
   public AccountPreferencesData getAccountPreferencesByUniqueField(String accountId) {
      WebTarget resource =
         accountTarget.path(ACCOUNT_ID_TEMPLATE).path(ACCOUNT_PREFERENCES).resolveTemplate(ACCOUNT_ID_PARAM, accountId);
      try {
         return resource.request(MediaType.APPLICATION_JSON_TYPE).get(AccountPreferencesData.class);
      } catch (Exception ex) {
         throw JaxRsExceptions.asOseeException(ex);
      }
   }

   @Override
   public boolean setAccountActive(String accountId, boolean active) {
      WebTarget resource =
         accountTarget.path(ACCOUNT_ID_TEMPLATE).path(ACCOUNT_ACTIVE).resolveTemplate(ACCOUNT_ID_PARAM, accountId);
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
      WebTarget resource =
         accountTarget.path(ACCOUNT_ID_TEMPLATE).path(ACCOUNT_ACTIVE).resolveTemplate(ACCOUNT_ID_PARAM, accountId);
      try {
         AccountActiveData data = resource.request(MediaType.APPLICATION_JSON_TYPE).get(AccountActiveData.class);
         return data.isActive();
      } catch (Exception ex) {
         throw JaxRsExceptions.asOseeException(ex);
      }
   }

   private boolean setAccountActive(WebTarget resource) {
      try {
         Response response = resource.request().put(null);
         return Status.OK.getStatusCode() == response.getStatus();
      } catch (Exception ex) {
         throw JaxRsExceptions.asOseeException(ex);
      }
   }

   private boolean setAccountInActive(WebTarget resource) {
      try {
         Response response = resource.request().delete();
         return Status.OK.getStatusCode() == response.getStatus();
      } catch (Exception ex) {
         throw JaxRsExceptions.asOseeException(ex);
      }
   }

   @Override
   public boolean setAccountPreferences(String accountId, Map<String, String> preferences) {
      WebTarget resource =
         accountTarget.path(ACCOUNT_ID_TEMPLATE).path(ACCOUNT_PREFERENCES).resolveTemplate(ACCOUNT_ID_PARAM, accountId);

      AccountPreferencesInput input = new AccountPreferencesInput();
      input.setMap(preferences);
      try {
         Response response = resource.request().put(Entity.json(input));
         return Status.OK.getStatusCode() == response.getStatus();
      } catch (Exception ex) {
         throw JaxRsExceptions.asOseeException(ex);
      }
   }

   private ResultSet<SubscriptionData> getSubscriptionsForAccount(String userId) {
      WebTarget resource =
         baseTarget.path("subscriptions/for-account/{account-id}").resolveTemplate(ACCOUNT_ID_PARAM, userId);
      Builder builder = resource.request(MediaType.APPLICATION_JSON_TYPE);
      try {
         SubscriptionData[] data = builder.get(SubscriptionData[].class);
         return ResultSets.newResultSet(data);
      } catch (Exception ex) {
         throw JaxRsExceptions.asOseeException(ex);
      }
   }

   private UriBuilder newUnsubscribeBuilder() {
      return baseTarget.getUriBuilder().path("unsubscribe").path("ui").path("{subscription-uuid}");
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
