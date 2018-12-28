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

import java.util.HashMap;
import org.junit.Assert;

/**
 * @author Roberto E. Escobar
 */
public class TripletTest {
   private final Integer a = 144;
   private final Integer b = 233;
   private final Integer c = 533;
   private final Triplet<Integer, Integer, Integer> mapToPi = new Triplet<>(a, b, c);
   private final Triplet<Integer, Integer, Integer> mapToE = new Triplet<>(b, c, a);

   private final Triplet<Integer, Integer, Integer> alsoMapToPi = new Triplet<>(a, b, c);
   private final Triplet<Integer, Integer, Integer> alsoMapToE = new Triplet<>(b, c, a);

   private final Triplet<Integer, Integer, Integer> firstEntryNull = new Triplet<>(null, 222, 333);
   private final Triplet<Integer, Integer, Integer> secondEntryNull = new Triplet<>(111, null, 333);
   private final Triplet<Integer, Integer, Integer> thirdEntryNull = new Triplet<>(111, 222, null);
   private final Triplet<Integer, Integer, Integer> nonNull = new Triplet<>(111, 222, 333);

   @org.junit.Test
   public void testConstructor() {
      Assert.assertTrue(a.equals(mapToPi.getFirst()));
      Assert.assertTrue(b.equals(mapToPi.getSecond()));
      Assert.assertTrue(c.equals(mapToPi.getThird()));

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

      Assert.assertTrue(thirdEntryNull.equals(thirdEntryNull));
      Assert.assertFalse(thirdEntryNull.equals(nonNull));

      Assert.assertTrue(firstEntryNull.toString().equals("[null, 222, 333]"));
      Assert.assertTrue(secondEntryNull.toString().equals("[111, null, 333]"));
      Assert.assertTrue(thirdEntryNull.toString().equals("[111, 222, null]"));
      Assert.assertTrue(nonNull.toString().equals("[111, 222, 333]"));
   }

   @org.junit.Test
   public void testSetters() {
      Triplet<Integer, Integer, Integer> newPair = new Triplet<>(0, 0, 0);
      newPair.setFirst(a);
      newPair.setSecond(b);
      newPair.setThird(c);

      Assert.assertTrue(a.equals(newPair.getFirst()));
      Assert.assertTrue(b.equals(newPair.getSecond()));
      Assert.assertTrue(c.equals(newPair.getThird()));

      Assert.assertFalse(a.equals(newPair.getSecond()));
      Assert.assertFalse(a.equals(newPair.getThird()));

      Assert.assertFalse(b.equals(newPair.getFirst()));
      Assert.assertFalse(b.equals(newPair.getThird()));

      Assert.assertFalse(c.equals(newPair.getFirst()));
      Assert.assertFalse(c.equals(newPair.getSecond()));

      Triplet<Integer, Integer, Integer> anotherPair = new Triplet<>(0, 0, 0);
      Assert.assertTrue(anotherPair.set(a, b, c).equals(anotherPair));
   }

   @org.junit.Test
   public void testHashCorrectness() {
      HashMap<Triplet<Integer, Integer, Integer>, Double> hash =
         new HashMap<>();
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
