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

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;

/**
 * @author David W. Miller
 * @author Ryan D. Brooks
 */
public class TraceMatch {
   private Matcher primaryMatcher = null;
   private Matcher secondaryMatcher = null;

   public TraceMatch(String primaryRegex, String secondaryRegex) {
      if (primaryRegex == null || primaryRegex.isEmpty()) {
         throw new OseeArgumentException("invalid Primary Regular Expression");
      }

      primaryMatcher = Pattern.compile(primaryRegex).matcher("");

      if (secondaryRegex != null && !secondaryRegex.isEmpty()) {
         secondaryMatcher = Pattern.compile(secondaryRegex).matcher("");
      }
   }

   public int processLine(String inProcessing, TraceAccumulator accumulator) {
      int count = 0;

      primaryMatcher.reset(inProcessing);
      while (primaryMatcher.find()) {
         String primaryMatch = getMatchResult(primaryMatcher);

         if (secondaryMatcher == null) {
            accumulator.addValidTrace(primaryMatch);
            count++;
         } else {
            int numFound = processSecondary(primaryMatch, accumulator);
            if (numFound == 0) {
               accumulator.addInvalidTrace(primaryMatch);
            }
            count += numFound;
         }
      }
      return count;
   }

   private String getMatchResult(Matcher matcher) {
      String match;
      int groupCount = matcher.groupCount();
      if (groupCount == 0) {
         match = matcher.group(0);
      } else if (groupCount == 1) {
         match = matcher.group(1);
      } else {
         StringBuilder strB = new StringBuilder();
         for (int i = 1; i <= groupCount; i++) {
            String subMatch = matcher.group(i);
            if (subMatch != null) {
               strB.append(subMatch);
            }
         }
         match = strB.toString();
      }
      return match;
   }

   private int processSecondary(String primaryMatch, TraceAccumulator accumulator) {
      int count = 0;
      secondaryMatcher.reset(primaryMatch);
      while (secondaryMatcher.find()) {
         String secondaryMatch = getMatchResult(secondaryMatcher);
         accumulator.addValidTrace(secondaryMatch);
         count++;
      }
      return count;
   }

   @Override
   public String toString() {
      String primary = primaryMatcher.pattern().toString();
      String secondary = secondaryMatcher == null ? "" : secondaryMatcher.pattern().toString();
      return String.format("primary=[%s]  secondary=[%s]", primary, secondary);
   }
}