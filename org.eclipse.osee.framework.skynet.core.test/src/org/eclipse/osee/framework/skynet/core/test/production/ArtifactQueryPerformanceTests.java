/*
 * Created on Nov 13, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.skynet.core.test.production;

import static org.eclipse.osee.framework.skynet.core.artifact.ArtifactLoad.FULL;
import java.util.ArrayList;
import java.util.List;
import junit.framework.TestCase;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactPersistenceManager;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQueryBuilder;

/**
 * @author Andrew M Finkbeiner
 */
public class ArtifactQueryPerformanceTests extends TestCase {

   /*  constructors to test
   
   no good way to get id's for test so we son't test these two.
   
   public ArtifactQueryBuilder(int artId, Branch branch, boolean allowDeleted, ArtifactLoad loadLevel) {
      this(null, artId, null, null, null, branch, allowDeleted, loadLevel, true);
   }
   public ArtifactQueryBuilder(Collection<Integer> artifactIds, Branch branch, boolean allowDeleted, ArtifactLoad loadLevel) {
      this(artifactIds, 0, null, null, null, branch, allowDeleted, loadLevel, true);
      emptyCriteria = artifactIds.size() == 0;
   }
   
   public ArtifactQueryBuilder(Branch branch, ArtifactLoad loadLevel, boolean allowDeleted, AbstractArtifactSearchCriteria... criteria) {
      this(null, 0, null, null, null, branch, allowDeleted, loadLevel, true, criteria);
      emptyCriteria = criteria.length == 0;
   }

   public ArtifactQueryBuilder(Branch branch, ArtifactLoad loadLevel, List<AbstractArtifactSearchCriteria> criteria) {
      this(null, 0, null, null, null, branch, false, loadLevel, true, toArray(criteria));
      emptyCriteria = criteria.size() == 0;
   }

   public ArtifactQueryBuilder(ArtifactType artifactType, Branch branch, ArtifactLoad loadLevel, AbstractArtifactSearchCriteria... criteria) {
      this(null, 0, null, null, Arrays.asList(artifactType), branch, false, loadLevel, true, criteria);
      emptyCriteria = criteria.length == 0;
   }

   public ArtifactQueryBuilder(ArtifactType artifactType, Branch branch, ArtifactLoad loadLevel, List<AbstractArtifactSearchCriteria> criteria) {
      this(null, 0, null, null, Arrays.asList(artifactType), branch, false, loadLevel, true, toArray(criteria));
      emptyCriteria = criteria.size() == 0;
   }
    */

   public void testGetArtifactByHRID() throws OseeCoreException {
      Branch common = BranchManager.getCommonBranch();
      Artifact art = ArtifactPersistenceManager.getDefaultHierarchyRootArtifact(common);
      ArtifactQueryBuilder builder = new ArtifactQueryBuilder(art.getHumanReadableId(), common, true, FULL);
      long startTime = System.currentTimeMillis();
      Artifact result = builder.getArtifact();
      long elapsedTime = System.currentTimeMillis() - startTime;
      System.out.println(String.format("testGetArtifactByHRID took %dms", elapsedTime));
      assertNotNull("No artifact found", result);
      assertTrue(String.format("Elapsed time for artifact by hrid query took %dms.  It should take less than 100ms.",
            elapsedTime), elapsedTime < 100);
   }

   public void testGetArtifactsByHRID() throws OseeCoreException {
      Branch common = BranchManager.getCommonBranch();
      Artifact art = ArtifactPersistenceManager.getDefaultHierarchyRootArtifact(common);
      List<String> hrids = new ArrayList<String>();
      List<Artifact> children = art.getChildren();
      for (Artifact child : children) {
         hrids.add(child.getHumanReadableId());
      }
      ArtifactQueryBuilder builder = new ArtifactQueryBuilder(hrids, common, true, FULL);
      long startTime = System.currentTimeMillis();
      List<Artifact> result = builder.getArtifacts(children.size() + 1, null);
      long elapsedTime = System.currentTimeMillis() - startTime;
      System.out.println(String.format("testGetArtifactsByHRID took %dms for %d artifacts", elapsedTime, result.size()));
      assertTrue("No artifacts found", result.size() > 0);
      assertTrue(String.format("Elapsed time for artifact by hrid query took %dms.  It should take less than 50ms.",
            elapsedTime), elapsedTime < 50);
   }

