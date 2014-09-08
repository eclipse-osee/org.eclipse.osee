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

import static org.eclipse.osee.activity.ActivityConstants.ACTIVITY_LOGGER__EXECUTOR_ID;
import static org.eclipse.osee.activity.ActivityConstants.ACTIVITY_LOGGER__EXECUTOR_POOL_SIZE;
import static org.eclipse.osee.activity.ActivityConstants.ACTIVITY_LOGGER__STACKTRACE_LINE_COUNT;
import static org.eclipse.osee.activity.ActivityConstants.ACTIVITY_LOGGER__WRITE_RATE_IN_MILLIS;
import static org.eclipse.osee.activity.ActivityConstants.DEFAULT_ACTIVITY_LOGGER__EXECUTOR_POOL_SIZE;
import static org.eclipse.osee.activity.ActivityConstants.DEFAULT_ACTIVITY_LOGGER__STACKTRACE_LINE_COUNT;
import static org.eclipse.osee.activity.ActivityConstants.DEFAULT_ACTIVITY_LOGGER__WRITE_RATE_IN_MILLIS;
import static org.eclipse.osee.activity.internal.ActivityUtil.captureStackTrace;
import static org.eclipse.osee.activity.internal.ActivityUtil.get;
import static org.eclipse.osee.framework.database.IOseeDatabaseService.MAX_VARCHAR_LENGTH;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import org.eclipse.osee.activity.ActivityStorage;
import org.eclipse.osee.activity.api.Activity;
import org.eclipse.osee.activity.api.ActivityLog;
import org.eclipse.osee.activity.api.ActivityType;
import org.eclipse.osee.executor.admin.ExecutorAdmin;
import org.eclipse.osee.framework.jdk.core.type.DrainingIterator;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.logger.Log;

/**
 * @author Ryan D. Brooks
 */
public class ActivityLogImpl implements ActivityLog, Callable<Void> {

   public static enum LogEntry {
      ENTRY_ID,
      PARENT_ID,
      TYPE_ID,
      ACCOUNT_ID,
      SERVER_ID,
      CLIENT_ID,
      START_TIME,
      DURATION,
      STATUS,
      MESSAGE_ARGS;

      Long from(Object[] entry) {
         Object obj = entry[ordinal()];
         if (obj instanceof Long) {
            return (Long) entry[ordinal()];
         }
         throw new OseeArgumentException("LogEntryIndex.from may only be used with values of type Long");
      }
   };

   private final ConcurrentHashMap<Long, Object[]> newEntities = new ConcurrentHashMap<Long, Object[]>();
   private final ConcurrentHashMap<Long, Object[]> updatedEntities = new ConcurrentHashMap<Long, Object[]>();

   private Log logger;
   private ExecutorAdmin executorAdmin;
   private ActivityStorage storage;

   private final AtomicBoolean initialized = new AtomicBoolean(false);
   private ActivityMonitorImpl activityMonitor;
   private volatile long freshnessMillis;
   private volatile int exceptionLineCount;
   private volatile int executorPoolSize;
   private volatile long lastFlushTime;

   public void setLogger(Log logger) {
      this.logger = logger;
   }

   public void setActivityStorage(ActivityStorage storage) {
      this.storage = storage;
   }

   public void setExecutorAdmin(ExecutorAdmin executorAdmin) {
      this.executorAdmin = executorAdmin;
   }

   public void start(Map<String, Object> properties) throws Exception {
      activityMonitor = new ActivityMonitorImpl();
      update(properties);
   }

   public void stop() {
      flush(true);
      try {
         executorAdmin.shutdown(ACTIVITY_LOGGER__EXECUTOR_ID);
      } catch (Throwable th) {
         logger.error(th, "Error shutting down executor [%s]", ACTIVITY_LOGGER__EXECUTOR_ID);
      }
   }

   @Override
   public void queryEntry(Long entryId, ActivityDataHandler handler) {
      storage.selectEntry(entryId, handler);
   }

