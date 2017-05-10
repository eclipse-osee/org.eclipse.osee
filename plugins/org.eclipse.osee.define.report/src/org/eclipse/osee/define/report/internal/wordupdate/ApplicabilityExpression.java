/*******************************************************************************
 * Copyright (c) 2016 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/

package org.eclipse.osee.define.report.internal.wordupdate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import org.codehaus.jackson.map.ObjectMapper;
import org.eclipse.osee.framework.core.data.FeatureDefinitionData;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.util.WordCoreUtil;
import org.eclipse.osee.framework.jdk.core.type.HashCollection;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.orcs.data.ArtifactReadable;

/**
 * @author Morgan E. Cook
 */
public class ApplicabilityExpression {

   private String content;
   private String elseContent;

   private String configuration;
   private HashCollection<String, String> featureValuesAllowed;

   private static ScriptEngineManager sem = new ScriptEngineManager();
   private static ScriptEngine se = sem.getEngineByName("JavaScript");

   public ApplicabilityExpression(String configuration, HashCollection<String, String> featureValuesAllowed) {
      this.configuration = configuration;
      this.featureValuesAllowed = featureValuesAllowed;
   }

   public String getValidConfigurationContent(String text, ArrayList<String> configurations) {
      parseContent(text, false);

      String toReturn = elseContent;

      for (String config : configurations) {
         if (configuration.equals(config)) {
            toReturn = content;
         }
      }

      return toReturn;
   }

   public String getValidFeatureContent(String text, HashMap<String, List<String>> featureIdValuesMap, ArrayList<String> featureOperators, ArtifactReadable featureDefArt) {
      parseContent(text, true);

      String toReturn = null;

      String expression = createFeatureExpression(featureIdValuesMap, featureOperators, featureDefArt);

      boolean result = false;
      try {
         result = (boolean) se.eval(expression);
      } catch (ScriptException ex) {
         throw new OseeCoreException("Failed to parse expression: " + expression);
      }

      if (result) {
         toReturn = content;
      } else {
         toReturn = elseContent;
      }

      return toReturn;
   }

   private void parseContent(String text, boolean isFeature) {
      content = text;
      elseContent = null;

      Matcher match = WordCoreUtil.ELSE_PATTERN.matcher(text);

      if (match.find()) {
         content = text.substring(0, match.start());

         elseContent = text.substring(match.end());
         if (isFeature) {
            elseContent = elseContent.replaceAll(WordCoreUtil.ENDFEATURE, "");
            elseContent = elseContent.replaceAll(WordCoreUtil.BEGINFEATURE, "");
         } else {
            elseContent = elseContent.replaceAll(WordCoreUtil.ENDCONFIG, "");
            elseContent = elseContent.replaceAll(WordCoreUtil.BEGINCONFIG, "");
         }

      }
      if (isFeature) {
         content = content.replaceAll(WordCoreUtil.ENDFEATURE, "");
         content = content.replaceAll(WordCoreUtil.BEGINFEATURE, "");
      } else {
         content = content.replaceAll(WordCoreUtil.ENDCONFIG, "");
         content = content.replaceAll(WordCoreUtil.BEGINCONFIG, "");
      }
   }

   private String createFeatureExpression(HashMap<String, List<String>> featureIdValuesMap, ArrayList<String> featureOperators, ArtifactReadable featureDefArt) {
      String myFeatureExpression = "";
      Iterator<String> iterator = featureOperators.iterator();

      for (String feature : featureIdValuesMap.keySet()) {
         List<String> values = featureIdValuesMap.get(feature);

         String valueExpression = createValueExpression(feature, values, featureDefArt);

         boolean result = false;

         try {
            result = (boolean) se.eval(valueExpression);
         } catch (ScriptException ex) {
            throw new OseeCoreException("Failed to parse expression: " + valueExpression);
         }

         myFeatureExpression += result + " ";

         if (iterator.hasNext()) {
            String next = iterator.next();
            if (next.equals("|")) {
               myFeatureExpression += "|| ";
            } else if (next.equals("&")) {
               myFeatureExpression += "&& ";
            }
         }
      }

      return myFeatureExpression;
   }

   private String createValueExpression(String feature, List<String> values, ArtifactReadable featureDefArt) {
      String myValueExpression = "";
      for (String value : values) {
         if (value.equals("(")) {
            myValueExpression += "( ";
         } else if (value.equals(")")) {
            myValueExpression += ") ";
         } else if (value.equals("|")) {
            myValueExpression += "|| ";
         } else if (value.equals("&")) {
            myValueExpression += "&& ";
         } else {
            boolean eval = isFeatureValuePairValid(feature, value, featureDefArt);
            myValueExpression += eval + " ";
         }
      }

      return myValueExpression;
   }

   private boolean isFeatureValuePairValid(String feature, String value, ArtifactReadable featureDefArt) {
      if (featureValuesAllowed.containsKey(feature.toUpperCase())) {
         Collection<String> validValues = featureValuesAllowed.getValues(feature.toUpperCase());

         value = value.equalsIgnoreCase("Default") ? getDefaultValue(feature, featureDefArt) : value;

         if (containsIgnoreCase(validValues, value)) {
            return true;
         }
      }

      return false;
   }

   private static boolean containsIgnoreCase(Collection<String> validValues, String val) {
      for (String validValue : validValues) {
         if (validValue.equalsIgnoreCase(val)) {
            return true;
         }
      }
      return false;
   }

   private String getDefaultValue(String feature, ArtifactReadable featureDefArt) {
      String toReturn = null;
      try {
         String json = featureDefArt.getSoleAttributeAsString(CoreAttributeTypes.GeneralStringData);

         ObjectMapper mapper = new ObjectMapper();
         FeatureDefinitionData[] featDataList = mapper.readValue(json, FeatureDefinitionData[].class);

         for (FeatureDefinitionData featData : featDataList) {
            if (featData.getName().equalsIgnoreCase(feature)) {
               toReturn = featData.getDefaultValue();
               break;
            }
         }
      } catch (Exception e) {
         throw new OseeCoreException("Error getting default value for feature: " + feature);
      }

      return toReturn;
   }
}
