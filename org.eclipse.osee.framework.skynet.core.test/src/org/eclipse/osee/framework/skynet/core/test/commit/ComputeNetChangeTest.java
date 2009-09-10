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
package org.eclipse.osee.framework.skynet.core.test.commit;

import static org.eclipse.osee.framework.core.enums.ModificationType.ARTIFACT_DELETED;
import static org.eclipse.osee.framework.core.enums.ModificationType.DELETED;
import static org.eclipse.osee.framework.core.enums.ModificationType.INTRODUCED;
import static org.eclipse.osee.framework.core.enums.ModificationType.MERGED;
import static org.eclipse.osee.framework.core.enums.ModificationType.MODIFIED;
import static org.eclipse.osee.framework.core.enums.ModificationType.NEW;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import junit.framework.Assert;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.operation.IOperation;
import org.eclipse.osee.framework.skynet.core.commit.ChangePair;
import org.eclipse.osee.framework.skynet.core.commit.CommitItem;
import org.eclipse.osee.framework.skynet.core.commit.ComputeNetChangeOperation;
import org.eclipse.osee.framework.skynet.core.commit.CommitItem.GammaKind;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Low-level Change Data Test - checks :
 * 
 * @author Roberto E. Escobar
 */
public class ComputeNetChangeTest {
   private static int itemId = 0;

   @BeforeClass
   public static void prepateTest() {
      itemId = 0;
   }

   private List<TestData> getTestData() {
      List<TestData> data = new ArrayList<TestData>();

      // New Or Introduced
      data.add(createTest(null, new ChangePair(4L, NEW), new ChangePair(5L, MODIFIED), null, new ChangePair(5L, NEW),
            false));
      data.add(createTest(null, new ChangePair(6L, INTRODUCED), new ChangePair(7L, MODIFIED), null, new ChangePair(7L,
            INTRODUCED), false));

      // Modified Once
      data.add(createTest(new ChangePair(10L, MODIFIED), null, new ChangePair(11L, MODIFIED), new ChangePair(10L,
            MODIFIED), new ChangePair(11L, MODIFIED), false));

      // Modified Twice
      data.add(createTest(new ChangePair(10L, NEW), new ChangePair(11L, MODIFIED), new ChangePair(12L, MODIFIED),
            new ChangePair(10L, NEW), new ChangePair(12L, MODIFIED), false));

      // Removal - new/intro and deleted
      data.add(createTest(null, new ChangePair(1L, NEW), new ChangePair(2L, DELETED), null, null, true));
      data.add(createTest(null, new ChangePair(2L, INTRODUCED), new ChangePair(3L, DELETED), null, null, true));
      data.add(createTest(null, new ChangePair(4L, NEW), new ChangePair(5L, ARTIFACT_DELETED), null, null, true));
      data.add(createTest(null, new ChangePair(6L, INTRODUCED), new ChangePair(7L, ARTIFACT_DELETED), null, null, true));
      data.add(createTest(null, null, new ChangePair(7693330L, INTRODUCED), new ChangePair(7693330L, NEW), null, true));
      data.add(createTest(null, null, new ChangePair(21345L, NEW), new ChangePair(21345L, NEW), null, true));

      // Undelete then delete again
      data.add(createTest(new ChangePair(4L, DELETED), new ChangePair(3L, MODIFIED), new ChangePair(4L, DELETED),
            new ChangePair(4L, DELETED), null, true));
      data.add(createTest(new ChangePair(4L, ARTIFACT_DELETED), null, new ChangePair(5L, MODIFIED), new ChangePair(4L,
            ARTIFACT_DELETED), null, true));

      // Delete Cases -
      data.add(createTest(new ChangePair(10L, MODIFIED), null, new ChangePair(10L, DELETED), new ChangePair(10L,
            MODIFIED), new ChangePair(10L, DELETED), false));
      data.add(createTest(new ChangePair(330L, NEW), null, new ChangePair(331L, ARTIFACT_DELETED), new ChangePair(330L,
            NEW), new ChangePair(331L, ARTIFACT_DELETED), false));
      data.add(createTest(new ChangePair(330L, NEW), new ChangePair(331L, ARTIFACT_DELETED), new ChangePair(331L,
            ARTIFACT_DELETED), new ChangePair(330L, NEW), new ChangePair(331L, ARTIFACT_DELETED), false));

      // Parent to Child Intro
      data.add(createTest(null, new ChangePair(10L, INTRODUCED), new ChangePair(11L, MODIFIED), new ChangePair(10L,
            MODIFIED), new ChangePair(11L, MODIFIED), false));

      // Attribute Modified/New
      data.add(createTest(null, null, new ChangePair(11L, MODIFIED), null, new ChangePair(11L, NEW), false));

      // Test Merge Items
      data.add(createTest(null, null, new ChangePair(12L, MODIFIED), null, new ChangePair(13L, MERGED), new ChangePair(
            13L, MERGED), false));
      data.add(createTest(null, null, new ChangePair(12L, NEW), null, new ChangePair(14L, MERGED), new ChangePair(14L,
            MERGED), false));

      return data;
   }

