/*********************************************************************
 * Copyright (c) 2010 Boeing
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

package org.eclipse.osee.framework.core.exception;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.osee.framework.jdk.core.text.tool.Find;
import org.eclipse.osee.framework.jdk.core.text.tool.FindResultsIterator;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.junit.Assert;
import org.junit.Test;

/**
 * NOT_FOR_SUITE: This test is a long running test that should not be in normal test suites
 * 
 * @author Ryan D. Brooks
 */
public class TestStringFormatMessages {
   private static final String formatSuffix = "\\s*\\(\".*?%.*?\"(?<!\\\\\")[^;]+;";
   private static final String throwPattern = "throw new\\s*\\w+" + formatSuffix;
   private static final String formatPattern = "String.format" + formatSuffix;
   private static final Pattern outerPattern = Pattern.compile("\"(.*?)\"(?<!\\\\\")([^;]+);");
   private static final Pattern specifierPattern = Pattern.compile("%-?[\\d.]*(\\w)");

   private FindResultsIterator getResultsIterator() {
      List<String> patterns = new ArrayList<>(2);
      patterns.add(throwPattern);
      patterns.add(formatPattern);

      File rootSrcDir = new File("c:/UserData/git");
      List<File> files = Lib.recursivelyListFiles(rootSrcDir, Pattern.compile(".+\\.java"));
      Find app = new Find(patterns, files);
      app.find(999999, true);
      return app.getResults().iterator();
   }

   @Test
   public void testFormatMessages() {
      FindResultsIterator iterator = getResultsIterator();
      while (iterator.hasNext()) {
         Matcher matcher = outerPattern.matcher(iterator.currentRegion);
         if (matcher.find()) {
            examineArgs(matcher);
         } else {
            Assert.fail(iterator.currentRegion);
         }
      }
   }

   private void examineArgs(Matcher matcher) {
      String formatMessage = matcher.group(1);
      int argCount = countArguments(matcher.group(2));
      Object[] args = new Object[argCount];

      Matcher specifierMatcher = specifierPattern.matcher(formatMessage);
      int index = 0;
      while (specifierMatcher.find()) {
         if (index >= argCount) {
            Assert.fail(formatMessage);
         }
         String specifier = specifierMatcher.group(1);
         if (specifier.equals("d")) {
            args[index++] = 0;
         } else if (specifier.equals("f")) {
            args[index++] = 0.0f;
         } else if (specifier.equalsIgnoreCase("s")) {
            args[index++] = new Object();
         } else if (specifier.equalsIgnoreCase("x")) {
            args[index++] = Byte.valueOf((byte) 0);
         } else if (specifier.equalsIgnoreCase("b")) {
            args[index++] = true;
         } else if (specifier.equalsIgnoreCase("c")) {
            args[index++] = 'x';
         } else if (specifier.equalsIgnoreCase("t")) {
            args[index++] = new Date();
         } else {
            Assert.fail(String.format("[%s] has unknown format specifier [%s]", formatMessage, specifier));
         }
      }
      if (argCount != index) {
         Assert.assertEquals(formatMessage, index, argCount);
      }

      try {
         String.format(formatMessage, args);
      } catch (RuntimeException ex) {
         Assert.fail(ex.toString());
      }
   }

   private int countArguments(String argString) {
      int argCount = 0;
      int balance = 1;
      char[] chars = argString.toCharArray();

      for (int i = 0; i < chars.length; i++) {
         char c = chars[i];
         if (c == '(') {
            balance++;
         } else if (c == ')') {
            balance--;
         } else if (c == ',' && balance == 1) {
            argCount++;
         }
         if (balance == 0) {
            break;
         }
      }
      return argCount;
   }
}