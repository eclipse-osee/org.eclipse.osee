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

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonGenerator;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Map;
import javax.script.ScriptContext;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.orcs.core.ds.CharacterDataProxy;

/**
 * @author David W. Miller
 */
public class JsonOutputMath {
   private String attribute = null;
   private boolean doSum;
   private boolean doCount;
   private boolean doAverage;
   private boolean doMax;
   private boolean doMin;
   private LinkedList<Double> attributeValues;
   private static String OUTPUT_ATTR = "calc.attribute";
   private static String OUTPUT_SUM = "calc.sum";
   private static String OUTPUT_COUNT = "calc.count";
   private static String OUTPUT_AVG = "calc.avg";
   private static String OUTPUT_MAX = "calc.max";
   private static String OUTPUT_MIN = "calc.min";

   public void initialize(ScriptContext context) {
      Object attr = context.getAttribute(OUTPUT_ATTR);
      if (attr != null) {
         attribute = String.valueOf(attr);
      }
      Object sum = context.getAttribute(OUTPUT_SUM);
      doSum = Boolean.parseBoolean(String.valueOf(sum));
      Object count = context.getAttribute(OUTPUT_COUNT);
      doCount = Boolean.parseBoolean(String.valueOf(count));
      Object average = context.getAttribute(OUTPUT_AVG);
      doAverage = Boolean.parseBoolean(String.valueOf(average));
      Object max = context.getAttribute(OUTPUT_MAX);
      doMax = Boolean.parseBoolean(String.valueOf(max));
      Object min = context.getAttribute(OUTPUT_MIN);
      doMin = Boolean.parseBoolean(String.valueOf(min));
      attributeValues = new LinkedList<>();
   }

   public boolean isUsed() {
      boolean toReturn = false;
      if (attribute != null && (doSum || doCount || doAverage || doMax || doMin)) {
         toReturn = true;
      }
      return toReturn;
   }

   public void add(Map<String, Object> data) {
      Object attr = data.get("attributes");
      if (attr != null && attr instanceof Map<?, ?>) {
         Object value = ((Map<?, ?>) attr).get(attribute);
         if (value != null && value instanceof Map<?, ?>) {
            for (Object item : ((Map<?, ?>) value).values()) {
               if (item instanceof CharacterDataProxy) {
                  String element = ((CharacterDataProxy) item).getValueAsString();
                  if (Strings.isNumeric(element)) {
                     attributeValues.add(Double.valueOf(element));
                  }
               }
            }
         }
      }
   }

   public void write(JsonGenerator writer) throws JsonGenerationException, IOException {
      if (!attributeValues.isEmpty()) {
         writer.writeStartObject();
         if (attribute != null) {
            writer.writeObjectFieldStart("calculations");
            writer.writeFieldName("attribute");
            writer.writeString(attribute);
            if (doSum) {
               writer.writeFieldName("sum");
               writer.writeNumber(getSum());
            }
            if (doCount) {
               writer.writeFieldName("count");
               writer.writeNumber(attributeValues.size());
            }
            if (doAverage) {
               writer.writeFieldName("average");
               writer.writeNumber(getAverage());
            }
            if (doMax) {
               writer.writeFieldName("max");
               writer.writeNumber(getMax());
            }
            if (doMin) {
               writer.writeFieldName("min");
               writer.writeNumber(getMin());
            }
            writer.writeEndObject();
         }
         writer.writeEndObject();
      }
   }

   public double getSum() {
      double toReturn = 0;
      if (!attributeValues.isEmpty()) {
         for (double value : attributeValues) {
            toReturn += value;
         }
      }
      return toReturn;
   }

   public double getAverage() {
      double toReturn = 0;
      if (attributeValues.size() > 0) {
         toReturn = getSum() / attributeValues.size();
      } else {
         toReturn = Double.NaN;
      }
      return toReturn;
   }

   public double getMax() {
      double toReturn = Double.NEGATIVE_INFINITY;
      if (!attributeValues.isEmpty()) {
         for (double value : attributeValues) {
            if (value > toReturn) {
               toReturn = value;
            }
         }
      }
      return toReturn;
   }

   public double getMin() {
      double toReturn = Double.POSITIVE_INFINITY;
      if (!attributeValues.isEmpty()) {
         for (double value : attributeValues) {
            if (value < toReturn) {
               toReturn = value;
            }
         }
      }
      return toReturn;
   }
}
