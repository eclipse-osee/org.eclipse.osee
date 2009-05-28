/*
 * Created on May 28, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.skynet.core.test2.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.eclipse.osee.framework.core.enums.BranchType;
import org.eclipse.osee.framework.db.connection.exception.BranchDoesNotExist;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.artifact.BranchArchivedState;
import org.eclipse.osee.framework.skynet.core.artifact.BranchControlled;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.relation.CoreRelationEnumeration;
import org.eclipse.osee.support.test.util.DemoSubsystems;
import org.eclipse.osee.support.test.util.TestUtil;

/**
 * @author Donald G. Dunne
 */
public class FrameworkTestUtil {

   /**
    * Creates a simple artifact and adds it to the root artifact default hierarchical relation
    * 
    * @param artifactTypeName
    * @param name
    * @param branch
    * @throws Exception
    */
   public static Artifact createSimpleArtifact(String artifactTypeName, String name, Branch branch) throws Exception {
      Artifact softArt = ArtifactTypeManager.addArtifact(artifactTypeName, branch);
      softArt.setDescriptiveName(name);
      softArt.addAttribute("Subsystem", DemoSubsystems.Electrical.name());
      Artifact rootArtifact = ArtifactQuery.getDefaultHierarchyRootArtifact(branch, true);
      rootArtifact.addRelation(CoreRelationEnumeration.DEFAULT_HIERARCHICAL__CHILD, softArt);
      return softArt;
   }

   public static Collection<Artifact> createSimpleArtifacts(String artifactTypeName, int numArts, String name, Branch branch) throws Exception {
      List<Artifact> arts = new ArrayList<Artifact>();
      for (int x = 1; x < numArts + 1; x++) {
         arts.add(createSimpleArtifact(artifactTypeName, name + " " + x, branch));
      }
      return arts;
   }

   public static void purgeBranch(Branch branch) throws Exception {
      try {
         BranchManager.purgeBranchInJob(branch);
         TestUtil.sleep(2000);
      } catch (BranchDoesNotExist ex) {
         // do nothing
      }
   }

   public static void purgeWorkingBranches(Collection<String> branchNamesContain) throws Exception {
      try {
         // delete working branches
         for (Branch workingBranch : BranchManager.getBranches(BranchArchivedState.ALL, BranchControlled.ALL,
               BranchType.WORKING)) {
            for (String branchName : branchNamesContain) {
               if (workingBranch.getBranchName().contains(branchName)) {
                  BranchManager.purgeBranchInJob(workingBranch);
                  TestUtil.sleep(2000);
               }
            }
         }
      } catch (BranchDoesNotExist ex) {
         // do nothing
      }
   }

}
