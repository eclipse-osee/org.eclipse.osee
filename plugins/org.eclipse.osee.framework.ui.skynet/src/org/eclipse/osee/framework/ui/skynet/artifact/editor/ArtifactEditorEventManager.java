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
package org.eclipse.osee.framework.ui.skynet.artifact.editor;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.event.EventUtil;
import org.eclipse.osee.framework.skynet.core.event.OseeEventManager;
import org.eclipse.osee.framework.skynet.core.event.filter.IEventFilter;
import org.eclipse.osee.framework.skynet.core.event.listener.IArtifactEventListener;
import org.eclipse.osee.framework.skynet.core.event.listener.IBranchEventListener;
import org.eclipse.osee.framework.skynet.core.event.model.AccessArtifactLockTopicEvent;
import org.eclipse.osee.framework.skynet.core.event.model.AccessTopicEvent;
import org.eclipse.osee.framework.skynet.core.event.model.ArtifactEvent;
import org.eclipse.osee.framework.skynet.core.event.model.BranchEvent;
import org.eclipse.osee.framework.skynet.core.event.model.BranchEventType;
import org.eclipse.osee.framework.skynet.core.event.model.EventBasicGuidArtifact;
import org.eclipse.osee.framework.skynet.core.event.model.EventModType;
import org.eclipse.osee.framework.skynet.core.event.model.Sender;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

/**
 * Common location for event handling for ArtifactExplorers in order to keep number of registrations and processing to a
 * minimum.
 *
 * @author Donald G. Dunne
 */
public class ArtifactEditorEventManager implements IArtifactEventListener, IBranchEventListener, EventHandler {

   static List<IArtifactEditorEventHandler> handlers = new CopyOnWriteArrayList<>();
   static ArtifactEditorEventManager instance;

   public static void add(IArtifactEditorEventHandler iWorldEventHandler) {
      if (instance == null) {
         instance = new ArtifactEditorEventManager();
         OseeEventManager.addListener(instance);
      }
      ArtifactEditorEventManager.handlers.add(iWorldEventHandler);
   }

   public static void remove(IArtifactEditorEventHandler iWorldEventHandler) {
      if (instance != null) {
         ArtifactEditorEventManager.handlers.remove(iWorldEventHandler);
      }
   }

   @Override
   public List<? extends IEventFilter> getEventFilters() {
      // Can't filter cause this class handles all artifact explorers which can care about different branches
      return null;
   }

   @Override
   public void handleArtifactEvent(final ArtifactEvent artifactEvent, Sender sender) {
      for (IArtifactEditorEventHandler handler : handlers) {
         if (handler.isDisposed()) {
            handlers.remove(handler);
         }
      }
      EventUtil.eventLog(
         "ArtifactEditorEventManager: handleArtifactEvent called [" + artifactEvent + "] - sender " + sender + "");
      final Collection<Artifact> modifiedArts =
         artifactEvent.getCacheArtifacts(EventModType.Modified, EventModType.Reloaded);
      final Collection<Artifact> relModifiedArts = artifactEvent.getRelCacheArtifacts();
      final Collection<EventBasicGuidArtifact> deletedPurgedChangedArts =
         artifactEvent.get(EventModType.Deleted, EventModType.Purged);
      final Collection<Artifact> relOrderChangedArtifacts = artifactEvent.getRelationOrderArtifacts();

      Displays.ensureInDisplayThread(new Runnable() {
         @Override
         public void run() {
            if (!deletedPurgedChangedArts.isEmpty()) {
               for (IArtifactEditorEventHandler handler : handlers) {
                  try {
                     if (!handler.isDisposed() && handler.getArtifactFromEditorInput() != null && deletedPurgedChangedArts.contains(
                        handler.getArtifactFromEditorInput())) {
                        handler.closeEditor();
                     }
                  } catch (Exception ex) {
                     OseeLog.log(Activator.class, Level.SEVERE,
                        "Error processing event handler for deleted - " + handler, ex);
                  }
               }
            }
            if (!modifiedArts.isEmpty() || !relModifiedArts.isEmpty() || !relOrderChangedArtifacts.isEmpty()) {
               for (IArtifactEditorEventHandler handler : handlers) {
                  try {
                     if (!handler.isDisposed() && handler.getArtifactFromEditorInput() != null) {

                        if (modifiedArts.contains(handler.getArtifactFromEditorInput())) {
                           handler.refreshDirtyArtifact();
                        }

                        for (Artifact art : modifiedArts) {
                           if (art.isOfType(CoreArtifactTypes.AccessControlModel)) {
                              handler.refreshDirtyArtifact();
                           }
                        }

                        boolean relModified = relModifiedArts.contains(handler.getArtifactFromEditorInput());
                        boolean reorderArt = relOrderChangedArtifacts.contains(handler.getArtifactFromEditorInput());
                        if (relModified || reorderArt) {
                           handler.refreshRelations();
                           handler.getEditor().onDirtied();
                        }
                     }
                  } catch (Exception ex) {
                     OseeLog.log(Activator.class, Level.SEVERE,
                        "Error processing event handler for modified - " + handler, ex);
                  }
               }
            }
         }
      });
   }

   @Override
   public void handleBranchEvent(Sender sender, final BranchEvent branchEvent) {
      Displays.ensureInDisplayThread(new Runnable() {
         @Override
         public void run() {
            for (IArtifactEditorEventHandler handler : handlers) {
               if (!handler.isDisposed()) {
                  if (branchEvent.getEventType() == BranchEventType.Committing || branchEvent.getEventType() == BranchEventType.Committed) {
                     if (handler.getArtifactFromEditorInput().isOnBranch(branchEvent.getSourceBranch())) {
                        handler.closeEditor();
                     }
                  }
               }
            }
         }
      });
   }

   @Override
   public void handleEvent(Event event) {
      try {
         if (AccessTopicEvent.ACCESS_ARTIFACT_LOCK_MODIFIED.matches(event)) {
            for (final IArtifactEditorEventHandler handler : handlers) {
               if (!handler.isDisposed()) {
                  AccessArtifactLockTopicEvent payload =
                     EventUtil.getTopicJson(event, AccessArtifactLockTopicEvent.class);
                  if (payload.matches(handler.getArtifactFromEditorInput())) {
                     Displays.ensureInDisplayThread(new Runnable() {
                        @Override
                        public void run() {
                           handler.refresh();
                        }
                     });
                  }
               }
            }
         }
      } catch (Exception ex) {
         // do nothing
      }
   }

}
