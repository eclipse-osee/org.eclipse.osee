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
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.workflow.note.AtsStateNoteXml;
import org.eclipse.osee.ats.api.workflow.note.AtsStateNoteXmlType;
import org.eclipse.osee.framework.core.data.UserToken;
import org.eclipse.osee.framework.core.enums.SystemUser;
import org.eclipse.osee.framework.core.exception.UserNotInDatabase;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.AHTML;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.jdk.core.util.xml.Jaxp;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * @author Donald G. Dunne
 */
public class StateNotesXml {
   private final IAtsWorkItem workItem;
   public final static String LOG_ITEM_TAG = "Item";
   public final static String ATS_NOTE_TAG = "AtsNote";
   private final AtsApi atsApi;

   public StateNotesXml(IAtsWorkItem workItem, AtsApi atsApi) {
      this.workItem = workItem;
      this.atsApi = atsApi;
   }

   public void addNote(AtsStateNoteXmlType type, String state, String msg) {
      addNote(type, state, msg, new Date(), atsApi.userService().getUser());
   }

   public void addNoteItem(AtsStateNoteXml noteItem) {
      addNote(noteItem.getType(), noteItem.getState(), noteItem.getMsg(), noteItem.getDate(), noteItem.getUser());
   }

   public void addNote(AtsStateNoteXmlType type, String state, String msg, Date date, UserToken user) {
      AtsStateNoteXml logItem = new AtsStateNoteXml(type, state, String.valueOf(date.getTime()), user, msg);
      List<AtsStateNoteXml> logItems = getNoteItems();
      if (logItems.isEmpty()) {
         logItems = Arrays.asList(logItem);
      } else {
         logItems.add(logItem);
      }
      saveNoteItems(logItems);
   }

   public List<AtsStateNoteXml> getNoteItems() {
      try {
         String xml = getNoteXml();
         if (Strings.isValid(xml)) {
            return fromXml(xml, getNoteId(), atsApi);
         }
      } catch (Exception ex) {
         atsApi.getLogger().error(ex, "Error extracting note");
      }
      return Collections.emptyList();
   }

   public void saveNoteItems(List<AtsStateNoteXml> items) {
      try {
         String xml = toXml(items, atsApi);
         saveNoteXml(xml);
      } catch (Exception ex) {
         atsApi.getLogger().error(ex, "Error saving note");
      }
   }

   /**
    * Display Note Table; If state == null, only display non-state notes Otherwise, show only notes associated with
    * state
    */
   public String getTable(String state) {
      if (!isNoteable()) {
         return "";
      }
      ArrayList<AtsStateNoteXml> showNotes = new ArrayList<>();
      List<AtsStateNoteXml> noteItems = getNoteItems();

      for (AtsStateNoteXml li : noteItems) {
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

   private String buildTable(List<AtsStateNoteXml> showNotes) {
      StringBuilder builder = new StringBuilder();
      builder.append(AHTML.beginMultiColumnTable(100, 1));
      builder.append(AHTML.addHeaderRowMultiColumnTable(Arrays.asList("Type", "State", "Message", "User", "Date")));
      DateFormat dateFormat = getDateFormat();
      for (AtsStateNoteXml note : showNotes) {
         UserToken user = note.getUser();
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

   public static List<AtsStateNoteXml> fromXml(String xml, String atsId, AtsApi atsApi) {
      List<AtsStateNoteXml> logItems = new ArrayList<>();
      try {
         if (Strings.isValid(xml)) {
            NodeList nodes = Jaxp.readXmlDocument(xml).getElementsByTagName(LOG_ITEM_TAG);
            for (int i = 0; i < nodes.getLength(); i++) {
               Element element = (Element) nodes.item(i);
               try {
                  UserToken user = atsApi.userService().getUserByUserId(element.getAttribute("userId"));
                  AtsStateNoteXml item =
                     new AtsStateNoteXml(element.getAttribute("type"), element.getAttribute("state"), // NOPMD by b0727536 on 9/29/10 8:52 AM
                        element.getAttribute("date"), user, element.getAttribute("msg"));
                  logItems.add(item);
               } catch (UserNotInDatabase ex) {
                  atsApi.getLogger().error(ex, "Error parsing notes for [%s]", atsId);
                  AtsStateNoteXml item =
                     new AtsStateNoteXml(element.getAttribute("type"), element.getAttribute("state"), // NOPMD by b0727536 on 9/29/10 8:52 AM
                        element.getAttribute("date"), SystemUser.OseeSystem, element.getAttribute("msg"));
                  logItems.add(item);
               }
            }
         }
      } catch (Exception ex) {
         atsApi.getLogger().error(ex, "Error reading AtsNote");
      }
      return logItems;
   }

   public static String toXml(List<AtsStateNoteXml> items, AtsApi atsApi) {
      try {
         Document doc = Jaxp.newDocumentNamespaceAware();
         Element rootElement = doc.createElement(ATS_NOTE_TAG);
         doc.appendChild(rootElement);
         for (AtsStateNoteXml item : items) {
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

   public String getNoteXml() {
      return atsApi.getAttributeResolver().getSoleAttributeValue(workItem, AtsAttributeTypes.StateNotes, "");
   }

   public Result saveNoteXml(String xml) {
      try {
         atsApi.getAttributeResolver().setSoleAttributeValue(workItem, AtsAttributeTypes.StateNotes, xml);
         return Result.TrueResult;
      } catch (OseeCoreException ex) {
         atsApi.getLogger().error(ex, "Error saving note xml");
         return new Result(false, "saveLogXml exception " + ex.getLocalizedMessage());
      }
   }

   public String getNoteTitle() {
      return "History for \"" + atsApi.getStoreService().getArtifactType(
         workItem.getStoreObject()).getName() + "\" - " + getNoteId() + " - titled \"" + workItem.getName() + "\"";
   }

   public String getNoteId() {
      return workItem.getAtsId();
   }

   public boolean isNoteable() {
      return atsApi.getStoreService().isAttributeTypeValid(workItem, AtsAttributeTypes.StateNotes);
   }

}