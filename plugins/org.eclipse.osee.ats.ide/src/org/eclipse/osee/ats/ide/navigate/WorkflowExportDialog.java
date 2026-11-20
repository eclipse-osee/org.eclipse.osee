/*********************************************************************
 * Copyright (c) 2026 Boeing
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

package org.eclipse.osee.ats.ide.navigate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.window.Window;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.program.IAtsProgram;
import org.eclipse.osee.ats.api.sysml.WorkflowExportData;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.api.version.IAtsVersion;
import org.eclipse.osee.ats.api.version.VersionLockedType;
import org.eclipse.osee.ats.api.version.VersionReleaseType;
import org.eclipse.osee.ats.ide.util.widgets.dialog.TeamDefinitionDialog;
import org.eclipse.osee.ats.ide.util.widgets.dialog.VersionListDialog;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.jdk.core.type.NamedId;
import org.eclipse.osee.framework.ui.plugin.util.ArrayTreeContentProvider;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.FilteredTreeDialog;

/**
 * Prompts the user to select the options for a SysML export: a Program (required) and, optionally, a Team Definition,
 * workflow Artifact Type and Version to filter the exported workflows by. Returns a populated {@link WorkflowExportData}
 * or {@code null} if the user cancelled before selecting a program.
 *
 * @author Donald G. Dunne
 */
public class WorkflowExportDialog {

   private final AtsApi atsApi;

   public WorkflowExportDialog(AtsApi atsApi) {
      this.atsApi = atsApi;
   }

   /**
    * @return a populated WorkflowExportData, or null if the user cancelled program selection.
    */
   public WorkflowExportData openAndGet() {
      IAtsProgram program = selectProgram();
      if (program == null) {
         return null;
      }

      WorkflowExportData exportData = new WorkflowExportData();
      exportData.setProgramId(ArtifactId.valueOf(program.getId()));

      IAtsTeamDefinition teamDef = selectTeamDefinition(program);
      if (teamDef != null) {
         exportData.setTeamDefId(ArtifactId.valueOf(teamDef.getId()));
      }

      ArtifactTypeToken artType = selectArtifactType();
      if (artType != null) {
         exportData.setArtifactType(artType);
      }

      IAtsVersion version = selectVersion(teamDef);
      if (version != null) {
         exportData.setVersionId(ArtifactId.valueOf(version.getId()));
      }

      return exportData;
   }

   private IAtsProgram selectProgram() {
      FilteredTreeDialog dialog = new FilteredTreeDialog("Select Program",
         "Select the Program to export (required).", new ArrayTreeContentProvider(), new AtsObjectLabelProvider());
      dialog.setInput(atsApi.getProgramService().getPrograms());
      dialog.setMultiSelect(false);
      if (dialog.open() == Window.OK) {
         return dialog.getSelectedFirst();
      }
      return null;
   }

   private IAtsTeamDefinition selectTeamDefinition(IAtsProgram program) {
      Collection<IAtsTeamDefinition> teamDefs = atsApi.getProgramService().getTeamDefs(program);
      if (teamDefs.isEmpty()) {
         return null;
      }
      TeamDefinitionDialog dialog =
         new TeamDefinitionDialog("Select Team Definition", "Optional: select a Team Definition to filter by (Cancel to skip).");
      dialog.setInput(teamDefs);
      dialog.setMultiSelect(false);
      if (dialog.open() == Window.OK) {
         return dialog.getSelectedFirst();
      }
      return null;
   }

   private ArtifactTypeToken selectArtifactType() {
      List<ArtifactTypeToken> teamWfTypes = new ArrayList<>();
      for (ArtifactTypeToken artType : atsApi.tokenService().getArtifactTypes()) {
         if (artType.inheritsFrom(AtsArtifactTypes.TeamWorkflow)) {
            teamWfTypes.add(artType);
         }
      }
      if (teamWfTypes.isEmpty()) {
         return null;
      }
      FilteredTreeDialog dialog = new FilteredTreeDialog("Select Artifact Type",
         "Optional: select a workflow Artifact Type to filter by (Cancel to skip).", new ArrayTreeContentProvider(),
         new AtsObjectLabelProvider());
      dialog.setInput(teamWfTypes);
      dialog.setMultiSelect(false);
      if (dialog.open() == Window.OK) {
         return dialog.getSelectedFirst();
      }
      return null;
   }

   private IAtsVersion selectVersion(IAtsTeamDefinition teamDef) {
      if (teamDef == null) {
         return null;
      }
      Collection<IAtsVersion> versions = atsApi.getVersionService().getVersionsFromTeamDefHoldingVersions(teamDef,
         VersionReleaseType.Both, VersionLockedType.Both);
      if (versions.isEmpty()) {
         return null;
      }
      VersionListDialog dialog = new VersionListDialog("Select Version",
         "Optional: select a Version to filter by (Cancel to skip).", versions);
      if (dialog.open() == Window.OK) {
         return dialog.getSelectedFirst();
      }
      return null;
   }

   /**
    * Label provider that renders ATS objects and ArtifactTypeTokens by name.
    */
   private static class AtsObjectLabelProvider extends LabelProvider {
      @Override
      public String getText(Object element) {
         if (element instanceof NamedId) {
            return ((NamedId) element).getName();
         }
         return String.valueOf(element);
      }
   }
}
