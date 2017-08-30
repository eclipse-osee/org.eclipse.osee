/*******************************************************************************
 * Copyright (c) 2010 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.editor;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;
import org.eclipse.osee.ats.api.commit.ICommitConfigItem;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.api.version.IAtsVersion;
import org.eclipse.osee.ats.api.workflow.IAtsAction;
import org.eclipse.osee.ats.api.workflow.IAtsTask;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.core.client.review.AbstractReviewArtifact;
import org.eclipse.osee.ats.core.client.review.ReviewManager;
import org.eclipse.osee.ats.core.client.task.TaskArtifact;
import org.eclipse.osee.ats.core.client.team.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.core.client.util.AtsUtilClient;
import org.eclipse.osee.ats.core.client.workflow.AbstractWorkflowArtifact;
import org.eclipse.osee.ats.core.util.AtsUtilCore;
import org.eclipse.osee.ats.internal.Activator;
import org.eclipse.osee.ats.internal.AtsClientService;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.event.OseeEventManager;
import org.eclipse.osee.framework.skynet.core.event.filter.IEventFilter;
import org.eclipse.osee.framework.skynet.core.event.listener.IArtifactEventListener;
import org.eclipse.osee.framework.skynet.core.event.model.ArtifactEvent;
import org.eclipse.osee.framework.skynet.core.event.model.EventBasicGuidRelation;
import org.eclipse.osee.framework.skynet.core.event.model.Sender;
import org.eclipse.osee.framework.ui.swt.Displays;

/**
 * Common location for event handling for SMAEditors in order to keep number of registrations and processing to a
 * minimum.
 *
 * @author Donald G. Dunne
 */
public class WfeArtifactEventManager implements IArtifactEventListener {

   static List<IWfeEventHandler> handlers = new CopyOnWriteArrayList<>();
   static WfeArtifactEventManager instance = new WfeArtifactEventManager();

   private WfeArtifactEventManager() {
      OseeEventManager.addListener(this);
   }

   public static void add(IWfeEventHandler iWorldEventHandler) {
      WfeArtifactEventManager.handlers.add(iWorldEventHandler);
   }

   public static void remove(IWfeEventHandler iWorldEventHandler) {
      if (instance != null) {
         WfeArtifactEventManager.handlers.remove(iWorldEventHandler);
      }
   }

   @Override
   public List<? extends IEventFilter> getEventFilters() {
      return AtsUtilClient.getAtsObjectEventFilters();
   }

   @Override
   public void handleArtifactEvent(final ArtifactEvent artifactEvent, Sender sender) {
      for (IWfeEventHandler handler : handlers) {
         if (handler.isDisposed()) {
            handlers.remove(handler);
         }
      }
      try {
         if (!artifactEvent.isOnBranch(AtsClientService.get().getAtsBranch())) {
            return;
         }
      } catch (OseeCoreException ex) {
         return;
      }
      for (final IWfeEventHandler handler : handlers) {
         try {
            safelyProcessHandler(artifactEvent, handler);
         } catch (Exception ex) {
            OseeLog.log(Activator.class, Level.SEVERE, "Error processing event handler - " + handler, ex);
         }
      }
   }

