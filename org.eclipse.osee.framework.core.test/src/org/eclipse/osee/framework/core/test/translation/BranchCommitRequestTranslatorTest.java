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
import org.eclipse.osee.framework.core.cache.BranchCache;
import org.eclipse.osee.framework.core.data.BranchCommitRequest;
import org.eclipse.osee.framework.core.data.IBasicArtifact;
import org.eclipse.osee.framework.core.enums.CoreTranslatorId;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.services.IDataTranslationService;
import org.eclipse.osee.framework.core.services.IOseeCachingServiceProvider;
import org.eclipse.osee.framework.core.services.IOseeModelFactoryServiceProvider;
import org.eclipse.osee.framework.core.test.mocks.MockDataFactory;
import org.eclipse.osee.framework.core.translation.BasicArtifactTranslator;
import org.eclipse.osee.framework.core.translation.BranchCommitRequestTranslator;
import org.eclipse.osee.framework.core.translation.BranchTranslator;
import org.eclipse.osee.framework.core.translation.DataTranslationService;
import org.eclipse.osee.framework.core.translation.ITranslator;
import org.eclipse.osee.framework.core.translation.TransactionRecordTranslator;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

/**
 * Test Case for {@link BranchCommitRequestTranslator}
 * 
 * @author Megumi Telles
 */
@RunWith(Parameterized.class)
public class BranchCommitRequestTranslatorTest extends BaseTranslatorTest<BranchCommitRequest> {

   public BranchCommitRequestTranslatorTest(BranchCommitRequest data, ITranslator<BranchCommitRequest> translator) {
      super(data, translator);
   }

   @Override
   protected void checkEquals(BranchCommitRequest expected, BranchCommitRequest actual) throws OseeCoreException {
      DataAsserts.assertEquals(expected, actual);
   }

   @Parameters
   public static Collection<Object[]> data() throws OseeCoreException {
      List<Object[]> data = new ArrayList<Object[]>();
      IOseeCachingServiceProvider serviceProvider = MockDataFactory.createCachingProvider();
      IOseeModelFactoryServiceProvider factoryProvider = MockDataFactory.createFactoryProvider();
      IDataTranslationService service = new DataTranslationService();
      service.addTranslator(new BranchTranslator(service, factoryProvider), CoreTranslatorId.BRANCH);
      service.addTranslator(new BasicArtifactTranslator(), CoreTranslatorId.ARTIFACT_METADATA);
      service.addTranslator(new TransactionRecordTranslator(factoryProvider), CoreTranslatorId.TRANSACTION_RECORD);

      ITranslator<BranchCommitRequest> translator = new BranchCommitRequestTranslator(service);

      BranchCache cache = serviceProvider.getOseeCachingService().getBranchCache();
      boolean archive = false;
      for (int index = 1; index <= 2; index++) {
         Branch srcBranch = MockDataFactory.createBranch(index);
         Branch destBranch = MockDataFactory.createBranch(index * 7);
         cache.cache(srcBranch);
         cache.cache(destBranch);
         IBasicArtifact<?> user = MockDataFactory.createArtifact(index);
         archive ^= archive;
         data.add(new Object[] {new BranchCommitRequest(user, srcBranch, destBranch, archive), translator});
      }
      data.add(new Object[] {new BranchCommitRequest(null, null, null, archive), translator});
      return data;
   }
}
