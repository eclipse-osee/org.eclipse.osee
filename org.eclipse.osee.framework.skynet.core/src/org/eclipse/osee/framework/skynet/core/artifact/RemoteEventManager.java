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
import java.util.ArrayList;
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
import org.eclipse.osee.framework.messaging.event.skynet.event.NetworkAccessControlArtifactsEvent;
import org.eclipse.osee.framework.messaging.event.skynet.event.NetworkArtifactChangeTypeEvent;
import org.eclipse.osee.framework.messaging.event.skynet.event.NetworkArtifactDeletedEvent;
import org.eclipse.osee.framework.messaging.event.skynet.event.NetworkArtifactModifiedEvent;
import org.eclipse.osee.framework.messaging.event.skynet.event.NetworkArtifactPurgeEvent;
import org.eclipse.osee.framework.messaging.event.skynet.event.NetworkBroadcastEvent;
import org.eclipse.osee.framework.messaging.event.skynet.event.NetworkNewRelationLinkEvent;
import org.eclipse.osee.framework.messaging.event.skynet.event.NetworkRelationLinkDeletedEvent;
import org.eclipse.osee.framework.messaging.event.skynet.event.NetworkRelationLinkModifiedEvent;
import org.eclipse.osee.framework.messaging.event.skynet.event.NetworkTransactionDeletedEvent;
import org.eclipse.osee.framework.messaging.event.skynet.event.SkynetAttributeChange;
import org.eclipse.osee.framework.plugin.core.config.ConfigUtil;
import org.eclipse.osee.framework.skynet.core.SkynetActivator;
import org.eclipse.osee.framework.skynet.core.attribute.Attribute;
import org.eclipse.osee.framework.skynet.core.attribute.AttributeToTransactionOperation;
import org.eclipse.osee.framework.skynet.core.attribute.AttributeTypeManager;
import org.eclipse.osee.framework.skynet.core.event.AccessControlModType;
import org.eclipse.osee.framework.skynet.core.event.BroadcastEventType;
import org.eclipse.osee.framework.skynet.core.event.RemoteEventModType;
import org.eclipse.osee.framework.skynet.core.event.ArtifactModifiedEvent;
import org.eclipse.osee.framework.skynet.core.event.OseeEventManager;
import org.eclipse.osee.framework.skynet.core.event.ArtifactTransactionModifiedEvent;
import org.eclipse.osee.framework.skynet.core.event.RelationModifiedEvent;
import org.eclipse.osee.framework.skynet.core.exception.ArtifactDoesNotExist;
import org.eclipse.osee.framework.skynet.core.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.relation.RelationLink;
import org.eclipse.osee.framework.skynet.core.relation.RelationManager;
import org.eclipse.osee.framework.skynet.core.relation.RelationModType;
import org.eclipse.osee.framework.skynet.core.relation.RelationSide;
import org.eclipse.osee.framework.skynet.core.relation.RelationType;
import org.eclipse.osee.framework.skynet.core.relation.RelationTypeManager;
import org.eclipse.osee.framework.skynet.core.utility.LoadedArtifacts;
import org.eclipse.osee.framework.ui.plugin.event.Sender;
import org.eclipse.osee.framework.ui.plugin.event.UnloadedArtifact;
import org.eclipse.osee.framework.ui.plugin.event.UnloadedRelation;
import org.eclipse.osee.framework.ui.plugin.event.Sender.Source;

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

   public static void kick(Collection<ISkynetEvent> events) {
      kick(events.toArray(new ISkynetEvent[events.size()]));
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
         OseeEventManager.kickRemoteEventManagerEvent(OseeEventManager.getSender(Source.Local, this),
               RemoteEventModType.Connected);

      } catch (OseeCoreException e) {
         logger.log(Level.SEVERE, e.toString(), e);
      } catch (ExportException e) {
         logger.log(Level.SEVERE, e.toString(), e);
      } catch (RemoteException e) {
         disconnectService(e);
      }
   }

   private void disconnectService(Exception e) {
      logger.log(Level.WARNING, "Skynet Event Service connection lost\n" + e.toString(), e);
      skynetEventService = null;
      try {
         OseeEventManager.kickRemoteEventManagerEvent(OseeEventManager.getSender(Source.Local, this),
               RemoteEventModType.DisConnected);

      } catch (OseeCoreException ex) {
         logger.log(Level.SEVERE, ex.toString(), ex);
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

   /**
    * @author Jeff C. Phillips
    */
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

         final List<ArtifactTransactionModifiedEvent> xModifiedEvents = new LinkedList<ArtifactTransactionModifiedEvent>();
         Job job = new Job("Receive Event") {

            @Override
            protected IStatus run(IProgressMonitor monitor) {
               Arrays.sort(events);

               for (ISkynetEvent event : events) {

                  if (event instanceof NetworkAccessControlArtifactsEvent) {
                     try {
                        Sender sender =
                              new Sender(Source.Remote, null, ((NetworkAccessControlArtifactsEvent) event).getAuthor());
                        AccessControlModType accessControlModType =
                              AccessControlModType.valueOf(((NetworkAccessControlArtifactsEvent) event).getAccessControlModTypeName());
                        LoadedArtifacts loadedArtifacts =
                              new LoadedArtifacts(((NetworkAccessControlArtifactsEvent) event).getBranchId(),
                                    ((NetworkAccessControlArtifactsEvent) event).getArtifactIds(),
                                    ((NetworkAccessControlArtifactsEvent) event).getArtifactTypeIds());
                        OseeEventManager.kickAccessControlArtifactsEvent(sender, accessControlModType, loadedArtifacts);
                     } catch (Exception ex) {
                        logger.log(Level.SEVERE, ex.toString(), ex);
                     }
                  } else if (event instanceof NetworkRenameBranchEvent) {
                     int branchId = ((NetworkRenameBranchEvent) event).getBranchId();
                     try {
                        Branch branch = BranchPersistenceManager.getBranch(branchId);
                        branch.setBranchName(((NetworkRenameBranchEvent) event).getBranchName());
                        branch.setBranchShortName(((NetworkRenameBranchEvent) event).getShortName(), false);
                        try {
                           Sender sender =
                                 new Sender(Source.Remote, null, ((NetworkRenameBranchEvent) event).getAuthor());
                           OseeEventManager.kickBranchEvent(sender, BranchModType.Renamed, branchId);
                        } catch (Exception ex) {
                           logger.log(Level.SEVERE, ex.toString(), ex);
                        }
                     } catch (Exception ex) {
                        logger.log(Level.SEVERE, ex.toString(), ex);
                     }
                  } else if (event instanceof NetworkNewBranchEvent) {
                     int branchId = ((NetworkNewBranchEvent) event).getBranchId();
                     try {
                        Sender sender = new Sender(Source.Remote, null, ((NetworkNewBranchEvent) event).getAuthor());
                        OseeEventManager.kickBranchEvent(sender, BranchModType.Added, branchId);
                     } catch (Exception ex) {
                        logger.log(Level.SEVERE, ex.toString(), ex);
                     }
                  } else if (event instanceof NetworkDeletedBranchEvent) {
                     int branchId = ((NetworkDeletedBranchEvent) event).getBranchId();
                     BranchPersistenceManager.removeBranchFromCache(branchId);
                     try {
                        Sender sender =
                              new Sender(Source.Remote, null, ((NetworkDeletedBranchEvent) event).getAuthor());
                        OseeEventManager.kickBranchEvent(sender, BranchModType.Deleted, branchId);
                     } catch (Exception ex) {
                        logger.log(Level.SEVERE, ex.toString(), ex);
                     }
                  } else if (event instanceof NetworkCommitBranchEvent) {
                     int branchId = ((NetworkCommitBranchEvent) event).getBranchId();
                     BranchPersistenceManager.removeBranchFromCache(branchId);
                     try {
                        Sender sender = new Sender(Source.Remote, null, ((NetworkCommitBranchEvent) event).getAuthor());
                        OseeEventManager.kickBranchEvent(sender, BranchModType.Committed, branchId);
                     } catch (Exception ex) {
                        logger.log(Level.SEVERE, ex.toString(), ex);
                     }
                  } else if (event instanceof NetworkBroadcastEvent) {
                     try {
                        Sender sender = new Sender(Source.Remote, null, ((NetworkBroadcastEvent) event).getAuthor());
                        final BroadcastEventType broadcastEventType =
                              BroadcastEventType.valueOf(((NetworkBroadcastEvent) event).getBroadcastEventTypeName());
                        if (broadcastEventType == null) {
                           SkynetActivator.getLogger().log(
                                 Level.SEVERE,
                                 "Unknown broadcast event type \"" + ((NetworkBroadcastEvent) event).getBroadcastEventTypeName() + "\"",
                                 new IllegalArgumentException());
                        } else {
                           OseeEventManager.kickBroadcastEvent(sender, broadcastEventType,
                                 ((NetworkBroadcastEvent) event).getUserIds(),
                                 ((NetworkBroadcastEvent) event).getMessage());
                        }
                     } catch (Exception ex) {
                        logger.log(Level.SEVERE, ex.toString(), ex);
                     }
                  } else if (event instanceof ISkynetArtifactEvent) {
                     updateArtifacts((ISkynetArtifactEvent) event, xModifiedEvents);
                  } else if (event instanceof ISkynetRelationLinkEvent) {
                     updateRelations((ISkynetRelationLinkEvent) event, xModifiedEvents);
                  } else if (event instanceof NetworkArtifactChangeTypeEvent) {
                     try {
                        Sender sender =
                              new Sender(Source.Remote, null, ((NetworkArtifactChangeTypeEvent) event).getAuthor());
                        LoadedArtifacts loadedArtifacts =
                              new LoadedArtifacts(((NetworkArtifactChangeTypeEvent) event).getBranchId(),
                                    ((NetworkArtifactChangeTypeEvent) event).getArtifactIds(),
                                    ((NetworkArtifactChangeTypeEvent) event).getArtifactTypeIds());
                        OseeEventManager.kickArtifactsChangeTypeEvent(sender,
                              ((NetworkArtifactChangeTypeEvent) event).getToArtifactTypeId(), loadedArtifacts);
                     } catch (Exception ex) {
                        logger.log(Level.SEVERE, ex.toString(), ex);
                     }
                  } else if (event instanceof NetworkArtifactPurgeEvent) {
                     try {
                        Sender sender =
                              new Sender(Source.Remote, null, ((NetworkArtifactPurgeEvent) event).getAuthor());
                        LoadedArtifacts loadedArtifacts =
                              new LoadedArtifacts(((NetworkArtifactPurgeEvent) event).getBranchId(),
                                    ((NetworkArtifactPurgeEvent) event).getArtifactIds(),
                                    ((NetworkArtifactChangeTypeEvent) event).getArtifactTypeIds());
                        OseeEventManager.kickArtifactsPurgedEvent(sender, loadedArtifacts);
                     } catch (Exception ex) {
                        logger.log(Level.SEVERE, ex.toString(), ex);
                     }
                  } else if (event instanceof NetworkTransactionDeletedEvent) {
                     try {
                        Sender sender =
                              new Sender(Source.Remote, null, ((NetworkTransactionDeletedEvent) event).getAuthor());
                        OseeEventManager.kickTransactionsDeletedEvent(sender,
                              ((NetworkTransactionDeletedEvent) event).getTransactionIds());
                     } catch (Exception ex) {
                        logger.log(Level.SEVERE, ex.toString(), ex);
                     }
                  }
               }

               OseeEventManager.kickTransactionEvent(Source.Remote, xModifiedEvents);
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
   private static void updateArtifacts(ISkynetArtifactEvent event, Collection<ArtifactTransactionModifiedEvent> xModifiedEvents) {
      if (event == null) return;

      try {
         int artId = event.getArtId();
         int artTypeId = event.getArtTypeId();
         List<String> dirtyAttributeName = new LinkedList<String>();

         if (event instanceof NetworkArtifactModifiedEvent) {
            int branchId = ((NetworkArtifactModifiedEvent) event).getBranchId();
            Artifact artifact = ArtifactCache.getActive(artId, branchId);
            if (artifact == null) {
               Sender sender = new Sender(Source.Remote, instance, ((NetworkArtifactModifiedEvent) event).getAuthor());
               UnloadedArtifact unloadedArtifact = new UnloadedArtifact(branchId, artId, artTypeId);
               xModifiedEvents.add(new ArtifactModifiedEvent(sender, ArtifactModType.Changed, unloadedArtifact));
            } else if (!artifact.isHistorical()) {
               for (SkynetAttributeChange skynetAttributeChange : ((NetworkArtifactModifiedEvent) event).getAttributeChanges()) {
                  boolean attributeNeedsCreation = true;
                  for (Attribute<Object> attribute : artifact.getAttributes(skynetAttributeChange.getName())) {
                     if (attribute.getAttrId() == skynetAttributeChange.getAttributeId()) {
                        if (attribute.isDirty()) {
                           dirtyAttributeName.add(attribute.getNameValueDescription());
                           OseeLog.log(SkynetActivator.class, Level.INFO, String.format(
                                 "%s's attribute %d [/n%s/n] has been overwritten.", artifact.getSafeName(),
                                 attribute.getAttrId(), attribute.toString()));
                        }
                        attribute.getAttributeDataProvider().loadData(skynetAttributeChange.getData());
                        attribute.setGammaId(skynetAttributeChange.getGammaId());
                        attributeNeedsCreation = false;
                        attribute.setNotDirty();
                        break;
                     }
                  }
                  if (attributeNeedsCreation) {
                     Attribute<?> attribute =
                           AttributeToTransactionOperation.initializeAttribute(artifact, AttributeTypeManager.getType(
                                 skynetAttributeChange.getName()).getAttrTypeId(),
                                 skynetAttributeChange.getAttributeId(), skynetAttributeChange.getGammaId(),
                                 skynetAttributeChange.getData());
                     if (attribute != null) {
                        attribute.setNotDirty();
                     }
                  }
               }

               Sender sender = new Sender(Source.Remote, instance, ((NetworkArtifactModifiedEvent) event).getAuthor());
               xModifiedEvents.add(new ArtifactModifiedEvent(sender, ArtifactModType.Changed, artifact,
                     ((NetworkArtifactModifiedEvent) event).getTransactionId(),
                     ((NetworkArtifactModifiedEvent) event).getAttributeChanges()));

            }
         } else if (event instanceof NetworkArtifactDeletedEvent) {
            int branchId = ((NetworkArtifactDeletedEvent) event).getBranchId();
            Artifact artifact = ArtifactCache.getActive(artId, branchId);
            if (artifact == null) {
               Sender sender = new Sender(Source.Remote, instance, ((NetworkArtifactDeletedEvent) event).getAuthor());
               UnloadedArtifact unloadedArtifact = new UnloadedArtifact(branchId, artId, artTypeId);
               xModifiedEvents.add(new ArtifactModifiedEvent(sender, ArtifactModType.Deleted, unloadedArtifact));
            } else if (!artifact.isHistorical()) {
               artifact.setDeleted();
               artifact.setNotDirty();

               Sender sender = new Sender(Source.Remote, instance, ((NetworkArtifactDeletedEvent) event).getAuthor());
               xModifiedEvents.add(new ArtifactModifiedEvent(sender, ArtifactModType.Deleted, artifact,
                     ((NetworkArtifactDeletedEvent) event).getTransactionId(), new ArrayList<SkynetAttributeChange>()));
            }
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
   private static void updateRelations(ISkynetRelationLinkEvent event, Collection<ArtifactTransactionModifiedEvent> xModifiedEvents) {
      if (event == null) return;

      try {
         RelationType relationType = RelationTypeManager.getType(event.getRelTypeId());
         Branch branch = BranchPersistenceManager.getBranch(event.getBranchId());
         Artifact aArtifact = ArtifactCache.getActive(event.getArtAId(), branch.getBranchId());
         Artifact bArtifact = ArtifactCache.getActive(event.getArtBId(), branch.getBranchId());
         boolean aArtifactLoaded = aArtifact != null;
         boolean bArtifactLoaded = bArtifact != null;

         if (!aArtifactLoaded && !bArtifactLoaded) {
            if (event instanceof NetworkRelationLinkDeletedEvent) {
               Sender sender =
                     new Sender(Source.Remote, instance, ((NetworkRelationLinkDeletedEvent) event).getAuthor());
               UnloadedRelation unloadedRelation =
                     new UnloadedRelation(branch.getBranchId(), event.getArtAId(), event.getArtATypeId(),
                           event.getArtBId(), event.getArtBTypeId(), event.getRelTypeId());
               xModifiedEvents.add(new RelationModifiedEvent(sender, RelationModType.Deleted, unloadedRelation));
            } else if (event instanceof NetworkRelationLinkModifiedEvent) {
               Sender sender =
                     new Sender(Source.Remote, instance, ((NetworkRelationLinkModifiedEvent) event).getAuthor());
               UnloadedRelation unloadedRelation =
                     new UnloadedRelation(branch.getBranchId(), event.getArtAId(), event.getArtATypeId(),
                           event.getArtBId(), event.getArtBTypeId(), event.getRelTypeId());
               xModifiedEvents.add(new RelationModifiedEvent(sender, RelationModType.Changed, unloadedRelation));
            } else if (event instanceof NetworkNewRelationLinkEvent) {
               Sender sender = new Sender(Source.Remote, instance, ((NetworkNewRelationLinkEvent) event).getAuthor());
               UnloadedRelation unloadedRelation =
                     new UnloadedRelation(branch.getBranchId(), event.getArtAId(), event.getArtATypeId(),
                           event.getArtBId(), event.getArtBTypeId(), event.getRelTypeId());
               xModifiedEvents.add(new RelationModifiedEvent(sender, RelationModType.Added, unloadedRelation));
            }
         }
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

                  Sender sender =
                        new Sender(Source.Remote, instance, ((NetworkRelationLinkModifiedEvent) event).getAuthor());
                  xModifiedEvents.add(new RelationModifiedEvent(sender, RelationModType.Changed, relation,
                        relation.getBranch(), relation.getRelationType().getTypeName(),
                        aOrderChanged ? relation.getASideName() : relation.getBSideName()));
               }
            } else if (event instanceof NetworkRelationLinkDeletedEvent) {
               RelationLink relation =
                     RelationManager.getLoadedRelation(RelationTypeManager.getType(event.getRelTypeId()),
                           event.getArtAId(), event.getArtBId(), branch, branch);
               if (relation != null) {
                  relation.deleteWithoutDirtyAndEvent();

                  Sender sender =
                        new Sender(Source.Remote, instance, ((NetworkRelationLinkDeletedEvent) event).getAuthor());
                  xModifiedEvents.add(new RelationModifiedEvent(sender, RelationModType.Deleted, relation,
                        relation.getBranch(), relation.getRelationType().getTypeName(), ""));
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

                  Sender sender =
                        new Sender(Source.Remote, instance, ((NetworkRelationLinkDeletedEvent) event).getAuthor());
                  xModifiedEvents.add(new RelationModifiedEvent(sender, RelationModType.Added, relation,
                        relation.getBranch(), relation.getRelationType().getTypeName(), ""));
               }
            }
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
