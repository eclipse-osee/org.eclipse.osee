/*******************************************************************************
 * Copyright (c) 2009 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.core.cache;

import java.util.Collection;
import java.util.HashSet;

import org.eclipse.osee.framework.core.enums.BranchType;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.core.enums.OseeCacheEnum;
import org.eclipse.osee.framework.core.exception.BranchDoesNotExist;
import org.eclipse.osee.framework.core.exception.MultipleBranchesExist;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.util.Conditions;
import org.eclipse.osee.framework.jdk.core.type.CompositeKeyHashMap;

/**
 * @author Roberto E. Escobar
 */
public class BranchCache extends AbstractOseeCache<Branch> {
   private final CompositeKeyHashMap<Branch, Branch, Branch> sourceDestMerge =
         new CompositeKeyHashMap<Branch, Branch, Branch>();

   private Branch systemRootBranch;
   private Branch commonBranch;

   public BranchCache(IOseeDataAccessor<Branch> dataAccessor) {
      super(OseeCacheEnum.BRANCH_CACHE, dataAccessor, false);
      this.systemRootBranch = null;
      this.commonBranch = null;
   }

   @Override
   public void cache(Branch type) throws OseeCoreException {
      if (BranchType.SYSTEM_ROOT == type.getBranchType()) {
         systemRootBranch = type;
      }
      super.cache(type);
   }

   public Branch getSystemRootBranch() throws OseeCoreException {
      ensurePopulated();
      return systemRootBranch;
   }

   public CompositeKeyHashMap<Branch, Branch, Branch> getMergeBranches() throws OseeCoreException {
      return sourceDestMerge;
   }

   public Branch getMergeBranch(Branch sourceBranch, Branch destinationBranch) throws OseeCoreException {
      ensurePopulated();
      return sourceDestMerge.get(sourceBranch, destinationBranch);
   }

   public void cacheMergeBranch(Branch mergeBranch, Branch sourceBranch, Branch destinationBranch) throws OseeCoreException {
      Conditions.checkNotNull(mergeBranch, "merge branch");
      Conditions.checkNotNull(sourceBranch, "source branch");
      Conditions.checkNotNull(destinationBranch, "destination branch");

      ensurePopulated();
      sourceDestMerge.put(sourceBranch, destinationBranch, mergeBranch);
   }

   public Collection<Branch> getByAlias(String alias) throws OseeCoreException {
      Conditions.checkNotNullOrEmpty(alias, "alias");

      ensurePopulated();
      Collection<Branch> branches = new HashSet<Branch>();
      String aliasToMatch = alias.toLowerCase();
      for (Branch branch : getAll()) {
         Collection<String> aliases = branch.getAliases();
         if (aliases != null && !aliases.isEmpty()) {
            if (aliases.contains(aliasToMatch)) {
               branches.add(branch);
            }
         }
      }
      return branches;
   }

   public Branch getUniqueByAlias(String alias) throws OseeCoreException {
      ensurePopulated();
      Collection<Branch> branches = getByAlias(alias);
      if (branches.isEmpty()) {
         throw new BranchDoesNotExist(String.format("The alias [%s] does not refer to any branch", alias));
      }
      if (branches.size() > 1) {
         throw new MultipleBranchesExist(String.format("The alias [%s] refers to more than 1 branch [%s]", alias,
               branches));
      }
      return branches.iterator().next();
   }

   //   public void setAssociatedArtifact(Branch branch, IBasicArtifact<?> artifact) throws OseeCoreException {
   //      ensurePopulated();
   //      if (artifact != null) {
   //         // Artifact has already been loaded so check
   //         // TODO: this method should allow the artifact to be on any branch, not just common
   //         if (artifact instanceof IBasicArtifact<?>) {
   //            if (artifact.getBranch() != getCommonBranch()) {
   //               throw new OseeArgumentException(
   //                     "Setting associated artifact for branch only valid for common branch artifact.");
   //            }
   //         }
   //         IBasicArtifact<?> lastArtifact = branchToAssociatedArtifact.get(branch);
   //         if (lastArtifact != null) {
   //            if (!lastArtifact.equals(artifact)) {
   //               branchToAssociatedArtifact.put(branch, artifact);
   //            }
   //         } else {
   //            branchToAssociatedArtifact.put(branch, artifact);
   //         }
   //      } else {
   //         branchToAssociatedArtifact.remove(branch);
   //      }
   //   }
   //
   //   public IBasicArtifact<?> getAssociatedArtifact(Branch branch) throws OseeCoreException {
   //      ensurePopulated();
   //      IBasicArtifact<?> associatedArtifact = branchToAssociatedArtifact.get(branch);
   //      if (associatedArtifact == null) {
   //         associatedArtifact = getDefaultAssociatedArtifact();
   //      }
   //      return associatedArtifact;
   //   }
   //

   public Branch getCommonBranch() throws OseeCoreException {
      ensurePopulated();
      if (commonBranch == null) {
         commonBranch = getUniqueByAlias(CoreBranches.COMMON.getName());
      }
      return commonBranch;
   }
}
