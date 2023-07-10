/*********************************************************************
 * Copyright (c) 2023 Boeing
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
package org.eclipse.osee.framework.jdk.core.util.io.excel.pojo;

import org.eclipse.osee.framework.jdk.core.util.io.excel.pojo.model.ECell;
import org.eclipse.osee.framework.jdk.core.util.io.excel.pojo.model.EFile;
import org.eclipse.osee.framework.jdk.core.util.io.excel.pojo.model.EHeaderCell;
import org.eclipse.osee.framework.jdk.core.util.io.excel.pojo.model.ERow;
import org.eclipse.osee.framework.jdk.core.util.io.excel.pojo.model.EWorksheet;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

/**
 * @author Donald G. Dunne
 */
public class ExcelToPojoOperationTest {

   @Ignore
   @Test
   public void test() {
      EFile eFile = ExcelToPojoOperationExample.getExcelToPojo();
      Assert.assertNotNull(eFile);
      Assert.assertNotNull(eFile.getWorkbook());
      Assert.assertNotNull(eFile.getWorkbook().getSheets());
      Assert.assertEquals(1, eFile.getWorkbook().getSheets().size());
      EWorksheet sheet = eFile.getWorkbook().getSheets().iterator().next();
      Assert.assertEquals(7, sheet.getHeader().getHcells().size());
      Assert.assertEquals(2, sheet.getRows().size());
      ERow eRow = sheet.getRows().iterator().next();
      Assert.assertEquals(7, eRow.getCells().size());
      ECell cell = eRow.getCells().iterator().next();
      Assert.assertEquals(1, cell.getCol().getColNum());
      Assert.assertEquals("My Title 1", cell.getValue());
      EHeaderCell col = cell.getCol();
      Assert.assertEquals(1, col.getColNum());
      Assert.assertEquals("Title", col.getName());
      Assert.assertEquals(2, cell.getRow().getRowNum());
   }

}
