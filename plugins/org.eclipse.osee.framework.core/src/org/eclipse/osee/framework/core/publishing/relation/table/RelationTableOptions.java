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
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.eclipse.jdt.annotation.NonNull;

/**
 * Encapsulates configuration options for rendering a relation table in publishing templates. The options include
 * filtering by artifact types, selecting columns to display, and specifying relation type sides.
 * 
 * @author Jaden W. Puckett
 */
public class RelationTableOptions {

   /**
    * List of artifact type names or IDs to filter the artifacts rendered in the relation table.
    */
   private final @NonNull List<String> relationTableArtifactTypeNamesAndOrIds;

   /**
    * List of column names or attribute type names/IDs that define the columns rendered in the relation table. If no
    * columns are specified, default columns will be used.
    */
   private final @NonNull List<String> relationTableColumns;

   /**
    * Column name representing the artifact ID.
    */
   public static final String ARTIFACT_ID = "Artifact Id";

   /**
    * Column name representing the artifact name.
    */
   public static final String ARTIFACT_NAME = "Artifact Name";

   /**
    * List of predefined columns for displaying core data such as artifact ID and artifact name.
    */
   private static final List<String> DEFINED_COLUMNS = new ArrayList<>(Arrays.asList(ARTIFACT_ID, ARTIFACT_NAME));

   /**
    * Default columns used if no specific columns are provided during object construction or if an error occurs while
    * loading column configurations.
    */
   private static final List<String> DEFAULT_COLUMNS = new ArrayList<>(Arrays.asList(ARTIFACT_ID, ARTIFACT_NAME));

   /**
    * List of relation type sides to be displayed in the relation table. Each entry uses a "|" (pipe) delimiter to
    * separate the relation type name/ID from the associated relation type side name. Format:
    * "(relationTypeNameOrId)|(relationTypeSideName)".
    */
   private final @NonNull List<String> relationTableRelationTypeSides;

   /**
    * Constructs a new {@link RelationTableOptions} object.
    * 
    * @param relationTableArtifactTypeNamesAndOrIds the list of artifact type names or IDs to filter by
    * @param relationTableColumns the list of columns to display in the relation table; if empty, default columns are
    * used
    * @param relationTableRelationTypeSides the list of relation type sides to display, formatted as
    * "(relationTypeNameOrId)|(relationTypeSideName)"
    */
   public RelationTableOptions(@NonNull List<String> relationTableArtifactTypeNamesAndOrIds, @NonNull List<String> relationTableColumns, @NonNull List<String> relationTableRelationTypeSides) {
      // Ensure ArrayList for each list
      this.relationTableArtifactTypeNamesAndOrIds = new ArrayList<>(relationTableArtifactTypeNamesAndOrIds);
      this.relationTableColumns =
         relationTableColumns.isEmpty() ? new ArrayList<>(DEFAULT_COLUMNS) : new ArrayList<>(relationTableColumns);
      this.relationTableRelationTypeSides = new ArrayList<>(relationTableRelationTypeSides);
   }

   /**
    * Constructs a new default {@link RelationTableOptions} object.
    */
   public RelationTableOptions() {
      // Ensure ArrayList for default constructor
      this.relationTableArtifactTypeNamesAndOrIds = new ArrayList<>(Collections.emptyList());
      this.relationTableColumns = new ArrayList<>(DEFAULT_COLUMNS);
      this.relationTableRelationTypeSides = new ArrayList<>(Collections.emptyList());
   }

   /**
    * Gets the list of artifact type names or IDs used to filter the relation table.
    * 
    * @return a list of artifact type names or IDs
    */
   public List<String> getRelationTableArtifactTypeNamesAndOrIds() {
      return relationTableArtifactTypeNamesAndOrIds;
   }

   /**
    * Gets the list of columns to be displayed in the relation table.
    * 
    * @return a list of column names or attribute type names/IDs
    */
   public List<String> getRelationTableColumns() {
      return relationTableColumns;
   }

   /**
    * Gets the list of relation type sides to be displayed in the relation table.
    * 
    * @return a list of relation type sides formatted as "(relationTypeNameOrId)|(relationTypeSideName)"
    */
   public List<String> getRelationTableRelationTypeSides() {
      return relationTableRelationTypeSides;
   }

   /**
    * Checks if a given column name is defined as a core column.
    * 
    * @param columnName the name of the column to check
    * @return {@code true} if the column name is predefined, {@code false} otherwise
    */
   public static boolean isDefinedColumn(String columnName) {
      return DEFINED_COLUMNS.contains(columnName);
   }

   @Override
   public String toString() {
      return "RelationTableOptions {\n" + "   relationTableArtifactTypeNamesAndOrIds: " + relationTableArtifactTypeNamesAndOrIds + ",\n" + "   relationTableColumns: " + relationTableColumns + ",\n" + "   relationTableRelationTypeSides: " + relationTableRelationTypeSides + "\n" + '}';
   }
}