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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.framework.db.connection.exception.MultipleAttributesExist;
import org.eclipse.osee.framework.db.connection.exception.OseeArgumentException;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.AHTML;
import org.eclipse.osee.framework.jdk.core.util.xml.Jaxp;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.util.OSEELog;
import org.eclipse.osee.framework.ui.skynet.widgets.XDate;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * @author Donald G. Dunne
 */
public class ATSLog {

   private final Artifact artifact;
   private boolean enabled = true;
   private static String ATS_LOG_TAG = "AtsLog";
   private static String LOG_ITEM_TAG = "Item";
   public static enum LogType {
      None, Originated, StateComplete, StateCancelled, StateEntered, Released, Error, Assign, Note, Metrics;

      public static LogType getType(String type) throws OseeArgumentException {
         for (Enum<LogType> e : LogType.values()) {
            if (e.name().equals(type)) return (LogType) e;
         }
         throw new OseeArgumentException("Unhandled LogType: \"" + type + "\"");
      }

   };

   public ATSLog(Artifact artifact) {
      this.artifact = artifact;
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
      if (getLogItems().size() == 0) return "";
      StringBuffer sb = new StringBuffer();
      if (showLog) sb.append(AHTML.addSpace(1) + AHTML.getLabelStr(
            AHTML.LABEL_FONT,
            "History for \"" + artifact.getArtifactTypeName() + "\" - " + artifact.getHumanReadableId() + " - titled \"" + artifact.getDescriptiveName() + "\""));
      sb.append(getTable());
      return sb.toString();
   }

   private static Pattern LOG_ITEM_PATTERN =
         Pattern.compile("<Item date=\"(.*?)\" msg=\"(.*?)\" state=\"(.*?)\" type=\"(.*?)\" userId=\"(.*?)\"/>");

   private static Pattern LOG_ITEM_TAG_PATTERN = Pattern.compile("<Item ");

   public List<LogItem> getLogItems() throws OseeCoreException {
      //      System.out.println("getLogItems " + artifact.getHumanReadableId());
      List<LogItem> logItems = new ArrayList<LogItem>();
      String xml = artifact.getSoleAttributeValue(ATSAttributes.LOG_ATTRIBUTE.getStoreName(), "");
      if (!xml.equals("")) {
         Matcher m = LOG_ITEM_PATTERN.matcher(xml);
         while (m.find()) {
            LogItem item = new LogItem(m.group(4), m.group(1), m.group(5), m.group(3), m.group(2));
            logItems.add(item);
         }

         Matcher m2 = LOG_ITEM_TAG_PATTERN.matcher(xml);
         int openTagsFound = 0;
         while (m2.find())
            openTagsFound++;
         if (logItems.size() != openTagsFound) {
            OseeLog.log(AtsPlugin.class, Level.SEVERE, String.format(
                  "ATS Log: open tags found %d doesn't match log items parsed %d for %s", openTagsFound,
                  logItems.size(), artifact.getHumanReadableId()));
         }
      }
      return logItems;
   }

   public Date getLastStatusedDate() throws OseeCoreException {
      LogItem logItem = getLastEvent(LogType.Metrics);
      if (logItem == null) return null;
      return logItem.getDate();
   }

