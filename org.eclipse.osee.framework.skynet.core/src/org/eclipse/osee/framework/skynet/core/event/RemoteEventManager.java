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
package org.eclipse.osee.framework.skynet.core.event;

import java.rmi.RemoteException;
import java.rmi.server.ExportException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import net.jini.core.entry.Entry;
import net.jini.core.lookup.ServiceItem;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.osee.framework.core.client.ClientSessionManager;
import org.eclipse.osee.framework.core.exception.OseeAuthenticationRequiredException;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.GUID;
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
import org.eclipse.osee.framework.messaging.event.skynet.event.NetworkAccessControlArtifactsEvent;
import org.eclipse.osee.framework.messaging.event.skynet.event.NetworkArtifactChangeTypeEvent;
import org.eclipse.osee.framework.messaging.event.skynet.event.NetworkArtifactDeletedEvent;
import org.eclipse.osee.framework.messaging.event.skynet.event.NetworkArtifactModifiedEvent;
import org.eclipse.osee.framework.messaging.event.skynet.event.NetworkArtifactPurgeEvent;
import org.eclipse.osee.framework.messaging.event.skynet.event.NetworkBroadcastEvent;
import org.eclipse.osee.framework.messaging.event.skynet.event.NetworkCommitBranchEvent;
import org.eclipse.osee.framework.messaging.event.skynet.event.NetworkDeletedBranchEvent;
import org.eclipse.osee.framework.messaging.event.skynet.event.NetworkNewBranchEvent;
import org.eclipse.osee.framework.messaging.event.skynet.event.NetworkNewRelationLinkEvent;
import org.eclipse.osee.framework.messaging.event.skynet.event.NetworkRelationLinkDeletedEvent;
import org.eclipse.osee.framework.messaging.event.skynet.event.NetworkRelationLinkOrderModifiedEvent;
import org.eclipse.osee.framework.messaging.event.skynet.event.NetworkRelationLinkRationalModifiedEvent;
import org.eclipse.osee.framework.messaging.event.skynet.event.NetworkRenameBranchEvent;
import org.eclipse.osee.framework.messaging.event.skynet.event.NetworkTransactionDeletedEvent;
import org.eclipse.osee.framework.messaging.event.skynet.event.SkynetAttributeChange;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactCache;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactModType;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.attribute.Attribute;
import org.eclipse.osee.framework.skynet.core.attribute.AttributeType;
import org.eclipse.osee.framework.skynet.core.attribute.AttributeTypeManager;
import org.eclipse.osee.framework.skynet.core.internal.Activator;
import org.eclipse.osee.framework.skynet.core.relation.RelationLink;
import org.eclipse.osee.framework.skynet.core.relation.RelationManager;
import org.eclipse.osee.framework.skynet.core.relation.RelationModType;
import org.eclipse.osee.framework.skynet.core.relation.RelationSide;
import org.eclipse.osee.framework.skynet.core.relation.RelationType;
import org.eclipse.osee.framework.skynet.core.relation.RelationTypeManager;
import org.eclipse.osee.framework.skynet.core.utility.LoadedArtifacts;
import org.eclipse.osee.framework.ui.plugin.event.UnloadedArtifact;
import org.eclipse.osee.framework.ui.plugin.event.UnloadedRelation;

/**
 * Manages remote events from the SkynetEventService.
 * 
 * @author Jeff C. Phillips
 */
public class RemoteEventManager {
   private static String ACCEPTABLE_SERVICE;

   private static final RemoteEventManager instance = new RemoteEventManager();

   private final InternalSkynetEventManager internalSkynetEventManager;
   private final ISkynetEventListener clientEventListener;
   private ISkynetEventListener clientEventListenerRemoteReference;

   private RemoteEventManager() {
      super();
      internalSkynetEventManager = new InternalSkynetEventManager();
      clientEventListener = new EventListener();
      checkJiniRegistration();
   }

