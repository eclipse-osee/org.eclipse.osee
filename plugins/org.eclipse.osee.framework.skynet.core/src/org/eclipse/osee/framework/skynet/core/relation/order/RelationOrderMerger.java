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
package org.eclipse.osee.framework.skynet.core.relation.order;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import org.eclipse.core.runtime.Assert;

public class RelationOrderMerger<T> {
   private List<T> starredList;

   public RelationOrderMerger() {
      starredList = new ArrayList<T>();
   }

   public void computeMergedOrder(List<T> leftOrder, List<T> rightOrder, Collection<T> mergedSet) {
      // 1) Cross out anything not found in mergedSet
      makeSubset(leftOrder, mergedSet);
      makeSubset(rightOrder, mergedSet);

      // 2) Star anything not in both lists
      starUnionComplement(leftOrder, rightOrder);

      // 3) Perform cursor algorithm to either
      //    a) generate a merged order, or
      //    b) fail
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

   private void starUnionComplement(List<T> setA, List<T> setB) {
      for (T element : setA) {
         if (!setB.contains(element)) {
            addStar(element);
         }
      }

      for (T element : setB) {
         if (!setA.contains(element)) {
            addStar(element);
         }
      }
   }

   private void addStar(T element) {
      starredList.add(element);
   }

   private boolean hasStar(T element) {
      return starredList.contains(element);
   }

   public static void main(String[] args) {
      test();
   }

   private static void test() {
      RelationOrderMerger<String> merger = new RelationOrderMerger<String>();
      Collection<String> mergedSet = new HashSet<String>();
      mergedSet.add("Sig");
      mergedSet.add("HK");
      mergedSet.add("Wilson");
      mergedSet.add("SA");
      mergedSet.add("Browning");

      List<String> subset = new ArrayList<String>();
      subset.add("Glock");
      subset.add("Sig");
      subset.add("HK");
      subset.add("Wilson");
      subset.add("Browning");

      merger.makeSubset(subset, mergedSet);
      Assert.isTrue(subset.size() == 4);
      Assert.isTrue(!subset.contains("Glock"));

      List<String> subset2 = new ArrayList<String>(subset);
      subset2.add("SA");

      merger.starUnionComplement(subset, subset2);
      Assert.isTrue(!merger.hasStar("Wilson"));
      Assert.isTrue(merger.hasStar("SA"));

      merger.starredList = new ArrayList<String>();
      merger.starUnionComplement(subset2, subset);
   }
}
