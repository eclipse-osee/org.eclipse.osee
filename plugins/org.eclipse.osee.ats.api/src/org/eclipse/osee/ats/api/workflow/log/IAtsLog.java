/*********************************************************************
 * Copyright (c) 2013 Boeing
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

package org.eclipse.osee.ats.api.workflow.log;

import java.util.Date;
import java.util.List;

/**
 * @author Donald G. Dunne
 */
public interface IAtsLog {

   List<IAtsLogItem> getLogItems();

   Date getLastStatusDate();

   List<IAtsLogItem> getLogItemsReversed();

   IAtsLogItem getLogItemWithTypeAsOfDate(LogType logType, Date date);

   /**
    * Used to reset the original originated user. Only for internal use. Kept for backward compatibility.
    */
   void internalResetCreatedDate(Date date);

   String internalGetCancelledReason();

   /**
    * This method is replaced by workItem.getCompletedFromState. Kept for backward compatibility.
    */
   String internalGetCompletedFromState();

   IAtsLogItem addLog(LogType type, String state, String msg, String userId);

   IAtsLogItem addLogItem(IAtsLogItem item);

   IAtsLogItem addLog(LogType type, String state, String msg, Date date, String userId);

   void clearLog();

   IAtsLogItem getLastEvent(LogType type);

   IAtsLogItem getStateEvent(LogType type, String stateName);

   IAtsLogItem getStateEvent(LogType type);

   void setLogId(String logId);

   void setDirty(boolean dirty);

   boolean isDirty();
}
