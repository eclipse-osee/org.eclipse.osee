/*
 * Created on Oct 27, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.coverage.test.model;

import org.eclipse.osee.coverage.model.CoverageItem;
import org.eclipse.osee.coverage.model.CoverageOptionManager;
import org.eclipse.osee.coverage.model.CoverageOptionManagerDefault;
import org.eclipse.osee.coverage.model.CoverageUnit;
import org.eclipse.osee.coverage.store.OseeCoverageUnitStore;
import org.eclipse.osee.coverage.test.util.CoverageTestUtil;
import org.eclipse.osee.coverage.util.CoverageUtil;
import org.eclipse.osee.framework.core.exception.ArtifactDoesNotExist;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
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

   public static CoverageUnit parentCu = null;
   public static CoverageItem ci = null;
   public static String parentGuid = null;
   public static String guid = null;

   @AfterClass
   public static void testCleanup() throws OseeCoreException {
      CoverageTestUtil.cleanupCoverageTests();
   }

   @BeforeClass
   public static void testSetup() throws OseeCoreException {
      CoverageUtil.setBranch(BranchManager.getCommonBranch());
      CoverageTestUtil.cleanupCoverageTests();
      // If this fails, cleanup didn't happen.  Must DbInit
      Assert.assertEquals(0, CoverageTestUtil.getAllCoverageArtifacts().size());

      parentCu = new CoverageUnit(null, "Top", "C:/UserData/", null);
      parentGuid = parentCu.getGuid();
      ci = new CoverageItem(parentCu, CoverageOptionManager.Deactivated_Code, "1");
      for (int x = 0; x < 10; x++) {
         ci.addTestUnitName("Test Unit " + x);
      }
      ci.setRationale("this is rationale");
      ci.setName("this is text");
      guid = ci.getGuid();
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

      Artifact artifact = (new OseeCoverageUnitStore(parentCu)).getArtifact(false);
      Assert.assertNull("Artifact should not have been created", artifact);
      artifact = (new OseeCoverageUnitStore(parentCu)).getArtifact(true);
      CoverageTestUtil.registerAsTestArtifact(artifact);
      artifact.persist();
      Assert.assertNotNull("Artifact should have been created", artifact);
   }

   /**
    * Test method for
    * {@link org.eclipse.osee.coverage.model.CoverageItem#save(org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction)}
    */
   @Test
   public void testSave() throws OseeCoreException {
      // Since test units are stored through provider, ensure they are same before and after save
      Assert.assertEquals(10, ci.getTestUnits().size());

      Artifact artifact = (new OseeCoverageUnitStore(parentCu)).getArtifact(true);
      Assert.assertNotNull(artifact);
      SkynetTransaction transaction = new SkynetTransaction(CoverageUtil.getBranch(), "Save CoverageItem");
      (new OseeCoverageUnitStore(parentCu)).save(transaction);
      transaction.execute();

      Assert.assertEquals(10, ci.getTestUnits().size());
   }

   /**
    * Test method for {@link org.eclipse.osee.coverage.model.CoverageItem#getArtifact(boolean)}.
    */
   @Test
   public void testGetArtifact2() throws OseeCoreException {
      OseeCoverageUnitStore.get(parentCu).load(CoverageOptionManagerDefault.instance());
      CoverageItem ci = parentCu.getCoverageItems().iterator().next();
      Assert.assertEquals(guid, ci.getGuid());
      Assert.assertEquals("1", ci.getOrderNumber());
      Assert.assertEquals(CoverageOptionManager.Deactivated_Code, ci.getCoverageMethod());
      Assert.assertEquals(10, ci.getTestUnits().size());
      Assert.assertEquals("this is text", ci.getFileContents());
      Assert.assertEquals("this is rationale", ci.getRationale());
      Assert.assertFalse(ci.isFolder());
   }

   /**
    * Test method for
    * {@link org.eclipse.osee.coverage.model.CoverageItem#delete(org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction, boolean)}
    * .
    */
   @Test
   public void testDelete() throws OseeCoreException {
      Artifact artifact = (new OseeCoverageUnitStore(parentCu)).getArtifact(false);
      Assert.assertNotNull(artifact);
      SkynetTransaction transaction = new SkynetTransaction(CoverageUtil.getBranch(), "Save CoverageItem");
      (new OseeCoverageUnitStore(parentCu)).delete(transaction, false);
      transaction.execute();
      artifact = (new OseeCoverageUnitStore(parentCu)).getArtifact(false);
      Assert.assertTrue(artifact.isDeleted());
      Assert.assertEquals(0, CoverageTestUtil.getAllCoverageArtifacts().size());
   }

}
