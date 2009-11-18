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
package org.eclipse.osee.framework.core.test.data;

import static org.junit.Assert.assertEquals;
import java.util.Date;
import org.eclipse.osee.framework.core.data.TransactionRecord;
import org.eclipse.osee.framework.core.enums.BranchState;
import org.eclipse.osee.framework.core.enums.BranchType;
import org.eclipse.osee.framework.core.enums.TransactionDetailsType;
import org.eclipse.osee.framework.core.test.util.BranchTestUtil;
import org.junit.Test;

/**
 * Test Case for {@link TransactionRecord}
 * 
 * @author Megumi Telles
 */

public class TransactionRecordTest {

   private final String GUID = "gjdkfghfr183848754";
   private final String BRANCH_NAME = "test branch";
   private final BranchState BRANCH_STATE = BranchState.CREATED;
   private final BranchType BRANCH_TYPE = BranchType.BASELINE;
   private final boolean isArchived = false;

   private final int transactionNumber = 1234;
   private final String comment = "test branch";
   private final int authorArtId = 99999;
   private final int commitArtId = 111111;
   private final TransactionDetailsType txType = TransactionDetailsType.Baselined;

   @Test
   public void testTransactionRecordConstruction() {
      BranchTestUtil branch = new BranchTestUtil(GUID, BRANCH_NAME, BRANCH_STATE, BRANCH_TYPE, isArchived);
      Date date = new Date();
      TransactionRecord record =
            new TransactionRecord(transactionNumber, branch, comment, date, authorArtId, commitArtId, txType);
      assertEquals(transactionNumber, record.getId());
      assertEquals(branch, record.getBranch());
      assertEquals(comment, record.getComment());
      assertEquals(branch, record.getBranch());
      assertEquals(date, record.getDate());
      assertEquals(authorArtId, record.getAuthor());
      assertEquals(commitArtId, record.getCommit());
      assertEquals(txType, record.getTxType());
   }

   @Test
   public void testSetComment() {
      BranchTestUtil branch = new BranchTestUtil(GUID, BRANCH_NAME, BRANCH_STATE, BRANCH_TYPE, isArchived);
      Date date = new Date();
      TransactionRecord record =
            new TransactionRecord(transactionNumber, branch, comment, date, authorArtId, commitArtId, txType);
      record.setComment("test set comment");
      assertEquals("test set comment", record.getComment());
   }

   @Test
   public void testSetTime() {
      BranchTestUtil branch = new BranchTestUtil(GUID, BRANCH_NAME, BRANCH_STATE, BRANCH_TYPE, isArchived);
      Date date = new Date();
      TransactionRecord record =
            new TransactionRecord(transactionNumber, branch, comment, date, authorArtId, commitArtId, txType);
      record.setTime(date);
      assertEquals(date, record.getDate());
   }

   @Test
   public void testSetAuthor() {
      BranchTestUtil branch = new BranchTestUtil(GUID, BRANCH_NAME, BRANCH_STATE, BRANCH_TYPE, isArchived);
      Date date = new Date();
      TransactionRecord record =
            new TransactionRecord(transactionNumber, branch, comment, date, authorArtId, commitArtId, txType);
      record.setAuthor(5678);
      assertEquals(5678, record.getAuthor());
   }

   @Test
   public void testSetCommit() {
      BranchTestUtil branch = new BranchTestUtil(GUID, BRANCH_NAME, BRANCH_STATE, BRANCH_TYPE, isArchived);
      Date date = new Date();
      TransactionRecord record =
            new TransactionRecord(transactionNumber, branch, comment, date, authorArtId, commitArtId, txType);
      record.setCommit(123456);
      assertEquals(123456, record.getCommit());
   }

}
