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
package org.eclipse.osee.orcs.rest.internal.writers;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactReadable;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.RelationTypeSide;
import org.eclipse.osee.framework.core.enums.DeletionFlag;
import org.eclipse.osee.framework.core.enums.RelationSide;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.rest.model.GenericReport;
import org.eclipse.osee.orcs.search.QueryBuilder;
import org.eclipse.osee.orcs.search.ReportColumn;
import org.eclipse.osee.orcs.search.ReportFilter;
import org.eclipse.osee.orcs.search.ReportLevel;

/**
 * @author David W. Miller
 */
public class GenericReportBuilder implements GenericReport {
   private final List<ReportLevel> reportLevels = new LinkedList<>();
   private final OrcsApi orcsApi;
   private final BranchId branch;
   private final ArtifactId view;
   private QueryBuilder query;
   private ReportLevel currentLevel = null;

   public GenericReportBuilder(BranchId branch, ArtifactId view, OrcsApi orcsApi) {
      this.orcsApi = orcsApi;
      this.branch = branch;
      this.view = view;
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
      int nextDepth = nextDepth();
      currentLevel = new ReportLevel(levelName);
      currentLevel.setDepth(nextDepth);
      reportLevels.add(currentLevel);
      query = addedQuery; // intentional: builder pattern requires shared mutable reference
      return this;
   }

   @Override
   public GenericReport level(String levelName, String typeName) {
      int nextDepth = nextDepth();
      currentLevel = new ReportLevel(levelName);
      currentLevel.setDepth(nextDepth);
      reportLevels.add(currentLevel);
      query = query.andIsOfType(orcsApi.tokenService().getArtifactType(typeName));
      return this;
   }

   @Override
   public GenericReport relationLevel(String levelName, String relationName, String relationSide) {
      int nextDepth = nextDepth();
      currentLevel = new ReportLevel(levelName);
      currentLevel.setDepth(nextDepth);
      reportLevels.add(currentLevel);
      RelationTypeSide relation =
         new RelationTypeSide(orcsApi.tokenService().getRelationType(relationName), RelationSide.valueOf(relationSide));
      currentLevel.setRelation(relation);
      query = query.follow(relation);
      return this;
   }

   @Override
   public GenericReport followFork(String relationName, String relationSide) {
      if (!currentLevel.isRelationLevel()) {
         throw new OseeArgumentException(
            "followFork can only be used on a level created by relationLevel, not a query-based level");
      }
      if (!currentLevel.getColumns().isEmpty()) {
         throw new OseeArgumentException(
            "followFork must be called before adding columns. Add all followFork calls immediately after relationLevel.");
      }
      if (currentLevel.hasForkRelationName(relationName)) {
         throw new OseeArgumentException(
            "followFork relation '%s' is already used in this level. Each followFork must specify a different relation.",
            relationName);
      }
      RelationTypeSide relation =
         new RelationTypeSide(orcsApi.tokenService().getRelationType(relationName), RelationSide.valueOf(relationSide));
      currentLevel.addForkRelation(relation, relationName);
      query = query.followFork(relation);
      return this;
   }

   private int nextDepth() {
      return currentLevel == null ? 0 : currentLevel.getDepth() + 1;
   }

   @Override
   public GenericReport column(String columnName) {
      currentLevel.column(columnName);
      return this;
   }

   @Override
   public GenericReport column(String columnName, String typeName) {
      AttributeTypeToken type = orcsApi.tokenService().getAttributeType(typeName);
      currentLevel.column(columnName, type);
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
   public GenericReport type(String columnName) {
      currentLevel.type(columnName);
      return this;
   }

   @Override
   public GenericReport filter(AttributeTypeToken type, String regex) {
      currentLevel.filter(type, regex);
      return this;
   }

   /**
    * Returns the current query builder. Intentionally exposes the mutable reference as part of the builder pattern —
    * callers chain query operations (e.g. andIsOfType, follow).
    */
   @Override
   public QueryBuilder query() {
      return query;
   }

   @Override
   public OrcsApi getOrcsApi() {
      return orcsApi;
   }

   public List<ReportLevel> getLevels() {
      return Collections.unmodifiableList(reportLevels);
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
            try {
               if (filter.filterMatches(columnData)) {
                  // skip this row (and possibly all of the next levels)
                  return;
               }
            } catch (Exception ex) {
               // skip matcher exceptions
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

   /**
    * Copies filled columns into a new row array and appends it to the result set. Columns beyond {@code pos} remain
    * null, representing empty cells for deeper levels that had no related artifacts.
    */
   private void finishRow(List<Object[]> rows, String row[], int pos) {
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
         throw new OseeCoreException("This a level guard exception, this method should not be called for level 0",
            depth);
      } else {
         if (level.isRelationLevel() && level.getRelation() == null) {
            throw new OseeCoreException("Relation not found for level %d - unexpected behavior, check parsed code.",
               depth);
         }
         for (RelationTypeSide relation : level.getAllRelations()) {
            arts.addAll(art.getRelated(relation, DeletionFlag.EXCLUDE_DELETED));
         }
         // This method is cheating a little bit. Instead of using the query to get the artifacts from a relation,
         // it uses the ArtifactReadable loading of related artifacts instead. However, not all relations are loaded.
         // The following fixes that, and loads all of the artifacts in the list of related artifacts, replacing them
         // with the loaded version.
         ListIterator<ArtifactReadable> iter = arts.listIterator();
         while (iter.hasNext()) {
            ArtifactReadable check = iter.next();
            if (check.isNotLoaded()) {
               ArtifactReadable loaded =
                  orcsApi.getQueryFactory().fromBranch(branch, view).andId(check).asArtifactOrSentinel();
               if (loaded.isValid()) {
                  iter.set(loaded);
               } else {
                  iter.remove();
               }
            }
         }
      }
      return arts;
   }
}
