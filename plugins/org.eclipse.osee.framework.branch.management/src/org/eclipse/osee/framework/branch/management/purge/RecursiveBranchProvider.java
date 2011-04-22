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
package org.eclipse.osee.framework.branch.management.purge;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.model.cache.BranchFilter;
import org.eclipse.osee.framework.core.util.Conditions;

/**
 * @author John Misinco
 */
public final class RecursiveBranchProvider implements IBranchesProvider {
   private final Branch parentBranch;
   private final BranchFilter filter;

   public RecursiveBranchProvider(Branch parentBranch, BranchFilter filter) {
      this.parentBranch = parentBranch;
      this.filter = filter;
   }

   @Override
   public Collection<Branch> getBranches() throws OseeCoreException {
      Conditions.checkNotNull(parentBranch, "seed");
      Set<Branch> children = new HashSet<Branch>();

      parentBranch.getChildBranches(children, true, filter);
      if (filter.matches(parentBranch)) {
         children.add(parentBranch);
      }
      return children;
   }
}