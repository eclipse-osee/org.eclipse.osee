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

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.osee.framework.jdk.core.util.AXml;

/**
 * @author Donald G. Dunne
 */
public class ColumnFilterData {

   private static final String COLUMN_FILTER_TAG = "xColFilter";
   private static final String COLUMN_ID_TAG = "id";
   private static final String FILTER_TEXT_TAG = "str";
   private final Map<String, String> colIdToFilterText = new HashMap<String, String>();
   private static Pattern p = Pattern.compile("<" + COLUMN_FILTER_TAG + ">(.*?)</" + COLUMN_FILTER_TAG + ">");

   /**
    * @return the filterText
    */
   public String getFilterText(String colId) {
      return colIdToFilterText.get(colId);
   }

   public Set<String> getColIds() {
      return colIdToFilterText.keySet();
   }

   /**
    * @param filterText the filterText to set
    */
   public void setFilterText(String colId, String filterText) {
      colIdToFilterText.put(colId, filterText);
   }

   public void clear() {
      colIdToFilterText.clear();
   }

   public void removeFilterText(String colId) {
      colIdToFilterText.remove(colId);
   }

   public String getXml() {
      StringBuffer sb = new StringBuffer();
      for (String colId : colIdToFilterText.keySet()) {
         sb.append(AXml.addTagData(COLUMN_FILTER_TAG, AXml.addTagData(COLUMN_ID_TAG, colId) + AXml.addTagData(
               FILTER_TEXT_TAG, colIdToFilterText.get(colId))));
      }
      return sb.toString();
   }

   public void setFromXml(String xml) {
      colIdToFilterText.clear();
      Matcher columnMatch = p.matcher(xml);
      while (columnMatch.find()) {
         colIdToFilterText.put(AXml.getTagData(columnMatch.group(1), COLUMN_ID_TAG), AXml.getTagData(
               columnMatch.group(1), FILTER_TEXT_TAG));
      }
   }

   @Override
   public String toString() {
      return "colFilter:[" + colIdToFilterText.toString() + "]";
   }

   public boolean isFiltered() {
      return colIdToFilterText.size() > 0;
   }
}
