/*******************************************************************************
 * Copyright (c) 2012 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs.core.internal.branch;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.orcs.data.BranchReadable;
import org.eclipse.osee.orcs.search.QueryFactory;

public class BranchUtil {

   private BranchUtil() {
      //Utility class
   }

   public static List<Branch> orderByParent(Collection<Branch> branches) throws OseeCoreException {

      ArrayList<Branch> sorted = new ArrayList<Branch>(branches);
      for (int i = 0; i < sorted.size(); i++) {
         Branch current = sorted.get(i);
         Branch parent = current.getParentBranch();
         int parentIdx = sorted.indexOf(parent);
         if (parentIdx >= 0 && parentIdx < i) {
            sorted.set(i, parent);
            sorted.set(parentIdx, current);
            i = -1; // start over
         }
      }

      return sorted;
   }

   public static List<BranchReadable> orderByParentReadable(final QueryFactory queryFactory, Collection<BranchReadable> branches) throws OseeCoreException {
      List<BranchReadable> sorted = new ArrayList<BranchReadable>(branches);

      for (int i = 0; i < sorted.size(); i++) {
         BranchReadable current = sorted.get(i);
         BranchReadable parent =
            queryFactory.branchQuery().andUuids(current.getParentBranch()).getResults().getExactlyOne();

         int parentIdx = sorted.indexOf(parent);
         if (parentIdx >= 0 && parentIdx < i) {
            sorted.set(i, parent);
            sorted.set(parentIdx, current);
            i = -1; // start over
         }
      }
      return sorted;
   }
}
