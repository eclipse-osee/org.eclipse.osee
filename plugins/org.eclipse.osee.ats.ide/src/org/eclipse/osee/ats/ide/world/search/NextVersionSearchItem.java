/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
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

package org.eclipse.osee.ats.ide.world.search;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.api.util.AtsImage;
import org.eclipse.osee.ats.api.version.IAtsVersion;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.ats.ide.util.widgets.dialog.TeamDefinitionDialog;
import org.eclipse.osee.framework.core.enums.Active;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;

/**
 * @author Donald G. Dunne
 */
public class NextVersionSearchItem extends WorldUISearchItem {

   private final IAtsTeamDefinition teamDefHoldingVersions;
   private IAtsTeamDefinition selectedTeamDef;
   private IAtsVersion selectedVersionArt;

   public NextVersionSearchItem(IAtsTeamDefinition teamDefHoldingVersions, LoadView loadView) {
      this(null, teamDefHoldingVersions, loadView);
   }

   public NextVersionSearchItem(String name, IAtsTeamDefinition teamDefHoldingVersions, LoadView loadView) {
      super(name != null ? name : "Workflows Targeted-For Next Version", loadView, AtsImage.VERSION_NEXT);
      this.teamDefHoldingVersions = teamDefHoldingVersions;
   }

   public NextVersionSearchItem(NextVersionSearchItem nextVersionSearchItem) {
      super(nextVersionSearchItem, AtsImage.VERSION);
      this.teamDefHoldingVersions = nextVersionSearchItem.teamDefHoldingVersions;
      this.selectedTeamDef = nextVersionSearchItem.selectedTeamDef;
   }

   @Override
   public String getSelectedName(SearchType searchType) {
      String name = super.getName();
      IAtsTeamDefinition teamDef = getTeamDefinition();
      try {
         if (teamDef != null) {
            name += " - " + teamDef.getName();
            selectedVersionArt = AtsApiService.get().getVersionService().getNextReleaseVersion(teamDef);
            name += selectedVersionArt != null ? " - " + selectedVersionArt.getName() : "";
         }
      } catch (Exception ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
         return "Exception Occurred - See Log - " + ex.getLocalizedMessage();
      }
      return name;
   }

   private IAtsTeamDefinition getTeamDefinition() {
      if (teamDefHoldingVersions != null) {
         return teamDefHoldingVersions;
      }
      return selectedTeamDef;
   }

   @Override
   public Collection<Artifact> performSearch(SearchType searchType) {
      if (isCancelled()) {
         return EMPTY_SET;
      }
      if (AtsApiService.get().getVersionService().getNextReleaseVersion(getTeamDefinition()) == null) {
         AWorkbench.popup("ERROR", "No version marked as Next Release for \"" + getTeamDefinition() + "\"");
         return EMPTY_SET;
      }
      List<Artifact> arts = new ArrayList<>();
      List<Artifact> castAll = Collections.castAll(AtsApiService.get().getVersionService().getTargetedForTeamWorkflows(
         AtsApiService.get().getVersionService().getNextReleaseVersion(getTeamDefinition())));
      arts.addAll(castAll);
      if (isCancelled()) {
         return EMPTY_SET;
      }
      return arts;
   }

   @Override
   public void performUI(SearchType searchType) {
      super.performUI(searchType);
      if (teamDefHoldingVersions != null) {
         return;
      }
      if (searchType == SearchType.ReSearch && selectedTeamDef != null) {
         return;
      }
      try {
         TeamDefinitionDialog dialog = new TeamDefinitionDialog();
         dialog.setInput(AtsApiService.get().getTeamDefinitionService().getTeamReleaseableDefinitions(Active.Active));
         int result = dialog.open();
         if (result == 0) {
            selectedTeamDef = dialog.getSelectedFirst();
            return;
         } else {
            cancelled = true;
         }
      } catch (Exception ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }
      cancelled = true;
   }

   @Override
   public WorldUISearchItem copy() {
      return new NextVersionSearchItem(this);
   }

   public IAtsVersion getSelectedVersionArt() {
      return selectedVersionArt;
   }

}
