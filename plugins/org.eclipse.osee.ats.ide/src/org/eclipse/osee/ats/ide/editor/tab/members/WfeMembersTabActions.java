/*********************************************************************
 * Copyright (c) 2020 Boeing
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
package org.eclipse.osee.ats.ide.editor.tab.members;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IAction;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.core.util.AtsObjects;
import org.eclipse.osee.ats.ide.AtsImage;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.ats.ide.workflow.AbstractWorkflowArtifact;
import org.eclipse.osee.ats.ide.workflow.goal.GoalManager;
import org.eclipse.osee.ats.ide.workflow.review.ReviewManager;
import org.eclipse.osee.ats.ide.workflow.teamwf.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.ide.world.WorldComposite;
import org.eclipse.osee.ats.ide.world.WorldEditor;
import org.eclipse.osee.ats.ide.world.WorldEditorSimpleProvider;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.plugin.core.util.Jobs;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.swt.ImageManager;
import org.eclipse.swt.widgets.Menu;

/**
 * @author Donald G. Dunne
 */
public class WfeMembersTabActions {
   private static Action toTask, toAction, toWorkFlow, toReview, toGoal;
   private final String ACTIONS = "Open as Actions in World Editor";
   private final String GOALS = "Open as Goals in World Editor";
   private final String WORKFLOWS = "Open as WorkFlows in World Editor";
   private final String TASKS = "Open as Tasks in World Editor";
   private final String REVIEWS = "Open as Reviews in World Editor";
   private final WorldComposite worldComposite;

   public WfeMembersTabActions(WfeMembersTab membersTab) {
      this.worldComposite = membersTab.getWorldComposite();
   }

   public void createActions(Menu fMenu) {
      getActionsMenuItem();
      getGoalsMenuItem();
      getWorkFlowsMenuItem();
      getTasksMenuItem();
      getReviewsMenuItem();
      addActionToMenu(fMenu, toAction);
      addActionToMenu(fMenu, toGoal);
      addActionToMenu(fMenu, toWorkFlow);
      addActionToMenu(fMenu, toTask);
      addActionToMenu(fMenu, toReview);
   }

   protected void addActionToMenu(Menu parent, Action action) {
      ActionContributionItem item = new ActionContributionItem(action);
      item.fill(parent, -1);
   }

   public void openAsActions() {
      final List<Artifact> artifacts = worldComposite.getXViewer().getLoadedArtifacts();
      Job job = new Job(ACTIONS) {
         @Override
         protected IStatus run(IProgressMonitor monitor) {
            try {
               final Set<Artifact> arts = new HashSet<>();
               for (Artifact art : artifacts) {
                  if (art.isOfType(AtsArtifactTypes.Action)) {
                     arts.add(art);
                  } else if (art instanceof AbstractWorkflowArtifact) {
                     Artifact parentArt =
                        (Artifact) ((AbstractWorkflowArtifact) art).getParentAction().getStoreObject();
                     if (parentArt != null) {
                        arts.add(parentArt);
                     }
                  }
               }
               WorldEditor.open(new WorldEditorSimpleProvider("Actions", arts));
            } catch (OseeCoreException ex) {
               OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
            }
            return Status.OK_STATUS;
         }
      };
      Jobs.startJob(job, true);
   }

   public void openAsGoals() {
      final List<Artifact> artifacts = worldComposite.getXViewer().getLoadedArtifacts();
      Job job = new Job(GOALS) {
         @Override
         protected IStatus run(IProgressMonitor monitor) {
            try {
               final Set<Artifact> goals = new HashSet<>();
               new GoalManager().getCollectors(artifacts, goals, true);
               WorldEditor.open(new WorldEditorSimpleProvider("Goals", goals));
            } catch (OseeCoreException ex) {
               OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
            }
            return Status.OK_STATUS;
         }
      };
      Jobs.startJob(job, true);
   }

   public void openAsWorkFlows() {
      final List<Artifact> artifacts = worldComposite.getXViewer().getLoadedArtifacts();
      Job job = new Job(WORKFLOWS) {
         @Override
         protected IStatus run(IProgressMonitor monitor) {
            try {
               final Set<Artifact> arts = new HashSet<>();
               for (Artifact art : artifacts) {
                  if (art.isOfType(AtsArtifactTypes.Action)) {
                     arts.addAll(org.eclipse.osee.framework.jdk.core.util.Collections.castAll(
                        AtsObjects.getArtifacts(AtsApiService.get().getWorkItemService().getTeams(art))));
                  } else if (art instanceof AbstractWorkflowArtifact) {
                     Artifact parentArt = (Artifact) ((AbstractWorkflowArtifact) art).getParentTeamWorkflow();
                     if (parentArt != null) {
                        arts.add(parentArt);
                     }
                  }
               }
               WorldEditor.open(new WorldEditorSimpleProvider("WorkFlows", arts));
            } catch (OseeCoreException ex) {
               OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
            }
            return Status.OK_STATUS;
         }
      };
      Jobs.startJob(job, true);
   }

