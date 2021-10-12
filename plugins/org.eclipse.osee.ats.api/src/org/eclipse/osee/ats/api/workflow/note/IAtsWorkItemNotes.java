/*********************************************************************
 * Copyright (c) 2017 Boeing
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

package org.eclipse.osee.ats.api.workflow.note;

import java.util.Date;
import java.util.List;
import org.eclipse.osee.framework.core.data.UserToken;

/**
 * @author Donald G. Dunne
 */
public interface IAtsWorkItemNotes {

   void addNote(NoteType type, String state, String msg);

   void addNoteItem(NoteItem noteItem);

   void addNote(NoteType type, String state, String msg, Date date, UserToken user);

   List<NoteItem> getNoteItems();

   /**
    * Display Note Table; If state == null, only display non-state notes Otherwise, show only notes associated with
    * state
    */
   String getTable(String state);

}
