/*********************************************************************
 * Copyright (c) 2010 Boeing
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

import static org.mockito.Mockito.when;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.user.AtsUser;
import org.eclipse.osee.ats.api.workflow.note.IAtsWorkItemNotes;
import org.eclipse.osee.ats.api.workflow.note.NoteItem;
import org.eclipse.osee.framework.core.data.UserService;
import org.eclipse.osee.framework.core.data.UserToken;
import org.eclipse.osee.framework.core.enums.DemoUsers;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

/**
 * @author Donald G. Dunne
 */
public class AtsNoteTest {
   private final UserToken joe = DemoUsers.Joe_Smith;

   // @formatter:off
   @Mock private UserService userService;
   @Mock private AtsApi atsApi;
   // @formatter:on
   List<AtsUser> assignees = new ArrayList<>();

   @Before
   public void setup() {
      MockitoAnnotations.initMocks(this);

      when(atsApi.userService()).thenReturn(userService);
      when(userService.getUserByUserId(joe.getUserId())).thenReturn(joe);
   }

   @Test
   public void testToAndFromStore() {
      Date date = new Date();
      SimpleNoteStore store = new SimpleNoteStore();
      IAtsWorkItemNotes log = new AtsWorkItemNotes(store, atsApi);
      NoteItem item = NoteItemTest.getTestNoteItem(date, joe);
      log.addNoteItem(item);

      IAtsWorkItemNotes log2 = new AtsWorkItemNotes(store, atsApi);
      Assert.assertEquals(1, log2.getNoteItems().size());
      NoteItem loadItem = log2.getNoteItems().iterator().next();
      NoteItemTest.validate(loadItem, date, joe);
   }

   public class SimpleNoteStore implements INoteStorageProvider {

      String store = "";

      @Override
      public String getNoteXml() {
         return store;
      }

      @Override
      public Result saveNoteXml(String xml) {
         store = xml;
         return Result.TrueResult;
      }

      @Override
      public String getNoteTitle() {
         return "This is the title";
      }

      @Override
      public String getNoteId() {
         return GUID.create();
      }

      @Override
      public boolean isNoteable() {
         return false;
      }

   }

}
