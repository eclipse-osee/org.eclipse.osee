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
package org.eclipse.osee.framework.skynet.core.remoteEvent;

import java.rmi.RemoteException;
import java.rmi.server.ExportException;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.jini.core.entry.Entry;
import net.jini.core.lookup.ServiceItem;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osee.framework.jdk.core.util.OseeProperties;
import org.eclipse.osee.framework.jini.discovery.EclipseJiniClassloader;
import org.eclipse.osee.framework.jini.discovery.IServiceLookupListener;
import org.eclipse.osee.framework.jini.discovery.ServiceDataStore;
import org.eclipse.osee.framework.jini.service.core.SimpleFormattedEntry;
import org.eclipse.osee.framework.jini.util.OseeJini;
import org.eclipse.osee.framework.messaging.event.skynet.ASkynetEventListener;
import org.eclipse.osee.framework.messaging.event.skynet.ISkynetArtifactEvent;
import org.eclipse.osee.framework.messaging.event.skynet.ISkynetEvent;
import org.eclipse.osee.framework.messaging.event.skynet.ISkynetEventListener;
import org.eclipse.osee.framework.messaging.event.skynet.ISkynetEventService;
import org.eclipse.osee.framework.messaging.event.skynet.ISkynetRelationLinkEvent;
import org.eclipse.osee.framework.messaging.event.skynet.NetworkCommitBranchEvent;
import org.eclipse.osee.framework.messaging.event.skynet.NetworkDeletedBranchEvent;
import org.eclipse.osee.framework.messaging.event.skynet.NetworkNewBranchEvent;
import org.eclipse.osee.framework.messaging.event.skynet.NetworkRenameBranchEvent;
import org.eclipse.osee.framework.messaging.event.skynet.event.NetworkBroadcastEvent;
import org.eclipse.osee.framework.messaging.event.skynet.event.SkynetDisconnectClientsEvent;
import org.eclipse.osee.framework.plugin.core.config.ConfigUtil;
import org.eclipse.osee.framework.plugin.core.config.data.DbDetailData;
import org.eclipse.osee.framework.skynet.core.PersistenceManager;
import org.eclipse.osee.framework.skynet.core.PersistenceManagerInit;
import org.eclipse.osee.framework.skynet.core.SkynetAuthentication;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactPersistenceManager;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.artifact.BranchPersistenceManager;
import org.eclipse.osee.framework.skynet.core.event.RemoteCommitBranchEvent;
import org.eclipse.osee.framework.skynet.core.event.RemoteDeletedBranchEvent;
import org.eclipse.osee.framework.skynet.core.event.RemoteNewBranchEvent;
import org.eclipse.osee.framework.skynet.core.event.RemoteRenameBranchEvent;
import org.eclipse.osee.framework.skynet.core.event.RemoteTransactionEvent;
import org.eclipse.osee.framework.skynet.core.event.SkynetEventManager;
import org.eclipse.osee.framework.skynet.core.event.SkynetServiceEvent;
import org.eclipse.osee.framework.skynet.core.relation.RelationPersistenceManager;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionId;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionIdManager;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionIdManager.TransactionSwitch;
import org.eclipse.osee.framework.ui.plugin.event.Event;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;

/**
 * Manages remote events from the SkynetEventService.
 * 
 * @author Jeff C. Phillips
 */
public class RemoteEventManager implements IServiceLookupListener, PersistenceManager {

   private static final Logger logger = ConfigUtil.getConfigFactory().getLogger(RemoteEventManager.class);
   private static String ACCEPTABLE_SERVICE;
   private ISkynetEventService skynetEventService;
   private ASkynetEventListener listener;
   private ISkynetEventListener myReference;

   private SkynetEventManager eventManager;
   private static ArtifactPersistenceManager artifactPersistenceManager;
   private static RelationPersistenceManager relationPersistenceManager;
   private static TransactionIdManager transactionIdManager;
   private static BranchPersistenceManager branchPersistenceManager;
   private static SkynetAuthentication skynetAuthentication;

   private static final RemoteEventManager instance = new RemoteEventManager();

   private RemoteEventManager() {
      super();

      DbDetailData dbData = ConfigUtil.getConfigFactory().getOseeConfig().getDefaultClientData().getDatabaseDetails();
      String dbName = dbData.getFieldValue(DbDetailData.ConfigField.DatabaseName);
      String userName = dbData.getFieldValue(DbDetailData.ConfigField.UserName);

      ACCEPTABLE_SERVICE = dbName + ":" + userName;

      try {
         this.listener = new EventListener();
         this.myReference = (ISkynetEventListener) OseeJini.getRemoteReference(listener);
         this.eventManager = SkynetEventManager.getInstance();

         addListenerForEventService();
      } catch (ExportException e) {
         logger.log(Level.SEVERE, e.toString(), e);
      }
   }

