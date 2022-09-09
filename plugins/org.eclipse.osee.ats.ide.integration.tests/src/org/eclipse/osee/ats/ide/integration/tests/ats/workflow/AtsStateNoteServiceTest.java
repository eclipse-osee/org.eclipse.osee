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

package org.eclipse.osee.ats.ide.integration.tests.ats.workflow;

import java.util.Date;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.workflow.note.AtsStateNote;
import org.eclipse.osee.ats.api.workflow.note.AtsStateNoteType;
import org.eclipse.osee.ats.api.workflow.note.IAtsStateNoteService;
import org.eclipse.osee.ats.core.workflow.state.TeamState;
import org.eclipse.osee.ats.ide.demo.DemoUtil;
import org.eclipse.osee.ats.ide.integration.tests.AtsApiService;
import org.eclipse.osee.ats.ide.workflow.teamwf.TeamWorkFlowArtifact;
import org.eclipse.osee.framework.core.enums.DemoUsers;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author Donald G. Dunne
 */
public class AtsStateNoteServiceTest {

   public static Date now = new Date();
   public static String msg = "This is my message.";

   @Test
   public void testAddAndGetNotes() {

      TeamWorkFlowArtifact teamWf = DemoUtil.getSawCodeCommittedWf();
      Assert.assertNotNull(teamWf);

      AtsApi atsApi = AtsApiService.get();
      IAtsStateNoteService noteService = atsApi.getWorkItemService().getStateNoteService();

      // Test get
      Assert.assertEquals(0, noteService.getNotes(teamWf).size());

      // Test isNoteable
      Assert.assertTrue(noteService.isNoteable(teamWf));

      // Test add
      AtsStateNote note = getNote();
      noteService.addNote(teamWf, note);

      Assert.assertEquals(1, noteService.getNotes(teamWf).size());
      Assert.assertEquals(AtsStateNoteType.Problem.name(), noteService.getNotes(teamWf).iterator().next().getType());

      // Test add
      AtsStateNote note2 = getNote();
      note2.setType(AtsStateNoteType.Warning.getName());
      noteService.addNote(teamWf, note2);

      Assert.assertEquals(2, noteService.getNotes(teamWf).size());

      // Test remove
      noteService.removeNote(teamWf, note);

      Assert.assertEquals(1, noteService.getNotes(teamWf).size());
      Assert.assertTrue(note2.getId().equals(noteService.getNotes(teamWf).iterator().next().getId()));

      // Test update (will remove old and add new)
      noteService.updateNote(teamWf, note2, "New Message");

      Assert.assertEquals(1, noteService.getNotes(teamWf).size());
      // New id is given
      Assert.assertFalse(note2.getId().equals(noteService.getNotes(teamWf).iterator().next().getId()));
      Assert.assertEquals("New Message", noteService.getNotes(teamWf).iterator().next().getMsg());
   }

   private AtsStateNote getNote() {
      AtsStateNote note = new AtsStateNote(AtsStateNoteType.Problem.name(), TeamState.Analyze.getName(),
         String.valueOf(now.getTime()), DemoUsers.Joe_Smith, msg);
      note.setId(Lib.generateId());
      return note;
   }
}
