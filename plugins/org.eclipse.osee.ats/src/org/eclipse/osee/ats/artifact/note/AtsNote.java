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
package org.eclipse.osee.ats.artifact.note;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import org.eclipse.osee.ats.NoteType;
import org.eclipse.osee.ats.internal.AtsPlugin;
import org.eclipse.osee.framework.jdk.core.util.AHTML;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.User;

/**
 * @author Donald G. Dunne
 */
public class AtsNote {
   private boolean enabled = true;
   private final INoteStorageProvider storeProvder;

   public AtsNote(INoteStorageProvider storeProvder) {
      this.storeProvder = storeProvder;
   }

   public void addNote(NoteType type, String state, String msg, User user) {
      addNote(type, state, msg, new Date(), user);
   }

   public void addNoteItem(NoteItem noteItem) {
      addNote(noteItem.getType(), noteItem.getState(), noteItem.getMsg(), noteItem.getDate(), noteItem.getUser());
   }

   public void addNote(NoteType type, String state, String msg, Date date, User user) {
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

   public List<NoteItem> getNoteItems() {
      try {
         String xml = storeProvder.getNoteXml();
         if (Strings.isValid(xml)) {
            return NoteItem.fromXml(xml, storeProvder.getNoteId());
         }
      } catch (Exception ex) {
         OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, ex);
      }
      return Collections.emptyList();
   }

   public void saveNoteItems(List<NoteItem> items) {
      try {
         String xml = NoteItem.toXml(items);
         storeProvder.saveNoteXml(xml);
      } catch (Exception ex) {
         OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, "Can't create ats note document", ex);
      }
   }

   /**
    * Display Note Table; If state == null, only display non-state notes Otherwise, show only notes associated with
    * state
    */
   public String getTable(String state) {
      if (!storeProvder.isNoteable()) {
         return "";
      }
      ArrayList<NoteItem> showNotes = new ArrayList<NoteItem>();
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
         User user = note.getUser();
         String name = "";
         if (user != null) {
            name = user.getName();
            if (!Strings.isValid(name)) {
               name = user.getName();
            }
         }
         builder.append(AHTML.addRowMultiColumnTable(String.valueOf(note.getType()),
            (note.getState().isEmpty() ? "," : note.getState()), (note.getMsg().equals("") ? "," : note.getMsg()),
            name, dateFormat.format(note.getDate())));
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

}