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

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.framework.jdk.core.util.AHTML;
import org.eclipse.osee.framework.jdk.core.util.xml.Jaxp;
import org.eclipse.osee.framework.skynet.core.SkynetAuthentication;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.exception.MultipleAttributesExist;
import org.eclipse.osee.framework.skynet.core.exception.OseeCoreException;
import org.eclipse.osee.framework.ui.skynet.util.OSEELog;
import org.eclipse.osee.framework.ui.skynet.widgets.XDate;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

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

      public static LogType getType(String type) {
         for (Enum<LogType> e : LogType.values()) {
            if (e.name().equals(type)) return (LogType) e;
         }
         throw new IllegalArgumentException("Unhandled LogType");
      }

   };

   public ATSLog(Artifact artifact) {
      this.artifact = artifact;
   }

   @Override
   public String toString() {
      return getLogItems().toString();
   }

   public String getHtml() {
      return getHtml(true);
   }

   public String getHtml(boolean showLog) {
      if (getLogItems().size() == 0) return "";
      StringBuffer sb = new StringBuffer();
      if (showLog) sb.append(AHTML.addSpace(1) + AHTML.getLabelStr(
            AHTML.LABEL_FONT,
            "History for \"" + artifact.getArtifactTypeName() + "\" - " + artifact.getHumanReadableId() + " - titled \"" + artifact.getDescriptiveName() + "\""));
      sb.append(getTable());
      return sb.toString();
   }

   public List<LogItem> getLogItems() {
      List<LogItem> logItems = new ArrayList<LogItem>();
      try {
         String xml = artifact.getSoleAttributeValue(ATSAttributes.LOG_ATTRIBUTE.getStoreName(), "");
         if (!xml.equals("")) {
            NodeList nodes = Jaxp.readXmlDocument(xml).getElementsByTagName(LOG_ITEM_TAG);
            for (int i = 0; i < nodes.getLength(); i++) {
               Element element = (Element) nodes.item(i);
               LogItem item =
                     new LogItem(element.getAttribute("type"), element.getAttribute("date"),
                           element.getAttribute("userId"), element.getAttribute("state"), element.getAttribute("msg"));
               logItems.add(item);
            }
         }
      } catch (Exception ex) {
         OSEELog.logException(AtsPlugin.class, "Error Parsing ATS Log for " + artifact.getHumanReadableId(), ex, true);
      }
      return logItems;
   }

   public Date getLastStatusedDate() throws OseeCoreException, SQLException {
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

   public List<LogItem> getLogItemsReversed() {
      List<LogItem> logItems = getLogItems();
      Collections.reverse(logItems);
      return logItems;
   }

   public void setOriginator(User user) {
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
    * @return
    */
   public Date getCreationDate() {
      LogItem logItem = getEvent(LogType.Originated);
      if (logItem == null) return null;
      return logItem.getDate();
   }

   /**
    * Since originator change be changed, return the last originated event's user
    * 
    * @return
    */
   public User getOriginator() {
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
   public void overrideStateItemData(LogType matchType, String matchState, LogItem newItem) {
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
   public void overrideItemData(LogType matchType, LogItem newItem) {
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

   public void addLog(LogType type, String state, String msg) throws IllegalStateException, SQLException, MultipleAttributesExist {
      addLog(type, state, msg, new Date(), SkynetAuthentication.getUser());
   }

   public void addLog(LogType type, String state, String msg, User user) throws SQLException, MultipleAttributesExist {
      addLog(type, state, msg, new Date(), user);
   }

   public void addLogItem(LogItem item) throws SQLException, MultipleAttributesExist {
      addLog(item.getType(), item.getState(), item.getMsg(), item.getDate(), item.getUser());
   }

   public void addLog(LogType type, String state, String msg, Date date, User user) throws SQLException, MultipleAttributesExist {
      if (!enabled) return;
      LogItem logItem = new LogItem(type, date, user, state, msg);
      List<LogItem> logItems = getLogItems();
      logItems.add(logItem);
      putLogItems(logItems);
   }

   public void clearLog() {
      putLogItems(new ArrayList<LogItem>());
   }

   public String getTable() {
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
         if (user.equals(SkynetAuthentication.getUser()))
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

   public LogItem getEvent(LogType type) {
      for (LogItem item : getLogItems())
         if (item.getType() == type) return item;
      return null;
   }

   public LogItem getLastEvent(LogType type) {
      for (LogItem item : getLogItemsReversed())
         if (item.getType() == type) return item;
      return null;
   }

   public LogItem getStateEvent(LogType type, String stateName) {
      for (LogItem item : getLogItemsReversed())
         if (item.getType() == type && item.getState().equals(stateName)) return item;
      return null;
   }

   public LogItem getStateEvent(LogType type) {
      for (LogItem item : getLogItemsReversed())
         if (item.getType() == type) return item;
      return null;
   }

}