   public void update(Map<String, Object> properties) {
      //@formatter:off
      freshnessMillis = get(properties, ACTIVITY_LOGGER__WRITE_RATE_IN_MILLIS, DEFAULT_ACTIVITY_LOGGER__WRITE_RATE_IN_MILLIS);
      exceptionLineCount = get(properties, ACTIVITY_LOGGER__STACKTRACE_LINE_COUNT, DEFAULT_ACTIVITY_LOGGER__STACKTRACE_LINE_COUNT);
      int newExecutorPoolSize = get(properties, ACTIVITY_LOGGER__EXECUTOR_POOL_SIZE, DEFAULT_ACTIVITY_LOGGER__EXECUTOR_POOL_SIZE);
      //@formatter:on

      if (newExecutorPoolSize != executorPoolSize) {
         executorPoolSize = newExecutorPoolSize;
         try {
            executorAdmin.shutdown(ACTIVITY_LOGGER__EXECUTOR_ID);
         } catch (Throwable th) {
            logger.error(th, "Error shutting down executor [%s]", ACTIVITY_LOGGER__EXECUTOR_ID);
         } finally {
            try {
               executorAdmin.createFixedPoolExecutor(ACTIVITY_LOGGER__EXECUTOR_ID, executorPoolSize);
            } catch (Throwable th) {
               logger.error(th, "Error creating new executor for [%s]", ACTIVITY_LOGGER__EXECUTOR_ID);
            }
         }
      }
   }

   @Override
   public Long createEntry(ActivityType type, String... messageArgs) {
      return createEntry(type.getTypeId(), COMPLETE_STATUS, messageArgs);
   }

   @Override
   public Long createUpdateableEntry(ActivityType type, String... messageArgs) {
      return createEntry(type.getTypeId(), INITIAL_STATUS, messageArgs);
   }

   @Override
   public Long createEntry(ActivityType type, Long parentId, Integer status, String... messageArgs) {
      return createEntry(type.getTypeId(), parentId, status, messageArgs);
   }

   @Override
   public Long createEntry(Long typeId, Integer status, String... messageArgs) {
      Object[] threadRootEntry = activityMonitor.getThreadRootEntry();
      Long entryId = LogEntry.ENTRY_ID.from(threadRootEntry);
      return createEntry(typeId, entryId, status, messageArgs);
   }

   @Override
   public Long createEntry(Long typeId, Long parentId, Integer status, String... messageArgs) {
      Object[] rootEntry = activityMonitor.getThreadRootEntry();
      Long accountId = LogEntry.ACCOUNT_ID.from(rootEntry);
      Long serverId = LogEntry.SERVER_ID.from(rootEntry);
      Long clientId = LogEntry.CLIENT_ID.from(rootEntry);
      Object[] entry =
         createEntry(parentId, typeId, accountId, serverId, clientId, computeDuration(), status, messageArgs);
      return LogEntry.ENTRY_ID.from(entry);
   }

   private Object[] createEntry(Long parentId, Long typeId, Long accountId, Long serverId, Long clientId, Long duration, Integer status, String... messageArgs) {
      Long entryId = Lib.generateUuid();
      Long startTime = System.currentTimeMillis();
      String fullMsg = Collections.toString("\n", (Object[]) messageArgs);

      String msg = fullMsg.substring(0, Math.min(fullMsg.length(), MAX_VARCHAR_LENGTH));
      // this is the parent entry so it must be inserted first (because the entry writing is asynchronous
      Object[] entry =
         new Object[] {entryId, parentId, typeId, accountId, serverId, clientId, startTime, duration, status, msg};
      newEntities.put(entryId, entry);

      if (fullMsg.length() > MAX_VARCHAR_LENGTH) {
         Long parentCursor = entryId;
         for (int i = MAX_VARCHAR_LENGTH; i < fullMsg.length(); i += MAX_VARCHAR_LENGTH) {
            Long continueEntryId = Lib.generateUuid();
            Object[] continueEntry =
               new Object[] {
                  continueEntryId,
                  parentCursor,
                  Activity.MSG_CONTINUATION.getTypeId(),
                  accountId,
                  serverId,
                  clientId,
                  startTime,
                  duration,
                  status,
                  fullMsg.substring(i, Math.min(fullMsg.length(), i + MAX_VARCHAR_LENGTH))};
            newEntities.put(continueEntryId, continueEntry);
            parentCursor = continueEntryId;
         }
      }
      flush(false);
      return entry;
   }

   @Override
   public Long createThrowableEntry(ActivityType type, Throwable throwable) {
      Long entryId = -1L;
      try {
         String message = captureStackTrace(throwable, exceptionLineCount);
         entryId = createEntry(type.getTypeId(), ABNORMALLY_ENDED_STATUS, message);
      } catch (Throwable th) {
         logger.error(th, "logging failed in ActivityLogImpl.createThrowableEntry");
      }
      return entryId;
   }

