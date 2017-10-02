/*******************************************************************************
 * Copyright (c) 2015 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs.rest.internal.writer;

import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.util.result.XResultData;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.jdk.core.util.io.xml.RowProcessor;
import org.eclipse.osee.orcs.rest.model.writer.reader.OwBranch;
import org.eclipse.osee.orcs.rest.model.writer.reader.OwCollector;

/**
 * @author Donald G. Dunne
 */
public class OrcsWriterSheetProcessorForSettings implements RowProcessor {

   private final OwCollector collector;
   private int rowCount = 0;
   private final OrcsWriterFactory factory;

   public OrcsWriterSheetProcessorForSettings(OwCollector collector, XResultData result) {
      this.collector = collector;
      this.factory = new OrcsWriterFactory(collector);
   }

   @Override
   public void detectedRowAndColumnCounts(int rowCount, int columnCount) {
      // do nothing
   }

   @Override
   public void foundStartOfWorksheet(String sheetName)  {
      // do nothing
   }

   @Override
   public void processCommentRow(String[] row) {
      // do nothing
   }

   @Override
   public void processEmptyRow() {
      // do nothing
   }

   @Override
   public void processHeaderRow(String[] headerRow) {
      // do nothing
   }

   @Override
   public void processRow(String[] row)  {
      rowCount++;
      for (int colCount = 0; colCount < row.length; colCount++) {
         if (colCount == 0) {
            String key = row[colCount];
            if (key.equals(OrcsWriterUtil.BRANCH_TOKEN_SETTING)) {
               String branchTokenStr = row[1];
               if (Strings.isValid(branchTokenStr)) {
                  OwBranch branchToken = factory.getOrCreateBranchToken(branchTokenStr);
                  collector.setBranch(branchToken);
                  collector.setBranchId(BranchId.valueOf(branchToken.getId()));
                  branchToken.setData(OrcsWriterUtil.getData(OrcsWriterUtil.INSTRUCTIONS_AND_SETTINGS_SHEET_NAME,
                     rowCount, colCount, branchTokenStr));
               }
            } else if (key.equals(OrcsWriterUtil.AS_USER_ID_SETTING)) {
               String userId = row[1];
               if (Strings.isValid(userId)) {
                  collector.setAsUserId(userId);
               }
            } else if (key.equals(OrcsWriterUtil.PERSIST_COMMENT_SETTING)) {
               String persistComment = row[1];
               if (Strings.isValid(persistComment)) {
                  collector.setPersistComment(persistComment);
               }
            }
         }
      }
   }

   @Override
   public void reachedEndOfWorksheet() {
      // do nothing
   }

}
