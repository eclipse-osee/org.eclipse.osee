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

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.eclipse.osee.ats.ActionDebug;
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.artifact.ActionArtifact;
import org.eclipse.osee.ats.artifact.StateMachineArtifact;
import org.eclipse.osee.ats.artifact.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.editor.SMAManager;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.event.LocalTransactionEvent;
import org.eclipse.osee.framework.skynet.core.event.RemoteTransactionEvent;
import org.eclipse.osee.framework.skynet.core.event.SkynetEventManager;
import org.eclipse.osee.framework.skynet.core.event.TransactionEvent;
import org.eclipse.osee.framework.skynet.core.event.TransactionEvent.EventData;
import org.eclipse.osee.framework.ui.plugin.event.Event;
import org.eclipse.osee.framework.ui.plugin.event.IEventReceiver;
import org.eclipse.osee.framework.ui.skynet.util.OSEELog;

/**
 * @author Donald G. Dunne
 */
public class WorldArtifactItem implements IEventReceiver {

   private Artifact artifact;
   private final WorldXViewer xViewer;
   protected List<WorldArtifactItem> artifactList;
   protected WorldArtifactItem parentItem;
   private ActionDebug debug = new ActionDebug(false, "WorldArtifactItem");

   public WorldArtifactItem(WorldXViewer xViewer, Artifact artifact, WorldArtifactItem parentItem) {
      this.xViewer = xViewer;
      this.parentItem = parentItem;
      artifactList = new ArrayList<WorldArtifactItem>();
      setArtifact(artifact);
   }

   private void setArtifact(Artifact newArt) {
      if (artifact != null) {
         SkynetEventManager.getInstance().unRegisterAll(this);
         SkynetEventManager.getInstance().unRegisterAll(this);
      }
      if (newArt != null) {
         SkynetEventManager.getInstance().register(RemoteTransactionEvent.class, this);
         SkynetEventManager.getInstance().register(LocalTransactionEvent.class, this);
      }
      artifact = newArt;
   }

   public StateMachineArtifact getSMA() {
      if (artifact instanceof StateMachineArtifact) return (StateMachineArtifact) artifact;
      return null;
   }

   public Artifact getArtifact() {
      return artifact;
   }

   public List<WorldArtifactItem> getArtifactItems() {
      return artifactList;
   }

   public WorldArtifactItem getParentItem() {
      return parentItem;
   }

   protected void addArtifactItem(WorldArtifactItem artifactItem) {
      artifactList.add(artifactItem);
      artifactItem.parentItem = this;
   }

   protected void removeArtifactItem(WorldArtifactItem artifactItem) {
      artifactList.remove(artifactItem);
      fireRemove(artifactItem);
   }

   protected void fireRemove(Object removed) {
      SkynetEventManager.getInstance().unRegisterAll(this);
   }

   private void addArtsToArtifactItems(WorldArtifactItem parentItem, ArrayList<WorldArtifactItem> items, Collection<? extends Artifact> arts) {
      for (Artifact art : arts) {
         WorldArtifactItem wai = new WorldArtifactItem(xViewer, art, parentItem);
         items.add(wai);
         addArtifactItem(wai);
      }
   }

   @SuppressWarnings("unchecked")
   public Object[] getChildren() {
      debug.report("getChildren()");
      if (artifact == null) {
         Object[] objs = (Object[]) ((Collection) artifactList).toArray(new Object[artifactList.size()]);
         return objs;
      }
      if (artifact.isDeleted()) return new Object[] {};
      if (artifact instanceof ActionArtifact) {
         ActionArtifact art = (ActionArtifact) artifact;
         // Convert artifacts to WorldArtifactItems
         ArrayList<WorldArtifactItem> wais = new ArrayList<WorldArtifactItem>();
         try {
            addArtsToArtifactItems(this, wais, art.getTeamWorkFlowArtifacts());
         } catch (SQLException ex) {
            // Don't do anything
            ;
         }
         return (Object[]) ((ArrayList) wais).toArray(new Object[((ArrayList) wais).size()]);
      }
      if (artifact instanceof TeamWorkFlowArtifact) {
         TeamWorkFlowArtifact twf = (TeamWorkFlowArtifact) artifact;
         SMAManager smaMgr = new SMAManager(twf);
         // Convert artifacts to WorldArtifactItems
         ArrayList<WorldArtifactItem> wais = new ArrayList<WorldArtifactItem>();
         try {
            addArtsToArtifactItems(this, wais, smaMgr.getReviewManager().getReviews());
            addArtsToArtifactItems(this, wais, smaMgr.getTaskMgr().getTaskArtifacts());
         } catch (SQLException ex) {
            // Don't do anything
         }
         return (Object[]) ((ArrayList) wais).toArray(new Object[((ArrayList) wais).size()]);
      }
      return null;
   }

   public void dispose() {
      // Remove children
      for (WorldArtifactItem child : artifactList) {
         child.dispose();
      }
      // Clear artifactList
      artifactList.clear();
      // Clear out event registration
      setArtifact(null);
   }

   public void onEvent(final Event event) {
      if (artifact == null || artifact.isDeleted() || xViewer.getTree() == null || xViewer.getTree().isDisposed()) {
         if (xViewer.getTree() != null && !xViewer.getTree().isDisposed()) xViewer.remove(this);
         dispose();
         return;
      }
      final WorldArtifactItem wai = this;

      if (event instanceof TransactionEvent) {
         EventData ed = ((TransactionEvent) event).getEventData(artifact);
         if (ed.isRemoved()) {
            xViewer.remove(wai);
         } else if (ed.getAvie() != null && ed.getAvie().getOldVersion().equals(artifact)) {
            setArtifact((Artifact) ed.getAvie().getNewVersion());
            xViewer.update(wai, null);
         } else if (ed.isModified() || ed.isRelChange()) {
            xViewer.update(wai, null);
         }

         // If ActionArtifact, need to check if it was a child team that changed
         // Cause Action shows data rolled up from team children
         if (artifact instanceof ActionArtifact) {
            try {
               for (Artifact team : ((ActionArtifact) artifact).getTeamWorkFlowArtifacts()) {
                  EventData teamEd = ((TransactionEvent) event).getEventData(team);
                  if (teamEd.isHasEvent()) {
                     xViewer.update(wai, null);
                     break;
                  }
               }
            } catch (SQLException ex) {
               // Do Nothing
            }
         }
         if (artifact instanceof TeamWorkFlowArtifact) {
            try {
               SMAManager smaMgr = new SMAManager((TeamWorkFlowArtifact) artifact);
               for (Artifact taskArt : smaMgr.getTaskMgr().getTaskArtifacts()) {
                  EventData teamEd = ((TransactionEvent) event).getEventData(taskArt);
                  if (teamEd.isHasEvent()) {
                     xViewer.update(wai, null);
                     break;
                  }
               }
            } catch (SQLException ex) {
               // Do Nothing
            }
         }
      } else
         OSEELog.logSevere(AtsPlugin.class, "Unexpected event => " + event, true);
   }

   public boolean runOnEventInDisplayThread() {
      return true;
   }

   public String toString() {
      return getArtifact().getArtifactTypeName() + " - \"" + artifact + "\"";
   }
}