   private void checkJiniRegistration() {
      if (clientEventListenerRemoteReference == null) {
         try {
            // We need to trigger authentication before attempting to get database information from client session manager.
            UserManager.getUser();
            ACCEPTABLE_SERVICE =
                  ClientSessionManager.getDataStoreName() + ":" + ClientSessionManager.getDataStoreLoginName();
            clientEventListenerRemoteReference =
                  (ISkynetEventListener) OseeJini.getRemoteReference(clientEventListener);
         } catch (Exception ex) {
            OseeLog.log(Activator.class, Level.SEVERE, ex);
            clientEventListenerRemoteReference = null;
         }

         if (clientEventListenerRemoteReference != null) {
            ServiceDataStore.getEclipseInstance(EclipseJiniClassloader.getInstance()).addListener(
                  internalSkynetEventManager, ISkynetEventService.class);
         }
      }
   }

   private static ISkynetEventListener getClientEventListenerRemoteReference() {
      instance.checkJiniRegistration();
      return instance.clientEventListenerRemoteReference;
   }

   private static InternalSkynetEventManager getEventServiceManager() {
      return instance.internalSkynetEventManager;
   }

   public static void deregisterFromRemoteEventManager() {
      ServiceDataStore.getEclipseInstance(EclipseJiniClassloader.getInstance()).removeListener(getEventServiceManager());
      getEventServiceManager().reset();
   }

   public static void kick(Collection<ISkynetEvent> events) {
      kick(events.toArray(new ISkynetEvent[events.size()]));
   }

   public static boolean isConnected() {
      return getEventServiceManager().isValid();
   }

   public static void kick(final ISkynetEvent... events) {
      if (isConnected()) {
         Job job = new Job("Send Event") {
            @Override
            protected IStatus run(IProgressMonitor monitor) {
               getEventServiceManager().kick(events, getClientEventListenerRemoteReference());
               return Status.OK_STATUS;
            }
         };

         job.schedule();
      }
      /*
       * This will enable a testing loopback that will take the kicked remote events and
       * loop them back as if they came from an external client.  It will allow for the testing
       * of the OEM -> REM -> OEM processing.  In addition, this onEvent is put in a non-display
       * thread which will test that all handling by applications is properly handled by doing
       * all processing and then kicking off display-thread when need to update ui.  SessionId needs
       * to be modified so this client doesn't think the events came from itself.
       */
      if (InternalEventManager.enableRemoteEventLoopback) {
         OseeLog.log(Activator.class, Level.INFO, "REM: Loopback enabled - Returning events as Remote event.");
         Thread thread = new Thread() {
            /* (non-Javadoc)
             * @see java.lang.Thread#run()
             */
            @Override
            public void run() {
               try {
                  String newSessionId = GUID.generateGuidStr();
                  for (ISkynetEvent event : events) {
                     event.getNetworkSender().sessionId = newSessionId;
                  }
                  instance.clientEventListener.onEvent(events);
               } catch (RemoteException ex) {
                  OseeLog.log(Activator.class, Level.SEVERE, ex);

               }
            }
         };
         thread.start();
      }
   }

   private class InternalSkynetEventManager implements IServiceLookupListener {
      private ISkynetEventService currentEventService;

      private InternalSkynetEventManager() {
         currentEventService = null;
      }

      public ISkynetEventService getReference() {
         return currentEventService;
      }

      public boolean isValid() {
         return isValidService(currentEventService);
      }

      public void reset() {
         setEventService(null);
      }

      public void kick(ISkynetEvent[] events, ISkynetEventListener... except) {
         try {
            getReference().kick(events, except);
         } catch (ExportException ex) {
            OseeLog.log(Activator.class, Level.SEVERE, ex);
         } catch (RemoteException ex) {
            disconnectService(ex);
         }
      }

      private boolean isValidService(ISkynetEventService service) {
         boolean result = false;
         try {
            if (service != null) {
               result = service.isAlive();
            }
         } catch (Exception ex) {
            // Do Nothing
            result = false;
         }
         return result;
      }

      private synchronized void setEventService(ISkynetEventService service) {
         if (isValidService(currentEventService)) {
            try {
               currentEventService.deregister(getClientEventListenerRemoteReference());
            } catch (RemoteException ex) {
               OseeLog.log(Activator.class, Level.SEVERE, ex);
            }
         }
         currentEventService = service;
      }

