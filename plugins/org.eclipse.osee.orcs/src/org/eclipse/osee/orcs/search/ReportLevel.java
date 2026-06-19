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

package org.eclipse.osee.orcs.search;

import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.RelationTypeSide;

/**
 * @author David W. Miller
 */
public class ReportLevel {
   private final String levelName;
   private int depth = 0;
   private final List<ReportColumn> columns = new LinkedList<ReportColumn>();

   private boolean isRelationLevel = false;
   private boolean finalized = false;
   private RelationTypeSide mainRelation = null;
   private final List<RelationTypeSide> forkRelations = new LinkedList<>();
   /** Tracks all relation names used in this level (main + forks) to prevent duplicates */
   private final Set<String> forkRelationNames = new HashSet<>();

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

   public ReportLevel type(String columnName) {
      columns.add(new ArtifactTypeReportColumn(columnName));
      return this;
   }

   public void filter(AttributeTypeToken type, String filterRegex) {
      for (ReportColumn column : getColumnsOfType(type)) {
         column.addFilter(filterRegex);
      }
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
      return mainRelation;
   }

   public void setRelation(RelationTypeSide relation) {
      this.mainRelation = relation;
      this.isRelationLevel = true;
      this.forkRelationNames.add(relation.getRelationType().getName());
   }

   public boolean isRelationLevel() {
      return isRelationLevel;
   }

   public boolean isFinalized() {
      return finalized;
   }

   public void markFinalized() {
      this.finalized = true;
   }

   public void addForkRelation(RelationTypeSide relation, String relationName) {
      forkRelations.add(relation);
      forkRelationNames.add(relationName);
   }

   public boolean hasForkRelationName(String relationName) {
      return forkRelationNames.contains(relationName);
   }

   public List<RelationTypeSide> getForkRelations() {
      return Collections.unmodifiableList(forkRelations);
   }

   public List<RelationTypeSide> getAllRelations() {
      if (forkRelations.isEmpty()) {
         return mainRelation != null ? Collections.singletonList(mainRelation) : Collections.emptyList();
      }
      List<RelationTypeSide> all = new LinkedList<>();
      if (mainRelation != null) {
         all.add(mainRelation);
      }
      all.addAll(forkRelations);
      return all;
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
