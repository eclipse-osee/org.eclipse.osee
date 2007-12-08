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
package org.eclipse.osee.framework.jdk.core.text.tool;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.osee.framework.jdk.core.text.FileToBufferConvert;
import org.eclipse.osee.framework.jdk.core.text.FindResults;
import org.eclipse.osee.framework.jdk.core.util.Lib;

/**
 * @author Ryan D. Brooks
 */
public class Find {
   private Collection<File> files;
   private FindResults results;
   private FileToBufferConvert converter;
   private int precedingCount;
   private int trailingCount;
   private Matcher[] matchers;
   private boolean[] matcherUsed;

   /**
    * @param patterns a Collection of strings that are regular expressions
    * @param files
    * @param converter
    */
   public Find(Collection<String> patterns, Collection<File> files, FileToBufferConvert converter) {
      this.files = files;
      this.converter = converter;
      this.precedingCount = 0;
      this.trailingCount = 0;
      this.results = new FindResults(patterns.size());

      this.matchers = new Matcher[patterns.size()];
      int i = 0;
      for (String pattern : patterns) {
         //       the empty string is never used in the search because a matcher.reset happens first
         matchers[i++] = Pattern.compile(pattern).matcher("");
      }
      this.matcherUsed = new boolean[matchers.length];
   }

   private static FileToBufferConvert simpleToBuffer = new SimpleFileToBufferConvert();

   private static ArrayList<String> toList(String pattern) {
      ArrayList<String> patterns = new ArrayList<String>();
      patterns.add(pattern);
      return patterns;
   }

   public Find(String pattern, File topLevelSearchDir, String fileNamePattern) {
      this(toList(pattern), Lib.recursivelyListFiles(topLevelSearchDir, Pattern.compile(fileNamePattern)),
            simpleToBuffer);
   }

   public static void main(String[] args) throws IOException {
      if (args.length < 7) {
         System.out.println("Usage: java text.Find <pattern-list file or pattern> <search directory> <fileName pattern> <leading pad> <trailing pad> <negative: true|false> <ignoreComments: true|false>");
         return;
      }

      ArrayList<String> patterns = null;
      if (new File(args[0]).exists()) {
         patterns = Lib.readListFromFile(args[0]);
      } else {
         patterns = new ArrayList<String>();
         patterns.add(args[0]);
      }
      File resultFile = new File("results.txt");
      BufferedWriter out = new BufferedWriter(new FileWriter(resultFile));

      List<File> files = Lib.recursivelyListFiles(new File(args[1]), Pattern.compile(args[2]));
      System.out.println("Searching " + files.size() + " files...");
      if (files.size() == 0) {
         return;
      }

      FileToBufferConvert toBuffer = null;
      if (Boolean.valueOf(args[6]).booleanValue()) {
         toBuffer = new StripBlockComments();
      } else {
         toBuffer = simpleToBuffer;
      }

      Find app = new Find(patterns, files, toBuffer);
      app.setRegionPadding(Integer.parseInt(args[3]), Integer.parseInt(args[4]));

      if (Boolean.valueOf(args[5]).booleanValue()) {
         app.findMeNot();
      } else {
         app.find(999999, true);
      }
      OutputStreamWriter stdOut = new OutputStreamWriter(System.out);
      stdOut.write(resultFile.getAbsolutePath());

      app.writeUnusedPatterns(stdOut);
      app.getResults().writeFindResutls(out);
      out.close();
   }

   public void setRegionPadding(int precedingCount, int trailingCount) {
      this.precedingCount = precedingCount;
      this.trailingCount = trailingCount;
   }

   public FindResults getResults() {
      return results;
   }

   /**
    * Search a given file for all patterns: maxIsPerPattern == true up to maxMatches of any single pattern (then search
    * the same file for the next pattern) otherwise up to a maxMatches for all patterns in a given file(then end search
    * of that file and start on the next one)
    * 
    * @param maxMatches
    * @param maxIsPerPattern
    */
   public void find(int maxMatches, boolean maxIsPerPattern) {
      for (Iterator<File> iter = files.iterator(); iter.hasNext();) { // for each file

         File file = iter.next();
         CharSequence buf = null;
         try {
            buf = converter.fileToCharSequence(file);
         } catch (Exception ex) {
            System.out.println("In File " + file + ": " + ex);
            continue;
         }

         int numMatches = 0;
         for (int i = 0; i < matchers.length; i++) {
            matchers[i].reset(buf);
            if (maxIsPerPattern) {
               numMatches = 0; // new pattern matcher so reset the match count
            }

            if (numMatches == maxMatches) { // can only be true if maxIsPerPattern is false
               break; // don't look for any more matches in this file
            }

            // find each pattern at most maxMatches per file
            while (matchers[i].find() && numMatches++ < maxMatches) {
               matcherUsed[i] = true;
               // System.out.println(files[f].getName() + " " + matchers[i].group());

               String region = null;
               if (matchers[i].groupCount() == 0) {
                  if (precedingCount == 0 && trailingCount == 0) {
                     region = matchers[i].group();
                  } else {
                     int start = matchers[i].start() - precedingCount;
                     if (start < 0) {
                        start = 0;
                     }
                     int end = matchers[i].end() + trailingCount;
                     if (end > buf.length()) {
                        end = buf.length();
                     }
                     region = buf.subSequence(start, end).toString();
                  }
               } else {
                  region = matchers[i].group(1);
               }
               results.addMatch(matchers[i].pattern().pattern(), file, region);
            }
         }
      }
   }

   /**
    * Search a given file list for all patterns: maxIsPerPattern == true up to maxMatches of any single pattern (then
    * search the same file for the next pattern) otherwise up to a maxMatches for all patterns in a given file(then end
    * search of that file and start on the next one)
    */
   public void findMeNot() {
      for (Iterator<File> iter = files.iterator(); iter.hasNext();) { // for each file

         File file = iter.next();
         CharSequence buf = null;
         try {
            buf = converter.fileToCharSequence(file);
         } catch (ParseException ex) {
            System.out.println(ex);
            continue;
         } catch (IOException ex) {
            System.out.println(ex);
            continue;
         }

         for (int i = 0; i < matchers.length; i++) {
            matchers[i].reset(buf);
            if (!matchers[i].find()) {
               matcherUsed[i] = true;
               results.addMatch(matchers[i].pattern().pattern(), file, "findMeNot");
            }
         }
      }
   }

   public void writeUnusedPatterns(Writer out) throws IOException {
      boolean titleNotYetPrinted = true;
      for (int i = 0; i < matcherUsed.length; i++) {
         if (!matcherUsed[i]) {
            if (titleNotYetPrinted) {
               out.write("Patterns that were never used\n");
               titleNotYetPrinted = false;
            }
            out.write(matchers[i].pattern().pattern());
            out.write('\n');
         }
      }
      out.flush();
   }
}