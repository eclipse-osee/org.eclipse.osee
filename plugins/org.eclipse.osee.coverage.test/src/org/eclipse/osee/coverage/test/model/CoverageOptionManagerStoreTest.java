/*
 * Created on Apr 12, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.coverage.test.model;

import junit.framework.Assert;
import org.eclipse.osee.coverage.model.CoverageOption;
import org.eclipse.osee.coverage.model.CoverageOptionManager;
import org.eclipse.osee.coverage.model.CoverageOptionManagerDefault;
import org.eclipse.osee.coverage.model.CoveragePackage;
import org.eclipse.osee.coverage.store.CoverageArtifactTypes;
import org.eclipse.osee.coverage.store.CoverageOptionManagerStore;
import org.eclipse.osee.coverage.store.OseeCoveragePackageStore;
import org.eclipse.osee.coverage.store.CoverageOptionManagerStore.StoreLocation;
import org.eclipse.osee.coverage.util.CoverageUtil;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.exception.ArtifactDoesNotExist;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.artifact.GeneralData;
import org.eclipse.osee.framework.skynet.core.artifact.KeyValueArtifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author Donald G. Dunne
 */
public class CoverageOptionManagerStoreTest {

   @BeforeClass
   public static void setUp() throws OseeCoreException {
      SkynetTransaction transaction = new SkynetTransaction(BranchManager.getCommonBranch(), "delete");
      for (Artifact artifact : ArtifactQuery.getArtifactListFromTypeAndName(CoverageArtifactTypes.CoveragePackage,
            CoverageOptionManagerStoreTest.class.getSimpleName(), BranchManager.getCommonBranch())) {
         artifact.deleteAndPersist(transaction);
      }
      Artifact artifact =
            ArtifactQuery.getArtifactFromTypeAndName(CoreArtifactTypes.GeneralData, "Coverage Preferences",
                  BranchManager.getCommonBranch());
      if (artifact != null) {
         KeyValueArtifact kvArt = new KeyValueArtifact(artifact, GeneralData.GENERAL_STRING_ATTRIBUTE_TYPE_NAME);
         kvArt.removeValues("CoverageOptions");
         kvArt.save();
         artifact.persist(transaction);
      }
      transaction.execute();
   }

   private static Artifact getCoveragePackageArt() throws OseeCoreException {
      try {
         Artifact artifact =
               ArtifactQuery.getArtifactFromTypeAndName(CoverageArtifactTypes.CoveragePackage,
                     CoverageOptionManagerStoreTest.class.getSimpleName(), BranchManager.getCommonBranch());
         return artifact;
      } catch (ArtifactDoesNotExist ex) {
         // do nothing
      }
      return null;
   }

   @Test
   public void testCoverageOptionManagerStore() throws OseeCoreException {
      CoverageUtil.setNavigatorSelectedBranch(BranchManager.getCommonBranch());
      CoveragePackage coveragePackage =
            new CoveragePackage(CoverageOptionManagerStoreTest.class.getSimpleName(),
                  CoverageOptionManagerDefault.instance());
      OseeCoveragePackageStore store = new OseeCoveragePackageStore(coveragePackage, BranchManager.getCommonBranch());
      store.save();

      CoverageOptionManagerStore optionStore = new CoverageOptionManagerStore(store);
      // Global option should not be created yet
      Assert.assertEquals(StoreLocation.None, optionStore.getStoreLocation());
      Assert.assertEquals(CoverageOptionManagerDefault.instance().toXml(),
            optionStore.getCoverageOptionManager().toXml());

      // add another coverage option and store globally
      CoverageOptionManager coverageOptionManager = coveragePackage.getCoverageOptionManager();
      // have to create new coverage manager cause can't change default options
      CoverageOptionManager newCoverageOptionManager = new CoverageOptionManager(coverageOptionManager.toXml());
      newCoverageOptionManager.add(new CoverageOption("Another", "description", true));
      String newXmlWithAnother = newCoverageOptionManager.toXml();
      // Make sure option xml changed after adding a new option
      Assert.assertNotSame(newXmlWithAnother, coverageOptionManager.toXml());
      optionStore.store(newCoverageOptionManager, StoreLocation.Global);

      // Re-acquire option store and ensure that xml has been updated globally
      optionStore = new CoverageOptionManagerStore(store);
      Assert.assertEquals(StoreLocation.Global, optionStore.getStoreLocation());
      Assert.assertEquals(newXmlWithAnother, optionStore.getCoverageOptionManager().toXml());

      newCoverageOptionManager.add(new CoverageOption("One More", "description", true));
      String newXmlWithOneMore = newCoverageOptionManager.toXml();
      // Make sure option xml changed after adding a new option
      Assert.assertTrue(newXmlWithOneMore.contains("One More"));
      optionStore.store(newCoverageOptionManager, StoreLocation.Local);

      // Re-acquire option store and ensure that xml has been updated globally
      optionStore = new CoverageOptionManagerStore(store);
      Assert.assertEquals(StoreLocation.Local, optionStore.getStoreLocation());
      Assert.assertEquals(newXmlWithOneMore, optionStore.getCoverageOptionManager().toXml());

   }

}
