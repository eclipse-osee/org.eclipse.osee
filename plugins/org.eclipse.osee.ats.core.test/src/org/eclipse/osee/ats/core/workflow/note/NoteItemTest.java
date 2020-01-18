/*******************************************************************************
 * Copyright (c) 2010 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.core.workflow.note;

import static org.eclipse.osee.ats.core.users.AbstractUserTest.joe;
import static org.mockito.Mockito.when;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.user.IAtsUser;
import org.eclipse.osee.ats.api.user.IAtsUserService;
import org.eclipse.osee.ats.api.workflow.note.NoteItem;
import org.eclipse.osee.ats.api.workflow.note.NoteType;
import org.eclipse.osee.framework.jdk.core.util.DateUtil;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

/**
 * @author Donald G. Dunne
 */
public class NoteItemTest {

   // @formatter:off
   @Mock private IAtsUserService userService;
   @Mock private AtsApi atsApi;
   // @formatter:on
   List<IAtsUser> assignees = new ArrayList<>();

   @Before
   public void setup() {
      MockitoAnnotations.initMocks(this);

      when(atsApi.getUserService()).thenReturn(userService);
      when(userService.getUserById(joe.getUserId())).thenReturn(joe);
   }

   @Test
   public void testNoteItemNoteTypeStringStringUserString() {
      Date date = new Date();
      NoteItem item = new NoteItem(NoteType.Comment, "Implement", String.valueOf(date.getTime()), joe, "my msg");
      validate(item, date, joe);
   }

   public static void validate(NoteItem item, Date date, IAtsUser joe) {
      Assert.assertEquals(NoteType.Comment, item.getType());
      Assert.assertEquals("Implement", item.getState());
      Assert.assertEquals(joe, item.getUser());
      Assert.assertEquals("my msg", item.getMsg());
   }

   public static NoteItem getTestNoteItem(Date date, IAtsUser user) {
      return new NoteItem(NoteType.Comment, "Implement", String.valueOf(date.getTime()), user, "my msg");
   }

   @Test
   public void testNoteItemStringStringStringUserString() {
      Date date = new Date();
      NoteItem item = new NoteItem(NoteType.Comment.name(), "Implement", String.valueOf(date.getTime()), joe, "my msg");
      validate(item, date, joe);
   }

   @Test
   public void testToString() {
      Date date = new Date();
      NoteItem item = getTestNoteItem(date, joe);

      Assert.assertEquals(
         "Note: Comment from " + joe.getName() + " for \"Implement\" on " + DateUtil.getMMDDYYHHMM(date) + " - my msg",
         item.toString());
   }

   @Test
   public void testToXmlFromXml() {
      Date date = new Date();
      NoteItem item = getTestNoteItem(date, joe);
      NoteItem item2 =
         new NoteItem(NoteType.Question.name(), "Analyze", String.valueOf(date.getTime()), joe, "another message");

      String xml = AtsWorkItemNotes.toXml(Arrays.asList(item, item2), atsApi);
      Assert.assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?><AtsNote>" + //
         "<Item date=\"" + date.getTime() + "\" msg=\"my msg\" state=\"Implement\" type=\"Comment\" userId=\"" + joe.getUserId() + "\"/>" + //
         "<Item date=\"" + date.getTime() + "\" msg=\"another message\" state=\"Analyze\" type=\"Question\" userId=\"" + joe.getUserId() + "\"/></AtsNote>",
         xml);

      List<NoteItem> items = AtsWorkItemNotes.fromXml(xml, "ASDF4", atsApi);
      validate(items.iterator().next(), date, joe);

      NoteItem fromXmlItem2 = items.get(1);
      Assert.assertEquals(NoteType.Question, fromXmlItem2.getType());
      Assert.assertEquals("Analyze", fromXmlItem2.getState());
      Assert.assertEquals(joe, fromXmlItem2.getUser());
      Assert.assertEquals("another message", fromXmlItem2.getMsg());
   }

   @Test
   public void testToHTML() {
      Date date = new Date();
      NoteItem item = getTestNoteItem(date, joe);

      Assert.assertEquals(
         "<b>Note:</b>Comment from " + joe.getName() + " for \"Implement\" on " + DateUtil.getMMDDYYHHMM(
            date) + " - my msg",
         item.toHTML());
   }
}