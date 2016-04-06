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

import static org.eclipse.osee.framework.core.enums.BranchState.COMMITTED;
import static org.eclipse.osee.framework.core.enums.BranchType.WORKING;
import java.util.Arrays;
import java.util.List;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author David W. Miller
 */
public class BranchUtilBranchTest {

   private final Branch one = new Branch(Lib.generateUuid(), "one", WORKING, COMMITTED, true, false);
   private final Branch two = new Branch(Lib.generateUuid(), "two", WORKING, COMMITTED, true, false);
   private final Branch three = new Branch(Lib.generateUuid(), "three", WORKING, COMMITTED, true, false);
   private final Branch four = new Branch(Lib.generateUuid(), "four", WORKING, COMMITTED, true, false);
   private final Branch five = new Branch(Lib.generateUuid(), "five", WORKING, COMMITTED, true, false);
   private final Branch six = new Branch(Lib.generateUuid(), "six", WORKING, COMMITTED, true, false);
   private final Branch seven = new Branch(Lib.generateUuid(), "seven", WORKING, COMMITTED, true, false);
   private final Branch eight = new Branch(Lib.generateUuid(), "eight", WORKING, COMMITTED, true, false);
   private final Branch nine = new Branch(Lib.generateUuid(), "nine", WORKING, COMMITTED, true, false);
   private final Branch ten = new Branch(Lib.generateUuid(), "ten", WORKING, COMMITTED, true, false);
   private final Branch outside = new Branch(Lib.generateUuid(), "outside", WORKING, COMMITTED, true, false);

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

   private List<Branch> getDisjointOrderList() {
      return Arrays.asList(two, three, four, five, six, seven, eight, nine, ten, one);
   }

   private List<Branch> getOutOfOrderList() {
      return Arrays.asList(three, eight, one, four, five, six, ten, nine, two, seven);
   }

   @Test
   public void testConnectedBranchSorting() {
      initBranchParentageList();
      List<Branch> ordered = BranchUtil.orderByParent(getOutOfOrderList());
      Assert.assertEquals("[ten, nine, eight, seven, six, five, four, three, two, one]", ordered.toString());
   }

   @Test
   public void testDisjointBranchSorting() {
      initDisjointParentageList();
      List<Branch> ordered = BranchUtil.orderByParent(getDisjointOrderList());
      Assert.assertEquals("[four, three, two, seven, six, five, ten, nine, eight, one]", ordered.toString());
   }

   @Test
   public void testDisjointOutOfOrderBranchSorting() {
      initDisjointParentageList();
      List<Branch> ordered = BranchUtil.orderByParent(getOutOfOrderList());
      Assert.assertEquals("[four, ten, seven, three, six, nine, eight, two, five, one]", ordered.toString());
   }

   @Test
   public void testOutsideParentsBranchSorting() {
      initOutsideParentList();
      List<Branch> ordered = BranchUtil.orderByParent(getOutOfOrderList());
      Assert.assertEquals("[four, ten, three, two, seven, six, nine, eight, one, five]", ordered.toString());
   }
}
