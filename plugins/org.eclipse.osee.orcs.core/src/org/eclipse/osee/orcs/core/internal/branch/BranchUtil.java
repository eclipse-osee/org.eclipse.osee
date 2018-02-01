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

import com.google.common.collect.Lists;
import java.util.List;
import org.eclipse.osee.framework.core.data.Branch;
import org.eclipse.osee.framework.core.data.BranchId;

public final class BranchUtil {

   private BranchUtil() {
      //Utility class
   }

   public static List<Branch> orderByParentReadable(Iterable<? extends Branch> branches) {
      List<Branch> sorted = Lists.newArrayList(branches);

      for (int i = 0; i < sorted.size(); i++) {
         Branch current = sorted.get(i);
         BranchId parent = current.getParentBranch();

         int parentIdx = sorted.indexOf(parent);
         if (parentIdx >= 0 && parentIdx < i) {
            sorted.set(i, sorted.get(parentIdx));
            sorted.set(parentIdx, current);
            i = -1; // start over
         }
      }
      return sorted;
   }
}