/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/

package org.eclipse.osee.ats.walker;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.osee.ats.api.agile.IAgileSprint;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.core.client.action.ActionArtifact;
import org.eclipse.osee.ats.core.client.artifact.GoalArtifact;
import org.eclipse.osee.ats.core.client.review.ReviewManager;
import org.eclipse.osee.ats.core.client.team.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.internal.Activator;
import org.eclipse.osee.ats.internal.AtsClientService;
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
         if (!isTopArtifactGoal() && entity instanceof ActionArtifact) {
            objs.add(entity);
            objs.addAll(((ActionArtifact) entity).getTeams());
         } else if (!isTopArtifactGoal() && entity instanceof TeamWorkFlowArtifact) {
            objs.add(entity);
            TeamWorkFlowArtifact teamArt = (TeamWorkFlowArtifact) entity;
            if (!view.isShowAll() && teamArt.getTaskArtifacts().size() > 8) {
               TaskWrapper taskWrapper = new TaskWrapper(teamArt);
               objs.add(taskWrapper);
               if (teamArt.getTaskArtifacts().contains(view.getActiveAwa())) {
                  view.setActiveGraphItem(taskWrapper);
               }
            } else {
               objs.addAll(((TeamWorkFlowArtifact) entity).getTaskArtifacts());
            }
            if (!view.isShowAll() && ReviewManager.getReviews(teamArt).size() > 4) {
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
            if (!view.isShowAll() && goal.getMembers().size() > 10) {
               objs.add(new GoalMemberWrapper(goal));
            } else {
               objs.addAll(goal.getMembers());
            }
         } else if (entity instanceof Artifact && ((Artifact) entity).isOfType(AtsArtifactTypes.AgileSprint)) {
            objs.add(entity);
            IAgileSprint sprint = AtsClientService.get().getWorkItemFactory().getAgileSprint((Artifact) entity);
            if (!view.isShowAll() && AtsClientService.get().getAgileService().getItems(sprint).size() > 10) {
               objs.add(new SprintMemberWrapper(sprint));
            } else {
               objs.addAll(AtsClientService.get().getAgileService().getItems(sprint));
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
         if (!isTopArtifactGoal() && inputElement instanceof ActionArtifact) {
            return ((ActionArtifact) inputElement).getTeams().toArray();
         } else if (inputElement instanceof TeamWorkFlowArtifact) {
            List<Object> objs = new ArrayList<>(5);
            if (!isTopArtifactGoal()) {
               TeamWorkFlowArtifact teamArt = (TeamWorkFlowArtifact) inputElement;
               if (!view.isShowAll() && ReviewManager.getReviews(teamArt).size() > 4) {
                  ReviewWrapper reviewWrapper = new ReviewWrapper(teamArt);
                  objs.add(reviewWrapper);
                  if (ReviewManager.getReviews(teamArt).contains(view.getActiveAwa())) {
                     view.setActiveGraphItem(reviewWrapper);
                  }
               } else {
                  objs.addAll(ReviewManager.getReviews(teamArt));
               }
               if (!view.isShowAll() && teamArt.getTaskArtifacts().size() > 8) {
                  TaskWrapper taskWrapper = new TaskWrapper(teamArt);
                  objs.add(taskWrapper);
                  if (teamArt.getTaskArtifacts().contains(view.getActiveAwa())) {
                     view.setActiveGraphItem(taskWrapper);
                  }
               } else {
                  objs.addAll(teamArt.getTaskArtifacts());
               }
            }
            return objs.toArray();
         } else if (inputElement instanceof GoalArtifact) {
            List<Object> objs = new ArrayList<>(5);
            GoalArtifact goal = (GoalArtifact) inputElement;
            if (!view.isShowAll() && goal.getMembers().size() > 10) {
               objs.add(new GoalMemberWrapper(goal));
            } else {
               objs.addAll(goal.getMembers());
            }
            return objs.toArray();
         } else if (inputElement instanceof Artifact && ((Artifact) inputElement).isOfType(
            AtsArtifactTypes.AgileSprint)) {
            List<Object> objs = new ArrayList<>(5);
            IAgileSprint sprint = AtsClientService.get().getWorkItemFactory().getAgileSprint((Artifact) inputElement);
            if (!view.isShowAll() && AtsClientService.get().getAgileService().getItems(sprint).size() > 10) {
               objs.add(new SprintMemberWrapper(sprint));
            } else {
               objs.addAll(AtsClientService.get().getAgileService().getItems(sprint));
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
