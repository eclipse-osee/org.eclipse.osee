/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/

package org.eclipse.osee.ats.artifact;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import org.eclipse.osee.ats.internal.AtsPlugin;
import org.eclipse.osee.ats.util.DefaultTeamState;
import org.eclipse.osee.framework.core.exception.OseeArgumentException;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeStateException;
import org.eclipse.osee.framework.jdk.core.util.AHTML;
import org.eclipse.osee.framework.jdk.core.util.DateUtil;
import org.eclipse.osee.framework.jdk.core.util.xml.Jaxp;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * @author Donald G. Dunne
 */
public class ATSLog {

   private final WeakReference<Artifact> artifactRef;
   private boolean enabled = true;
   private final static String ATS_LOG_TAG = "AtsLog";
   private final static String LOG_ITEM_TAG = "Item";
   private LogItem cancelledLogItem;
   private LogItem completedLogItem;
   public static enum LogType {
      None,
      Originated,
      StateComplete,
      StateCancelled,
      StateEntered,
      Released,
      Error,
      Assign,
      Note,
      Metrics;

      public static LogType getType(String type) throws OseeArgumentException {
         for (Enum<LogType> e : LogType.values()) {
            if (e.name().equals(type)) {
               return (LogType) e;
            }
         }
         throw new OseeArgumentException("Unhandled LogType: [%s]", type);
      }

   };

   public ATSLog(Artifact artifact) {
      this.artifactRef = new WeakReference<Artifact>(artifact);
   }

   @Override
   public String toString() {
      try {
         return getLogItems().toString();
      } catch (Exception ex) {
         OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
         return ex.getLocalizedMessage();
      }
   }

   public String getHtml() throws OseeCoreException {
      return getHtml(true);
   }

   public String getHtml(boolean showLog) throws OseeCoreException {
      if (getLogItems().isEmpty()) {
         return "";
      }
      StringBuffer sb = new StringBuffer();
      if (showLog) {
         sb.append(AHTML.addSpace(1) + AHTML.getLabelStr(
            AHTML.LABEL_FONT,
            "History for \"" + getArtifact().getArtifactTypeName() + "\" - " + getArtifact().getHumanReadableId() + " - titled \"" + getArtifact().getName() + "\""));
      }
      sb.append(getTable());
      return sb.toString();
   }

   public Artifact getArtifact() throws OseeStateException {
      if (artifactRef.get() == null) {
         throw new OseeStateException("Artifact has been garbage collected");
      }
      return artifactRef.get();
   }

   public List<LogItem> getLogItems() throws OseeCoreException {
      String xml = getArtifact().getSoleAttributeValue(AtsAttributeTypes.Log, "");
      return LogItem.getLogItems(xml, getArtifact().getHumanReadableId());
   }

   public Date getLastStatusedDate() throws OseeCoreException {
      LogItem logItem = getLastEvent(LogType.Metrics);
      if (logItem == null) {
         return null;
      }
      return logItem.getDate();
   }

