/*
 * Created on Apr 9, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.coverage.test.model;

import org.eclipse.osee.coverage.model.CoverageOptionManagerDefault;
import org.eclipse.osee.coverage.model.CoveragePreferences;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.exception.ArtifactDoesNotExist;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author Donald G. Dunne
 */
public class CoveragePreferencesTest {

   @BeforeClass
   public static void setUp() throws OseeCoreException {
      SkynetTransaction transaction = new SkynetTransaction(BranchManager.getCommonBranch(), "delete");
      for (Artifact artifact : ArtifactQuery.getArtifactListFromTypeAndName(CoreArtifactTypes.GeneralData,
            "Coverage Preferences", BranchManager.getCommonBranch())) {
         artifact.deleteAndPersist(transaction);
      }
      transaction.execute();
   }

   private static Artifact getCoveragePrefArt() throws OseeCoreException {
      try {
         Artifact artifact =
               ArtifactQuery.getArtifactFromTypeAndName(CoreArtifactTypes.GeneralData, "Coverage Preferences",
                     BranchManager.getCommonBranch());
         return artifact;
      } catch (ArtifactDoesNotExist ex) {
         // do nothing
      }
      return null;
   }

   @Test
   public void testCoveragePreferences() throws OseeCoreException {
      Assert.assertNull(getCoveragePrefArt());
      CoveragePreferences prefs = new CoveragePreferences(BranchManager.getCommonBranch());
      Assert.assertNotNull(prefs);
      Assert.assertNull(prefs.getCoverageOptions());
      prefs.setCoverageOptions(CoverageOptionManagerDefault.instance().toXml());
      Assert.assertNotNull(getCoveragePrefArt());
      Assert.assertFalse(getCoveragePrefArt().isDirty());
      Assert.assertEquals(CoverageOptionManagerDefault.instance().toXml(), prefs.getCoverageOptions());
   }

}
