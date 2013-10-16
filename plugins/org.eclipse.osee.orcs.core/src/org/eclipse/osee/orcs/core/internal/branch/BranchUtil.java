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

public class BranchUtil {

   private BranchUtil() {
      //Utility class
   }

   public static List<Branch> orderByParent(Collection<Branch> branches) throws OseeCoreException {
      List<Branch> list = new ArrayList<Branch>(branches);
      for (int i = 0; i < list.size(); i++) {
         Branch cur = list.get(i);
         Branch parent = cur.getParentBranch();

         //this is the last element in the list
         if (parent == null || !list.contains(parent)) {
            Branch last = list.get(list.size() - 1);
            list.set(i, last);
            list.set(list.size() - 1, cur);
         } else {
            int parentIdx = list.indexOf(parent);
            //need to swap
            if (parentIdx < i) {
               list.set(i, parent);
               list.set(parentIdx, cur);
               //reset i
               i--;
            }
         }
      }
      return list;
   }
}
