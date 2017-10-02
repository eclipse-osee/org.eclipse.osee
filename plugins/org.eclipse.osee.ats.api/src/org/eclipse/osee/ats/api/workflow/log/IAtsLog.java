/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.api.workflow.log;

import java.util.Date;
import java.util.List;

/**
 * @author Donald G. Dunne
 */
public interface IAtsLog {

   public abstract List<IAtsLogItem> getLogItems();

   public abstract Date getLastStatusDate();

   public abstract List<IAtsLogItem> getLogItemsReversed();

   public abstract IAtsLogItem getLogItemWithTypeAsOfDate(LogType logType, Date date);

   /**
    * Used to reset the original originated user. Only for internal use. Kept for backward compatibility.
    */
   public abstract void internalResetCreatedDate(Date date);

   public abstract String internalGetCancelledReason();

   /**
    * This method is replaced by workItem.getCompletedFromState. Kept for backward compatibility.
    */
   public abstract String internalGetCompletedFromState();

   public abstract IAtsLogItem addLog(LogType type, String state, String msg, String userId);

   public abstract IAtsLogItem addLogItem(IAtsLogItem item);

   public abstract IAtsLogItem addLog(LogType type, String state, String msg, Date date, String userId);

   public abstract void clearLog();

   public abstract IAtsLogItem getLastEvent(LogType type);

   public abstract IAtsLogItem getStateEvent(LogType type, String stateName);

   public abstract IAtsLogItem getStateEvent(LogType type);

   public abstract void setLogId(String logId);

   public abstract void setDirty(boolean dirty);

   public abstract boolean isDirty();
}
