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
package org.eclipse.osee.framework.skynet.core.importing;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.apache.commons.lang.StringUtils;
import org.junit.Assert;
import org.junit.Test;

/**
 * @see ReqNumbering
 * @author Ryan Schmitt
 */
public class ReqNumberingTest {
   @Test
   public void testConstructor() {
      String number = "1.2.10.4";
      ReqNumbering reqNumber = new ReqNumbering(number);
      String reprocessedNumber = StringUtils.join(reqNumber.tokenize(), ".");
      assertTrue(number.equals(reprocessedNumber));
   }

   @Test
   public void testIsChild() {
      String parent = "1.3.4";
      String child = "1.3.4.6";
      ReqNumbering reqParent = new ReqNumbering(parent);
      ReqNumbering reqChild = new ReqNumbering(child);
      assertTrue(reqParent.isChild(reqChild));
   }

   @Test
   public void testIsChild_ZeroBasedParagraphNumber() {
      String parent = "1.3.4";
      String child = "1.3.4.0-6";
      ReqNumbering reqParent = new ReqNumbering(parent);
      ReqNumbering reqChild = new ReqNumbering(child);
      assertTrue(reqParent.isChild(reqChild));
   }

   @Test
   public void testIsChild_DoubleZeroBasedParagraphNumber() {
      String parent = "1.3.4";
      String child = "1.3.4.0-6.0-1";
      ReqNumbering reqParent = new ReqNumbering(parent);
      ReqNumbering reqChild = new ReqNumbering(child);
      assertTrue(reqParent.isChild(reqChild));
   }

   @Test
   public void testIsChild_DoubleDoubleZeroBasedParagraphNumber() {
      String parent = "3.7.7.1.3.0-2";
      String child = "3.7.7.1.3.0-2.0-1";
      ReqNumbering reqParent = new ReqNumbering(parent);
      ReqNumbering reqChild = new ReqNumbering(child);
      assertTrue(reqParent.isChild(reqChild));
   }

   @Test
   public void testIsNotChild_ZeroBasedParagraphNumber() {
      String parent = "1.3.4";
      String child = "1.3.5.0-5";
      ReqNumbering reqParent = new ReqNumbering(parent);
      ReqNumbering reqChild = new ReqNumbering(child);
      assertFalse(reqParent.isChild(reqChild));
   }

   @Test
   public void testIsNotChild() {
      String parent = "1.3.4";
      String child = "1.3.4.6";
      ReqNumbering reqParent = new ReqNumbering(parent);
      ReqNumbering reqChild = new ReqNumbering(child);
      assertFalse(reqChild.isChild(reqParent));
   }

   @Test
   public void test_isChild_ZeroBasedParagraphNumbers_OddCase() {
      ReqNumbering A = new ReqNumbering("1.0");
      ReqNumbering B = new ReqNumbering("1.0-1");
      assertFalse(B.isChild(A));
      assertTrue(A.isChild(B));
   }

   /**
    * <p>
    * a.compareTo(b) = x <br/>
    * b.compareTo(a) = -x
    * </p>
    */
   @Test
   public void test_compareTo_NonCommutative() {
      ReqNumbering A = new ReqNumbering("1.0", false);
      ReqNumbering B = new ReqNumbering("1.2.3");
      assertTrue(B.compareTo(A) == 1);
      assertTrue(A.compareTo(B) == -1);
   }

   /**
    * <p>
    * a.compareTo(b) = 1 <br/>
    * b.compareTo(c) = 1, then <br/>
    * a.compareTo(c) = 1
    * </p>
    */
   @Test
   public void test_compareTo_Transitive() {
      ReqNumbering A = new ReqNumbering("2.4.6");
      ReqNumbering B = new ReqNumbering("1.2.3");
      ReqNumbering C = new ReqNumbering("1.0", false);
      assertTrue(A.compareTo(B) == 1);
      assertTrue(B.compareTo(C) == 1);
      assertTrue(A.compareTo(C) == 1);
   }

   @Test
   public void testCompare() {
      List<ReqNumbering> referenceList = Arrays.asList(new ReqNumbering("1"), new ReqNumbering("1.0-1"),
         new ReqNumbering("1.3"), new ReqNumbering("1.3.1.1.1.1.1.1"), new ReqNumbering("2.4"),
         new ReqNumbering("2.4.0.1"), new ReqNumbering("3"));

      List<ReqNumbering> sampleList = Arrays.asList(new ReqNumbering("3"), new ReqNumbering("1.0-1"),
         new ReqNumbering("2.4.0-1"), new ReqNumbering("2.4"), new ReqNumbering("1.3.1.1.1.1.1.1"),
         new ReqNumbering("1.3"), new ReqNumbering("1"));

      Collections.sort(sampleList);

      Assert.assertEquals(referenceList, sampleList);
   }
}
