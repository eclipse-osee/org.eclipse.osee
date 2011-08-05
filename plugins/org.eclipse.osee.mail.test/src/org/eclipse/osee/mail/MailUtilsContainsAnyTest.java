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
package org.eclipse.osee.mail;

import java.util.ArrayList;
import java.util.Collection;
import org.junit.Assert;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

/**
 * Test unit for {@link MailMessageFactory}.
 * 
 * @author Shawn F. cook
 */
@RunWith(Parameterized.class)
public class MailUtilsContainsAnyTest {
   private final String testStr;
   private final char[] chars;
   private final int indexOfFirstInstance;//index of the first instance of any of chars in testStr.

   public MailUtilsContainsAnyTest(String testStr, char[] chars, int indexOfFirstInstance) {
      this.testStr = testStr;
      this.chars = chars;
      this.indexOfFirstInstance = indexOfFirstInstance;
   }

   @org.junit.Test
   public void testContainsAny() {
      int ret = MailUtils.containsAny(testStr, chars);
      Assert.assertEquals(indexOfFirstInstance, ret);
   }

   @Parameters
   public static Collection<Object[]> getData() {
      Collection<Object[]> data = new ArrayList<Object[]>();

      data.add(new Object[] {"xxxxx",//testStr
         new char[] {'x'},//chars
         0,//indexOfFirstInstance
      });

      data.add(new Object[] {"xxxxx",//testStr
         new char[] {'a'},//chars
         -1,//indexOfFirstInstance
      });

      data.add(new Object[] {"XABcxx",//testStr
         new char[] {'a', 'b', 'c'},//chars
         3,//indexOfFirstInstance
      });

      data.add(new Object[] {"XABcx%x",//testStr
         new char[] {'z', '$', '\u0025'},//chars
         5,//indexOfFirstInstance
      });

      return data;
   }//getData
}
