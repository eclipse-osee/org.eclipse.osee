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
package org.eclipse.osee.coverage.event;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;
import org.eclipse.osee.coverage.editor.CoverageEditor;
import org.eclipse.osee.coverage.internal.Activator;
import org.eclipse.osee.coverage.model.CoveragePackage;
import org.eclipse.osee.coverage.msgs.CoverageChange1;
import org.eclipse.osee.coverage.msgs.CoveragePackageEvent1;
import org.eclipse.osee.coverage.store.CoverageArtifactTypes;
import org.eclipse.osee.framework.core.client.ClientSessionManager;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.messaging.ConnectionNode;
import org.eclipse.osee.framework.messaging.OseeMessagingListener;
import org.eclipse.osee.framework.messaging.OseeMessagingStatusCallback;
import org.eclipse.osee.framework.messaging.ReplyConnection;
import org.eclipse.osee.framework.skynet.core.event.OseeEventManager;
import org.eclipse.osee.framework.skynet.core.event.filter.ArtifactTypeEventFilter;
import org.eclipse.osee.framework.skynet.core.event.filter.IEventFilter;
import org.eclipse.osee.framework.skynet.core.event.listener.IArtifactEventListener;
import org.eclipse.osee.framework.skynet.core.event.model.ArtifactEvent;
import org.eclipse.osee.framework.skynet.core.event.model.EventBasicGuidArtifact;
import org.eclipse.osee.framework.skynet.core.event.model.EventModType;
import org.eclipse.osee.framework.skynet.core.event.model.Sender;
import org.eclipse.ui.PlatformUI;

/**
 * @author Donald G. Dunne
 */
public class CoverageEventManager implements IArtifactEventListener, OseeMessagingStatusCallback {

   private static CoverageEventManager instance;
   private final List<CoverageEditor> editors = new ArrayList<CoverageEditor>();
   private ArtifactTypeEventFilter artifactTypeEventFilter;
   private ConnectionNode connectionNode;
   private OseeMessagingTracker oseeMessagingTracker;

   public static CoverageEventManager getInstance() {
      if (instance == null) {
         instance = new CoverageEventManager();
      }
      return instance;
   }

   public static void dispose() {
      if (instance != null) {
         instance.stopListeneingForFrameworkEvents();
         instance.stopListeningForRemoteCoverageEvents();
         instance.editors.clear();
         instance.oseeMessagingTracker.close();
         instance = null;
      }
   }

   private void stopListeneingForFrameworkEvents() {
      OseeEventManager.removeListener(this);
   }

   private void startListeningForFrameworkEvents() {
      OseeEventManager.addListener(this);
   }

   private ArtifactTypeEventFilter createArtifactTypeEventFilter() {
      if (artifactTypeEventFilter == null) {
         artifactTypeEventFilter =
            new ArtifactTypeEventFilter(CoverageArtifactTypes.CoverageFolder, CoverageArtifactTypes.CoverageUnit,
               CoverageArtifactTypes.CoveragePackage);
      }
      return artifactTypeEventFilter;
   }

   private void startListeningForRemoteCoverageEvents() {
      if (oseeMessagingTracker == null) {
         oseeMessagingTracker = new OseeMessagingTracker();
         oseeMessagingTracker.open(true);
      }
   }

   private void stopListeningForRemoteCoverageEvents() {
      if (oseeMessagingTracker != null) {
         oseeMessagingTracker.close();
      }
      oseeMessagingTracker = null;
   }

   public void addingRemoteEventService(ConnectionNode connectionNode) {
      this.connectionNode = connectionNode;
      connectionNode.subscribe(CoverageMessages.CoveragePackageEvent1, new CoverageMessageListener(), instance);
   }

   public void sendRemoteEvent(CoveragePackageEvent coverageEvent) {
      System.out.println(String.format("Sending CoveragePackageEvent [%s]", coverageEvent.getPackage().getName()));
      if (connectionNode != null) {
         try {
            CoveragePackageEvent1 event1 = getCoveragePackageEvent(coverageEvent);
            connectionNode.send(CoverageMessages.CoveragePackageEvent1, event1, instance);
         } catch (OseeCoreException ex) {
            OseeLog.log(Activator.class, Level.SEVERE, ex);
         }
      }
   }

   public void register(CoverageEditor coverageEditor) {
      editors.add(coverageEditor);
      startListeningForRemoteCoverageEvents();
      startListeningForFrameworkEvents();
   }

   public void unregister(CoverageEditor coverageEditor) {
      editors.remove(coverageEditor);
      if (editors.isEmpty()) {
         stopListeningForRemoteCoverageEvents();
         stopListeneingForFrameworkEvents();
      }
   }

