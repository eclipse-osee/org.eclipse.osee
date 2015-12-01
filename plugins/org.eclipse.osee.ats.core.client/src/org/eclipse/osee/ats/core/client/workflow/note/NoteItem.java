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

package org.eclipse.osee.ats.core.client.workflow.note;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import org.eclipse.osee.ats.api.user.IAtsUser;
import org.eclipse.osee.ats.core.client.internal.Activator;
import org.eclipse.osee.ats.core.client.internal.AtsClientService;
import org.eclipse.osee.ats.core.users.AtsCoreUsers;
import org.eclipse.osee.framework.core.exception.UserNotInDatabase;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.DateUtil;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.jdk.core.util.xml.Jaxp;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * @author Donald G. Dunne
 */
public class NoteItem {

   private Date date;
   private final String state;
   private String msg;
   private IAtsUser user;
   private NoteType type = NoteType.Other;
   protected final static String LOG_ITEM_TAG = "Item";
   private final static String ATS_NOTE_TAG = "AtsNote";

   public NoteItem(NoteType type, String state, String date, IAtsUser user, String msg) {
      Long l = Long.valueOf(date);
      this.date = new Date(l.longValue());
      this.state = Strings.intern(state);
      this.msg = msg;
      this.user = user;
      this.type = type;
   }

   public NoteItem(String type, String state, String date, IAtsUser user, String msg) throws OseeCoreException {
      this(NoteType.getType(type), state, date, user, msg);
   }

   public Date getDate() {
      return date;
   }

   public void setDate(Date date) {
      this.date = date;
   }

   public String getMsg() {
      return msg;
   }

   public void setMsg(String msg) {
      this.msg = msg;
   }

   @Override
   public String toString() {
      return String.format("Note: %s from %s%s on %s - %s", type, user.getName(), toStringState(),
         DateUtil.getMMDDYYHHMM(date), msg);
   }

   private String toStringState() {
      return (state.isEmpty() ? "" : " for \"" + state + "\"");
   }

   public IAtsUser getUser() {
      return user;
   }

   public NoteType getType() {
      return type;
   }

   public void setType(NoteType type) {
      this.type = type;
   }

   public String toHTML() {
      return toString().replaceFirst("^Note: ", "<b>Note:</b>");
   }

   public String getState() {
      return state;
   }

   public void setUser(IAtsUser user) {
      this.user = user;
   }

   public static List<NoteItem> fromXml(String xml, String atsId) {
      List<NoteItem> logItems = new ArrayList<>();
      try {
         if (Strings.isValid(xml)) {
            NodeList nodes = Jaxp.readXmlDocument(xml).getElementsByTagName(LOG_ITEM_TAG);
            for (int i = 0; i < nodes.getLength(); i++) {
               Element element = (Element) nodes.item(i);
               try {
                  IAtsUser user = AtsClientService.get().getUserService().getUserById(element.getAttribute("userId"));
                  NoteItem item = new NoteItem(element.getAttribute("type"), element.getAttribute("state"), // NOPMD by b0727536 on 9/29/10 8:52 AM
                     element.getAttribute("date"), user, element.getAttribute("msg"));
                  logItems.add(item);
               } catch (UserNotInDatabase ex) {
                  OseeLog.logf(Activator.class, Level.SEVERE, ex, "Error parsing notes for [%s]", atsId);
                  NoteItem item =
                     new NoteItem(element.getAttribute("type"),
                        element.getAttribute("state"), // NOPMD by b0727536 on 9/29/10 8:52 AM
                        element.getAttribute("date"), AtsCoreUsers.ANONYMOUS_USER,
                        element.getAttribute("msg"));
                  logItems.add(item);
               }
            }
         }
      } catch (Exception ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }
      return logItems;
   }

   public static String toXml(List<NoteItem> items) {
      try {
         Document doc = Jaxp.newDocumentNamespaceAware();
         Element rootElement = doc.createElement(ATS_NOTE_TAG);
         doc.appendChild(rootElement);
         for (NoteItem item : items) {
            Element element = doc.createElement(NoteItem.LOG_ITEM_TAG);
            element.setAttribute("type", item.getType().name());
            element.setAttribute("state", item.getState());
            element.setAttribute("date", String.valueOf(item.getDate().getTime()));
            element.setAttribute("userId", item.getUser().getUserId());
            element.setAttribute("msg", item.getMsg());
            rootElement.appendChild(element);
         }
         return Jaxp.getDocumentXml(doc);
      } catch (Exception ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, "Can't create ats note document", ex);
      }
      return null;
   }

}