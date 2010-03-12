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

import java.io.File;
import java.io.IOException;
import java.nio.CharBuffer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.osee.framework.jdk.core.text.Range;
import org.eclipse.osee.framework.jdk.core.text.change.ChangeSet;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.io.MatchFilter;

/**
 * @author Ryan D. Brooks
 */
public class ReplaceRcsLog {
   private static final Pattern logStartP = Pattern.compile("\\$Log.*\\s+");
   private static final Pattern revP = Pattern.compile("Revision \\d+\\.\\d+.*");
   private static final Pattern revEndP = Pattern.compile("\n[* \t/\\\\]*\n");

   public static void main(String[] args) {
      if (args.length < 3) {
         System.out.println("Usage: java text.ReplaceRcsLog <dir for history> <dir for content> <result dir>");
         return;
      }

      ReplaceRcsLog app = new ReplaceRcsLog();
      app.replaceRcsLogs(new File(args[0]), new File(args[1]), new File(args[2]));
   }

   /**
    * @param directoryA directory of test files will have their rcs log extracted
    * @param directoryB directory of test files that will have their content preserved and log replaced
    * @param resultDir
    */
   public void replaceRcsLogs(File directoryA, File directoryB, File resultDir) {
      File[] files = directoryA.listFiles(new MatchFilter(".*\\.(c|mac|h)"));

      for (int i = 0; i < files.length; i++) {
         File fileB = new File(directoryB, files[i].getName());
         if (fileB.exists()) {
            replaceRcsLog(files[i], fileB, new File(resultDir, files[i].getName()));
         }
      }
   }

   public void replaceRcsLog(File historyFile, File contentFile, File resultFile) {

      CharBuffer historySeq;
      CharBuffer contentSeq;
      try {
         historySeq = Lib.fileToCharBuffer(historyFile);
         Range historyRange = findRcsLog(historySeq);

         contentSeq = Lib.fileToCharBuffer(contentFile);
         Range contentRange = findRcsLog(contentSeq);

         ChangeSet changeSet = new ChangeSet(contentSeq);
         changeSet.replace(contentRange.start, contentRange.end, historySeq.array(), historyRange.start,
               historyRange.length);
         changeSet.applyChanges(resultFile);
      } catch (IOException ex) {
         ex.printStackTrace();
         return;
      } catch (IllegalArgumentException ex) {
         System.out.println(historyFile + ": " + ex.getMessage());
         return;
      }
   }

   public static Range findRcsLog(CharSequence seq) throws IllegalArgumentException {
      Matcher logStartM = logStartP.matcher(seq);
      if (!logStartM.find()) {
         throw new IllegalArgumentException(" is missing $Log");
      }
      int firstRevStartIndex = logStartM.end(); // first char of actual revision info

      Matcher revM = revP.matcher(seq);
      int lastRevStartIndex = 0;
      while (revM.find()) {
         lastRevStartIndex = revM.start();
      } // this loop with cause the last revsion to be found (usually 1.1)

      Matcher revEndM = revEndP.matcher(seq);
      int lastRevEndIndex = 0;
      if (!revEndM.find(lastRevStartIndex)) {
         throw new IllegalArgumentException(" didn't find end of revision info");
      }
      lastRevEndIndex = revEndM.start() + 1; // include the last new line

      return new Range(firstRevStartIndex, lastRevEndIndex);
   }
}