      private void disconnectService(Exception e) {
         OseeLog.log(Activator.class, Level.WARNING, "Skynet Event Service connection lost\n" + e.toString(), e);
         setEventService(null);
         try {
            OseeEventManager.kickRemoteEventManagerEvent(instance, RemoteEventServiceEventType.DisConnected);
         } catch (OseeCoreException ex) {
            OseeLog.log(Activator.class, Level.SEVERE, ex);
         }
      }

      private void connectToService(ISkynetEventService service) {
         try {
            ISkynetEventListener clientListener = getClientEventListenerRemoteReference();
            if (clientListener != null) {
               service.register(clientListener);
               setEventService(service);
               OseeLog.log(Activator.class, Level.INFO,
                     "Skynet Event Service connection established " + ACCEPTABLE_SERVICE);
               OseeEventManager.kickRemoteEventManagerEvent(this, RemoteEventServiceEventType.Connected);
            } else {
               OseeLog.log(Activator.class, Level.SEVERE, "Client listener reference was null");
            }
         } catch (Exception ex) {
            OseeLog.log(Activator.class, Level.SEVERE, ex);
         }
      }

      /*
       * (non-Javadoc)
       * 
       * @see org.eclipse.osee.framework.jini.discovery.IServiceLookupListener#serviceAdded(net.jini.core.lookup.ServiceItem)
       */
      public void serviceAdded(ServiceItem serviceItem) {
         if (serviceItem.service instanceof ISkynetEventService) {
            ISkynetEventService service = (ISkynetEventService) serviceItem.service;
            if (isValidService(service)) {
               // Check if the service is for the database we are using
               for (Entry entry : serviceItem.attributeSets) {
                  if (entry instanceof SimpleFormattedEntry) {
                     SimpleFormattedEntry simpleEntry = (SimpleFormattedEntry) entry;
                     if ("db".equals(simpleEntry.name) && ACCEPTABLE_SERVICE.equals(simpleEntry.value)) {
                        connectToService(service);
                        break;
                     }
                  }
               }
            }
         }
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
   }

   private static class EventListener extends ASkynetEventListener {
      private static final long serialVersionUID = -3017349745450262540L;
      private static final ISchedulingRule mutexRule = new ISchedulingRule() {

         public boolean contains(ISchedulingRule rule) {
            return rule == this;
         }

         public boolean isConflicting(ISchedulingRule rule) {
            return rule == this;
         }
      };

