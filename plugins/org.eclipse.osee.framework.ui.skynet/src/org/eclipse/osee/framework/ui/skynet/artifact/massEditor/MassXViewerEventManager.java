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
package org.eclipse.osee.framework.ui.skynet.artifact.massEditor;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;
import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.event.OseeEventManager;
import org.eclipse.osee.framework.skynet.core.event.filter.IEventFilter;
import org.eclipse.osee.framework.skynet.core.event.listener.IArtifactEventListener;
import org.eclipse.osee.framework.skynet.core.event.model.ArtifactEvent;
import org.eclipse.osee.framework.skynet.core.event.model.EventBasicGuidArtifact;
import org.eclipse.osee.framework.skynet.core.event.model.EventModType;
import org.eclipse.osee.framework.skynet.core.event.model.Sender;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;
import org.eclipse.osee.framework.ui.swt.Displays;

/**
 * Common location for event handling for mass editor in order to keep number of registrations and processing to a
 * minimum.
 *
 * @author Donald G. Dunne
 */
public class MassXViewerEventManager implements IArtifactEventListener {

   List<IMassViewerEventHandler> handlers = new CopyOnWriteArrayList<>();
   static MassXViewerEventManager instance;

   static {
      instance = new MassXViewerEventManager();
      OseeEventManager.addListener(instance);
   }

   public static synchronized void add(IMassViewerEventHandler iWorldEventHandler) {
      instance.handlers.add(iWorldEventHandler);
   }

   public static void remove(IMassViewerEventHandler iWorldEventHandler) {
      if (instance != null) {
         instance.handlers.remove(iWorldEventHandler);
      }
   }

   @Override
   public void handleArtifactEvent(final ArtifactEvent artifactEvent, Sender sender) {
      for (IMassViewerEventHandler handler : handlers) {
         if (handler.isDisposed()) {
            handlers.remove(handler);
         }
      }

      final Collection<Artifact> modifiedArts =
         artifactEvent.getCacheArtifacts(EventModType.Modified, EventModType.Reloaded);
      final Collection<Artifact> relModifiedArts = artifactEvent.getRelCacheArtifacts();
      final Collection<EventBasicGuidArtifact> deletedPurgedArts =
         artifactEvent.get(EventModType.Deleted, EventModType.Purged);

      Displays.ensureInDisplayThread(new Runnable() {
         @Override
         public void run() {
            if (!deletedPurgedArts.isEmpty()) {
               for (IMassViewerEventHandler handler : handlers) {
                  try {
                     if (!handler.isDisposed()) {
                        IContentProvider contentProvider = handler.getMassXViewer().getContentProvider();
                        // remove from UI
                        if (contentProvider instanceof MassContentProvider) {
                           ((MassContentProvider) contentProvider).removeAll(deletedPurgedArts);
                        }
                     }
                  } catch (Exception ex) {
                     OseeLog.log(Activator.class, Level.SEVERE,
                        "Error processing event handler for deleted - " + handler, ex);
                  }
               }
            }
            for (IMassViewerEventHandler handler : handlers) {
               try {
                  if (!handler.isDisposed()) {
                     IContentProvider contentProvider = handler.getMassXViewer().getContentProvider();
                     // remove from UI
                     if (contentProvider instanceof MassContentProvider) {
                        ((MassContentProvider) contentProvider).updateAll(modifiedArts);
                     }
                  }

                  for (Artifact art : relModifiedArts) {
                     // Don't refresh deleted artifacts
                     if (art.isDeleted()) {
                        continue;
                     }
                     handler.getMassXViewer().refresh(art);
                  }
               } catch (Exception ex) {
                  OseeLog.log(Activator.class, Level.SEVERE, "Error processing event handler for modified - " + handler,
                     ex);
               }
            }
         }
      });
   }

   @Override
   public List<? extends IEventFilter> getEventFilters() {
      return null;
   }

}
