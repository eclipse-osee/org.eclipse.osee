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
package org.eclipse.osee.framework.skynet.core.test.word;

import static org.junit.Assert.assertEquals;
import org.eclipse.osee.framework.skynet.core.word.WordUtil;
import org.junit.Test;

/**
 * @author Jeff C. Phillips
 */
public class WordUtilTest {
   @Test
   public void loadDeletedRelationTest() {
      String bookmark =
         "more content><aml:annotation aml:id=\"10546168\" w:type=\"Word.Bookmark.Start\"/><aml:annotation aml:id=\"133334\" w:type=\"Word.Bookmark.End\"/><Some more content> <aml:annotation aml:id=\"133334\" w:type=\"Word.Bookmark.Start\"/>aml:id=\"1\" w:type=\"Word.Bookmark.End";
      bookmark = WordUtil.reassignBookMarkID(bookmark);
      assertEquals(
         "The bookmark IDs have been reset",
         "more content><aml:annotation aml:id=\"10546168\" w:type=\"Word.Bookmark.Start\"/><aml:annotation aml:id=\"1000\" w:type=\"Word.Bookmark.End\"/><Some more content> <aml:annotation aml:id=\"133334\" w:type=\"Word.Bookmark.Start\"/>aml:id=\"1001\" w:type=\"Word.Bookmark.End",
         bookmark);
   }
}
