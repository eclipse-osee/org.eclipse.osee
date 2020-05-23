/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.framework.jdk.core.type;

import java.util.HashMap;
import org.junit.Assert;

/**
 * Test Case for {@link MatchLocation}
 * 
 * @author Roberto E. Escobar
 */
public class MatchLocationTest {

   private static int a = 144;
   private static int b = 233;
   private static MatchLocation mapToPi = new MatchLocation(a, b);
   private static MatchLocation mapToE = new MatchLocation(b, a);
   private static MatchLocation alsoMapToPi = new MatchLocation(a, b);
   private static MatchLocation alsoMapToE = new MatchLocation(b, a);

   @org.junit.Test
   public void testConstructor() {
      Assert.assertTrue(a == mapToPi.getStartPosition());
      Assert.assertTrue(b == mapToPi.getEndPosition());
      Assert.assertTrue(b != mapToPi.getStartPosition());
      Assert.assertTrue(a != mapToPi.getEndPosition());
   }

   @org.junit.Test
   public void testEquals() {
      Assert.assertTrue(mapToPi.equals(mapToPi));
      Assert.assertTrue(mapToPi.equals(alsoMapToPi));
      Assert.assertFalse(mapToPi.equals(mapToE));
   }

   @org.junit.Test
   public void testSetters() {
      MatchLocation newLocation = new MatchLocation(0, 0);
      newLocation.setStartPosition(a);
      newLocation.setEndPosition(b);
      Assert.assertTrue(a == newLocation.getStartPosition());
      Assert.assertTrue(b == newLocation.getEndPosition());
      Assert.assertTrue(b != newLocation.getStartPosition());
      Assert.assertTrue(a != newLocation.getEndPosition());
      Assert.assertTrue(newLocation.set(a, b).equals(newLocation));
   }

   @org.junit.Test
   public void testHashCorrectness() {
      HashMap<MatchLocation, Double> hash = new HashMap<>();
      hash.put(mapToPi, Math.PI);
      hash.put(mapToE, Math.E);
      Assert.assertTrue(hash.get(mapToPi).equals(Math.PI));
      Assert.assertTrue(hash.get(mapToE).equals(Math.E));
      Assert.assertTrue(hash.get(alsoMapToPi).equals(Math.PI));
      Assert.assertTrue(hash.get(alsoMapToE).equals(Math.E));
      Assert.assertFalse(hash.get(mapToPi).equals(Math.E));
      Assert.assertFalse(hash.get(mapToE).equals(Math.PI));
      Assert.assertFalse(mapToPi.equals(mapToE));
      Assert.assertTrue(mapToPi.equals(mapToPi));
   }
}
