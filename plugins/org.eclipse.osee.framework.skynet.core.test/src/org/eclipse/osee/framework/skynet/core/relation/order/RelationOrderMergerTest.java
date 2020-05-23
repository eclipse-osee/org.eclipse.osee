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
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import org.eclipse.core.runtime.Assert;
import org.junit.Test;

/**
 * @author Ryan Schmitt
 */
public class RelationOrderMergerTest {
   @Test
   public void testCursorAlgorithmSuccess() {
      RelationOrderMerger<String> merger = new RelationOrderMerger<>();
      Collection<String> mergedSet = new HashSet<>();
      Collections.addAll(mergedSet, new String[] {"Red", "Yellow", "Blue", "Purple", "Green", "Brown", "Black"});

      List<String> leftOrder = new ArrayList<>();
      leftOrder.add("Orange");
      leftOrder.add("Red");
      leftOrder.add("Yellow");
      leftOrder.add("Blue");
      leftOrder.add("Black");
      leftOrder.add("Green");

      List<String> rightOrder = new ArrayList<>();
      rightOrder.add("Red");
      rightOrder.add("Yellow");
      rightOrder.add("Purple");
      rightOrder.add("Blue");
      rightOrder.add("Brown");
      rightOrder.add("Green");

      List<String> mergedOrder = merger.computeMergedOrder(leftOrder, rightOrder, mergedSet);
      Assert.isTrue(mergedOrder.size() == mergedSet.size());
      Assert.isTrue(mergedOrder.get(0).equals("Red"));
      Assert.isTrue(mergedOrder.get(1).equals("Yellow"));
      Assert.isTrue(mergedOrder.get(2).equals("Purple"));
      Assert.isTrue(mergedOrder.get(3).equals("Blue"));
      Assert.isTrue(mergedOrder.get(4).equals("Black"));
      Assert.isTrue(mergedOrder.get(5).equals("Brown"));
      Assert.isTrue(mergedOrder.get(6).equals("Green"));
   }

   @Test
   public void testCursorAlgorithmFailure() {
      RelationOrderMerger<String> merger = new RelationOrderMerger<>();
      Collection<String> mergedSet = new HashSet<>();
      Collections.addAll(mergedSet, new String[] {"Red", "Yellow", "Green"});

      List<String> leftOrder = new ArrayList<>();
      leftOrder.add("Orange");
      leftOrder.add("Yellow");
      leftOrder.add("Red");
      leftOrder.add("Green");

      List<String> rightOrder = new ArrayList<>();
      rightOrder.add("Red");
      rightOrder.add("Yellow");
      rightOrder.add("Purple");
      rightOrder.add("Green");

      List<String> mergedOrder = merger.computeMergedOrder(leftOrder, rightOrder, mergedSet);
      Assert.isTrue(mergedOrder == null);
   }
}
