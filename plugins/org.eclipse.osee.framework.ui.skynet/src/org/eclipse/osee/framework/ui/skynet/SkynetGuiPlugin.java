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
package org.eclipse.osee.framework.ui.skynet;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osee.framework.core.client.ClientSessionManager;
import org.eclipse.osee.framework.core.exception.OseeAuthenticationRequiredException;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.services.IOseeCachingService;
import org.eclipse.osee.framework.core.util.ServiceDependencyTracker;
import org.eclipse.osee.framework.database.IOseeDatabaseService;
import org.eclipse.osee.framework.database.IOseeDatabaseServiceProvider;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.event.OseeEventManager;
import org.eclipse.osee.framework.skynet.core.event.listener.IBroadcastEventListener;
import org.eclipse.osee.framework.skynet.core.event.model.BroadcastEvent;
import org.eclipse.osee.framework.skynet.core.event.model.BroadcastEventType;
import org.eclipse.osee.framework.skynet.core.event.model.Sender;
import org.eclipse.osee.framework.skynet.core.utility.DbUtil;
import org.eclipse.osee.framework.ui.plugin.OseeFormActivator;
import org.eclipse.osee.framework.ui.skynet.artifact.ArtifactSaveNotificationHandler;
import org.eclipse.osee.framework.ui.skynet.artifact.IAccessPolicyHandlerService;
import org.eclipse.osee.framework.ui.skynet.cm.IOseeCmService;
import org.eclipse.osee.framework.ui.skynet.internal.AccessPolicyServiceRegHandler;
import org.eclipse.osee.framework.ui.skynet.internal.ArtifactPromptService;
import org.eclipse.osee.framework.ui.skynet.internal.ArtifactPromptServiceRegHandler;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchListener;
import org.eclipse.ui.PlatformUI;
import org.osgi.framework.BundleContext;
import org.osgi.service.packageadmin.PackageAdmin;
import org.osgi.util.tracker.ServiceTracker;

public class SkynetGuiPlugin extends OseeFormActivator implements IBroadcastEventListener, IOseeDatabaseServiceProvider {
   private static SkynetGuiPlugin pluginInstance; // The shared instance.
   public static final String PLUGIN_ID = "org.eclipse.osee.framework.ui.skynet";
   public static final String CHANGE_REPORT_ATTRIBUTES_PREF =
      "org.eclipse.osee.framework.ui.skynet.changeReportAttributes";
   public static final String ARTIFACT_EXPLORER_ATTRIBUTES_PREF =
      "org.eclipse.osee.framework.ui.skynet.artifactExplorerAttributes";

   public static final String ARTIFACT_SEARCH_RESULTS_ATTRIBUTES_PREF =
      "org.eclipse.osee.framework.ui.skynet.artifactSearchResultsAttributes";
   private ServiceTracker packageAdminTracker;
   private ServiceTracker cacheServiceTracker;
   private ServiceTracker databaseServiceTracker;
   private ServiceTracker cmServiceTracker;

   private final Map<String, ServiceDependencyTracker> trackers = new HashMap<String, ServiceDependencyTracker>();

   public SkynetGuiPlugin() {
      super();
      pluginInstance = this;
   }

   @Override
   public void stop(BundleContext context) throws Exception {
      super.stop(context);
      packageAdminTracker.close();
      cacheServiceTracker.close();
      databaseServiceTracker.close();
      cmServiceTracker.close();

      for (ServiceDependencyTracker tracker : trackers.values()) {
         Lib.close(tracker);
      }
   }

   @Override
   public void start(BundleContext context) throws Exception {
      super.start(context);

      packageAdminTracker = new ServiceTracker(context, PackageAdmin.class.getName(), null);
      packageAdminTracker.open();

      cacheServiceTracker = new ServiceTracker(context, IOseeCachingService.class.getName(), null);
      cacheServiceTracker.open();

      databaseServiceTracker = new ServiceTracker(context, IOseeDatabaseService.class.getName(), null);
      databaseServiceTracker.open();

      cmServiceTracker = new ServiceTracker(context, IOseeCmService.class.getName(), null);
      cmServiceTracker.open();

      trackers.put(IAccessPolicyHandlerService.class.getName(), new ServiceDependencyTracker(context,
         new AccessPolicyServiceRegHandler()));

      trackers.put(ArtifactPromptService.class.getName(), new ServiceDependencyTracker(context,
         new ArtifactPromptServiceRegHandler()));

      for (ServiceDependencyTracker tracker : trackers.values()) {
         tracker.open();
      }

      OseeEventManager.addListener(this);

      if (PlatformUI.isWorkbenchRunning()) {

         OseeLog.registerLoggerListener(new DialogPopupLoggerListener());

         IWorkbench workbench = PlatformUI.getWorkbench();
         workbench.addWorkbenchListener(new IWorkbenchListener() {

            @Override
            public void postShutdown(IWorkbench workbench) {
               // do nothing
            }

            @Override
            public boolean preShutdown(IWorkbench workbench, boolean forced) {
               if (!DbUtil.isDbInit()) {
                  try {
                     UserManager.getUser().saveSettings();
                  } catch (Throwable th) {
                     th.printStackTrace();
                  }
               }
               return true;
            }
         });

         PlatformUI.getWorkbench().addWorkbenchListener(new ArtifactSaveNotificationHandler());
      }
   }

