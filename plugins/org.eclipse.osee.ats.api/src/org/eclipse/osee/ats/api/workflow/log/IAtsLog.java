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
import org.eclipse.osee.ats.api.user.IAtsUser;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;

/**
 * @author Donald G. Dunne
 */
public interface IAtsLog {

   public abstract List<IAtsLogItem> getLogItems() throws OseeCoreException;

   public abstract Date getLastStatusDate() throws OseeCoreException;

   public abstract List<IAtsLogItem> getLogItemsReversed() throws OseeCoreException;

   /**
    * Used to reset the original originated user. Only for internal use. Kept for backward compatibility.
    * 
    * @param changes JavaTip
    */
   public abstract void internalResetOriginator(IAtsUser user) throws OseeCoreException;

   /**
    * Used to reset the original originated user. Only for internal use. Kept for backward compatibility.
    * 
    * @param changes JavaTip
    */
   public abstract void internalResetCreatedDate(Date date) throws OseeCoreException;

   public abstract String internalGetCancelledReason() throws OseeCoreException;

   /**
    * This method is replaced by workItem.getCompletedFromState. Kept for backward compatibility.
    */
   public abstract String internalGetCompletedFromState() throws OseeCoreException;

   /**
    * @param state name of state or null
    * @param userId JavaTip
    * @param changes JavaTip
    */
   public abstract void addLog(LogType type, String state, String msg, String userId) throws OseeCoreException;

   public abstract void addLogItem(IAtsLogItem item) throws OseeCoreException;

   public abstract void addLog(LogType type, String state, String msg, Date date, String userId) throws OseeCoreException;

   public abstract void clearLog();

   public abstract IAtsLogItem getLastEvent(LogType type) throws OseeCoreException;

   public abstract IAtsLogItem getStateEvent(LogType type, String stateName) throws OseeCoreException;

   public abstract IAtsLogItem getStateEvent(LogType type) throws OseeCoreException;

   public abstract void setLogId(String logId);

   public abstract void setDirty(boolean dirty);

   public abstract boolean isDirty();
}
