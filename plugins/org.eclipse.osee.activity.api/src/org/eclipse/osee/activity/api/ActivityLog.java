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
package org.eclipse.osee.activity.api;

/**
 * @author Ryan D. Brooks
 */
public interface ActivityLog {

   public static interface ActivityDataHandler {
      void onData(Long entryId, Long parentId, Long typeId, Long accountId, Long serverId, Long clientId, Long startTime, Long duration, Integer status, String messageArgs);
   }

   public static interface ActivityTypeDataHandler {
      void onData(Long typeId, Long logLevel, String module, String messageFormat);
   }

   Integer INITIAL_STATUS = 100;
   Integer COMPLETE_STATUS = 100;
   Integer ABNORMALLY_ENDED_STATUS = 500;

   void queryEntry(Long entryId, ActivityDataHandler handler);

   Long createEntry(Long typeId, Integer status, Object... messageArgs);

   Long createEntry(Long typeId, Long parentId, Integer status, Object... messageArgs);

   Long createUpdateableEntry(ActivityType type, Object... messageArgs);

   Long createEntry(ActivityType type, Object... messageArgs);

   Long createEntry(ActivityType type, Long parentId, Integer status, Object... messageArgs);

   Long createThrowableEntry(ActivityType type, Throwable throwable);

   boolean updateEntry(Long entryId, Integer status);

   /**
    * Set the status of log entry corresponding to entryId to 100% complete
    */
   void completeEntry(Long entryId);

   /**
    * Set the status of log entry corresponding to entryId to abnormally ended
    */
   void endEntryAbnormally(Long entryId);

   void endEntryAbnormally(Long entryId, Integer status);

   Long createActivityThread(ActivityType type, Long accountId, Long serverId, Long clientId, Object... messageArgs);

   Long createActivityThread(Long parentId, ActivityType type, Long accountId, Long serverId, Long clientId, Object... messageArgs);

   void createActivityTypes(ActivityType... types);

   void queryActivityTypes(ActivityTypeDataHandler handler);

   void queryActivityType(Long typeId, ActivityTypeDataHandler handler);

   boolean activityTypeExists(Long typeId);

   boolean isEnabled();

   void setEnabled(boolean enabled);

   /**
    * Sets the ActivityLog as un-initialized. Next call to create will re-initialize
    */
   void unInitialize();
}