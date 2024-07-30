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
package org.eclipse.osee.orcs.rest.internal;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import org.eclipse.osee.framework.core.ApiKeyApi;
import org.eclipse.osee.framework.core.data.ApiKey;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.KeyScopeContainer;
import org.eclipse.osee.framework.core.data.UserId;
import org.eclipse.osee.framework.core.enums.KeyScope;
import org.eclipse.osee.jdbc.JdbcService;

public class ApiKeyApiImpl implements ApiKeyApi {

   private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
   private JdbcService jdbcService;
   public void start() {
      // Start Service
   }

   public void setJdbcService(JdbcService jdbcService) {
      this.jdbcService = jdbcService;
   }

   @Override
   public List<ApiKey> getApiKeys(ArtifactId userArtId) {
      String query = "select * from osee_api_key where user_art_id = ?";

      List<ApiKey> apiKeys = new ArrayList<>();

      // @formatter:off
      jdbcService.getClient().runQuery(
         stmt -> apiKeys.add(
            new ApiKey(
                stmt.getString("key_name"),
                Arrays.stream(stmt.getString("scopes").split(","))
                    .map(scopeID -> new KeyScopeContainer(Long.parseLong(scopeID)))
                    .collect(Collectors.toList()),
                stmt.getString("creation_date").replaceAll(" \\d{2}:\\d{2}:\\d{2}$", ""),
                stmt.getString("expiration_date").replaceAll(" \\d{2}:\\d{2}:\\d{2}$", ""),
                stmt.getString("key_uid")
            )
        ),
        query,
        userArtId
       );
      // @formatter:on

      return apiKeys;
   }

   @Override
   public ApiKey getApiKey(String apiKeyString) {
      String query = "SELECT * FROM osee_api_key WHERE API_KEY_VALUE = ? FETCH FIRST 1 ROWS ONLY";

      apiKeyString = hashApiKey(apiKeyString);

      AtomicReference<ApiKey> apiKeyRef = new AtomicReference<>();

      // @formatter:off
      jdbcService.getClient().runQuery(
          stmt -> {
              ApiKey apiKey = new ApiKey(
                  stmt.getString("key_name"),
                  Arrays.stream(stmt.getString("scopes").split(","))
                      .map(scopeID -> new KeyScopeContainer(Long.parseLong(scopeID)))
                      .collect(Collectors.toList()),
                  stmt.getString("creation_date").replaceAll(" \\d{2}:\\d{2}:\\d{2}$", ""),
                  stmt.getString("expiration_date").replaceAll(" \\d{2}:\\d{2}:\\d{2}$", ""),
                  stmt.getString("key_uid"),
                  UserId.valueOf(stmt.getLong("user_art_id"))
              );
              apiKeyRef.set(apiKey);
          },
          query,
          apiKeyString
      );
      // @formatter:on

      return apiKeyRef.get();
   }

   @Override
   public List<KeyScopeContainer> getKeyScopes() {
      List<KeyScopeContainer> keyScopes = scopesToContainer(KeyScope.values(), false);

      return keyScopes;
   }

   @Override
   public Map<String, String> createApiKey(ApiKey apiKey, ArtifactId userArtId) {

      final String apiKeyValue = generateApiKey();

      apiKey.setHashedValue(hashApiKey(apiKeyValue));

      Map<String, String> uidAndValue = new HashMap<>();
      uidAndValue.put("uniqueID", saveApiKey(apiKey, userArtId));
      uidAndValue.put("keyValue", apiKeyValue);

      return uidAndValue;
   }

   // REPLACE WITH BELOW WHEN ORACLE DRIVER HAS BEEN UPDATED
   private String saveApiKey(ApiKey apiKey, ArtifactId userArtId) {

      String insertSql =
         "insert into osee_api_key(key_name,api_key_value,scopes,creation_date,expiration_date,user_art_id) " + //
            " values (?,?,?,?,?,?)";
      String selectSql = "SELECT KEY_UID FROM osee_api_key WHERE API_KEY_VALUE = ? FETCH FIRST 1 ROWS ONLY";

      // @formatter:off
      jdbcService.getClient().runPreparedUpdate(
         insertSql,
         apiKey.getName(),
         apiKey.getHashedValue(),
         String.join(
             ",",
             apiKey.getScopes().stream()
                 .map(scopeContainer -> scopeContainer.getId().toString())
                 .collect(Collectors.toList())
         ),
         dateStringToTimestamp(apiKey.getCreationDate()),
         dateStringToTimestamp(apiKey.getExpirationDate()),
         userArtId
     );
     // @formatter:on
      AtomicReference<String> uniqueIdRef = new AtomicReference<>();

     // @formatter:off
     jdbcService.getClient().runQuery(
         stmt -> {
             uniqueIdRef.set(stmt.getString("key_uid"));
         },
         selectSql,
         apiKey.getHashedValue()
     );
     // @formatter:on

      return uniqueIdRef.get();
   }

