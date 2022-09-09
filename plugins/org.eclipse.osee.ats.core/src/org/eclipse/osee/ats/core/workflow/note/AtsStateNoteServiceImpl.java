/*********************************************************************
 * Copyright (c) 2022 Boeing
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
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.user.AtsUser;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.workflow.note.AtsStateNote;
import org.eclipse.osee.ats.api.workflow.note.AtsStateNoteType;
import org.eclipse.osee.ats.api.workflow.note.IAtsStateNoteService;
import org.eclipse.osee.ats.core.internal.AtsApiService;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.IAttribute;
import org.eclipse.osee.framework.core.data.UserId;
import org.eclipse.osee.framework.jdk.core.util.AHTML;
import org.eclipse.osee.framework.logging.OseeLog;

/**
 * @author Donald G. Dunne
 */
public class AtsStateNoteServiceImpl implements IAtsStateNoteService {

   private final AtsApi atsApi;

   public AtsStateNoteServiceImpl(AtsApi atsApi) {
      this.atsApi = atsApi;
   }

   @Override
   public void addNote(IAtsWorkItem workItem, AtsStateNoteType type, String state, String msg, IAtsChangeSet changes) {
      addNote(workItem, type, state, msg, new Date(), atsApi.userService().getUser(), changes);
   }

   @Override
   public void addNote(IAtsWorkItem workItem, AtsStateNoteType type, String state, String msg, Date date, UserId user, IAtsChangeSet changes) {
      AtsStateNote note =
         new AtsStateNote(type.getName(), state, String.valueOf(date.getTime()), UserId.valueOf(user.getId()), msg);
      addNote(workItem, note, changes);
   }

   @Override
   public void addNote(IAtsWorkItem workItem, AtsStateNote note, IAtsChangeSet changes) {
      String json = getNoteAsJson(note);
      changes.addAttribute(workItem, AtsAttributeTypes.StateNotes, json);
   }

   @Override
   public void addNote(IAtsWorkItem workItem, AtsStateNote note) {
      IAtsChangeSet changes = atsApi.createChangeSet("Add State Note");
      addNote(workItem, note, changes);
      changes.executeIfNeeded();
   }

   @Override
   public List<AtsStateNote> getNotes(IAtsWorkItem workItem) {
      List<AtsStateNote> notes = new ArrayList<>();
      try {
         for (String json : atsApi.getAttributeResolver().getAttributesToStringList(workItem,
            AtsAttributeTypes.StateNotes)) {
            if (json.startsWith("{")) {
               AtsStateNote note = getNoteFromJson(json);
               ArtifactId userArtId = note.getUser();
               AtsUser user = atsApi.getUserService().getUserById(userArtId);
               note.setUserTok(user.getArtifactToken());
               notes.add(note);
            }
         }
      } catch (Exception ex) {
         OseeLog.log(AtsStateNoteServiceImpl.class, Level.SEVERE, "Error extracting note", ex);
      }
      return notes;
   }

   @Override
   public String getNoteAsJson(AtsStateNote note) {
      return atsApi.jaxRsApi().toJson(note);
   }

   @Override
   public AtsStateNote getNoteFromJson(String jsonNote) {
      return atsApi.jaxRsApi().readValue(jsonNote, AtsStateNote.class);
   }

   /**
    * Display Note Table; If state == null, only display non-state notes Otherwise, show only notes associated with
    * state
    */
   @Override
   public String getTable(IAtsWorkItem workItem, String state) {
      if (!isNoteable(workItem)) {
         return "";
      }
      ArrayList<AtsStateNote> showNotes = new ArrayList<>();
      List<AtsStateNote> noteItems = getNotes(workItem);
      for (AtsStateNote li : noteItems) {
         if (state == null && li.getState().equals("")) {
            showNotes.add(li);
         } else if (state != null && ("ALL".equals(state) || li.getState().equals(state))) {
            showNotes.add(li);
         }
      }
      if (showNotes.isEmpty()) {
         return "";
      }
      return toHtmlTable(showNotes);
   }

   private String toHtmlTable(List<AtsStateNote> showNotes) {
      StringBuilder builder = new StringBuilder();
      builder.append(AHTML.beginMultiColumnTable(100, 1));
      builder.append(AHTML.addHeaderRowMultiColumnTable(Arrays.asList("Type", "State", "Message", "User", "Date")));
      DateFormat dateFormat = getDateFormat();
      for (AtsStateNote note : showNotes) {
         String name = note.getUserName();
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

   @Override
   public String getNoteTitle(IAtsWorkItem workItem) {
      return "History for \"" + atsApi.getStoreService().getArtifactType(
         workItem.getStoreObject()).getName() + "\" - " + workItem.getId() + " - titled \"" + workItem.getName() + "\"";
   }

   @Override
   public boolean isNoteable(IAtsWorkItem workItem) {
      return atsApi.getStoreService().isAttributeTypeValid(workItem, AtsAttributeTypes.StateNotes);
   }

   @Override
   public boolean removeNote(IAtsWorkItem workItem, AtsStateNote note) {
      boolean changed = false;
      IAtsChangeSet changes = AtsApiService.get().createChangeSet("Remove State Note");
      changed = removeNote(workItem, note, changes);
      if (changed) {
         changes.executeIfNeeded();
      }
      return changed;
   }

   @Override
   public boolean removeNote(IAtsWorkItem workItem, AtsStateNote note, IAtsChangeSet changes) {
      IAttribute<Object> matchAttr = getNoteAttr(workItem, note);
      if (matchAttr == null) {
         return false;
      }
      changes.deleteAttribute(workItem, matchAttr);
      return true;
   }

   @Override
   public boolean updateNote(IAtsWorkItem workItem, AtsStateNote note, String newMsg, IAtsChangeSet changes) {
      IAttribute<Object> matchAttr = getNoteAttr(workItem, note);
      if (matchAttr == null) {
         return false;
      }
      changes.deleteAttribute(workItem, matchAttr);
      atsApi.getWorkItemService().getStateNoteService().addNote(workItem, note.getTypeEnum(), note.getState(), newMsg,
         changes);
      return true;
   }

   @Override
   public boolean updateNote(IAtsWorkItem workItem, AtsStateNote note, String newMsg) {
      boolean changed = false;
      IAtsChangeSet changes = AtsApiService.get().createChangeSet("Update State Note");
      changed = updateNote(workItem, note, newMsg, changes);
      if (changed) {
         changes.executeIfNeeded();
      }
      return changed;
   }

   private IAttribute<Object> getNoteAttr(IAtsWorkItem workItem, AtsStateNote note) {
      IAttribute<Object> matchAttr = null;
      for (IAttribute<Object> attr : AtsApiService.get().getAttributeResolver().getAttributes(workItem,
         AtsAttributeTypes.StateNotes)) {
         String value = (String) attr.getValue();
         if (value.contains(note.getId().toString())) {
            matchAttr = attr;
            break;
         }
      }
      return matchAttr;
   }

}