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
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.osee.ats.ActionDebug;
import org.eclipse.osee.ats.artifact.ActionArtifact;
import org.eclipse.osee.ats.artifact.ReviewSMArtifact;
import org.eclipse.osee.ats.artifact.StateMachineArtifact;
import org.eclipse.osee.ats.artifact.TaskArtifact;
import org.eclipse.osee.ats.artifact.TeamWorkFlowArtifact;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.plugin.util.Displays;

public class WorldContentProvider implements ITreeContentProvider {

   protected List<Artifact> rootSet = new ArrayList<Artifact>();
   private final WorldXViewer xViewer;
   private static Object[] EMPTY_ARRAY = new Object[0];
   private final ActionDebug debug = new ActionDebug(false, "WorldTreeContentProvider");

   public WorldContentProvider(WorldXViewer WorldXViewer) {
      super();
      this.xViewer = WorldXViewer;
   }

   @Override
   public String toString() {
      return "WorldContentProvider";
   }

   public void add(final Artifact item) {
      add(Arrays.asList(item));
   }

   public void add(final Collection<? extends Artifact> items) {
      Displays.ensureInDisplayThread(new Runnable() {
         public void run() {
            if (xViewer.getInput() == null) xViewer.setInput(rootSet);
            for (Artifact item : items) {
               if (!rootSet.contains(item)) {
                  rootSet.add(item);
               }
            }
            xViewer.refresh();
         };
      });
   }

   public void set(final Collection<? extends Artifact> arts) {
      Displays.ensureInDisplayThread(new Runnable() {
         public void run() {
            if (xViewer.getInput() == null) xViewer.setInput(rootSet);
            rootSet.clear();
            add(arts);
         };
      });
   }

   public void remove(final Artifact art) {
      removeAll(Arrays.asList(art));
   }

   public void removeAll(final Collection<? extends Object> arts) {
      if (arts.size() == 0) return;
      Displays.ensureInDisplayThread(new Runnable() {
         public void run() {
            if (xViewer.getInput() == null) xViewer.setInput(rootSet);
            for (Object art : arts) {
               rootSet.remove(art);
            }
            xViewer.refresh();
         };
      });
   }

   public void updateAll(final Collection<? extends Object> arts) {
      if (arts.size() == 0) return;
      Displays.ensureInDisplayThread(new Runnable() {
         public void run() {
            if (xViewer.getInput() == null) xViewer.setInput(rootSet);
            for (Object art : arts) {
               xViewer.update(art, null);
            }
         };
      });
   }

   public void clear() {
      rootSet.clear();
      Displays.ensureInDisplayThread(new Runnable() {
         public void run() {
            if (xViewer.getInput() == null) {
               xViewer.setInput(rootSet);
            }
            xViewer.refresh();
         };
      });
   }

   public Object[] getChildren(Object parentElement) {
      debug.report("getChildren");
      if (parentElement instanceof Collection) {
         return ((Collection<?>) parentElement).toArray();
      }
      if (parentElement instanceof Artifact) {
         try {
            Artifact artifact = (Artifact) parentElement;
            if (artifact == null) {
               return new Object[] {};
            }
            if (artifact.isDeleted()) return new Object[] {};
            if (artifact instanceof ActionArtifact) {
               return ((ActionArtifact) artifact).getTeamWorkFlowArtifacts().toArray();
            }
            if (artifact instanceof TeamWorkFlowArtifact) {
               TeamWorkFlowArtifact teamArt = (TeamWorkFlowArtifact) artifact;
               List<Artifact> arts = new ArrayList<Artifact>();
               // Convert artifacts to WorldArtifactItems
               arts.addAll(teamArt.getSmaMgr().getReviewManager().getReviews());
               arts.addAll(teamArt.getSmaMgr().getTaskMgr().getTaskArtifacts());
               return arts.toArray();
            }
            if (artifact instanceof ReviewSMArtifact) {
               ReviewSMArtifact reviewArt = (ReviewSMArtifact) artifact;
               List<Artifact> arts = new ArrayList<Artifact>();
               // Convert artifacts to WorldArtifactItems
               arts.addAll(reviewArt.getSmaMgr().getReviewManager().getReviews());
               arts.addAll(reviewArt.getSmaMgr().getTaskMgr().getTaskArtifacts());
               return arts.toArray();
            }
         } catch (Exception ex) {
            // do nothing
         }
      }
      return EMPTY_ARRAY;
   }

   public Object getParent(Object element) {
      debug.report("getParent");
      if (element instanceof Artifact) {
         try {
            Artifact artifact = (Artifact) element;
            if (artifact == null) {
               return null;
            }
            if (artifact.isDeleted()) return null;
            if (artifact instanceof TeamWorkFlowArtifact) {
               ((TeamWorkFlowArtifact) artifact).getParentActionArtifact();
            }
            if (artifact instanceof TaskArtifact) {
               ((TaskArtifact) artifact).getParentSMA();
            }
            if (artifact instanceof ReviewSMArtifact) {
               ((TeamWorkFlowArtifact) artifact).getParentSMA();
            }
         } catch (Exception ex) {
            // do nothing
         }
      }
      return null;
   }

   public boolean hasChildren(Object element) {
      debug.report("hasChildren");
      if (element instanceof Collection) return true;
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
      debug.report("getElements");
      if (inputElement instanceof String) return new Object[] {inputElement};
      return getChildren(inputElement);
   }

   public void dispose() {
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer,
    *      java.lang.Object, java.lang.Object)
    */
   public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
   }

   /**
    * @return the rootSet
    */
   public Collection<Artifact> getRootSet() {
      return rootSet;
   }

}
