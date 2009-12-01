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
import java.util.List;
import junit.framework.Assert;
import org.eclipse.osee.framework.core.data.TransactionCacheUpdateResponse;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.TransactionRecord;
import org.eclipse.osee.framework.core.model.TransactionRecordFactory;
import org.eclipse.osee.framework.core.test.mocks.MockDataFactory;
import org.eclipse.osee.framework.core.test.translation.DataAsserts;
import org.eclipse.osee.framework.core.util.Compare;
import org.junit.Test;

/**
 * Test Case for {@link TransactionCacheUpdateResponse}
 * 
 * @author Roberto E. Escobar
 */
public class TransactionCacheUpdateResponseTest {

   @Test
   public void testGetRows() {
      List<TransactionRecord> expected = new ArrayList<TransactionRecord>();
      for (int j = 1; j <= 2; j++) {
         expected.add(MockDataFactory.createTransaction(j, j * 3));
      }

      TransactionCacheUpdateResponse response = new TransactionCacheUpdateResponse(expected);
      List<TransactionRecord> actual = response.getTxRows();
      Assert.assertFalse(Compare.isDifferent(expected, actual));
   }

   @Test
   public void testFromCache() throws OseeCoreException {

      List<TransactionRecord> data = new ArrayList<TransactionRecord>();
      for (int j = 1; j <= 2; j++) {
         data.add(MockDataFactory.createTransaction(j, j * 3));
      }

      TransactionRecordFactory factory = new TransactionRecordFactory();
      TransactionCacheUpdateResponse response = TransactionCacheUpdateResponse.fromCache(factory, data);
      List<TransactionRecord> actual = response.getTxRows();
      Assert.assertEquals(data.size(), actual.size());
      for (int index = 0; index < data.size(); index++) {
         DataAsserts.assertEquals(data.get(index), actual.get(index));
      }
   }
}
