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

import junit.framework.TestCase;

import org.eclipse.osee.ote.message.data.HeaderData;
import org.eclipse.osee.ote.message.data.MemoryResource;
import org.eclipse.osee.ote.message.elements.SignedInteger16Element;

public class SignedInteger16ElementTest extends TestCase{


   public void test1() {
      final HeaderData hd1 = new HeaderData("test_data 1", new MemoryResource(new byte[64], 0, 64));
      final HeaderData hd2 = new HeaderData("test_data 2", new MemoryResource(new byte[64], 2, 64));
      SignedInteger16Element element1 = new SignedInteger16Element(null, "Element1", hd1, 0, 0, 15);
      SignedInteger16Element element2 = new SignedInteger16Element(null, "Element2", hd2, 0, 0, 15);

      int val1 = -1000;
      int val2 = 2000;
      element1.setValue(val1);
      check(element1, val1);
      element1.setValue(val2);
      check(element1, val2);
      
      element2.setValue(val1);
      check(element2, val1);
      element2.setValue(val2);
      check(element2, val2);
   }

   public void test2() {
      final HeaderData hd1 = new HeaderData("test_data 1", new MemoryResource(new byte[64], 0, 64));
      final HeaderData hd2 = new HeaderData("test_data 2", new MemoryResource(new byte[64], 2, 64));
      SignedInteger16Element element1 = new SignedInteger16Element(null, "Element1", hd1, 0, 16, 31);
      SignedInteger16Element element2 = new SignedInteger16Element(null, "Element2", hd2, 0, 16, 31);

      int val1 = -10000;
      int val2 = 2100;
      element1.setValue(val1);
      check(element1, val1);
      element1.setValue(val2);
      check(element1, val2);
      
      element2.setValue(val1);
      check(element2, val1);
      element2.setValue(val2);
      check(element2, val2);
   }


   private void check(SignedInteger16Element e, int expectedVals) {
      if (e.get() != expectedVals) {
         failNotEquals(
               String.format("corruption detect on %s: msb=%d, lsb=%d", e.getName(), e.getMsb(), e.getLsb()), 
               expectedVals, 
               e.get());
      }
   }
}
