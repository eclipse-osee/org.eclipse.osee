/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
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

package org.eclipse.osee.ats.core.internal.log;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import org.eclipse.osee.ats.api.workdef.IStateToken;
import org.eclipse.osee.ats.api.workflow.log.IAtsLog;
import org.eclipse.osee.ats.api.workflow.log.IAtsLogItem;
import org.eclipse.osee.ats.api.workflow.log.LogType;
import org.eclipse.osee.framework.logging.OseeLog;

/**
 * @author Donald G. Dunne
 */
public class AtsLog implements IAtsLog {

   private boolean dirty = false;
   private final List<IAtsLogItem> logItems = new ArrayList<>();
   private String logId = "none";

   @Override
   public String toString() {
      try {
         return org.eclipse.osee.framework.jdk.core.util.Collections.toString("\n", getLogItems());
      } catch (Exception ex) {
         OseeLog.log(AtsLog.class, Level.SEVERE, ex);
         return ex.getLocalizedMessage();
      }
   }

   @Override
   public Date getLastStatusDate() {
      IAtsLogItem logItem = getLastEvent(LogType.Metrics);
      if (logItem == null) {
         return null;
      }
      return logItem.getDate();
   }

   @Override
   public List<IAtsLogItem> getLogItemsReversed() {
      List<IAtsLogItem> logItems = new ArrayList<>(getLogItems());
      Collections.reverse(logItems);
      return logItems;
   }

   @Override
   public Collection<IAtsLogItem> getLogItems(LogType logType) {
      List<IAtsLogItem> items = new ArrayList<>();
      for (IAtsLogItem logItem : logItems) {
         if (logItem.getType().equals(logType)) {
            items.add(logItem);
         }
      }
      return items;
   }

   @Override
   public IAtsLogItem getLogItemWithTypeAsOfDate(LogType logType, Date date) {
      IAtsLogItem retLogItem = null;
      for (IAtsLogItem logItem : logItems) {
         if (logItem.getType().equals(logType)) {
            Date logItemDate = logItem.getDate();
            if (logItemDate.after(date)) {
               break;
            } else {
               retLogItem = logItem;
            }
         }
      }
      return retLogItem;
   }

   /**
    * Used to reset the original originated user. Only for internal use. Kept for backward compatibility.
    */
   @Override
   public void internalResetCreatedDate(Date date) {
      List<IAtsLogItem> logItems = getLogItems();
      for (IAtsLogItem item : logItems) {
         if (item.getType() == LogType.Originated) {
            item.setDate(date);
            dirty = true;
            return;
         }
      }
   }

   @Override
   public String internalGetCancelledReason() {
      IAtsLogItem item = getStateEvent(LogType.StateCancelled);
      if (item == null) {
         return "";
      }
      return item.getMsg();
   }

   /**
    * This method is replaced by workItem.getCompletedFromState. Kept for backward compatibility.
    */
   @Override
   public String internalGetCompletedFromState() {
      IAtsLogItem item = getStateEvent(LogType.StateComplete);
      if (item == null) {
         return "";
      }
      return item.getState();
   }

   @Override
   public IAtsLogItem addLogItem(IAtsLogItem item) {
      return addLog(item.getType(), item.getState(), item.getMsg(), item.getDate(), item.getUserId());
   }

   @Override
   public IAtsLogItem addLog(LogType logType, String state, String msg, String userId) {
      return addLog(logType, state, msg, new Date(), userId);
   }

   @Override
   public IAtsLogItem addLog(LogType logType, String state, String msg, Date date, String userId) {
      LogItem logItem = new LogItem(logType, date, userId, state, msg);
      List<IAtsLogItem> logItems = getLogItems();
      logItems.add(logItem);
      dirty = true;
      return logItem;
   }

   @Override
   public void clearLog() {
      logItems.clear();
      dirty = true;
   }

   @Override
   public IAtsLogItem getLastEvent(LogType logType) {
      for (IAtsLogItem item : getLogItemsReversed()) {
         if (item.getType() == logType) {
            return item;
         }
      }
      return null;
   }

   @Override
   public IAtsLogItem getStateEvent(LogType logType, String stateName) {
      for (IAtsLogItem item : getLogItemsReversed()) {
         if (item.getType() == logType && item.getState().equals(stateName)) {
            return item;
         }
      }
      return null;
   }

   @Override
   public IAtsLogItem getStateEvent(LogType logType) {
      for (IAtsLogItem item : getLogItemsReversed()) {
         if (item.getType() == logType) {
            return item;
         }
      }
      return null;
   }

   @Override
   public List<IAtsLogItem> getLogItems() {
      return logItems;
   }

   @Override
   public boolean isDirty() {
      return dirty;
   }

   @Override
   public void setDirty(boolean dirty) {
      this.dirty = dirty;
   }

   @Override
   public void setLogId(String logId) {
      this.logId = logId;
   }

   public String getLogId() {
      return logId;
   }

   @Override
   public List<String> getVisitedStateNames() {
      List<String> names = new ArrayList<>();
      for (IAtsLogItem item : getLogItems(LogType.StateEntered)) {
         if (!names.contains(item.getState())) {
            names.add(item.getState());
         }
      }
      return names;
   }

   @Override
   public boolean isStateVisited(IStateToken state) {
      return isStateVisited(state.getName());
   }

   @Override
   public boolean isStateVisited(String stateName) {
      for (IAtsLogItem item : getLogItems(LogType.StateEntered)) {
         if (item.getState().equals(stateName)) {
            return true;
         }
      }
      return false;
   }
}