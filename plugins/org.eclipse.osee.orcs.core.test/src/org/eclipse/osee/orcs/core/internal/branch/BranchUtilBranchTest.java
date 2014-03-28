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

import java.util.LinkedList;
import java.util.List;
import org.eclipse.osee.framework.core.enums.BranchState;
import org.eclipse.osee.framework.core.enums.BranchType;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author David W. Miller
 */
public class BranchUtilBranchTest {

   private final Branch one = new Branch(Lib.generateUuid(), "one", BranchType.WORKING, BranchState.COMMITTED, true);
   private final Branch two = new Branch(Lib.generateUuid(), "two", BranchType.WORKING, BranchState.COMMITTED, true);
   private final Branch three = new Branch(Lib.generateUuid(), "three", BranchType.WORKING, BranchState.COMMITTED, true);
   private final Branch four = new Branch(Lib.generateUuid(), "four", BranchType.WORKING, BranchState.COMMITTED, true);
   private final Branch five = new Branch(Lib.generateUuid(), "five", BranchType.WORKING, BranchState.COMMITTED, true);
   private final Branch six = new Branch(Lib.generateUuid(), "six", BranchType.WORKING, BranchState.COMMITTED, true);
   private final Branch seven = new Branch(Lib.generateUuid(), "seven", BranchType.WORKING, BranchState.COMMITTED, true);
   private final Branch eight = new Branch(Lib.generateUuid(), "eight", BranchType.WORKING, BranchState.COMMITTED, true);
   private final Branch nine = new Branch(Lib.generateUuid(), "nine", BranchType.WORKING, BranchState.COMMITTED, true);
   private final Branch ten = new Branch(Lib.generateUuid(), "ten", BranchType.WORKING, BranchState.COMMITTED, true);
   private final Branch outside = new Branch(Lib.generateUuid(), "outside", BranchType.WORKING, BranchState.COMMITTED, true);

   private void initBranchParentageList() {
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
      one.setParentBranch(outside);
      two.setParentBranch(one);
      three.setParentBranch(two);
      four.setParentBranch(three);
      five.setParentBranch(outside);
      six.setParentBranch(five);
      seven.setParentBranch(six);
      eight.setParentBranch(outside);
      nine.setParentBranch(eight);
      ten.setParentBranch(nine);
   }

   private void initDisjointOrderList(List<Branch> branchList) {
      branchList.add(0, two);
      branchList.add(1, three);
      branchList.add(2, four);
      branchList.add(3, five);
      branchList.add(4, six);
      branchList.add(5, seven);
      branchList.add(6, eight);
      branchList.add(7, nine);
      branchList.add(8, ten);
      branchList.add(9, one);
   }

   private void initOutOfOrderList(List<Branch> branchList) {
      branchList.add(0, three);
      branchList.add(1, eight);
      branchList.add(2, one);
      branchList.add(3, four);
      branchList.add(4, five);
      branchList.add(5, six);
      branchList.add(6, ten);
      branchList.add(7, nine);
      branchList.add(8, two);
      branchList.add(9, seven);
   }

   @Test
   public void testConnectedBranchSorting() {
      List<Branch> branchList = new LinkedList<Branch>();
      initBranchParentageList();
      initOutOfOrderList(branchList);
      List<Branch> ordered = BranchUtil.orderByParent(branchList);
      Assert.assertEquals("[ten, nine, eight, seven, six, five, four, three, two, one]", ordered.toString());
   }

   @Test
   public void testDisjointBranchSorting() {
      List<Branch> branchList = new LinkedList<Branch>();
      initDisjointParentageList();
      initDisjointOrderList(branchList);
      List<Branch> ordered = BranchUtil.orderByParent(branchList);
      Assert.assertEquals("[four, three, two, seven, six, five, ten, nine, eight, one]", ordered.toString());
   }

   @Test
   public void testDisjointOutOfOrderBranchSorting() {
      List<Branch> branchList = new LinkedList<Branch>();
      initDisjointParentageList();
      initOutOfOrderList(branchList);
      List<Branch> ordered = BranchUtil.orderByParent(branchList);
      Assert.assertEquals("[four, ten, seven, three, six, nine, eight, two, five, one]", ordered.toString());
   }

   @Test
   public void testOutsideParentsBranchSorting() {
      List<Branch> branchList = new LinkedList<Branch>();
      initOutsideParentList();
      initOutOfOrderList(branchList);
      List<Branch> ordered = BranchUtil.orderByParent(branchList);
      Assert.assertEquals("[four, ten, three, two, seven, six, nine, eight, one, five]", ordered.toString());
   }
}
