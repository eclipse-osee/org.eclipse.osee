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
import org.eclipse.osee.framework.core.data.CommitTransactionRecordResponse;
import org.junit.Test;

/**
 * Test Case for {@link CommitTransactionRecordResponse}
 * 
 * @author Megumi Telles
 */
public class CommitTransactionRecordResponseTest {
   private static int first_transactionNumber = 1234;
   private static int second_transactionNumber = 4321;
   private CommitTransactionRecordResponse record;

   @Test
   public void testConstruction() {
      record = new CommitTransactionRecordResponse(first_transactionNumber);
      assertEquals(first_transactionNumber, record.getTransactionNumber());
   }

   @Test
   public void testSetTransactionNumber() {
      record = new CommitTransactionRecordResponse();
      record.setTransactionNumber(second_transactionNumber);
      assertEquals(second_transactionNumber, record.getTransactionNumber());
   }

}
