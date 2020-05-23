/*********************************************************************
 * Copyright (c) 2014 Boeing
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

package org.eclipse.osee.framework.skynet.core.importing.parsers;

import org.eclipse.osee.framework.skynet.core.importing.parsers.DoorsTableRow.RowType;
import org.jsoup.select.Elements;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test Case for {@link DoorsTableRow}
 * 
 * @author David Miller
 */
public final class DoorsTableRowTest {

   @Test
   public void testConstruction() {
      DoorsTableRow dr = new DoorsTableRow();
      Assert.assertNotNull(dr);
      Assert.assertEquals(dr.getDataType(), DoorsDataType.NOT_DEFINED);
      Assert.assertEquals(dr.getType(), DoorsTableRow.RowType.SINGLE);
   }

   @Test
   public void testFillElements() {
      Elements elements = new DoorsJsoupElementUtility().getJsoupElements();
      DoorsTableRow dr = new DoorsTableRow();
      dr.fill(elements.get(0));
      Assert.assertEquals(dr.getRows().size(), 16);
      Assert.assertNotSame(dr.getRows().get(0).data(), "ID");
      Assert.assertEquals(dr.getElement(5), dr.getRows().get(5));
   }

   @Test
   public void testDataType() {
      DoorsTableRow dr = new DoorsTableRow();
      Assert.assertNotNull(dr);
      dr.setDataType(DoorsDataType.HEADING);
      Assert.assertEquals(dr.getDataType(), DoorsDataType.HEADING);
   }

   @Test
   public void testType() {
      DoorsTableRow dr = new DoorsTableRow();
      Assert.assertNotNull(dr);
      dr.setRowType(RowType.FIRST_ROW);
      Assert.assertEquals(dr.getType(), RowType.FIRST_ROW);
   }

   @Test
   public void testMainRow() {
      DoorsTableRow dr = new DoorsTableRow();
      Assert.assertNotNull(dr);
      Assert.assertTrue(dr.isMainRow());
      dr.setRowType(RowType.MULTI_MID);
      Assert.assertFalse(dr.isMainRow());
   }
}
