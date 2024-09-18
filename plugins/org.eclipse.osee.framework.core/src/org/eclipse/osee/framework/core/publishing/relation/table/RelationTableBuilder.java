/*********************************************************************
 * Copyright (c) 2024 Boeing
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
package org.eclipse.osee.framework.core.publishing.relation.table;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.osee.framework.core.OrcsTokenService;
import org.eclipse.osee.framework.core.data.ArtifactReadable;
import org.eclipse.osee.framework.core.data.RelationTypeSide;
import org.eclipse.osee.framework.core.data.RelationTypeToken;
import org.eclipse.osee.framework.core.enums.DeletionFlag;
import org.eclipse.osee.framework.core.enums.RelationSide;
import org.eclipse.osee.framework.core.publishing.PublishingArtifact;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * Builds and generates a relation table by processing each relation type side specified in the options. This class uses
 * the provided {@link RelationTableAppender} to append HTML content for the relation table, including headers and rows,
 * based on the relation type and related artifacts.
 * 
 * @author Jaden W. Puckett
 */
public class RelationTableBuilder {

   private final RelationTableOptions relationTableOptions;
   private final PublishingArtifact artifact;
   private final OrcsTokenService orcsTokenService;
   private final RelationTableAppender appender;

   /**
    * Constructs a RelationTableBuilder with the necessary dependencies.
    *
    * @param relationTableOptions the options for configuring the relation table
    * @param artifact the publishing artifact associated with the relation table
    * @param orcsTokenService the service used for retrieving relation types and attributes
    * @param appender the appender used to add the generated content to the publishing output
    */
   public RelationTableBuilder(RelationTableOptions relationTableOptions, PublishingArtifact artifact, OrcsTokenService orcsTokenService, RelationTableAppender appender) {
      this.relationTableOptions = relationTableOptions;
      this.artifact = artifact;
      this.orcsTokenService = orcsTokenService;
      this.appender = appender;
   }

   /**
    * Builds the relation table by processing each relation type side specified in the options.
    */
   public void buildRelationTable() {
      List<String> relationTypeSides = relationTableOptions.getRelationTableRelationTypeSides();
      for (String relationTypeSide : relationTypeSides) {
         processRelationTypeSide(relationTypeSide);
      }
   }

   /**
    * Processes a single relation type side, generating and appending the corresponding HTML table.
    *
    * @param relationTypeSide the relation type side in the format "RelationTypeNameOrId|RelationTypeSideName"
    */
   private void processRelationTypeSide(String relationTypeSide) {
      String[] parts = relationTypeSide.split("\\|");
      if (parts.length != 2) {
         throw new IllegalArgumentException("Invalid format for relationTypeSide: " + relationTypeSide);
      }

      String relationTypeNameOrId = parts[0].trim();
      String relationTypeSideName = parts[1].trim();

      RelationTypeToken relation = retrieveRelationType(relationTypeNameOrId);
      if (relation == null) {
         return;
      }

      List<ArtifactReadable> relatedArtifacts = getRelatedArtifacts(relation, relationTypeSideName);
      if (relatedArtifacts.isEmpty()) {
         return;
      }

      // Start the table and append the column headers
      appender.startTable();
      appender.appendTableHeader(relation.getName(), relationTypeSideName,
         relationTableOptions.getRelationTableColumns().size());
      appender.appendColumnHeaders(relationTableOptions.getRelationTableColumns());

      // Append rows
      for (ArtifactReadable artifact : relatedArtifacts) {
         List<String> cellValues = getCellValuesForArtifact(artifact);
         appender.appendRow(cellValues);
      }

      appender.endTable();
   }

   /**
    * Retrieves a {@link RelationTypeToken} based on the given relation type name or ID.
    *
    * @param relationTypeNameOrId the relation type name or ID
    * @return the corresponding {@link RelationTypeToken}, or null if the relation type is not found
    */
   private RelationTypeToken retrieveRelationType(String relationTypeNameOrId) {
      if (Strings.isLong(relationTypeNameOrId)) {
         Long relTypeId = Long.parseLong(relationTypeNameOrId);
         return orcsTokenService.getRelationType(relTypeId);
      } else {
         return orcsTokenService.getRelationType(relationTypeNameOrId);
      }
   }

   /**
    * Retrieves the related artifacts for a given relation type and side.
    *
    * @param relation the relation type token
    * @param relationTypeSideName the name of the relation type side
    * @return a list of related artifacts associated with the specified relation type side
    */
   private List<ArtifactReadable> getRelatedArtifacts(RelationTypeToken relation, String relationTypeSideName) {
      RelationTypeSide sideA = RelationTypeSide.create(relation, RelationSide.SIDE_A);
      RelationTypeSide sideB = RelationTypeSide.create(relation, RelationSide.SIDE_B);
      RelationTypeSide properSide =
         relation.getSideName(RelationSide.SIDE_A).equals(relationTypeSideName) ? sideA : sideB;
      return artifact.getRelated(properSide, DeletionFlag.EXCLUDE_DELETED);
   }

   /**
    * Retrieves the cell values for a given artifact based on the configured columns.
    *
    * @param artifact the artifact to retrieve cell values for
    * @return a list of cell values for the artifact
    */
   private List<String> getCellValuesForArtifact(ArtifactReadable artifact) {
      List<String> cellValues = new ArrayList<>();
      for (String column : relationTableOptions.getRelationTableColumns()) {
         String cellValue = getCellValue(column, artifact);
         cellValues.add(cellValue);
      }
      return cellValues;
   }

   /**
    * Retrieves the cell value for a given column and related artifact.
    *
    * @param column the column name or ID
    * @param relatedArtifact the related artifact
    * @return the cell value as a string
    */
   private String getCellValue(String column, ArtifactReadable relatedArtifact) {
      if (RelationTableOptions.isDefinedColumn(column)) {
         if (column.equals(RelationTableOptions.ARTIFACT_ID)) {
            return String.valueOf(relatedArtifact.getArtifactId());
         } else if (column.equals(RelationTableOptions.ARTIFACT_NAME)) {
            return relatedArtifact.getName();
         }
      } else {
         if (Strings.isLong(column)) {
            Long attrTypeId = Long.parseLong(column);
            return relatedArtifact.getAttributeValuesAsString(orcsTokenService.getAttributeType(attrTypeId));
         } else {
            return relatedArtifact.getAttributeValuesAsString(orcsTokenService.getAttributeType(column));
         }
      }
      return "";
   }
}