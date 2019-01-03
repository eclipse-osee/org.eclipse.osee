/*******************************************************************************
 * Copyright (c) 2015 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.ide.ev;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerColumn;
import org.eclipse.osee.framework.jdk.core.util.DateUtil;
import org.eclipse.osee.framework.ui.plugin.PluginUiImage;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateComposite.TableLoadOption;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItem;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItemAction;
import org.eclipse.osee.framework.ui.skynet.results.IResultsEditorProvider;
import org.eclipse.osee.framework.ui.skynet.results.IResultsEditorTab;
import org.eclipse.osee.framework.ui.skynet.results.ResultsEditor;
import org.eclipse.osee.framework.ui.skynet.results.table.IResultsXViewerRow;
import org.eclipse.osee.framework.ui.skynet.results.table.ResultsEditorTableTab;
import org.eclipse.osee.framework.ui.skynet.results.table.ResultsXViewerRow;

/**
 * @author Donald G. Dunne
 */
public class WorkPackageConfigReport extends XNavigateItemAction {

   public static final String TITLE = "Work Package Config Report";

   public WorkPackageConfigReport(XNavigateItem parent) {
      super(parent, TITLE, PluginUiImage.ADMIN);
   }

   @Override
   public void run(TableLoadOption... tableLoadOptions) {
      ResultsEditor.open(new IResultsEditorProvider() {

         private List<IResultsEditorTab> tabs;

         @Override
         public String getEditorName() {
            return TITLE;
         }

         @Override
         public List<IResultsEditorTab> getResultsEditorTabs() {
            if (tabs == null) {
               tabs = new LinkedList<>();
               tabs.add(createConfigurationTab());
            }
            return tabs;
         }

      });
   }

   private IResultsEditorTab createConfigurationTab() {
      List<XViewerColumn> columns = getColumns();

      List<IResultsXViewerRow> rows = new ArrayList<>();
      WorkPackageConfigLoader loader = new WorkPackageConfigLoader();
      loader.load();

      for (WorkPackageData data : loader.getWorkPackageDatas()) {
         rows.add(new ResultsXViewerRow(new String[] {
            data.getWorkPackageName(),
            data.getColorTeam(),
            String.valueOf(data.isWorkPackageActive()),
            data.getProgramName(),
            data.getWorkPackageProgram(),
            data.getInsertionName(),
            data.getInsertionActivityName(), //
            data.getWorkPackageIdStr(),
            data.getActivityId(),
            String.valueOf(data.getWorkPackagePercentComplete()),
            String.valueOf(data.getWorkPackagePointsNumeric()),
            data.getWorkPackageType(),
            String.valueOf(data.getWorkPackageBac()),
            data.getWorkPackageIpt(),
            DateUtil.get(data.getWorkPackageStartDate(), DateUtil.MMDDYY),
            DateUtil.get(data.getWorkPackageEndDate(), DateUtil.MMDDYY),
            data.getWorkPackageNotes(),
            data.getWorkPackageAnnotation(),
            data.getTeamNames(),
            data.getCountryName(),
            String.valueOf(data.getWorkPackageId()),
            //
         }));
      }
      return new ResultsEditorTableTab("Configuration", columns, rows);
   }

   private List<XViewerColumn> getColumns() {
      List<XViewerColumn> columns = Arrays.asList( //
         WorkPackageReportColumns.workPackageNameColumn, //
         WorkPackageReportColumns.wpColorTeamColumn, //
         WorkPackageReportColumns.wpActiveColumn, //
         WorkPackageReportColumns.programColumn, //
         WorkPackageReportColumns.wpProgramColumn, //
         WorkPackageReportColumns.insertionColumn, //
         WorkPackageReportColumns.insertionActivityColumn, //
         WorkPackageReportColumns.wpIdColumn, //
         WorkPackageReportColumns.wpActivityIdColumn, //
         WorkPackageReportColumns.wpPercentComplete, //
         WorkPackageReportColumns.wpPointsNumeric, //
         WorkPackageReportColumns.wpType, //
         WorkPackageReportColumns.wpBac, //
         WorkPackageReportColumns.wpIpt, //
         WorkPackageReportColumns.wpStartDate, //
         WorkPackageReportColumns.wpEndDate, //
         WorkPackageReportColumns.wpNotes, //
         WorkPackageReportColumns.wpAnnotation, //
         WorkPackageReportColumns.wpTeamAiNames, //
         WorkPackageReportColumns.countryColumn, //
         WorkPackageReportColumns.wpId //
      //
      );
      return columns;
   }

}
