/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/

package org.eclipse.osee.ats.ide.world.search;

import java.util.ArrayList;
import java.util.Collection;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.api.version.IAtsVersion;
import org.eclipse.osee.ats.api.version.VersionLockedType;
import org.eclipse.osee.ats.api.version.VersionReleaseType;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.core.config.TeamDefinitions;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.ats.ide.internal.AtsClientService;
import org.eclipse.osee.ats.ide.util.widgets.dialog.TeamDefinitionDialog;
import org.eclipse.osee.ats.ide.util.widgets.dialog.VersionListDialog;
import org.eclipse.osee.ats.ide.workflow.action.ActionArtifact;
import org.eclipse.osee.ats.ide.workflow.teamwf.TeamWorkFlowArtifact;
import org.eclipse.osee.framework.core.enums.Active;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;

/**
 * @author Donald G. Dunne
 */
public class VersionTargetedForTeamSearchItem extends WorldUISearchItem {
   private final IAtsVersion versionArt;
   private IAtsVersion selectedVersionArt;
   private final boolean returnAction;
   private final IAtsTeamDefinition teamDef;

   public VersionTargetedForTeamSearchItem(IAtsTeamDefinition teamDef, IAtsVersion versionArt, boolean returnAction, LoadView loadView) {
      this(null, teamDef, versionArt, returnAction, loadView);
   }

   public VersionTargetedForTeamSearchItem(String name, IAtsTeamDefinition teamDef, IAtsVersion versionArt, boolean returnAction, LoadView loadView) {
      super(name != null ? name : (returnAction ? "Actions" : "Workflows") + " Targeted-For Version", loadView,
         FrameworkImage.VERSION);
      this.teamDef = teamDef;
      this.versionArt = versionArt;
      this.returnAction = returnAction;
   }

   public VersionTargetedForTeamSearchItem(VersionTargetedForTeamSearchItem versionTargetedForTeamSearchItem) {
      super(versionTargetedForTeamSearchItem, FrameworkImage.VERSION);
      this.versionArt = versionTargetedForTeamSearchItem.versionArt;
      this.returnAction = versionTargetedForTeamSearchItem.returnAction;
      this.teamDef = versionTargetedForTeamSearchItem.teamDef;
   }

   @Override
   public String getSelectedName(SearchType searchType) {
      if (getSearchVersionArtifact() != null) {
         return super.getName() + " - " + getSearchVersionArtifact();
      }
      return "";
   }

   public IAtsVersion getSearchVersionArtifact() {
      if (versionArt != null) {
         return versionArt;
      }
      return selectedVersionArt;
   }

   @Override
   public Collection<Artifact> performSearch(SearchType searchType) {

      if (getSearchVersionArtifact() == null) {
         throw new OseeArgumentException("Invalid release version");
      }

      ArrayList<Artifact> arts = new ArrayList<>();
      for (IAtsTeamWorkflow team : AtsClientService.get().getVersionService().getTargetedForTeamWorkflows(
         getSearchVersionArtifact())) {
         if (returnAction) {
            ActionArtifact parentAction = ((TeamWorkFlowArtifact) team.getStoreObject()).getParentActionArtifact();
            if (parentAction != null) {
               arts.add(parentAction);
            }
         } else {
            arts.add((TeamWorkFlowArtifact) team.getStoreObject());
         }
      }
      if (isCancelled()) {
         return EMPTY_SET;
      }
      return arts;
   }

   @Override
   public void performUI(SearchType searchType) {
      super.performUI(searchType);
      if (searchType == SearchType.ReSearch && selectedVersionArt != null) {
         return;
      }
      if (versionArt != null) {
         return;
      }
      try {
         IAtsTeamDefinition selectedTeamDef = teamDef;
         if (versionArt == null && selectedTeamDef == null) {
            TeamDefinitionDialog dialog = new TeamDefinitionDialog();
            dialog.setInput(
               TeamDefinitions.getTeamReleaseableDefinitions(Active.Both, AtsClientService.get().getQueryService()));
            int result = dialog.open();
            if (result == 0) {
               selectedTeamDef = dialog.getSelectedFirst();
            } else {
               cancelled = true;
            }
         }
         if (versionArt == null && selectedTeamDef != null) {
            final VersionListDialog dialog = new VersionListDialog("Select Version", "Select Version",
               AtsClientService.get().getVersionService().getVersions(selectedTeamDef, VersionReleaseType.Both,
                  VersionLockedType.Both));
            if (dialog.open() == 0) {
               selectedVersionArt = dialog.getSelectedFirst();
               return;
            }
         }
      } catch (Exception ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }
      cancelled = true;
   }

   @Override
   public WorldUISearchItem copy() {
      return new VersionTargetedForTeamSearchItem(this);
   }

}
