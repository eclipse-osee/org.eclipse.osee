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
package org.eclipse.osee.framework.jdk.core.text.rules;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.osee.framework.jdk.core.text.Rule;
import org.eclipse.osee.framework.jdk.core.text.change.ChOps;
import org.eclipse.osee.framework.jdk.core.text.change.ChangeSet;
import org.eclipse.osee.framework.jdk.core.util.Lib;

/**
 * @author Ryan D. Brooks
 */
public class ReplaceAll extends Rule {
   private static Pattern backReferencePattern = Pattern.compile("\\\\(\\d)");
   private final Pattern pattern;
   private final String[] replacements;
   private final Matcher backReferenceMatcher = backReferencePattern.matcher("");

   public ReplaceAll(Pattern pattern, String replacement) {
      this(pattern, new String[] {replacement});
   }

   public ReplaceAll(Pattern pattern, String[] replacements) {
      super(null); // don't change extension on resulting file (i.e. overwrite the original file)
      this.pattern = pattern;
      this.replacements = new String[replacements.length];
      for (int i = 0; i < replacements.length; i++) {
         char[] chars = replacements[i].toCharArray();
         this.replacements[i] = new String(ChOps.embedNewLines(chars, 0, chars.length));
      }
   }

   public ReplaceAll(String patternStr, String[] replacements) {
      this(Pattern.compile(patternStr), replacements);
   }

   public ReplaceAll(String patternStr, String replacement) {
      this(Pattern.compile(patternStr), replacement);
   }

   @Override
   public ChangeSet computeChanges(CharSequence seq) {
      Matcher matcher = pattern.matcher(seq);

      ChangeSet changeSet = new ChangeSet(seq);

      while (matcher.find()) {
         ruleWasApplicable = true;

         int numGroups = matcher.groupCount();
         if (numGroups == 0) {
            changeSet.replace(matcher.start(), matcher.end(), replacements[0]);
         } else {
            for (int i = 0; i < numGroups; i++) {
               int start = matcher.start(i + 1);
               if (start > -1) {
                  backReferenceMatcher.reset(replacements[i]);
                  if (backReferenceMatcher.matches()) {
                     replacements[i] = matcher.group(Integer.parseInt(backReferenceMatcher.group(1)));
                  }
                  changeSet.replace(start, matcher.end(i + 1), replacements[i]);
               }
            }
         }
      }
      return changeSet;
   }

   public static void main(String[] args) {
      if (args.length < 3) {
         System.out.println("Usage: java text.rules.ReplaceAll <pattern> <replace str or file> <directory> <fileName pattern>");
         return;
      }

      try {
         Rule rule = null;
         if (new File(args[1]).exists()) {
            List<String> list = Lib.readListFromFile(args[1]);
            String[] strs = new String[list.size()];
            list.toArray(strs);
            rule = new ReplaceAll(Pattern.compile(args[0]), strs);
         } else {
            rule = new ReplaceAll(Pattern.compile(args[0]), args[1]);
         }

         rule.process(Lib.recursivelyListFiles(new File(args[2]), Pattern.compile(args[3])));
      } catch (IOException e) {
         e.printStackTrace();
      }
   }
}