   public void openAsTask() {
      final List<Artifact> artifacts = worldComposite.getXViewer().getLoadedArtifacts();
      Job job = new Job(TASKS) {
         @Override
         protected IStatus run(IProgressMonitor monitor) {
            try {
               final Set<Artifact> arts = new HashSet<>();
               for (Artifact art : artifacts) {
                  if (art.isOfType(AtsArtifactTypes.Action)) {
                     for (IAtsTeamWorkflow team : AtsApiService.get().getWorkItemService().getTeams(art)) {
                        arts.addAll(org.eclipse.osee.framework.jdk.core.util.Collections.castAll(
                           AtsObjects.getArtifacts(AtsApiService.get().getTaskService().getTasks(team))));
                     }
                  } else if (art instanceof TeamWorkFlowArtifact) {
                     arts.addAll(org.eclipse.osee.framework.jdk.core.util.Collections.castAll(AtsObjects.getArtifacts(
                        AtsApiService.get().getTaskService().getTasks((TeamWorkFlowArtifact) art))));
                  }
               }
               WorldEditor.open(new WorldEditorSimpleProvider("Tasks", arts));

            } catch (OseeCoreException ex) {
               OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
            }
            return Status.OK_STATUS;
         }
      };
      Jobs.startJob(job, true);
   }

   public void openAsReview() {
      final List<Artifact> artifacts = worldComposite.getXViewer().getLoadedArtifacts();
      Job job = new Job(REVIEWS) {
         @Override
         protected IStatus run(IProgressMonitor monitor) {
            try {
               final Set<Artifact> arts = new HashSet<>();
               for (Artifact art : artifacts) {
                  if (art.isOfType(AtsArtifactTypes.Action)) {
                     for (IAtsTeamWorkflow team : AtsApiService.get().getWorkItemService().getTeams(art)) {
                        arts.addAll(org.eclipse.osee.framework.jdk.core.util.Collections.castAll(
                           AtsObjects.getArtifacts(AtsApiService.get().getReviewService().getReviews(team))));
                     }
                  } else if (art.isOfType(AtsArtifactTypes.TeamWorkflow)) {
                     arts.addAll(ReviewManager.getReviews((TeamWorkFlowArtifact) art));
                  }
               }
               WorldEditor.open(new WorldEditorSimpleProvider("Reviews", arts));
            } catch (OseeCoreException ex) {
               OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
            }
            return Status.OK_STATUS;
         }
      };
      Jobs.startJob(job, true);
   }

   private Action getActionsMenuItem() {
      toAction = new Action(ACTIONS, IAction.AS_PUSH_BUTTON) {
         @Override
         public void run() {
            openAsActions();
         }
      };
      toAction.setImageDescriptor(ImageManager.getImageDescriptor(AtsImage.ACTION));
      return toAction;
   }

   private Action getGoalsMenuItem() {
      toGoal = new Action(GOALS, IAction.AS_PUSH_BUTTON) {

         @Override
         public void run() {
            openAsGoals();
         }
      };
      toGoal.setImageDescriptor(ImageManager.getImageDescriptor(AtsImage.GOAL));
      return toGoal;
   }

   private Action getWorkFlowsMenuItem() {
      toWorkFlow = new Action(WORKFLOWS, IAction.AS_PUSH_BUTTON) {

         @Override
         public void run() {
            openAsWorkFlows();
         }
      };
      toWorkFlow.setImageDescriptor(ImageManager.getImageDescriptor(FrameworkImage.WORKFLOW));
      return toWorkFlow;
   }

   private Action getTasksMenuItem() {
      toTask = new Action(TASKS, IAction.AS_PUSH_BUTTON) {

         @Override
         public void run() {
            openAsTask();
         }
      };
      toTask.setImageDescriptor(ImageManager.getImageDescriptor(AtsImage.TASK));
      return toTask;
   }

   private Action getReviewsMenuItem() {
      toReview = new Action(REVIEWS, IAction.AS_PUSH_BUTTON) {

         @Override
         public void run() {
            openAsReview();
         }
      };
      toReview.setImageDescriptor(ImageManager.getImageDescriptor(AtsImage.REVIEW));

      return toReview;
   }

   public void createDropDownMenuActions() {
      getActionsMenuItem();
      getGoalsMenuItem();
      getWorkFlowsMenuItem();
      getTasksMenuItem();
      getReviewsMenuItem();
   }
}
