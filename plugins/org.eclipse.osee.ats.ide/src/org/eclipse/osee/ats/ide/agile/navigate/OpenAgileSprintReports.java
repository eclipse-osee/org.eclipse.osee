/*********************************************************************
 * Copyright (c) 2017 Boeing
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

package org.eclipse.osee.ats.ide.agile.navigate;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import org.eclipse.jface.window.Window;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.api.util.AtsImage;
import org.eclipse.osee.ats.ide.agile.XOpenSprintReportsButton;
import org.eclipse.osee.ats.ide.agile.navigate.OpenAgileSprint.SprintArtifactLabelProvider;
import org.eclipse.osee.ats.ide.agile.navigate.OpenAgileSprint.SprintStateTypeComparator;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateComposite.TableLoadOption;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItemAction;
import org.eclipse.osee.framework.ui.skynet.ArtifactLabelProvider;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.ArtifactTreeContentProvider;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.FilteredTreeArtifactDialog;

/**
 * @author Donald G. Dunne
 */
public class OpenAgileSprintReports extends XNavigateItemAction {

   public OpenAgileSprintReports() {
      super("Open Agile Sprint Reports", AtsImage.REPORT, AgileNavigateItemProvider.AGILE_REPORTS);
   }

   @Override
   public void run(TableLoadOption... tableLoadOptions) {

      List<Artifact> activeTeams = new LinkedList<>();
      for (Artifact agTeam : ArtifactQuery.getArtifactListFromType(AtsArtifactTypes.AgileTeam,
         AtsApiService.get().getAtsBranch())) {
         if (agTeam.getSoleAttributeValue(AtsAttributeTypes.Active, true)) {
            activeTeams.add(agTeam);
         }
      }
      FilteredTreeArtifactDialog dialog = new FilteredTreeArtifactDialog(getName(), "Select Agile Team", activeTeams,
         new ArtifactTreeContentProvider(), new ArtifactLabelProvider());
      if (dialog.open() == Window.OK) {
         Artifact agileTeamArt = dialog.getSelectedFirst();
         Collection<Artifact> sprints =
            Collections.castAll(AtsApiService.get().getRelationResolver().getRelated(agileTeamArt,
               AtsRelationTypes.AgileTeamToSprint_Sprint));

         FilteredTreeArtifactDialog dialog2 = new FilteredTreeArtifactDialog(getName(), "Select Agile Team", sprints,
            new ArtifactTreeContentProvider(), new SprintArtifactLabelProvider(), new SprintStateTypeComparator());
         if (dialog2.open() == 0) {
            Collection<Artifact> selected = dialog2.getSelected();
            XOpenSprintReportsButton stored = new XOpenSprintReportsButton();
            stored.setArtifact(selected.iterator().next());
            stored.openExternally();
         }
      }
   }

}