   @Override
   public void handleArtifactEvent(ArtifactEvent artifactEvent, Sender sender) {
      for (CoverageEditor editor : new CopyOnWriteArrayList<CoverageEditor>(editors)) {
         try {
            for (EventBasicGuidArtifact eventArt : artifactEvent.getArtifacts()) {
               if (editor.getCoverageEditorInput().getCoveragePackageArtifact() == null) {
                  return;
               }
               if (!editor.getCoverageEditorInput().getCoveragePackageArtifact().getBranch().getGuid().equals(
                  eventArt.getBranchGuid())) {
                  return;
               }
               if (eventArt.is(EventModType.Deleted, EventModType.ChangeType, EventModType.Purged)) {
                  if (eventArt.getGuid().equals(editor.getCoverageEditorInput().getCoveragePackageArtifact().getGuid())) {
                     unregister(editor);
                     editor.closeEditor();
                  }
               }
            }
         } catch (OseeCoreException ex) {
            OseeLog.log(Activator.class, Level.SEVERE, ex);
         }
      }
   }

   @Override
   public void fail(Throwable th) {
      OseeLog.log(CoverageEventManager.class, Level.SEVERE, th);
   }

   @Override
   public void success() {
      // do nothing
   }

   public class CoverageMessageListener extends OseeMessagingListener {

      public CoverageMessageListener() {
         super(CoveragePackageEvent1.class);
      }

      @Override
      public void process(final Object message, Map<String, Object> headers, ReplyConnection replyConnection) {
         PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
            @Override
            public void run() {
               CoveragePackageEvent1 coverageEvent1 = (CoveragePackageEvent1) message;
               try {
                  // Don't process this event if sent from this session
                  if (coverageEvent1.getSessionId().equals(ClientSessionManager.getSessionId())) {
                     return;
                  }
               } catch (Exception ex) {
                  OseeLog.log(Activator.class, Level.SEVERE, ex);
               }
               CoveragePackageEvent coverageEvent = getCoveragePackageEvent(coverageEvent1);
               if (coverageEvent != null) {
                  processCoveragePackageEvent(coverageEvent);
               }
            }
         });
      }
   }

   private void processCoveragePackageEvent(CoveragePackageEvent coverageEvent) {
      if (coverageEvent != null) {
         System.out.println(String.format("Receiving coverageEvent [%s]", coverageEvent.getPackage().getName()));
         CoverageChange packageCoverage = coverageEvent.getPackage();
         CoverageEventType packageModType = packageCoverage.getEventType();
         for (CoverageEditor editor : new CopyOnWriteArrayList<CoverageEditor>(editors)) {
            try {
               if (packageModType == CoverageEventType.Deleted) {
                  unregister(editor);
                  editor.closeEditor();
               } else if (packageModType == CoverageEventType.Modified) {
                  CoveragePackage coveragePackage = (CoveragePackage) editor.getCoveragePackageBase();
                  handleCoverageEditorSaveEvent(editor, coveragePackage, coverageEvent);
               }
            } catch (Exception ex) {
               OseeLog.log(Activator.class, Level.SEVERE, ex);
            }
         }
      }

   }

   private void handleCoverageEditorSaveEvent(CoverageEditor editor, CoveragePackage coveragePackage, CoveragePackageEvent coverageEvent) {
      System.out.println("handle coverage save event => " + coverageEvent);
   }

   @Override
   public List<? extends IEventFilter> getEventFilters() {
      return Arrays.asList(createArtifactTypeEventFilter());
   }

   private CoveragePackageEvent1 getCoveragePackageEvent(CoveragePackageEvent event) {
      CoveragePackageEvent1 event1 = new CoveragePackageEvent1();
      event1.setSessionId(event.getSessionId());
      CoverageChange1 change1 = new CoverageChange1();
      change1.setGuid(event.getPackage().getGuid());
      change1.setName(event.getPackage().getName());
      change1.setModTypeGuid(event.getPackage().getEventType().getGuid());
      event1.setPackage(change1);
      for (CoverageChange change : event.getCoverages()) {
         CoverageChange1 childChange1 = new CoverageChange1();
         childChange1.setGuid(change.getGuid());
         childChange1.setName(change.getName());
         childChange1.setModTypeGuid(change.getEventType().getGuid());
      }
      return event1;
   }

   private CoveragePackageEvent getCoveragePackageEvent(CoveragePackageEvent1 coveragePackageEvent1) {
      try {
         CoverageEventType packageEventType =
            CoverageEventType.getType(coveragePackageEvent1.getPackage().getModTypeGuid());
         if (packageEventType != null) {
            CoveragePackageEvent event =
               new CoveragePackageEvent(coveragePackageEvent1.getPackage().getName(),
                  coveragePackageEvent1.getPackage().getGuid(), packageEventType, coveragePackageEvent1.getSessionId());
            event.setSessionId(coveragePackageEvent1.getSessionId());
            for (CoverageChange1 change : coveragePackageEvent1.getCoverages()) {
               CoverageEventType eventType = CoverageEventType.getType(change.getGuid());
               if (eventType != null) {
                  event.getCoverages().add(new CoverageChange(change.getName(), change.getGuid(), eventType));
               } else {
                  OseeLog.log(Activator.class, Level.INFO,
                     "Unhandled coverage event type => " + coveragePackageEvent1.getPackage().getModTypeGuid());
               }
            }
            return event;
         } else {
            OseeLog.log(Activator.class, Level.INFO,
               "Unhandled package coverage event type => " + coveragePackageEvent1.getPackage().getModTypeGuid());
         }
      } catch (Exception ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
      return null;
   }
}
