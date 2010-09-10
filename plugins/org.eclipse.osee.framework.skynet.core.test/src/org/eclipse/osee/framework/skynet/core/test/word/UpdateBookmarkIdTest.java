/*
 * Created on Aug 30, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.skynet.core.test.word;

import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.word.UpdateBookmarkIds;
import org.junit.Assert;
import org.junit.Test;

public class UpdateBookmarkIdTest {

   @Test
   public void testBookMarkIdFix() throws OseeCoreException {
      UpdateBookmarkIds bookMarkIds = new UpdateBookmarkIds(1000);
      String bookmark =
         "<aml:annotation aml:id=\"133334\" w:type=\"Word.Bookmark.Start\"/><aml:annotation aml:id=\"133334\" w:type=\"Word.Bookmark.End\" />";
      String modifiedContent = bookMarkIds.fixTags(bookmark);

      Assert.assertEquals(
         "The bookmark IDs have been reset",
         "<aml:annotation aml:id=\"1001\" w:type=\"Word.Bookmark.Start\"></aml:annotation><aml:annotation aml:id=\"1001\" w:type=\"Word.Bookmark.End\"></aml:annotation>",
         modifiedContent);
   }

}
