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
package org.eclipse.osee.ats.client.integration.tests.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Level;
import org.eclipse.osee.ats.navigate.NavigateViewItems;
import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.jdk.core.type.HashCollection;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItem;
import org.eclipse.swt.widgets.TreeItem;
import org.junit.Assert;

/**
 * @author Donald G. Dunne
 */
public final class NavigateTestUtil {

   private static final HashCollection<String, XNavigateItem> nameToNavItem =
      new HashCollection<String, XNavigateItem>(true, ArrayList.class, 100);

   private static boolean ensurePopulatedRanOnce = false;

   public static final String[] expectedErrorCols1 =
      {"ats.column.type", "ats.column.team", "ats.column.initWf", "ats.column.origWf"};

   public static final String[] expectedErrorCols2 = {
      "ats.column.state",
      "ats.column.assignees",
      "ats.column.versionTarget",
      "ats.column.annualCostAvoidance",
      "ats.column.remainingHours",
      "ats.column.statePercentComplete",
      "ats.column.stateTaskPercentComplete",
      "ats.column.stateReviewPercentComplete",
      "ats.column.totalPercentComplete",
      "ats.column.stateHoursSpent",
      "ats.column.stateTaskHoursSpent",
      "ats.column.stateReviewHoursSpent",
      "ats.column.stateTotalHoursSpent",
      "ats.column.totalHoursSpent",
      "ats.column.implementer",
      "ats.column.completedDate",
      "ats.column.cancelledDate",
      "ats.column.manDaysNeeded"};

   private NavigateTestUtil() {
      // Test Utility
   }

   public static XNavigateItem getAtsNavigateItem(String itemName) {
      ensurePopulated();
      Collection<XNavigateItem> navigateItems = nameToNavItem.getValues(itemName);
      Assert.assertNotNull("No items of name [" + itemName + "] found", navigateItems);
      Assert.assertFalse("0 items found of name [" + itemName + "]", navigateItems.isEmpty());
      Assert.assertTrue("Multiple items of name [" + itemName + "] found; use getAtsNavigateItems",
         navigateItems.size() == 1);
      return navigateItems.iterator().next();
   }

   public static Collection<XNavigateItem> getAtsNavigateItems(String itemName) {
      ensurePopulated();
      return nameToNavItem.getValues(itemName);
   }

   private static synchronized void ensurePopulated() {
      if (!ensurePopulatedRanOnce) {
         ensurePopulatedRanOnce = true;
         for (XNavigateItem item : NavigateViewItems.getInstance().getSearchNavigateItems()) {
            addToMap(item);
         }
      }
   }

   private static void addToMap(XNavigateItem item) {
      nameToNavItem.put(item.getName(), item);
      for (XNavigateItem child : item.getChildren()) {
         addToMap(child);
      }
   }

   public static void testExpectedVersusActual(String name, Collection<? extends Artifact> arts, IArtifactType artType, int expectedNumOfType) {
      int actualNumOfType = numOfType(arts, artType);
      String expectedStr = String.format("\"%s\"   Expected: %s   Found: %s   Of Type: %s", name, expectedNumOfType,
         actualNumOfType, artType);
      compare(expectedNumOfType, actualNumOfType, expectedStr);
   }

   public static void testExpectedVersusActual(String testStr, int expected, int actual) {
      String expectedStr = String.format("%sExpected: %s   Found: %s", testStr, expected, actual);
      compare(expected, actual, expectedStr);
   }

   public static void testExpectedVersusActual(String testStr, boolean expectedCond, boolean actualCond) {
      String expectedStr = String.format("%sExpected: %s    Found: %s", testStr, expectedCond, actualCond);
      compare(expectedCond, actualCond, expectedStr);
   }

   public static void compare(int expectedNumOfType, int actualNumOfType, String expectedStr) {
      if (expectedNumOfType != actualNumOfType) {
         OseeLog.log(NavigateTestUtil.class, Level.SEVERE, expectedStr, null);
      }
      Assert.assertTrue(expectedStr, actualNumOfType == expectedNumOfType);
   }

   public static void compare(boolean expectedCond, boolean actualCond, String expectedStr) {
      if (expectedCond != actualCond) {
         OseeLog.log(NavigateTestUtil.class, Level.SEVERE, expectedStr, null);
      }
      Assert.assertTrue(expectedStr, expectedCond == actualCond);
   }

   public static int numOfType(Collection<? extends Artifact> arts, IArtifactType artType) {
      int num = 0;
      for (Artifact art : arts) {
         if (art.isOfType(artType)) {
            num++;
         }
      }
      return num;
   }

   public static void getAllArtifactChildren(TreeItem items[], Collection<Artifact> children) {
      for (TreeItem item : items) {
         if (item.getData() instanceof Artifact) {
            //if (!children.contains(item.getData())) children.add((Artifact) item.getData());
            children.add((Artifact) item.getData());
            if (item.getExpanded()) {
               getAllArtifactChildren(item.getItems(), children);
            }
         }
      }
   }

}
