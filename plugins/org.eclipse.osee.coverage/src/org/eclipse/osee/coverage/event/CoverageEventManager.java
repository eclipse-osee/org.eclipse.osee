/*
 * Created on Mar 22, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.coverage.event;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import org.eclipse.osee.coverage.editor.CoverageEditor;
import org.eclipse.osee.coverage.internal.Activator;
import org.eclipse.osee.coverage.msgs.CoveragePackageSave;
import org.eclipse.osee.coverage.store.CoverageArtifactTypes;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.messaging.ConnectionNode;
import org.eclipse.osee.framework.messaging.OseeMessagingListener;
import org.eclipse.osee.framework.messaging.OseeMessagingStatusCallback;
import org.eclipse.osee.framework.messaging.ReplyConnection;
import org.eclipse.osee.framework.skynet.core.event.OseeEventManager;
import org.eclipse.osee.framework.skynet.core.event.Sender;
import org.eclipse.osee.framework.skynet.core.event.artifact.EventBasicGuidArtifact;
import org.eclipse.osee.framework.skynet.core.event.artifact.EventModType;
import org.eclipse.osee.framework.skynet.core.event.artifact.IArtifactListener;
import org.eclipse.osee.framework.skynet.core.event.filter.ArtifactTypeEventFilter;
import org.eclipse.osee.framework.skynet.core.event.filter.FilteredEventListener;
import org.eclipse.ui.PlatformUI;

/**
 * @author Donald G. Dunne
 */
public class CoverageEventManager implements IArtifactListener, OseeMessagingStatusCallback {

   private static CoverageEventManager instance;
   private List<CoverageEditor> editors = new ArrayList<CoverageEditor>();
   private ArtifactTypeEventFilter artifactTypeEventFilter;
   private FilteredEventListener filteredEventListener;
   private ConnectionNode connectionNode;
   private OseeMessagingTracker oseeMessagingTracker;

   private CoverageEventManager() {
   }

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
      if (filteredEventListener != null) {
         OseeEventManager.removeListener(filteredEventListener);
      }
   }

   private void startListeningForFrameworkEvents() {
      if (filteredEventListener == null) {
         filteredEventListener = new FilteredEventListener(this, artifactTypeEventFilter);
      }
      if (artifactTypeEventFilter == null) {
         artifactTypeEventFilter =
               new ArtifactTypeEventFilter(CoverageArtifactTypes.CoverageFolder, CoverageArtifactTypes.CoverageUnit,
                     CoverageArtifactTypes.CoveragePackage);
      }
      OseeEventManager.addListener(filteredEventListener);
   }

   private void startListeningForRemoteCoverageEvents() {
      if (oseeMessagingTracker == null) {
         oseeMessagingTracker = new OseeMessagingTracker();
         oseeMessagingTracker.open(true);
      }
   }

   private void stopListeningForRemoteCoverageEvents() {
      oseeMessagingTracker.close();
      oseeMessagingTracker = null;
   }

   public void addingRemoteEventService(ConnectionNode connectionNode) {
      this.connectionNode = connectionNode;
      connectionNode.subscribe(CoverageMessages.CoveragePackageSave, new CoverageMessageListener(), instance);
   }

   public void sendRemoteEvent(CoveragePackageSave packSave) {
      System.out.println(String.format("Sending CoveragePackageSave [%s]", packSave.getName()));
      if (connectionNode != null) {
         try {
            connectionNode.send(CoverageMessages.CoveragePackageSave, packSave, instance);
         } catch (OseeCoreException ex) {
            OseeLog.log(Activator.class, OseeLevel.SEVERE, ex);
         }
      }
   }

   public void register(CoverageEditor coverageEditor) throws OseeCoreException {
      editors.add(coverageEditor);
      startListeningForRemoteCoverageEvents();
      startListeningForFrameworkEvents();
   }

   public void unregister(CoverageEditor coverageEditor) throws OseeCoreException {
      editors.remove(coverageEditor);
      if (editors.size() == 0) {
         stopListeningForRemoteCoverageEvents();
         stopListeneingForFrameworkEvents();
      }
   }

   @Override
   public void handleArtifactModified(Collection<EventBasicGuidArtifact> eventArtifacts, Sender sender) {
      for (CoverageEditor editor : editors) {
         try {
            for (EventBasicGuidArtifact eventArt : eventArtifacts) {
               if (editor.getCoverageEditorInput().getCoveragePackageArtifact() == null) return;
               if (editor.getCoverageEditorInput().getCoveragePackageArtifact().getBranch().getGuid() != eventArt.getBranchGuid()) return;
               if (eventArt.getModType() == EventModType.Deleted || eventArt.getModType() == EventModType.ChangeType || eventArt.getModType() == EventModType.Purged) {
                  if (eventArt.getGuid().equals(editor.getCoverageEditorInput().getCoveragePackageArtifact().getGuid())) {
                     unregister(editor);
                     editor.closeEditor();
                  }
               }
            }
         } catch (OseeCoreException ex) {
            OseeLog.log(Activator.class, OseeLevel.SEVERE, ex);
         }
      }
   }

   @Override
   public void fail(Throwable th) {
      OseeLog.log(CoverageEventManager.class, Level.SEVERE, th);
   }

   @Override
   public void success() {
   }

   public class CoverageMessageListener extends OseeMessagingListener {

      public CoverageMessageListener() {
         super(CoveragePackageSave.class);
      }

      @Override
      public void process(final Object message, Map<String, Object> headers, ReplyConnection replyConnection) {
         PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
            @Override
            public void run() {
               CoveragePackageSave packSave = (CoveragePackageSave) message;
               System.out.println(String.format("Receiving CoveragePackageSave [%s]", packSave.getName()));
            }
         });
      }
   }

}
