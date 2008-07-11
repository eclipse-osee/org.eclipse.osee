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
package org.eclipse.osee.framework.skynet.core.artifact;

import java.rmi.RemoteException;
import java.rmi.server.ExportException;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
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
import org.eclipse.osee.framework.db.connection.OseeDb;
import org.eclipse.osee.framework.db.connection.info.DbDetailData;
import org.eclipse.osee.framework.jini.discovery.EclipseJiniClassloader;
import org.eclipse.osee.framework.jini.discovery.IServiceLookupListener;
import org.eclipse.osee.framework.jini.discovery.ServiceDataStore;
import org.eclipse.osee.framework.jini.service.core.SimpleFormattedEntry;
import org.eclipse.osee.framework.jini.util.OseeJini;
import org.eclipse.osee.framework.logging.OseeLog;
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
import org.eclipse.osee.framework.messaging.event.skynet.event.NetworkArtifactDeletedEvent;
import org.eclipse.osee.framework.messaging.event.skynet.event.NetworkArtifactModifiedEvent;
import org.eclipse.osee.framework.messaging.event.skynet.event.NetworkBroadcastEvent;
import org.eclipse.osee.framework.messaging.event.skynet.event.NetworkNewRelationLinkEvent;
import org.eclipse.osee.framework.messaging.event.skynet.event.NetworkRelationLinkDeletedEvent;
import org.eclipse.osee.framework.messaging.event.skynet.event.NetworkRelationLinkModifiedEvent;
import org.eclipse.osee.framework.messaging.event.skynet.event.SkynetAttributeChange;
import org.eclipse.osee.framework.messaging.event.skynet.event.SkynetDisconnectClientsEvent;
import org.eclipse.osee.framework.plugin.core.config.ConfigUtil;
import org.eclipse.osee.framework.skynet.core.SkynetActivator;
import org.eclipse.osee.framework.skynet.core.SkynetAuthentication;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactModifiedEvent.ModType;
import org.eclipse.osee.framework.skynet.core.attribute.Attribute;
import org.eclipse.osee.framework.skynet.core.event.RemoteCommitBranchEvent;
import org.eclipse.osee.framework.skynet.core.event.RemoteDeletedBranchEvent;
import org.eclipse.osee.framework.skynet.core.event.RemoteNewBranchEvent;
import org.eclipse.osee.framework.skynet.core.event.RemoteRenameBranchEvent;
import org.eclipse.osee.framework.skynet.core.event.RemoteTransactionEvent;
import org.eclipse.osee.framework.skynet.core.event.SkynetEventManager;
import org.eclipse.osee.framework.skynet.core.event.SkynetServiceEvent;
import org.eclipse.osee.framework.skynet.core.exception.ArtifactDoesNotExist;
import org.eclipse.osee.framework.skynet.core.relation.RelationLink;
import org.eclipse.osee.framework.skynet.core.relation.RelationManager;
import org.eclipse.osee.framework.skynet.core.relation.RelationSide;
import org.eclipse.osee.framework.skynet.core.relation.RelationType;
import org.eclipse.osee.framework.skynet.core.relation.RelationTypeManager;
import org.eclipse.osee.framework.skynet.core.relation.TransactionRelationModifiedEvent;
import org.eclipse.osee.framework.ui.plugin.event.Event;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;

/**
 * Manages remote events from the SkynetEventService.
 * 
 * @author Jeff C. Phillips
 */
public class RemoteEventManager implements IServiceLookupListener {

   private static final Logger logger = ConfigUtil.getConfigFactory().getLogger(RemoteEventManager.class);
   private static String ACCEPTABLE_SERVICE;
   private ISkynetEventService skynetEventService;
   private ASkynetEventListener listener;
   private ISkynetEventListener myReference;

   private static final RemoteEventManager instance = new RemoteEventManager();

