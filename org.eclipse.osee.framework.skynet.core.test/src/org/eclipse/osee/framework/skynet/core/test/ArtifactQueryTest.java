package org.eclipse.osee.framework.skynet.core.test;

import java.sql.SQLException;
import java.util.List;
import junit.framework.TestCase;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactPersistenceManager;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.artifact.BranchPersistenceManager;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.exception.ArtifactDoesNotExist;
import org.eclipse.osee.framework.skynet.core.exception.MultipleArtifactsExist;

/**
 * @author Ryan D. Brooks
 */
public class ArtifactQueryTest extends TestCase {

   public void testGetArtifactFromId() throws MultipleArtifactsExist, ArtifactDoesNotExist, SQLException {
      Branch common = BranchPersistenceManager.getCommonBranch();
      Artifact root = ArtifactPersistenceManager.getDefaultHierarchyRootArtifact(common);
      Artifact artifact = ArtifactQuery.getArtifactFromId(root.getHumanReadableId(), common);
      assertEquals(root.getHumanReadableId(), artifact.getHumanReadableId());
   }

   public void testGetArtifactsFromBranch() throws SQLException, MultipleArtifactsExist, ArtifactDoesNotExist {
      Branch branch = BranchPersistenceManager.getCommonBranch();
      //List<Artifact> artifacts = ArtifactQuery.getArtifactsFromType("Software Requirement", branch);
      List<Artifact> artifacts = ArtifactQuery.getArtifactsFromBranch(branch, true);

      assertTrue(artifacts.size() > 0);
      for (Artifact artifact : artifacts) {
         System.out.println(artifact.getDescriptiveName());
         assertTrue(artifact.getDescriptiveName().length() > 0);
         artifact.isOrphan(); // this is good exercise like doing push-ups
      }
   }
}