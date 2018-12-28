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
public class QuadTest {
   private final Integer a = 144;
   private final Integer b = 233;
   private final Integer c = 433;
   private final Integer d = 555;
   private final Quad<Integer, Integer, Integer, Integer> mapToPi = new Quad<>(a, b, c, d);
   private final Quad<Integer, Integer, Integer, Integer> mapToE = new Quad<>(b, c, d, a);

   private final Quad<Integer, Integer, Integer, Integer> alsoMapToPi = new Quad<>(a, b, c, d);
   private final Quad<Integer, Integer, Integer, Integer> alsoMapToE = new Quad<>(b, c, d, a);

   private final Quad<Integer, Integer, Integer, Integer> firstEntryNull =
      new Quad<>(null, 222, 333, 444);
   private final Quad<Integer, Integer, Integer, Integer> secondEntryNull =
      new Quad<>(111, null, 333, 444);
   private final Quad<Integer, Integer, Integer, Integer> thirdEntryNull =
      new Quad<>(111, 222, null, 444);
   private final Quad<Integer, Integer, Integer, Integer> fourthEntryNull =
      new Quad<>(111, 222, 333, null);
   private final Quad<Integer, Integer, Integer, Integer> nonNull = new Quad<>(111, 222, 333, 444);

   @org.junit.Test
   public void testConstructor() {
      Assert.assertTrue(a.equals(mapToPi.getFirst()));
      Assert.assertTrue(b.equals(mapToPi.getSecond()));
      Assert.assertTrue(c.equals(mapToPi.getThird()));
      Assert.assertTrue(d.equals(mapToPi.getFourth()));

      Assert.assertFalse(a.equals(mapToPi.getSecond()));
      Assert.assertFalse(a.equals(mapToPi.getThird()));
      Assert.assertFalse(a.equals(mapToPi.getFourth()));

      Assert.assertFalse(b.equals(mapToPi.getFirst()));
      Assert.assertFalse(b.equals(mapToPi.getThird()));
      Assert.assertFalse(b.equals(mapToPi.getFourth()));

      Assert.assertFalse(c.equals(mapToPi.getFirst()));
      Assert.assertFalse(c.equals(mapToPi.getSecond()));
      Assert.assertFalse(c.equals(mapToPi.getFourth()));

      Assert.assertFalse(d.equals(mapToPi.getFirst()));
      Assert.assertFalse(d.equals(mapToPi.getSecond()));
      Assert.assertFalse(d.equals(mapToPi.getThird()));
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

      Assert.assertTrue(fourthEntryNull.equals(fourthEntryNull));
      Assert.assertFalse(fourthEntryNull.equals(nonNull));

      Assert.assertTrue(firstEntryNull.toString().equals("[null, 222, 333, 444]"));
      Assert.assertTrue(secondEntryNull.toString().equals("[111, null, 333, 444]"));
      Assert.assertTrue(thirdEntryNull.toString().equals("[111, 222, null, 444]"));
      Assert.assertTrue(fourthEntryNull.toString().equals("[111, 222, 333, null]"));
      Assert.assertTrue(nonNull.toString().equals("[111, 222, 333, 444]"));
   }

   @org.junit.Test
   public void testSetters() {
      Quad<Integer, Integer, Integer, Integer> newPair = new Quad<>(0, 0, 0, 0);
      newPair.setFirst(a);
      newPair.setSecond(b);
      newPair.setThird(c);
      newPair.setFourth(d);

      Assert.assertTrue(a.equals(newPair.getFirst()));
      Assert.assertTrue(b.equals(newPair.getSecond()));
      Assert.assertTrue(c.equals(newPair.getThird()));
      Assert.assertTrue(d.equals(newPair.getFourth()));

      Assert.assertFalse(a.equals(newPair.getSecond()));
      Assert.assertFalse(a.equals(newPair.getThird()));
      Assert.assertFalse(a.equals(newPair.getFourth()));

      Assert.assertFalse(b.equals(newPair.getFirst()));
      Assert.assertFalse(b.equals(newPair.getThird()));
      Assert.assertFalse(b.equals(newPair.getFourth()));

      Assert.assertFalse(c.equals(newPair.getFirst()));
      Assert.assertFalse(c.equals(newPair.getSecond()));
      Assert.assertFalse(c.equals(newPair.getFourth()));

      Assert.assertFalse(d.equals(newPair.getFirst()));
      Assert.assertFalse(d.equals(newPair.getSecond()));
      Assert.assertFalse(d.equals(newPair.getThird()));

      Quad<Integer, Integer, Integer, Integer> anotherPair = new Quad<>(0, 0, 0, 0);
      Assert.assertTrue(anotherPair.set(a, b, c, d).equals(anotherPair));
   }

   @org.junit.Test
   public void testHashCorrectness() {
      HashMap<Quad<Integer, Integer, Integer, Integer>, Double> hash =
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
