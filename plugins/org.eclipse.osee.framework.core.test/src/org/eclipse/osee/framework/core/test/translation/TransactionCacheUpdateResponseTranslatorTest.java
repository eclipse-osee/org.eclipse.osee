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
package org.eclipse.osee.framework.core.test.translation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import junit.framework.Assert;
import org.eclipse.osee.framework.core.data.TransactionCacheUpdateResponse;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.TransactionRecord;
import org.eclipse.osee.framework.core.services.IOseeModelFactoryServiceProvider;
import org.eclipse.osee.framework.core.test.mocks.MockDataFactory;
import org.eclipse.osee.framework.core.translation.ITranslator;
import org.eclipse.osee.framework.core.translation.TransactionCacheUpdateResponseTranslator;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

/**
 * Test Case for {@link TransactionCacheUpdateResponseTranslator}
 * 
 * @author Roberto E. Escobar
 */
@RunWith(Parameterized.class)
public class TransactionCacheUpdateResponseTranslatorTest extends BaseTranslatorTest<TransactionCacheUpdateResponse> {

   public TransactionCacheUpdateResponseTranslatorTest(TransactionCacheUpdateResponse data, ITranslator<TransactionCacheUpdateResponse> translator) {
      super(data, translator);
   }

   @Override
   protected void checkEquals(TransactionCacheUpdateResponse expected, TransactionCacheUpdateResponse actual) throws OseeCoreException {
      Assert.assertNotSame(expected, actual);
      List<TransactionRecord> expectedRows = expected.getTxRows();
      List<TransactionRecord> actualRows = actual.getTxRows();
      Assert.assertEquals(expectedRows.size(), actualRows.size());
      for (int index = 0; index < expectedRows.size(); index++) {
         DataAsserts.assertEquals(expectedRows.get(index), actualRows.get(index));
      }
   }

   @Parameters
   public static Collection<Object[]> data() throws OseeCoreException {
      IOseeModelFactoryServiceProvider factoryProvider = MockDataFactory.createFactoryProvider();
      ITranslator<TransactionCacheUpdateResponse> translator =
            new TransactionCacheUpdateResponseTranslator(factoryProvider);

      List<Object[]> data = new ArrayList<Object[]>();
      for (int index = 1; index <= 2; index++) {
         List<TransactionRecord> txRecords = new ArrayList<TransactionRecord>();
         for (int j = 1; j <= index; j++) {
            txRecords.add(MockDataFactory.createTransaction(j, j * 3));
         }
         data.add(new Object[] {new TransactionCacheUpdateResponse(txRecords), translator});
      }
      return data;
   }
}
