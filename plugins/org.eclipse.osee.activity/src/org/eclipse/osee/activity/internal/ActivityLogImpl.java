/*********************************************************************
 * Copyright (c) 2014 Boeing
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

package org.eclipse.osee.activity.internal;

import static org.eclipse.osee.activity.ActivityConstants.ACTIVITY_LOGGER__CLEANER_EXECUTOR_ID;
import static org.eclipse.osee.activity.ActivityConstants.ACTIVITY_LOGGER__CLEANER_KEEP_DAYS;
import static org.eclipse.osee.activity.ActivityConstants.ACTIVITY_LOGGER__ENABLED;
import static org.eclipse.osee.activity.ActivityConstants.ACTIVITY_LOGGER__EXECUTOR_ID;
import static org.eclipse.osee.activity.ActivityConstants.ACTIVITY_LOGGER__STACKTRACE_LINE_COUNT;
import static org.eclipse.osee.activity.ActivityConstants.ACTIVITY_LOGGER__WRITE_RATE_IN_MILLIS;
import static org.eclipse.osee.activity.ActivityConstants.DEFAULT_ACTIVITY_LOGGER__CLEANER_KEEP_DAYS;
import static org.eclipse.osee.activity.ActivityConstants.DEFAULT_ACTIVITY_LOGGER__STACKTRACE_LINE_COUNT;
import static org.eclipse.osee.activity.ActivityConstants.DEFAULT_ACTIVITY_LOGGER__WRITE_RATE_IN_MILLIS;
import static org.eclipse.osee.activity.ActivityConstants.DEFAULT_CLIENT_ID;
import static org.eclipse.osee.activity.internal.ActivityLogImpl.LogEntry.ENTRY_ID;
import static org.eclipse.osee.activity.internal.ActivityLogImpl.LogEntry.SERVER_ID;
import static org.eclipse.osee.activity.internal.ActivityLogImpl.LogEntry.START_TIME;
import static org.eclipse.osee.framework.core.data.CoreActivityTypes.DEFAULT_ROOT;
import static org.eclipse.osee.framework.core.data.CoreActivityTypes.THREAD_ACTIVITY;
import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.lang.management.RuntimeMXBean;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import org.eclipse.osee.activity.ActivityConstants;
import org.eclipse.osee.activity.ActivityStorage;
import org.eclipse.osee.activity.api.ActivityEntry;
import org.eclipse.osee.activity.api.ActivityEntryId;
import org.eclipse.osee.activity.api.ActivityLog;
import org.eclipse.osee.activity.api.ThreadStats;
import org.eclipse.osee.framework.core.data.ActivityTypeId;
import org.eclipse.osee.framework.core.data.ActivityTypeToken;
import org.eclipse.osee.framework.core.data.CoreActivityTypes;
import org.eclipse.osee.framework.core.data.UserId;
import org.eclipse.osee.framework.core.enums.SystemUser;
import org.eclipse.osee.framework.core.executor.ExecutorAdmin;
import org.eclipse.osee.framework.core.server.IApplicationServerManager;
import org.eclipse.osee.framework.jdk.core.type.DrainingIterator;
import org.eclipse.osee.framework.jdk.core.type.Id;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.jdk.core.util.time.GlobalTime;
import org.eclipse.osee.jdbc.JdbcConstants;
import org.eclipse.osee.logger.Log;

/**
 * @author Ryan D. Brooks
 */
public class ActivityLogImpl implements ActivityLog, Runnable {

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

      public static final Long SENTINEL = Id.SENTINEL;

