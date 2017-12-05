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
package org.eclipse.osee.ats.agile.navigate;

import java.util.LinkedList;
import java.util.List;
import org.eclipse.osee.ats.AtsArtifactImageProvider;
import org.eclipse.osee.ats.AtsOpenOption;
import org.eclipse.osee.ats.api.data.AtsArtifactImages;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.internal.AtsClientService;
import org.eclipse.osee.ats.util.AtsUtil;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateComposite.TableLoadOption;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItem;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItemAction;
import org.eclipse.osee.framework.ui.skynet.ArtifactLabelProvider;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.ArtifactTreeContentProvider;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.FilteredTreeArtifactDialog;

/**
 * @author Donald G. Dunne
 */
public class OpenAgileBacklog extends XNavigateItemAction {

   public OpenAgileBacklog(XNavigateItem parent) {
      super(parent, "Open Agile Backlog", AtsArtifactImageProvider.getKeyedImage(AtsArtifactImages.AGILE_BACKLOG));
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
         Artifact backlog = agileTeamArt.getRelatedArtifactOrNull(AtsRelationTypes.AgileTeamToBacklog_Backlog);
         if (backlog == null) {
            AWorkbench.popup("No backlog set for team %s", agileTeamArt.toStringWithId());
         } else {
            AtsUtil.openATSAction(backlog, AtsOpenOption.OpenAll);
         }
      }
   }

}
