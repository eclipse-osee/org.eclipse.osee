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
import java.util.List;
import junit.framework.Assert;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.operation.IOperation;
import org.eclipse.osee.framework.skynet.core.commit.ChangeItem;
import org.eclipse.osee.framework.skynet.core.commit.ComputeNetChangeOperation;
import org.eclipse.osee.framework.skynet.core.commit.VersionedChange;
import org.junit.Test;

/**
 * Low-level Change Data Test
 * 
 * @author Roberto E. Escobar
 */
public class ComputeNetChangeTest {

   private List<TestData> getTestData() {
      List<TestData> data = new ArrayList<TestData>();

      // New Or Introduced
      data.add(createTest(1, null, new VersionedChange(4L, NEW, 1L), new VersionedChange(5L, MODIFIED, 1L), null,
            new VersionedChange(5L, NEW, 1L), false));
      data.add(createTest(2, null, new VersionedChange(6L, INTRODUCED, 1L), new VersionedChange(7L, MODIFIED, 1L), null, new VersionedChange(
            7L, INTRODUCED, 1L), false));

      // Modified Once
      data.add(createTest(3, new VersionedChange(10L, MODIFIED, 1L), null, new VersionedChange(11L, MODIFIED, 1L), new VersionedChange(10L,
            MODIFIED, 1L), new VersionedChange(11L, MODIFIED, 1L), false));

      // Modified Twice
      data.add(createTest(4, new VersionedChange(10L, NEW, 1L), new VersionedChange(11L, MODIFIED, 1L), new VersionedChange(12L, MODIFIED, 1L),
            new VersionedChange(10L, NEW, 1L), new VersionedChange(12L, MODIFIED, 1L), false));

      // Removal - new/intro and deleted
      data.add(createTest(5, null, new VersionedChange(1L, NEW, 1L), new VersionedChange(2L, DELETED, 1L), null, null, true));
      data.add(createTest(6, null, new VersionedChange(2L, INTRODUCED, 1L), new VersionedChange(3L, DELETED, 1L), null, null, true));
      data.add(createTest(7, null, new VersionedChange(4L, NEW, 1L), new VersionedChange(5L, ARTIFACT_DELETED, 1L), null, null, true));
      data.add(createTest(8, null, new VersionedChange(6L, INTRODUCED, 1L), new VersionedChange(7L, ARTIFACT_DELETED, 1L), null, null,
            true));
      data.add(createTest(9, null, null, new VersionedChange(7693330L, INTRODUCED, 1L), new VersionedChange(7693330L, NEW, 1L), null,
            true));
      data.add(createTest(10, null, null, new VersionedChange(21345L, NEW, 1L), new VersionedChange(21345L, NEW, 1L), null, true));

      data.add(createTest(11, null, null, new VersionedChange(1L, NEW, 1L), new VersionedChange(2L, MODIFIED, 1L), null, null, true));
      data.add(createTest(12, null, null, new VersionedChange(1L, INTRODUCED, 1L), new VersionedChange(2L, DELETED, 1L), null, null, true));

      // Undelete then delete again
      data.add(createTest(13, new VersionedChange(4L, DELETED,1L), new VersionedChange(3L, MODIFIED,1L), new VersionedChange(4L, DELETED,1L),
            new VersionedChange(4L, DELETED,1L), null, true));
      data.add(createTest(14, new VersionedChange(4L, ARTIFACT_DELETED,1L), null, new VersionedChange(5L, MODIFIED,1L), new VersionedChange(
            4L, ARTIFACT_DELETED,1L), null, true));

      // Delete Cases -
      data.add(createTest(15, new VersionedChange(10L, MODIFIED,1L), null, new VersionedChange(10L, DELETED,1L), new VersionedChange(10L,
            MODIFIED,1L), new VersionedChange(10L, DELETED,1L), false));
      data.add(createTest(16, new VersionedChange(330L, NEW,1L), null, new VersionedChange(331L, ARTIFACT_DELETED,1L), new VersionedChange(
            330L, NEW,1L), new VersionedChange(331L, ARTIFACT_DELETED,1L), false));
      data.add(createTest(17, new VersionedChange(330L, NEW, 1L), new VersionedChange(331L, ARTIFACT_DELETED, 1L), new VersionedChange(331L,
            ARTIFACT_DELETED, 1L), new VersionedChange(330L, NEW, 1L), new VersionedChange(331L, ARTIFACT_DELETED, 1L), false));

      // Parent to Child Intro
      data.add(createTest(18, null, new VersionedChange(10L, INTRODUCED, 1L), new VersionedChange(11L, MODIFIED, 1L), new VersionedChange(10L,
            MODIFIED, 1L), new VersionedChange(11L, MODIFIED, 1L), false));

      // Attribute Modified/New
      data.add(createTest(19, null, null, new VersionedChange(11L, MODIFIED, 1L), null, new VersionedChange(11L, NEW, 1L), false));

      // Test Merge Items
      data.add(createTest(20, null, null, new VersionedChange(12L, MODIFIED, 1L), null, new VersionedChange(13L, MERGED, 1L),
            new VersionedChange(13L, MERGED, 1L), false));
      data.add(createTest(21, null, null, new VersionedChange(12L, NEW, 1L), null, new VersionedChange(14L, MERGED, 1L), new VersionedChange(
            14L, MERGED, 1L), false));
      data.add(createTest(22, new VersionedChange(96915L, MODIFIED, 1L), new VersionedChange(7290448L, MODIFIED, 1L), new VersionedChange(
            7865315L, DELETED, 1L), new VersionedChange(7432082L, MODIFIED, 1L), new VersionedChange(7865315L, MERGED, 1L), new VersionedChange(
            7865315L, DELETED, 1L), false));
      data.add(createTest(21, new VersionedChange(96915L, MODIFIED, 1L), new VersionedChange(7290448L, MODIFIED, 1L), new VersionedChange(
            7865315L, ARTIFACT_DELETED, 1L), new VersionedChange(7432082L, MODIFIED, 1L), new VersionedChange(7865315L, MERGED, 1L),
            new VersionedChange(7865315L, ARTIFACT_DELETED, 1L), false));

      return data;
   }

