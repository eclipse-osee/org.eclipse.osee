/*
 * Created on Oct 27, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.coverage.test.model;

import org.eclipse.osee.coverage.model.CoverageItem;
import org.eclipse.osee.coverage.model.CoverageMethodEnum;
import org.eclipse.osee.coverage.model.CoverageTestUnit;
import org.eclipse.osee.coverage.model.CoverageUnit;
import org.eclipse.osee.coverage.test.util.CoverageTestUtil;
import org.eclipse.osee.coverage.util.CoverageUtil;
import org.eclipse.osee.framework.core.exception.ArtifactDoesNotExist;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.StaticIdManager;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author Donald G. Dunne
 */
public class CoverageItemPersistTest {

   public static CoverageUnit parent = null;
   public static CoverageItem ci1 = null;
   public static String parentGuid = null;
   public static String guid = null;

   @BeforeClass
   @AfterClass
   public static void testCleanup() throws OseeCoreException {
      CoverageTestUtil.cleanupCoverageTests();
   }

   @BeforeClass
   public static void testSetup() throws OseeCoreException {
      Assert.assertEquals(0, CoverageTestUtil.getAllCoverageArtifacts().size());

      parent = new CoverageUnit(null, "Top", "C:/UserData/");
      parentGuid = parent.getGuid();
      ci1 = new CoverageItem(parent, CoverageMethodEnum.Deactivated_Code, "1");
      for (int x = 0; x < 10; x++) {
         ci1.addTestUnit(new CoverageTestUnit("Test Unit " + x, "C:\\UserData\\"));
      }
      ci1.setLineNum("55");
      ci1.setMethodNum("33");
      ci1.setGuid("asdf");
      ci1.setCoverageRationale("this is rationale");
      ci1.setText("this is text");
      guid = ci1.getGuid();
   }

   /**
    * Test method for {@link org.eclipse.osee.coverage.model.CoverageItem#getArtifact(boolean)}.
    */
   @Test
   public void testGetArtifact() throws OseeCoreException {
      try {
         ArtifactQuery.getArtifactFromId(parentGuid, CoverageUtil.getBranch());
         Assert.fail("Artifact should not yet exist");
      } catch (ArtifactDoesNotExist ex) {
         // do nothing
      }

      Artifact artifact = parent.getArtifact(false);
      Assert.assertNull("Artifact should not have been created", artifact);
      artifact = parent.getArtifact(true);
      StaticIdManager.setSingletonAttributeValue(artifact, CoverageTestUtil.COVERAGE_STATIC_ID);
      Assert.assertNotNull("Artifact should have been created", artifact);
   }

   /**
    * Test method for
    * {@link org.eclipse.osee.coverage.model.CoverageItem#save(org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction)}
    */
   @Test
   public void testSave() throws OseeCoreException {
      Artifact artifact = parent.getArtifact(true);
      Assert.assertNotNull(artifact);
      SkynetTransaction transaction = new SkynetTransaction(CoverageUtil.getBranch(), "Save CoverageItem");
      parent.save(transaction);
      transaction.execute();
      for (CoverageTestUnit testUnit : ci1.getTestUnits()) {
         Artifact testArt = testUnit.getArtifact(false);
         Assert.assertNotNull(String.format("TestUnit [%s] should have been created", testUnit.getName()), testArt);
         StaticIdManager.setSingletonAttributeValue(testArt, CoverageTestUtil.COVERAGE_STATIC_ID);
      }
   }

   /**
    * Test method for {@link org.eclipse.osee.coverage.model.CoverageItem#getArtifact(boolean)}.
    */
   @Test
   public void testGetArtifact2() throws OseeCoreException {
      parent.load();
      CoverageItem ci = parent.getCoverageItems().iterator().next();
      Assert.assertEquals(guid, ci.getGuid());
      Assert.assertEquals("33", ci.getMethodNum());
      Assert.assertEquals("1", ci.getExecuteNum());
      Assert.assertEquals("55", ci.getLineNum());
      Assert.assertEquals(CoverageMethodEnum.Deactivated_Code, ci.getCoverageMethod());
      Assert.assertEquals(10, ci.getTestUnits().size());
      Assert.assertEquals("this is text", ci.getText());
      Assert.assertEquals("this is rationale", ci.getCoverageRationale());
      Assert.assertFalse(ci.isFolder());
   }

   /**
    * Test method for
    * {@link org.eclipse.osee.coverage.model.CoverageItem#delete(org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction, boolean)}
    * .
    */
   @Test
   public void testDelete() throws OseeCoreException {
      Artifact artifact = parent.getArtifact(false);
      Assert.assertNotNull(artifact);
      SkynetTransaction transaction = new SkynetTransaction(CoverageUtil.getBranch(), "Save CoverageItem");
      parent.delete(transaction, false);
      transaction.execute();
      artifact = parent.getArtifact(false);
      Assert.assertTrue(artifact.isDeleted());
      Assert.assertEquals(0, CoverageTestUtil.getAllCoverageArtifacts().size());
   }

}
