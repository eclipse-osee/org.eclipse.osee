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
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.operation.IOperation;
import org.eclipse.osee.framework.skynet.core.commit.ChangeItem;
import org.eclipse.osee.framework.skynet.core.commit.ChangeVersion;
import org.eclipse.osee.framework.skynet.core.commit.ComputeNetChangeOperation;
import org.junit.Test;

/**
 * Low-level Change Data Test
 * 
 * @author Roberto E. Escobar
 */
public class ComputeNetChangeTest {

   public List<TestData> getTestData() throws OseeCoreException {
      List<TestData> data = new ArrayList<TestData>();

      // New Or Introduced
      data.add(createTest(1, null, entry(4L, NEW), entry(5L, MODIFIED), null, entry(5L, NEW), false));
      data.add(createTest(2, null, entry(6L, INTRODUCED), entry(7L, MODIFIED), null, entry(7L, INTRODUCED), false));

      // Modified Once
      data.add(createTest(3, entry(10L, MODIFIED), null, entry(11L, MODIFIED), entry(10L, MODIFIED), entry(11L,
            MODIFIED), false));

      // Modified Twice
      data.add(createTest(4, entry(10L, NEW), entry(11L, MODIFIED), entry(12L, MODIFIED), entry(10L, NEW), entry(12L,
            MODIFIED), false));

      // Removal - new/intro and deleted
      data.add(createTest(5, null, entry(1L, NEW), entry(2L, DELETED), null, null, true));
      data.add(createTest(6, null, entry(2L, INTRODUCED), entry(3L, DELETED), null, null, true));
      data.add(createTest(7, null, entry(4L, NEW), entry(5L, ARTIFACT_DELETED), null, null, true));
      data.add(createTest(8, null, entry(6L, INTRODUCED), entry(7L, ARTIFACT_DELETED), null, null, true));
      data.add(createTest(9, null, null, entry(7693330L, INTRODUCED), entry(7693330L, NEW), null, true));
      data.add(createTest(10, null, null, entry(21345L, NEW), entry(21345L, NEW), null, true));

      data.add(createTest(11, null, null, entry(1L, NEW), entry(2L, MODIFIED), null, null, true));
      data.add(createTest(12, null, null, entry(1L, INTRODUCED), entry(2L, DELETED), null, null, true));

      // Undelete then delete again
      data.add(createTest(13, entry(4L, DELETED), entry(3L, MODIFIED), entry(4L, DELETED), entry(4L, DELETED), null,
            true));
      data.add(createTest(14, entry(4L, ARTIFACT_DELETED), null, entry(5L, MODIFIED), entry(4L, ARTIFACT_DELETED),
            null, true));

      // Delete Cases -
      data.add(createTest(15, entry(10L, MODIFIED), null, entry(10L, DELETED), entry(10L, MODIFIED),
            entry(10L, DELETED), false));
      data.add(createTest(16, entry(330L, NEW), null, entry(331L, ARTIFACT_DELETED), entry(330L, NEW), entry(331L,
            ARTIFACT_DELETED), false));
      data.add(createTest(17, entry(330L, NEW), entry(331L, ARTIFACT_DELETED), entry(331L, ARTIFACT_DELETED), entry(
            330L, NEW), entry(331L, ARTIFACT_DELETED), false));

      // Parent to Child Intro
      data.add(createTest(18, null, entry(10L, INTRODUCED), entry(11L, MODIFIED), entry(10L, MODIFIED), entry(11L,
            MODIFIED), false));

      // Attribute Modified/New
      data.add(createTest(19, null, null, entry(11L, MODIFIED), null, entry(11L, NEW), false));

      // Test Merge Items
      data.add(createTest(20, null, null, entry(12L, MODIFIED), null, entry(13L, MERGED), entry(13L, MERGED), false));
      data.add(createTest(21, null, null, entry(12L, NEW), null, entry(14L, MERGED), entry(14L, MERGED), false));
      data.add(createTest(22, entry(96915L, MODIFIED), entry(7290448L, MODIFIED), entry(7865315L, DELETED), entry(
            7432082L, MODIFIED), entry(7865315L, MERGED), entry(7865315L, DELETED), false));
      data.add(createTest(21, entry(96915L, MODIFIED), entry(7290448L, MODIFIED), entry(7865315L, ARTIFACT_DELETED),
            entry(7432082L, MODIFIED), entry(7865315L, MERGED), entry(7865315L, ARTIFACT_DELETED), false));

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
            ChangeItemTestUtil.checkChange(message, testData.getExpectedNet(), testData.getItem().getNet());
         }
      }
   }

   @Test
   public void testErrorStates() {
      List<ChangeItem> items = new ArrayList<ChangeItem>();

      // Source to Non-Parent commit
      items.add(ChangeItemTestUtil.createItem(3, entry(10L, MODIFIED), null, entry(11L, MODIFIED), null, null));

      computeNetChange(items, IStatus.ERROR);
   }

   private void computeNetChange(List<ChangeItem> changes, int status) {
      IOperation operation = new ComputeNetChangeOperation(changes);
      operation.run(new NullProgressMonitor());
      String message = operation.getStatus().toString();
      Assert.assertEquals(message, status, operation.getStatus().getSeverity());
   }

   private static TestData createTest(int itemId, ChangeVersion base, ChangeVersion first, ChangeVersion current, ChangeVersion destination, ChangeVersion expected, boolean isRemoved) {
      return new TestData(ChangeItemTestUtil.createItem(itemId, base, first, current, destination, null), expected, isRemoved);
   }

   private static TestData createTest(int itemId, ChangeVersion base, ChangeVersion first, ChangeVersion current, ChangeVersion destination, ChangeVersion net, ChangeVersion expected, boolean isRemoved) {
      return new TestData(ChangeItemTestUtil.createItem(itemId, base, first, current, destination, net), expected, isRemoved);
   }

   private static ChangeVersion entry(Long gammaId, ModificationType modType) {
      return ChangeItemTestUtil.createChange(gammaId, modType);
   }

   private static final class TestData {
      private final ChangeItem item;
      private final ChangeVersion expectedNet;
      private final boolean isRemoved;

      public TestData(ChangeItem item, ChangeVersion expectedNet, boolean isRemoved) {
         super();
         this.item = item;
         this.expectedNet = expectedNet;
         this.isRemoved = isRemoved;
      }

      public ChangeItem getItem() {
         return item;
      }

      public ChangeVersion getExpectedNet() {
         return expectedNet;
      }

      public boolean isRemoved() {
         return isRemoved;
      }
   }
}
