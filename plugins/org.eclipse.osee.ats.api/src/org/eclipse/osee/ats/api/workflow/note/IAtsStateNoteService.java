/*
 * Created on Sep 9, 2022
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.api.workflow.note;

import java.util.Date;
import java.util.List;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.framework.core.data.UserId;

public interface IAtsStateNoteService {

   void addNote(IAtsWorkItem workItem, AtsStateNoteType type, String state, String msg, IAtsChangeSet changes);

   void addNote(IAtsWorkItem workItem, AtsStateNote note, IAtsChangeSet changes);

   void addNote(IAtsWorkItem workItem, AtsStateNoteType type, String state, String msg, Date date, UserId user, IAtsChangeSet changes);

   List<AtsStateNote> getNotes(IAtsWorkItem workItem);

   /**
    * Display Note Table; If state == null, only display non-state notes Otherwise, show only notes associated with
    * state
    */
   String getTable(IAtsWorkItem workItem, String state);

   String getNoteTitle(IAtsWorkItem workItem);

   boolean isNoteable(IAtsWorkItem workItem);

   String getNoteAsJson(AtsStateNote note);

   AtsStateNote getNoteFromJson(String json);

   boolean removeNote(IAtsWorkItem workItem, AtsStateNote note);

   boolean removeNote(IAtsWorkItem workItem, AtsStateNote note, IAtsChangeSet changes);

   boolean updateNote(IAtsWorkItem workItem, AtsStateNote note, String entry);

   boolean updateNote(IAtsWorkItem workItem, AtsStateNote note, String newMsg, IAtsChangeSet changes);

   void addNote(IAtsWorkItem workItem, AtsStateNote note);

}