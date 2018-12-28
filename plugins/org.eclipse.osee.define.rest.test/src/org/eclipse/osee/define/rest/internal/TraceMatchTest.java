/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.define.rest.internal;

import java.util.Set;
import org.eclipse.osee.framework.jdk.core.type.CaseInsensitiveString;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author David W. Miller
 */
public class TraceMatchTest {
   private final String expression = "\\^SRS\\s*([^;]+);?";
   private final String secondaryExpression = "\\[?(\\{[^\\}]+\\})(.*)";
   private final String line = "       -------- ^SRS  String_to_Represent a UI {content}; end stuff";

   @Test
   public void testConstructionPrimaryOnly() {
      String expectedResult = "primary=[" + expression + "]  secondary=[]";
      TraceMatch tm = new TraceMatch(expression, "");
      Assert.assertEquals(expectedResult, tm.toString());
   }

   @Test
   public void testConstructionBoth() {
      String expectedResult = "primary=[" + expression + "]  secondary=[" + secondaryExpression + "]";
      TraceMatch tm = new TraceMatch(expression, secondaryExpression);
      Assert.assertEquals(expectedResult, tm.toString());
   }

   @Test(expected = OseeArgumentException.class)
   public void testConstructionNull() {
      new TraceMatch(null, null);
   }

   @Test
   public void testProcessPrimaryOnly() {
      TraceMatch tm = new TraceMatch(expression, null);
      TraceAccumulator accumulator = new TraceAccumulator(".*\\.(java$)", tm);
      int count = tm.processLine(line, accumulator);
      Assert.assertEquals(1, count);
      Set<CaseInsensitiveString> items = accumulator.getTraceMarks();
      CaseInsensitiveString s = new CaseInsensitiveString("String_to_Represent a UI {content}");
      Assert.assertTrue(items.iterator().next().equals(s));
   }

   @Test
   public void testProcessBoth() {
      TraceMatch tm = new TraceMatch(expression, secondaryExpression);
      TraceAccumulator accumulator = new TraceAccumulator(".*\\.(java$)", tm);
      int count = tm.processLine(line, accumulator);
      Assert.assertEquals(1, count);
      Set<CaseInsensitiveString> items = accumulator.getTraceMarks();
      CaseInsensitiveString s = new CaseInsensitiveString("{content}");
      Assert.assertTrue(items.iterator().next().equals(s));
   }
}