   //   (TEST) USE WHEN ORACLE DRIVER HAS BEEN UPDATED TO A VERSION GREATER THAN 12.1.0. getGeneratedKeys is more efficient than follow up select.

   //   private String saveApiKey(ApiKey apiKey, ArtifactId userArtId) {
   //
   //      String insertSql =
   //         "insert into osee_api_key(key_name,api_key_value,scopes,creation_date,expiration_date,user_art_id) " + //
   //            " values (?,?,?,?,?,?)";
   //
//      // @formatter:off
//      ResultSet result = jdbcService.getClient().runPreparedUpdateReturnAuto(
//         insertSql,
//         apiKey.getName(),
//         apiKey.getHashedValue(),
//         String.join(
//             ",",
//             apiKey.getScopes().stream()
//                 .map(scopeContainer -> scopeContainer.getId().toString())
//                 .collect(Collectors.toList())
//         ),
//         dateStringToTimestamp(apiKey.getCreationDate()),
//         dateStringToTimestamp(apiKey.getExpirationDate()),
//         userArtId
//     );
//     // @formatter:on
   //      String id = "";
   //      try {
   //         if (result.next()) {
   //            id = result.getObject(1).toString();
   //         }
   //      } catch (SQLException ex) {
   //         throw new RuntimeException(ex);
   //      }
   //
   //      return id;
   //   }

   @Override
   public boolean revokeApiKey(long keyUID) {

      String deleteSql = "delete from osee_api_key where KEY_UID = ?";

      int rowsDeleted = jdbcService.getClient().runPreparedUpdate(deleteSql, keyUID);

      if (rowsDeleted > 0) {
         return true;
      }

      return false;
   }

   @Override
   // This method can probably be removed now that api key has isExpired method
   public boolean checkKeyExpiration(ApiKey apiKey) {
      LocalDate currentDate = LocalDate.now();
      LocalDate expirationDate = LocalDate.parse(apiKey.getExpirationDate(), dateFormatter);

      return (currentDate.isEqual(expirationDate) || currentDate.isBefore(expirationDate)) ? true : false;
   }

   private String generateApiKey() {
      final int length = 32;
      final String charset = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789@~";

      SecureRandom secureRandom = new SecureRandom();

      StringBuilder apiKeValue = new StringBuilder();
      for (int i = 0; i < length; i++) {
         int randomIndex = secureRandom.nextInt(charset.length());
         apiKeValue.append(charset.charAt(randomIndex));
      }

      return apiKeValue.toString();
   }

   private String hashApiKey(String apiKeyValue) {
      try {
         // Create a MessageDigest instance for SHA-256.
         MessageDigest digest = MessageDigest.getInstance("SHA-256");

         // Perform the hash computation.
         byte[] encodedhash = digest.digest(apiKeyValue.getBytes("UTF-8"));

         // Convert the byte array to a hexadecimal string.
         StringBuilder hexString = new StringBuilder(2 * encodedhash.length);
         for (byte b : encodedhash) {
            // Bitwise AND with 0xff ensures unsigned
            String hex = Integer.toHexString(0xff & b);
            // If value is between 0 and 15 (only one char) prepend 0
            // to endure every byte is represented by two hexadecimal characters.
            if (hex.length() == 1) {
               hexString.append('0');
            }
            hexString.append(hex);
         }

         return hexString.toString();
      } catch (NoSuchAlgorithmException | java.io.UnsupportedEncodingException ex) {
         throw new RuntimeException(ex);
      }
   }

   private List<KeyScopeContainer> scopeIdsToContainer(Long[] scopeIds, boolean selected) {
      // @formatter:off
      return Arrays.stream(scopeIds)
                   .map(KeyScope::fromId)
                   .map(scope -> new KeyScopeContainer(scope.getId(), scope.getName(), selected))
                   .collect(Collectors.toList());
      // @formatter:on
   }

   private List<KeyScopeContainer> scopesToContainer(KeyScope[] scopes, boolean selected) {
      // @formatter:off
      return Arrays.stream(scopes)
                   .map(scope -> new KeyScopeContainer(scope.getId(), scope.getName(), selected))
                   .collect(Collectors.toList());
      // @formatter:on
   }

   private Timestamp dateStringToTimestamp(String dateString) {
      LocalDateTime localDateTime = LocalDate.parse(dateString).atStartOfDay();
      return Timestamp.valueOf(localDateTime);
   }

}