   @Test
   public void testNetChange() throws OseeCoreException {
      List<TestData> data = getTestData();
      List<CommitItem> items = new ArrayList<CommitItem>();
      for (TestData testData : data) {
         items.add(testData.getItem());
      }
      computeNetChange(items, IStatus.OK);

      for (int index = 0; index < data.size(); index++) {
         TestData testData = data.get(index);
         String message = String.format("Test: %s", index + 1);
         if (testData.isRemoved()) {
            Assert.assertFalse(message, items.contains(testData.getItem()));
         } else {
            Assert.assertTrue(message, items.contains(testData.getItem()));
            checkChange(message, testData.getExpectedNet(), testData.getItem().getNet());
         }
      }
   }

   @Test
   public void testErrorStates() {
      CommitItem change = createItem(null, null, new ChangePair(1L, NEW), new ChangePair(1L, NEW), null);
      computeNetChange(Arrays.asList(change), IStatus.ERROR);

      change = createItem(null, null, new ChangePair(1L, INTRODUCED), new ChangePair(1L, INTRODUCED), null);
      computeNetChange(Arrays.asList(change), IStatus.ERROR);

      // Source to Non-Parent commit
      change = createItem(new ChangePair(10L, MODIFIED), null, new ChangePair(11L, MODIFIED), null, null);
      computeNetChange(Arrays.asList(change), IStatus.ERROR);
   }

   private void checkChange(String message, ChangePair expected, ChangePair actual) {
      Assert.assertEquals(message, expected.getGammaId(), actual.getGammaId());
      Assert.assertEquals(message, expected.getModType(), actual.getModType());
   }

   private void computeNetChange(List<CommitItem> changes, int status) {
      IOperation operation = new ComputeNetChangeOperation(changes);
      operation.run(new NullProgressMonitor());
      String message = operation.getStatus().toString();
      Assert.assertEquals(message, status, operation.getStatus().getSeverity());
   }

   private TestData createTest(ChangePair base, ChangePair first, ChangePair current, ChangePair destination, ChangePair expected, boolean isRemoved) {
      return new TestData(createItem(base, first, current, destination, null), expected, isRemoved);
   }

   private TestData createTest(ChangePair base, ChangePair first, ChangePair current, ChangePair destination, ChangePair net, ChangePair expected, boolean isRemoved) {
      return new TestData(createItem(base, first, current, destination, net), expected, isRemoved);
   }

   private CommitItem createItem(ChangePair base, ChangePair first, ChangePair current, ChangePair destination, ChangePair net) {
      CommitItem change = new CommitItem(current.getGammaId(), current.getModType());
      change.setItemId(++itemId);

      GammaKind[] kinds = GammaKind.values();
      change.setKind(kinds[itemId % kinds.length]);

      if (base != null) {
         change.getBase().setModType(base.getModType());
         change.getBase().setGammaId(base.getGammaId());
      }
      if (first != null) {
         change.getFirst().setGammaId(first.getGammaId());
         change.getFirst().setModType(first.getModType());
      }
      if (destination != null) {
         change.getDestination().setGammaId(destination.getGammaId());
         change.getDestination().setModType(destination.getModType());
      }
      if (net != null) {
         change.getNet().setGammaId(net.getGammaId());
         change.getNet().setModType(net.getModType());
      }
      return change;
   }

   private final class TestData {
      private final CommitItem item;
      private final ChangePair expectedNet;
      private final boolean isRemoved;

      public TestData(CommitItem item, ChangePair expectedNet, boolean isRemoved) {
         super();
         this.item = item;
         this.expectedNet = expectedNet;
         this.isRemoved = isRemoved;
      }

      public CommitItem getItem() {
         return item;
      }

      public ChangePair getExpectedNet() {
         return expectedNet;
      }

      public boolean isRemoved() {
         return isRemoved;
      }

   }
}