   @Test
   public void testNetChange() throws OseeCoreException {
      List<TestData> data = getTestData();
      List<ChangeItem> items = new ArrayList<ChangeItem>();
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
            CommitUtil.checkChange(message, testData.getExpectedNet(), testData.getItem().getNet());
         }
      }
   }

   @Test
   public void testErrorStates() {
      List<ChangeItem> items = new ArrayList<ChangeItem>();
      items.clear();
      // Source to Non-Parent commit
      items.add(CommitUtil.createItem(3, new VersionedChange(10L, MODIFIED, 1L), null, new VersionedChange(11L, MODIFIED, 1L), null, null));
      computeNetChange(items, IStatus.ERROR);
   }

   private void computeNetChange(List<ChangeItem> changes, int status) {
      IOperation operation = new ComputeNetChangeOperation(changes);
      operation.run(new NullProgressMonitor());
      String message = operation.getStatus().toString();
      Assert.assertEquals(message, status, operation.getStatus().getSeverity());
   }

   private TestData createTest(int itemId, VersionedChange base, VersionedChange first, VersionedChange current, VersionedChange destination, VersionedChange expected, boolean isRemoved) {
      return new TestData(CommitUtil.createItem(itemId, base, first, current, destination, null), expected, isRemoved);
   }

   private TestData createTest(int itemId, VersionedChange base, VersionedChange first, VersionedChange current, VersionedChange destination, VersionedChange net, VersionedChange expected, boolean isRemoved) {
      return new TestData(CommitUtil.createItem(itemId, base, first, current, destination, net), expected, isRemoved);
   }

   private final class TestData {
      private final ChangeItem item;
      private final VersionedChange expectedNet;
      private final boolean isRemoved;

      public TestData(ChangeItem item, VersionedChange expectedNet, boolean isRemoved) {
         super();
         this.item = item;
         this.expectedNet = expectedNet;
         this.isRemoved = isRemoved;
      }

      public ChangeItem getItem() {
         return item;
      }

      public VersionedChange getExpectedNet() {
         return expectedNet;
      }

      public boolean isRemoved() {
         return isRemoved;
      }
   }
}
