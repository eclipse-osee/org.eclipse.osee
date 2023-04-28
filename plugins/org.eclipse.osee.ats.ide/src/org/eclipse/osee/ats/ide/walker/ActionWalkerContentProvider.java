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

package org.eclipse.osee.ats.ide.walker;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.osee.ats.api.agile.IAgileSprint;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.workflow.IAtsAction;
import org.eclipse.osee.ats.api.workflow.IAtsTask;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.ats.ide.workflow.AbstractWorkflowArtifact;
import org.eclipse.osee.ats.ide.workflow.goal.GoalArtifact;
import org.eclipse.osee.ats.ide.workflow.review.ReviewManager;
import org.eclipse.osee.ats.ide.workflow.teamwf.TeamWorkFlowArtifact;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.zest.core.viewers.IGraphEntityContentProvider;

/**
 * @author Donald G. Dunne
 */
public class ActionWalkerContentProvider implements IGraphEntityContentProvider {
   // private static final Collection<Artifact>EMPTY_LIST = new ArrayList<>(0);

   private final ActionWalkerView view;

   public ActionWalkerContentProvider(ActionWalkerView view) {
      super();
      this.view = view;
   }

   private boolean isTopArtifactGoal() {
      return view.getTopAtsArt() instanceof GoalArtifact;
   }

   @Override
   public Object[] getElements(Object entity) {
      List<Object> objs = new ArrayList<>(5);
      try {
         if (!isTopArtifactGoal() && entity instanceof IAtsAction) {
            objs.add(entity);
            objs.addAll(AtsApiService.get().getWorkItemServiceIde().getTeams(entity));
         } else if (!isTopArtifactGoal() && entity instanceof TeamWorkFlowArtifact) {
            objs.add(entity);
            TeamWorkFlowArtifact teamArt = (TeamWorkFlowArtifact) entity;
            if (!view.isShowAll() && AtsApiService.get().getTaskService().getTasks(teamArt).size() > 0) {
               TaskWrapper taskWrapper = new TaskWrapper(teamArt);
               objs.add(taskWrapper);
               AbstractWorkflowArtifact activeAwa = view.getActiveAwa();
               if (activeAwa instanceof IAtsTask) {
                  IAtsTask task = (IAtsTask) activeAwa;
                  if (AtsApiService.get().getTaskService().getTasks(teamArt).contains(task)) {
                     view.setActiveGraphItem(taskWrapper);
                  }
               }
            } else {
               objs.addAll(AtsApiService.get().getTaskService().getTasks((TeamWorkFlowArtifact) entity));
            }
            if (!view.isShowAll() && ReviewManager.getReviews(teamArt).size() > 0) {
               ReviewWrapper reviewWrapper = new ReviewWrapper(teamArt);
               objs.add(reviewWrapper);
               if (ReviewManager.getReviews(teamArt).contains(view.getActiveAwa())) {
                  view.setActiveGraphItem(reviewWrapper);
               }
            } else {
               objs.addAll(ReviewManager.getReviews((TeamWorkFlowArtifact) entity));
            }
         } else if (entity instanceof GoalArtifact) {
            objs.add(entity);
            GoalArtifact goal = (GoalArtifact) entity;
            if (!view.isShowAll() && goal.getMembers().size() > 0) {
               objs.add(new GoalMemberWrapper(goal));
            } else {
               objs.addAll(goal.getMembers());
            }
         } else if (entity instanceof Artifact && AtsApiService.get().getQueryServiceIde().getArtifact(entity).isOfType(
            AtsArtifactTypes.AgileSprint)) {
            objs.add(entity);
            IAgileSprint sprint = AtsApiService.get().getWorkItemService().getAgileSprint(
               AtsApiService.get().getQueryServiceIde().getArtifact(entity));
            if (!view.isShowAll() && AtsApiService.get().getAgileService().getItems(sprint).size() > 0) {
               objs.add(new SprintMemberWrapper(sprint));
            } else {
               objs.addAll(AtsApiService.get().getAgileService().getItems(sprint));
            }
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
      return objs.toArray();
   }

   @Override
   public Object[] getConnectedTo(Object inputElement) {
      try {
         if (!isTopArtifactGoal() && inputElement instanceof IAtsAction) {
            return AtsApiService.get().getWorkItemServiceIde().getTeams(inputElement).toArray();

         } else if (inputElement instanceof TeamWorkFlowArtifact) {
            List<Object> objs = new ArrayList<>(5);
            if (!isTopArtifactGoal()) {
               TeamWorkFlowArtifact teamArt = (TeamWorkFlowArtifact) inputElement;
               if (!view.isShowAll() && ReviewManager.getReviews(teamArt).size() > 0) {
                  ReviewWrapper reviewWrapper = new ReviewWrapper(teamArt);
                  objs.add(reviewWrapper);
                  if (ReviewManager.getReviews(teamArt).contains(view.getActiveAwa())) {
                     view.setActiveGraphItem(reviewWrapper);
                  }
               } else {
                  objs.addAll(ReviewManager.getReviews(teamArt));
               }
               if (!view.isShowAll() && AtsApiService.get().getTaskService().getTasks(teamArt).size() > 0) {
                  TaskWrapper taskWrapper = new TaskWrapper(teamArt);
                  objs.add(taskWrapper);
                  AbstractWorkflowArtifact activeAwa = view.getActiveAwa();
                  if (activeAwa instanceof IAtsTask) {
                     IAtsTask task = (IAtsTask) activeAwa;
                     if (AtsApiService.get().getTaskService().getTasks(teamArt).contains(task)) {
                        view.setActiveGraphItem(taskWrapper);
                     }
                  }
               } else {
                  objs.addAll(AtsApiService.get().getTaskService().getTasks(teamArt));
               }
            }
            return objs.toArray();
         } else if (inputElement instanceof GoalArtifact) {
            List<Object> objs = new ArrayList<>(5);
            GoalArtifact goal = (GoalArtifact) inputElement;
            if (!view.isShowAll() && goal.getMembers().size() > 0) {
               objs.add(new GoalMemberWrapper(goal));
            } else {
               objs.addAll(goal.getMembers());
            }
            return objs.toArray();
         } else if (inputElement instanceof Artifact && AtsApiService.get().getQueryServiceIde().getArtifact(
            inputElement).isOfType(AtsArtifactTypes.AgileSprint)) {
            List<Object> objs = new ArrayList<>(5);
            IAgileSprint sprint = AtsApiService.get().getWorkItemService().getAgileSprint(
               AtsApiService.get().getQueryServiceIde().getArtifact(inputElement));
            if (!view.isShowAll() && AtsApiService.get().getAgileService().getItems(sprint).size() > 0) {
               objs.add(new SprintMemberWrapper(sprint));
            } else {
               objs.addAll(AtsApiService.get().getAgileService().getItems(sprint));
            }
            return objs.toArray();
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
      return new Object[] {};
   }

   @Override
   public void dispose() {
      // do nothing
   }

   @Override
   public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
      // do nothing
   }

}
