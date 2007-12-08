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
package org.eclipse.osee.framework.ui.skynet.widgets.xviewer.customize;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.osee.framework.jdk.core.util.AXml;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.XViewerColumn;

/**
 * @author Donald G. Dunne
 */
public class SortingData {

   private static String XTREESORTER_TAG = "xSorter";
   private static String COL_NAME_TAG = "name";
   private List<String> sortingNames = new ArrayList<String>();
   private final CustomizeData custData;

   public SortingData(CustomizeData custData) {
      this.custData = custData;
   }

   public void clearSorter() {
      sortingNames.clear();
   }

   public boolean isSorting() {
      return sortingNames.size() > 0;
   }

   public String toString() {
      List<XViewerColumn> cols = getSortXCols();
      if (cols.size() == 0) return "";
      StringBuffer sb = new StringBuffer("Sort: ");
      for (XViewerColumn col : getSortXCols()) {
         if (col != null) {
            sb.append(col.getSystemName());
            sb.append(col.isSortForward() ? " (FWD) , " : " (REV) , ");
         }
      }
      return sb.toString().replaceFirst(" , $", "");
   }

   public List<XViewerColumn> getSortXCols() {
      List<XViewerColumn> cols = new ArrayList<XViewerColumn>();
      for (String name : getSortingNames())
         cols.add(custData.getColumnData().getXColumn(name));
      return cols;
   }

   public void setSortXCols(List<XViewerColumn> sortXCols) {
      sortingNames.clear();
      for (XViewerColumn xCol : sortXCols) {
         sortingNames.add(xCol.getSystemName());
      }
   }

   public String getXml() {
      StringBuffer sb = new StringBuffer("<" + XTREESORTER_TAG + ">");
      for (String item : sortingNames)
         sb.append(AXml.addTagData(COL_NAME_TAG, item));
      sb.append("</" + XTREESORTER_TAG + ">");
      return sb.toString();
   }

   public void setFromXml(String xml) {
      sortingNames.clear();
      String xmlSortStr = AXml.getTagData(xml, XTREESORTER_TAG);
      Matcher m = Pattern.compile("<" + COL_NAME_TAG + ">(.*?)</" + COL_NAME_TAG + ">").matcher(xmlSortStr);
      while (m.find()) {
         sortingNames.add(m.group(1));
      }
   }

   /**
    * @return the sortingNames
    */
   public List<String> getSortingNames() {
      return sortingNames;
   }

   public void addSortingName(String name) {
      if (!this.sortingNames.contains(name)) this.sortingNames.add(name);
   }

   /**
    * @param sortingNames the sortingNames to set
    */
   public void setSortingNames(List<String> sortingNames) {
      this.sortingNames = sortingNames;
   }

}