   public static SkynetGuiPlugin getInstance() {
      return pluginInstance;
   }

   @Override
   protected String getPluginName() {
      return PLUGIN_ID;
   }

   public PackageAdmin getPackageAdmin() {
      return (PackageAdmin) this.packageAdminTracker.getService();
   }

   public IOseeCachingService getOseeCacheService() {
      return (IOseeCachingService) cacheServiceTracker.getService();
   }

   @Override
   public IOseeDatabaseService getOseeDatabaseService() {
      return (IOseeDatabaseService) databaseServiceTracker.getService();
   }

   public IOseeCmService getOseeCmService() {
      return (IOseeCmService) cmServiceTracker.getService();
   }

   public IAccessPolicyHandlerService getPolicyHandlerService() throws OseeCoreException {
      return ((AccessPolicyServiceRegHandler) trackers.get(IAccessPolicyHandlerService.class.getName()).getHandler()).getService();
   }

   public ArtifactPromptService getArtifactPromptService() throws OseeCoreException {
      return ((ArtifactPromptServiceRegHandler) trackers.get(ArtifactPromptService.class.getName()).getHandler()).getService();
   }

   @Override
   public void handleBroadcastEvent(Sender sender, final BroadcastEvent broadcastEvent) {

      // Determine whether this is a shutdown event
      // Prevent shutting down users without a valid message
      if (broadcastEvent.getBroadcastEventType() == BroadcastEventType.Force_Shutdown) {
         if (broadcastEvent.getMessage() == null || broadcastEvent.getMessage().length() == 0) {
            return;
         }
         Displays.ensureInDisplayThread(new Runnable() {
            @Override
            public void run() {
               boolean isShutdownRequest = false;
               try {
                  isShutdownRequest = broadcastEvent.getUsers().contains(UserManager.getUser());
               } catch (OseeCoreException ex) {
                  // do nothing
               }
               if (isShutdownRequest) {
                  MessageDialog.openWarning(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
                     "Shutdown Requested", broadcastEvent.getMessage());
                  // Shutdown the bench when this event is received
                  PlatformUI.getWorkbench().close();
               }
            }
         });
      } else if (broadcastEvent.getBroadcastEventType() == BroadcastEventType.Message) {
         if (broadcastEvent.getMessage() == null || broadcastEvent.getMessage().length() == 0) {
            return;
         }
         Displays.ensureInDisplayThread(new Runnable() {
            @Override
            public void run() {
               MessageDialog.openInformation(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
                  "Remote Message", broadcastEvent.getMessage());
            }
         });
      } else if (broadcastEvent.getBroadcastEventType() == BroadcastEventType.Ping) {
         // Another client ping'd this client for session information; Pong back with
         // original client's session id so it can be identified as the correct pong
         try {
            OseeEventManager.kickBroadcastEvent(this, new BroadcastEvent(BroadcastEventType.Pong, null,
               sender.getOseeSession().toString()));
         } catch (Exception ex) {
            OseeLog.log(SkynetGuiPlugin.class, OseeLevel.SEVERE_POPUP, ex);
         }
      } else if (broadcastEvent.getBroadcastEventType() == BroadcastEventType.Pong) {
         // Got pong from another client; If message == this client's sessionId, then it's
         // the response from this client's ping
         try {
            if (broadcastEvent.getMessage() != null && broadcastEvent.getMessage().equals(
               ClientSessionManager.getSession().toString())) {
               OseeLog.log(SkynetGuiPlugin.class, Level.INFO, "Pong: " + sender.toString());
            }
         } catch (OseeAuthenticationRequiredException ex) {
            OseeLog.log(SkynetGuiPlugin.class, Level.SEVERE, "Pong: " + sender.toString(), ex);
         }
      }
   }

}