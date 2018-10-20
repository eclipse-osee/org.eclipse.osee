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
package org.eclipse.osee.orcs.db.internal.change;

import static org.eclipse.osee.framework.core.enums.ModificationType.ARTIFACT_DELETED;
import static org.eclipse.osee.framework.core.enums.ModificationType.DELETED;
import static org.eclipse.osee.framework.core.enums.ModificationType.INTRODUCED;
import static org.eclipse.osee.framework.core.enums.ModificationType.MERGED;
import static org.eclipse.osee.framework.core.enums.ModificationType.MODIFIED;
import static org.eclipse.osee.framework.core.enums.ModificationType.NEW;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.core.model.change.ChangeIgnoreType;
import org.eclipse.osee.framework.core.model.change.ChangeItem;
import org.eclipse.osee.framework.core.model.change.ChangeItemUtil;
import org.eclipse.osee.framework.core.model.change.ChangeVersion;
import org.eclipse.osee.framework.jdk.core.type.OseeStateException;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Test Case for {@link ComputeNetChangeCallable}
 *
 * @author Roberto E. Escobar
 */
public class ComputeNetChangeTest {

   public List<TestData> getTestData() {
      List<TestData> data = new ArrayList<>();

      // New Or Introduced
      data.add(createTest(1L, entry(3L, NEW), entry(4L, NEW), entry(5L, MODIFIED), null, entry(5L, NEW), false));
      data.add(createTest(2L, entry(3L, NEW), entry(6L, INTRODUCED), entry(7L, MODIFIED), null, entry(7L, INTRODUCED),
         false));
      data.add(createTest(3L, entry(3L, NEW), null, entry(7693330L, INTRODUCED), entry(7693330L, NEW),
         entry(7693330L, INTRODUCED), false));

      // Modified Once
      data.add(createTest(4L, entry(10L, MODIFIED), null, entry(11L, MODIFIED), entry(10L, MODIFIED),
         entry(11L, MODIFIED), false));

      // Modified Twice
      data.add(createTest(5L, entry(10L, NEW), entry(11L, MODIFIED), entry(12L, MODIFIED), entry(10L, NEW),
         entry(12L, MODIFIED), false));

      // Removal - new/intro and deleted
      data.add(createTest(6L, entry(3L, NEW), entry(1L, NEW), entry(2L, DELETED), null, null, true));
      data.add(createTest(7L, entry(3L, NEW), entry(2L, INTRODUCED), entry(3L, DELETED), null, null, true));
      data.add(createTest(8L, entry(3L, NEW), entry(4L, NEW), entry(5L, ARTIFACT_DELETED), null, null, true));
      data.add(createTest(9L, entry(3L, NEW), entry(6L, INTRODUCED), entry(7L, ARTIFACT_DELETED), null, null, true));
      data.add(createTest(10L, entry(3L, NEW), null, entry(21345L, NEW), entry(21345L, NEW), null, true));
      data.add(createTest(11L, entry(3L, NEW), null, entry(1L, NEW), entry(2L, MODIFIED), null, entry(1L, NEW), false));
      data.add(createTest(12L, entry(3L, NEW), null, entry(1L, INTRODUCED), entry(2L, DELETED), null, null, true));

      // Undelete then delete again (Resurrected) not ignored
      data.add(createTest(13L, entry(2L, DELETED), entry(3L, MODIFIED), entry(4L, DELETED), entry(5L, DELETED),
         entry(4L, DELETED), false));
      data.add(createTest(14L, entry(2L, ARTIFACT_DELETED), null, entry(4L, ARTIFACT_DELETED),
         entry(5L, ARTIFACT_DELETED), entry(4L, ARTIFACT_DELETED), false));

      // Delete Cases -
      data.add(createTest(15L, entry(10L, MODIFIED), null, entry(10L, DELETED), entry(10L, MODIFIED),
         entry(10L, DELETED), false));
      data.add(createTest(16L, entry(330L, NEW), null, entry(331L, ARTIFACT_DELETED), entry(330L, NEW),
         entry(331L, ARTIFACT_DELETED), false));
      data.add(createTest(17L, entry(330L, NEW), entry(331L, ARTIFACT_DELETED), entry(331L, ARTIFACT_DELETED),
         entry(330L, NEW), entry(331L, ARTIFACT_DELETED), false));

      // Parent to Child Intro
      data.add(createTest(18L, null, entry(10L, INTRODUCED), entry(11L, MODIFIED), entry(10L, MODIFIED),
         entry(11L, MODIFIED), false));

      // Attribute Modified/New
      data.add(createTest(19L, null, null, entry(11L, MODIFIED), null, entry(11L, NEW), false));

      // Test Merge Items
      data.add(createTest(20L, null, null, entry(12L, MODIFIED), null, entry(13L, MERGED), entry(13L, MERGED), false));
      data.add(createTest(21L, null, null, entry(12L, NEW), null, entry(14L, MERGED), entry(14L, MERGED), false));
      data.add(createTest(22L, entry(96915L, MODIFIED), entry(7290448L, MODIFIED), entry(7865315L, DELETED),
         entry(7432082L, MODIFIED), entry(7865315L, MERGED), entry(7865315L, DELETED), false));
      data.add(createTest(21L, entry(96915L, MODIFIED), entry(7290448L, MODIFIED), entry(7865315L, ARTIFACT_DELETED),
         entry(7432082L, MODIFIED), entry(7865315L, MERGED), entry(7865315L, ARTIFACT_DELETED), false));

      return data;
   }

