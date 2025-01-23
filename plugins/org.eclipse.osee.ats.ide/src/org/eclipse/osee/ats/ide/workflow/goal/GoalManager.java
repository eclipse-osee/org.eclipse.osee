/*********************************************************************
 * Copyright (c) 2010 Boeing
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

package org.eclipse.osee.ats.ide.workflow.goal;

import java.util.Collection;
import org.eclipse.jface.window.Window;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.RelationTypeSide;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.plugin.util.ArrayTreeContentProvider;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.FilteredTreeArtifactDialog;

/**
 * @author Donald G. Dunne
 */
public class GoalManager extends MembersManager<GoalArtifact> {

   /**
    * change goal, prompt if member of two goals
    */
   public GoalArtifact promptChangeGoalOrder(Artifact artifact) {
      if (!isHasCollector(artifact)) {
         AWorkbench.popup(String.format("No Goal set for artifact [%s]", artifact));
         return null;
      }
      Collection<Artifact> goals = getCollectors(artifact, false);
      GoalArtifact goal = null;
      if (goals.size() == 1) {
         goal = (GoalArtifact) goals.iterator().next();
      } else if (goals.size() > 1) {
         FilteredTreeArtifactDialog dialog =
            new FilteredTreeArtifactDialog("Select Goal", "Artifact has multiple Goals\n\nSelect Goal to change order",
               goals, new ArrayTreeContentProvider(), new MembersLabelProvider(), new MembersViewerSorter());
         dialog.setMultiSelect(false);
         if (dialog.open() == Window.OK) {
            goal = (GoalArtifact) dialog.getSelectedFirst();
         } else {
            return null;
         }
      }
      return promptChangeMemberOrder(goal, artifact);
   }

   @Override
   public RelationTypeSide getMembersRelationTypeSide() {
      return AtsRelationTypes.Goal_Member;
   }

   @Override
   public String getItemName() {
      return "Goal";
   }

   @Override
   public ArtifactTypeToken getArtifactType() {
      return AtsArtifactTypes.Goal;
   }

   @Override
   public String getMemberOrder(GoalArtifact goalArt, Artifact member) {
      return AtsApiService.get().getGoalMembersCache().getMemberOrder(goalArt, member);
   }

}