      public Long from(Object[] entry) {
         if (entry == null) {
            return SENTINEL;
         }
         Object obj = entry[ordinal()];
         if (obj instanceof Long) {
            return (Long) obj;
         }
         if (obj instanceof Id) {
            return ((Id) obj).getId();
         }
         if (obj instanceof Timestamp) {
            return ((Timestamp) obj).getTime();
         }
         throw new OseeArgumentException(
            "LogEntryIndex.from called with agrgument of unsupported type " + obj.getClass());
      }
   };

   private final ConcurrentHashMap<Long, ActivityTypeToken> types = new ConcurrentHashMap<>(30);

   private final ConcurrentHashMap<Long, Object[]> newEntities = new ConcurrentHashMap<>();
   private final ConcurrentHashMap<Long, Object[]> updatedEntities = new ConcurrentHashMap<>();
   private final ThreadActivity threadActivity = new ThreadActivity();
   private Log logger;
   private ExecutorAdmin executorAdmin;
   private ActivityStorage storage;

   private ActivityMonitor activityMonitor;
   private volatile long freshnessMillis;
   private volatile int exceptionLineCount;
   private volatile long lastFlushTime;
   private volatile int cleanerKeepDays;
   private volatile boolean enabled = ActivityConstants.DEFAULT_ACTIVITY_LOGGER__ENABLED;
   private IApplicationServerManager applicationServerManager;
   private String host;
   private ThreadStats[] threadStats;
   private Long threadActivityParententryId;

   public void setApplicationServerManager(IApplicationServerManager applicationServerManager) {
      this.applicationServerManager = applicationServerManager;
   }

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
      for (ActivityTypeToken type : CoreActivityTypes.getTypes()) {
         types.put(type.getId(), type);
      }
      update(properties);

      host = applicationServerManager.getServerUri().toString();
      Object[] defaultRootEntry = createEntry(Id.SENTINEL, DEFAULT_ROOT, SystemUser.OseeSystem, getThisServerId(),
         DEFAULT_CLIENT_ID, 0L, 0, "default root entry for " + host);
      activityMonitor = new ActivityMonitor(defaultRootEntry);
      setupThreadActivityLogging();
   }

   private Long getThisServerId() {
      return (long) applicationServerManager.getPort();
   }

   private void setupThreadActivityLogging() {
      threadActivityParententryId = createActivityThread(THREAD_ACTIVITY, SystemUser.OseeSystem, get(SERVER_ID),
         DEFAULT_CLIENT_ID, "Start of thread activity logging thread on " + host);

      int sampleWindowSecs = 100;
      threadStats = getThreadActivity();
      executorAdmin.scheduleWithFixedDelay("Thread Activity Log", this::continuouslyLogThreadActivity, sampleWindowSecs,
         sampleWindowSecs, TimeUnit.SECONDS);
   }

   private void continuouslyLogThreadActivity() {
      String threadReport = Collections.toString("\n", "", getThreadActivityDelta(threadStats));
      threadStats = getThreadActivity();

      if (!threadReport.isEmpty()) {
         createEntry(THREAD_ACTIVITY, threadActivityParententryId, COMPLETE_STATUS, threadReport);
      }

      RuntimeMXBean runtimeMxBean = ManagementFactory.getRuntimeMXBean();

      StringBuilder sb = new StringBuilder("Mem Sats - ");
      sb.append(" start [");
      String startTime = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG).format(
         applicationServerManager.getDateStarted()).toString();
      sb.append(startTime);
      sb.append("] upTime [");
      int seconds = (int) (runtimeMxBean.getUptime() / 1000) % 60;
      int minutes = (int) ((runtimeMxBean.getUptime() / (1000 * 60)) % 60);
      int hours = (int) ((runtimeMxBean.getUptime() / (1000 * 60 * 60)) % 24);
      sb.append(String.format("%s h %s m %s s", hours, minutes, seconds));

      MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();
      MemoryUsage heapMem = memoryMXBean.getHeapMemoryUsage();
      sb.append("] heapUsed [");
      sb.append(Lib.toMBytes(heapMem.getUsed()));
      sb.append("] heapAlloc [");
      sb.append(Lib.toMBytes(heapMem.getCommitted()));
      sb.append("] heapMax [");
      sb.append(Lib.toMBytes(heapMem.getMax()));
      MemoryUsage nonHeapMem = memoryMXBean.getNonHeapMemoryUsage();
      sb.append("] nonHeapUsed [");
      sb.append(Lib.toMBytes(nonHeapMem.getUsed()));
      sb.append("] nonHeapAlloc [");
      sb.append(Lib.toMBytes(nonHeapMem.getCommitted()));
      sb.append("] nonHeapMax [");
      sb.append(Lib.toMBytes(nonHeapMem.getMax()));
      sb.append("] ");

      sb.append(getGarbageCollectionStats());
      createEntry(CoreActivityTypes.MEMORY_ACTIVITY, threadActivityParententryId, COMPLETE_STATUS, sb.toString());
   }

   @Override
   public List<String> getGarbageCollectionStats() {
      List<GarbageCollectorMXBean> garbageCollectorMXBeans = ManagementFactory.getGarbageCollectorMXBeans();
      List<String> stats = new ArrayList<>(garbageCollectorMXBeans.size());
      for (GarbageCollectorMXBean gcBean : garbageCollectorMXBeans) {
         stats.add(String.format("%s: %s (count); %s (ms)", gcBean.getName(), gcBean.getCollectionCount(),
            gcBean.getCollectionTime()));
      }
      return stats;
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
   public ActivityEntry getEntry(ActivityEntryId entryId) {
      return storage.getEntry(entryId);
   }

   public void update(Map<String, Object> properties) {
      //@formatter:off
      freshnessMillis = get(properties, ACTIVITY_LOGGER__WRITE_RATE_IN_MILLIS, DEFAULT_ACTIVITY_LOGGER__WRITE_RATE_IN_MILLIS);
      exceptionLineCount = get(properties, ACTIVITY_LOGGER__STACKTRACE_LINE_COUNT, DEFAULT_ACTIVITY_LOGGER__STACKTRACE_LINE_COUNT);
      String value = (String)properties.get(ACTIVITY_LOGGER__ENABLED);
      int newCleanerKeepDays = get(properties, ACTIVITY_LOGGER__CLEANER_KEEP_DAYS, DEFAULT_ACTIVITY_LOGGER__CLEANER_KEEP_DAYS);
      if (Strings.isValid(value)) {
         enabled = Boolean.valueOf(value);
      }
      //@formatter:on

      if (newCleanerKeepDays != cleanerKeepDays) {
         cleanerKeepDays = newCleanerKeepDays;
         setupCleaner();
      }
   }

   private void clean() {
      storage.cleanEntries(cleanerKeepDays);
   }

   private void setupCleaner() {
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
      executorAdmin.scheduleAtFixedRate(ACTIVITY_LOGGER__CLEANER_EXECUTOR_ID, this::clean, startAfter, 60 * 24,
         TimeUnit.MINUTES);
   }

   @Override
   public Long createEntry(ActivityTypeToken type, Object... messageArgs) {
      return createEntry(type, COMPLETE_STATUS, messageArgs);
   }

   @Override
   public Long createUpdateableEntry(ActivityTypeToken type, Object... messageArgs) {
      return createEntry(type, INITIAL_STATUS, messageArgs);
   }

   @Override
   public Long createEntry(ActivityTypeToken type, Integer status, Object... messageArgs) {
      Long parentId = get(ENTRY_ID);
      return createEntry(type, parentId, status, messageArgs);
   }

   @Override
   public Long createEntry(UserId accountId, ActivityTypeToken type, Integer status, Object... messageArgs) {
      Long parentId = get(ENTRY_ID);
      return createEntry(accountId, type, parentId, status, messageArgs);
   }

   @Override
   public Long createEntry(ActivityTypeToken type, Long parentId, Integer status, Object... messageArgs) {
      UserId accountId = UserId.valueOf(get(LogEntry.ACCOUNT_ID));
      return createEntry(accountId, type, parentId, status, messageArgs);
   }

   private Long createEntry(UserId accountId, ActivityTypeToken type, Long parentId, Integer status, Object... messageArgs) {
      if (isEnabled()) {
         Object[] rootEntry = activityMonitor.getThreadRootEntry(parentId);
         Long serverId = LogEntry.SERVER_ID.from(rootEntry);
         Long clientId = LogEntry.CLIENT_ID.from(rootEntry);
         Object[] entry =
            createEntry(parentId, type, accountId, serverId, clientId, computeDuration(parentId), status, messageArgs);
         return LogEntry.ENTRY_ID.from(entry);
      }
      return 0L;
   }

   private Long get(LogEntry entry) {
      return entry.from(activityMonitor.getThreadRootEntry());
   }

   @Override
   public Long createEntry(UserId accountId, Long clientId, ActivityTypeToken type, Long parentId, Integer status, String... messageArgs) {
      Object[] entry = createEntry(parentId, type, accountId, get(SERVER_ID), clientId, computeDuration(parentId),
         status, (Object[]) messageArgs);
      return LogEntry.ENTRY_ID.from(entry);
   }

   private Object[] createEntry(Long parentId, ActivityTypeToken type, UserId accountId, Long serverId, Long clientId, Long duration, Integer status, Object... messageArgs) {
      Object[] entry;
      Long entryId = Lib.generateUuid();
      Date startTime = GlobalTime.GreenwichMeanTimestamp();

      String msg;
      String fullMsg = null;
      try {
         String messageFormat = type.getMessageFormat();
         if (Strings.isValid(messageFormat)) {
            fullMsg = String.format(messageFormat, messageArgs);
         } else {
            fullMsg = Collections.toString("\n", messageArgs);
         }

         msg = fullMsg.substring(0, Math.min(fullMsg.length(), JdbcConstants.JDBC__MAX_VARCHAR_LENGTH));
      } catch (Throwable th) {
         msg = th.toString();
         logger.error(th, "Error ActivityLog.createEntry");
      }

      // this is the parent entry so it must be inserted first (because the entry writing is asynchronous
      entry = new Object[] {entryId, parentId, type, accountId, serverId, clientId, startTime, duration, status, msg};
      newEntities.put(entryId, entry);

      if (fullMsg != null && fullMsg.length() > JdbcConstants.JDBC__MAX_VARCHAR_LENGTH) {
         Long parentCursor = entryId;
         for (int i = JdbcConstants.JDBC__MAX_VARCHAR_LENGTH; i < fullMsg.length(); i +=
            JdbcConstants.JDBC__MAX_VARCHAR_LENGTH) {
            Long continueEntryId = Lib.generateUuid();
            Object[] continueEntry = new Object[] {
               continueEntryId,
               parentCursor,
               CoreActivityTypes.MSG_CONTINUATION,
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

      return entry;
   }

   @Override
   public Long createThrowableEntry(ActivityTypeToken type, Throwable throwable) {
      return createThrowableEntry(type, throwable, "");
   }

   @Override
   public Long createThrowableEntry(ActivityTypeToken type, Throwable throwable, String messageSummary) {
      Long entryId = -1L;
      if (isEnabled()) {
         try {
            String stackTrace = captureStackTrace(throwable, exceptionLineCount);
            String message = Strings.isValid(messageSummary) ? messageSummary + ": " + stackTrace : stackTrace;
            entryId = createEntry(type, ABNORMALLY_ENDED_STATUS, message);
         } catch (Throwable th) {
            logger.error(th, "logging failed in ActivityLogImpl.createThrowableEntry");
         }
      }
      return entryId;
   }

   @Override
   public boolean updateEntry(Long entryId, Integer status) {
      boolean modified = false;
      if (isEnabled()) {
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
      return computeDuration(activityMonitor.getThreadRootEntry());
   }

   private Long computeDuration(Object[] threadRootEntry) {
      return System.currentTimeMillis() - START_TIME.from(threadRootEntry);
   }

   private Long computeDuration(Long parentId) {
      return computeDuration(activityMonitor.getThreadRootEntry(parentId));
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
   public void run() {
      if (isEnabled()) {
         if (!newEntities.isEmpty()) {
            try {
               storage.addEntries(new DrainingIterator<>(newEntities.values().iterator()));
            } catch (Throwable ex) {
               logger.error(ex, "Exception while storing updates to the activity log");
            }
         }
         if (!updatedEntities.isEmpty()) {
            try {
               storage.updateEntries(new DrainingIterator<>(updatedEntities.values().iterator()));
            } catch (Throwable ex) {
               logger.error(ex, "Exception while storing updates to the activity log");
            }
         }
      } else {
         newEntities.clear();
         updatedEntities.clear();
      }
   }

   private void flush(boolean force) {
      long currentTime = System.currentTimeMillis();
      if (force || currentTime - lastFlushTime > freshnessMillis) {
         try {
            executorAdmin.submit("Activity Log flush to datastore", this);
         } catch (Exception ex) {
            logger.error(ex, "Error scheduling activity log callable");
         } finally {
            lastFlushTime = currentTime;
         }
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
   public Long createActivityThread(ActivityTypeToken type, UserId accountId, Long serverId, Long clientId, Object... messageArgs) {
      return createActivityThread(Id.SENTINEL, type, accountId, serverId, clientId, messageArgs);
   }

   @Override
   public Long createActivityThread(Long parentId, ActivityTypeToken type, UserId accountId, Long serverId, Long clientId, Object... messageArgs) {
      Object[] entry = createEntry(parentId, type, accountId, serverId, clientId, 0L, 0, messageArgs);
      activityMonitor.addActivityThread(entry);
      return LogEntry.ENTRY_ID.from(entry);
   }

   @Override
   public void removeActivityThread() {
      activityMonitor.removeActivityThread();
   }

   @Override
   public ActivityTypeToken getActivityType(ActivityTypeId typeId) {
      ActivityTypeToken type = types.get(typeId);
      if (type == null) {
         type = storage.getActivityType(typeId);
         types.put(type.getId(), type);
      }
      return type;
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
   public ActivityTypeToken createIfAbsent(ActivityTypeToken token) {
      ActivityTypeToken type = types.get(token);
      if (type == null) {
         type = storage.createIfAbsent(token);
         types.put(type.getId(), type);
      }
      return type;
   }

   @Override
   public ThreadStats[] getThreadActivity() {
      return threadActivity.getThreadActivity();
   }

   @Override
   public List<String> getThreadActivityDelta(ThreadStats[] threadStats) {
      return threadActivity.getThreadActivityDelta(threadStats);
   }

   @Override
   public Log getDebugLogger() {
      return logger;
   }

   private String captureStackTrace(Throwable ex, int linesToCapture) {
      Throwable cause = ex;

      while (cause.getCause() != null) {
         cause = cause.getCause();
      }

      StringBuilder sb = new StringBuilder();
      sb.append(cause.toString() + "\n");
      StackTraceElement stackElements[] = cause.getStackTrace();
      for (int i = 0; i < Math.min(stackElements.length, linesToCapture); i++) {
         sb.append(stackElements[i] + "\n");
      }
      return sb.toString();
   }

   @SuppressWarnings("unchecked")
   private <T> T get(Map<String, Object> properties, String key, T defaultValue) {
      T value = properties != null ? (T) properties.get(key) : null;
      if (value == null) {
         value = defaultValue;
      }
      return value;
   }
}