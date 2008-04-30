package org.eclipse.osee.framework.skynet.core.test;

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
         Artifact root = ArtifactPersistenceManager.getInstance().getDefaultHierarchyRootArtifact(common);
         Artifact artifact = ArtifactQuery.getArtifactFromId(root.getHumanReadableId(), common);
         assertEquals(root.getHumanReadableId(), artifact.getHumanReadableId());
      } catch (Exception ex) {
         fail(ex.getLocalizedMessage());
      }
   }
}