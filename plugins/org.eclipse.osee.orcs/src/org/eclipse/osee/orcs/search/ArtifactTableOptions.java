/*********************************************************************
 * Copyright (c) 2022 Boeing
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

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;

/**
 * @author Christopher G. Rebuck
 */
public class ArtifactTableOptions {

   private final List<AttributeReportColumn> attributeColumns;

   private final ArtifactIdReportColumn artifactIdColumn;
   private final ArtifactTypeReportColumn artifactTypeColumn;

   private final List<ReportColumn> columns = new ArrayList<>();

   private final List<AttributeTypeToken> attributeTypes;

   public ArtifactTableOptions(List<AttributeTypeToken> attributeTypes) {
      this.attributeTypes = attributeTypes;
      this.attributeColumns = new ArrayList<AttributeReportColumn>();
      this.artifactIdColumn = new ArtifactIdReportColumn();
      this.artifactTypeColumn = new ArtifactTypeReportColumn();

      attributeTypes.forEach(column -> attributeColumns.add(new AttributeReportColumn(column)));

      columns.add(artifactIdColumn);
      columns.add(artifactTypeColumn);
      columns.addAll(attributeColumns);
   }

   @JsonIgnore
   public List<AttributeTypeToken> getAttributeColumns() {
      return attributeTypes;
   }

   public List<ReportColumn> getColumns() {
      return columns;
   }

   @Override
   public String toString() {
      return attributeColumns.toString();
   }

}