      @Override
      public void onEvent(final ISkynetEvent[] events) throws RemoteException {

         final List<ArtifactTransactionModifiedEvent> xModifiedEvents =
               new LinkedList<ArtifactTransactionModifiedEvent>();
         Job job = new Job("Receive Event") {

            @Override
            protected IStatus run(IProgressMonitor monitor) {
               Arrays.sort(events);

               Sender lastArtifactRelationModChangeSender = null;

               for (ISkynetEvent event : events) {

                  Sender sender = new Sender((event).getNetworkSender());
                  // If the sender's sessionId is the same as this client, then this event was
                  // created in this client and returned by remote event manager; ignore and continue
                  try {
                     if (sender.isLocal()) continue;
                  } catch (OseeAuthenticationRequiredException ex1) {
                     OseeLog.log(Activator.class, Level.SEVERE, ex1);
                  }

                  if (event instanceof NetworkAccessControlArtifactsEvent) {
                     try {

                        AccessControlEventType accessControlModType =
                              AccessControlEventType.valueOf(((NetworkAccessControlArtifactsEvent) event).getAccessControlModTypeName());
                        LoadedArtifacts loadedArtifacts =
                              new LoadedArtifacts(((NetworkAccessControlArtifactsEvent) event).getBranchId(),
                                    ((NetworkAccessControlArtifactsEvent) event).getArtifactIds(),
                                    ((NetworkAccessControlArtifactsEvent) event).getArtifactTypeIds());
                        InternalEventManager.kickAccessControlArtifactsEvent(sender, accessControlModType,
                              loadedArtifacts);
                     } catch (Exception ex) {
                        OseeLog.log(Activator.class, Level.SEVERE, ex);
                     }
                  } else if (event instanceof NetworkRenameBranchEvent) {
                     int branchId = ((NetworkRenameBranchEvent) event).getBranchId();
                     try {
                        Branch branch = BranchManager.getBranch(branchId);
                        branch.setBranchName(((NetworkRenameBranchEvent) event).getBranchName());
                        try {
                           InternalEventManager.kickBranchEvent(sender, BranchEventType.Renamed, branchId);
                        } catch (Exception ex) {
                           OseeLog.log(Activator.class, Level.SEVERE, ex);
                        }
                     } catch (Exception ex) {
                        OseeLog.log(Activator.class, Level.SEVERE, ex);
                     }
                  } else if (event instanceof NetworkNewBranchEvent) {
                     int branchId = ((NetworkNewBranchEvent) event).getBranchId();
                     try {
                        InternalEventManager.kickBranchEvent(sender, BranchEventType.Added, branchId);
                     } catch (Exception ex) {
                        OseeLog.log(Activator.class, Level.SEVERE, ex);
                     }
                  } else if (event instanceof NetworkDeletedBranchEvent) {
                     int branchId = ((NetworkDeletedBranchEvent) event).getBranchId();
                     BranchManager.handleBranchDeletion(branchId);
                     try {
                        InternalEventManager.kickBranchEvent(sender, BranchEventType.Deleted, branchId);
                     } catch (Exception ex) {
                        OseeLog.log(Activator.class, Level.SEVERE, ex);
                     }
                  } else if (event instanceof NetworkCommitBranchEvent) {
                     int branchId = ((NetworkCommitBranchEvent) event).getBranchId();
                     try {
                        InternalEventManager.kickBranchEvent(sender, BranchEventType.Committed, branchId);
                     } catch (Exception ex) {
                        OseeLog.log(Activator.class, Level.SEVERE, ex);
                     }
                  } else if (event instanceof NetworkBroadcastEvent) {
                     try {
                        final BroadcastEventType broadcastEventType =
                              BroadcastEventType.valueOf(((NetworkBroadcastEvent) event).getBroadcastEventTypeName());
                        if (broadcastEventType == null) {
                           OseeLog.log(
                                 Activator.class,
                                 Level.SEVERE,
                                 "Unknown broadcast event type \"" + ((NetworkBroadcastEvent) event).getBroadcastEventTypeName() + "\"",
                                 new IllegalArgumentException());
                        } else {
                           InternalEventManager.kickBroadcastEvent(sender, broadcastEventType,
                                 ((NetworkBroadcastEvent) event).getUserIds(),
                                 ((NetworkBroadcastEvent) event).getMessage());
                        }
                     } catch (Exception ex) {
                        OseeLog.log(Activator.class, Level.SEVERE, ex);
                     }
                  } else if (event instanceof ISkynetArtifactEvent) {
                     updateArtifacts(sender, (ISkynetArtifactEvent) event, xModifiedEvents);
                     lastArtifactRelationModChangeSender = sender;
                  } else if (event instanceof ISkynetRelationLinkEvent) {
                     updateRelations(sender, (ISkynetRelationLinkEvent) event, xModifiedEvents);
                     lastArtifactRelationModChangeSender = sender;
                  } else if (event instanceof NetworkArtifactChangeTypeEvent) {
                     try {
                        LoadedArtifacts loadedArtifacts =
                              new LoadedArtifacts(((NetworkArtifactChangeTypeEvent) event).getBranchId(),
                                    ((NetworkArtifactChangeTypeEvent) event).getArtifactIds(),
                                    ((NetworkArtifactChangeTypeEvent) event).getArtifactTypeIds());
                        InternalEventManager.kickArtifactsChangeTypeEvent(sender,
                              ((NetworkArtifactChangeTypeEvent) event).getToArtifactTypeId(), loadedArtifacts);
                     } catch (Exception ex) {
                        OseeLog.log(Activator.class, Level.SEVERE, ex);
                     }
                  } else if (event instanceof NetworkArtifactPurgeEvent) {
                     try {
                        LoadedArtifacts loadedArtifacts =
                              new LoadedArtifacts(((NetworkArtifactPurgeEvent) event).getBranchId(),
                                    ((NetworkArtifactPurgeEvent) event).getArtifactIds(),
                                    ((NetworkArtifactPurgeEvent) event).getArtifactTypeIds());
                        InternalEventManager.kickArtifactsPurgedEvent(sender, loadedArtifacts);
                     } catch (Exception ex) {
                        OseeLog.log(Activator.class, Level.SEVERE, ex);
                     }
                  } else if (event instanceof NetworkTransactionDeletedEvent) {
                     try {
                        InternalEventManager.kickTransactionsDeletedEvent(sender,
                              ((NetworkTransactionDeletedEvent) event).getTransactionIds());
                     } catch (Exception ex) {
                        OseeLog.log(Activator.class, Level.SEVERE, ex);
                     }
                  }
               }

               if (xModifiedEvents.size() > 0) {
                  /*
                   * Since transaction events are a collection of ArtifactModfied and RelationModified
                   * events, create a new Sender based on the last sender for these events.
                   */
                  Sender transactionSender =
                        new Sender("RemoteEventManager", lastArtifactRelationModChangeSender.getOseeSession());
                  InternalEventManager.kickTransactionEvent(transactionSender, xModifiedEvents);
               }

               return Status.OK_STATUS;
            }
         };
         job.setSystem(true);
         job.setUser(false);
         job.setRule(mutexRule);
         job.schedule();
      }

