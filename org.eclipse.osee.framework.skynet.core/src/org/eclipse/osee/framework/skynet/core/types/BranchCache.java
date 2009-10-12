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
package org.eclipse.osee.framework.skynet.core.types;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import org.eclipse.osee.framework.core.enums.TransactionDetailsType;
import org.eclipse.osee.framework.core.exception.BranchDoesNotExist;
import org.eclipse.osee.framework.core.exception.MultipleBranchesExist;
import org.eclipse.osee.framework.core.exception.OseeArgumentException;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.CompositeKeyHashMap;
import org.eclipse.osee.framework.jdk.core.type.HashCollection;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.artifact.CoreBranches;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionId;

/**
 * @author Roberto E. Escobar
 */
public class BranchCache extends AbstractOseeCache<Branch> {
   private final HashCollection<Branch, Branch> parentToChildrenBranches = new HashCollection<Branch, Branch>();
   private final Map<Branch, Branch> childToParent = new HashMap<Branch, Branch>();

   private final HashCollection<Branch, String> branchToAlias = new HashCollection<Branch, String>();

   private final CompositeKeyHashMap<Branch, Branch, Branch> sourceDestMerge =
         new CompositeKeyHashMap<Branch, Branch, Branch>();
   private final Map<Branch, IArtifact> branchToAssociatedArtifact = new HashMap<Branch, IArtifact>();

   private final Map<Branch, TransactionId> branchToBaseTx = new HashMap<Branch, TransactionId>();
   private final Map<Branch, TransactionId> branchToSourceTx = new HashMap<Branch, TransactionId>();

   private Branch systemRootBranch;
   private IArtifact defaultAssociatedArtifact;

   public BranchCache(IOseeTypeFactory factory, IOseeDataAccessor<Branch> dataAccessor) {
      super(factory, dataAccessor);
      this.defaultAssociatedArtifact = null;
      this.systemRootBranch = null;
   }

   @Override
   public Branch getByGuid(String typeGuid) throws OseeCoreException {
      ensurePopulated();
      Branch branch = null;
      if (CoreBranches.COMMON.getGuid().equals(typeGuid)) {
         branch = getCommonBranch();
      } else {
         branch = super.getByGuid(typeGuid);
      }
      return branch;
   }

   @Override
   public void cache(Branch type) throws OseeCoreException {
      if (type.getBranchType().isSystemRootBranch()) {
         systemRootBranch = type;
      }
      super.cache(type);
   }

   @Override
   public void decache(Branch branch) throws OseeCoreException {
      super.decache(branch);
      // TODO add decache from all maps
   }

   public Branch getSystemRootBranch() throws OseeCoreException {
      ensurePopulated();
      return systemRootBranch;
   }

   public Branch getParentBranch(Branch childBranch) throws OseeCoreException {
      ensurePopulated();
      return childToParent.get(childBranch);
   }

   public Collection<Branch> getChildren(Branch parent) throws OseeCoreException {
      ensurePopulated();
      Collection<Branch> childBranches = new HashSet<Branch>();
      Collection<Branch> children = parentToChildrenBranches.getValues(parent);
      if (children != null) {
         childBranches.addAll(children);
      }
      return childBranches;
   }

   public Branch getMergeBranch(Branch sourceBranch, Branch destinationBranch) throws OseeCoreException {
      ensurePopulated();
      return sourceDestMerge.get(sourceBranch, destinationBranch);
   }

   public void setBranchParent(Branch parentBranch, Branch childBranch) throws OseeCoreException {
      if (parentBranch == null) {
         throw new OseeArgumentException("Parent Branch cannot be null");
      }
      if (childBranch == null) {
         throw new OseeArgumentException("Child Branch cannot be null");
      }
      ensurePopulated();
      parentToChildrenBranches.put(parentBranch, childBranch);
      childToParent.put(childBranch, parentBranch);
   }

   public void cacheMergeBranch(Branch mergeBranch, Branch sourceBranch, Branch destinationBranch) throws OseeCoreException {
      if (mergeBranch == null) {
         throw new OseeArgumentException("Merge Branch cannot be null");
      }
      if (sourceBranch == null) {
         throw new OseeArgumentException("Source Branch cannot be null");
      }
      if (destinationBranch == null) {
         throw new OseeArgumentException("Destination Branch cannot be null");
      }
      ensurePopulated();
      sourceDestMerge.put(sourceBranch, destinationBranch, mergeBranch);
   }

