/*********************************************************************
 * Copyright (c) 2023 Boeing
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
package org.eclipse.osee.orcs.rest.internal.health.operations;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import org.eclipse.osee.activity.api.ActivityEntry;
import org.eclipse.osee.framework.core.data.CoreActivityTypes;
import org.eclipse.osee.framework.core.data.UserToken;
import org.eclipse.osee.framework.jdk.core.util.DateUtil;
import org.eclipse.osee.jdbc.JdbcClient;
import org.eclipse.osee.jdbc.JdbcStatement;
import org.eclipse.osee.orcs.OrcsApi;

/**
 * Health usage over the past 1 month from current time
 *
 * @author Jaden W. Puckett
 */
public class HealthUsage {
   private final JdbcClient jdbcClient;
   private final OrcsApi orcsApi;

   private final List<User> allUsers = new ArrayList<>();
   private final List<Session> allSessions = new ArrayList<>();
   private final Map<String, List<User>> versionTypeMap = new HashMap<>();
   private final Map<String, List<User>> versionNameMap = new HashMap<>();
   private String errorMsg = "";

   public HealthUsage(OrcsApi orcsApi, JdbcClient jdbcClient) {
      this.orcsApi = orcsApi;
      this.jdbcClient = jdbcClient;
   }

   public void calculateUsage() {
      Collection<ActivityEntry> entries = getUsageLogEntries();
      for (ActivityEntry entry : entries) {
         UserToken userTok = orcsApi.userService().getUser(entry.getAccountId());
         User user = new User(userTok.getName(), userTok.getEmail(), userTok.getUserId(), entry.getAccountId());
         // Collect unique users
         if (!allUsers.stream().anyMatch(u -> u.getUserId() == user.getUserId())) {
            allUsers.add(user);
         }
         // Collect sessions and populate maps based on version type and version name
         try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode =
               objectMapper.readTree(entry.getMessageArgs().substring(entry.getMessageArgs().indexOf("{")));
            // Parse json string
            String version = jsonNode.get("version").asText();
            String clientAddress = jsonNode.get("clientAddress").asText();
            String clientMachineName = jsonNode.get("clientMachineName").asText();
            String port = jsonNode.get("port").asText();
            String sessionId = jsonNode.get("sessionId").asText();
            // Add session
            Session session = new Session(user, entry.getStartTimestamp().toString(), version, sessionId, clientAddress,
               clientMachineName, port);
            allSessions.add(session);
            // Add unique users to version name map
            if (versionNameMap.containsKey(version)) {
               List<User> users = versionNameMap.get(version);
               if (!users.stream().anyMatch(u -> u.getUserId() == user.getUserId())) {
                  users.add(user);
               }
            } else {
               List<User> users = new ArrayList<>();
               users.add(user);
               versionNameMap.put(version, users);
            }
            // Add unique users to version type map
            if (version.contains("-")) {
               version = version.substring(version.indexOf("-"));
            }
            if (versionTypeMap.containsKey(version)) {
               List<User> users = versionTypeMap.get(version);
               if (!users.stream().anyMatch(u -> u.getUserId() == user.getUserId())) {
                  users.add(user);
               }
            } else {
               List<User> users = new ArrayList<>();
               users.add(user);
               versionTypeMap.put(version, users);
            }
         } catch (Exception e) {
            errorMsg += "\n" + e.getMessage();
         }

      }
   }

   private Date getDatePreviousMonth() {
      Calendar calendar = Calendar.getInstance();
      calendar.add(Calendar.MONTH, -1);
      return calendar.getTime();
   }

   private Collection<ActivityEntry> getUsageLogEntries() {
      Date date = getDatePreviousMonth();
      String query =
         "select * from osee_activity where type_id = " + CoreActivityTypes.IDE.getIdString() + " and trunc(start_timestamp) > '" + DateUtil.get(
            date, DateUtil.DD_MMM_YYYY) + "' and msg_args like '%Session Created%' order by start_timestamp desc";

      List<ActivityEntry> logMsgs = new ArrayList<>();

      Consumer<JdbcStatement> consumer = stmt -> {
         ActivityEntry entry = new ActivityEntry(stmt.getLong(1));
         entry.setAccountId(stmt.getLong("account_id"));
         entry.setClientId(stmt.getLong("client_id"));
         entry.setDuration(stmt.getLong("duration"));
         entry.setMessageArgs(stmt.getString("msg_args"));
         entry.setParentId(stmt.getLong("parent_id"));
         entry.setServerId(stmt.getLong("server_id"));
         entry.setStartTime(stmt.getLong("start_time"));
         entry.setStartTimestamp(stmt.getTimestamp("start_timestamp"));
         entry.setStatus(stmt.getInt("status"));
         entry.setTypeId(stmt.getLong("type_id"));
         logMsgs.add(entry);
      };

      jdbcClient.runQuery(consumer, query);
      return logMsgs;
   }

   public List<User> getAllUsers() {
      return allUsers;
   }

   public List<Session> getAllSessions() {
      return allSessions;
   }

   public Map<String, List<User>> getVersionTypeMap() {
      return versionTypeMap;
   }

   public Map<String, List<User>> getVersionNameMap() {
      return versionNameMap;
   }

   public String getErrorMsg() {
      return errorMsg;
   }

   public class User {
      private final String name;
      private final String email;
      private final String userId;
      private final Long accountId;

      public User(String name, String email, String userId, Long accountId) {
         this.name = name;
         this.email = email;
         this.userId = userId;
         this.accountId = accountId;
      }

      public String getName() {
         return name;
      }

      public String getEmail() {
         return email;
      }

      public String getUserId() {
         return userId;
      }

      public Long getAccountId() {
         return accountId;
      }
   }

   public class Session {
      private final User user;
      private final String date;
      private final String version;
      private final String sessionId;
      private final String clientAddress;
      private final String clientMachineName;
      private final String port;

      public Session(User user, String date, String version, String sessionId, String clientAddress, String clientMachineName, String port) {
         this.user = user;
         this.date = date;
         this.version = version;
         this.sessionId = sessionId;
         this.port = port;
         this.clientAddress = clientAddress;
         this.clientMachineName = clientMachineName;
      }

      public User getUser() {
         return user;
      }

      public String getDate() {
         return date;
      }

      public String getVersion() {
         return version;
      }

      public String getSessionId() {
         return sessionId;
      }

      public String getClientAddress() {
         return clientAddress;
      }

      public String getClientMachineName() {
         return clientMachineName;
      }

      public String getPort() {
         return port;
      }

   }

}
