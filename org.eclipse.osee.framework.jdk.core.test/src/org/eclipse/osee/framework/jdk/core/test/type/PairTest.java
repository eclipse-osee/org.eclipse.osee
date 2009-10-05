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
package org.eclipse.osee.framework.jdk.core.test.type;

import java.util.HashMap;
import junit.framework.TestCase;
import org.eclipse.osee.framework.jdk.core.type.Pair;

/**
 * @author Ryan Schmitt
 */
public class PairTest extends TestCase {
   private static Integer a = Integer.valueOf(144);
   private static Integer b = Integer.valueOf(233);
   private static Pair<Integer, Integer> mapToPi = new Pair<Integer, Integer>(a, b);
   private static Pair<Integer, Integer> mapToE = new Pair<Integer, Integer>(b, a);
   private static Pair<Integer, Integer> alsoMapToPi = new Pair<Integer, Integer>(a, b);
   private static Pair<Integer, Integer> alsoMapToE = new Pair<Integer, Integer>(b, a);
   private static Pair<Integer, Integer> firstEntryNull = new Pair<Integer, Integer>(null, 144);
   private static Pair<Integer, Integer> secondEntryNull = new Pair<Integer, Integer>(144, null);
   private static Pair<Integer, Integer> nonNull = new Pair<Integer, Integer>(15, 144);

   @org.junit.Test
   public static void testConstructor() {
      assertTrue(a.equals(mapToPi.getFirst()));
      assertTrue(b.equals(mapToPi.getSecond()));
      assertFalse(b.equals(mapToPi.getFirst()));
      assertFalse(a.equals(mapToPi.getSecond()));
   }

   @org.junit.Test
   public static void testEquals() {
      assertTrue(mapToPi.equals(mapToPi));
      assertTrue(mapToPi.equals(alsoMapToPi));
      assertFalse(mapToPi.equals(mapToE));
   }

   @org.junit.Test
   public static void testNulls() {
      assertTrue(firstEntryNull.equals(firstEntryNull));
      assertFalse(firstEntryNull.equals(nonNull));

      assertTrue(secondEntryNull.equals(secondEntryNull));
      assertFalse(secondEntryNull.equals(nonNull));

      assertTrue(firstEntryNull.toString().equals("[null, 144]"));
      assertTrue(secondEntryNull.toString().equals("[144, null]"));
      assertTrue(nonNull.toString().equals("[15, 144]"));
   }

   @org.junit.Test
   public static void testSetters() {
      Pair<Integer, Integer> newPair = new Pair<Integer, Integer>(0, 0);
      newPair.setFirst(a);
      newPair.setSecond(b);
      assertTrue(a.equals(newPair.getFirst()));
      assertTrue(b.equals(newPair.getSecond()));
      assertFalse(b.equals(newPair.getFirst()));
      assertFalse(a.equals(newPair.getSecond()));
      assertTrue(newPair.set(a, b).equals(newPair));
   }

   @org.junit.Test
   public static void testHashCorrectness() {
      HashMap<Pair<Integer, Integer>, Double> hash = new HashMap<Pair<Integer, Integer>, Double>();
      hash.put(mapToPi, Math.PI);
      hash.put(mapToE, Math.E);
      assertTrue(hash.get(mapToPi).equals(Math.PI));
      assertTrue(hash.get(mapToE).equals(Math.E));
      assertTrue(hash.get(alsoMapToPi).equals(Math.PI));
      assertTrue(hash.get(alsoMapToE).equals(Math.E));
      assertFalse(hash.get(mapToPi).equals(Math.E));
      assertFalse(hash.get(mapToE).equals(Math.PI));
      assertFalse(mapToPi.equals(mapToE));
      assertTrue(mapToPi.equals(mapToPi));
   }
}
