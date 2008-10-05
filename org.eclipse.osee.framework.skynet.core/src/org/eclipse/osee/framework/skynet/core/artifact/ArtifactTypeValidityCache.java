/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/

package org.eclipse.osee.framework.skynet.core.artifact;

import java.util.Collection;
import java.util.TreeSet;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.HashCollection;

/**
 * @author Ryan D. Brooks
 */
public class ArtifactTypeValidityCache {
   private final HashCollection<Branch, ArtifactType> branchToartifactTypeMap;

   public ArtifactTypeValidityCache() {
      branchToartifactTypeMap = new HashCollection<Branch, ArtifactType>(false, TreeSet.class);
   }

   private synchronized void ensurePopulated() throws OseeCoreException {
      if (branchToartifactTypeMap.size() == 0) {
         populateCache();
      }
   }

   private void populateCache() throws OseeCoreException {
      Collection<ArtifactType> artifactTypes = ArtifactTypeManager.getAllTypes();
      for (Branch branch : BranchPersistenceManager.getRootBranches()) {
         branchToartifactTypeMap.put(branch, artifactTypes);
      }
   }

   public Collection<ArtifactType> getValidArtifactTypes(Branch branch) throws OseeCoreException {
      if (false) {
         // TODO: Filter Types By Branch
         ensurePopulated();
         Branch rootBranch = branch.getRootBranch();
         Collection<ArtifactType> artifactTypes = branchToartifactTypeMap.getValues(rootBranch);
         if (artifactTypes == null) {
            throw new IllegalArgumentException(
                  "There are no valid artifact types available for the branch " + rootBranch);
         }
         return artifactTypes;
      } else {
         return ArtifactTypeManager.getAllTypes();
      }
   }
}