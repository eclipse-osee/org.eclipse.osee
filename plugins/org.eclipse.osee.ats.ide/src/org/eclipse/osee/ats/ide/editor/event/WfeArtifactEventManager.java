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
package org.eclipse.osee.ats.ide.editor.event;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;
import org.eclipse.osee.ats.api.util.AtsTopicEvent;
import org.eclipse.osee.ats.api.workflow.IAtsTask;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.ats.ide.internal.AtsClientService;
import org.eclipse.osee.ats.ide.util.AtsUtilClient;
import org.eclipse.osee.ats.ide.workflow.AbstractWorkflowArtifact;
import org.eclipse.osee.ats.ide.workflow.review.AbstractReviewArtifact;
import org.eclipse.osee.ats.ide.workflow.review.ReviewManager;
import org.eclipse.osee.ats.ide.workflow.task.TaskArtifact;
import org.eclipse.osee.ats.ide.workflow.teamwf.TeamWorkFlowArtifact;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.attribute.AttributeTypeManager;
import org.eclipse.osee.framework.skynet.core.event.OseeEventManager;
import org.eclipse.osee.framework.skynet.core.event.filter.IEventFilter;
import org.eclipse.osee.framework.skynet.core.event.listener.IArtifactEventListener;
import org.eclipse.osee.framework.skynet.core.event.model.ArtifactEvent;
import org.eclipse.osee.framework.skynet.core.event.model.Sender;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

/**
 * Common location for event handling for SMAEditors in order to keep number of registrations and processing to a
 * minimum.
 *
 * @author Donald G. Dunne
 */
public class WfeArtifactEventManager implements IArtifactEventListener, EventHandler {

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

   private void safelyProcessHandler(final ArtifactEvent artifactEvent, final IWfeEventHandler handler) {
      final AbstractWorkflowArtifact awa = handler.getWorkflowEditor().getWorkItem();
      //      boolean refreshed = false;

      if (artifactEvent.isDeletedPurged(awa)) {
         handler.getWorkflowEditor().closeEditor();
         return;
      }

      if (isReloaded(artifactEvent, awa)) {
         Displays.ensureInDisplayThread(new Runnable() {
            @Override
            public void run() {
               handler.getWorkflowEditor().refreshPages();
            }
         });
         return;
      }

      Displays.ensureInDisplayThread(new Runnable() {

         @Override
         public void run() {
            handler.getWorkflowEditor().handleEvent(artifactEvent);
         }
      });

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

   @Override
   public void handleEvent(Event event) {
      try {
         if (event.getTopic().equals(AtsTopicEvent.WORK_ITEM_MODIFIED.getTopic())) {
            String ids = (String) event.getProperty(AtsTopicEvent.WORK_ITEM_IDS_KEY);
            for (Long workItemId : Collections.fromString(ids, ";", Long::valueOf)) {
               ArtifactId workItemArtId = ArtifactId.valueOf(workItemId);
               String attrTypeIds = (String) event.getProperty(AtsTopicEvent.WORK_ITEM_ATTR_TYPE_IDS_KEY);
               for (Long attrTypeId : Collections.fromString(attrTypeIds, ";", Long::valueOf)) {
                  for (IWfeEventHandler handler : handlers) {
                     try {
                        if (!handler.isDisposed()) {
                           if (handler.getWorkflowEditor().getArtifactFromEditorInput().equals(workItemArtId)) {
                              handler.getWorkflowEditor().handleEvent(AttributeTypeManager.getTypeById(attrTypeId));
                           }
                        }
                     } catch (Exception ex) {
                        OseeLog.logf(Activator.class, Level.SEVERE, ex, "Error processing event handler for - %s",
                           handler);
                     }
                  }
               }
            }
         }
      } catch (Exception ex) {
         // do nothing
      }
   }

}
