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
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.osee.coverage.editor.CoverageEditor;
import org.eclipse.osee.coverage.internal.Activator;
import org.eclipse.osee.coverage.model.CoverageItem;
import org.eclipse.osee.coverage.model.CoveragePackage;
import org.eclipse.osee.coverage.model.CoverageUnit;
import org.eclipse.osee.coverage.model.ICoverage;
import org.eclipse.osee.coverage.model.IWorkProductTaskProvider;
import org.eclipse.osee.coverage.model.WorkProductTask;
import org.eclipse.osee.coverage.msgs.CoverageChange1;
import org.eclipse.osee.coverage.msgs.CoveragePackageEvent1;
import org.eclipse.osee.coverage.store.CoverageArtifactTypes;
import org.eclipse.osee.coverage.store.OseeCoveragePackageStore;
import org.eclipse.osee.coverage.store.OseeCoverageUnitStore;
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
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
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
               CoverageArtifactTypes.CoveragePackage,
               SkynetGuiPlugin.getInstance().getOseeCmService().getPcrArtifactType(),
               SkynetGuiPlugin.getInstance().getOseeCmService().getPcrTaskArtifactType());
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

   public void sendRemoteEvent(final CoveragePackageEvent coverageEvent) {
      Job job = new Job("Sending Coverage Event") {

         @Override
         protected IStatus run(IProgressMonitor monitor) {
            try {
               Thread.sleep(1000);
            } catch (InterruptedException ex1) {
               // do nothing
            }
            //            System.out.println(String.format("Sending CoveragePackageEvent %d items [%s]",
            //               coverageEvent.getCoverages().size(), coverageEvent.getPackage().getName()));
            if (connectionNode != null) {
               try {
                  CoveragePackageEvent1 event1 = getCoveragePackageEvent(coverageEvent);
                  connectionNode.send(CoverageMessages.CoveragePackageEvent1, event1, instance);
               } catch (OseeCoreException ex) {
                  OseeLog.log(Activator.class, Level.SEVERE, ex);
               }
            }
            return Status.OK_STATUS;
         }
      };
      job.schedule();
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
            if (!editor.getBranch().getGuid().equals(artifactEvent.getBranchGuid())) {
               return;
            }
            boolean updatedWorkProductCache = false;
            for (EventBasicGuidArtifact eventArt : artifactEvent.getArtifacts()) {
               if (editor.getCoverageEditorInput().getCoveragePackageArtifact() == null) {
                  return;
               }
               checkForCoveragePackageDeletion(editor, eventArt);
               // Only update work product cache once
               if (!updatedWorkProductCache) {
                  updatedWorkProductCache = checkForWorkProductTaskModified(editor, eventArt);
               }
            }
         } catch (OseeCoreException ex) {
            OseeLog.log(Activator.class, Level.SEVERE, ex);
         }
      }
   }

   private boolean checkForWorkProductTaskModified(CoverageEditor editor, EventBasicGuidArtifact eventArt) {
      try {
         CoveragePackage coveragePackage = (CoveragePackage) editor.getCoverageEditorInput().getCoveragePackageBase();
         // if one of the related tasks is modified
         if (isWorkProductTasksEquals(coveragePackage.getWorkProductTaskProvider(), eventArt)) {
            // reload the related actions/tasks
            coveragePackage.getWorkProductTaskProvider().reload();
            // reset the product task names
            OseeCoveragePackageStore cpStore = new OseeCoveragePackageStore(coveragePackage, editor.getBranch());
            cpStore.loadWorkProductTaskNames(coveragePackage.getCoverageUnits());
            // refresh the editors
            editor.refreshWorkProductTasks();
            return true;
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
      return false;
   }

   private boolean isWorkProductTasksEquals(IWorkProductTaskProvider taskProvider, EventBasicGuidArtifact eventArt) {
      for (WorkProductTask task : taskProvider.getWorkProductTasks()) {
         if (task.getGuid().equals(eventArt.getGuid())) {
            return true;
         }
      }
      return false;
   }

   private void checkForCoveragePackageDeletion(CoverageEditor editor, EventBasicGuidArtifact eventArt) {
      try {
         if (!eventArt.getGuid().equals(editor.getCoverageEditorInput().getCoveragePackageArtifact().getGuid())) {
            return;
         }
         if (eventArt.is(EventModType.Deleted, EventModType.ChangeType, EventModType.Purged)) {
            unregister(editor);
            editor.closeEditor();
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
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
         //         System.out.println(String.format("Receiving coverageEvent [%s]", coverageEvent.getPackage().getName()));
         CoverageChange packageCoverage = coverageEvent.getPackage();
         CoverageEventType packageModType = packageCoverage.getEventType();
         for (CoverageEditor editor : new CopyOnWriteArrayList<CoverageEditor>(editors)) {
            try {
               if (coverageEvent.getPackage().getGuid().equals(editor.getCoveragePackageBase().getGuid())) {
                  if (packageModType == CoverageEventType.Deleted) {
                     unregister(editor);
                     editor.closeEditor();
                  } else if (packageModType == CoverageEventType.Modified) {
                     CoveragePackage coveragePackage = (CoveragePackage) editor.getCoveragePackageBase();
                     handleCoverageEditorSaveEvent(editor, coveragePackage, coverageEvent);
                  }
               }
            } catch (Exception ex) {
               OseeLog.log(Activator.class, Level.SEVERE, ex);
            }
         }
      }

   }

   private void handleCoverageEditorSaveEvent(CoverageEditor editor, CoveragePackage coveragePackage, CoveragePackageEvent coverageEvent) {
      //      System.out.println("handle coverage save event => " + coverageEvent.getCoverages().size() + " items");
      for (CoverageChange change : coverageEvent.getCoverages()) {
         if (change.getEventType() == CoverageEventType.Modified) {
            reloadCoverage(editor, coveragePackage, change);
         }
      }
   }

   private void reloadCoverage(CoverageEditor editor, CoveragePackage coveragePackage, CoverageChange change) {
      //      System.out.println("handle reloadCoverage coverage => " + change);
      ICoverage coverage = coveragePackage.getCoverage(change.getGuid());
      if (coverage != null) {
         if (coverage instanceof CoverageItem) {
            try {
               CoverageUnit parent = (CoverageUnit) ((CoverageItem) coverage).getParent();
               OseeCoverageUnitStore store = new OseeCoverageUnitStore(parent, editor.getBranch());
               store.reloadItem(change.getEventType(), (CoverageItem) coverage, change,
                  coveragePackage.getCoverageOptionManager());
               OseeCoveragePackageStore cpStore = new OseeCoveragePackageStore(coveragePackage, editor.getBranch());
               cpStore.loadWorkProductTaskNames(Arrays.asList(coverage));
               editor.refresh(coverage);
            } catch (OseeCoreException ex) {
               OseeLog.log(Activator.class, Level.SEVERE, ex);
            }
         } else if (coverage instanceof CoverageUnit) {
            try {
               OseeCoverageUnitStore store = new OseeCoverageUnitStore((CoverageUnit) coverage, editor.getBranch());
               store.load(coveragePackage.getCoverageOptionManager());
               OseeCoveragePackageStore cpStore = new OseeCoveragePackageStore(coveragePackage, editor.getBranch());
               cpStore.loadWorkProductTaskNames(Arrays.asList(coverage));
               editor.refresh(coverage);
            } catch (OseeCoreException ex) {
               OseeLog.log(Activator.class, Level.SEVERE, ex);
            }
         }
      }
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
         event1.getCoverages().add(childChange1);
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
               CoverageEventType eventType = CoverageEventType.getType(change.getModTypeGuid());
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
