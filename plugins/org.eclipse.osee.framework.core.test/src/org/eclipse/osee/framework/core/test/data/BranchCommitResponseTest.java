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

import java.util.ArrayList;
import java.util.Collection;
import junit.framework.Assert;
import org.eclipse.osee.framework.core.data.BranchCommitResponse;
import org.eclipse.osee.framework.core.model.TransactionRecord;
import org.eclipse.osee.framework.core.test.mocks.MockDataFactory;
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
      Assert.assertNull(response.getTransaction());
      response.setTransaction(transaction);

      Assert.assertNotNull(response.getTransaction());
      Assert.assertEquals(transaction, response.getTransaction());
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
