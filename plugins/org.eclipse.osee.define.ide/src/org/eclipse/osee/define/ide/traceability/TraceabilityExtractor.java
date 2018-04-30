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

package org.eclipse.osee.define.ide.traceability;

import java.io.File;
import java.io.IOException;
import java.nio.CharBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * @author Roberto E. Escobar
 */
public class TraceabilityExtractor {
   private static final Pattern ofpTraceabilityPattern = Pattern.compile("\\^SRS\\s*([^;\n\r]+);");
   private static final Pattern scriptTraceabilityPattern =
      Pattern.compile("addTraceability\\s*\\(\\s*\\\"\\s*(?:SubDD|SRS|CSID)?\\s*([^\\\"]+)\\\"");
   private static final Pattern invalidTraceabilityPattern = Pattern.compile("(\\[[A-Za-z]|USES_).*");

   private static final Pattern embeddedVolumePattern = Pattern.compile("\\{\\d+ (.*)\\}[ .]*");
   private static final Pattern nonWordPattern = Pattern.compile("[^A-Z_0-9]");
   private static final Pattern structuredReqNamePattern = Pattern.compile("\\[?(\\{[^\\}]+\\})(.*)");
   private static final Pattern stripTrailingReqNamePatern = Pattern.compile("(\\}|\\])(.*)");

   private static TraceabilityExtractor instance = null;
   private final Matcher scriptReqTraceMatcher;
   private final Matcher ofpReqTraceMatcher;
   private final Matcher invalidTraceMatcher;
   private final Matcher embeddedVolumeMatcher;
   private final Matcher nonWordMatcher;
   private final Matcher structuredRequirementMatcher;
   private final Matcher stripTrailingReqNameMatcher;

   private TraceabilityExtractor() {
      this.ofpReqTraceMatcher = ofpTraceabilityPattern.matcher("");
      this.scriptReqTraceMatcher = scriptTraceabilityPattern.matcher("");
      this.invalidTraceMatcher = invalidTraceabilityPattern.matcher("");
      this.embeddedVolumeMatcher = embeddedVolumePattern.matcher("");
      this.nonWordMatcher = nonWordPattern.matcher("");
      this.structuredRequirementMatcher = structuredReqNamePattern.matcher("");
      this.stripTrailingReqNameMatcher = stripTrailingReqNamePatern.matcher("");
   }

   public static TraceabilityExtractor getInstance() {
      if (instance == null) {
         instance = new TraceabilityExtractor();
      }
      return instance;
   }

   public List<String> getTraceMarksFromFile(File sourceFile) throws IOException {
      CharBuffer buffer = Lib.fileToCharBuffer(sourceFile);
      Matcher matcher = isScriptFile(sourceFile) ? getScriptTraceMarkMatcher() : getCodeTraceMarkMatcher();
      return getTraceMarks(buffer, matcher);
   }

   private List<String> getTraceMarks(CharBuffer buffer, Matcher matcher) {
      List<String> toReturn = new ArrayList<>();
      matcher.reset(buffer);
      while (matcher.find() != false) {
         String mark = matcher.group(1);
         if (Strings.isValid(mark) != false) {
            toReturn.add(mark);
         }
      }
      return toReturn;
   }

   public boolean isValidTraceMark(String toCheck) {
      invalidTraceMatcher.reset(toCheck);
      return invalidTraceMatcher.matches() != true;
   }

   private Matcher getScriptTraceMarkMatcher() {
      return scriptReqTraceMatcher;
   }

   public Matcher getCodeTraceMarkMatcher() {
      return ofpReqTraceMatcher;
   }

   private boolean isScriptFile(File sourceFile) {
      return sourceFile.getName().endsWith("java");
   }

   public String getCanonicalRequirementName(String requirementMark) {
      String canonicalReqReference = requirementMark;
      if (Strings.isValid(requirementMark) != false) {
         canonicalReqReference = requirementMark.toUpperCase();

         embeddedVolumeMatcher.reset(canonicalReqReference);
         if (embeddedVolumeMatcher.find()) {
            canonicalReqReference = embeddedVolumeMatcher.group(1);
         }

         // Added to strip trailing artifact descriptive names } ... or ] ....
         stripTrailingReqNameMatcher.reset(canonicalReqReference);
         if (stripTrailingReqNameMatcher.find()) {
            String trail = stripTrailingReqNameMatcher.group(2);
            if (Strings.isValid(trail) && !trail.startsWith(".")) {
               canonicalReqReference = canonicalReqReference.substring(0, stripTrailingReqNameMatcher.start(1) + 1);
            }
         }

         nonWordMatcher.reset(canonicalReqReference);
         canonicalReqReference = nonWordMatcher.replaceAll("");

      }
      return canonicalReqReference;
   }

   // [{SUBSCRIBER}.ID] and example procedure {CURSOR_ACKNOWLEDGE}.NORMAL
   public Pair<String, String> getStructuredRequirement(String requirementMark) {
      Pair<String, String> toReturn = null;
      structuredRequirementMatcher.reset(requirementMark);
      if (structuredRequirementMatcher.matches() != false) {
         String primary = structuredRequirementMatcher.group(1);
         String secondary = structuredRequirementMatcher.group(2);
         if (Strings.isValid(primary) != false) {
            toReturn = new Pair<>(primary, secondary);
         }
      }
      return toReturn;
   }

}