   public Collection<String> getAliases(Branch branch) throws OseeCoreException {
      ensurePopulated();
      Collection<String> aliases = new HashSet<String>();
      Collection<String> storedAliases = branchToAlias.getValues(branch);
      if (storedAliases != null) {
         aliases.addAll(storedAliases);
      }
      return aliases;
   }

   public void setAliases(Branch branch, Collection<String> aliases) throws OseeCoreException {
      if (branch == null) {
         throw new OseeArgumentException("branch cannot be null");
      }
      ensurePopulated();
      branchToAlias.removeValues(branch);
      for (String alias : aliases) {
         branchToAlias.put(branch, alias.toLowerCase());
      }
   }

   public Collection<Branch> getByAlias(String alias) throws OseeCoreException {
      if (!Strings.isValid(alias)) {
         throw new OseeArgumentException("Alias cannot be null or empty");
      }
      ensurePopulated();
      Collection<Branch> branches = new HashSet<Branch>();
      String aliasToMatch = alias.toLowerCase();
      for (Branch key : branchToAlias.keySet()) {
         Collection<String> aliases = branchToAlias.getValues(key);
         if (aliases != null) {
            if (aliases.contains(aliasToMatch)) {
               branches.add(key);
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

   public void cacheBaseTransaction(Branch branch, TransactionId baseTransaction) throws OseeCoreException {
      if (branch == null) {
         throw new OseeArgumentException("branch cannot be null");
      }
      if (baseTransaction == null) {
         throw new OseeArgumentException("base transaction cannot be null");
      }
      if (baseTransaction.getTxType() != TransactionDetailsType.Baselined) {
         throw new OseeArgumentException("Transaction should be a baseline type transaction");
      }
      ensurePopulated();
      branchToBaseTx.put(branch, baseTransaction);
   }

   public void cacheSourceTransaction(Branch branch, TransactionId sourceTransaction) throws OseeCoreException {
      if (branch == null) {
         throw new OseeArgumentException("branch cannot be null");
      }
      if (sourceTransaction == null) {
         throw new OseeArgumentException("source transaction cannot be null");
      }
      ensurePopulated();
      branchToSourceTx.put(branch, sourceTransaction);
   }

   public TransactionId getBaseTransaction(Branch branch) throws OseeCoreException {
      ensurePopulated();
      return branchToBaseTx.get(branch);
   }

   public TransactionId getSourceTransaction(Branch branch) throws OseeCoreException {
      ensurePopulated();
      return branchToSourceTx.get(branch);
   }

   public void setAssociatedArtifact(Branch branch, IArtifact artifact) throws OseeCoreException {
      ensurePopulated();
      if (artifact != null) {
         // Artifact has already been loaded so check
         // TODO: this method should allow the artifact to be on any branch, not just common
         if (artifact instanceof Artifact) {
            if (artifact.getBranch() != getCommonBranch()) {
               throw new OseeArgumentException(
                     "Setting associated artifact for branch only valid for common branch artifact.");
            }
         }
         IArtifact lastArtifact = branchToAssociatedArtifact.get(branch);
         if (lastArtifact != null) {
            if (!lastArtifact.equals(artifact)) {
               branchToAssociatedArtifact.put(branch, artifact);
            }
         } else {
            branchToAssociatedArtifact.put(branch, artifact);
         }
      } else {
         branchToAssociatedArtifact.remove(branch);
      }
   }

   public IArtifact getAssociatedArtifact(Branch branch) throws OseeCoreException {
      ensurePopulated();
      IArtifact associatedArtifact = branchToAssociatedArtifact.get(branch);
      if (associatedArtifact == null) {
         associatedArtifact = getDefaultAssociatedArtifact();
      }
      return associatedArtifact;
   }

   public IArtifact getDefaultAssociatedArtifact() throws OseeCoreException {
      ensurePopulated();
      return defaultAssociatedArtifact;
   }

   public void setDefaultAssociatedArtifact(IArtifact artifact) {
      this.defaultAssociatedArtifact = artifact;
   }

   public Branch getCommonBranch() throws OseeCoreException {
      ensurePopulated();
      return getUniqueByAlias(CoreBranches.COMMON.getName());
   }
}
