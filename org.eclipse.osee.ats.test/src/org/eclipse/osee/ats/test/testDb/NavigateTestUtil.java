/*
 * Created on May 14, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.test.testDb;

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

/**
 * @author Donald G. Dunne
 */
public class NavigateTestUtil {
   private static Map<String, List<XNavigateItem>> nameToNavItem;

   public static XNavigateItem getAtsNavigateItem(String itemName) {
      if (nameToNavItem == null) {
         nameToNavItem = new HashMap<String, List<XNavigateItem>>(100);
         // Setup hash if navigate items to names
         for (XNavigateItem item : AtsNavigateViewItems.getInstance().getSearchNavigateItems())
            NavigateTestUtil.createNameToNavItemMap(item, nameToNavItem);
      }
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
      if (expectedNumOfType != actualNumOfType)
         OseeLog.log(AtsPlugin.class, Level.SEVERE,  expectedStr, null);
      else
         OseeLog.log(AtsPlugin.class, Level.INFO, expectedStr);
      TestCase.assertTrue(expectedStr, actualNumOfType == expectedNumOfType);
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

}
