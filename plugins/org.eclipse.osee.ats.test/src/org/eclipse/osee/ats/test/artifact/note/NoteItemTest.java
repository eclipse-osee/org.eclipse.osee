/*
 * Created on Sep 28, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.test.artifact.note;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import junit.framework.Assert;
import org.eclipse.osee.ats.NoteType;
import org.eclipse.osee.ats.artifact.note.NoteItem;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.DateUtil;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.junit.Test;

public class NoteItemTest {

   @Test
   public void testNoteItemNoteTypeStringStringUserString() throws OseeCoreException {
      Date date = new Date();
      NoteItem item =
         new NoteItem(NoteType.Comment, "Implement", String.valueOf(date.getTime()), UserManager.getUser(), "my msg");
      validate(item, date);
   }

   public static void validate(NoteItem item, Date date) throws OseeCoreException {
      Assert.assertEquals(NoteType.Comment, item.getType());
      Assert.assertEquals("Implement", item.getState());
      Assert.assertEquals(UserManager.getUser(), item.getUser());
      Assert.assertEquals("my msg", item.getMsg());
   }

   public static NoteItem getTestNoteItem(Date date) throws OseeCoreException {
      return new NoteItem(NoteType.Comment, "Implement", String.valueOf(date.getTime()), UserManager.getUser(),
         "my msg");
   }

   @Test
   public void testNoteItemStringStringStringUserString() throws OseeCoreException {
      Date date = new Date();
      NoteItem item =
         new NoteItem(NoteType.Comment.name(), "Implement", String.valueOf(date.getTime()), UserManager.getUser(),
            "my msg");
      validate(item, date);
   }

   @Test
   public void testToString() throws OseeCoreException {
      Date date = new Date();
      NoteItem item = getTestNoteItem(date);

      Assert.assertEquals(
         "Note: Comment from " + UserManager.getUser().getName() + " for \"Implement\" on " + DateUtil.getMMDDYYHHMM(date) + " - my msg",
         item.toString());
   }

   @Test
   public void testToXmlFromXml() throws OseeCoreException {
      Date date = new Date();
      NoteItem item = getTestNoteItem(date);
      NoteItem item2 =
         new NoteItem(NoteType.Question.name(), "Analyze", String.valueOf(date.getTime()), UserManager.getUser(),
            "another message");

      String xml = NoteItem.toXml(Arrays.asList(item, item2));
      Assert.assertEquals(
         "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?><AtsNote>" + //
         "<Item date=\"" + date.getTime() + "\" msg=\"my msg\" state=\"Implement\" type=\"Comment\" userId=\"" + UserManager.getUser().getUserId() + "\"/>" + //
         "<Item date=\"" + date.getTime() + "\" msg=\"another message\" state=\"Analyze\" type=\"Question\" userId=\"" + UserManager.getUser().getUserId() + "\"/></AtsNote>",
         xml);

      List<NoteItem> items = NoteItem.fromXml(xml, "ASDF4");
      validate(items.iterator().next(), date);

      NoteItem fromXmlItem2 = items.get(1);
      Assert.assertEquals(NoteType.Question, fromXmlItem2.getType());
      Assert.assertEquals("Analyze", fromXmlItem2.getState());
      Assert.assertEquals(UserManager.getUser(), fromXmlItem2.getUser());
      Assert.assertEquals("another message", fromXmlItem2.getMsg());

   }

   @Test
   public void testToHTML() throws OseeCoreException {
      Date date = new Date();
      NoteItem item = getTestNoteItem(date);

      Assert.assertEquals(
         "<b>Note:</b>Comment from " + UserManager.getUser().getName() + " for \"Implement\" on " + DateUtil.getMMDDYYHHMM(date) + " - my msg",
         item.toHTML());
   }

}
