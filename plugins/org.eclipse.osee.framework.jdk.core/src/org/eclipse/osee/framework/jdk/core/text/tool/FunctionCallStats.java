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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import org.eclipse.osee.framework.jdk.core.text.FindResults;
import org.eclipse.osee.framework.jdk.core.util.Lib;

/**
 * @author Ryan D. Brooks
 */
public class FunctionCallStats {
   @SuppressWarnings("unchecked")
   public static void main(String[] args) throws IOException {
      if (args.length < 2) {
         System.out.println("Usage: FunctionCallStats <search directory> <fileName pattern>");
         return;
      }

      ArrayList<String> patterns = new ArrayList<String>();
      patterns.add("\\W(\\w+)\\s*\\([^;{]*?\\)\\s*;");

      BufferedWriter out = new BufferedWriter(new FileWriter("results.csv"));

      List files = Lib.recursivelyListFiles(new File(args[0]), Pattern.compile(args[1]));
      System.out.println("Searching " + files.size() + " files...");

      FindNonLocalFunctionCalls nonLocalFindApp =
            new FindNonLocalFunctionCalls((File[]) files.toArray(new File[files.size()]));
      nonLocalFindApp.searchFiles();
      Set nonLocalFunctions = nonLocalFindApp.getResultSet();

      Find app = new Find(patterns, files, new StripBlockComments());
      app.setRegionPadding(0, 0);
      app.find(999999, true);
      FindResults results = app.getResults();

      class Counter {
         public int count = 0;
      }

      String lastFileName = null;
      HashMap functions = new HashMap(1000);
      for (FindResults.FindResultsIterator i = results.iterator(); i.hasNext();) {
         String currentFileName = i.currentFile.getName();

         if (i.currentRegion != null) {
            if (!currentFileName.equals(lastFileName)) {
               for (Iterator functionsIterator = functions.entrySet().iterator(); functionsIterator.hasNext();) {
                  Map.Entry entry = (Map.Entry) functionsIterator.next();
                  String functionName = (String) entry.getKey();
                  if (nonLocalFunctions.contains(functionName)) {
                     out.write(lastFileName);
                     out.write(',');
                     out.write(functionName);
                     out.write(',');
                     out.write(String.valueOf(((Counter) entry.getValue()).count));
                     out.write('\n');
                  }
               }
               lastFileName = currentFileName;
               functions.clear();
            }

            Counter counter = (Counter) functions.get(i.currentRegion);
            if (counter == null) {
               counter = new Counter();
            }
            counter.count++;
            functions.put(i.currentRegion, counter);
         }
      }
      out.close();
   }
}