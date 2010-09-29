/*
 * Created on Sep 29, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.test.artifact.note;

import java.util.Date;
import junit.framework.Assert;
import org.eclipse.osee.ats.artifact.note.AtsNote;
import org.eclipse.osee.ats.artifact.note.INoteStorageProvider;
import org.eclipse.osee.ats.artifact.note.NoteItem;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.eclipse.osee.framework.ui.plugin.util.Result;
import org.junit.Test;

public class AtsNoteTest {

   @Test
   public void testToAndFromStore() throws OseeCoreException {
      Date date = new Date();
      SimpleNoteStore store = new SimpleNoteStore();
      AtsNote log = new AtsNote(store);
      NoteItem item = NoteItemTest.getTestNoteItem(date);
      log.addNoteItem(item);

      AtsNote log2 = new AtsNote(store);
      Assert.assertEquals(1, log2.getNoteItems().size());
      NoteItem loadItem = log2.getNoteItems().iterator().next();
      NoteItemTest.validate(loadItem, date);
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
