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
package org.eclipse.osee.framework.skynet.core.types.branch;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.CompositeKeyHashMap;
import org.eclipse.osee.framework.jdk.core.type.HashCollection;
import org.eclipse.osee.framework.skynet.core.types.AbstractOseeCache;
import org.eclipse.osee.framework.skynet.core.types.IOseeTypeDataAccessor;
import org.eclipse.osee.framework.skynet.core.types.IOseeTypeFactory;
import org.eclipse.osee.framework.skynet.core.types.OseeTypeCache;

/**
 * @author Roberto E. Escobar
 */
public class BranchCache extends AbstractOseeCache<Branch> {

   private final HashCollection<Branch, Branch> parentToChildrenBranches = new HashCollection<Branch, Branch>();
   private final Map<Branch, Branch> childToParent = new HashMap<Branch, Branch>();
   private final Map<String, Branch> aliasToBranch = new HashMap<String, Branch>();
   private final CompositeKeyHashMap<Branch, Branch, Branch> sourceDestMerge =
         new CompositeKeyHashMap<Branch, Branch, Branch>();

   private Branch systemRootBranch;

   public BranchCache(OseeTypeCache cache, IOseeTypeFactory factory, IOseeTypeDataAccessor<Branch> dataAccessor) {
      super(factory, dataAccessor);
      this.systemRootBranch = null;
   }

   @Override
   public void cacheType(Branch type) throws OseeCoreException {
      if (type.getBranchType().isSystemRootBranch()) {
         systemRootBranch = type;
      }
      super.cacheType(type);
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

   public void setBranchParent(Branch childBranch, Branch parentBranch) {
      childToParent.put(childBranch, parentBranch);
      parentToChildrenBranches.put(parentBranch, childBranch);
   }

   public void addMergeBranch(Branch mergeBranch, Branch sourceBranch, Branch destinationBranch) {
      sourceDestMerge.put(sourceBranch, destinationBranch, mergeBranch);
   }

   public void addBranchAlias(Branch branch, String alias) {
      aliasToBranch.put(alias, branch);
   }

}
