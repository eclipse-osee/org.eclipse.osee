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
import org.eclipse.osee.ats.api.util.AtsTopicEvent;
import org.eclipse.osee.ats.ide.editor.WorkflowEditor;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.ats.ide.util.AtsUtilClient;
import org.eclipse.osee.ats.ide.workflow.AbstractWorkflowArtifact;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.event.OseeEventManager;
import org.eclipse.osee.framework.skynet.core.event.filter.IEventFilter;
import org.eclipse.osee.framework.skynet.core.event.listener.IArtifactEventListener;
import org.eclipse.osee.framework.skynet.core.event.model.ArtifactEvent;
import org.eclipse.osee.framework.skynet.core.event.model.Sender;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

/**
 * Common location for event handling for ATS objects
 *
 * @author Donald G. Dunne
 */
public class WfeArtifactEventManager implements IArtifactEventListener, EventHandler {

   static List<WorkflowEditor> editors = new CopyOnWriteArrayList<>();
   static WfeArtifactEventManager instance = new WfeArtifactEventManager();

   // Singleton
   private WfeArtifactEventManager() {
      OseeEventManager.addListener(this);
   }

   public static void add(WorkflowEditor editor) {
      WfeArtifactEventManager.editors.add(editor);
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
   public void handleArtifactEvent(final ArtifactEvent artifactEvent, Sender sender) {
      for (WorkflowEditor editor : editors) {
         if (editor.isDisposed()) {
            editors.remove(editor);
         }
      }
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

   private void safelyProcessHandler(final ArtifactEvent artifactEvent, final WorkflowEditor editor) {
      final AbstractWorkflowArtifact awa = editor.getWorkItem();

      if (artifactEvent.isDeletedPurged(awa)) {
         editor.closeEditor();
         return;
      }

      Displays.ensureInDisplayThread(new Runnable() {

         @Override
         public void run() {
            editor.refresh();
         }
      });

   }

   @Override
   public void handleEvent(Event event) {
      try {
         if (event.getTopic().equals(AtsTopicEvent.WORK_ITEM_MODIFIED.getTopic())) {
            String ids = (String) event.getProperty(AtsTopicEvent.WORK_ITEM_IDS_KEY);
            for (Long workItemId : Collections.fromString(ids, ";", Long::valueOf)) {
               ArtifactId workItemArtId = ArtifactId.valueOf(workItemId);
               for (WorkflowEditor editor : editors) {
                  try {
                     if (!editor.isDisposed()) {
                        if (editor.getWorkItem().equals(workItemArtId)) {
                           editor.refresh();
                        }
                     }
                  } catch (Exception ex) {
                     OseeLog.logf(Activator.class, Level.SEVERE, ex, "Error processing event handler for - %s", editor);
                  }
               }
            }
         }
      } catch (Exception ex) {
         // do nothing
      }
   }

   public static void handleEventAfterReload(Event event) {
      instance.handleEvent(event);
   }

}
