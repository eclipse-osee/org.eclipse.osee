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

package org.eclipse.osee.ats.world;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.artifact.ActionArtifact;
import org.eclipse.osee.ats.artifact.GoalArtifact;
import org.eclipse.osee.ats.artifact.ReviewSMArtifact;
import org.eclipse.osee.ats.artifact.StateMachineArtifact;
import org.eclipse.osee.ats.artifact.TaskArtifact;
import org.eclipse.osee.ats.artifact.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.util.AtsRelationTypes;
import org.eclipse.osee.ats.util.widgets.ReviewManager;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.plugin.util.Displays;

public class WorldContentProvider implements ITreeContentProvider {

   // Store off relatedArts as they are discovered so they're not garbage collected
   protected Set<Artifact> relatedArts = new HashSet<Artifact>();
   private final WorldXViewer xViewer;
   private static Object[] EMPTY_ARRAY = new Object[0];

   public WorldContentProvider(WorldXViewer WorldXViewer) {
      super();
      this.xViewer = WorldXViewer;
   }

   @Override
   public String toString() {
      return "WorldContentProvider";
   }

   public void clear(boolean forcePend) {
      Displays.ensureInDisplayThread(new Runnable() {
         public void run() {
            xViewer.setInput(Collections.emptyList());
            xViewer.refresh();
         };
      }, forcePend);
   }

   public Object[] getChildren(Object parentElement) {
      if (parentElement instanceof Collection<?>) {
         return ((Collection<?>) parentElement).toArray();
      }
      if (parentElement instanceof Artifact) {
         try {
            Artifact artifact = (Artifact) parentElement;
            if (artifact.isDeleted()) return new Object[] {};
            if (artifact instanceof ActionArtifact) {
               relatedArts.addAll(((ActionArtifact) artifact).getTeamWorkFlowArtifacts());
               return ((ActionArtifact) artifact).getTeamWorkFlowArtifacts().toArray();
            }
            if (artifact instanceof GoalArtifact) {
               List<Artifact> arts = artifact.getRelatedArtifacts(AtsRelationTypes.Goal_Member, false);
               relatedArts.addAll(arts);
               return arts.toArray(new Artifact[artifact.getRelatedArtifactsCount(AtsRelationTypes.Goal_Member)]);
            }
            if (artifact instanceof TeamWorkFlowArtifact) {
               TeamWorkFlowArtifact teamArt = (TeamWorkFlowArtifact) artifact;
               List<Artifact> arts = new ArrayList<Artifact>();
               // Convert artifacts to WorldArtifactItems
               arts.addAll(ReviewManager.getReviews(teamArt));
               arts.addAll(teamArt.getTaskArtifactsSorted());
               relatedArts.addAll(arts);
               return arts.toArray();
            }
            if (artifact instanceof ReviewSMArtifact) {
               ReviewSMArtifact reviewArt = (ReviewSMArtifact) artifact;
               List<Artifact> arts = new ArrayList<Artifact>();
               arts.addAll(reviewArt.getTaskArtifactsSorted());
               relatedArts.addAll(arts);
               return arts.toArray();
            }
         } catch (OseeCoreException ex) {
            OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE, ex);
         }
      }
      return EMPTY_ARRAY;
   }

   public Object getParent(Object element) {
      if (element instanceof Artifact) {
         try {
            Artifact artifact = (Artifact) element;
            if (artifact.isDeleted()) return null;
            if (artifact instanceof TeamWorkFlowArtifact) {
               return ((TeamWorkFlowArtifact) artifact).getParentActionArtifact();
            }
            if (artifact instanceof TaskArtifact) {
               return ((TaskArtifact) artifact).getParentSMA();
            }
            if (artifact instanceof ReviewSMArtifact) {
               return ((ReviewSMArtifact) artifact).getParentSMA();
            }
            if (artifact instanceof GoalArtifact) {
               return ((GoalArtifact) artifact).getParentSMA();
            }
         } catch (Exception ex) {
            // do nothing
         }
      }
      return null;
   }

   public boolean hasChildren(Object element) {
      if (element instanceof Collection<?>) return true;
      if (element instanceof String) return false;
      if (((Artifact) element).isDeleted()) return false;
      if (element instanceof ActionArtifact) return true;
      if (element instanceof StateMachineArtifact) {
         try {
            return ((StateMachineArtifact) element).hasAtsWorldChildren();
         } catch (Exception ex) {
            // do nothing
         }
      }
      return true;
   }

   public Object[] getElements(Object inputElement) {
      if (inputElement instanceof String) return new Object[] {inputElement};
      return getChildren(inputElement);
   }

   public void dispose() {
   }

   public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
   }

}