      /**
       * Updates local cache
       * 
       * @param event
       */
      private static void updateArtifacts(Sender sender, ISkynetArtifactEvent event, Collection<ArtifactTransactionModifiedEvent> xModifiedEvents) {
         if (event == null) return;

         try {
            int artId = event.getArtId();
            int artTypeId = event.getArtTypeId();
            List<String> dirtyAttributeName = new LinkedList<String>();

            if (event instanceof NetworkArtifactModifiedEvent) {
               int branchId = ((NetworkArtifactModifiedEvent) event).getBranchId();
               Artifact artifact = ArtifactCache.getActive(artId, branchId);
               if (artifact == null) {
                  UnloadedArtifact unloadedArtifact = new UnloadedArtifact(branchId, artId, artTypeId);
                  xModifiedEvents.add(new ArtifactModifiedEvent(sender, ArtifactModType.Changed, unloadedArtifact));
               } else if (!artifact.isHistorical()) {
                  for (SkynetAttributeChange skynetAttributeChange : ((NetworkArtifactModifiedEvent) event).getAttributeChanges()) {
                     if (!InternalEventManager.enableRemoteEventLoopback) {
                        boolean attributeNeedsCreation = true;
                        AttributeType attributeType = AttributeTypeManager.getType(skynetAttributeChange.getTypeId());
                        for (Attribute<Object> attribute : artifact.getAttributes(attributeType.getName())) {
                           if (attribute.getAttrId() == skynetAttributeChange.getAttributeId()) {
                              if (attribute.isDirty()) {
                                 dirtyAttributeName.add(attribute.getNameValueDescription());
                                 OseeLog.log(Activator.class, Level.INFO, String.format(
                                       "%s's attribute %d [/n%s/n] has been overwritten.", artifact.getSafeName(),
                                       attribute.getAttrId(), attribute.toString()));
                              }
                              if (skynetAttributeChange.isDeleted()) {
                                 attribute.internalSetDeleted();
                              } else {
                                 attribute.getAttributeDataProvider().loadData(skynetAttributeChange.getData());
                              }
                              attribute.internalSetGammaId(skynetAttributeChange.getGammaId());
                              attributeNeedsCreation = false;
                              attribute.setNotDirty();
                              break;
                           }
                        }
                        if (attributeNeedsCreation) {
                           Attribute.initializeAttribute(artifact, AttributeTypeManager.getType(
                                 skynetAttributeChange.getTypeId()).getAttrTypeId(),
                                 skynetAttributeChange.getAttributeId(), skynetAttributeChange.getGammaId(),
                                 skynetAttributeChange.getData());
                        }
                     }
                  }

                  xModifiedEvents.add(new ArtifactModifiedEvent(sender, ArtifactModType.Changed, artifact,
                        ((NetworkArtifactModifiedEvent) event).getTransactionId(),
                        ((NetworkArtifactModifiedEvent) event).getAttributeChanges()));

               }
            } else if (event instanceof NetworkArtifactDeletedEvent) {
               int branchId = ((NetworkArtifactDeletedEvent) event).getBranchId();
               Artifact artifact = ArtifactCache.getActive(artId, branchId);
               if (artifact == null) {
                  UnloadedArtifact unloadedArtifact = new UnloadedArtifact(branchId, artId, artTypeId);
                  xModifiedEvents.add(new ArtifactModifiedEvent(sender, ArtifactModType.Deleted, unloadedArtifact));
               } else if (!artifact.isHistorical()) {
                  if (!InternalEventManager.enableRemoteEventLoopback) {
                     artifact.setDeleted();
                     artifact.setNotDirty();
                  }

                  xModifiedEvents.add(new ArtifactModifiedEvent(sender, ArtifactModType.Deleted, artifact,
                        ((NetworkArtifactDeletedEvent) event).getTransactionId(),
                        new ArrayList<SkynetAttributeChange>()));
               }
            }
         } catch (Exception ex) {
            OseeLog.log(Activator.class, Level.SEVERE, ex);
         }
      }

