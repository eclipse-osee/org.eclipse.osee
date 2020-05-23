/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.framework.skynet.core.relation.order;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class RelationOrderMerger<T> {
   private final List<T> starredList;

   public RelationOrderMerger() {
      starredList = new ArrayList<>();
   }

   public List<T> computeMergedOrder(List<T> leftOrder, List<T> rightOrder, Collection<T> mergedSet) {
      makeSubset(leftOrder, mergedSet);
      makeSubset(rightOrder, mergedSet);

      starUnionComplement(leftOrder, rightOrder);

      return cursorAlgorithm(leftOrder, rightOrder, mergedSet);
   }

   private void makeSubset(List<T> subset, Collection<T> superset) {
      for (int i = 0; i < subset.size(); i++) {
         T current = subset.get(i);
         if (!superset.contains(current)) {
            subset.remove(i);
            i--;
         }
      }
   }

   private void starUnionComplement(Collection<T> left, Collection<T> right) {
      for (T element : left) {
         if (!right.contains(element)) {
            addStar(element);
         }
      }

      for (T element : right) {
         if (!left.contains(element)) {
            addStar(element);
         }
      }
   }

   private List<T> cursorAlgorithm(List<T> left, List<T> right, Collection<T> mergedSet) {
      List<T> mergedOrder = new ArrayList<>();
      int leftIndex = 0;
      int rightIndex = 0;
      while (leftIndex < left.size() && rightIndex < right.size()) {
         T leftElement = left.get(leftIndex);
         T rightElement = right.get(rightIndex);
         boolean resolved = false;
         if (leftElement.equals(rightElement)) {
            mergedOrder.add(leftElement);
            leftIndex++;
            rightIndex++;
            resolved = true;
         }
         if (hasStar(leftElement)) {
            mergedOrder.add(leftElement);
            leftIndex++;
            resolved = true;
         }
         if (hasStar(rightElement)) {
            mergedOrder.add(rightElement);
            rightIndex++;
            resolved = true;
         }
         if (!resolved) {
            return null;
         }
      }

      while (leftIndex < left.size()) {
         T leftElement = left.get(leftIndex);
         mergedOrder.add(leftElement);
         leftIndex++;
      }

      while (rightIndex < right.size()) {
         T rightElement = right.get(rightIndex);
         mergedOrder.add(rightElement);
         rightIndex++;
      }

      return mergedOrder;
   }

   private void addStar(T element) {
      starredList.add(element);
   }

   private boolean hasStar(T element) {
      return starredList.contains(element);
   }

}