   /**
    * Returns a RemoteEventManager reference;
    */
   public static RemoteEventManager getInstance() {
      PersistenceManagerInit.initManagerWeb(instance);
      return instance;
   }

   public void onManagerWebInit() throws Exception {
      artifactPersistenceManager = ArtifactPersistenceManager.getInstance();
      relationPersistenceManager = RelationPersistenceManager.getInstance();
      transactionIdManager = TransactionIdManager.getInstance();
      branchPersistenceManager = BranchPersistenceManager.getInstance();
      skynetAuthentication = SkynetAuthentication.getInstance();
   }

   public void kick(final ISkynetEvent... events) {
      if (skynetEventService != null) {

         Job job = new Job("Send Event") {

            @Override
            protected IStatus run(IProgressMonitor monitor) {
               try {
                  skynetEventService.kick(events, myReference);
               } catch (ExportException e) {
                  logger.log(Level.SEVERE, e.toString(), e);
               } catch (RemoteException e) {
                  disconnectService(e);
               }
               return Status.OK_STATUS;
            }
         };

         job.schedule();
      }
   }

   private void addListenerForEventService() {
      if (!ConfigUtil.getConfigFactory().getOseeConfig().isDisableRemoteEvents()) ServiceDataStore.getEclipseInstance(
            EclipseJiniClassloader.getInstance()).addListener(this, ISkynetEventService.class);
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.framework.jini.discovery.IServiceLookupListener#serviceAdded(net.jini.core.lookup.ServiceItem)
    */
   public void serviceAdded(ServiceItem serviceItem) {

      if (serviceItem.service instanceof ISkynetEventService) {
         boolean acceptable = false;

         // Check if the service is for the database we are using
         for (Entry entry : serviceItem.attributeSets) {
            if (entry instanceof SimpleFormattedEntry) {
               SimpleFormattedEntry simpleEntry = (SimpleFormattedEntry) entry;
               acceptable = "db".equals(simpleEntry.name) && ACCEPTABLE_SERVICE.equals(simpleEntry.value);
               if (acceptable) break;
            }
         }

         if (acceptable) {
            connectToService((ISkynetEventService) serviceItem.service);
         }
      }
   }

   private void connectToService(ISkynetEventService service) {
      skynetEventService = service;

      try {
         logger.log(Level.INFO, "Skynet Event Service connection established " + ACCEPTABLE_SERVICE);
         skynetEventService.register(myReference);
         eventManager.kick(new SkynetServiceEvent(this, true));

      } catch (ExportException e) {
         logger.log(Level.SEVERE, e.toString(), e);
      } catch (RemoteException e) {
         disconnectService(e);
      }
   }

   private void disconnectService(Exception e) {
      logger.log(Level.WARNING, "Skynet Event Service connection lost\n" + e.toString(), e);
      skynetEventService = null;
      eventManager.kick(new SkynetServiceEvent(this, false));
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.framework.jini.discovery.IServiceLookupListener#serviceChanged(net.jini.core.lookup.ServiceItem)
    */
   public void serviceChanged(ServiceItem serviceItem) {
      serviceAdded(serviceItem);
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.framework.jini.discovery.IServiceLookupListener#serviceRemoved(net.jini.core.lookup.ServiceItem)
    */
   public void serviceRemoved(ServiceItem serviceItem) {
   }

   /**
    * @author Jeff C. Phillips
    */
   private static class EventListener extends ASkynetEventListener {
      private static final long serialVersionUID = -3017349745450262540L;
      private TransactionId editableTransactionId;
      private TransactionId priorEditableTransactionId;
      private SkynetEventManager eventManager = SkynetEventManager.getInstance();
      private static final ISchedulingRule mutexRule = new ISchedulingRule() {

         public boolean contains(ISchedulingRule rule) {
            return rule == this;
         }

         public boolean isConflicting(ISchedulingRule rule) {
            return rule == this;
         }
      };

      private void handleBroadcastMessageEvent(NetworkBroadcastEvent event) {
         final String message = event.getMessage();
         if (message != null && message.length() > 0) {
            boolean isShutdownAllowed = false;

            // Determine whether this is a shutdown event
            // Prevent shutting down users without a valid message
            if (event instanceof SkynetDisconnectClientsEvent) {
               String[] userIds = ((SkynetDisconnectClientsEvent) event).getUserIds();
               User user = skynetAuthentication.getAuthenticatedUser();
               String userId = user != null ? user.getUserId() : "";
               for (String temp : userIds) {
                  if (temp.equals(userId)) {
                     isShutdownAllowed = true;
                     break;
                  }
               }
            }
            final boolean isShutdownRequest = isShutdownAllowed;
            Display.getDefault().asyncExec(new Runnable() {
               public void run() {
                  if (false != isShutdownRequest) {
                     MessageDialog.openWarning(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
                           "Shutdown Requested", message);
                     // Shutdown the bench when this event is received
                     PlatformUI.getWorkbench().close();
                  } else {
                     MessageDialog.openInformation(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
                           "Remote Message", message);
                  }
               }
            });
         }
      }

      @Override
      public void onEvent(final ISkynetEvent[] events) throws RemoteException {

         final List<Event> localEvents = new LinkedList<Event>();
         Job job = new Job("Receive Event") {

            private void checkTransactionIds(ISkynetEvent event) {
               int newTransactionNumber = event.getTransactionId();
               int branchId = -1;

               if (priorEditableTransactionId == null && editableTransactionId == null || newTransactionNumber > editableTransactionId.getTransactionNumber()) {
                  try {
                     branchId = event.getBranchId();
                     Branch branch = BranchPersistenceManager.getInstance().getBranch(branchId);

                     if (branch == null) {
                        throw new IllegalArgumentException(
                              "No branch could be found associated with this branch id: " + branchId);
                     }

                     TransactionSwitch transactionSwitch = transactionIdManager.switchTransaction(branch);
                     editableTransactionId = transactionSwitch.getEditableTransactionId();
                     priorEditableTransactionId = transactionSwitch.getNonEditableTransactionId();
                  } catch (SQLException e) {
                     logger.log(Level.SEVERE, e.toString(), e);
                  } catch (IllegalArgumentException e) {
                     if (OseeProperties.getInstance().isDeveloper()) {
                        throw e;
                     } else {
                        // The event will be set to null because it can not be processed with illegal arguments
                        event = null;
                        logger.log(Level.SEVERE, e.toString(), e + " Branch id: " + branchId);
                     }
                  }
               }
            }

            private void clearTransactionIds() {
               priorEditableTransactionId = null;
               editableTransactionId = null;
            }

            @Override
            protected IStatus run(IProgressMonitor monitor) {
               Arrays.sort(events);

               for (ISkynetEvent event : events) {

                  if (event instanceof NetworkRenameBranchEvent) {
                     int branchId = ((NetworkRenameBranchEvent) event).getBranchId();
                     try {
                        Branch branch = branchPersistenceManager.getBranch(branchId);
                        branch.setBranchName(((NetworkRenameBranchEvent) event).getBranchName());
                        branch.setBranchShortName(((NetworkRenameBranchEvent) event).getShortName(), false);
                        eventManager.kick(new RemoteRenameBranchEvent(this, branchId, branch.getBranchName(),
                              branch.getBranchShortName()));
                     } catch (Exception ex) {
                        logger.log(Level.SEVERE, ex.toString(), ex);
                     }
                  } else if (event instanceof NetworkNewBranchEvent) {
                     eventManager.kick(new RemoteNewBranchEvent(this, ((NetworkNewBranchEvent) event).getBranchId()));
                  } else if (event instanceof NetworkDeletedBranchEvent) {
                     int branchId = ((NetworkDeletedBranchEvent) event).getBranchId();
                     branchPersistenceManager.removeBranchFromCache(branchId);
                     eventManager.kick(new RemoteDeletedBranchEvent(this, branchId));
                  } else if (event instanceof NetworkCommitBranchEvent) {
                     int branchId = ((NetworkCommitBranchEvent) event).getBranchId();
                     branchPersistenceManager.removeBranchFromCache(branchId);
                     eventManager.kick(new RemoteCommitBranchEvent(this, branchId));
                  } else if (event instanceof NetworkBroadcastEvent) {
                     handleBroadcastMessageEvent((NetworkBroadcastEvent) event);
                  } else if (event instanceof ISkynetArtifactEvent) {
                     ISkynetArtifactEvent skynetEvent = (ISkynetArtifactEvent) event;
                     checkTransactionIds(skynetEvent);

                     if (skynetEvent != null) {
                        artifactPersistenceManager.updateArtifactCache(skynetEvent, localEvents, editableTransactionId,
                              priorEditableTransactionId);
                     }
                  } else if (event instanceof ISkynetRelationLinkEvent) {

                     ISkynetRelationLinkEvent skynetRelationLinkEvent = (ISkynetRelationLinkEvent) event;
                     checkTransactionIds(skynetRelationLinkEvent);

                     if (skynetRelationLinkEvent != null) {
                        relationPersistenceManager.updateRelationCache(skynetRelationLinkEvent, localEvents,
                              editableTransactionId, priorEditableTransactionId);
                     }
                  } else {
                     logger.log(Level.INFO, "Unexpected ISkynetEvent " + event.getClass().getName());
                  }
               }
               eventManager.kick(new RemoteTransactionEvent(localEvents, this));

               clearTransactionIds();
               return Status.OK_STATUS;
            }
         };
         job.setSystem(true);
         job.setUser(false);
         job.setRule(mutexRule);
         job.schedule();
      }
   }

   public boolean isConnected() {
      try {
         return skynetEventService != null && skynetEventService.isAlive();
      } catch (RemoteException ex) {
         logger.log(Level.SEVERE, ex.toString(), ex);
         return false;
      }

   }

}