      /**
       * @param event
       * @param newTransactionId
       */
      private static void updateRelations(Sender sender, ISkynetRelationLinkEvent event, Collection<ArtifactTransactionModifiedEvent> xModifiedEvents) {
         if (event == null) return;

         try {
            RelationType relationType = RelationTypeManager.getType(event.getRelTypeId());
            Branch branch = BranchManager.getBranch(event.getBranchId());
            Artifact aArtifact = ArtifactCache.getActive(event.getArtAId(), branch.getBranchId());
            Artifact bArtifact = ArtifactCache.getActive(event.getArtBId(), branch.getBranchId());
            boolean aArtifactLoaded = aArtifact != null;
            boolean bArtifactLoaded = bArtifact != null;

            if (!aArtifactLoaded && !bArtifactLoaded) {
               if (event instanceof NetworkRelationLinkDeletedEvent) {
                  UnloadedRelation unloadedRelation =
                        new UnloadedRelation(branch.getBranchId(), event.getArtAId(), event.getArtATypeId(),
                              event.getArtBId(), event.getArtBTypeId(), event.getRelTypeId());
                  xModifiedEvents.add(new RelationModifiedEvent(sender, RelationModType.Deleted, unloadedRelation));
               } else if (event instanceof NetworkRelationLinkOrderModifiedEvent) {
                  UnloadedRelation unloadedRelation =
                        new UnloadedRelation(branch.getBranchId(), event.getArtAId(), event.getArtATypeId(),
                              event.getArtBId(), event.getArtBTypeId(), event.getRelTypeId());
                  xModifiedEvents.add(new RelationModifiedEvent(sender, RelationModType.ReOrdered, unloadedRelation));
               } else if (event instanceof NetworkRelationLinkRationalModifiedEvent) {
                  UnloadedRelation unloadedRelation =
                        new UnloadedRelation(branch.getBranchId(), event.getArtAId(), event.getArtATypeId(),
                              event.getArtBId(), event.getArtBTypeId(), event.getRelTypeId());
                  xModifiedEvents.add(new RelationModifiedEvent(sender, RelationModType.RationaleMod, unloadedRelation));
               } else if (event instanceof NetworkNewRelationLinkEvent) {
                  UnloadedRelation unloadedRelation =
                        new UnloadedRelation(branch.getBranchId(), event.getArtAId(), event.getArtATypeId(),
                              event.getArtBId(), event.getArtBTypeId(), event.getRelTypeId());
                  xModifiedEvents.add(new RelationModifiedEvent(sender, RelationModType.Added, unloadedRelation));
               }
            }
            if (aArtifactLoaded || bArtifactLoaded) {
               //            if (aArtifactLoaded) {
               //               OseeLog.log(Activator.class, Level.INFO, String.format("%s - %s - %d", aArtifact.toString(),
               //                     aArtifact.getArtifactTypeName(), aArtifact.getArtId()));
               //               aArtifact.reloadArtifact();
               //            }
               //            if (bArtifactLoaded) {
               //               OseeLog.log(Activator.class, Level.INFO, String.format("%s - %s - %d", bArtifact.toString(),
               //                     bArtifact.getArtifactTypeName(), bArtifact.getArtId()));
               //               bArtifact.reloadArtifact();
               //            }
               //TODO Maybe just send a link change refresh event
               if (event instanceof NetworkRelationLinkOrderModifiedEvent) {
                  RelationLink relation =
                        RelationManager.getLoadedRelation(RelationTypeManager.getType(event.getRelTypeId()),
                              event.getArtAId(), event.getArtBId(), branch, branch);

                  if (relation != null) {
                     RelationModType relationModType = null;
                     boolean aOrderChanged =
                           ((NetworkRelationLinkOrderModifiedEvent) event).getAOrder() != relation.getAOrder();
                     boolean bOrderChanged =
                           ((NetworkRelationLinkOrderModifiedEvent) event).getBOrder() != relation.getBOrder();
                     if (aOrderChanged) {
                        relation.setAOrder(((NetworkRelationLinkOrderModifiedEvent) event).getAOrder());
                        if (bArtifactLoaded) {
                           RelationManager.sortRelations(bArtifact, relation.getRelationType(),
                                 new HashMap<Integer, RelationLink>(), new HashMap<Integer, RelationLink>());
                        }
                        relationModType = RelationModType.ReOrdered;
                     }
                     if (bOrderChanged) {
                        relation.setBOrder(((NetworkRelationLinkOrderModifiedEvent) event).getBOrder());
                        if (aArtifactLoaded) {
                           RelationManager.sortRelations(aArtifact, relation.getRelationType(),
                                 new HashMap<Integer, RelationLink>(), new HashMap<Integer, RelationLink>());
                        }
                        relationModType = RelationModType.ReOrdered;
                     }
                     if (relation.getRationale().equals(((NetworkRelationLinkOrderModifiedEvent) event).getRationale())) {
                        relation.setRationale(((NetworkRelationLinkOrderModifiedEvent) event).getRationale(), false);
                        relationModType = RelationModType.RationaleMod;
                     }
                     if (relationModType == null) {
                        OseeLog.log(Activator.class, Level.SEVERE,
                              "Link Modified Type Can Not Be Determined; Event Ignored.  " + event);
                     } else {
                        relation.setNotDirty();

                        xModifiedEvents.add(new RelationModifiedEvent(sender, relationModType, relation,
                              relation.getBranch(), relation.getRelationType().getTypeName()));
                     }
                  }
               } else if (event instanceof NetworkRelationLinkDeletedEvent) {
                  RelationLink relation =
                        RelationManager.getLoadedRelation(RelationTypeManager.getType(event.getRelTypeId()),
                              event.getArtAId(), event.getArtBId(), branch, branch);
                  if (relation != null) {
                     relation.deleteWithoutDirtyAndEvent();

                     xModifiedEvents.add(new RelationModifiedEvent(sender, RelationModType.Deleted, relation,
                           relation.getBranch(), relation.getRelationType().getTypeName()));
                  }
               } else if (event instanceof NetworkNewRelationLinkEvent) {
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

                     xModifiedEvents.add(new RelationModifiedEvent(sender, RelationModType.Added, relation,
                           relation.getBranch(), relation.getRelationType().getTypeName()));
                  }
               }
            }
         } catch (OseeCoreException ex) {
            OseeLog.log(Activator.class, Level.SEVERE, ex);
         }
      }

   }
}
