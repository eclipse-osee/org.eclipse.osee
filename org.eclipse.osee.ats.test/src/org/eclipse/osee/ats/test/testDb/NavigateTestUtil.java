/*
 * Created on May 14, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.test.testDb;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import junit.framework.TestCase;
import org.eclipse.osee.ats.navigate.AtsNavigateViewItems;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.widgets.xnavigate.XNavigateItem;

/**
 * @author Donald G. Dunne
 */
public class NavigateTestUtil {
   private static Map<String, XNavigateItem> nameToNavItem;

   public static XNavigateItem getAtsNavigateItem(String itemName) {
      if (nameToNavItem == null) {
         nameToNavItem = new HashMap<String, XNavigateItem>();
         // Setup hash if navigate items to names
         for (XNavigateItem item : AtsNavigateViewItems.getInstance().getSearchNavigateItems())
            NavigateTestUtil.createNameToNavItemMap(item, nameToNavItem);
      }
      return nameToNavItem.get(itemName);
   }

   public static void testExpectedVersusActual(String name, Collection<? extends Artifact> arts, Class<?> clazz, int expectedNumOfType) {
      int actualNumOfType = numOfType(arts, clazz);
      String expectedStr = "\"" + name + "\"   Expected: " + expectedNumOfType + "   Found: " + actualNumOfType;
      if (expectedNumOfType != actualNumOfType)
         System.err.println(expectedStr);
      else
         System.out.println(expectedStr);
      TestCase.assertTrue(actualNumOfType == expectedNumOfType);
   }

   public static int numOfType(Collection<? extends Artifact> arts, Class<?> clazz) {
      int num = 0;
      for (Artifact art : arts)
         if (clazz.isAssignableFrom(art.getClass())) num++;
      return num;
   }

   public static void createNameToNavItemMap(XNavigateItem item, Map<String, XNavigateItem> nameToItemMap) {
      nameToItemMap.put(item.getName(), item);
      for (XNavigateItem child : item.getChildren()) {
         createNameToNavItemMap(child, nameToItemMap);
      }
   }

}
