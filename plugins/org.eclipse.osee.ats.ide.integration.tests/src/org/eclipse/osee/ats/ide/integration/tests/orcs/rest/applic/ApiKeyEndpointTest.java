/*********************************************************************
 * Copyright (c) 2024 Boeing
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

package org.eclipse.osee.ats.ide.integration.tests.orcs.rest.applic;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status.Family;
import org.eclipse.osee.ats.ide.util.ServiceUtil;
import org.eclipse.osee.client.test.framework.NotProductionDataStoreRule;
import org.eclipse.osee.client.test.framework.PostgresOnly;
import org.eclipse.osee.client.test.framework.PostgresOnlyRule;
import org.eclipse.osee.framework.core.data.ApiKey;
import org.eclipse.osee.framework.core.data.KeyScopeContainer;
import org.eclipse.osee.orcs.rest.model.ApiKeyEndpoint;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;

@PostgresOnly
public class ApiKeyEndpointTest {
   @Rule
   public PostgresOnlyRule postgresOnly = new PostgresOnlyRule();

   @Rule
   public NotProductionDataStoreRule notProduction = new NotProductionDataStoreRule();
   private static ApiKeyEndpoint apiKeyEndpoint;
   private static final int keyLength = 32;
   private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
   private static LocalDate currentDate = LocalDate.now();
   private static String currentDateString = currentDate.format(dateFormatter);

   private static GenericType<List<KeyScopeContainer>> keyScopeContainerList =
      new GenericType<List<KeyScopeContainer>>() {
         /* anonymous subclass */};
   private static GenericType<Map<String, String>> uidAndValueMap = new GenericType<Map<String, String>>() {
      /* anonymous subclass */};
   private static GenericType<List<ApiKey>> apiKeyList = new GenericType<List<ApiKey>>() {
      /* anonymous subclass */};

   @BeforeClass
   public static void testSetup() {
      apiKeyEndpoint = ServiceUtil.getOseeClient().getApiKeyEndpoint();
   }

   @Test
   public void testKeyAndUID() {

      String expirationDate1 = getFutureDate(3);
      String expirationDate2 = getFutureDate(5);

      ApiKey keyToCreate1 = new ApiKey("Key 1", setKeyScopes(new int[] {0, 1}), currentDateString, expirationDate1, "");
      ApiKey keyToCreate2 = new ApiKey("Key 2", setKeyScopes(new int[] {2}), currentDateString, expirationDate2, "");

      Map<String, String> uidAndValue1 = apiKeyEndpoint.createApiKey(keyToCreate1).readEntity(uidAndValueMap);
      Map<String, String> uidAndValue2 = apiKeyEndpoint.createApiKey(keyToCreate2).readEntity(uidAndValueMap);

      String uid1 = uidAndValue1.get("uniqueID");
      String keyValue1 = uidAndValue1.get("keyValue");

      String uid2 = uidAndValue2.get("uniqueID");
      String keyValue2 = uidAndValue2.get("keyValue");

      Assert.assertEquals("Expected key1 value of length " + keyLength + " but was " + keyValue1.length(), keyLength,
         keyValue1.length());
      Assert.assertEquals("Expected key2 value of length " + keyLength + " but was " + keyValue2.length(), keyLength,
         keyValue2.length());

      Assert.assertEquals("The uids are not sequential", Integer.parseInt(uid1) + 1, Integer.parseInt(uid2));
   }

   @Test
   public void testKeyLifeCycle() {

      // Create
      String keyName = "Key 1";
      String expirationDate = getFutureDate(7);
      List<KeyScopeContainer> scopes = setKeyScopes(new int[] {0, 1});
      ApiKey keyToCreate = new ApiKey(keyName, scopes, currentDateString, expirationDate, "");

      Map<String, String> uidAndValue = apiKeyEndpoint.createApiKey(keyToCreate).readEntity(uidAndValueMap);

      String uid = uidAndValue.get("uniqueID");
      String keyValue = uidAndValue.get("keyValue");

      // Get
      List<ApiKey> apiKeys = apiKeyEndpoint.getApiKeys().readEntity(apiKeyList);

      apiKeys = apiKeys.stream().filter(apiKey -> uid.equals(apiKey.getUniqueID())).collect(Collectors.toList());
      Assert.assertTrue("Was expecting one Api Key with UID:" + uid + " but found " + apiKeys.size(),
         apiKeys.size() == 1);

      ApiKey retrievedApiKey = apiKeys.get(0);

      // Check Name
      Assert.assertEquals(
         "Retrieved Api Key was expected to have name: " + keyName + " but instead had: " + retrievedApiKey.getName(),
         keyName, retrievedApiKey.getName());

      // Check Dates
      Assert.assertEquals(
         "Retrieved Api Key was expected to have creation date: " + currentDateString + " but instead had: " + retrievedApiKey.getCreationDate(),
         currentDateString, retrievedApiKey.getCreationDate());
      Assert.assertEquals(
         "Retrieved Api Key was expected to have expiration date: " + expirationDate + " but instead had: " + retrievedApiKey.getExpirationDate(),
         expirationDate, retrievedApiKey.getExpirationDate());

      // Check Scopes
      Assert.assertEquals("Expected scopes: " + scopes + " but found: " + retrievedApiKey.getScopes(), scopes,
         retrievedApiKey.getScopes());

      // Check Value
      Assert.assertEquals("Expected API Key value of length " + keyLength + " but was " + keyValue.length(), keyLength,
         keyValue.length());

      // Check Use
      Response branchesResponse = getRequestWithAuthorization("/orcs/branches", keyValue);
      assertEquals("Call with API Key was not successful with status: " + branchesResponse.getStatus(),
         Family.SUCCESSFUL, branchesResponse.getStatusInfo().getFamily());

      // Revoke
      Response response = apiKeyEndpoint.revokeApiKey(Long.parseLong(uid));
      assertEquals(Family.SUCCESSFUL, response.getStatusInfo().getFamily());

      // Check ApiKey is gone
      apiKeys = apiKeyEndpoint.getApiKeys().readEntity(apiKeyList);

      Assert.assertFalse("Api key with UID:" + uid + " still present after being revoked.",
         apiKeys.stream().anyMatch(apiKey -> uid.equals(apiKey.getUniqueID())));
   }

   @Test
   public void testKeyExpiration() {
      // Create Expired Key
      String expirationDate = getFutureDate(-3);
      List<KeyScopeContainer> scopes = setKeyScopes(new int[] {0, 1});
      ApiKey keyToCreate = new ApiKey("Key 1", scopes, currentDateString, expirationDate, "");

      Map<String, String> uidAndValue = apiKeyEndpoint.createApiKey(keyToCreate).readEntity(uidAndValueMap);
      String keyValue = uidAndValue.get("keyValue");

      // Check Use
      Response branchesResponse = getRequestWithAuthorization("/orcs/branches", keyValue);
      assertNotEquals(
         "Call with with expired API Key was unexpectedly successful with status: " + branchesResponse.getStatus(),
         Family.SUCCESSFUL, branchesResponse.getStatusInfo().getFamily());

      // Create Key With Same Day Expiration
      scopes = setKeyScopes(new int[] {2});
      keyToCreate = new ApiKey("Key 1", scopes, currentDateString, currentDateString, "");

      uidAndValue = apiKeyEndpoint.createApiKey(keyToCreate).readEntity(uidAndValueMap);
      keyValue = uidAndValue.get("keyValue");

      // Check Use
      branchesResponse = getRequestWithAuthorization("/orcs/branches", keyValue);
      assertEquals(
         "API Key call with same day expiration unexpectedly failed with status: " + branchesResponse.getStatus(),
         Family.SUCCESSFUL, branchesResponse.getStatusInfo().getFamily());
   }

   @Test
   public void testInvalidKey() {
      // Check Use
      Response branchesResponse = getRequestWithAuthorization("/orcs/branches", "invalid");
      assertNotEquals(
         "Call with with expired API Key was unexpectedly successful with status: " + branchesResponse.getStatus(),
         Family.SUCCESSFUL, branchesResponse.getStatusInfo().getFamily());
   }

   @Test
   public void testKeyScopes() { // Make request to acceptable scope and to unacceptable scope

   }

   @Test
   public void testDefaultDemoKey() { // Check to make sure that the Joe Smith User has a default demo API Key

   }

   private List<KeyScopeContainer> setKeyScopes(int[] scopeIdsToSelect) {

      List<KeyScopeContainer> keyScopes = apiKeyEndpoint.getKeyScopes().readEntity(keyScopeContainerList);

      for (int id : scopeIdsToSelect) {
         keyScopes.get(id).setSelected(true);
      }

      return keyScopes.stream().filter(KeyScopeContainer::isSelected).collect(Collectors.toList());
   }

   private Response getRequestWithAuthorization(String urlPath, String authorization) {
      BufferedReader in = null;
      HttpURLConnection connection = null;

      try {
         // Create the URL object
         URL url = new URL(System.getProperty("osee.application.server") + urlPath);

         // Open a connection to the URL
         connection = (HttpURLConnection) url.openConnection();

         // Set the request method to GET
         connection.setRequestMethod("GET");

         // Set the Authorization header
         String basicAuth = "Basic " + authorization;
         connection.setRequestProperty("Authorization", basicAuth);

         // Get the response code
         int responseCode = connection.getResponseCode();
         System.out.println("Response Code: " + responseCode);

         // Read the response
         in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
         StringBuilder response = new StringBuilder();
         String inputLine;
         while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
         }
         in.close();

         // Construct and return a JAX-RS Response object
         return Response.status(responseCode).entity(response.toString()).build();
      } catch (Exception e) {
         e.printStackTrace();
         // Construct a JAX-RS Response object for errors
         return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
      } finally {

         try {
            if (in != null) {
               in.close();
            }
            if (connection != null) {
               connection.disconnect();
            }
         } catch (IOException e) {
            e.printStackTrace();
         }
      }
   }

   private String getFutureDate(int monthDelta) {
      return currentDate.plusMonths(monthDelta).format(dateFormatter);
   }
}