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

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.osee.ats.AtsArtifactImageProvider;
import org.eclipse.osee.ats.AtsOpenOption;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.data.AtsArtifactImages;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.api.workdef.StateType;
import org.eclipse.osee.ats.internal.AtsClientService;
import org.eclipse.osee.ats.util.AtsEditors;
import org.eclipse.osee.ats.workflow.sprint.SprintArtifact;
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
public class OpenAgileSprint extends XNavigateItemAction {

   public OpenAgileSprint(XNavigateItem parent) {
      super(parent, "Open Agile Sprint", AtsArtifactImageProvider.getKeyedImage(AtsArtifactImages.AGILE_SPRINT));
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
            if (selected.size() == 1) {
               AtsEditors.openATSAction(selected.iterator().next(), AtsOpenOption.OpenAll);
            } else {
               AtsEditors.openInAtsWorldEditor("Sprints", selected);
            }
         }
      }
   }

   public static class SprintStateTypeComparator extends ViewerComparator {

      @Override
      public int compare(Viewer viewer, Object e1, Object e2) {
         if (e1 instanceof SprintArtifact && e2 instanceof SprintArtifact) {
            StateType e1StateType = ((IAtsWorkItem) e1).getStateMgr().getStateType();
            StateType e2StateType = ((IAtsWorkItem) e2).getStateMgr().getStateType();
            if (e1StateType != e2StateType) {
               if (e1StateType.isInWork()) {
                  return -1;
               } else {
                  return 1;
               }
            }
         }
         return super.compare(viewer, e2.toString(), e1.toString());
      }

   }

   public static class SprintArtifactLabelProvider extends ArtifactLabelProvider {

      @Override
      public String getText(Object element) {
         if (element instanceof IAtsWorkItem) {
            return String.format("%s %s", super.getText(element),
               ((IAtsWorkItem) element).isInWork() ? "" : " - (" + ((IAtsWorkItem) element).getStateMgr().getCurrentStateName() + ")");
         }
         return super.getText(element);
      }

   }
}
