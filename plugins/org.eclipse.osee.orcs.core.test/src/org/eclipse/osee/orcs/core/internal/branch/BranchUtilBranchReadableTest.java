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
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.BranchReadable;
import org.eclipse.osee.framework.core.data.TransactionId;
import org.eclipse.osee.framework.core.enums.BranchArchivedState;
import org.eclipse.osee.framework.core.enums.BranchState;
import org.eclipse.osee.framework.core.enums.BranchType;
import org.eclipse.osee.framework.jdk.core.type.NamedIdBase;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @author David W. Miller
 */
public class BranchUtilBranchReadableTest {

   private BranchReadableImpl one, two, three, four, five, six, seven, eight, nine, ten;

   @Before
   public void init() {
      one = new BranchReadableImpl(1);
      two = new BranchReadableImpl(2);
      three = new BranchReadableImpl(3);
      four = new BranchReadableImpl(4);
      five = new BranchReadableImpl(5);
      six = new BranchReadableImpl(6);
      seven = new BranchReadableImpl(7);
      eight = new BranchReadableImpl(8);
      nine = new BranchReadableImpl(9);
      ten = new BranchReadableImpl(10);
   }

   private static class BranchReadableImpl extends NamedIdBase implements BranchReadable {
      private BranchId parent;

      public void setParentBranch(BranchId parent) {
         this.parent = parent;
      }

      public BranchReadableImpl(int branchId) {
         super((long) branchId, String.valueOf(branchId));
      }

      @Override
      public BranchArchivedState getArchiveState() {
         return null;
      }

      @Override
      public BranchState getBranchState() {
         return null;
      }

      @Override
      public BranchType getBranchType() {
         return null;
      }

      @Override
      public boolean hasParentBranch() {
         return false;
      }

      @Override
      public ArtifactId getAssociatedArtifact() {
         return ArtifactId.SENTINEL;
      }

      @Override
      public TransactionId getBaseTransaction() {
         return null;
      }

      @Override
      public TransactionId getSourceTransaction() {
         return null;
      }

      @Override
      public BranchId getParentBranch() {
         return parent;
      }

      @Override
      public boolean isInheritAccessControl() {
         return false;
      }
   }

   @Test
   public void testConnectedBranchSorting() {
      initBranchParentageList();
      List<BranchReadable> ordered = BranchUtil.orderByParentReadable(getOutOfOrderList());
      Assert.assertEquals("[10, 9, 8, 7, 6, 5, 4, 3, 2, 1]", ordered.toString());
   }

   @Test
   public void testDisjointBranchSorting() {
      initDisjointParentageList();
      List<BranchReadable> ordered = BranchUtil.orderByParentReadable(getDisjointOrderList());
      Assert.assertEquals("[4, 3, 2, 7, 6, 5, 10, 9, 8, 1]", ordered.toString());
   }

   @Test
   public void testDisjointOutOfOrderBranchSorting() {
      initDisjointParentageList();
      List<BranchReadable> ordered = BranchUtil.orderByParentReadable(getOutOfOrderList());
      Assert.assertEquals("[4, 10, 7, 3, 6, 9, 8, 2, 5, 1]", ordered.toString());
   }

   @Test
   public void testOutsideParentsBranchSorting() {
      initOutsideParentList();
      List<BranchReadable> ordered = BranchUtil.orderByParentReadable(getOutOfOrderList());
      Assert.assertEquals("[4, 10, 3, 2, 7, 6, 9, 8, 1, 5]", ordered.toString());
   }

   private List<BranchReadable> getOutOfOrderList() {
      return Arrays.asList(three, eight, one, four, five, six, ten, nine, two, seven);
   }

   private List<BranchReadable> getDisjointOrderList() {
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