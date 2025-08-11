/*********************************************************************
 * Copyright (c) 2025 Boeing
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
package org.eclipse.osee.framework.core.publishing.table;

import java.util.Arrays;
import java.util.List;
import org.eclipse.osee.framework.core.data.ArtifactReadable;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;

/**
 * A builder class for constructing HTML tables using a TableAppender.
 */
public class ArtifactAppendixTableBuilder {

   private final TableAppender tableAppender;
   public static final String SECTION_HEADING = "Appendix of Unpublished Linked Artifacts";
   public static final String HEADING = " - Linked Artifacts";
   public static final String ARTIFACT_NAME = "Artifact Name";
   public static final String ARTIFACT_ID = "Artifact ID";
   public static final String ARTIFACT_CONTENT = "Content";

   public static final List<String> columns = Arrays.asList(ARTIFACT_NAME, ARTIFACT_ID, ARTIFACT_CONTENT);
   private final List<ArtifactReadable> artsNotInPublish;
   private final String classification;

   /**
    * Constructor for the HtmlTableBuilder.
    *
    * @param tableAppender the TableAppender to use for building the table
    * @param HEADER the header for the table
    * @param columns the list of column names for the table
    * @param artsNotInPublish the list of artifacts to populate the table rows
    */
   public ArtifactAppendixTableBuilder(TableAppender tableAppender, List<ArtifactReadable> artsNotInPublish, String classification) {
      this.tableAppender = tableAppender;
      this.artsNotInPublish = artsNotInPublish;
      this.classification = classification;
   }

   /**
    * Public method to build the HTML table with the specified header, columns, and rows from artifacts.
    *
    * @return the HTML table as a string
    */
   public String buildTable() {
      startTable();
      appendTableHeader();
      appendColumnHeaders();
      appendRows();
      endTable();
      return getTable();
   }

   private void startTable() {
      tableAppender.startTable();
   }

   private void appendTableHeader() {
      tableAppender.appendTableHeading(classification + HEADING, columns.size());
   }

   private void appendColumnHeaders() {
      tableAppender.appendColumnHeaders(columns);
   }

   private void appendRows() {
      for (ArtifactReadable artifact : artsNotInPublish) {
         List<String> rowValues =
            Arrays.asList(artifact.getName(), getIdContent(artifact.getIdString()), getArtifactContent(artifact));
         tableAppender.appendRow(rowValues);
      }
   }

   private void endTable() {
      tableAppender.endTable();
   }

   private String getTable() {
      return tableAppender.getTable();
   }

   private String getArtifactContent(ArtifactReadable artifact) {
      String content = "";

      if (artifact.getAttributeCount(CoreAttributeTypes.MarkdownContent) == 1) {
         content = artifact.getSoleAttributeValue(CoreAttributeTypes.MarkdownContent);
      }

      return content;
   }

   private String getIdContent(String artId) {
      return "<a id=\"" + artId + "\"></a>" + artId;
   }
}