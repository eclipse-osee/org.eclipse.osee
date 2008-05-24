package org.eclipse.osee.framework.skynet.core.test;

import java.util.List;
import junit.framework.TestCase;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactPersistenceManager;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.artifact.BranchPersistenceManager;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;

/**
 * @author Ryan D. Brooks
 */
public class ArtifactQueryTest extends TestCase {

   public void testGetArtifactFromId() {
      try {
         Branch common = BranchPersistenceManager.getCommonBranch();
         Artifact root = ArtifactPersistenceManager.getDefaultHierarchyRootArtifact(common);
         Artifact artifact = ArtifactQuery.getArtifactFromId(root.getHumanReadableId(), common);
         assertEquals(root.getHumanReadableId(), artifact.getHumanReadableId());
      } catch (Exception ex) {
         fail(ex.getLocalizedMessage());
      }
   }

   public void testGetArtifactsFromBranch() {
      try {
         Branch common = BranchPersistenceManager.getCommonBranch();
         List<Artifact> artifacts = ArtifactQuery.getArtifactsFromBranch(common, true);

         assertTrue(artifacts.size() > 0);
         for (Artifact artifact : artifacts) {
            assertTrue(artifact.getDescriptiveName().length() > 0);
            artifact.getParent();
         }
      } catch (Exception ex) {
         fail(ex.getLocalizedMessage());
      }
   }
}