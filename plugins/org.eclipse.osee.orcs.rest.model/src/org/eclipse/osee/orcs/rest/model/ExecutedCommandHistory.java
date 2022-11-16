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
package org.eclipse.osee.orcs.rest.model;

import static org.eclipse.osee.framework.core.enums.CoreAttributeTypes.CommandTimestamp;
import static org.eclipse.osee.framework.core.enums.CoreAttributeTypes.ExecutionFrequency;
import static org.eclipse.osee.framework.core.enums.CoreAttributeTypes.Favorite;
import static org.eclipse.osee.framework.core.enums.CoreAttributeTypes.IsValidated;
import static org.eclipse.osee.framework.core.enums.CoreAttributeTypes.ParameterizedCommand;
import static org.eclipse.osee.framework.core.enums.CoreRelationTypes.DefaultHierarchical_Child;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import org.eclipse.osee.framework.core.data.ArtifactReadable;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.UserToken;
import org.eclipse.osee.framework.jdk.core.type.NamedIdBase;
import org.eclipse.osee.orcs.search.ArtifactIdReportColumn;
import org.eclipse.osee.orcs.search.AttributeReportColumn;
import org.eclipse.osee.orcs.search.ReportColumn;

/**
 * @author Christopher Rebuck
 */

public final class ExecutedCommandHistory extends NamedIdBase {

   private final String executedCommandHistory;
   private final String commandHistoryId;
   private String[][] data;
   private List<ReportColumn> columnHeaders;

   public ExecutedCommandHistory(UserToken user, ArtifactReadable executedCommandHx, List<String> headers) {
      super(user.getId(), user.getName());
      this.executedCommandHistory = executedCommandHx.getName();
      this.commandHistoryId = executedCommandHx.getIdString();
      this.setColumnHeaders(createColumns(executedCommandHx));
      this.setData(createData(executedCommandHx, headers));
   }

   public ExecutedCommandHistory(UserToken user) {
      super(user.getId(), user.getName());
      this.executedCommandHistory = "No history available";
      this.commandHistoryId = "";
      this.columnHeaders = new ArrayList<ReportColumn>();
      this.data = new String[0][0];

   }

   public String getExecutedCommandHistory() {
      return executedCommandHistory;
   }

   public String getCommandHistoryId() {
      return commandHistoryId;
   }

   public void setColumnHeaders(List<AttributeReportColumn> columns) {
      ArtifactIdReportColumn artifactIdColumn = new ArtifactIdReportColumn();
      List<ReportColumn> headers = new ArrayList<>();
      headers.add(artifactIdColumn);
      headers.addAll(columns);
      this.columnHeaders = headers;
   }

   public List<ReportColumn> getColumns() {
      return columnHeaders;
   }

   private void setData(String[][] createdData) {
      this.data = createdData;
   }

   public String[][] getData() {
      return data;
   }

   private List<AttributeReportColumn> createColumns(ArtifactReadable executedCommandHistory) {
      List<AttributeReportColumn> attributeColumns = new ArrayList<AttributeReportColumn>();

      if (!executedCommandHistory.getRelated(DefaultHierarchical_Child, ArtifactTypeToken.SENTINEL).isEmpty()) {
         ArtifactReadable executedCommand =
            executedCommandHistory.getRelated(DefaultHierarchical_Child, ArtifactTypeToken.SENTINEL).get(0);
         Collection<AttributeTypeToken> attributeList = executedCommand.getExistingAttributeTypes();
         attributeList.forEach(column -> attributeColumns.add(new AttributeReportColumn(column)));
      }
      return attributeColumns;
   }

   private String[][] createData(ArtifactReadable executedCommandHistory, List<String> columns) {
      if (!executedCommandHistory.getRelated(DefaultHierarchical_Child, ArtifactTypeToken.SENTINEL).isEmpty()) {
         List<String[]> executedCommandList =
            executedCommandHistory.getRelated(DefaultHierarchical_Child, ArtifactTypeToken.SENTINEL).stream().map(
               executedCommand -> createRowData(executedCommand)).collect(Collectors.toList());
         data = new String[executedCommandList.size()][columns.size()];

         for (int i = 0; i < executedCommandList.size(); i++) {
            data[i] = executedCommandList.get(i);
         }
         return data;

      } else {
         data = new String[0][0];
         return data;
      }

   }

   private String[] createRowData(ArtifactReadable command) {
      String[] arrayEl = new String[] {
         command.getIdString(),
         command.getName(),
         command.getAttributeValuesAsString(ParameterizedCommand),
         command.getAttributeValuesAsString(ExecutionFrequency),
         command.getAttributeValuesAsString(CommandTimestamp),
         command.getAttributeValuesAsString(IsValidated),
         command.getAttributeValuesAsString(Favorite),};

      return arrayEl;
   }
}