   @Test
   public void testNetChange() throws Exception {
      List<TestData> data = getTestData();
      List<ChangeItem> items = new ArrayList<>();
      for (TestData testData : data) {
         items.add(testData.getItem());
      }

      ChangeItemUtil.computeNetChanges(items);

      for (int index = 0; index < data.size(); index++) {
         TestData testData = data.get(index);
         String message = String.format("Test: %s", index + 1);
         if (testData.isRemoved()) {
            if (testData.getIgnoreType().isNone()) {
               Assert.assertFalse(message, items.contains(testData.getItem()));
            }
         } else {
            Assert.assertTrue(message, items.contains(testData.getItem()));
            ChangeTestUtility.checkChange(message, testData.getExpectedNet(), testData.getItem().getNetChange());
         }
      }
   }

   @Test(expected = OseeStateException.class)
   @Ignore
   public void testErrorStates() throws Exception {
      List<ChangeItem> items = new ArrayList<>();

      // Source to Non-Parent commit
      items.add(ChangeTestUtility.createItem(3L, entry(10L, MODIFIED), null, entry(11L, MODIFIED), null, null));

      ChangeItemUtil.computeNetChanges(items);
   }

   private static TestData createTest(Long itemId, ChangeVersion base, ChangeVersion first, ChangeVersion current, ChangeVersion destination, ChangeVersion expected, boolean isRemoved) {
      return new TestData(ChangeTestUtility.createItem(itemId, base, first, current, destination, null), expected,
         isRemoved);
   }

   private static TestData createTest(Long itemId, ChangeVersion base, ChangeVersion first, ChangeVersion current, ChangeVersion destination, ChangeVersion net, ChangeVersion expected, boolean isRemoved) {
      return new TestData(ChangeTestUtility.createItem(itemId, base, first, current, destination, net), expected,
         isRemoved);
   }

   private static ChangeVersion entry(Long gammaId, ModificationType modType) {
      return ChangeTestUtility.createChange(gammaId, modType);
   }

   private static final class TestData {
      private final ChangeItem item;
      private final ChangeVersion expectedNet;
      private final boolean isRemoved;

      public TestData(ChangeItem item, ChangeVersion expectedNet, boolean isRemoved) {
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

      public ChangeIgnoreType getIgnoreType() {
         return item.getIgnoreType();
      }
   }
}
