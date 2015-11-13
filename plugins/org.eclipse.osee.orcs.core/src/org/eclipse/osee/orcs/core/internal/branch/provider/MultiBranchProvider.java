/*******************************************************************************
 * Copyright (c) 2011 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs.core.internal.branch.provider;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.model.BranchReadable;
import org.eclipse.osee.framework.core.model.cache.BranchFilter;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Conditions;

/**
 * @author John R. Misinco
 */
public class MultiBranchProvider implements BranchProvider {

   private final boolean recursive;
   private final Set<BranchReadable> branches;
   private final BranchFilter filter;

   public MultiBranchProvider(boolean recursive, Set<BranchReadable> branches, BranchFilter filter) {
      this.recursive = recursive;
      this.branches = branches;
      this.filter = filter;
   }

   private Collection<BranchReadable> getChildBranches(BranchReadable branch) throws OseeCoreException {
      Set<BranchReadable> children = new HashSet<>();

      branch.getChildBranches(children, true, filter);
      if (filter.matches(branch)) {
         children.add(branch);
      }
      return children;
   }

   @Override
   public Collection<BranchReadable> getBranches() throws OseeCoreException {
      Conditions.checkNotNull(branches, "seeds");
      Set<BranchReadable> result = branches;
      if (recursive) {
         result = new HashSet<>(branches);
         for (BranchReadable b : branches) {
            result.addAll(getChildBranches(b));
         }
      }
      return result;
   }

}
