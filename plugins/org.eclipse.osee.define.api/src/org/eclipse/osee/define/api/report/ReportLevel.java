/*********************************************************************
 * Copyright (c) 2020 Boeing
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

package org.eclipse.osee.define.api.report;

import java.util.LinkedList;
import java.util.List;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.RelationTypeSide;

/**
 * @author David W. Miller
 */
public class ReportLevel {
   private final String levelName;
   private int depth = 0;
   private final List<ReportColumn> columns = new LinkedList<ReportColumn>();
   private Boolean filtered = false;

   private RelationTypeSide relation = null;

   public ReportLevel(String levelName) {
      this.levelName = levelName;
   }

   public ReportLevel column(String columnName) {
      columns.add(new ArtifactIdReportColumn(columnName));
      return this;
   }

   public ReportLevel column(String columnName, AttributeTypeToken type) {
      columns.add(new AttributeReportColumn(columnName, type));
      return this;
   }

   public ReportLevel column(AttributeTypeToken type) {
      columns.add(new AttributeReportColumn(type.getName(), type));
      return this;
   }

   public void filter(AttributeTypeToken type, String filterRegex) {
      for (ReportColumn column : getColumnsOfType(type)) {
         column.addFilter(filterRegex);
      }
   }

   public Boolean isFiltered() {
      return filtered;
   }

   public void setFiltered() {
      filtered = true;
   }

   public String getLevelName() {
      return levelName;
   }

   public List<ReportColumn> getColumns() {
      return columns;
   }

   public void setDepth(int depth) {
      this.depth = depth;
   }

   public int getDepth() {
      return depth;
   }

   public RelationTypeSide getRelation() {
      return relation;
   }

   public void setRelation(RelationTypeSide relation) {
      this.relation = relation;
   }

   private List<ReportColumn> getColumnsOfType(AttributeTypeToken type) {
      List<ReportColumn> results = new LinkedList<>();
      for (ReportColumn column : columns) {
         if (column instanceof AttributeReportColumn) {
            AttributeReportColumn attrColumn = (AttributeReportColumn) column;
            if (attrColumn.getType().equals(type)) {
               results.add(attrColumn);
            }
         }
      }
      return results;
   }
}
