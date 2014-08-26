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
package org.eclipse.osee.framework.core.message.test.data;

import java.util.ArrayList;
import java.util.Collection;
import org.eclipse.osee.framework.core.message.BranchCommitResponse;
import org.eclipse.osee.framework.core.message.test.mocks.MockDataFactory;
import org.eclipse.osee.framework.core.model.TransactionRecord;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

/**
 * Test Case for {@link BranchCommitResponse}
 * 
 * @author Roberto E. Escobar
 */
@RunWith(Parameterized.class)
public class BranchCommitResponseTest {

   private final BranchCommitResponse response;
   private final TransactionRecord transaction;

   public BranchCommitResponseTest(BranchCommitResponse response, TransactionRecord transaction) {
      this.response = response;
      this.transaction = transaction;
   }

   @Test
   public void testGetSetTransaction() {
      Assert.assertNull(response.getTransactionId());
      response.setTransactionId(transaction.getGuid());

      Assert.assertNotNull(response.getTransactionId());
      Assert.assertEquals(transaction.getGuid(), response.getTransactionId());
   }

   @Parameters
   public static Collection<Object[]> getData() {
      Collection<Object[]> data = new ArrayList<Object[]>();
      for (int index = 1; index <= 2; index++) {
         TransactionRecord transaction = MockDataFactory.createTransaction(index, 2);
         data.add(new Object[] {new BranchCommitResponse(), transaction});
      }
      return data;
   }
}
