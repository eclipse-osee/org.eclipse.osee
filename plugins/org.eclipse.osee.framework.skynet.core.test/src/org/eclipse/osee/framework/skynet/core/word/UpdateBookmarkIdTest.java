/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.skynet.core.word;

import org.junit.Assert;
import org.junit.Test;

/**
 * Test Case for {@link UpdateBookmarkIds}
 * 
 * @author Ryan D. Brooks
 */
public class UpdateBookmarkIdTest {

   @Test
   public void testBookMarkIdFix() {
      UpdateBookmarkIds bookMarkIds = new UpdateBookmarkIds(1000);
      String bookmark =
         "<aml:annotation aml:id=\"133334\" w:type=\"Word.Bookmark.Start\"/><aml:annotation aml:id=\"133334\" w:type=\"Word.Bookmark.End\"/>";
      String modifiedContent = bookMarkIds.fixTags(bookmark);

      Assert.assertEquals("The bookmark IDs have been reset",
         "<aml:annotation aml:id=\"1001\" w:type=\"Word.Bookmark.Start\"/><aml:annotation aml:id=\"1001\" w:type=\"Word.Bookmark.End\"/>",
         modifiedContent);
   }

}
