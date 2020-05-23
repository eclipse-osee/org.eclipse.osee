/*********************************************************************
 * Copyright (c) 2011 Boeing
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
