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

package org.eclipse.osee.ats.core.internal.log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.osee.ats.api.user.IAtsUser;
import org.eclipse.osee.ats.api.user.IAtsUserService;
import org.eclipse.osee.ats.api.workflow.log.IAtsLog;
import org.eclipse.osee.ats.api.workflow.log.IAtsLogItem;
import org.eclipse.osee.ats.api.workflow.log.ILogStorageProvider;
import org.eclipse.osee.ats.api.workflow.log.LogType;
import org.eclipse.osee.ats.core.AtsCore;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.AHTML;
import org.eclipse.osee.framework.jdk.core.util.AXml;
import org.eclipse.osee.framework.jdk.core.util.DateUtil;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.jdk.core.util.xml.Jaxp;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * @author Donald G. Dunne
 */
public class AtsLog implements IAtsLog {

   private final static String ATS_LOG_TAG = "AtsLog";
   private final static String LOG_ITEM_TAG = "Item";
   private final ILogStorageProvider storeProvider;
   private final static Pattern LOG_ITEM_PATTERN =
      Pattern.compile("<Item date=\"(.*?)\" msg=\"(.*?)\" state=\"(.*?)\" type=\"(.*?)\" userId=\"(.*?)\"/>");
   private final static Pattern LOG_ITEM_TAG_PATTERN = Pattern.compile("<Item ");
   private final IAtsUserService userService;

   public AtsLog(ILogStorageProvider storeProvider, IAtsUserService userService) {
      this.storeProvider = storeProvider;
      this.userService = userService;
   }

   @Override
   public String toString() {
      try {
         return org.eclipse.osee.framework.jdk.core.util.Collections.toString("\n", getLogItems());
      } catch (Exception ex) {
         OseeLog.log(AtsCore.class, Level.SEVERE, ex);
         return ex.getLocalizedMessage();
      }
   }

   @Override
   public String getHtml() throws OseeCoreException {
      return getHtml(true);
   }

   @Override
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

   @Override
   public List<IAtsLogItem> getLogItems() throws OseeCoreException {
      String xml = storeProvider.getLogXml();
      return getLogItems(xml, storeProvider.getLogId());
   }

   private List<IAtsLogItem> getLogItems(String xml, String id) throws OseeCoreException {
      List<IAtsLogItem> logItems = new ArrayList<IAtsLogItem>();
      if (!xml.isEmpty()) {
         Matcher m = LOG_ITEM_PATTERN.matcher(xml);
         while (m.find()) {
            IAtsLogItem item =
               new LogItem(m.group(4), m.group(1), Strings.intern(m.group(5)), Strings.intern(m.group(3)), // NOPMD by b0727536 on 9/29/10 8:52 AM
                  AXml.xmlToText(m.group(2)), id, userService);
            logItems.add(item);
         }

         Matcher m2 = LOG_ITEM_TAG_PATTERN.matcher(xml);
         int openTagsFound = 0;
         while (m2.find()) {
            openTagsFound++;
         }
         if (logItems.size() != openTagsFound) {
            OseeLog.logf(AtsCore.class, Level.SEVERE,
               "ATS Log: open tags found %d doesn't match log items parsed %d for %s", openTagsFound, logItems.size(),
               id);
         }
      }
      return logItems;
   }

   @Override
   public Date getLastStatusDate() throws OseeCoreException {
      IAtsLogItem logItem = getLastEvent(LogType.Metrics);
      if (logItem == null) {
         return null;
      }
      return logItem.getDate();
   }

   @Override
   public void putLogItems(List<IAtsLogItem> items) {
      try {
         Document doc = Jaxp.newDocumentNamespaceAware();
         Element rootElement = doc.createElement(ATS_LOG_TAG);
         doc.appendChild(rootElement);
         for (IAtsLogItem item : items) {
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
         OseeLog.log(AtsCore.class, OseeLevel.SEVERE_POPUP, "Can't create ats log document", ex);
      }
   }

   @Override
   public List<IAtsLogItem> getLogItemsReversed() throws OseeCoreException {
      List<IAtsLogItem> logItems = getLogItems();
      Collections.reverse(logItems);
      return logItems;
   }

   /**
    * Used to reset the original originated user. Only for internal use. Kept for backward compatibility.
    */
   @Override
   public void internalResetOriginator(IAtsUser user) throws OseeCoreException {
      List<IAtsLogItem> logItems = getLogItems();
      for (IAtsLogItem item : logItems) {
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
   @Override
   public void internalResetCreatedDate(Date date) throws OseeCoreException {
      List<IAtsLogItem> logItems = getLogItems();
      for (IAtsLogItem item : logItems) {
         if (item.getType() == LogType.Originated) {
            item.setDate(date);
            putLogItems(logItems);
            return;
         }
      }
   }

   @Override
   public String internalGetCancelledReason() throws OseeCoreException {
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
   public String internalGetCompletedFromState() throws OseeCoreException {
      IAtsLogItem item = getStateEvent(LogType.StateComplete);
      if (item == null) {
         return "";
      }
      return item.getState();
   }

   /**
    * @param state name of state or null
    */
   @Override
   public void addLog(LogType type, String state, String msg) throws OseeCoreException {
      addLog(type, state, msg, new Date(), userService.getCurrentUser());
   }

   @Override
   public void addLogItem(IAtsLogItem item) throws OseeCoreException {
      addLog(item.getType(), item.getState(), item.getMsg(), item.getDate(), item.getUser());
   }

   @Override
   public void addLog(LogType type, String state, String msg, Date date, IAtsUser user) throws OseeCoreException {
      LogItem logItem = new LogItem(type, date, user, state, msg, storeProvider.getLogId(), userService);
      List<IAtsLogItem> logItems = getLogItems();
      logItems.add(logItem);
      putLogItems(logItems);
   }

   @Override
   public void clearLog() {
      putLogItems(new ArrayList<IAtsLogItem>());
   }

   @Override
   public String getTable() throws OseeCoreException {
      StringBuilder builder = new StringBuilder();
      List<IAtsLogItem> logItems = getLogItems();
      builder.append(AHTML.beginMultiColumnTable(100, 1));
      builder.append(AHTML.addHeaderRowMultiColumnTable(Arrays.asList("Event", "State", "Message", "User", "Date")));
      for (IAtsLogItem item : logItems) {
         IAtsUser user = item.getUser();
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

   @Override
   public IAtsLogItem getLastEvent(LogType type) throws OseeCoreException {
      for (IAtsLogItem item : getLogItemsReversed()) {
         if (item.getType() == type) {
            return item;
         }
      }
      return null;
   }

   @Override
   public IAtsLogItem getStateEvent(LogType type, String stateName) throws OseeCoreException {
      for (IAtsLogItem item : getLogItemsReversed()) {
         if (item.getType() == type && item.getState().equals(stateName)) {
            return item;
         }
      }
      return null;
   }

   @Override
   public IAtsLogItem getStateEvent(LogType type) throws OseeCoreException {
      for (IAtsLogItem item : getLogItemsReversed()) {
         if (item.getType() == type) {
            return item;
         }
      }
      return null;
   }

}