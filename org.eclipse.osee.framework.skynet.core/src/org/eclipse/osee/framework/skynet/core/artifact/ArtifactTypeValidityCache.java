/*
 * Created on Feb 21, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.skynet.core.artifact;

import java.sql.SQLException;
import java.util.Collection;
import java.util.TreeSet;
import org.eclipse.osee.framework.jdk.core.type.HashCollection;
import org.eclipse.osee.framework.skynet.core.attribute.ArtifactSubtypeDescriptor;
import org.eclipse.osee.framework.skynet.core.attribute.ConfigurationPersistenceManager;

/**
 * @author Ryan D. Brooks
 */
public class ArtifactTypeValidityCache {
   private final HashCollection<Branch, ArtifactSubtypeDescriptor> branchToartifactTypeMap;

   public ArtifactTypeValidityCache() {
      branchToartifactTypeMap = new HashCollection<Branch, ArtifactSubtypeDescriptor>(false, TreeSet.class);
   }

   private synchronized void ensurePopulated() throws SQLException {
      if (branchToartifactTypeMap.size() == 0) {
         populateCache();
      }
   }

   private void populateCache() throws SQLException {
      Collection<ArtifactSubtypeDescriptor> artifactTypes =
            ConfigurationPersistenceManager.getInstance().getArtifactSubtypeDescriptors();

      for (Branch branch : BranchPersistenceManager.getInstance().getRootBranches()) {
         branchToartifactTypeMap.put(branch, artifactTypes);
      }
   }

   public Collection<ArtifactSubtypeDescriptor> getValidArtifactTypes(Branch branch) throws SQLException {
      ensurePopulated();
      Branch rootBranch = branch.getRootBranch();
      Collection<ArtifactSubtypeDescriptor> artifactTypes = branchToartifactTypeMap.getValues(rootBranch);
      if (artifactTypes == null) {
         throw new IllegalArgumentException("There are no valid artifact types available for the branch " + rootBranch);
      }
      return artifactTypes;
   }
}