   public void putLogItems(List<LogItem> items) {
      try {
         Document doc = Jaxp.newDocumentNamespaceAware();
         Element rootElement = doc.createElement(ATS_LOG_TAG);
         doc.appendChild(rootElement);
         for (LogItem item : items) {
            Element element = doc.createElement(LOG_ITEM_TAG);
            element.setAttribute("type", item.getType().name());
            element.setAttribute("date", String.valueOf(item.getDate().getTime()));
            element.setAttribute("userId", item.getUser().getUserId());
            element.setAttribute("state", item.getState());
            element.setAttribute("msg", item.getMsg());
            rootElement.appendChild(element);
         }
         getArtifact().setSoleAttributeValue(AtsAttributeTypes.Log, Jaxp.getDocumentXml(doc));
      } catch (Exception ex) {
         OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, "Can't create ats log document", ex);
      }
   }

   public List<LogItem> getLogItemsReversed() throws OseeCoreException {
      List<LogItem> logItems = getLogItems();
      Collections.reverse(logItems);
      return logItems;
   }

   public void setOriginator(User user) throws OseeCoreException {
      List<LogItem> logItems = getLogItems();
      for (LogItem item : logItems) {
         if (item.getType() == LogType.Originated) {
            item.setUser(user);
            putLogItems(logItems);
            return;
         }
      }
   }

   public void setCreationDate(Date date) throws OseeCoreException {
      List<LogItem> logItems = getLogItems();
      for (LogItem item : logItems) {
         if (item.getType() == LogType.Originated) {
            item.setDate(date);
            putLogItems(logItems);
            return;
         }
      }
   }

   public String getCancellationReason() throws OseeCoreException {
      LogItem item = getStateEvent(LogType.StateCancelled);
      if (item == null) {
         return "";
      }
      return item.getMsg();
   }

   public String getCancelledFromState() throws OseeCoreException {
      LogItem item = getStateEvent(LogType.StateCancelled);
      if (item == null) {
         return "";
      }
      return item.getState();
   }

   public String getCompletedFromState() throws OseeCoreException {
      LogItem item = getStateEvent(LogType.StateComplete);
      if (item == null) {
         return "";
      }
      return item.getState();
   }

   public void setCancellationReason(String reason) throws OseeCoreException {
      List<LogItem> logItems = getLogItemsReversed();
      for (LogItem item : logItems) {
         if (item.getType() == LogType.StateCancelled) {
            item.setMsg(reason);
            putLogItems(logItems);
            return;
         }
      }
   }

   /**
    * Since originator can be changed, return the date of the first originated log item
    */
   public Date getCreationDate() throws OseeCoreException {
      LogItem logItem = getEvent(LogType.Originated);
      if (logItem == null) {
         return null;
      }
      return logItem.getDate();
   }

   /**
    * Since originator change be changed, return the last originated event's user
    */
   public User getOriginator() throws OseeCoreException {
      LogItem logItem = getLastEvent(LogType.Originated);
      if (logItem == null) {
         return null;
      }
      return logItem.getUser();
   }

   /**
    * Overwrite the first logItem to match type and state with newItem data
    */
   public void overrideStateItemData(LogType matchType, String matchState, LogItem newItem) throws OseeCoreException {
      List<LogItem> logItems = getLogItems();
      for (LogItem item : logItems) {
         if (item.getType() == matchType && item.getState().equals(matchState)) {
            item.setUser(newItem.getUser());
            item.setDate(newItem.getDate());
            item.setMsg(newItem.getMsg());
            putLogItems(logItems);
            return;
         }
      }
   }

   /**
    * Overwrite the first logItem to match matchType with newItem data
    */
   public void overrideItemData(LogType matchType, LogItem newItem) throws OseeCoreException {
      List<LogItem> logItems = getLogItems();
      for (LogItem item : logItems) {
         if (item.getType() == matchType) {
            item.setState(newItem.getState());
            item.setUser(newItem.getUser());
            item.setDate(newItem.getDate());
            item.setMsg(newItem.getMsg());
            putLogItems(logItems);
            return;
         }
      }
   }

   /**
    * @param state name of state or null
    */
   public void addLog(LogType type, String state, String msg) throws OseeCoreException {
      addLog(type, state, msg, new Date(), UserManager.getUser());
   }

   /**
    * @param state name of state or null
    */
   public void addLog(LogType type, String state, String msg, User user) throws OseeCoreException {
      addLog(type, state, msg, new Date(), user);
   }

   public void addLogItem(LogItem item) throws OseeCoreException {
      addLog(item.getType(), item.getState(), item.getMsg(), item.getDate(), item.getUser());
   }

   public void addLog(LogType type, String state, String msg, Date date, User user) throws OseeCoreException {
      if (!enabled) {
         return;
      }
      if (artifactRef.get() == null) {
         OseeLog.log(AtsPlugin.class, Level.SEVERE, "Artifact unexpectedly garbage collected");
      } else {
         LogItem logItem = new LogItem(type, date, user, state, msg, getArtifact().getHumanReadableId());
         List<LogItem> logItems = getLogItems();
         logItems.add(logItem);
         putLogItems(logItems);
      }
   }

   public void clearLog() {
      putLogItems(new ArrayList<LogItem>());
   }

   public String getTable() throws OseeCoreException {
      StringBuilder builder = new StringBuilder();
      List<LogItem> logItems = getLogItems();
      builder.append(AHTML.beginMultiColumnTable(100, 1));
      builder.append(AHTML.addHeaderRowMultiColumnTable(Arrays.asList("Event", "State", "Message", "User", "Date")));
      for (LogItem item : logItems) {
         User user = item.getUser();
         String userStr = null;
         if (user == null) {
            userStr = item.getUserId();
         } else {
            userStr = user.getName();
         }
         builder.append(AHTML.addRowMultiColumnTable(String.valueOf(item.getType()),
            (item.getState().equals("") ? "." : item.getState()), (item.getMsg().equals("") ? "." : item.getMsg()),
            userStr, item.getDate(DateUtil.MMDDYYHHMM)));
      }
      builder.append(AHTML.endMultiColumnTable());
      return builder.toString();
   }

   public boolean isEnabled() {
      return enabled;
   }

   public void setEnabled(boolean enabled) {
      this.enabled = enabled;
   }

   public LogItem getEvent(LogType type) throws OseeCoreException {
      for (LogItem item : getLogItems()) {
         if (item.getType() == type) {
            return item;
         }
      }
      return null;
   }

   public LogItem getLastEvent(LogType type) throws OseeCoreException {
      for (LogItem item : getLogItemsReversed()) {
         if (item.getType() == type) {
            return item;
         }
      }
      return null;
   }

   public LogItem getStateEvent(LogType type, String stateName) throws OseeCoreException {
      for (LogItem item : getLogItemsReversed()) {
         if (item.getType() == type && item.getState().equals(stateName)) {
            return item;
         }
      }
      return null;
   }

   public LogItem getStateEvent(LogType type) throws OseeCoreException {
      for (LogItem item : getLogItemsReversed()) {
         if (item.getType() == type) {
            return item;
         }
      }
      return null;
   }

   public LogItem getCancelledLogItem() throws OseeCoreException {
      if (cancelledLogItem == null) {
         cancelledLogItem = getStateEvent(LogType.StateEntered, DefaultTeamState.Cancelled.name());
      }
      return cancelledLogItem;
   }

   public LogItem getCompletedLogItem() throws OseeCoreException {
      if (completedLogItem == null) {
         completedLogItem = getStateEvent(LogType.StateEntered, DefaultTeamState.Completed.name());
      }
      return completedLogItem;
   }
}