   @Override
   public boolean updateEntry(Long entryId, Integer status) {
      boolean modified = false;
      if (!updateIfNew(entryId, status)) {
         Object[] data = updatedEntities.get(entryId);
         if (data == null) {
            addUpdatedEntryToMap(entryId, status);
         } else {
            data[LogEntry.STATUS.ordinal()] = status;
            if (!updatedEntities.containsKey(entryId)) {
               addUpdatedEntryToMap(entryId, status);
            }
         }
         modified = true;
      }
      return modified;
   }

   private void addUpdatedEntryToMap(Long entryId, Integer status) {
      updatedEntities.put(entryId, new Object[] {status, computeDuration(), entryId});
   }

   private Long computeDuration() {
      long timeOfUpdate = System.currentTimeMillis();
      Object[] rootEntry = activityMonitor.getThreadRootEntry();
      LogEntry.START_TIME.from(rootEntry);
      return timeOfUpdate - LogEntry.START_TIME.from(rootEntry);
   }

   /**
    * If the status has changed for an entry that has not yet been written to the datastore, update in memory and return
    * true if it has not yet been drained and written to the datastore
    */
   private boolean updateIfNew(Long entryId, Integer status) {
      Object[] data = newEntities.get(entryId);
      if (data == null) {
         return false;
      } else {
         data[LogEntry.STATUS.ordinal()] = status;
         data[LogEntry.DURATION.ordinal()] = computeDuration();
         return newEntities.containsKey(entryId);
      }
   }

   @Override
   public Void call() {
      if (!initialized.getAndSet(true)) {
         initialize();
      }
      if (!newEntities.isEmpty()) {
         try {
            storage.addEntries(new DrainingIterator<Object[]>(newEntities.values().iterator()));
         } catch (Throwable ex) {
            logger.error(ex, "Exception while storing updates to the activity log");
         }
      }
      if (!updatedEntities.isEmpty()) {
         try {
            storage.updateEntries(new DrainingIterator<Object[]>(updatedEntities.values().iterator()));
         } catch (Throwable ex) {
            logger.error(ex, "Exception while storing updates to the activity log");
         }
      }
      return null;
   }

   private void flush(boolean force) {
      long currentTime = System.currentTimeMillis();
      if (force || currentTime - lastFlushTime > freshnessMillis) {
         try {
            executorAdmin.schedule(ACTIVITY_LOGGER__EXECUTOR_ID, this);
         } catch (Exception ex) {
            logger.error(ex, "Error scheduling activity log callable");
         } finally {
            lastFlushTime = currentTime;
         }
      }
   }

   private void initialize() {
      final Map<Long, ActivityType> types = new HashMap<Long, ActivityType>(4);
      for (Activity type : Activity.values()) {
         types.put(type.getTypeId(), type);
      }
      storage.selectTypes(new ActivityTypeDataHandler() {

         @Override
         public void onData(Long typeId, Long logLevel, String module, String messageFormat) {
            types.remove(typeId);
         }
      });
      if (!types.isEmpty()) {
         storage.addActivityTypes(types.values());
      }
   }

   @Override
   public void completeEntry(Long entryId) {
      updateEntry(entryId, COMPLETE_STATUS);
   }

   @Override
   public void endEntryAbnormally(Long entryId) {
      updateEntry(entryId, ABNORMALLY_ENDED_STATUS);
   }

   @Override
   public void endEntryAbnormally(Long entryId, Integer status) {
      if (status > COMPLETE_STATUS) {
         updateEntry(entryId, status);
      } else {
         endEntryAbnormally(entryId);
      }
   }

   @Override
   public Long createActivityThread(ActivityType type, Long accountId, Long serverId, Long clientId, String... messageArgs) {
      return createActivityThread(-1L, type, accountId, serverId, clientId, messageArgs);
   }

   @Override
   public Long createActivityThread(Long parentId, ActivityType type, Long accountId, Long serverId, Long clientId, String... messageArgs) {
      Object[] entry = createEntry(parentId, type.getTypeId(), accountId, serverId, clientId, 0L, 0, messageArgs);
      activityMonitor.addActivityThread(entry);
      return LogEntry.ENTRY_ID.from(entry);
   }

   @Override
   public void createActivityTypes(ActivityType... types) {
      storage.addActivityTypes(types);
   }

   @Override
   public void queryActivityType(Long typeId, ActivityTypeDataHandler handler) {
      storage.selectType(typeId, handler);
   }

   @Override
   public boolean activityTypeExists(Long typeId) {
      return storage.typeExists(typeId);
   }

   @Override
   public void queryActivityTypes(ActivityTypeDataHandler handler) {
      storage.selectTypes(handler);
   }
}