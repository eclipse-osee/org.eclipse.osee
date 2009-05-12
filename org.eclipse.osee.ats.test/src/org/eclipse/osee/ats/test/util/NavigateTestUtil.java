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
package org.eclipse.osee.ats.test.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import junit.framework.TestCase;
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.navigate.AtsNavigateViewItems;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.widgets.xnavigate.XNavigateItem;
import org.eclipse.swt.widgets.TreeItem;

/**
 * @author Donald G. Dunne
 */
public class NavigateTestUtil {
   private static Map<String, List<XNavigateItem>> nameToNavItem;
   public static String[] expectedErrorCols1 = {"ats.column.type", "ats.column.team", "ats.column.initWf"};
   public static String[] expectedErrorCols2 =
         {"ats.column.state", "ats.column.assignees", "ats.column.versionTarget", "ats.column.annualCostAvoidance",
               "ats.column.remainingHours", "ats.column.statePercentComplete", "ats.column.stateTaskPercentComplete",
               "ats.column.stateReviewPercentComplete", "ats.column.totalPercentComplete",
               "ats.column.stateHoursSpent", "ats.column.stateTaskHoursSpent", "ats.column.stateReviewHoursSpent",
               "ats.column.stateTotalHoursSpent", "ats.column.totalHoursSpent", "ats.column.implementer",
               "ats.column.completedDate", "ats.column.cancelledDate", "ats.column.manDaysNeeded"};

   public static XNavigateItem getAtsNavigateItem(String itemName) {
      if (nameToNavItem == null) {
         nameToNavItem = new HashMap<String, List<XNavigateItem>>(100);
         // Setup hash if navigate items to names
         for (XNavigateItem item : AtsNavigateViewItems.getInstance().getSearchNavigateItems())
            NavigateTestUtil.createNameToNavItemMap(item, nameToNavItem);
      }
      if (nameToNavItem.get(itemName) == null) throw new IllegalStateException(
            "No items of name \"" + itemName + "\" found");
      if (nameToNavItem.get(itemName).size() > 1) throw new IllegalStateException(
            "Multiple items of name \"" + itemName + "\" found; use getAtsNavigateItems");
      return nameToNavItem.get(itemName).iterator().next();
   }

   public static List<XNavigateItem> getAtsNavigateItems(String itemName) {
      if (nameToNavItem == null) {
         nameToNavItem = new HashMap<String, List<XNavigateItem>>(100);
         // Setup hash if navigate items to names
         for (XNavigateItem item : AtsNavigateViewItems.getInstance().getSearchNavigateItems())
            NavigateTestUtil.createNameToNavItemMap(item, nameToNavItem);
      }
      return nameToNavItem.get(itemName);
   }

   public static void testExpectedVersusActual(String name, Collection<? extends Artifact> arts, Class<?> clazz, int expectedNumOfType) {
      int actualNumOfType = numOfType(arts, clazz);
      String expectedStr =
            "\"" + name + "\"   Expected: " + expectedNumOfType + "   Found: " + actualNumOfType + "   Of Type: " + clazz;
      compare(expectedNumOfType, actualNumOfType, expectedStr);
   }

   public static void testExpectedVersusActual(String testStr, int expected, int actual) {
      String expectedStr = testStr + "Expected: " + expected + "   Found: " + actual;
      compare(expected, actual, expectedStr);
   }

   public static void testExpectedVersusActual(String testStr, boolean expectedCond, boolean actualCond) {
      String expectedStr = testStr + "Expected: " + expectedCond + "    Found: " + actualCond;
      compare(expectedCond, actualCond, expectedStr);
   }

   public static void compare(int expectedNumOfType, int actualNumOfType, String expectedStr) {
      if (expectedNumOfType != actualNumOfType)
         OseeLog.log(AtsPlugin.class, Level.SEVERE, expectedStr, null);
      else
         OseeLog.log(AtsPlugin.class, Level.INFO, expectedStr);
      TestCase.assertTrue(expectedStr, actualNumOfType == expectedNumOfType);
   }

   public static void compare(boolean expectedCond, boolean actualCond, String expectedStr) {
      if (expectedCond != actualCond)
         OseeLog.log(AtsPlugin.class, Level.SEVERE, expectedStr, null);
      else
         OseeLog.log(AtsPlugin.class, Level.INFO, expectedStr);
      TestCase.assertTrue(expectedStr, expectedCond == actualCond);
   }

   public static int numOfType(Collection<? extends Artifact> arts, Class<?> clazz) {
      int num = 0;
      for (Artifact art : arts)
         if (clazz.isAssignableFrom(art.getClass())) num++;
      return num;
   }

   public static void createNameToNavItemMap(XNavigateItem item, Map<String, List<XNavigateItem>> nameToItemMap) {
      List<XNavigateItem> items = new ArrayList<XNavigateItem>();
      if (nameToItemMap.containsKey(item.getName())) {
         items.addAll(nameToItemMap.get(item.getName()));
      }
      items.add(item);
      nameToItemMap.put(item.getName(), items);
      for (XNavigateItem child : item.getChildren()) {
         createNameToNavItemMap(child, nameToItemMap);
      }
   }

   public static void getAllArtifactChildren(TreeItem items[], Collection<Artifact> children) {
      for (TreeItem item : items) {
         if (item.getData() instanceof Artifact) {
            //if (!children.contains(item.getData())) children.add((Artifact) item.getData());
            children.add((Artifact) item.getData());
            if (item.getExpanded()) getAllArtifactChildren(item.getItems(), children);
         }
      }
   }

}
