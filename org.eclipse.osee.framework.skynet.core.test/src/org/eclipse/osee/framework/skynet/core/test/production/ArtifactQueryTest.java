package org.eclipse.osee.framework.skynet.core.test.production;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import junit.framework.TestCase;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactPersistenceManager;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;

/**
 * @author Ryan D. Brooks
 */
public class ArtifactQueryTest extends TestCase {

   public void testGetArtifactFromId() throws OseeCoreException {
      Branch common = BranchManager.getCommonBranch();
      Artifact root = ArtifactPersistenceManager.getDefaultHierarchyRootArtifact(common);
      Artifact artifact = ArtifactQuery.getArtifactFromId(root.getHumanReadableId(), common);
      assertEquals(root.getHumanReadableId(), artifact.getHumanReadableId());
   }

   public void testGetArtifactsFromBranch() throws OseeCoreException {
      Branch common = BranchManager.getCommonBranch();
      List<Artifact> artifacts = ArtifactQuery.getArtifactsFromBranch(common, true);

      assertTrue(artifacts.size() > 0);
      for (Artifact artifact : artifacts) {
         assertTrue(artifact.getDescriptiveName().length() > 0);
         artifact.isOrphan(); // this is good exercise - like doing push-ups
      }
   }

   public void testQuickSearch() throws Exception {
      Branch branch = BranchManager.getKeyedBranch("V13 FTB0");
      List<Artifact> artifacts =
            ArtifactQuery.getArtifactsFromAttributeWithKeywords("[.PRE_RETRIES]", true, false, false, branch);
      Collections.sort(artifacts);
      Artifact[] results = artifacts.toArray(new Artifact[artifacts.size()]);
      assertEquals(
            "[PRESET_DATABASE Local Data Definition, {MODEM_PARAMETER_SELECTION} Procedure, {RETRIES} Display Logic]",
            Arrays.deepToString(results));

      artifacts = ArtifactQuery.getArtifactsFromAttributeWithKeywords("[.PRE_RETRIES]", true, false, true, branch);
      Collections.sort(artifacts);
      results = artifacts.toArray(new Artifact[artifacts.size()]);
      assertEquals(
            "[PRESET_DATABASE Local Data Definition, {MODEM_PARAMETER_SELECTION} Procedure, {RETRIES} Display Logic, {UPDATE_LONGBOW_NET_PARAMETERS} Procedure]",
            Arrays.deepToString(results));

      artifacts = ArtifactQuery.getArtifactsFromAttributeWithKeywords("edit_ale_net_sel", true, false, false, branch);
      Collections.sort(artifacts);
      results = artifacts.toArray(new Artifact[artifacts.size()]);
      assertEquals("[{EDIT_ALE_NET_SEL} Display Logic, {EDIT_FREQ_UIG} Display Logic]", Arrays.deepToString(results));
   }
}