/*******************************************************************************
 * Copyright (c) 2011 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.core.model.change;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * @author Roberto E. Escobar
 */
public class CompareData {

   private final Map<String, String> dataToCompare = new LinkedHashMap<>();
   private final List<String> mergeList = new ArrayList<>();
   private final String outputPath;
   private final String generatorScriptPath;

   public CompareData(String outputPath, String generatorScriptPath) {
      this.outputPath = outputPath;
      this.generatorScriptPath = generatorScriptPath;
   }

   public String getOutputPath() {
      return outputPath;
   }

   public String getGeneratorScriptPath() {
      return generatorScriptPath;
   }

   public void add(String file1Location, String file2Location) {
      dataToCompare.put(file1Location, file2Location);
   }

   public Set<Entry<String, String>> entrySet() {
      return dataToCompare.entrySet();
   }

   public boolean isEmpty() {
      return dataToCompare.isEmpty();
   }

   public int size() {
      return dataToCompare.size();
   }

   public void clear() {
      dataToCompare.clear();
   }

   public void addMerge(String fileLocation) {
      if (fileLocation != null && fileLocation.length() > 0) {
         mergeList.add(fileLocation);
      }
   }

   public boolean isMerge(String fileLocation) {
      return mergeList.contains(fileLocation);
   }
}
