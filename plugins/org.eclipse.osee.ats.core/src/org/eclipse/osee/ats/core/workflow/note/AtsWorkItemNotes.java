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
package org.eclipse.osee.ats.core.workflow.note;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.user.AtsCoreUsers;
import org.eclipse.osee.ats.api.user.IAtsUser;
import org.eclipse.osee.ats.api.workflow.note.IAtsWorkItemNotes;
import org.eclipse.osee.ats.api.workflow.note.NoteItem;
import org.eclipse.osee.ats.api.workflow.note.NoteType;
import org.eclipse.osee.framework.core.exception.UserNotInDatabase;
import org.eclipse.osee.framework.jdk.core.util.AHTML;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.jdk.core.util.xml.Jaxp;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * @author Donald G. Dunne
 */
public class AtsWorkItemNotes implements IAtsWorkItemNotes {
   private boolean enabled = true;
   private final INoteStorageProvider storeProvder;
   public final static String LOG_ITEM_TAG = "Item";
   public final static String ATS_NOTE_TAG = "AtsNote";
   private final AtsApi atsApi;

   public AtsWorkItemNotes(INoteStorageProvider storeProvder, AtsApi atsApi) {
      this.storeProvder = storeProvder;
      this.atsApi = atsApi;
   }

   @Override
   public void addNote(NoteType type, String state, String msg, IAtsUser user) {
      addNote(type, state, msg, new Date(), user);
   }

   @Override
   public void addNoteItem(NoteItem noteItem) {
      addNote(noteItem.getType(), noteItem.getState(), noteItem.getMsg(), noteItem.getDate(), noteItem.getUser());
   }

   @Override
   public void addNote(NoteType type, String state, String msg, Date date, IAtsUser user) {
      if (!enabled) {
         return;
      }
      NoteItem logItem = new NoteItem(type, state, String.valueOf(date.getTime()), user, msg);
      List<NoteItem> logItems = getNoteItems();
      if (logItems.isEmpty()) {
         logItems = Arrays.asList(logItem);
      } else {
         logItems.add(logItem);
      }
      saveNoteItems(logItems);
   }

   @Override
   public List<NoteItem> getNoteItems() {
      try {
         String xml = storeProvder.getNoteXml();
         if (Strings.isValid(xml)) {
            return fromXml(xml, storeProvder.getNoteId(), atsApi);
         }
      } catch (Exception ex) {
         atsApi.getLogger().error(ex, "Error extracting note");
      }
      return Collections.emptyList();
   }

   public void saveNoteItems(List<NoteItem> items) {
      try {
         String xml = toXml(items, atsApi);
         storeProvder.saveNoteXml(xml);
      } catch (Exception ex) {
         atsApi.getLogger().error(ex, "Error saving note");
      }
   }

   /**
    * Display Note Table; If state == null, only display non-state notes Otherwise, show only notes associated with
    * state
    */
   @Override
   public String getTable(String state) {
      if (!storeProvder.isNoteable()) {
         return "";
      }
      ArrayList<NoteItem> showNotes = new ArrayList<>();
      List<NoteItem> noteItems = getNoteItems();

      for (NoteItem li : noteItems) {
         if (state == null && li.getState().equals("")) {
            showNotes.add(li);
         } else if (state != null && ("ALL".equals(state) || li.getState().equals(state))) {
            showNotes.add(li);
         }
      }
      if (showNotes.isEmpty()) {
         return "";
      }
      return buildTable(showNotes);
   }

   private String buildTable(List<NoteItem> showNotes) {
      StringBuilder builder = new StringBuilder();
      builder.append(AHTML.beginMultiColumnTable(100, 1));
      builder.append(AHTML.addHeaderRowMultiColumnTable(Arrays.asList("Type", "State", "Message", "User", "Date")));
      DateFormat dateFormat = getDateFormat();
      for (NoteItem note : showNotes) {
         IAtsUser user = note.getUser();
         String name = "";
         if (user != null) {
            name = user.getName();
            if (!Strings.isValid(name)) {
               name = user.getName();
            }
         }
         builder.append(AHTML.addRowMultiColumnTable(String.valueOf(note.getType()),
            note.getState().isEmpty() ? "," : note.getState(), note.getMsg().equals("") ? "," : note.getMsg(), name,
            dateFormat.format(note.getDate())));
      }
      builder.append(AHTML.endMultiColumnTable());
      return builder.toString();
   }

   public DateFormat getDateFormat() {
      return new SimpleDateFormat("MM/dd/yyyy h:mm a", Locale.US);
   }

   public boolean isEnabled() {
      return enabled;
   }

   public void setEnabled(boolean enabled) {
      this.enabled = enabled;
   }

   public static List<NoteItem> fromXml(String xml, String atsId, AtsApi atsApi) {
      List<NoteItem> logItems = new ArrayList<>();
      try {
         if (Strings.isValid(xml)) {
            NodeList nodes = Jaxp.readXmlDocument(xml).getElementsByTagName(LOG_ITEM_TAG);
            for (int i = 0; i < nodes.getLength(); i++) {
               Element element = (Element) nodes.item(i);
               try {
                  IAtsUser user = atsApi.getUserService().getUserById(element.getAttribute("userId"));
                  NoteItem item = new NoteItem(element.getAttribute("type"), element.getAttribute("state"), // NOPMD by b0727536 on 9/29/10 8:52 AM
                     element.getAttribute("date"), user, element.getAttribute("msg"));
                  logItems.add(item);
               } catch (UserNotInDatabase ex) {
                  atsApi.getLogger().error(ex, "Error parsing notes for [%s]", atsId);
                  NoteItem item = new NoteItem(element.getAttribute("type"), element.getAttribute("state"), // NOPMD by b0727536 on 9/29/10 8:52 AM
                     element.getAttribute("date"), AtsCoreUsers.ANONYMOUS_USER, element.getAttribute("msg"));
                  logItems.add(item);
               }
            }
         }
      } catch (Exception ex) {
         atsApi.getLogger().error(ex, "Error reading AtsNote");
      }
      return logItems;
   }

   public static String toXml(List<NoteItem> items, AtsApi atsApi) {
      try {
         Document doc = Jaxp.newDocumentNamespaceAware();
         Element rootElement = doc.createElement(ATS_NOTE_TAG);
         doc.appendChild(rootElement);
         for (NoteItem item : items) {
            Element element = doc.createElement(LOG_ITEM_TAG);
            element.setAttribute("type", item.getType().name());
            element.setAttribute("state", item.getState());
            element.setAttribute("date", String.valueOf(item.getDate().getTime()));
            element.setAttribute("userId", item.getUser().getUserId());
            element.setAttribute("msg", item.getMsg());
            rootElement.appendChild(element);
         }
         return Jaxp.getDocumentXml(doc);
      } catch (Exception ex) {
         atsApi.getLogger().error(ex, "Error writing AtsNote");
      }
      return null;
   }

}