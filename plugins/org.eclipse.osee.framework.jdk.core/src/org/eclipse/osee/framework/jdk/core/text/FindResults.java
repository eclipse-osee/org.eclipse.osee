/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
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

package org.eclipse.osee.framework.jdk.core.text;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.eclipse.osee.framework.jdk.core.text.tool.FindResultsIterator;

/**
 * @author Ryan D. Brooks
 */
public class FindResults {
   private final HashMap<String, HashMap<File, List<String>>> results;

   public FindResults(int initialCapacity) {
      results = new HashMap<>(initialCapacity * 4 / 3);
   }

   public void addMatch(String pattern, File file, String region) {
      HashMap<File, List<String>> fileMatches = results.get(pattern);

      if (fileMatches == null) {
         fileMatches = new HashMap<>();
         results.put(pattern, fileMatches);
      }

      List<String> regions = fileMatches.get(file);
      if (regions == null) {
         regions = new LinkedList<>();
         fileMatches.put(file, regions);
      }
      regions.add(region);
   }

   public void writeFindResutls(Writer out) throws IOException {
      for (FindResultsIterator i = iterator(); i.hasNext();) {
         // write out the file name, pattern, and region surrounding match
         out.write(i.currentPattern);
         out.write('@');
         out.write(i.currentFile.getName());
         out.write('@');
         if (i.currentRegion != null) {
            out.write(i.currentRegion);
         }
         out.write("\n");
      }
   }

   public FindResultsIterator iterator() {
      return new FindResultsIterator(results);
   }

   /**
    * Returns a simple set of all files that had matches (or anti-matches)
    */
   public Set<File> getFileSet() {
      Set<File> files = new LinkedHashSet<>(1000);
      for (Iterator<Entry<String, HashMap<File, List<String>>>> i = results.entrySet().iterator(); i.hasNext();) {
         Map.Entry<String, HashMap<File, List<String>>> entry = i.next();
         HashMap<File, List<String>> fileMatches = entry.getValue();
         files.addAll(fileMatches.keySet());
      }
      return files;
   }

   public HashMap<String, List<File>> getRegionToFileMapping() {
      HashMap<String, List<File>> mapping = new HashMap<>(1000);
      for (FindResultsIterator i = iterator(); i.hasNext();) {
         List<File> files = mapping.get(i.currentRegion);
         if (files == null) {
            files = new LinkedList<>();
            mapping.put(i.currentRegion, files);
         }
         files.add(i.currentFile);
      }
      return mapping;
   }

   public IteratorPerPattern getIteratorForPattern(String pattern) {
      return new IteratorPerPattern(pattern);
   }

   /**
    * @author Ryan D. Brooks
    */
   public class IteratorPerPattern {
      private final String pattern;
      private Iterator<Entry<File, List<String>>> fileIterator;
      private Iterator<String> listIterator;
      private boolean more;
      public File currentFile;
      public String currentRegion;

      private IteratorPerPattern(String pattern) {
         this.pattern = pattern;
         reset();
      }

      public void reset() {
         HashMap<File, List<String>> fileMatches = results.get(pattern);
         this.fileIterator = fileMatches.entrySet().iterator();
         this.listIterator = null;
         this.more = true;
      }

      // assumption every the list and file itorator's will have at least one item
      private void primePump() {
         if (listIterator == null || !listIterator.hasNext()) {
            if (!fileIterator.hasNext()) {
               more = false;
               return;
            }
            Map.Entry<File, List<String>> entry = fileIterator.next();
            currentFile = entry.getKey();
            List<String> list = entry.getValue();
            listIterator = list.iterator();
         }
         currentRegion = listIterator.next();
      }

      public boolean hasNext() {
         primePump();
         return more;
      }
   }
}