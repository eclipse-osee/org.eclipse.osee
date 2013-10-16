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
package org.eclipse.osee.coverage.integration.tests.integration;

import static org.eclipse.osee.coverage.demo.CoverageChoice.OSEE_COVERAGE_DEMO;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import org.eclipse.osee.client.test.framework.OseeClientIntegrationRule;
import org.eclipse.osee.client.test.framework.OseeLogMonitorRule;
import org.eclipse.osee.coverage.integration.tests.integration.util.CoverageTestUtil;
import org.eclipse.osee.coverage.model.CoverageItem;
import org.eclipse.osee.coverage.model.CoverageOptionManager;
import org.eclipse.osee.coverage.model.CoverageOptionManagerDefault;
import org.eclipse.osee.coverage.model.CoverageUnit;
import org.eclipse.osee.coverage.model.CoverageUnitFactory;
import org.eclipse.osee.coverage.model.ITestUnitProvider;
import org.eclipse.osee.coverage.store.ArtifactTestUnitStore;
import org.eclipse.osee.coverage.store.CoverageArtifactTypes;
import org.eclipse.osee.coverage.store.CoverageAttributeTypes;
import org.eclipse.osee.coverage.store.TestUnitCache;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionManager;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author John R. Misinco
 */
public class ArtifactTestUnitStoreTest {

   private static final String testInputDataReadOnlyList = "1|test1\n2|test2\n3|test3";
   private static final String testInputDataCoverageArtifact = "4|test4\n5|test5\n6|test6";
   private static final String coverageTestGuid = "Bs+PvSVQf4Z4EHSTcyQB";
   private static Artifact readOnlyTestUnitNames;
   private Branch testTopBranch;
   private Branch testWorkingBranch;

   @Rule
   public OseeClientIntegrationRule integration = new OseeClientIntegrationRule(OSEE_COVERAGE_DEMO);
   @Rule
   public OseeLogMonitorRule monitorRule = new OseeLogMonitorRule();

   private CoverageItem createCoverageItem(ITestUnitProvider tc) throws OseeCoreException {
      CoverageUnit parent = CoverageUnitFactory.createCoverageUnit(null, "Top", "C:/UserData/", null);
      CoverageItem ci1 = new CoverageItem(parent, CoverageOptionManager.Deactivated_Code, "1");
      ci1.setName("this is text");
      return CoverageItem.createCoverageItem(parent, ci1.toXml(), CoverageOptionManagerDefault.instance(), tc);
   }

   @Before
   public void createTestArtifact() throws OseeCoreException {
      testTopBranch = BranchManager.createTopLevelBranch("Test Top Level Branch");
      testWorkingBranch = BranchManager.createWorkingBranch(CoverageTestUtil.getTestBranch(), "Test Working Branch");
      readOnlyTestUnitNames =
         ArtifactQuery.getOrCreate(ArtifactTestUnitStore.READ_ONLY_GUID, CoreArtifactTypes.GeneralData, testTopBranch);
      readOnlyTestUnitNames.setSoleAttributeFromString(CoreAttributeTypes.GeneralStringData, testInputDataReadOnlyList);
   }

   @After
   public void cleanUpTestArtifact() {
      BranchManager.deleteBranch(testTopBranch);
      BranchManager.deleteBranch(testWorkingBranch);
   }

   @Test
   public void testLoad() throws OseeCoreException {
      ArtifactTestUnitStore store = new ArtifactTestUnitStore(null, readOnlyTestUnitNames);
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
      Assert.assertEquals(testInputDataReadOnlyList, actual.toString());
   }

   @Test
   public void testStore() throws OseeCoreException {

      Artifact testArtifact =
         ArtifactQuery.getOrCreate(coverageTestGuid, CoverageArtifactTypes.CoveragePackage, testWorkingBranch);
      testArtifact.setSoleAttributeFromString(CoverageAttributeTypes.UnitTestTable, testInputDataCoverageArtifact);

      ArtifactTestUnitStore store = new ArtifactTestUnitStore(testArtifact, readOnlyTestUnitNames);
      TestUnitCache tc = new TestUnitCache(store);
      CoverageItem ci = createCoverageItem(tc);
      ci.addTestUnitName("test1");

      SkynetTransaction transaction =
         TransactionManager.createTransaction(testWorkingBranch, getClass().getSimpleName());
      store.store(tc, transaction);

      String actual = testArtifact.getSoleAttributeValueAsString(CoverageAttributeTypes.UnitTestTable, "");
      String expected = "1|test1\n" + testInputDataCoverageArtifact;
      Assert.assertEquals(expected, actual);
      testArtifact.persist(transaction);
      transaction.execute();
   }

   @Test
   public void testCoveragePackageEmptyTestUnitTableAttr() throws OseeCoreException {
      // Test when CoveragePackageArtifact has an empty UnitTestTable the ArtifactStore gets testUnits from the readOnly Artifact
      Artifact testCoverageArtifact =
         ArtifactQuery.getOrCreate(coverageTestGuid, CoverageArtifactTypes.CoveragePackage, testWorkingBranch);
      testCoverageArtifact.setSoleAttributeFromString(CoverageAttributeTypes.UnitTestTable, "");

      ArtifactTestUnitStore store = new ArtifactTestUnitStore(testCoverageArtifact, readOnlyTestUnitNames);
      TestUnitCache tc = new TestUnitCache(store);

      Collection<Entry<Integer, String>> allCachedTestUnitNames = tc.getAllCachedTestUnitEntries();
      Map<Integer, String> testUnitNamesList = new HashMap<Integer, String>();
      for (Entry<Integer, String> entry : allCachedTestUnitNames) {
         testUnitNamesList.put(entry.getKey(), entry.getValue());
      }

      String actual = testUnitNamesList.get(1);
      String expected = "test1";
      Assert.assertEquals(expected, actual);

      actual = testUnitNamesList.get(2);
      expected = "test2";
      Assert.assertEquals(expected, actual);

      actual = testUnitNamesList.get(3);
      expected = "test3";
      Assert.assertEquals(expected, actual);

   }

   @Test
   public void testCoveragePackageTestUnitTableAttr() throws OseeCoreException {

      Artifact testCoverageArtifact =
         ArtifactQuery.getOrCreate(coverageTestGuid, CoverageArtifactTypes.CoveragePackage, testWorkingBranch);
      testCoverageArtifact.setSoleAttributeFromString(CoverageAttributeTypes.UnitTestTable,
         testInputDataCoverageArtifact);

      ArtifactTestUnitStore store = new ArtifactTestUnitStore(testCoverageArtifact, readOnlyTestUnitNames);
      TestUnitCache tc = new TestUnitCache(store);

      Collection<Entry<Integer, String>> allCachedTestUnitNames = tc.getAllCachedTestUnitEntries();
      Map<Integer, String> testUnitNamesList = new HashMap<Integer, String>();
      for (Entry<Integer, String> entry : allCachedTestUnitNames) {
         testUnitNamesList.put(entry.getKey(), entry.getValue());
      }

      String actual = testUnitNamesList.get(4);
      String expected = "test4";
      Assert.assertEquals(expected, actual);

      actual = testUnitNamesList.get(5);
      expected = "test5";
      Assert.assertEquals(expected, actual);

      actual = testUnitNamesList.get(6);
      expected = "test6";
      Assert.assertEquals(expected, actual);

   }
}
