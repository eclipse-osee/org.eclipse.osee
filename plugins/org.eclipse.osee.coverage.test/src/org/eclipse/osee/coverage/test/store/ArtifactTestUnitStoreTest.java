/*******************************************************************************
 * Copyright (c) 2011 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.coverage.test.store;

import java.util.Map.Entry;
import org.eclipse.osee.coverage.model.CoverageItem;
import org.eclipse.osee.coverage.model.CoverageOptionManager;
import org.eclipse.osee.coverage.model.CoverageOptionManagerDefault;
import org.eclipse.osee.coverage.model.CoverageUnit;
import org.eclipse.osee.coverage.model.CoverageUnitFactory;
import org.eclipse.osee.coverage.store.ArtifactTestUnitStore;
import org.eclipse.osee.coverage.store.TestUnitCache;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionManager;
import org.eclipse.osee.support.test.util.TestUtil;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @author John Misinco
 */
public class ArtifactTestUnitStoreTest {

   private static final String testInputData = "1|test1\n2|test2\n3|test3";
   private Branch testBranch;

   private CoverageItem createCoverageItem(TestUnitCache tc) throws OseeCoreException {
      CoverageUnit parent = CoverageUnitFactory.createCoverageUnit(null, "Top", "C:/UserData/", null);
      CoverageItem ci1 = new CoverageItem(parent, CoverageOptionManager.Deactivated_Code, "1");
      ci1.setName("this is text");
      return CoverageItem.createCoverageItem(parent, ci1.toXml(), CoverageOptionManagerDefault.instance(), tc);
   }

   @Before
   public void createTestArtifact() throws OseeCoreException {
      Assert.assertTrue(TestUtil.isTestDb());
      testBranch = BranchManager.createTopLevelBranch("TestBranch");
      Artifact testArtifact =
         ArtifactQuery.getOrCreate(ArtifactTestUnitStore.COVERAGE_GUID, null, CoreArtifactTypes.GeneralData, testBranch);
      testArtifact.setSoleAttributeFromString(CoreAttributeTypes.GeneralStringData, testInputData);
   }

   @After
   public void cleanUpTestArtifact() {
      BranchManager.deleteBranch(testBranch);
   }

   @Test
   public void testLoad() throws OseeCoreException {
      ArtifactTestUnitStore store = new ArtifactTestUnitStore(testBranch);
      TestUnitCache tc = new TestUnitCache(store);
      store.load(tc);
      StringBuilder actual = new StringBuilder();
      boolean firstTime = true;
      for (Entry<Integer, String> entry : tc.getAllCachedTestUnitEntries()) {
         if (!firstTime) {
            actual.append("\n");
         }
         actual.append(Integer.toString(entry.getKey()));
         actual.append("|");
         actual.append(entry.getValue());
         firstTime = false;
      }
      Assert.assertEquals(testInputData, actual.toString());
   }

   @Test
   public void testStore() throws OseeCoreException {

      ArtifactTestUnitStore store = new ArtifactTestUnitStore(testBranch);
      TestUnitCache tc = new TestUnitCache(store);
      CoverageItem ci = createCoverageItem(tc);
      ci.addTestUnitName("test1");
      ci.addTestUnitName("test10");

      store.store(tc);

      SkynetTransaction transaction = TransactionManager.createTransaction(testBranch, getClass().getSimpleName());
      Artifact testArtifact =
         ArtifactQuery.getOrCreate(ArtifactTestUnitStore.COVERAGE_GUID, null, CoreArtifactTypes.GeneralData, testBranch);
      String actual = testArtifact.getSoleAttributeValueAsString(CoreAttributeTypes.GeneralStringData, "");
      String expected = testInputData + "\n4|test10";
      Assert.assertEquals(expected, actual);
      testArtifact.persist(transaction);
      transaction.execute();
   }
}
