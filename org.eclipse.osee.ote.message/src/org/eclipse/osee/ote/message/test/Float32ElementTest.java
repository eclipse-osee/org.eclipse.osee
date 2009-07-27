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
package org.eclipse.osee.ote.message.test;

import java.util.Random;
import junit.framework.Assert;
import junit.framework.TestCase;
import org.eclipse.osee.ote.message.data.HeaderData;
import org.eclipse.osee.ote.message.data.MemoryResource;
import org.eclipse.osee.ote.message.elements.Float32Element;

public class Float32ElementTest extends TestCase {

   public void testFloat32() {
      final HeaderData hd = new HeaderData("test_data", new MemoryResource(new byte[64], 0, 64));
      Float32Element[] e = new Float32Element[8];
      for (int i = 0; i < e.length; i++) {
         e[i] = new Float32Element(null, "Float32@" + i, hd, i * 4, 0, 31);
      }
      Random r = new Random(System.currentTimeMillis());
      float[] vals = new float[e.length];
      for (int i = 0; i < 1000; i++) {
         generateAscending(r, e, vals, e.length);
         check(e, vals, e.length);

         generateDescending(r, e, vals, e.length);
         check(e, vals, e.length);

         // zeroize test
         for (int z = 0; z < e.length; z += 2) {
            e[z].zeroize();
            vals[z] = 0;
         }

         check(e, vals, e.length);

      }
      Double x = new Double(r.nextFloat());
      e[0].setValue(x);
      Assert.assertTrue(e[0].getValue().equals(x));
   }

   private void generateAscending(Random r, Float32Element[] e, float[] expectedVals, int length) {
      for (int i = 0; i < length; i++) {
         float val = r.nextFloat();
         Float32Element el = e[i];
         el.setValue(val);
         expectedVals[i] = val;
         if (el.getValue() != expectedVals[i]) {
            failNotEquals(String.format("set/get fail on %s: msb=%d, lsb=%d", el.getName(), el.getMsb(), el.getLsb()),
                  expectedVals[i], el.getValue());
         }
      }
   }

   private void generateDescending(Random r, Float32Element[] e, float[] expectedVals, int length) {
      for (int i = length - 1; i >= 0; i--) {
         float val = r.nextFloat();
         Float32Element el = e[i];
         el.setValue(val);
         expectedVals[i] = val;
         if (el.getValue() != expectedVals[i]) {
            failNotEquals(String.format("set/get fail on %s: msb=%d, lsb=%d", el.getName(), el.getMsb(), el.getLsb()),
                  Double.toHexString(expectedVals[i]), Double.toHexString(el.getValue()));
         }
      }
   }

   private void check(Float32Element[] e, float[] expectedVals, int length) {
      for (int i = 0; i < length; i++) {
         Float32Element el = e[i];
         Assert.assertTrue(String.format("corruption detect on %s: msb=%d, lsb=%d", el.getName(), el.getMsb(),
               el.getLsb()), expectedVals[i] == el.getValue());
      }
   }
}
