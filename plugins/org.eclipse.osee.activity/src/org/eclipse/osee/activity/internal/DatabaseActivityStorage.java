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

import org.eclipse.osee.activity.ActivityStorage;
import org.eclipse.osee.activity.api.ActivityLog.ActivityDataHandler;
import org.eclipse.osee.activity.api.ActivityLog.ActivityTypeDataHandler;
import org.eclipse.osee.activity.api.ActivityType;
import org.eclipse.osee.framework.database.IOseeDatabaseService;
import org.eclipse.osee.framework.database.IQueryProcessor;
import org.eclipse.osee.framework.database.core.IOseeStatement;

/**
 * @author Ryan D. Brooks
 */
public class DatabaseActivityStorage implements ActivityStorage {

   private static final String SELECT_ENTRY = "SELECT * FROM osee_activity WHERE entry_id = ?";

   private static final String INSERT_ENTRIES =
      "INSERT INTO osee_activity (entry_id, parent_id, type_id, account_id, server_id, client_id, start_time, duration, status, msg_args) VALUES (?,?,?,?,?,?,?,?,?,?)";

   private static final String UPDATE_ENTRIES = "UPDATE osee_activity set status = ?, duration = ? where entry_id = ?";

   private static final String SELECT_ALL_TYPES = "SELECT * FROM osee_activity_type";

   private static final String SELECT_TYPE = "SELECT * FROM osee_activity_type WHERE type_id = ?";

   private static final String INSERT_TYPE =
      "INSERT INTO osee_activity_type (type_id, log_level, module, msg_format) VALUES (?,?,?,?)";

   private static final String COUNT_TYPE = "SELECT count(1) FROM osee_activity_type WHERE type_id = ?";

   private static class ActivityEntryProcessor implements IQueryProcessor {

      private final ActivityDataHandler handler;

      public ActivityEntryProcessor(ActivityDataHandler handler) {
         super();
         this.handler = handler;
      }

      @Override
      public void processNext(IOseeStatement chStmt) {
         Long entryId = chStmt.getLong("entry_id");
         Long parentId = chStmt.getLong("parent_id");
         Long typeId = chStmt.getLong("type_id");
         Long accountId = chStmt.getLong("account_id");
         Long serverId = chStmt.getLong("server_id");
         Long clientId = chStmt.getLong("client_id");
         Long startTime = chStmt.getLong("start_time");
         Long duration = chStmt.getLong("duration");
         Integer status = chStmt.getInt("status");
         String messageArgs = chStmt.getString("msg_args");
         handler.onData(entryId, parentId, typeId, accountId, serverId, clientId, startTime, duration, status,
            messageArgs);
      }
   }

   private static class ActivityTypeProcessor implements IQueryProcessor {

      private final ActivityTypeDataHandler handler;

      public ActivityTypeProcessor(ActivityTypeDataHandler handler) {
         super();
         this.handler = handler;
      }

      @Override
      public void processNext(IOseeStatement chStmt) {
         Long typeId = chStmt.getLong("type_id");
         Long logLevel = chStmt.getLong("log_level");
         String module = chStmt.getString("module");
         String messageFormat = chStmt.getString("msg_format");
         handler.onData(typeId, logLevel, module, messageFormat);
      }
   }

   private IOseeDatabaseService dbService;

   public void setDatabaseService(IOseeDatabaseService dbService) {
      this.dbService = dbService;
   }

   @Override
   public void selectEntry(Long entryId, final ActivityDataHandler handler) {
      dbService.runQuery(new ActivityEntryProcessor(handler), SELECT_ENTRY, entryId);
   }

   @Override
   public int addEntries(Iterable<Object[]> newEntries) {
      return dbService.runBatchUpdate(INSERT_ENTRIES, newEntries);
   }

   @Override
   public int updateEntries(Iterable<Object[]> updatedEntries) {
      return dbService.runBatchUpdate(UPDATE_ENTRIES, updatedEntries);
   }

   @SuppressWarnings("unchecked")
   private void addLogType(ActivityType type) {
      Long typeId = type.getTypeId();
      Long logLevel = type.getLogLevel();
      String module = type.getModule();
      String messageFormat = type.getMessageFormat();
      dbService.runPreparedUpdate(INSERT_TYPE, typeId, logLevel, module, messageFormat);
   }

   @Override
   public void addActivityTypes(ActivityType... types) {
      for (ActivityType type : types) {
         addLogType(type);
      }
   }

   @Override
   public void selectTypes(final ActivityTypeDataHandler handler) {
      dbService.runQuery(new ActivityTypeProcessor(handler), SELECT_ALL_TYPES);
   }

   @Override
   public void selectType(Long typeId, final ActivityTypeDataHandler handler) {
      dbService.runQuery(new ActivityTypeProcessor(handler), SELECT_TYPE, typeId);
   }

   @Override
   public boolean typeExists(Long typeId) {
      return dbService.runPreparedQueryFetchObject(-1L, COUNT_TYPE, typeId) > 0;
   }

}