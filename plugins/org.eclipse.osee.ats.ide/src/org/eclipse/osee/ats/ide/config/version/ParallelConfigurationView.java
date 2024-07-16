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

package org.eclipse.osee.ats.ide.config.version;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import org.eclipse.jface.window.Window;
import org.eclipse.nebula.widgets.xviewer.core.model.SortDataType;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerAlign;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerColumn;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.api.util.AtsImage;
import org.eclipse.osee.ats.api.version.IAtsVersion;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.ats.ide.navigate.AtsNavigateViewItems;
import org.eclipse.osee.ats.ide.util.widgets.dialog.TeamDefinitionDialog;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateComposite.TableLoadOption;
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
public class ParallelConfigurationView extends XNavigateItemAction {

   public static final String TITLE = "Parallel Configuration View";
   private static enum Columns {
      AtsVersion,
      Branch,
      AllowCreate,
      AllowCommit,
      Released,
      ArtId;
   };

   public ParallelConfigurationView() {
      super(TITLE, AtsImage.VERSION, AtsNavigateViewItems.ATS_VERSIONS);
   }

   @Override
   public void run(TableLoadOption... tableLoadOptions) throws Exception {
      IAtsTeamDefinition teamDef;
      TeamDefinitionDialog dialog = new TeamDefinitionDialog(TITLE, "Select Team");
      dialog.setMultiSelect(false);
      Collection<IAtsTeamDefinition> teamDefs =
         AtsApiService.get().getTeamDefinitionService().getTeamDefHoldingVersions();
      dialog.setInput(teamDefs);
      if (dialog.open() == Window.OK) {
         teamDef = dialog.getSelectedFirst();

         ResultsEditor.open(new IResultsEditorProvider() {

            private List<IResultsEditorTab> tabs;

            @Override
            public String getEditorName() {
               return String.format("%s - %s", TITLE, teamDef.toStringWithId());
            }

            @Override
            public List<IResultsEditorTab> getResultsEditorTabs() {
               if (tabs == null) {
                  // load all versions first
                  AtsApiService.get().getVersionService().getVersions(teamDef);
                  tabs = new LinkedList<>();
                  tabs.add(createDataTab(teamDef));
               }
               return tabs;
            }
         });
      }
   }

   private IResultsEditorTab createDataTab(IAtsTeamDefinition teamDef) {
      List<IResultsXViewerRow> rows = new ArrayList<>();
      for (IAtsVersion ver : AtsApiService.get().getVersionService().getVersions(teamDef)) {
         rows.add(new VersionRow(ver, null));
      }

      List<XViewerColumn> columns = Arrays.asList(
         new XViewerColumn(Columns.AtsVersion.name(), Columns.AtsVersion.name(), 260, XViewerAlign.Left, true,
            SortDataType.String, false, ""),
         new XViewerColumn(Columns.Branch.name(), Columns.Branch.name(), 260, XViewerAlign.Left, true,
            SortDataType.String, false, ""),
         new XViewerColumn(Columns.AllowCreate.name(), Columns.AllowCreate.name(), 126, XViewerAlign.Left, true,
            SortDataType.String, false, ""),
         new XViewerColumn(Columns.AllowCommit.name(), Columns.AllowCommit.name(), 126, XViewerAlign.Left, true,
            SortDataType.String, false, ""),
         new XViewerColumn(Columns.Released.name(), Columns.Released.name(), 126, XViewerAlign.Left, true,
            SortDataType.String, false, ""),
         new XViewerColumn(Columns.ArtId.name(), Columns.ArtId.name(), 126, XViewerAlign.Left, true, SortDataType.Long,
            false, ""));

      return new ResultsEditorTableTab("Versions", columns, rows);
   }

   public static class VersionRow extends ResultsXViewerRow {

      private final IAtsVersion version;
      private final VersionRow parent;

      public VersionRow(IAtsVersion version, VersionRow parent) {
         this.version = version;
         this.parent = parent;
         setData(version.getArtifactToken());
         values.add(version.getName());
         BranchId baselineBranch = version.getBaselineBranch();
         if (baselineBranch.isValid()) {
            values.add(BranchManager.getBranchName(baselineBranch));
         } else {
            values.add("");
         }
         values.add(String.valueOf(version.isAllowCreateBranch()));
         values.add(String.valueOf(version.isAllowCommitBranch()));
         values.add(String.valueOf(version.isReleased()));
         values.add(String.valueOf(version.getIdString()));
      }

      @Override
      public boolean hasChildren() {
         return getChildren().size() > 0;
      }

      @Override
      public Collection<IResultsXViewerRow> getChildren() {
         List<IResultsXViewerRow> rows = new ArrayList<>();
         for (IAtsVersion version : AtsApiService.get().getVersionService().getParallelVersions(version)) {
            rows.add(new VersionRow(version, this));
         }
         return rows;
      }

      @Override
      public IResultsXViewerRow getParent() {
         return parent;
      }

   }
}
