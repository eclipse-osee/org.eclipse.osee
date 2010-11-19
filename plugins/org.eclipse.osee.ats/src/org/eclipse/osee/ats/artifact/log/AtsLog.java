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

package org.eclipse.osee.ats.artifact.log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.osee.ats.internal.AtsPlugin;
import org.eclipse.osee.ats.util.TeamState;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.AHTML;
import org.eclipse.osee.framework.jdk.core.util.AXml;
import org.eclipse.osee.framework.jdk.core.util.DateUtil;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.jdk.core.util.xml.Jaxp;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * @author Donald G. Dunne
 */
public class AtsLog {

   private boolean enabled = true;
   private final static String ATS_LOG_TAG = "AtsLog";
   private final static String LOG_ITEM_TAG = "Item";
   private LogItem cancelledLogItem;
   private LogItem completedLogItem;
   private final ILogStorageProvider storeProvider;
   private final static Pattern LOG_ITEM_PATTERN =
      Pattern.compile("<Item date=\"(.*?)\" msg=\"(.*?)\" state=\"(.*?)\" type=\"(.*?)\" userId=\"(.*?)\"/>");
   private final static Pattern LOG_ITEM_TAG_PATTERN = Pattern.compile("<Item ");

   public AtsLog(ILogStorageProvider storeProvider) {
      this.storeProvider = storeProvider;
   }

   @Override
   public String toString() {
      try {
         return org.eclipse.osee.framework.jdk.core.util.Collections.toString("\n", getLogItems());
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
         sb.append(AHTML.addSpace(1) + AHTML.getLabelStr(AHTML.LABEL_FONT, storeProvider.getLogTitle()));
      }
      sb.append(getTable());
      return sb.toString();
   }

   public List<LogItem> getLogItems() throws OseeCoreException {
      String xml = storeProvider.getLogXml();
      return getLogItems(xml, storeProvider.getLogId());
   }

   private List<LogItem> getLogItems(String xml, String id) throws OseeCoreException {
      List<LogItem> logItems = new ArrayList<LogItem>();
      if (!xml.isEmpty()) {
         Matcher m = LOG_ITEM_PATTERN.matcher(xml);
         while (m.find()) {
            LogItem item = new LogItem(m.group(4), m.group(1), Strings.intern(m.group(5)), Strings.intern(m.group(3)), // NOPMD by b0727536 on 9/29/10 8:52 AM
               AXml.xmlToText(m.group(2)), id);
            logItems.add(item);
         }

         Matcher m2 = LOG_ITEM_TAG_PATTERN.matcher(xml);
         int openTagsFound = 0;
         while (m2.find()) {
            openTagsFound++;
         }
         if (logItems.size() != openTagsFound) {
            OseeLog.log(AtsPlugin.class, Level.SEVERE, String.format(
               "ATS Log: open tags found %d doesn't match log items parsed %d for %s", openTagsFound, logItems.size(),
               id));
         }
      }
      return logItems;
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
         storeProvider.saveLogXml(Jaxp.getDocumentXml(doc));
      } catch (Exception ex) {
         OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, "Can't create ats log document", ex);
      }
   }

   public List<LogItem> getLogItemsReversed() throws OseeCoreException {
      List<LogItem> logItems = getLogItems();
      Collections.reverse(logItems);
      return logItems;
   }

   /**
    * Used to reset the original originated user. Only for internal use. Kept for backward compatibility.
    */
   public void internalResetOriginator(User user) throws OseeCoreException {
      List<LogItem> logItems = getLogItems();
      for (LogItem item : logItems) {
         if (item.getType() == LogType.Originated) {
            item.setUser(user);
            putLogItems(logItems);
            return;
         }
      }
   }

   /**
    * Used to reset the original originated user. Only for internal use. Kept for backward compatibility.
    */
   public void internalResetCreatedDate(Date date) throws OseeCoreException {
      List<LogItem> logItems = getLogItems();
      for (LogItem item : logItems) {
         if (item.getType() == LogType.Originated) {
            item.setDate(date);
            putLogItems(logItems);
            return;
         }
      }
   }

   /**
    * This method is replaced by AbstractWorkflowArtifact.getCancelledFromState. Kept for backward compatibility.
    */
   public String internalGetCancelledFromState() throws OseeCoreException {
      LogItem item = getStateEvent(LogType.StateCancelled);
      if (item == null) {
         return "";
      }
      return item.getState();
   }

   /**
    * This method is replaced by AbstractWorkflowArtifact.getCompletedFromState. Kept for backward compatibility.
    */
   public String internalGetCompletedFromState() throws OseeCoreException {
      LogItem item = getStateEvent(LogType.StateComplete);
      if (item == null) {
         return "";
      }
      return item.getState();
   }

   /**
    * This method is replaced by AbstractWorkflowArtifact.setCompletedFromState. Kept for backward compatibility.
    */
   public void internalSetCancellationReason(String reason) throws OseeCoreException {
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
    * Since originator can be changed, return the date of the first originated log item. Kept for backward
    * compatibility.
    */
   public Date internalGetCreationDate() throws OseeCoreException {
      LogItem logItem = getEvent(LogType.Originated);
      if (logItem == null) {
         return null;
      }
      return logItem.getDate();
   }

   /**
    * Since originator change be changed, return the last originated event's user. Kept for backward compatibility.
    */
   public User internalGetOriginator() throws OseeCoreException {
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
      LogItem logItem = new LogItem(type, date, user, state, msg, storeProvider.getLogId());
      List<LogItem> logItems = getLogItems();
      logItems.add(logItem);
      putLogItems(logItems);
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

   /**
    * This method is replaced by Cancelled Date, By and Reason attributes. It will not work with multiple cancelled
    * state design
    */
   public LogItem internalGetCancelledLogItem() throws OseeCoreException {
      if (cancelledLogItem == null) {
         cancelledLogItem = getStateEvent(LogType.StateEntered, TeamState.Cancelled.getPageName());
      }
      return cancelledLogItem;
   }

   /**
    * This method is replaced by Completed Date, By attributes. It will not work with multiple completed state design
    */
   public LogItem internalGetCompletedLogItem() throws OseeCoreException {
      if (completedLogItem == null) {
         completedLogItem = getStateEvent(LogType.StateEntered, TeamState.Completed.getPageName());
      }
      return completedLogItem;
   }
}