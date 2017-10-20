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

import static org.eclipse.osee.activity.ActivityConstants.ACTIVITY_LOGGER__CLEANER_EXECUTOR_ID;
import static org.eclipse.osee.activity.ActivityConstants.ACTIVITY_LOGGER__CLEANER_KEEP_DAYS;
import static org.eclipse.osee.activity.ActivityConstants.ACTIVITY_LOGGER__ENABLED;
import static org.eclipse.osee.activity.ActivityConstants.ACTIVITY_LOGGER__EXECUTOR_ID;
import static org.eclipse.osee.activity.ActivityConstants.ACTIVITY_LOGGER__EXECUTOR_POOL_SIZE;
import static org.eclipse.osee.activity.ActivityConstants.ACTIVITY_LOGGER__STACKTRACE_LINE_COUNT;
import static org.eclipse.osee.activity.ActivityConstants.ACTIVITY_LOGGER__WRITE_RATE_IN_MILLIS;
import static org.eclipse.osee.activity.ActivityConstants.DEFAULT_ACTIVITY_LOGGER__CLEANER_KEEP_DAYS;
import static org.eclipse.osee.activity.ActivityConstants.DEFAULT_ACTIVITY_LOGGER__EXECUTOR_POOL_SIZE;
import static org.eclipse.osee.activity.ActivityConstants.DEFAULT_ACTIVITY_LOGGER__STACKTRACE_LINE_COUNT;
import static org.eclipse.osee.activity.ActivityConstants.DEFAULT_ACTIVITY_LOGGER__WRITE_RATE_IN_MILLIS;
import static org.eclipse.osee.activity.ActivityConstants.DEFAULT_CLIENT_ID;
import static org.eclipse.osee.activity.api.Activity.THREAD_ACTIVITY;
import static org.eclipse.osee.activity.internal.ActivityUtil.captureStackTrace;
import static org.eclipse.osee.activity.internal.ActivityUtil.get;
import java.net.UnknownHostException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import org.eclipse.osee.activity.ActivityConstants;
import org.eclipse.osee.activity.ActivityStorage;
import org.eclipse.osee.activity.api.Activity;
import org.eclipse.osee.activity.api.ActivityLog;
import org.eclipse.osee.activity.api.ActivityType;
import org.eclipse.osee.executor.admin.ExecutorAdmin;
import org.eclipse.osee.framework.core.data.OrcsTypesData;
import org.eclipse.osee.framework.core.data.OseeClient;
import org.eclipse.osee.framework.core.enums.SystemUser;
import org.eclipse.osee.framework.jdk.core.type.DrainingIterator;
import org.eclipse.osee.framework.jdk.core.type.Id;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.Network;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.jdbc.JdbcConstants;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.SystemPreferences;

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

      public static final Long SENTINAL = -1L;

      Long from(Object[] entry) {
         Object obj = entry[ordinal()];
         if (obj instanceof Long) {
            return (Long) entry[ordinal()];
         }
         throw new OseeArgumentException("LogEntryIndex.from may only be used with values of type Long");
      }
   };

   private final static int HALF_HOUR = 30 * 60 * 1000;
   private static final int FIVE_MINUTES = 5 * 60 * 1000;

   private final ConcurrentHashMap<Long, Object[]> newEntities = new ConcurrentHashMap<>();
   private final ConcurrentHashMap<Long, Object[]> updatedEntities = new ConcurrentHashMap<>();
   private final ThreadActivity threadActivity = new ThreadActivity();
   private static final Object[] EMPTY_ARRAY = new Object[0];

   private Log logger;
   private ExecutorAdmin executorAdmin;
   private ActivityStorage storage;
   private SystemPreferences preferences;

   private final AtomicBoolean initialized = new AtomicBoolean(false);
   private ActivityMonitorImpl activityMonitor;
   private volatile long freshnessMillis;
   private volatile int exceptionLineCount;
   private volatile int executorPoolSize;
   private volatile long lastFlushTime;
   private volatile int cleanerKeepDays;
   private volatile boolean enabled = ActivityConstants.DEFAULT_ACTIVITY_LOGGER__ENABLED;

   public void setLogger(Log logger) {
      this.logger = logger;
   }

   public void setActivityStorage(ActivityStorage storage) {
      this.storage = storage;
   }

   public void setExecutorAdmin(ExecutorAdmin executorAdmin) {
      this.executorAdmin = executorAdmin;
   }

   public void setSystemPreferences(SystemPreferences preferences) {
      this.preferences = preferences;
   }

   public void start(Map<String, Object> properties) throws Exception {
      activityMonitor = new ActivityMonitorImpl();
      executorAdmin.schedule(this::continuouslyLogThreadActivity);
      update(properties);
   }

   private Void continuouslyLogThreadActivity() {
      String portString = System.getProperty(OseeClient.OSGI_HTTP_PORT);
      Long port;
      try {
         port = Long.valueOf(portString);
      } catch (Exception ex) {
         port = Id.SENTINEL;
      }
      String host = "";
      try {
         host = Network.getValidIP().getCanonicalHostName();
      } catch (UnknownHostException ex) {
         logger.warn(ex, "Error getting host for start of tread activity logging");
      }
      Long threadActivityParententryId = createActivityThread(THREAD_ACTIVITY, SystemUser.OseeSystem.getId(), port,
         DEFAULT_CLIENT_ID, "Start of thread activity logging thread on " + host);

      int sampleWindowMs;
      while (true) {
         String sampleWindowMsStr =
            preferences.getCachedValue("thread.activity.sample.window." + OrcsTypesData.OSEE_TYPE_VERSION, HALF_HOUR);
         if (Strings.isValid(sampleWindowMsStr)) {
            sampleWindowMs = Integer.parseInt(sampleWindowMsStr);
         } else {
            sampleWindowMs = FIVE_MINUTES;
         }
         String threadActivity = getThreadActivity(sampleWindowMs);
         if (!threadActivity.isEmpty()) {
            createEntry(THREAD_ACTIVITY, threadActivityParententryId, COMPLETE_STATUS, threadActivity);
         }
      }
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
      String value = (String)properties.get(ACTIVITY_LOGGER__ENABLED);
      int newCleanerKeepDays = get(properties, ACTIVITY_LOGGER__CLEANER_KEEP_DAYS, DEFAULT_ACTIVITY_LOGGER__CLEANER_KEEP_DAYS);
      if (Strings.isValid(value)) {
         enabled = Boolean.valueOf(value);
      }
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
      if (newCleanerKeepDays != cleanerKeepDays) {
         cleanerKeepDays = newCleanerKeepDays;
         setupCleaner();
      }
   }

   private void setupCleaner() {
      Callable<Void> cleaner = new Callable<Void>() {

         @Override
         public Void call() throws Exception {
            storage.cleanEntries(cleanerKeepDays);
            return null;
         }
      };

      // randomly pick a start time around midnight
      Random random = new Random();
      Calendar start = Calendar.getInstance();
      start.set(Calendar.HOUR_OF_DAY, random.nextInt(4));
      start.set(Calendar.MINUTE, random.nextInt(180));
      int day = start.get(Calendar.DAY_OF_YEAR);
      start.set(Calendar.DAY_OF_YEAR, day + 1);

      long startMil = start.getTimeInMillis();
      long curMil = System.currentTimeMillis();
      long startAfter = TimeUnit.MILLISECONDS.toMinutes(startMil - curMil);

      // run once a day
      executorAdmin.shutdown(ACTIVITY_LOGGER__CLEANER_EXECUTOR_ID);
      executorAdmin.scheduleAtFixedRate(ACTIVITY_LOGGER__CLEANER_EXECUTOR_ID, cleaner, startAfter, 60 * 24,
         TimeUnit.MINUTES);
   }

   @Override
   public Long createEntry(ActivityType type, Object... messageArgs) {
      return createEntry(type.getTypeId(), COMPLETE_STATUS, messageArgs);
   }

   @Override
   public Long createUpdateableEntry(ActivityType type, Object... messageArgs) {
      return createEntry(type.getTypeId(), INITIAL_STATUS, messageArgs);
   }

   @Override
   public Long createEntry(ActivityType type, Long parentId, Integer status, Object... messageArgs) {
      return createEntry(type.getTypeId(), parentId, status, messageArgs);
   }

   @Override
   public Long createEntry(Long typeId, Integer status, Object... messageArgs) {
      if (enabled) {
         Object[] threadRootEntry = activityMonitor.getThreadRootEntry();
         // Should never have a null rootEntry, but still want to log message with sentinals
         Long entryId = threadRootEntry == null ? LogEntry.SENTINAL : LogEntry.ENTRY_ID.from(threadRootEntry);
         return createEntry(typeId, entryId, status, messageArgs);
      }
      return 0L;
   }

   @Override
   public Long createEntry(Long typeId, Long parentId, Integer status, Object... messageArgs) {
      if (enabled) {
         Object[] rootEntry = activityMonitor.getThreadRootEntry();
         // Should never have a null rootEntry, but still want to log message with sentinals
         Long accountId = rootEntry == null ? LogEntry.SENTINAL : LogEntry.ACCOUNT_ID.from(rootEntry);
         Long serverId = rootEntry == null ? LogEntry.SENTINAL : LogEntry.SERVER_ID.from(rootEntry);
         Long clientId = rootEntry == null ? LogEntry.SENTINAL : LogEntry.CLIENT_ID.from(rootEntry);
         Object[] entry =
            createEntry(parentId, typeId, accountId, serverId, clientId, computeDuration(), status, messageArgs);
         return LogEntry.ENTRY_ID.from(entry);
      }
      return 0L;
   }

   @Override
   public Long createEntry(Long accountId, Long clientId, Long typeId, Long parentId, Integer status, String... messageArgs) {
      Object[] rootEntry = activityMonitor.getThreadRootEntry();
      Long serverId = LogEntry.SERVER_ID.from(rootEntry);
      Object[] entry = createEntry(parentId, typeId, accountId, serverId, clientId, computeDuration(), status,
         (Object[]) messageArgs);
      return LogEntry.ENTRY_ID.from(entry);
   }

   private Object[] createEntry(Long parentId, Long typeId, Long accountId, Long serverId, Long clientId, Long duration, Integer status, Object... messageArgs) {
      Object[] entry = EMPTY_ARRAY;
      if (enabled) {
         try {
            Long entryId = Lib.generateUuid();
            Long startTime = System.currentTimeMillis();
            String fullMsg = null;

            String messageFormat = getTypeMessageFormat(typeId);
            if (Strings.isValid(messageFormat)) {
               fullMsg = String.format(messageFormat, messageArgs);
            } else {
               fullMsg = Collections.toString("\n", messageArgs);
            }

            String msg = fullMsg.substring(0, Math.min(fullMsg.length(), JdbcConstants.JDBC__MAX_VARCHAR_LENGTH));
            // this is the parent entry so it must be inserted first (because the entry writing is asynchronous
            entry = new Object[] {
               entryId,
               parentId,
               typeId,
               accountId,
               serverId,
               clientId,
               startTime,
               duration,
               status,
               msg};
            newEntities.put(entryId, entry);

            if (fullMsg.length() > JdbcConstants.JDBC__MAX_VARCHAR_LENGTH) {
               Long parentCursor = entryId;
               for (int i = JdbcConstants.JDBC__MAX_VARCHAR_LENGTH; i < fullMsg.length(); i +=
                  JdbcConstants.JDBC__MAX_VARCHAR_LENGTH) {
                  Long continueEntryId = Lib.generateUuid();
                  Object[] continueEntry = new Object[] {
                     continueEntryId,
                     parentCursor,
                     Activity.MSG_CONTINUATION.getTypeId(),
                     accountId,
                     serverId,
                     clientId,
                     startTime,
                     duration,
                     status,
                     fullMsg.substring(i, Math.min(fullMsg.length(), i + JdbcConstants.JDBC__MAX_VARCHAR_LENGTH))};
                  newEntities.put(continueEntryId, continueEntry);
                  parentCursor = continueEntryId;
               }
            }
            flush(false);
         } catch (Throwable th) {
            logger.error(th, "Error ActivityLog.createEntry");
         }
      }
      return entry;
   }

   private class ActivityTypeMessageRetriever implements ActivityTypeDataHandler {

      private final Long typeId;
      private String messageFormat = null;

      public ActivityTypeMessageRetriever(Long typeId) {
         this.typeId = typeId;
      }

      @Override
      public void onData(Long typeId, Long logLevel, String module, String messageFormat) {
         this.messageFormat = messageFormat;
      }

      public String get() {
         queryActivityType(typeId, this);
         return messageFormat;
      }

   }

   private String getTypeMessageFormat(Long typeId) {
      return new ActivityTypeMessageRetriever(typeId).get();
   }

   @Override
   public Long createThrowableEntry(ActivityType type, Throwable throwable) {
      Long entryId = -1L;
      if (enabled) {
         try {
            String message = captureStackTrace(throwable, exceptionLineCount);
            entryId = createEntry(type.getTypeId(), ABNORMALLY_ENDED_STATUS, message);
         } catch (Throwable th) {
            logger.error(th, "logging failed in ActivityLogImpl.createThrowableEntry");
         }
      }
      return entryId;
   }

   @Override
   public boolean updateEntry(Long entryId, Integer status) {
      boolean modified = false;
      if (enabled) {
         try {
            if (!updateIfNew(entryId, status)) {
               Object[] data = updatedEntities.get(entryId);
               if (data == null || !(data.length >= LogEntry.STATUS.ordinal())) {
                  addUpdatedEntryToMap(entryId, status);
               } else {
                  data[LogEntry.STATUS.ordinal()] = status;
                  if (!updatedEntities.containsKey(entryId)) {
                     addUpdatedEntryToMap(entryId, status);
                  }
               }
               modified = true;
            }
         } catch (Throwable th) {
            logger.error(th, "Error in ActivityLog.updateEntry");
         }
      }
      return modified;
   }

   private void addUpdatedEntryToMap(Long entryId, Integer status) {
      updatedEntities.put(entryId, new Object[] {status, computeDuration(), entryId});
   }

   private Long computeDuration() {
      long timeOfUpdate = System.currentTimeMillis();
      Object[] rootEntry = activityMonitor.getThreadRootEntry();
      return timeOfUpdate = rootEntry == null ? LogEntry.SENTINAL : timeOfUpdate - LogEntry.START_TIME.from(rootEntry);
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
      if (enabled) {
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
      } else {
         newEntities.clear();
         updatedEntities.clear();
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
      try {
         final Map<Long, ActivityType> types = new HashMap<>(4);
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
      } catch (Throwable ex) {
         this.initialized.set(false);
         logger.error(ex, "Exception while initializing activity types");
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
   public Long createActivityThread(ActivityType type, Long accountId, Long serverId, Long clientId, Object... messageArgs) {
      return createActivityThread(ActivityConstants.ROOT_ENTRY_ID, type, accountId, serverId, clientId, messageArgs);
   }

   @Override
   public Long createActivityThread(Long parentId, ActivityType type, Long accountId, Long serverId, Long clientId, Object... messageArgs) {
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

   @Override
   public boolean isEnabled() {
      return enabled;
   }

   @Override
   public void setEnabled(boolean enabled) {
      this.enabled = enabled;
   }

   @Override
   public void unInitialize() {
      this.initialized.set(false);
   }

   @Override
   public String getThreadActivity(int sampleWindowMs) {
      return threadActivity.getThreadActivity(sampleWindowMs);
   }
}