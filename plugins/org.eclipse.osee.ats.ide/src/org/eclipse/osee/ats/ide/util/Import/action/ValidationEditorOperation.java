/*********************************************************************
 * Copyright (c) 2023 Boeing
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

package org.eclipse.osee.ats.ide.util.Import.action;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import org.eclipse.nebula.widgets.xviewer.core.model.SortDataType;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerAlign;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerColumn;
import org.eclipse.osee.ats.api.task.JaxAttribute;
import org.eclipse.osee.framework.ui.skynet.results.IResultsEditorProvider;
import org.eclipse.osee.framework.ui.skynet.results.IResultsEditorTab;
import org.eclipse.osee.framework.ui.skynet.results.ResultsEditor;
import org.eclipse.osee.framework.ui.skynet.results.table.IResultsXViewerRow;
import org.eclipse.osee.framework.ui.skynet.results.table.ResultsEditorTableTab;
import org.eclipse.osee.framework.ui.skynet.results.table.ResultsXViewerRow;

/**
 * @author Donald G. Dunne
 */
public class ValidationEditorOperation {

   private final List<ActionData> actionDatas;

   public ValidationEditorOperation(List<ActionData> actionDatas) {
      this.actionDatas = actionDatas;
   }

   public void open() {
      ResultsEditor.open(new IResultsEditorProvider() {

         private List<IResultsEditorTab> tabs;

         @Override
         public String getEditorName() {
            return ImportActionsViaSpreadsheetBlam.NAME;
         }

         @Override
         public List<IResultsEditorTab> getResultsEditorTabs() {
            if (tabs == null) {
               tabs = new LinkedList<>();
               tabs.add(createDataTab());
            }
            return tabs;
         }

         private IResultsEditorTab createDataTab() {
            List<IResultsXViewerRow> rows = new ArrayList<>();
            List<XViewerColumn> columns = new ArrayList<>();
            columns.add(new XViewerColumn(ActionColumns.Title.name(), ActionColumns.Title.name(), 280,
               XViewerAlign.Left, true, SortDataType.String, false, ""));
            columns.add(new XViewerColumn(ActionColumns.Description.name(), ActionColumns.Description.name(), 80,
               XViewerAlign.Left, true, SortDataType.String, false, ""));
            columns.add(new XViewerColumn(ActionColumns.ActionableItems.name(), ActionColumns.ActionableItems.name(),
               80, XViewerAlign.Left, true, SortDataType.String, false, ""));
            columns.add(new XViewerColumn(ActionColumns.Assignees.name(), ActionColumns.Assignees.name(), 80,
               XViewerAlign.Left, true, SortDataType.String, false, ""));
            columns.add(new XViewerColumn(ActionColumns.Originator.name(), ActionColumns.Originator.name(), 80,
               XViewerAlign.Left, true, SortDataType.String, false, ""));
            columns.add(new XViewerColumn(ActionColumns.ChangeType.name(), ActionColumns.ChangeType.name(), 80,
               XViewerAlign.Left, true, SortDataType.String, false, ""));
            columns.add(new XViewerColumn(ActionColumns.Priority.name(), ActionColumns.Priority.name(), 30,
               XViewerAlign.Left, true, SortDataType.String, false, ""));
            columns.add(new XViewerColumn(ActionColumns.Version.name(), ActionColumns.Version.name(), 80,
               XViewerAlign.Left, true, SortDataType.String, false, ""));
            columns.add(new XViewerColumn(ActionColumns.EstimatedHours.name(), ActionColumns.EstimatedHours.name(),
               40, XViewerAlign.Left, true, SortDataType.Integer, false, ""));
            columns.add(new XViewerColumn(ActionColumns.AgilePoints.name(), ActionColumns.AgilePoints.name(), 40,
               XViewerAlign.Left, true, SortDataType.Integer, false, ""));
            columns.add(new XViewerColumn(ActionColumns.AgileTeamName.name(), ActionColumns.AgileTeamName.name(), 80,
               XViewerAlign.Left, true, SortDataType.String, false, ""));
            columns.add(new XViewerColumn(ActionColumns.AgileSprintName.name(), ActionColumns.AgileSprintName.name(),
               80, XViewerAlign.Left, true, SortDataType.String, false, ""));

            for (JaxAttribute attr : actionDatas.iterator().next().attributes) {
               columns.add(new XViewerColumn(attr.getAttrType().getName(), attr.getAttrType().getName(), 80,
                  XViewerAlign.Left, true, SortDataType.String, false, ""));
            }

            for (ActionData aData : actionDatas) {
               ResultsXViewerRow row = new ResultsXViewerRow();
               rows.add(row);
               row.addValue(aData.title);
               row.addValue(aData.desc);
               row.addValue(aData.actionableItems.toString());
               row.addValue(aData.assignees.toString());
               if (aData.originator != null) {
                  row.addValue(aData.originator.toString());
               } else {
                  row.addValue("");
               }
               row.addValue(aData.changeType.toString());
               row.addValue(aData.priorityStr.toString());
               row.addValue(aData.version.toString());
               if (aData.estimatedHours != null) {
                  row.addValue(aData.estimatedHours.toString());
               } else {
                  row.addValue("");
               }
               row.addValue(aData.agilePoints.toString());
               row.addValue(aData.agileTeamName.toString());
               row.addValue(aData.agileSprintName.toString());
               for (JaxAttribute attr : aData.attributes) {
                  row.addValue(attr.getValues().toString());
               }
            }

            return new ResultsEditorTableTab("Data", columns, rows);
         }
      });

   }

}