   private void safelyProcessHandler(final ArtifactEvent artifactEvent, final IWfeEventHandler handler) throws OseeCoreException {
      final AbstractWorkflowArtifact awa = handler.getWorkflowEditor().getAwa();
      boolean refreshed = false;

      if (artifactEvent.isDeletedPurged(awa)) {
         handler.getWorkflowEditor().closeEditor();
      } else if (
      //
      workflowModifiedOrReloaded(artifactEvent, awa) ||
      //
         workflowRelationIsAddedChangedOrDeleted(artifactEvent, awa) ||
         //
         workflowActionIsModifedOrReloaded(artifactEvent, awa) ||
         //
         workflowActionRelationIsAddedChangedOrDeleted(artifactEvent, awa) ||
         //
         teamWorkflowParallelConfigurationChanged(artifactEvent, awa)
      //
      ) {
         refreshed = true;
         Displays.ensureInDisplayThread(new Runnable() {
            @Override
            public void run() {
               handler.getWorkflowEditor().refreshPages();
            }
         });
      } else if (isReloaded(artifactEvent, awa)) {
         if (awa.isDeleted()) {
            WorkflowEditor.close(Collections.singleton(awa), false);
         } else {
            Displays.ensureInDisplayThread(new Runnable() {
               @Override
               public void run() {
                  handler.getWorkflowEditor().refreshPages();
               }
            });
         }
      }
      if (!refreshed && awa.isTeamWorkflow() && ReviewManager.hasReviews((TeamWorkFlowArtifact) awa)) {
         try {
            // If related review has made a change, redraw
            for (AbstractReviewArtifact reviewArt : ReviewManager.getReviews((TeamWorkFlowArtifact) awa)) {
               if (artifactEvent.isHasEvent(reviewArt)) {
                  refreshed = true;
                  Displays.ensureInDisplayThread(new Runnable() {
                     @Override
                     public void run() {
                        handler.getWorkflowEditor().refreshPages();
                     }
                  });
                  // Only refresh editor for first review that has event
                  break;
               }
            }
         } catch (Exception ex) {
            // do nothing
         }
      }
      if (!refreshed && awa.isTeamWorkflow() && AtsClientService.get().getTaskService().hasTasks(
         ((TeamWorkFlowArtifact) awa))) {
         try {
            // If related review has made a change, redraw
            for (IAtsTask task : AtsClientService.get().getTaskService().getTasks(awa, awa.getStateDefinition())) {
               if (artifactEvent.isHasEvent((TaskArtifact) task.getStoreObject())) {
                  refreshed = true;
                  Displays.ensureInDisplayThread(new Runnable() {
                     @Override
                     public void run() {
                        handler.getWorkflowEditor().refreshPages();
                     }
                  });
                  // Only refresh editor for first task that has event
                  break;
               }
            }
         } catch (Exception ex) {
            // do nothing
         }
      }
      if (!refreshed) {
         try {
            // Since SMAEditor is refreshed when a sibling workflow is changed, need to refresh this
            // list of actionable items when a sibling changes
            for (IAtsTeamWorkflow teamWf : AtsClientService.get().getWorkItemService().getTeams(
               awa.getParentAction())) {
               IAtsAction parentAction = teamWf.getParentAction();
               if (awa.notEqual(teamWf) && artifactEvent.isHasEvent(
                  (Artifact) teamWf.getStoreObject()) && parentAction != null && artifactEvent.isRelAddedChangedDeleted(
                     (Artifact) parentAction.getStoreObject())) {
                  refreshed = true;
                  Displays.ensureInDisplayThread(new Runnable() {
                     @Override
                     public void run() {
                        handler.getWorkflowEditor().refreshPages();
                     }
                  });
                  // Only need to refresh once
                  return;
               }
            }
         } catch (Exception ex) {
            // do nothing
         }
      }

   }