   public void testGetArtifactsByHRIDNoDeleted() throws OseeCoreException {
      Branch common = BranchManager.getCommonBranch();
      Artifact art = ArtifactPersistenceManager.getDefaultHierarchyRootArtifact(common);
      List<String> hrids = new ArrayList<String>();
      List<Artifact> children = art.getChildren();
      for (Artifact child : children) {
         hrids.add(child.getHumanReadableId());
      }
      ArtifactQueryBuilder builder = new ArtifactQueryBuilder(hrids, common, false, FULL);
      long startTime = System.currentTimeMillis();
      List<Artifact> result = builder.getArtifacts(children.size() + 1, null);
      long elapsedTime = System.currentTimeMillis() - startTime;
      System.out.println(String.format("testGetArtifactsByHRIDNoDeleted took %dms for %d artifacts", elapsedTime,
            result.size()));
      assertTrue("No artifacts found", result.size() > 0);
      assertTrue(String.format("Elapsed time for artifact by hrid query took %dms.  It should take less than 50ms.",
            elapsedTime), elapsedTime < 50);
   }

   public void testGetArtifactsByArtType() throws OseeCoreException {
      Branch common = BranchManager.getCommonBranch();
      ArtifactQueryBuilder builder =
            new ArtifactQueryBuilder(ArtifactTypeManager.getType("Team Definition"), common, FULL);
      long startTime = System.currentTimeMillis();
      List<Artifact> result = builder.getArtifacts(1000, null);
      long elapsedTime = System.currentTimeMillis() - startTime;
      System.out.println(String.format("testGetArtifactsByArtType took %dms for %d artifacts", elapsedTime,
            result.size()));
      assertTrue("No artifacts found", result.size() > 0);
      assertTrue(String.format(
            "Elapsed time for testGetArtifactsByArtType took %dms.  It should take less than 750ms.", elapsedTime),
            elapsedTime < 750);
   }

   public void testGetArtifactsByArtTypes() throws OseeCoreException {
      Branch common = BranchManager.getCommonBranch();
      List<String> artTypes = new ArrayList<String>();
      artTypes.add("Actionable Item");
      artTypes.add("General Document");
      artTypes.add("Folder");
      artTypes.add("Work Widget Definition");
      ArtifactQueryBuilder builder = new ArtifactQueryBuilder(ArtifactTypeManager.getTypes(artTypes), common, FULL);
      long startTime = System.currentTimeMillis();
      List<Artifact> result = builder.getArtifacts(5000, null);
      long elapsedTime = System.currentTimeMillis() - startTime;
      System.out.println(String.format("testGetArtifactsByArtTypes took %dms for %d artifacts", elapsedTime,
            result.size()));
      assertTrue("No artifacts found", result.size() > 0);
      assertTrue(String.format(
            "Elapsed time for testGetArtifactsByArtTypes took %dms.  It should take less than 1600ms.", elapsedTime),
            elapsedTime < 1600);
   }

   public void testGetArtifactsByArtTypesAllowDeleted() throws OseeCoreException {
      Branch common = BranchManager.getCommonBranch();
      List<String> artTypes = new ArrayList<String>();
      artTypes.add("User Group");
      artTypes.add("User");
      artTypes.add("Work Page Definition");
      artTypes.add("Work Rule Definition");
      ArtifactQueryBuilder builder = new ArtifactQueryBuilder(ArtifactTypeManager.getTypes(artTypes), common, FULL);
      long startTime = System.currentTimeMillis();
      List<Artifact> result = builder.getArtifacts(5000, null);
      long elapsedTime = System.currentTimeMillis() - startTime;
      System.out.println(String.format("testGetArtifactsByArtTypesAllowDeleted took %dms for %d artifacts",
            elapsedTime, result.size()));
      assertTrue("No artifacts found", result.size() > 0);
      assertTrue(String.format(
            "Elapsed time for testGetArtifactsByArtTypesAllowDeleted took %dms.  It should take less than 4300ms.",
            elapsedTime), elapsedTime < 4300);
   }

   public void testLoadAllBranch() throws OseeCoreException {
      Branch common = BranchManager.getCommonBranch();
      ArtifactQueryBuilder builder = new ArtifactQueryBuilder(common, FULL, false);
      long startTime = System.currentTimeMillis();
      List<Artifact> result = builder.getArtifacts(50000, null);
      long elapsedTime = System.currentTimeMillis() - startTime;
      System.out.println(String.format("loadAllBranch took %dms for %d artifacts", elapsedTime, result.size()));
      assertTrue("No artifacts found", result.size() > 0);
      assertTrue(String.format("Elapsed time for loadAllBranch took %dms.  It should take less than 500000ms.",
            elapsedTime), elapsedTime < 500000);
   }
}
