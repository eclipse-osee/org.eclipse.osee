/*******************************************************************************
 * Copyright (c) 2010 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.jdk.core.test.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import junit.framework.Assert;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.junit.Test;

/**
 * @author Donald G. Dunne
 */
public class CollectionsTest {
   @Test
   public void moveItem() {

      List<String> items = null;

      // Move item forward - insert before
      items = getTestList();
      boolean result = Collections.moveItem(items, "B", "D", false);
      Assert.assertTrue(result);
      Assert.assertEquals(Arrays.asList("A", "C", "B", "D", "E"), items);

      // Move item backward - insert before
      items = getTestList();
      result = Collections.moveItem(items, "D", "B", false);
      Assert.assertTrue(result);
      Assert.assertEquals(Arrays.asList("A", "D", "B", "C", "E"), items);

      // Move item forward - insert after
      items = getTestList();
      result = Collections.moveItem(items, "B", "D", true);
      Assert.assertTrue(result);
      Assert.assertEquals(Arrays.asList("A", "C", "D", "B", "E"), items);

      // Move item backward - insert after
      items = getTestList();
      result = Collections.moveItem(items, "D", "B", true);
      Assert.assertTrue(result);
      Assert.assertEquals(Arrays.asList("A", "B", "D", "C", "E"), items);

      // Move item forward to end
      items = getTestList();
      result = Collections.moveItem(items, "C", "E", false);
      Assert.assertTrue(result);
      Assert.assertEquals(Arrays.asList("A", "B", "D", "C", "E"), items);

      // Move item forward to end
      items = getTestList();
      result = Collections.moveItem(items, "A", "E", true);
      Assert.assertTrue(result);
      Assert.assertEquals(Arrays.asList("B", "C", "D", "E", "A"), items);

      // Move item backward to beginning
      items = getTestList();
      result = Collections.moveItem(items, "E", "A", false);
      Assert.assertTrue(result);
      Assert.assertEquals(Arrays.asList("E", "A", "B", "C", "D"), items);

      // Try moving something that doesn't exist
      items = getTestList();
      result = Collections.moveItem(items, "F", "C", false);
      // should fail
      Assert.assertFalse(result);
      // no change
      Assert.assertEquals(Arrays.asList("A", "B", "C", "D", "E"), items);

      // Try moving to somewhere that doesn't exist
      items = getTestList();
      result = Collections.moveItem(items, "C", "F", false);
      // should fail
      Assert.assertFalse(result);
      // no change
      Assert.assertEquals(Arrays.asList("A", "B", "C", "D", "E"), items);

   }

   private List<String> getTestList() {
      List<String> newList = new ArrayList<String>();
      newList.addAll(Arrays.asList("A", "B", "C", "D", "E"));
      return newList;
   }
}
