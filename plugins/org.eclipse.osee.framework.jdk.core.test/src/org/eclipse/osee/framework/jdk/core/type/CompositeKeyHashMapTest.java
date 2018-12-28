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
package org.eclipse.osee.framework.jdk.core.type;

import java.util.List;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author Ryan Schmitt
 */
public class CompositeKeyHashMapTest {
   @Test
   public void testEnumerateKeys() {
      CompositeKeyHashMap<String, String, String> testMap = new CompositeKeyHashMap<>();
      testMap.put("red", "blue", "value1");
      testMap.put("red", "green", "value2");
      testMap.put("red", "red", "value3");

      testMap.put("green", "green", "value4");
      testMap.put("blue", "green", "value5");
      List<Pair<String, String>> keyPairs = testMap.getEnumeratedKeys();
      Assert.assertEquals(5, keyPairs.size());
      Assert.assertTrue(keyPairs.contains(new Pair<>("red", "red")));
      Assert.assertTrue(keyPairs.contains(new Pair<>("red", "green")));
      Assert.assertTrue(keyPairs.contains(new Pair<>("red", "blue")));
      Assert.assertTrue(keyPairs.contains(new Pair<>("green", "green")));
      Assert.assertTrue(keyPairs.contains(new Pair<>("blue", "green")));
   }
}