   private RemoteEventManager() {
      super();

      DbDetailData dbData = OseeDb.getDefaultDatabaseService().getDatabaseDetails();
      String dbName = dbData.getFieldValue(DbDetailData.ConfigField.DatabaseName);
      String userName = dbData.getFieldValue(DbDetailData.ConfigField.UserName);

      ACCEPTABLE_SERVICE = dbName + ":" + userName;

      try {
         this.listener = new EventListener();
         this.myReference = (ISkynetEventListener) OseeJini.getRemoteReference(listener);

         addListenerForEventService();
      } catch (ExportException e) {
         logger.log(Level.SEVERE, e.toString(), e);
      }
   }

   public static void kick(final ISkynetEvent... events) {
      if (instance.skynetEventService != null) {

         Job job = new Job("Send Event") {

            @Override
            protected IStatus run(IProgressMonitor monitor) {
               try {
                  instance.skynetEventService.kick(events, instance.myReference);
               } catch (ExportException e) {
                  logger.log(Level.SEVERE, e.toString(), e);
               } catch (RemoteException e) {
                  instance.disconnectService(e);
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
         SkynetEventManager.getInstance().kick(new SkynetServiceEvent(this, true));

      } catch (ExportException e) {
         logger.log(Level.SEVERE, e.toString(), e);
      } catch (RemoteException e) {
         disconnectService(e);
      }
   }

   private void disconnectService(Exception e) {
      logger.log(Level.WARNING, "Skynet Event Service connection lost\n" + e.toString(), e);
      skynetEventService = null;
      SkynetEventManager.getInstance().kick(new SkynetServiceEvent(this, false));
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
               try {
                  String[] userIds = ((SkynetDisconnectClientsEvent) event).getUserIds();
                  User user = SkynetAuthentication.getUser();
                  if (user != null) {
                     String userId = user.getUserId();
                     for (String temp : userIds) {
                        if (temp.equals(userId)) {
                           isShutdownAllowed = true;
                           break;
                        }
                     }
                  }
               } catch (Exception ex) {
                  SkynetActivator.getLogger().log(Level.SEVERE, "Error processing shutdown", ex);
               }
               final boolean isShutdownRequest = isShutdownAllowed;
               Display.getDefault().asyncExec(new Runnable() {
                  public void run() {
                     if (isShutdownRequest) {
                        MessageDialog.openWarning(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
                              "Shutdown Requested", message);
                        // Shutdown the bench when this event is received
                        PlatformUI.getWorkbench().close();
                     }
                  }
               });
            } else {
               Display.getDefault().asyncExec(new Runnable() {
                  public void run() {
                     MessageDialog.openInformation(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
                           "Remote Message", message);
                  }
               });
            }
         }
      }

      @Override
      public void onEvent(final ISkynetEvent[] events) throws RemoteException {

         final List<Event> localEvents = new LinkedList<Event>();
         Job job = new Job("Receive Event") {

            @Override
            protected IStatus run(IProgressMonitor monitor) {
               Arrays.sort(events);

               for (ISkynetEvent event : events) {

                  if (event instanceof NetworkRenameBranchEvent) {
                     int branchId = ((NetworkRenameBranchEvent) event).getBranchId();
                     try {
                        Branch branch = BranchPersistenceManager.getInstance().getBranch(branchId);
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
                     BranchPersistenceManager.getInstance().removeBranchFromCache(branchId);
                     eventManager.kick(new RemoteDeletedBranchEvent(this, branchId));
                  } else if (event instanceof NetworkCommitBranchEvent) {
                     int branchId = ((NetworkCommitBranchEvent) event).getBranchId();
                     BranchPersistenceManager.getInstance().removeBranchFromCache(branchId);
                     eventManager.kick(new RemoteCommitBranchEvent(this, branchId));
                  } else if (event instanceof NetworkBroadcastEvent) {
                     handleBroadcastMessageEvent((NetworkBroadcastEvent) event);
                  } else if (event instanceof ISkynetArtifactEvent) {
                     updateArtifacts((ISkynetArtifactEvent) event, localEvents);
                  } else if (event instanceof ISkynetRelationLinkEvent) {
                     updateRelations((ISkynetRelationLinkEvent) event, localEvents);
                  }
               }
               eventManager.kick(new RemoteTransactionEvent(localEvents, this));
               return Status.OK_STATUS;
            }
         };
         job.setSystem(true);
         job.setUser(false);
         job.setRule(mutexRule);
         job.schedule();
      }
   }

   /**
    * Updates local cache
    * 
    * @param event
    */
   private static void updateArtifacts(ISkynetArtifactEvent event, Collection<Event> localEvents) {
      if (event == null) return;

      try {
         int artId = event.getArtId();
         int branchId = event.getBranchId();
         List<String> dirtyAttributeName = new LinkedList<String>();
         Artifact artifact = ArtifactCache.getActive(artId, branchId);

         if (artifact != null && artifact.isLive()) {
            ModType modType = null;

            if (event instanceof NetworkArtifactModifiedEvent) {
               for (SkynetAttributeChange skynetAttributeChange : ((NetworkArtifactModifiedEvent) event).getAttributeChanges()) {
                  for (Attribute<Object> attribute : artifact.getAttributes(skynetAttributeChange.getName())) {
                     if (attribute.getAttrId() == skynetAttributeChange.getAttributeId()) {
                        if (attribute.isDirty()) {
                           dirtyAttributeName.add(attribute.getNameValueDescription());
                        }
                        attribute.getAttributeDataProvider().loadData(skynetAttributeChange.getData());
                     }
                  }
               }
               modType = ModType.Changed;
            } else if (event instanceof NetworkArtifactDeletedEvent) {
               artifact.setDeleted();
               modType = ModType.Deleted;
            }
            localEvents.add(new TransactionArtifactModifiedEvent(artifact, modType, RemoteEventManager.instance));
            artifact.setNotDirty();
         }
      } catch (Exception e) {
         logger.log(Level.SEVERE, e.toString(), e);
      }
   }

   /**
    * @param event
    * @param newTransactionId
    * @throws ArtifactDoesNotExist
    * @throws SQLException
    */
   private static void updateRelations(ISkynetRelationLinkEvent event, Collection<Event> localEvents) {
      if (event == null) return;

      try {
         RelationType relationType = RelationTypeManager.getType(event.getRelTypeId());
         Branch branch = BranchPersistenceManager.getInstance().getBranch(event.getBranchId());
         org.eclipse.osee.framework.skynet.core.relation.RelationModifiedEvent.ModType modType = null;
         Artifact aArtifact = ArtifactCache.getActive(event.getArtAId(), branch.getBranchId());
         Artifact bArtifact = ArtifactCache.getActive(event.getArtBId(), branch.getBranchId());
         boolean aArtifactLoaded = aArtifact != null;
         boolean bArtifactLoaded = bArtifact != null;

         if (aArtifactLoaded || bArtifactLoaded) {
            //            if (aArtifactLoaded) {
            //               OseeLog.log(SkynetActivator.class, Level.INFO, String.format("%s - %s - %d", aArtifact.toString(),
            //                     aArtifact.getArtifactTypeName(), aArtifact.getArtId()));
            //               aArtifact.reloadArtifact();
            //            }
            //            if (bArtifactLoaded) {
            //               OseeLog.log(SkynetActivator.class, Level.INFO, String.format("%s - %s - %d", bArtifact.toString(),
            //                     bArtifact.getArtifactTypeName(), bArtifact.getArtId()));
            //               bArtifact.reloadArtifact();
            //            }
            //TODO Maybe just send a link change refresh event
            if (event instanceof NetworkRelationLinkModifiedEvent) {
               modType = org.eclipse.osee.framework.skynet.core.relation.RelationModifiedEvent.ModType.Changed;
               RelationLink relation =
                     RelationManager.getLoadedRelation(RelationTypeManager.getType(event.getRelTypeId()),
                           event.getArtAId(), event.getArtBId(), branch, branch);

               if (relation != null) {
                  boolean aOrderChanged =
                        ((NetworkRelationLinkModifiedEvent) event).getAOrder() != relation.getAOrder();
                  boolean bOrderChanged =
                        ((NetworkRelationLinkModifiedEvent) event).getBOrder() != relation.getBOrder();
                  if (aOrderChanged) {
                     relation.setAOrder(((NetworkRelationLinkModifiedEvent) event).getAOrder());
                     if (bArtifactLoaded) {
                        RelationManager.sortRelations(bArtifact, relation.getRelationType(),
                              new HashMap<Integer, RelationLink>(), new HashMap<Integer, RelationLink>());
                     }
                  }
                  if (bOrderChanged) {
                     relation.setBOrder(((NetworkRelationLinkModifiedEvent) event).getBOrder());
                     if (aArtifactLoaded) {
                        RelationManager.sortRelations(aArtifact, relation.getRelationType(),
                              new HashMap<Integer, RelationLink>(), new HashMap<Integer, RelationLink>());
                     }
                  }
                  relation.setRationale(((NetworkRelationLinkModifiedEvent) event).getRationale(), false);
                  relation.setNotDirty();
               }
            } else if (event instanceof NetworkRelationLinkDeletedEvent) {
               modType = org.eclipse.osee.framework.skynet.core.relation.RelationModifiedEvent.ModType.Deleted;
               RelationLink relation =
                     RelationManager.getLoadedRelation(RelationTypeManager.getType(event.getRelTypeId()),
                           event.getArtAId(), event.getArtBId(), branch, branch);
               if (relation != null) {
                  relation.deleteWithoutDirtyAndEvent();
               }
            } else if (event instanceof NetworkNewRelationLinkEvent) {
               modType = org.eclipse.osee.framework.skynet.core.relation.RelationModifiedEvent.ModType.Added;
               RelationLink relation =
                     RelationManager.getLoadedRelation(RelationTypeManager.getType(event.getRelTypeId()),
                           event.getArtAId(), event.getArtBId(), branch, branch);

               if (relation == null) {
                  relation =
                        new RelationLink(event.getArtAId(), event.getArtBId(), branch, branch, relationType,
                              event.getRelId(), event.getGammaId(),
                              ((NetworkNewRelationLinkEvent) event).getRationale(),
                              ((NetworkNewRelationLinkEvent) event).getAOrder(),
                              ((NetworkNewRelationLinkEvent) event).getBOrder());
                  RelationManager.manageRelation(relation, RelationSide.SIDE_A);
                  RelationManager.manageRelation(relation, RelationSide.SIDE_B);
                  if (bArtifactLoaded) {
                     RelationManager.sortRelations(bArtifact, relation.getRelationType(),
                           new HashMap<Integer, RelationLink>(), new HashMap<Integer, RelationLink>());
                  }
                  if (aArtifactLoaded) {
                     RelationManager.sortRelations(aArtifact, relation.getRelationType(),
                           new HashMap<Integer, RelationLink>(), new HashMap<Integer, RelationLink>());
                  }
               }
            }
         }
         RelationLink link =
               RelationManager.getLoadedRelation(relationType, event.getArtAId(), event.getArtBId(), branch, branch);

         if (link != null) {
            localEvents.add(new TransactionRelationModifiedEvent(link, branch, link.getRelationType().getTypeName(),
                  link.getASideName(), modType, RemoteEventManager.instance));
         } else {
            OseeLog.log(
                  SkynetActivator.class,
                  Level.FINE,
                  "Link was null for artifacts A:" + event.getArtAId() + " B:" + event.getArtBId() + " from the remote event service");
         }
      } catch (Exception ex) {
         logger.log(Level.SEVERE, ex.toString(), ex);
      }
   }

   public static boolean isConnected() {
      try {
         return instance.skynetEventService != null && instance.skynetEventService.isAlive();
      } catch (Throwable th) {
         logger.log(Level.SEVERE, th.toString(), th);
         return false;
      }
   }

}
