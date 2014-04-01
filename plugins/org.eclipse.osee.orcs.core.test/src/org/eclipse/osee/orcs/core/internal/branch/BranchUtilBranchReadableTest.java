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

import static org.mockito.Mockito.when;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import org.eclipse.osee.framework.jdk.core.type.ResultSet;
import org.eclipse.osee.framework.jdk.core.type.ResultSets;
import org.eclipse.osee.orcs.data.BranchReadable;
import org.eclipse.osee.orcs.search.BranchQuery;
import org.eclipse.osee.orcs.search.QueryFactory;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

/**
 * @author David W. Miller
 */
public class BranchUtilBranchReadableTest {

   @Mock
   private BranchReadable one, two, three, four, five, six, seven, eight, nine, ten, outside;
   @Mock
   QueryFactory queryFactory;
   @Mock
   BranchQuery branchQuery, bq1, bq2, bq3, bq4, bq5, bq6, bq7, bq8, bq9, bqout;

   @Before
   public void init() {
      MockitoAnnotations.initMocks(this);
      initBranchQueryElements();
   }

   @Test
   public void testConnectedBranchSorting() {
      initBranchParentageList();
      List<BranchReadable> branchList = new LinkedList<BranchReadable>();
      initOutOfOrderList(branchList);
      List<BranchReadable> ordered = BranchUtil.orderByParentReadable(queryFactory, branchList);
      Assert.assertEquals("[ten, nine, eight, seven, six, five, four, three, two, one]", ordered.toString());
   }

   @Test
   public void testDisjointBranchSorting() {
      initDisjointParentageList();
      List<BranchReadable> branchList = new LinkedList<BranchReadable>();
      initDisjointOrderList(branchList);
      List<BranchReadable> ordered = BranchUtil.orderByParentReadable(queryFactory, branchList);
      Assert.assertEquals("[four, three, two, seven, six, five, ten, nine, eight, one]", ordered.toString());
   }

   @Test
   public void testDisjointOutOfOrderBranchSorting() {
      initDisjointParentageList();
      List<BranchReadable> branchList = new LinkedList<BranchReadable>();
      initOutOfOrderList(branchList);
      List<BranchReadable> ordered = BranchUtil.orderByParentReadable(queryFactory, branchList);
      Assert.assertEquals("[four, ten, seven, three, six, nine, eight, two, five, one]", ordered.toString());
   }

   @Test
   public void testOutsideParentsBranchSorting() {
      List<BranchReadable> branchList = new LinkedList<BranchReadable>();
      initOutsideParentList();
      initOutOfOrderList(branchList);
      List<BranchReadable> ordered = BranchUtil.orderByParentReadable(queryFactory, branchList);
      Assert.assertEquals("[four, ten, three, two, seven, six, nine, eight, one, five]", ordered.toString());
   }

   private void initOutOfOrderList(List<BranchReadable> branchList) {
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

   private void initDisjointOrderList(List<BranchReadable> branchList) {
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

   private void initBranchParentageList() {
      when(one.getParentBranch()).thenReturn((long) -1);
      when(two.getParentBranch()).thenReturn((long) 1);
      when(three.getParentBranch()).thenReturn((long) 2);
      when(four.getParentBranch()).thenReturn((long) 3);
      when(five.getParentBranch()).thenReturn((long) 4);
      when(six.getParentBranch()).thenReturn((long) 5);
      when(seven.getParentBranch()).thenReturn((long) 6);
      when(eight.getParentBranch()).thenReturn((long) 7);
      when(nine.getParentBranch()).thenReturn((long) 8);
      when(ten.getParentBranch()).thenReturn((long) 9);
   }

   private void initDisjointParentageList() {
      when(one.getParentBranch()).thenReturn((long) -1);
      when(two.getParentBranch()).thenReturn((long) 1);
      when(three.getParentBranch()).thenReturn((long) 2);
      when(four.getParentBranch()).thenReturn((long) 3);
      when(five.getParentBranch()).thenReturn((long) 1);
      when(six.getParentBranch()).thenReturn((long) 5);
      when(seven.getParentBranch()).thenReturn((long) 6);
      when(eight.getParentBranch()).thenReturn((long) 1);
      when(nine.getParentBranch()).thenReturn((long) 8);
      when(ten.getParentBranch()).thenReturn((long) 9);
   }

   private void initOutsideParentList() {
      when(one.getParentBranch()).thenReturn((long) -1);
      when(two.getParentBranch()).thenReturn((long) 1);
      when(three.getParentBranch()).thenReturn((long) 2);
      when(four.getParentBranch()).thenReturn((long) 3);
      when(five.getParentBranch()).thenReturn((long) -1);
      when(six.getParentBranch()).thenReturn((long) 5);
      when(seven.getParentBranch()).thenReturn((long) 6);
      when(eight.getParentBranch()).thenReturn((long) -1);
      when(nine.getParentBranch()).thenReturn((long) 8);
      when(ten.getParentBranch()).thenReturn((long) 9);
   }

   private void initBranchQueryElements() {
      when(queryFactory.branchQuery()).thenReturn(branchQuery);
      when(branchQuery.andUuids(-1L)).thenReturn(bqout);
      when(bqout.getResults()).thenReturn(makeResults(outside));

      when(branchQuery.andUuids(1L)).thenReturn(bq1);
      when(branchQuery.andUuids(2L)).thenReturn(bq2);
      when(branchQuery.andUuids(3L)).thenReturn(bq3);
      when(branchQuery.andUuids(4L)).thenReturn(bq4);
      when(branchQuery.andUuids(5L)).thenReturn(bq5);
      when(branchQuery.andUuids(6L)).thenReturn(bq6);
      when(branchQuery.andUuids(7L)).thenReturn(bq7);
      when(branchQuery.andUuids(8L)).thenReturn(bq8);
      when(branchQuery.andUuids(9L)).thenReturn(bq9);

      when(bq1.getResults()).thenReturn(makeResults(one));
      when(bq2.getResults()).thenReturn(makeResults(two));
      when(bq3.getResults()).thenReturn(makeResults(three));
      when(bq4.getResults()).thenReturn(makeResults(four));
      when(bq5.getResults()).thenReturn(makeResults(five));
      when(bq6.getResults()).thenReturn(makeResults(six));
      when(bq7.getResults()).thenReturn(makeResults(seven));
      when(bq8.getResults()).thenReturn(makeResults(eight));
      when(bq9.getResults()).thenReturn(makeResults(nine));
   }

   private ResultSet<BranchReadable> makeResults(BranchReadable input) {
      List<BranchReadable> list = new ArrayList<BranchReadable>();
      list.add(input);
      ResultSet<BranchReadable> results = ResultSets.newResultSet(list);
      return results;
   }
}
