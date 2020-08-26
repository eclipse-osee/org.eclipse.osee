/*********************************************************************
 * Copyright (c) 2020 Boeing
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

package org.eclipse.osee.define.rest.internal.reflection;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author David W. Miller
 */
public class GenericMethodInvokerTest {
   /*
    * The purpose of this test is to test methods with types of arguments. The simple methods at the bottom of the test
    * are there to be examples of methods that take the different kinds of arguments as parameters - they are kept as
    * simple and free from dependencies as possible, while still testing different argument types.
    */
   @Test
   public void testBooleanPrimitive() {
      GenericMethodInvoker<GenericMethodInvokerTest> gm = new GenericMethodInvoker<GenericMethodInvokerTest>(this);
      List<Object> arguments = new ArrayList<>();
      arguments.add(true);
      arguments.add(false);
      gm.set("or", arguments);
      Boolean result = (Boolean) gm.invoke(this);
      Assert.assertEquals((boolean) result, true);
   }

   @Test
   public void testLongPrimitive() {
      GenericMethodInvoker<GenericMethodInvokerTest> gm = new GenericMethodInvoker<GenericMethodInvokerTest>(this);
      List<Object> arguments = new ArrayList<>();
      arguments.add(10L);
      arguments.add(15L);
      gm.set("add", arguments);
      Long result = (Long) gm.invoke(this);
      Assert.assertEquals((long) result, 25L);
   }

   @Test
   public void testLongPrimitiveArray() {
      GenericMethodInvoker<GenericMethodInvokerTest> gm = new GenericMethodInvoker<GenericMethodInvokerTest>(this);
      List<Object> arguments = new ArrayList<>();
      Long[] longArgs = {20L, 30L, 40L};
      arguments.add(longArgs);
      gm.set("sum", arguments);
      Long result = (Long) gm.invoke(this);
      Assert.assertEquals((long) result, 90L);
   }

   @Test
   public void testDoublePrimitiveArray() {
      GenericMethodInvoker<GenericMethodInvokerTest> gm = new GenericMethodInvoker<GenericMethodInvokerTest>(this);
      List<Object> arguments = new ArrayList<>();
      Double[] doubleArgs = {20.0, 30.0, 40.0};
      arguments.add(doubleArgs);
      gm.set("sum", arguments);
      Double result = (Double) gm.invoke(this);
      Assert.assertEquals(result, 90.0, .001);
   }

   @Test
   public void testCollection() {
      GenericMethodInvoker<GenericMethodInvokerTest> gm = new GenericMethodInvoker<GenericMethodInvokerTest>(this);
      List<Object> arguments = new ArrayList<>();
      Collection<String> strings = new HashSet<String>();
      strings.add("one");
      strings.add("two");
      strings.add("three");
      arguments.add(strings);
      gm.set("concatenate", arguments);
      String result = (String) gm.invoke(this);
      Assert.assertEquals(result, "one two three ");
   }

   public Boolean or(boolean a, boolean b) {
      return a || b;
   }

   public Long add(Long i, Long j) {
      return i + j;
   }

   public Long sum(Long... arg) {
      Long answer = 0L;
      for (int i = 0; i < arg.length; ++i) {
         answer += arg[i];
      }
      return answer;
   }

   public Double sum(Double[] doubles) {
      Double answer = 0.0;
      for (int i = 0; i < doubles.length; ++i) {
         answer += doubles[i];
      }
      return answer;
   }

   public String concatenate(Collection<String> strings) {
      StringBuilder sb = new StringBuilder();
      for (String s : strings) {
         sb.append(s);
         sb.append(" ");
      }
      return sb.toString();
   }
}
