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
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.model.OseeCachingService;
import org.eclipse.osee.framework.core.services.IDataTranslationService;
import org.eclipse.osee.framework.core.services.IOseeCachingService;
import org.eclipse.osee.framework.core.services.IOseeCachingServiceProvider;
import org.eclipse.osee.framework.core.test.mocks.MockDataFactory;
import org.eclipse.osee.framework.core.test.mocks.MockOseeCachingServiceProvider;
import org.eclipse.osee.framework.core.translation.BasicArtifactTranslator;
import org.eclipse.osee.framework.core.translation.BranchTranslator;
import org.eclipse.osee.framework.core.translation.DataTranslationService;
import org.eclipse.osee.framework.core.translation.ITranslator;
import org.junit.Assert;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

/**
 * Test Case For {@link BasicArtifactTranslator}
 * 
 * @author Roberto E. Escobar
 */
@RunWith(Parameterized.class)
public class BranchTranslatorTest extends BaseTranslatorTest<Branch> {

   private static BranchCache cache;

   public BranchTranslatorTest(Branch data, ITranslator<Branch> translator) {
      super(data, translator);
   }

   @Override
   protected void checkEquals(Branch expected, Branch actual) throws OseeCoreException {
      boolean isCached = cache.getByGuid(expected.getGuid()) != null;
      if (isCached) {
         Assert.assertSame(expected, actual);
         DataAsserts.assertEquals(expected, actual);
      } else {
         Assert.assertNull(actual);
      }
   }

   @Parameters
   public static Collection<Object[]> data() throws OseeCoreException {
      IOseeCachingServiceProvider serviceProvider = MockDataFactory.createCachingProvider();
      cache = serviceProvider.getOseeCachingService().getBranchCache();

      ITranslator<Branch> translator = new BranchTranslator(serviceProvider);

      IDataTranslationService service = new DataTranslationService();
      service.addTranslator(translator, Branch.class);

      List<Object[]> data = new ArrayList<Object[]>();
      for (int index = 1; index <= 5; index++) {
         Branch branch = MockDataFactory.createBranch(index * 10);
         cache.cache(branch);
         data.add(new Object[] {branch, translator});
      }
      Branch branch = MockDataFactory.createBranch(-1);
      cache.cache(branch);
      data.add(new Object[] {branch, translator});

      // Don't add it to the cache
      data.add(new Object[] {MockDataFactory.createBranch(-2), translator});
      return data;
   }

   public static IOseeCachingServiceProvider createProvider(BranchCache cache) {
      IOseeCachingService service = new OseeCachingService(cache, null, null, null, null, null);
      return new MockOseeCachingServiceProvider(service);
   }
}