   /**
    * Return true if one of the versions configured for parallel dev had a parallel config relation add, change or
    * delete. Refreshing in this case is necessary so the commit manager will show the updated parallel configuration
    * changes.
    */
   private boolean teamWorkflowParallelConfigurationChanged(ArtifactEvent artifactEvent, AbstractWorkflowArtifact awa) {
      boolean changed = false;
      // Only handle for teamWorkflows
      if (awa instanceof TeamWorkFlowArtifact) {
         TeamWorkFlowArtifact teamWf = (TeamWorkFlowArtifact) awa;
         try {
            // Retrieve all config to commit items for this team Wf, which will contain all parallel version artifacts
            Collection<ICommitConfigItem> configArtifactsConfiguredToCommitTo =
               AtsClientService.get().getBranchService().getConfigArtifactsConfiguredToCommitTo(teamWf);
            for (Object obj : configArtifactsConfiguredToCommitTo) {
               if (obj instanceof IAtsVersion) {
                  IAtsVersion version = (IAtsVersion) obj;
                  for (EventBasicGuidRelation relation : artifactEvent.getRelations()) {
                     // If relation is parallel config and guid is one of parallel configured versions
                     if (relation.is(AtsRelationTypes.ParallelVersion_Child) && (relation.getArtA().getGuid().equals(
                        AtsUtilCore.getGuid(version)) || relation.getArtB().getGuid().equals(
                           AtsUtilCore.getGuid(version)))) {
                        changed = true;
                        break;
                     }
                  }
               }
            }
         } catch (OseeCoreException ex) {
            OseeLog.log(Activator.class, Level.SEVERE, ex);
         }
      }
      return changed;
   }

   private boolean workflowActionRelationIsAddedChangedOrDeleted(final ArtifactEvent artifactEvent, AbstractWorkflowArtifact awa) {
      boolean result = false;
      if (awa instanceof TeamWorkFlowArtifact) {
         TeamWorkFlowArtifact teamWf = (TeamWorkFlowArtifact) awa;
         try {
            Artifact actionArt = teamWf.getParentActionArtifact();
            result = artifactEvent.isRelAddedChangedDeleted(actionArt);
         } catch (OseeCoreException ex) {
            OseeLog.log(Activator.class, Level.SEVERE, ex);
         }
      }
      return result;
   }

   private boolean workflowActionIsModifedOrReloaded(ArtifactEvent artifactEvent, AbstractWorkflowArtifact awa) {
      boolean result = false;
      if (awa instanceof TeamWorkFlowArtifact) {
         TeamWorkFlowArtifact teamWf = (TeamWorkFlowArtifact) awa;
         try {
            Artifact actionArt = teamWf.getParentActionArtifact();
            result = artifactEvent.isModifiedReloaded(actionArt);
         } catch (OseeCoreException ex) {
            OseeLog.log(Activator.class, Level.SEVERE, ex);
         }
      }
      return result;
   }

   private boolean workflowRelationIsAddedChangedOrDeleted(final ArtifactEvent artifactEvent, AbstractWorkflowArtifact awa) {
      return artifactEvent.isRelAddedChangedDeleted(awa);
   }

   private boolean workflowModifiedOrReloaded(final ArtifactEvent artifactEvent, AbstractWorkflowArtifact awa) {
      return artifactEvent.isModifiedReloaded(awa);
   }

   private boolean isReloaded(ArtifactEvent artifactEvent, AbstractWorkflowArtifact sma) {
      try {
         if (artifactEvent.isReloaded(sma)) {
            return true;
         }
         if (sma instanceof TeamWorkFlowArtifact) {
            for (IAtsTask task : AtsClientService.get().getTaskService().getTasks((TeamWorkFlowArtifact) sma)) {
               if (artifactEvent.isReloaded((TaskArtifact) task.getStoreObject())) {
                  return true;
               }
            }
         }
         if (sma.isTeamWorkflow()) {
            for (AbstractReviewArtifact reviewArt : ReviewManager.getReviews((TeamWorkFlowArtifact) sma)) {
               if (artifactEvent.isReloaded(reviewArt)) {
                  return true;
               }
            }
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
         return false;
      }
      return false;
   }

   public static boolean isLoaded(Artifact artifact) {
      for (IWfeEventHandler handler : handlers) {
         try {
            if (!handler.isDisposed()) {
               if (artifact.equals(handler.getWorkflowEditor().getArtifactFromEditorInput())) {
                  return true;
               }
            }
         } catch (Exception ex) {
            OseeLog.logf(Activator.class, Level.SEVERE, ex, "Error processing event handler for - %s", handler);
         }
      }
      return false;
   }

}
