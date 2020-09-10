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
package org.eclipse.osee.define.rest;

import java.util.LinkedList;
import java.util.List;
import org.eclipse.osee.define.api.GenericReport;
import org.eclipse.osee.define.api.report.ReportColumn;
import org.eclipse.osee.define.api.report.ReportFilter;
import org.eclipse.osee.define.api.report.ReportLevel;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.RelationTypeSide;
import org.eclipse.osee.framework.core.enums.DeletionFlag;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.data.ArtifactReadable;
import org.eclipse.osee.orcs.search.QueryBuilder;

/**
 * @author David W. Miller
 */
public class GenericReportBuilder implements GenericReport {
   private final List<ReportLevel> reportLevels = new LinkedList<>();
   private final OrcsApi orcsApi;
   private QueryBuilder query;
   private ReportLevel currentLevel = null;

   public GenericReportBuilder(BranchId branch, ArtifactId view, OrcsApi orcsApi) {
      this.orcsApi = orcsApi;
      query = orcsApi.getQueryFactory().fromBranch(branch, view);
   }

   public int getColumnCount() {
      int count = 0;
      for (ReportLevel level : reportLevels) {
         count += level.getColumns().size();
      }
      return count;
   }

   @Override
   public GenericReport level(String levelName, QueryBuilder addedQuery) {
      int depth = 0;
      if (currentLevel != null) {
         depth = currentLevel.getDepth() + 1;
      }
      currentLevel = new ReportLevel(levelName);
      currentLevel.setDepth(depth);
      reportLevels.add(currentLevel);
      query = addedQuery;
      return this;
   }

   @Override
   public GenericReport column(String columnName) {
      currentLevel.column(columnName);
      return this;
   }

   @Override
   public GenericReport column(String columnName, AttributeTypeToken type) {
      currentLevel.column(columnName, type);
      return this;
   }

   @Override
   public GenericReport column(AttributeTypeToken type) {
      currentLevel.column(type);
      return this;
   }

   @Override
   public GenericReport filter(AttributeTypeToken type, String regex) {
      currentLevel.filter(type, regex);
      return this;
   }

   @Override
   public QueryBuilder query() {
      return query;
   }

   @Override
   public OrcsApi getOrcsApi() {
      return orcsApi;
   }

   public List<ReportLevel> getLevels() {
      return reportLevels;
   }

   public String[] getTopRow() {
      String[] row = new String[getColumnCount()];
      int pos = 0;
      for (ReportLevel level : this.getLevels()) {
         row[pos] = level.getLevelName();
         List<ReportColumn> columns = level.getColumns();
         for (int i = 0; i < columns.size(); ++i) {
            if (i != 0) {
               row[pos] = null;
            }
            pos++;
         }
      }
      return row;
   }

   public String[] getHeaderRow() {
      String[] row = new String[getColumnCount()];

      int pos = 0;
      for (ReportLevel level : this.getLevels()) {
         List<ReportColumn> columns = level.getColumns();
         for (int i = 0; i < columns.size(); ++i) {
            row[pos] = columns.get(i).getName();
            pos++;
         }
      }
      return row;
   }

   public void getDataRowsFromQuery(List<Object[]> rows) {
      List<ArtifactReadable> arts = query.asArtifacts();
      if (arts.isEmpty()) {
         throw new OseeCoreException("Invalid Query in GenericReportBuilder");
      }

      rows.add(getTopRow());
      rows.add(getHeaderRow());
      String[] row = new String[getColumnCount()];

      for (ArtifactReadable art : arts) {
         fillReportDataFromQuery(art, rows, row, 0, 0);
      }
   }

   private boolean isFinalLevel(int depth) {
      return depth == (this.getLevels().size() - 1);
   }

   private void fillReportDataFromQuery(ArtifactReadable art, List<Object[]> rows, String[] row, int pos, int depth) {
      ReportLevel level = this.getLevels().get(depth);
      for (ReportColumn column : level.getColumns()) {
         String columnData = column.getReportData(art);
         for (ReportFilter filter : column.getFilters()) {
            if (filter.filterMatches(columnData)) {
               // skip this row (and possibly all of the next levels)
               return;
            }
         }
         row[pos++] = columnData;
      }

      if (isFinalLevel(depth)) {
         finishRow(rows, row, pos);
      } else {
         // fill next level
         depth += 1;
         level = this.getLevels().get(depth);
         List<ArtifactReadable> arts = getArtsForLevel(art, level);
         if (arts.isEmpty()) {
            finishRow(rows, row, pos);
         } else {
            for (ArtifactReadable child : arts) {
               fillReportDataFromQuery(child, rows, row, pos, depth);
            }
         }
      }
   }

   private void finishRow(List<Object[]> rows, String row[], int pos) {
      //copy the row and add it into the row data
      String[] setrow = new String[getColumnCount()];
      for (int i = 0; i < pos; ++i) {
         setrow[i] = row[i];
      }
      rows.add(setrow);
   }

   private List<ArtifactReadable> getArtsForLevel(ArtifactReadable art, ReportLevel level) {
      List<ArtifactReadable> arts = new LinkedList<>();
      int depth = level.getDepth();
      if (depth == 0) {
         arts.addAll(query.asArtifacts());
      } else {
         if (level.getRelation() == null) {
            List<RelationTypeSide> relations = query.getRelationTypesForLevel(depth); // query depth doesn't count the artifact query level
            if (relations.isEmpty()) {
               throw new OseeCoreException("Relation not found for level %d", depth);
            }
            if (relations.size() > 1) {
               throw new OseeCoreException("Multiple relations in one level not implemented for Generic Report");
            }
            RelationTypeSide relation = relations.get(0);
            if (relation.isValid()) {
               level.setRelation(relation);
            } else {
               throw new OseeCoreException("Invalid relation found for level %d", depth);
            }
         }
         arts.addAll(art.getRelated(level.getRelation(), DeletionFlag.EXCLUDE_DELETED));
      }
      return arts;
   }
}
