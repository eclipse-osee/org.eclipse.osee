/*******************************************************************************
 * Copyright (c) 2017 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.api.workflow.note;

import java.util.Date;
import java.util.List;
import org.eclipse.osee.ats.api.user.IAtsUser;

/**
 * @author Donald G. Dunne
 */
public interface IAtsWorkItemNotes {

   void addNote(NoteType type, String state, String msg, IAtsUser user);

   void addNoteItem(NoteItem noteItem);

   void addNote(NoteType type, String state, String msg, Date date, IAtsUser user);

   List<NoteItem> getNoteItems();

   /**
    * Display Note Table; If state == null, only display non-state notes Otherwise, show only notes associated with
    * state
    */
   String getTable(String state);

}