   public void putLogItems(List<LogItem> items) {
      try {
         Document doc = Jaxp.newDocument();
         Element rootElement = doc.createElement(ATS_LOG_TAG);
         doc.appendChild(rootElement);
         for (LogItem item : items) {
            Element element = doc.createElement(LOG_ITEM_TAG);
            element.setAttribute("type", item.getType().name());
            element.setAttribute("date", item.getDate().getTime() + "");
            element.setAttribute("userId", item.getUser().getUserId());
            element.setAttribute("state", item.getState());
            element.setAttribute("msg", item.getMsg());
            rootElement.appendChild(element);
         }
         artifact.setSoleAttributeValue(ATSAttributes.LOG_ATTRIBUTE.getStoreName(), Jaxp.getDocumentXml(doc));
      } catch (Exception ex) {
         OSEELog.logException(AtsPlugin.class, "Can't create ats log document", ex, true);
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

   /**
    * Since originator can be changed, return the date of the first originated log item
    * 
    * @return Date
    */
   public Date getCreationDate() throws OseeCoreException {
      LogItem logItem = getEvent(LogType.Originated);
      if (logItem == null) return null;
      return logItem.getDate();
   }

   /**
    * Since originator change be changed, return the last originated event's user
    * 
    * @return User
    */
   public User getOriginator() throws OseeCoreException {
      LogItem logItem = getLastEvent(LogType.Originated);
      if (logItem == null) return null;
      return logItem.getUser();
   }

   /**
    * Overwrite the first logItem to match type and state with newItem data
    * 
    * @param matchType
    * @param matchState
    * @param newItem
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
    * 
    * @param matchType
    * @param newItem
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
    * @param type
    * @param state name of state or null
    * @param msg
    * @throws OseeArgumentException
    * @throws MultipleAttributesExist
    */
   public void addLog(LogType type, String state, String msg) throws OseeCoreException {
      addLog(type, state, msg, new Date(), UserManager.getUser());
   }

   /**
    * @param type
    * @param state name of state or null
    * @param msg
    * @param user
    * @throws MultipleAttributesExist
    */
   public void addLog(LogType type, String state, String msg, User user) throws OseeCoreException {
      addLog(type, state, msg, new Date(), user);
   }

   public void addLogItem(LogItem item) throws OseeCoreException {
      addLog(item.getType(), item.getState(), item.getMsg(), item.getDate(), item.getUser());
   }

   /**
    * @param type
    * @param state name of state or null
    * @param msg
    * @param date
    * @param user
    * @throws MultipleAttributesExist
    */
   public void addLog(LogType type, String state, String msg, Date date, User user) throws OseeCoreException {
      if (!enabled) return;
      LogItem logItem = new LogItem(type, date, user, state, msg);
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
      builder.append("<TABLE BORDER=\"1\" cellspacing=\"1\" cellpadding=\"3%\" width=\"100%\"><THEAD><TR><TH>Event</TH>" + "<TH>State</TH><TH>Message</TH><TH>User</TH><TH>Date</TH></THEAD></TR>");
      for (LogItem item : logItems) {
         User user = item.getUser();
         String name = "";
         if (user != null) {
            name = user.getName();
            if (name == null || name.equals("")) {
               name = user.getName();
            }
         }
         builder.append("<TR>");
         builder.append("<TD>" + item.getType() + "</TD>");
         builder.append("<TD>" + (item.getState().equals("") ? "." : item.getState()) + "</TD>");
         builder.append("<TD>" + (item.getMsg().equals("") ? "." : item.getMsg()) + "</TD>");
         if (user.equals(UserManager.getUser()))
            builder.append("<TD bgcolor=\"#CCCCCC\">" + name + "</TD>");
         else
            builder.append("<TD>" + name + "</TD>");
         builder.append("<TD>" + item.getDate(XDate.MMDDYYHHMM) + "</TD>");
         builder.append("</TR>");
      }
      builder.append("</TABLE>");
      return builder.toString();
   }

   public boolean isEnabled() {
      return enabled;
   }

   public void setEnabled(boolean enabled) {
      this.enabled = enabled;
   }

   public LogItem getEvent(LogType type) throws OseeCoreException {
      for (LogItem item : getLogItems())
         if (item.getType() == type) return item;
      return null;
   }

   public LogItem getLastEvent(LogType type) throws OseeCoreException {
      for (LogItem item : getLogItemsReversed())
         if (item.getType() == type) return item;
      return null;
   }

   public LogItem getStateEvent(LogType type, String stateName) throws OseeCoreException {
      for (LogItem item : getLogItemsReversed())
         if (item.getType() == type && item.getState().equals(stateName)) return item;
      return null;
   }

   public LogItem getStateEvent(LogType type) throws OseeCoreException {
      for (LogItem item : getLogItemsReversed())
         if (item.getType() == type) return item;
      return null;
   }

}