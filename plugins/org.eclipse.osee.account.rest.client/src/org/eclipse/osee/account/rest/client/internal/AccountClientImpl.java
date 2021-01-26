/*********************************************************************
 * Copyright (c) 2013 Boeing
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

package org.eclipse.osee.account.rest.client.internal;

import static org.eclipse.osee.account.rest.model.AccountContexts.ACCOUNTS;
import static org.eclipse.osee.account.rest.model.AccountContexts.ACCOUNT_ACTIVE;
import static org.eclipse.osee.account.rest.model.AccountContexts.ACCOUNT_LOGIN;
import static org.eclipse.osee.account.rest.model.AccountContexts.ACCOUNT_LOGOUT;
import static org.eclipse.osee.account.rest.model.AccountContexts.ACCOUNT_PREFERENCES;
import static org.eclipse.osee.account.rest.model.AccountContexts.ACCOUNT_SESSSIONS;
import static org.eclipse.osee.framework.core.data.OseeClient.OSEE_APPLICATION_SERVER;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import javax.ws.rs.client.Entity;
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
import org.eclipse.osee.account.rest.model.AccountWebPreferences;
import org.eclipse.osee.account.rest.model.SubscriptionData;
import org.eclipse.osee.framework.core.JaxRsApi;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.jdk.core.type.ResultSet;
import org.eclipse.osee.framework.jdk.core.type.ResultSets;

/**
 * @author Roberto E. Escobar
 */
public class AccountClientImpl implements AccountClient {
   private URI baseUri;
   private JaxRsApi jaxRsApi;

   public void bindJaxRsApi(JaxRsApi jaxRsApi) {
      this.jaxRsApi = jaxRsApi;
   }

   public void start(Map<String, Object> properties) {
      String address = properties != null ? (String) properties.get(OSEE_APPLICATION_SERVER) : null;
      if (address == null) {
         address = System.getProperty(OSEE_APPLICATION_SERVER, "");
      }
      baseUri = UriBuilder.fromUri(address).build();

   }

   @Override
   public AccountSessionData login(String scheme, String username, String password) {
      AccountLoginData data = new AccountLoginData();
      data.setUsername(username);
      data.setPassword(password);
      data.setScheme(scheme);

      WebTarget target = jaxRsApi.newTarget(ACCOUNTS, ACCOUNT_LOGIN);
      return target.request(MediaType.APPLICATION_JSON_TYPE).post(Entity.json(data), AccountSessionData.class);
   }

   @Override
   public boolean logout(AccountSessionData session) {
      WebTarget target = jaxRsApi.newTarget(ACCOUNTS, ACCOUNT_LOGOUT);
      Response response = target.request().post(Entity.json(session));
      return Status.OK.getStatusCode() == response.getStatus();
   }

   @Override
   public AccountInfoData createAccount(String userName, AccountInput input) {
      WebTarget target = jaxRsApi.newTarget(ACCOUNTS, userName);
      return target.request(MediaType.APPLICATION_JSON_TYPE).post(Entity.json(input), AccountInfoData.class);
   }

   @Override
   public boolean deleteAccount(Long accountId) {
      WebTarget target = jaxRsApi.newTarget(ACCOUNTS, accountId.toString());
      Response response = target.request().delete();
      return Status.OK.getStatusCode() == response.getStatus();
   }

   @Override
   public ResultSet<AccountSessionDetailsData> getAccountSessionDataByUniqueField(String accountId) {
      WebTarget target = jaxRsApi.newTarget(ACCOUNTS, accountId, ACCOUNT_SESSSIONS);
      AccountSessionDetailsData[] data =
         target.request(MediaType.APPLICATION_JSON_TYPE).get(AccountSessionDetailsData[].class);
      return ResultSets.newResultSet(data);
   }

   @Override
   public ResultSet<AccountInfoData> getAllAccounts() {
      WebTarget target = jaxRsApi.newTarget(ACCOUNTS);
      AccountInfoData[] accounts = target.request(MediaType.APPLICATION_JSON_TYPE).get(AccountInfoData[].class);
      return ResultSets.newResultSet(accounts);
   }

   @Override
   public AccountDetailsData getAccountDetailsById(Long accountId) {
      WebTarget target = jaxRsApi.newTarget(ACCOUNTS, accountId.toString());
      return target.request(MediaType.APPLICATION_JSON_TYPE).get(AccountDetailsData.class);
   }

   @Override
   public AccountPreferencesData getAccountPreferencesById(Long accountId) {
      WebTarget target = jaxRsApi.newTarget(ACCOUNTS, accountId.toString(), ACCOUNT_PREFERENCES);
      return target.request(MediaType.APPLICATION_JSON_TYPE).get(AccountPreferencesData.class);
   }

   @Override
   public AccountWebPreferences getAccountWebPreferencesByUniqueField(ArtifactId accountId) {
      WebTarget target = jaxRsApi.newTarget(ACCOUNTS, ACCOUNT_PREFERENCES, accountId.getIdString());
      return target.request(MediaType.APPLICATION_JSON_TYPE).get(AccountWebPreferences.class);
   }

   @Override
   public boolean setAccountActive(Long accountId, boolean active) {
      WebTarget target = jaxRsApi.newTarget(ACCOUNTS, accountId.toString(), ACCOUNT_ACTIVE);

      Response response;
      if (active) {
         response = target.request().put(null);
      } else {
         response = target.request().delete();
      }

      return Status.OK.getStatusCode() == response.getStatus();
   }

   @Override
   public boolean isAccountActive(Long accountId) {
      WebTarget target = jaxRsApi.newTarget(ACCOUNTS, accountId.toString(), ACCOUNT_ACTIVE);
      AccountActiveData data = target.request(MediaType.APPLICATION_JSON_TYPE).get(AccountActiveData.class);
      return data.isActive();
   }

   @Override
   public boolean setAccountPreferences(Long accountId, Map<String, String> preferences) {
      WebTarget target = jaxRsApi.newTarget(ACCOUNTS, accountId.toString(), ACCOUNT_PREFERENCES);

      AccountPreferencesInput input = new AccountPreferencesInput();
      input.setMap(preferences);
      Response response = target.request().put(Entity.json(input));
      return Status.OK.getStatusCode() == response.getStatus();
   }

   private ResultSet<SubscriptionData> getSubscriptionsForAccount(Long accountId) {
      WebTarget target = jaxRsApi.newTarget("subscriptions", "for-account", accountId.toString());
      SubscriptionData[] data = target.request(MediaType.APPLICATION_JSON_TYPE).get(SubscriptionData[].class);
      return ResultSets.newResultSet(data);
   }

   @Override
   public ResultSet<UnsubscribeInfo> getUnsubscribeUris(Long accountId, Collection<String> groupNames) {
      ResultSet<UnsubscribeInfo> toReturn = ResultSets.emptyResultSet();
      ResultSet<SubscriptionData> results = getSubscriptionsForAccount(accountId);
      if (!results.isEmpty()) {
         List<UnsubscribeInfo> infos = new ArrayList<>();

         UriBuilder builder = UriBuilder.fromUri(baseUri).path("unsubscribe").path("ui").path("{subscription-uuid}");
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