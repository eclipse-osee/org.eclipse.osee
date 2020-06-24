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
package org.eclipse.osee.define.rest.internal;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import org.eclipse.osee.define.api.report.ReportColumn;
import org.eclipse.osee.define.api.report.ReportLevel;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.RelationTypeSide;
import org.eclipse.osee.framework.core.enums.DeletionFlag;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.Tree;
import org.eclipse.osee.framework.jdk.core.type.TreeNode;
import org.eclipse.osee.orcs.data.ArtifactReadable;
import org.eclipse.osee.orcs.search.QueryBuilder;

/**
 * @author David W. Miller
 */
public class GenericReportBuilder {
   private final Tree<ArtifactReadable> root = new Tree<>();
   private final List<ReportLevel> reportLevels = new LinkedList<>();
   private final BranchId branch;
   private final ArtifactId view;
   private ReportLevel currentLevel = null;

   public GenericReportBuilder(BranchId branch, ArtifactId view) {
      this.branch = branch;
      this.view = view;
   }

   public int getColumnCount() {
      int count = 0;
      for (ReportLevel level : reportLevels) {
         count += level.getColumns().size();
      }
      return count;
   }

   public GenericReportBuilder level(String levelName) {
      currentLevel = new ReportLevel(levelName);
      reportLevels.add(currentLevel);
      return this;
   }

   public GenericReportBuilder id(Long id) {
      ArtifactToken token = ArtifactToken.valueOf(id, branch);
      currentLevel.id(token);
      return this;
   }

   public GenericReportBuilder type(ArtifactTypeToken type) {
      currentLevel.type(type);
      return this;
   }

   public GenericReportBuilder relation(RelationTypeSide type) {
      currentLevel.relation(type);
      return this;
   }

   public GenericReportBuilder column(String columnName) {
      currentLevel.column(columnName);
      return this;
   }

   public GenericReportBuilder column(String columnName, AttributeTypeToken type) {
      currentLevel.column(columnName, type);
      return this;
   }

   public GenericReportBuilder column(AttributeTypeToken type) {
      currentLevel.column(type);
      return this;
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

   public List<Object[]> getReportRows() {
      List<Object[]> rows = new ArrayList<>();
      rows.add(getTopRow());
      rows.add(getHeaderRow());
      getDataRows(rows);
      return rows;
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

   public void getDataRows(List<Object[]> rows) {
      List<TreeNode<ArtifactReadable>> children = root.getRoot().getChildren();
      if (children.isEmpty()) {
         throw new OseeCoreException("Data not filled in yet");
      }

      rows.add(getTopRow());
      rows.add(getHeaderRow());
      String[] row = new String[getColumnCount()];

      for (TreeNode<ArtifactReadable> child : children) {
         List<ReportLevel> levels = this.getLevels();
         int i = 0;
         fillReportData(child, levels, rows, row, i, 0);
      }

   }

   private void fillReportData(TreeNode<ArtifactReadable> node, List<ReportLevel> levels, List<Object[]> rows, String[] row, int pos, int depth) {
      ReportLevel level = levels.get(depth);
      for (ReportColumn column : level.getColumns()) {
         row[pos++] = column.getReportData(node.getSelf());
      }
      List<TreeNode<ArtifactReadable>> children = node.getChildren();
      if (children.isEmpty()) {
         //copy the row and add it into the row data
         String[] setrow = new String[getColumnCount()];
         for (int i = 0; i < pos; ++i) {
            setrow[i] = row[i];
         }
         rows.add(setrow);
      } else {
         for (TreeNode<ArtifactReadable> child : children) {
            fillReportData(child, this.getLevels(), rows, row, pos, depth + 1);
         }
      }
   }

   public void generateDataTree(QueryBuilder query) {
      int depth = 0;
      for (ReportLevel level : getLevels()) {
         if (level.isIdLevel()) {
            query = query.andId(ArtifactId.valueOf(level.getArtifactToken().getId()));
         } else if (level.isArtifactTypeLevel()) {
            query = query.andIsOfType(level.getArtifactType());
         } else if (level.isRelationLevel()) {
            if (depth == 0) {
               throw new OseeCoreException("follow relation cannot be level 0");
            }
            query = query.follow(level.getRelationType());
         }
         level.setDepth(depth++);
      }
      TreeNode<ArtifactReadable> top = root.getRoot();
      TreeNode<ArtifactReadable> current = top;
      // load the first level
      top.addChildren(query.asArtifacts());
      fillLevels(current, getLevels(), 1);
   }

   private void fillLevels(TreeNode<ArtifactReadable> node, List<ReportLevel> levels, int depth) {
      for (TreeNode<ArtifactReadable> child : node.getChildren()) {
         List<ArtifactReadable> arts = getArtsForLevel(child.getSelf(), levels.get(depth));
         if (!arts.isEmpty()) {
            child.addChildren(arts);
            if (depth < levels.size() - 1) {
               fillLevels(child, levels, depth + 1);
            }
         }
      }
   }

   private List<ArtifactReadable> getArtsForLevel(ArtifactReadable art, ReportLevel level) {
      List<ArtifactReadable> arts = new LinkedList<>();
      if (level.getDepth() == 0) {
         throw new OseeCoreException("Top level should never be seen here");
      }
      if (level.isIdLevel() || level.isArtifactTypeLevel()) {
         arts.add(art);
      } else if (level.isRelationLevel()) {
         arts.addAll(art.getRelated(level.getRelationType(), DeletionFlag.EXCLUDE_DELETED));
      }
      return arts;
   }
}
