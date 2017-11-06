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

import org.eclipse.osee.framework.core.data.ActivityTypeId;
import org.eclipse.osee.framework.core.data.ActivityTypeToken;
import org.eclipse.osee.framework.core.data.UserId;

/**
 * @author Ryan D. Brooks
 */
public interface ActivityLog {
   Integer INITIAL_STATUS = 100;
   Integer COMPLETE_STATUS = 100;
   Integer ABNORMALLY_ENDED_STATUS = 500;

   ActivityEntry getEntry(ActivityEntryId entryId);

   Long createEntry(ActivityTypeToken type, Integer status, Object... messageArgs);

   Long createUpdateableEntry(ActivityTypeToken type, Object... messageArgs);

   Long createEntry(ActivityTypeToken type, Object... messageArgs);

   Long createEntry(ActivityTypeToken type, Long parentId, Integer status, Object... messageArgs);

   Long createEntry(UserId accountId, Long clientId, ActivityTypeToken typeId, Long parentId, Integer status, String... messageArgs);

   Long createThrowableEntry(ActivityTypeToken type, Throwable throwable);

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

   Long createActivityThread(ActivityTypeToken type, UserId accountId, Long serverId, Long clientId, Object... messageArgs);

   /**
    * @param serverId id of server originating the message. -1 if a client originates the message
    * @return the new entry id which is the parent id for all direct child entries created on this thread
    */
   Long createActivityThread(Long parentId, ActivityTypeToken type, UserId accountId, Long serverId, Long clientId, Object... messageArgs);

   void removeActivityThread();

   ActivityTypeToken getActivityType(ActivityTypeId typeId);

   boolean isEnabled();

   void setEnabled(boolean enabled);

   ActivityTypeToken createIfAbsent(ActivityTypeToken type);

   /**
    * Returns info for up to 15 threads ordered by most cpu time during the sample window </br>
    * Augmented Backus-Naur Form (ABNF) defined in https://tools.ietf.org/html/rfc5234</br>
    * ABNF Format:</br>
    * <code>
    * thread-activity = thread-info*</br>
    * thread-info = <thread-name> "(" thread-id ")" elapsed ", " total ", " blocked ", " (stack-trace-element " ")*
    * </code>
    * <li>elapsed = cpu elapsed time in milliseconds of the named thread during the sample period (currently 5 minutes)
    * <li>total = cpu elapsed time in milliseconds of the named thread since start of thread
    * <li>blocked = milliseconds that the thread associated has blocked to enter or reenter a monitor since thread
    * contention monitoring is enabled. -1 if thread contention monitoring is disabled.
    */
   String getThreadActivity(int sampleWindowMs);
}