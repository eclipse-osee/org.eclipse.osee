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
package org.eclipse.osee.framework.search.engine.test.utility;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.eclipse.osee.framework.search.engine.test.mocks.EngineAsserts;
import org.eclipse.osee.framework.search.engine.test.mocks.TagCollector;
import org.eclipse.osee.framework.search.engine.utility.TagEncoder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

/**
 * Test Case for {@link TagEncoder}
 * 
 * @author Roberto E. Escobar
 */
@RunWith(Parameterized.class)
public class TagEncoderTest {

   private final List<Pair<String, Long>> expected;
   private final String toEncode;

   public TagEncoderTest(String toEncode, List<Pair<String, Long>> expected) {
      this.toEncode = toEncode;
      this.expected = expected;
   }

   @Test
   public void testTagEncoder() {
      List<Pair<String, Long>> actualTags = new ArrayList<Pair<String, Long>>();
      TagEncoder.encode(toEncode, new TagCollector(actualTags));
      EngineAsserts.assertTagsEqual(expected, actualTags);
   }

   @Parameters
   public static Collection<Object[]> data() {
      List<Object[]> data = new ArrayList<Object[]>();
      data.add(new Object[] {"hello", EngineAsserts.asTags("hello", 1520625)});
      data.add(new Object[] {
         "what happens when we have a long string",
         EngineAsserts.asTags("what happens when we have a long string", 2080358399, -545259521, 290692031)});
      return data;
   }

}
