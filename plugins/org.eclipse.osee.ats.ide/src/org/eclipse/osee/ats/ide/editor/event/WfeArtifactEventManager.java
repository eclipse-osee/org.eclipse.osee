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

package org.eclipse.osee.ats.ide.editor.event;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.review.IAtsAbstractReview;
import org.eclipse.osee.ats.api.workflow.IAtsTask;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.ide.editor.WorkflowEditor;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.ats.ide.util.AtsUtilClient;
import org.eclipse.osee.ats.ide.workflow.AbstractWorkflowArtifact;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.event.OseeEventManager;
import org.eclipse.osee.framework.skynet.core.event.filter.IEventFilter;
import org.eclipse.osee.framework.skynet.core.event.listener.IArtifactEventListener;
import org.eclipse.osee.framework.skynet.core.event.listener.IArtifactTopicEventListener;
import org.eclipse.osee.framework.skynet.core.event.model.ArtifactEvent;
import org.eclipse.osee.framework.skynet.core.event.model.ArtifactTopicEvent;
import org.eclipse.osee.framework.skynet.core.event.model.Sender;
import org.eclipse.osee.framework.skynet.core.topic.event.filter.ITopicEventFilter;
import org.eclipse.osee.framework.ui.swt.Displays;

/**
 * Common location for WFE handling events. Only need to listen for ArtifactEvent cause transition locally will reload
 * and remote transition will reload if in cache.
 *
 * @author Donald G. Dunne
 */
public class WfeArtifactEventManager implements IArtifactEventListener, IArtifactTopicEventListener {

   static List<WorkflowEditor> editors = new CopyOnWriteArrayList<>();
   static WfeArtifactEventManager instance = new WfeArtifactEventManager();
   private final AtsApi atsApi;

   // Singleton
   private WfeArtifactEventManager() {
      OseeEventManager.addListener(this);
      atsApi = AtsApiService.get();
   }

   public static void add(WorkflowEditor editor) {
      if (!editors.contains(editor)) {
         WfeArtifactEventManager.editors.add(editor);
      }
   }

   public static void remove(WorkflowEditor editor) {
      if (instance != null) {
         WfeArtifactEventManager.editors.remove(editor);
      }
   }

   @Override
   public List<? extends IEventFilter> getEventFilters() {
      return AtsUtilClient.getAtsObjectEventFilters();
   }

   @Override
   public List<? extends ITopicEventFilter> getTopicEventFilters() {
      return AtsUtilClient.getAtsTopicObjectEventFilters();
   }

   @Override
   public void handleArtifactEvent(final ArtifactEvent artifactEvent, Sender sender) {
      editors.removeIf(editor -> editor.isDisposed());
      try {
         if (!artifactEvent.isOnBranch(AtsApiService.get().getAtsBranch())) {
            return;
         }
      } catch (OseeCoreException ex) {
         return;
      }
      for (final WorkflowEditor editor : editors) {
         try {
            safelyProcessHandler(artifactEvent, editor);
         } catch (Exception ex) {
            OseeLog.log(Activator.class, Level.SEVERE, "Error processing event handler - " + editor, ex);
         }
      }
   }

   @Override
   public void handleArtifactTopicEvent(final ArtifactTopicEvent artifactTopicEvent, Sender sender) {

      editors.removeIf(editor -> editor.isDisposed());
      try {
         if (!artifactTopicEvent.isOnBranch(AtsApiService.get().getAtsBranch())) {
            return;
         }
      } catch (OseeCoreException ex) {
         return;
      }
      for (final WorkflowEditor editor : editors) {
         try {
            safelyProcessHandler(artifactTopicEvent, editor);
         } catch (Exception ex) {
            OseeLog.log(Activator.class, Level.SEVERE, "Error processing event handler - " + editor, ex);
         }
      }
   }

   private void safelyProcessHandler(final ArtifactEvent artifactEvent, final WorkflowEditor editor) {
      final AbstractWorkflowArtifact awa = editor.getWorkItem();

      if (artifactEvent.isDeletedPurged(awa)) {
         editor.closeEditor();
         return;
      }
      if (artifactEvent.isHasEvent(awa) || artifactEvent.isReloaded(awa)) {
         Displays.ensureInDisplayThread(new Runnable() {

            @Override
            public void run() {
               editor.refresh();
            }
         });
      } else if (awa.isTeamWorkflow()) {
         boolean refreshNeeded = false;
         for (IAtsTask task : atsApi.getTaskService().getTasks((IAtsTeamWorkflow) awa)) {
            if (artifactEvent.isHasEvent((Artifact) task.getStoreObject())) {
               refreshNeeded = true;
               break;
            }
         }
         if (!refreshNeeded) {
            for (IAtsAbstractReview review : atsApi.getReviewService().getReviews((IAtsTeamWorkflow) awa)) {
               if (artifactEvent.isHasEvent((Artifact) review.getStoreObject())) {
                  refreshNeeded = true;
                  break;
               }
            }
         }
         if (refreshNeeded) {
            Displays.ensureInDisplayThread(new Runnable() {

               @Override
               public void run() {
                  editor.refresh();
               }
            });
         }
      }
   }

   private void safelyProcessHandler(final ArtifactTopicEvent artifactTopicEvent, final WorkflowEditor editor) {
      final AbstractWorkflowArtifact awa = editor.getWorkItem();

      if (artifactTopicEvent.isDeletedPurged(awa)) {
         editor.closeEditor();
         return;
      }
      if (artifactTopicEvent.isHasEvent(awa) || artifactTopicEvent.isReloaded(awa)) {
         Displays.ensureInDisplayThread(new Runnable() {

            @Override
            public void run() {
               editor.refresh();
            }
         });
      } else if (awa.isTeamWorkflow()) {
         boolean refreshNeeded = false;
         for (IAtsTask task : atsApi.getTaskService().getTasks((IAtsTeamWorkflow) awa)) {
            if (artifactTopicEvent.isHasEvent((Artifact) task.getStoreObject())) {
               refreshNeeded = true;
               break;
            }
         }
         if (!refreshNeeded) {
            for (IAtsAbstractReview review : atsApi.getReviewService().getReviews((IAtsTeamWorkflow) awa)) {
               if (artifactTopicEvent.isHasEvent((Artifact) review.getStoreObject())) {
                  refreshNeeded = true;
                  break;
               }
            }
         }
         if (refreshNeeded) {
            Displays.ensureInDisplayThread(new Runnable() {

               @Override
               public void run() {
                  editor.refresh();
               }
            });
         }
      }
   }

}
