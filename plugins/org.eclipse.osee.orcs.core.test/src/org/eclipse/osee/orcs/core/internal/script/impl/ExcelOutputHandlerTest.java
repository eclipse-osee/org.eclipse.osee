/*******************************************************************************
 * Copyright (c) 2015 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs.core.internal.script.impl;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;
import java.io.StringWriter;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import javax.script.ScriptContext;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.orcs.core.ds.CharacterDataProxy;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

/**
 * @author David W. Miller
 */
public class ExcelOutputHandlerTest {

   private static final String OUTPUT_SCRIPT = "output.script";
   private static final String OUTPUT_DEBUG = "output.debug";

   //@formatter:off
   private static final String[] headings = {
      "Heading 1",
      "Heading 2",
      "Heading 3",
      "Heading 4",
      "Heading 5",
      "Heading 6",
      "Heading 7",
      "Heading 8"};

   private static final String[] values = {
      "one",
      "two",
      "three",
      "four",
      "five",
      "six",
      "seven",
      "eight"};

   private static final String[] groups = {
      "group 1",
      "group 2",
      "group 3",
      "group 4",
      "group 5"};

   private static final String[] innerName = {
      "name",
      "value"};

   @Mock private ScriptContext context;
   //@formatter:on

   private Map<String, Object> top;
   private StringWriter resultBuffer;

   @Before
   public void setup() {
      MockitoAnnotations.initMocks(this);

      top = new LinkedHashMap<>();
      resultBuffer = new StringWriter();

      when(context.getAttribute(OUTPUT_DEBUG)).thenAnswer(answer("true"));
      when(context.getAttribute(OUTPUT_SCRIPT)).thenAnswer(answer("true"));
      when(context.getWriter()).thenAnswer(answer(resultBuffer));
   }

   @Test(expected = OseeArgumentException.class)
   public void testNullContext() {
      buildResultData(null);
   }

   @Test
   public void testMapData() {
      buildTestStructure(buildCharData(), true);
      buildResultData(context);
      String result = resultBuffer.toString();
      // add one for the heading row
      assertTrue(countRows(result) == groups.length + 1);
      for (int i = 0; i < values.length; i++) {
         assertTrue(result.contains(">" + values[i] + "<"));
      }
      assertTrue(result.contains("debug"));
   }

   @Test
   public void testSetData() {
      buildTestStructure(buildSetData(), false);
      buildResultData(context);
      String result = resultBuffer.toString();
      assertTrue(countCells(result) == headings.length);
      assertTrue(result.contains(innerName[1]));
      for (int i = 0; i < values.length; i++) {
         assertTrue(result.contains(values[i]));
      }
   }

   @Test
   public void testNoDebugOutput() {
      when(context.getAttribute(OUTPUT_DEBUG)).thenAnswer(answer("false"));
      buildTestStructure(buildCharData(), false);
      buildResultData(context);
      String result = resultBuffer.toString();
      // add one for the heading row
      assertFalse(result.contains("debug"));
   }

   @Test(expected = OseeCoreException.class)
   public void testBadSetData() {
      buildTestStructure(buildBadSetData(), false);
      buildResultData(context);
   }

   @SuppressWarnings("unchecked")
   @Test
   public void testOnError() {
      buildTestStructure(buildBadSetData(), false);
      ExcelOutputHandler handler = new ExcelOutputHandler(context);
      handler.onEvalStart();
      handler.onLoadStart();
      Map<String, Object> data = (Map<String, Object>) top.get("top");
      try {
         for (int i = 0; i < groups.length; i++) {
            handler.onDynamicData((Map<String, Object>) data.get(groups[i]));
         }
         handler.onLoadEnd();
      } catch (Exception ex) {
         handler.onError(ex);
      }
      handler.onEvalEnd();
      assertTrue(resultBuffer.toString().contains("OseeCoreException"));
   }

   @Test(expected = NullPointerException.class)
   public void testBadWriter() {
      when(context.getWriter()).thenAnswer(answer(null));
      buildTestStructure(buildCharData(), false);
      buildResultData(context);
   }

   private static <T> Answer<T> answer(final T object) {
      return new Answer<T>() {
         @Override
         public T answer(InvocationOnMock invocation) throws Throwable {
            return object;
         }
      };
   }

   private Object[] buildSetData() {
      Object[] data = new Object[values.length];
      for (int i = 0; i < values.length; i++) {
         Map<String, Object> element = new LinkedHashMap<>();
         element.put("data", values[i]);
         element.put(innerName[0], innerName[1]);
         Set<Map<String, Object>> dataset = new LinkedHashSet<>();
         dataset.add(element);
         data[i] = dataset;
      }
      return data;
   }

   private Object[] buildBadSetData() {
      Object[] data = new Object[values.length];
      for (int i = 0; i < values.length; i++) {
         Set<String> dataset = new LinkedHashSet<>();
         dataset.add(values[i]);
         data[i] = dataset;
      }
      return data;
   }

   private Object[] buildCharData() {
      Object[] data = new Object[values.length];
      for (int i = 0; i < values.length; i++) {
         CharacterDataProxy mockedDataProxy = Mockito.mock(CharacterDataProxy.class);
         when(mockedDataProxy.getValueAsString()).thenReturn(values[i]);
         when(mockedDataProxy.toString()).thenReturn(values[i]);
         data[i] = mockedDataProxy;
      }
      return data;
   }

   private void buildTestStructure(Object[] data, boolean deeper) {
      Map<String, Object> groupContainer = new LinkedHashMap<>();
      for (int j = groups.length - 1; j >= 0; --j) {
         Map<String, Object> inner = new LinkedHashMap<>();
         for (int i = headings.length - 1; i >= 0; --i) {
            if (i == 2 && deeper) {
               Map<String, Object> bottom = new LinkedHashMap<>();
               bottom.put(innerName[0], data[i]);
               inner.put(headings[i], bottom);
            } else {
               inner.put(headings[i], data[i]);
            }
         }
         groupContainer.put(groups[j], inner);
      }
      top.put("top", groupContainer);
   }

   @SuppressWarnings("unchecked")
   private void buildResultData(ScriptContext givenContext) {
      ExcelOutputHandler handler = new ExcelOutputHandler(givenContext);
      handler.onEvalStart();
      handler.onLoadStart();
      Map<String, Object> data = (Map<String, Object>) top.get("top");
      for (int i = 0; i < groups.length; i++) {
         handler.onDynamicData((Map<String, Object>) data.get(groups[i]));
      }
      handler.onLoadEnd();
      handler.onEvalEnd();
   }

   private int countCells(String xml) {
      // expects an excel xml file, counts cells in first row
      int ct = 0;
      String subset = xml.substring(xml.indexOf("<Row>"), xml.indexOf("</Row>"));
      int index = subset.indexOf("</Cell>", 0);
      while (index > 0) {
         ct++;
         index = subset.indexOf("</Cell>", index + 7);
      }
      return ct;
   }

   private int countRows(String xml) {
      // expects an excel xml file, counts the number of rows in the first work sheet
      int ct = 0;
      String subset = xml.substring(xml.indexOf("<Worksheet"), xml.indexOf("</Worksheet>"));
      int index = subset.indexOf("</Row>", 0);
      while (index > 0) {
         ct++;
         index = subset.indexOf("</Row>", index + 6);
      }
      return ct;
   }
}
