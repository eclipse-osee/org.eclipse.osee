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
 * @author Ryan Schmitt
 */
public class PairTest {
   private static Integer a = Integer.valueOf(144);
   private static Integer b = Integer.valueOf(233);
   private static Pair<Integer, Integer> mapToPi = new Pair<>(a, b);
   private static Pair<Integer, Integer> mapToE = new Pair<>(b, a);
   private static Pair<Integer, Integer> alsoMapToPi = new Pair<>(a, b);
   private static Pair<Integer, Integer> alsoMapToE = new Pair<>(b, a);
   private static Pair<Integer, Integer> firstEntryNull = new Pair<>(null, 144);
   private static Pair<Integer, Integer> secondEntryNull = new Pair<>(144, null);
   private static Pair<Integer, Integer> nonNull = new Pair<>(15, 144);

   @org.junit.Test
   public void testConstructor() {
      Assert.assertTrue(a.equals(mapToPi.getFirst()));
      Assert.assertTrue(b.equals(mapToPi.getSecond()));
      Assert.assertFalse(b.equals(mapToPi.getFirst()));
      Assert.assertFalse(a.equals(mapToPi.getSecond()));
   }

   @org.junit.Test
   public void testEquals() {
      Assert.assertTrue(mapToPi.equals(mapToPi));
      Assert.assertTrue(mapToPi.equals(alsoMapToPi));
      Assert.assertFalse(mapToPi.equals(mapToE));
   }

   @org.junit.Test
   public void testNulls() {
      Assert.assertTrue(firstEntryNull.equals(firstEntryNull));
      Assert.assertFalse(firstEntryNull.equals(nonNull));

      Assert.assertTrue(secondEntryNull.equals(secondEntryNull));
      Assert.assertFalse(secondEntryNull.equals(nonNull));

      Assert.assertTrue(firstEntryNull.toString().equals("[null, 144]"));
      Assert.assertTrue(secondEntryNull.toString().equals("[144, null]"));
      Assert.assertTrue(nonNull.toString().equals("[15, 144]"));
   }

   @org.junit.Test
   public void testSetters() {
      Pair<Integer, Integer> newPair = new Pair<>(0, 0);
      newPair.setFirst(a);
      newPair.setSecond(b);
      Assert.assertTrue(a.equals(newPair.getFirst()));
      Assert.assertTrue(b.equals(newPair.getSecond()));
      Assert.assertFalse(b.equals(newPair.getFirst()));
      Assert.assertFalse(a.equals(newPair.getSecond()));
      Assert.assertTrue(newPair.set(a, b).equals(newPair));
   }

   @org.junit.Test
   public void testHashCorrectness() {
      HashMap<Pair<Integer, Integer>, Double> hash = new HashMap<>();
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
