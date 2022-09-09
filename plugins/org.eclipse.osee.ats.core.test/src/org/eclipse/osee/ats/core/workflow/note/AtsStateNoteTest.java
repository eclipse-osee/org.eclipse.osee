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

import java.util.Date;
import org.eclipse.osee.ats.api.workflow.note.AtsStateNote;
import org.eclipse.osee.ats.api.workflow.note.AtsStateNoteType;
import org.eclipse.osee.ats.core.workflow.state.TeamState;
import org.eclipse.osee.framework.core.enums.DemoUsers;
import org.eclipse.osee.framework.core.util.JsonUtil;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author Donald G. Dunne
 */
public class AtsStateNoteTest {

   @Test
   public void testToAndFromJson() {
      Date now = new Date();
      String msg = "This is my message.";
      AtsStateNote note = new AtsStateNote(AtsStateNoteType.Problem.name(), TeamState.Analyze.getName(),
         String.valueOf(now.getTime()), DemoUsers.Joe_Smith, msg);

      String json = JsonUtil.toJson(note);
      Assert.assertTrue(json.contains(msg));

      AtsStateNote note2 = JsonUtil.readValue(json, AtsStateNote.class);

      Assert.assertEquals(note.getId(), note2.getId());
      Assert.assertEquals(note.getMsg(), note2.getMsg());
      Assert.assertEquals(note.getState(), note2.getState());
      Assert.assertTrue(note.getUser().equals(note2.getUser()));
      Assert.assertTrue(note.getUserTok().equals(note2.getUserTok()));
      Assert.assertTrue(note.getDate().equals(note2.getDate()));
      Assert.assertTrue(note.getTypeEnum().equals(note2.getTypeEnum()));
   }

}
