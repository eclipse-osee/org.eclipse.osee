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
package org.eclipse.osee.framework.ui.skynet.util.matrix;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.jdk.core.util.AHTML;

/**
 * Creates HTML matrix
 * 
 * @author Donald G. Dunne
 */
public class Matrix {
   private String title;
   private final ArrayList<MatrixItem> items;
   private Map<String, MatrixItem> nameToItem = new HashMap<String, MatrixItem>();
   private Set<String> values = new HashSet<String>();
   private Map<String, Set<String>> nameToValues = new HashMap<String, Set<String>>();
   // Names with no values will be listed at the bottom of the report so they don't take up space
   private Set<String> noValueNames = new HashSet<String>();
   private boolean useNameAsMark = false;
   private IProgressMonitor monitor;

   public Matrix(String title, ArrayList<MatrixItem> items) {
      this.title = title;
      this.items = items;
   }

   public String getMatrix() {
      StringBuilder sb = new StringBuilder();
      sb.append(AHTML.heading(3, title));
      sb.append(getMatrixBody());
      return sb.toString();
   }

   private void processData() {
      for (MatrixItem item : items) {
         nameToItem.put(item.getName(), item);
         values.addAll(item.getValues());
         if (nameToValues.containsKey(item.getName())) {
            Set<String> vals = nameToValues.get(item.getName());
            vals.addAll(item.getValues());
            nameToValues.remove(item.getName());
            nameToValues.put(item.getName(), vals);
         } else
            nameToValues.put(item.getName(), item.getValues());
      }
   }

   private String getMatrixBody() {
      processData();
      StringBuilder sb = new StringBuilder();
      sb.append(AHTML.beginMultiColumnTable(100, 1));
      // Determine all the names to deal with
      Set<String> names = new HashSet<String>();
      // Don't want to take up valuable table space with names that have no values; keep track
      // of them and print them at the end of the report
      for (String name : nameToItem.keySet()) {
         System.out.println("nameToValues.get(name) *" + nameToValues.get(name) + "*");
         if (nameToValues.get(name) == null || nameToValues.get(name).size() == 0)
            noValueNames.add(name);
         else
            names.add(name);
      }
      // Create sortedNames for use in looping through
      String[] sortedNames = names.toArray(new String[names.size()]);
      Arrays.sort(sortedNames);
      // Create headerNames with one more field due to value name column
      names.add(" ");
      String[] headerNames = names.toArray(new String[names.size()]);
      Arrays.sort(headerNames);
      // Add header names to table
      sb.append(AHTML.addHeaderRowMultiColumnTable(headerNames));
      int x = 1;
      // Create sorted list of values
      String[] sortedValues = values.toArray(new String[values.size()]);
      Arrays.sort(sortedValues);
      for (String value : sortedValues) {
         String str = String.format("Processing %s/%s \"%s\"", x++ + "", values.size(), value);
         System.out.println(str);
         if (monitor != null) monitor.subTask(str);
         List<String> marks = new ArrayList<String>();
         marks.add(value);
         for (String name : sortedNames) {
            if (nameToValues.get(name) != null && nameToValues.get(name).contains(value))
               marks.add(useNameAsMark ? name : "X");
            else
               marks.add(".");
         }
         String[] colOptions = new String[marks.size()];
         int i = 0;
         colOptions[i] = "";
         for (i = 1; i < marks.size(); i++)
            colOptions[i] = " align=center";
         sb.append(AHTML.addRowMultiColumnTable(marks.toArray(new String[marks.size()]), colOptions));
      }
      sb.append(AHTML.endMultiColumnTable());
      if (noValueNames.size() > 0) {
         sb.append(AHTML.newline(2) + AHTML.bold("Items with no values: "));
         String[] sortedItems = noValueNames.toArray(new String[noValueNames.size()]);
         Arrays.sort(sortedItems);
         for (String str : sortedItems)
            sb.append(AHTML.newline() + str);
         sb.append(AHTML.newline());
      }
      return sb.toString();
   }

   /**
    * @return Returns the useNameAsMark.
    */
   public boolean isUseNameAsMark() {
      return useNameAsMark;
   }

   /**
    * @param useNameAsMark The useNameAsMark to set.
    */
   public void setUseNameAsMark(boolean useNameAsMark) {
      this.useNameAsMark = useNameAsMark;
   }

   public IProgressMonitor getMonitor() {
      return monitor;
   }

   public void setMonitor(IProgressMonitor monitor) {
      this.monitor = monitor;
   }

}
