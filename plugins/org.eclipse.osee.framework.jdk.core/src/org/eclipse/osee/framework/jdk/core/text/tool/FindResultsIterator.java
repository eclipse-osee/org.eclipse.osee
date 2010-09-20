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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * @author Ryan D. Brooks
 */
public class FindResultsIterator {
   private Iterator<Entry<String, HashMap<File, List<String>>>> patternIterator;
   private Iterator<Entry<File, List<String>>> fileIterator;
   private Iterator<String> listIterator;
   private boolean more;
   public String currentPattern;
   public File currentFile;
   public String currentRegion;
   private final HashMap<String, HashMap<File, List<String>>> results;

   public FindResultsIterator(HashMap<String, HashMap<File, List<String>>> results) {
      this.results = results;
      reset();
   }

   public void reset() {
      this.more = true;
      this.patternIterator = results.entrySet().iterator();
      this.listIterator = null;
      this.fileIterator = null;
   }

   // assumption every the list and file itorator's will have at least one item
   private void primePump() {
      if (listIterator == null || !listIterator.hasNext()) {
         if (fileIterator == null || !fileIterator.hasNext()) {
            if (!patternIterator.hasNext()) {
               more = false;
               return;
            }
            Map.Entry<String, HashMap<File, List<String>>> entry = patternIterator.next();
            currentPattern = entry.getKey();
            HashMap<File, List<String>> fileMatches = entry.getValue();
            fileIterator = fileMatches.entrySet().iterator();
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