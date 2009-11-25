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
import org.eclipse.osee.framework.core.data.BranchCommitResponse;
import org.eclipse.osee.framework.core.enums.CoreTranslatorId;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.TransactionRecord;
import org.eclipse.osee.framework.core.services.IDataTranslationService;
import org.eclipse.osee.framework.core.services.IOseeModelFactoryServiceProvider;
import org.eclipse.osee.framework.core.test.mocks.MockDataFactory;
import org.eclipse.osee.framework.core.translation.BranchCommitResponseTranslator;
import org.eclipse.osee.framework.core.translation.DataTranslationService;
import org.eclipse.osee.framework.core.translation.ITranslator;
import org.eclipse.osee.framework.core.translation.TransactionRecordTranslator;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

/**
 * Test Case for {@link BranchCommitResponseTranslator}
 * 
 * @author Megumi Telles
 */
@RunWith(Parameterized.class)
public class BranchCommitResponseTranslatorTest extends BaseTranslatorTest<BranchCommitResponse> {

   public BranchCommitResponseTranslatorTest(BranchCommitResponse data, ITranslator<BranchCommitResponse> translator) {
      super(data, translator);
   }

   @Override
   protected void checkEquals(BranchCommitResponse expected, BranchCommitResponse actual) throws OseeCoreException {
      DataAsserts.assertEquals(expected, actual);
   }

   @Parameters
   public static Collection<Object[]> data() throws OseeCoreException {
      IOseeModelFactoryServiceProvider factoryProvider = MockDataFactory.createFactoryProvider();

      List<Object[]> data = new ArrayList<Object[]>();
      IDataTranslationService service = new DataTranslationService();
      service.addTranslator(new TransactionRecordTranslator(factoryProvider), CoreTranslatorId.TRANSACTION_RECORD);

      ITranslator<BranchCommitResponse> translator = new BranchCommitResponseTranslator(service);
      for (int index = 1; index <= 2; index++) {
         TransactionRecord tx = MockDataFactory.createTransaction(index, index * 3);

         BranchCommitResponse response = new BranchCommitResponse();
         response.setTransaction(tx);
         data.add(new Object[] {response, translator});
      }
      data.add(new Object[] {new BranchCommitResponse(), translator});
      return data;
   }
}
