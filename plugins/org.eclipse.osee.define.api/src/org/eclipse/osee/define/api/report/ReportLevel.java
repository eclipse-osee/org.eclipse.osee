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
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.RelationTypeSide;

/**
 * @author David W. Miller
 */
public class ReportLevel {
   private final String levelName;
   private ArtifactToken artToken = ArtifactToken.SENTINEL;
   private ArtifactTypeToken type = ArtifactTypeToken.SENTINEL;
   private RelationTypeSide relation = null;
   private int depth = 0;
   private final List<ReportColumn> columns = new LinkedList<ReportColumn>();

   public ReportLevel(String levelName) {
      this.levelName = levelName;
   }

   public ReportLevel type(ArtifactTypeToken type) {
      this.type = type;
      return this;
   }

   public ReportLevel id(ArtifactToken token) {
      artToken = token;
      return this;
   }

   public ReportLevel relation(RelationTypeSide reltype) {
      this.relation = reltype;
      return this;
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

   public String getLevelName() {
      return levelName;
   }

   public List<ReportColumn> getColumns() {
      return columns;
   }

   public boolean isIdLevel() {
      return artToken.isValid();
   }

   public boolean isArtifactTypeLevel() {
      return type.isValid();
   }

   public boolean isRelationLevel() {
      return relation != null ? true : false;
   }

   public ArtifactToken getArtifactToken() {
      return artToken;
   }

   public ArtifactTypeToken getArtifactType() {
      return type;
   }

   public RelationTypeSide getRelationType() {
      return relation;
   }

   public void setDepth(int depth) {
      this.depth = depth;
   }

   public int getDepth() {
      return depth;
   }
}
