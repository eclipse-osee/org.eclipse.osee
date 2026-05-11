/*******************************************************************************
 * Copyright (c) 2026 Boeing.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.ide.util.widgets;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.jface.window.Window;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.widgets.XHyperlinkLabelValueSelection;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.FilteredTreeArtifactDialog;
import org.eclipse.osee.framework.ui.swt.Widgets;

/**
 * @author Donald G. Dunne
 */
public class XHyperlinkPrBuildSelection extends XHyperlinkLabelValueSelection {

   public static final String LABEL = "Previous PRs";
   private static final String OPEN_PRS = "Open PR Lists";
   private ArtifactToken selected = ArtifactToken.SENTINEL;
   private IAtsTeamDefinition teamDef;

   public XHyperlinkPrBuildSelection() {
      this(LABEL);
   }

   public XHyperlinkPrBuildSelection(String label) {
      super(label);
   }

   public ArtifactToken getSelected() {
      return selected;
   }

   @Override
   public String getCurrentValue() {
      if (selected.isInvalid()) {
         return Widgets.NOT_SET;
      }
      return selected.getName();
   }

   @Override
   public boolean handleSelection() {
      if (teamDef == null) {
         AWorkbench.popup("Team Definition must be selected");
         return false;
      }

      Artifact openPrsFolder = null;

      @Nullable
      Artifact artifact = (Artifact) AtsApiService.get().getQueryService().getArtifact(teamDef.getId());
      for (Artifact child : artifact.getChildren()) {
         if (child.getName().contains(OPEN_PRS)) {
            openPrsFolder = child;
            break;
         }
      }
      if (openPrsFolder == null) {
         AWorkbench.popup("Can't find open PRs folder");
         return false;
      }

      List<Artifact> openPrsArts = new ArrayList<>();
      for (Artifact child : openPrsFolder.getChildren()) {
         if (child.isOfType(CoreArtifactTypes.EnumeratedArtifact)) {
            openPrsArts.add(child);
         }
      }

      if (openPrsArts.isEmpty()) {
         AWorkbench.popup("No Enumerated Artifacts under folder %s", openPrsFolder.toStringWithId());
         return false;
      }

      FilteredTreeArtifactDialog dialog = new FilteredTreeArtifactDialog("Select " + LABEL, openPrsArts);
      boolean changed = false;
      if (dialog.open() == Window.OK) {
         selected = dialog.getSelectedFirst();
         return true;
      }
      return changed;
   }

   public IAtsTeamDefinition getTeamDef() {
      return teamDef;
   }

   public void setTeamDef(IAtsTeamDefinition teamDef) {
      this.teamDef = teamDef;
   }

   public void set(ArtifactToken art) {
      if (art == null) {
         selected = ArtifactToken.SENTINEL;
      } else {
         selected = art;
      }
      refresh();
   }

   public void clear() {
      selected = ArtifactToken.SENTINEL;
      refresh();
   }

   public ArtifactToken getToken() {
      return selected;
   }

}
