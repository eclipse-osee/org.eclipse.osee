/*
 * Created on Feb 10, 2011
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.ui.skynet.render.compare;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class CompareData {

   private final Map<String, String> dataToCompare = new LinkedHashMap<String, String>();
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
}
