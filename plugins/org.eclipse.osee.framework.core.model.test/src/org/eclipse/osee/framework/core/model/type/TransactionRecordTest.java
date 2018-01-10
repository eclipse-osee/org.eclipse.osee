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
package org.eclipse.osee.framework.core.model.type;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.TransactionId;
import org.eclipse.osee.framework.core.enums.TransactionDetailsType;
import org.eclipse.osee.framework.core.model.TransactionRecord;
import org.eclipse.osee.framework.core.model.mocks.MockDataFactory;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

/**
 * Test Case for {@link TransactionRecord}
 *
 * @author Megumi Telles
 */
@RunWith(Parameterized.class)
public class TransactionRecordTest {

   private final TransactionRecord transaction;
   private final Long transactionNumber;
   private final BranchId branch;
   private final TransactionDetailsType txType;

   private final String comment;
   private final Date time;
   private final int authorArtId;
   private final int commitArtId;

   public TransactionRecordTest(int transactionNumber, BranchId branch, String comment, Date time, int authorArtId, int commitArtId, TransactionDetailsType txType) {
      this.transactionNumber = (long) transactionNumber;
      this.branch = branch;
      this.comment = comment;
      this.time = time;
      this.authorArtId = authorArtId;
      this.commitArtId = commitArtId;
      this.txType = txType;

      this.transaction =
         new TransactionRecord(this.transactionNumber, branch, comment, time, authorArtId, commitArtId, txType, 234L);
   }

   @Test
   public void getBranch() {
      Assert.assertEquals(branch, transaction.getBranch());
   }

   @Test
   public void getId() {
      Assert.assertEquals(transactionNumber, transaction.getId());
   }

   @Test
   public void getTxType() {
      Assert.assertEquals(txType, transaction.getTxType());
   }

   @Test
   public void testGetSetComment() {
      Assert.assertEquals(comment, transaction.getComment());

      transaction.setComment("test set comment");
      Assert.assertEquals("test set comment", transaction.getComment());

      transaction.setComment(comment);
   }

   @Test
   public void testGetSetDate() {
      Assert.assertEquals(time, transaction.getTimeStamp());

      Date anotherDate = new Date(11111111111L);
      transaction.setTimeStamp(anotherDate);
      Assert.assertEquals(anotherDate, transaction.getTimeStamp());

      transaction.setTimeStamp(time);
   }

   @Test
   public void testGetSetAuthor() {
      Assert.assertEquals(authorArtId, transaction.getAuthor());

      transaction.setAuthor(authorArtId * 101);
      Assert.assertEquals(authorArtId * 101, transaction.getAuthor());

      transaction.setAuthor(authorArtId);
   }

   @Test
   public void testGetSetCommit() {
      Assert.assertEquals(commitArtId, transaction.getCommit());

      transaction.setCommit(commitArtId * 333);
      Assert.assertEquals(commitArtId * 333, transaction.getCommit());

      transaction.setCommit(commitArtId);
   }

   @Test
   public void testEqualsAndHashCode() {
      TransactionRecord tx2 = MockDataFactory.createTransaction(99, 2);
      TransactionId tx1 = TransactionId.valueOf(tx2.getId());

      // Add some variation to tx2 so we are certain that only the txId is used in the equals method;
      tx2.setAuthor(0);
      tx2.setComment("a");
      tx2.setCommit(1);
      tx2.setTimeStamp(new Date(11111111111L));

      Assert.assertNotSame(tx1, tx2);

      Assert.assertTrue(tx1.equals(tx2));
      Assert.assertTrue(tx2.equals(tx1));
      Assert.assertEquals(tx1.hashCode(), tx2.hashCode());

      Assert.assertFalse(transaction.equals(tx1));
      Assert.assertFalse(transaction.equals(tx2));
      Assert.assertFalse(transaction.hashCode() == tx1.hashCode());
      Assert.assertFalse(transaction.hashCode() == tx2.hashCode());
   }

   @Test
   public void testAdaptable() {
      Assert.assertNull(transaction.getAdapter(null));
      Assert.assertSame(transaction, transaction.getAdapter(Object.class));
      Assert.assertSame(transaction, transaction.getAdapter(TransactionRecord.class));
   }

   @Test
   public void testToString() {
      Assert.assertEquals(String.valueOf(transactionNumber), transaction.toString());
   }

   @Parameters
   public static Collection<Object[]> getData() {
      Collection<Object[]> data = new ArrayList<>();
      for (int index = 1; index <= 2; index++) {
         int transactionNumber = index * 11;
         BranchId branch = BranchId.valueOf(index * 9L);
         String comment = GUID.create();
         Date time = new Date();
         int authorArtId = index * 47;
         int commitArtId = index * 37;
         TransactionDetailsType txType = TransactionDetailsType.toEnum(index % TransactionDetailsType.values().length);
         data.add(new Object[] {transactionNumber, branch, comment, time, authorArtId, commitArtId, txType});
      }
      return data;
   }
}
