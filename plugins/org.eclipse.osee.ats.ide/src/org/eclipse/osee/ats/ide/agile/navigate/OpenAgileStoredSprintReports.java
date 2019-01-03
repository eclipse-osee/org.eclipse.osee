/*******************************************************************************
 * Copyright (c) 2017 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.ide.agile.navigate;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.ide.AtsImage;
import org.eclipse.osee.ats.ide.agile.XOpenStoredSprintReportsButton;
import org.eclipse.osee.ats.ide.agile.navigate.OpenAgileSprint.SprintArtifactLabelProvider;
import org.eclipse.osee.ats.ide.agile.navigate.OpenAgileSprint.SprintStateTypeComparator;
import org.eclipse.osee.ats.ide.internal.AtsClientService;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateComposite.TableLoadOption;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItem;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItemAction;
import org.eclipse.osee.framework.ui.skynet.ArtifactLabelProvider;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.ArtifactTreeContentProvider;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.FilteredTreeArtifactDialog;

/**
 * @author Donald G. Dunne
 */
public class OpenAgileStoredSprintReports extends XNavigateItemAction {

   public OpenAgileStoredSprintReports(XNavigateItem parent) {
      super(parent, "Open Agile Stored Sprint Reports", AtsImage.REPORT);
   }

   @Override
   public void run(TableLoadOption... tableLoadOptions) {

      List<Artifact> activeTeams = new LinkedList<>();
      for (Artifact agTeam : ArtifactQuery.getArtifactListFromType(AtsArtifactTypes.AgileTeam,
         AtsClientService.get().getAtsBranch())) {
         if (agTeam.getSoleAttributeValue(AtsAttributeTypes.Active, true)) {
            activeTeams.add(agTeam);
         }
      }
      FilteredTreeArtifactDialog dialog = new FilteredTreeArtifactDialog(getName(), "Select Agile Team", activeTeams,
         new ArtifactTreeContentProvider(), new ArtifactLabelProvider());
      if (dialog.open() == 0) {
         Artifact agileTeamArt = dialog.getSelectedFirst();
         Collection<Artifact> sprints =
            Collections.castAll(AtsClientService.get().getRelationResolver().getRelated(agileTeamArt,
               AtsRelationTypes.AgileTeamToSprint_Sprint));

         FilteredTreeArtifactDialog dialog2 = new FilteredTreeArtifactDialog(getName(), "Select Agile Team", sprints,
            new ArtifactTreeContentProvider(), new SprintArtifactLabelProvider(), new SprintStateTypeComparator());
         if (dialog2.open() == 0) {
            Collection<Artifact> selected = dialog2.getSelected();
            XOpenStoredSprintReportsButton stored = new XOpenStoredSprintReportsButton();
            stored.setArtifact(selected.iterator().next());
            stored.openExternally();
         }
      }
   }

}
