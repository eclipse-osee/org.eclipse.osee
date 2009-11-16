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
package org.eclipse.osee.framework.core.test.exchange;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.eclipse.osee.framework.core.IDataTranslationService;
import org.eclipse.osee.framework.core.data.Branch;
import org.eclipse.osee.framework.core.data.TransactionRecord;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exchange.BranchTranslator;
import org.eclipse.osee.framework.core.exchange.DataTranslationService;
import org.eclipse.osee.framework.core.exchange.IDataTranslator;
import org.eclipse.osee.framework.core.exchange.TransactionRecordTranslator;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

/**
 * Test Case For {@link TransactionRecordTranslator}
 * 
 * @author Roberto E. Escobar
 */
@RunWith(Parameterized.class)
public class TransactionRecordTranslatorTest extends BaseTranslatorTest<TransactionRecord> {

   public TransactionRecordTranslatorTest(TransactionRecord data, IDataTranslator<TransactionRecord> translator) {
      super(data, translator);
   }

   @Override
   protected void checkEquals(TransactionRecord expected, TransactionRecord actual) throws OseeCoreException {
      DataUtility.assertEquals(expected, actual);
   }

   @Parameters
   public static Collection<Object[]> data() {
      IDataTranslationService service = new DataTranslationService();
      service.addTranslator(Branch.class, new BranchTranslator(service));
      service.addTranslator(TransactionRecord.class, new TransactionRecordTranslator(service));

      IDataTranslator<TransactionRecord> translator = new TransactionRecordTranslator(service);

      List<Object[]> data = new ArrayList<Object[]>();
      for (int index = 1; index <= 5; index++) {
         data.add(new Object[] {DataUtility.createTx(index * 10), translator});
      }
      data.add(new Object[] {DataUtility.createTx(-1), translator});
      return data;
   }

}
