/*******************************************************************************
 * Copyright (c) 2019 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.ide.workflow.goal;

import java.util.Collection;
import java.util.List;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.workflow.IAtsGoal;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.core.workflow.util.IDuplicateWorkflowListener;
import org.eclipse.osee.ats.ide.actions.CloneWorkflowAction;
import org.eclipse.osee.ats.ide.actions.ISelectedAtsArtifacts;
import org.eclipse.osee.ats.ide.editor.tab.members.IMemberProvider;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.ats.ide.internal.AtsClientService;
import org.eclipse.osee.ats.ide.workflow.CollectorArtifact;
import org.eclipse.osee.ats.ide.workflow.sprint.SprintArtifact;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.swt.ImageManager;

/**
 * @author Donald G. Dunne
 */
public class CloneActionToGoalAction extends Action {

   private final CollectorArtifact collectorArt;
   private final ISelectedAtsArtifacts selectedAtsArtifacts;
   private final IMemberProvider memberProvider;

   public static interface RemovedFromCollectorHandler {
      void removedFromCollector(Collection<? extends Artifact> removed);
   }

   public CloneActionToGoalAction(IMemberProvider memberProvider, CollectorArtifact collectorArt, ISelectedAtsArtifacts selectedAtsArtifacts) {
      super("Clone Selected Action At This Location");
      this.memberProvider = memberProvider;
      this.collectorArt = collectorArt;
      this.selectedAtsArtifacts = selectedAtsArtifacts;
   }

   @Override
   public ImageDescriptor getImageDescriptor() {
      return ImageManager.getImageDescriptor(FrameworkImage.DUPLICATE);
   }

   @Override
   public void run() {
      try {
         Collection<? extends Artifact> selected = selectedAtsArtifacts.getSelectedAtsArtifacts();
         final Artifact dropTarget = selected.isEmpty() ? null : selected.iterator().next();
         IAtsTeamWorkflow teamWf = null;
         if (!(dropTarget instanceof IAtsTeamWorkflow)) {
            AWorkbench.popup("Can only clone workflow");
            return;
         }
         teamWf = (IAtsTeamWorkflow) dropTarget;

         CloneWorkflowAction cloneAction = new CloneWorkflowAction(teamWf, new IDuplicateWorkflowListener() {

            @Override
            public IAtsGoal addToGoal(IAtsTeamWorkflow teamWf, IAtsChangeSet changes) {
               IAtsGoal goal = null;

               List<Artifact> related = collectorArt.getRelatedArtifacts(memberProvider.getMemberRelationTypeSide());
               if (!related.contains(teamWf.getStoreObject())) {
                  changes.relate(collectorArt, memberProvider.getMemberRelationTypeSide(), teamWf.getStoreObject());
               }
               collectorArt.setRelationOrder(memberProvider.getMemberRelationTypeSide(), dropTarget, false,
                  AtsClientService.get().getQueryServiceClient().getArtifact(teamWf));
               if (collectorArt.isOfType(AtsArtifactTypes.Goal)) {
                  AtsClientService.get().getGoalMembersCache().decache((GoalArtifact) collectorArt);
               } else if (memberProvider.isSprint()) {
                  AtsClientService.get().getSprintItemsCache().decache((SprintArtifact) collectorArt);
               }
               changes.add(collectorArt);

               return goal;
            }

         });

         cloneAction.run();

      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }
   }

   public void refreshText() {
      setText(getText());
   }
}
