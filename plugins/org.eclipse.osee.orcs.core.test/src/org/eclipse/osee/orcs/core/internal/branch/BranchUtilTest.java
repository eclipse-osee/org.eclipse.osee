/*******************************************************************************
 * Copyright (c) 2014 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs.core.internal.branch;

import java.util.Arrays;
import java.util.List;
import org.eclipse.osee.framework.core.data.Branch;
import org.eclipse.osee.framework.core.data.BranchId;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @author David W. Miller
 */
public class BranchUtilTest {

   private BranchMock one, two, three, four, five, six, seven, eight, nine, ten;

   @Before
   public void init() {
      one = new BranchMock(1);
      two = new BranchMock(2);
      three = new BranchMock(3);
      four = new BranchMock(4);
      five = new BranchMock(5);
      six = new BranchMock(6);
      seven = new BranchMock(7);
      eight = new BranchMock(8);
      nine = new BranchMock(9);
      ten = new BranchMock(10);
   }

   private static final class BranchMock extends Branch {
      private BranchId parent;

      public BranchMock(long id) {
         super(id, String.valueOf(id), null, null, null, null, false, null, null, false, null);
      }

      @Override
      public BranchId getParentBranch() {
         return parent;
      }

      public void setParentBranch(BranchId parent) {
         this.parent = parent;
      }
   }

   @Test
   public void testConnectedBranchSorting() {
      initBranchParentageList();
      List<Branch> ordered = BranchUtil.orderByParentReadable(getOutOfOrderList());
      Assert.assertEquals("[10, 9, 8, 7, 6, 5, 4, 3, 2, 1]", ordered.toString());
   }

   @Test
   public void testDisjointBranchSorting() {
      initDisjointParentageList();
      List<Branch> ordered = BranchUtil.orderByParentReadable(getDisjointOrderList());
      Assert.assertEquals("[4, 3, 2, 7, 6, 5, 10, 9, 8, 1]", ordered.toString());
   }

   @Test
   public void testDisjointOutOfOrderBranchSorting() {
      initDisjointParentageList();
      List<Branch> ordered = BranchUtil.orderByParentReadable(getOutOfOrderList());
      Assert.assertEquals("[4, 10, 7, 3, 6, 9, 8, 2, 5, 1]", ordered.toString());
   }

   @Test
   public void testOutsideParentsBranchSorting() {
      initOutsideParentList();
      List<Branch> ordered = BranchUtil.orderByParentReadable(getOutOfOrderList());
      Assert.assertEquals("[4, 10, 3, 2, 7, 6, 9, 8, 1, 5]", ordered.toString());
   }

   private List<Branch> getOutOfOrderList() {
      return Arrays.asList(three, eight, one, four, five, six, ten, nine, two, seven);
   }

   private List<Branch> getDisjointOrderList() {
      return Arrays.asList(two, three, four, five, six, seven, eight, nine, ten, one);
   }

   private void initBranchParentageList() {
      one.setParentBranch(BranchId.SENTINEL);
      two.setParentBranch(one);
      three.setParentBranch(two);
      four.setParentBranch(three);
      five.setParentBranch(four);
      six.setParentBranch(five);
      seven.setParentBranch(six);
      eight.setParentBranch(seven);
      nine.setParentBranch(eight);
      ten.setParentBranch(nine);
   }

   private void initDisjointParentageList() {
      one.setParentBranch(BranchId.SENTINEL);
      two.setParentBranch(one);
      three.setParentBranch(two);
      four.setParentBranch(three);
      five.setParentBranch(one);
      six.setParentBranch(five);
      seven.setParentBranch(six);
      eight.setParentBranch(one);
      nine.setParentBranch(eight);
      ten.setParentBranch(nine);
   }

   private void initOutsideParentList() {
      one.setParentBranch(BranchId.SENTINEL);
      two.setParentBranch(one);
      three.setParentBranch(two);
      four.setParentBranch(three);
      five.setParentBranch(BranchId.SENTINEL);
      six.setParentBranch(five);
      seven.setParentBranch(six);
      eight.setParentBranch(BranchId.SENTINEL);
      nine.setParentBranch(eight);
      ten.setParentBranch(nine);
   }
}