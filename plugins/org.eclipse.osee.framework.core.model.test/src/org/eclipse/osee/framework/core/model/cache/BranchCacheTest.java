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
package org.eclipse.osee.framework.core.model.cache;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.model.mocks.MockDataFactory;
import org.eclipse.osee.framework.core.model.mocks.MockOseeDataAccessor;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.junit.Assert;
import org.junit.BeforeClass;

/**
 * Test Case for {@link BranchCache}
 *
 * @author Roberto E. Escobar
 */
public class BranchCacheTest extends AbstractOseeCacheTest<Branch> {

   private static List<Branch> branchData;
   private static BranchCache cache;

   @BeforeClass
   public static void prepareTestData() throws OseeCoreException {
      branchData = new ArrayList<>();

      BranchDataAccessor branchAccessor = new BranchDataAccessor(branchData);
      cache = new BranchCache(branchAccessor, new TransactionCache());
      cache.ensurePopulated();

      Assert.assertTrue(branchAccessor.wasLoaded());

   }

   public BranchCacheTest() {
      super(branchData, cache);
   }

   private final static class BranchDataAccessor extends MockOseeDataAccessor<Branch> {

      private final List<Branch> data;

      public BranchDataAccessor(List<Branch> data) {
         super();
         this.data = data;
      }

      @Override
      public void load(IOseeCache<Branch> cache) throws OseeCoreException {
         super.load(cache);
         int typeId = 100;
         for (int index = 0; index < 10; index++) {
            Branch item = MockDataFactory.createBranch(typeId++, index);
            data.add(item);
            cache.cache(item);
         }
      }
   }

   @Override
   protected Long createKey() {
      return Lib.generateUuid();
   }
}
