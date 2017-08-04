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
package org.eclipse.osee.activity.internal;

import java.util.Calendar;
import java.util.function.Consumer;
import java.util.logging.Level;
import org.eclipse.osee.activity.ActivityStorage;
import org.eclipse.osee.activity.api.ActivityEntry;
import org.eclipse.osee.activity.api.ActivityEntryId;
import org.eclipse.osee.framework.core.data.ActivityTypeId;
import org.eclipse.osee.framework.core.data.ActivityTypeToken;
import org.eclipse.osee.framework.core.exception.OseeNotFoundException;
import org.eclipse.osee.framework.jdk.core.type.MutableBoolean;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.jdbc.JdbcClient;
import org.eclipse.osee.jdbc.JdbcService;
import org.eclipse.osee.jdbc.JdbcStatement;

/**
 * @author Ryan D. Brooks
 */
public class DatabaseActivityStorage implements ActivityStorage {

   private static final String SELECT_ENTRY = "SELECT * FROM osee_activity WHERE entry_id = ?";

   private static final String INSERT_ENTRIES =
      "INSERT INTO osee_activity (entry_id, parent_id, type_id, account_id, server_id, client_id, start_time, duration, status, msg_args) VALUES (?,?,?,?,?,?,?,?,?,?)";

   private static final String UPDATE_ENTRIES = "UPDATE osee_activity set status = ?, duration = ? where entry_id = ?";

   private static final String SELECT_TYPE = "SELECT * FROM osee_activity_type WHERE type_id = ?";

   private static final String INSERT_TYPE =
      "INSERT INTO osee_activity_type (type_id, log_level, module, msg_format) SELECT (?,?,?,?) where NOT EXISTS (SELECT 1 from osee_activity_type where type_id = ?)";

   private static final String DELETE_ENTRIES = "DELETE FROM osee_activity WHERE start_time <= ?";

   private JdbcClient jdbcClient;

   public void setJdbcService(JdbcService jdbcService) {
      this.jdbcClient = jdbcService.getClient();
   }

   @Override
   public ActivityEntry getEntry(ActivityEntryId entryId) {
      final ActivityEntry entry = new ActivityEntry(entryId.getId());
      final MutableBoolean found = new MutableBoolean(false);

      Consumer<JdbcStatement> consumer = stmt -> {
         entry.setAccountId(stmt.getLong("account_id"));
         entry.setClientId(stmt.getLong("client_id"));
         entry.setDuration(stmt.getLong("duration"));
         entry.setMessageArgs(stmt.getString("msg_args"));
         entry.setParentId(stmt.getLong("parent_id"));
         entry.setServerId(stmt.getLong("server_id"));
         entry.setStartTime(stmt.getLong("start_time"));
         entry.setStatus(stmt.getInt("status"));
         entry.setTypeId(stmt.getLong("type_id"));
         found.setValue(true);
      };

      jdbcClient.runQuery(consumer, SELECT_ENTRY, entryId);
      if (!found.getValue()) {
         throw new OseeNotFoundException("Activity Log entry [%s] not found", entryId.getIdString());
      }
      return entry;
   }

   @Override
   public int addEntries(Iterable<Object[]> newEntries) {
      return jdbcClient.runBatchUpdate(INSERT_ENTRIES, newEntries);
   }

   @Override
   public int updateEntries(Iterable<Object[]> updatedEntries) {
      return jdbcClient.runBatchUpdate(UPDATE_ENTRIES, updatedEntries);
   }

   @Override
   public ActivityTypeToken getActivityType(ActivityTypeId typeId) {
      ActivityTypeToken[] token = new ActivityTypeToken[1];
      Consumer<JdbcStatement> consumer = stmt -> {
         token[0] = ActivityTypeToken.valueOf(stmt.getLong("type_id"), Level.parse(stmt.getString("log_level")),
            stmt.getString("module"), stmt.getString("msg_format"));
      };

      jdbcClient.runQuery(consumer, SELECT_TYPE, typeId);
      if (token[0] == null) {
         throw new OseeNotFoundException("Activity type [%s] not found", typeId.getIdString());
      }
      return token[0];
   }

   @Override
   public void cleanEntries(int daysToKeep) {
      Calendar cal = Calendar.getInstance();
      if (daysToKeep > 0) {
         daysToKeep = -daysToKeep;
      }
      cal.add(Calendar.DATE, daysToKeep);
      jdbcClient.runPreparedUpdate(DELETE_ENTRIES, cal.getTimeInMillis());
   }

   @Override
   public ActivityTypeToken createIfAbsent(ActivityTypeToken type) {
      if (type.isInvalid()) {
         type = ActivityTypeToken.valueOf(Lib.generateUuid(), type.getLogLevel(), type.getModule(),
            type.getMessageFormat());
      }
      jdbcClient.runPreparedUpdate(INSERT_TYPE, type, type.getLogLevel(), type.getModule(), type.getMessageFormat());
      return type;
   }

   @Override
   public void createIfAbsent(Iterable<ActivityTypeToken> types) {
      for (ActivityTypeToken type : types) {
         createIfAbsent(type);
      }
   }
}