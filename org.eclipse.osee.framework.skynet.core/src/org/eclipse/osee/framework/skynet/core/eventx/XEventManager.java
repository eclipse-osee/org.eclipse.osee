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
package org.eclipse.osee.framework.skynet.core.eventx;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import org.eclipse.osee.framework.jdk.core.type.HashCollection;
import org.eclipse.osee.framework.messaging.event.skynet.ISkynetEvent;
import org.eclipse.osee.framework.messaging.event.skynet.event.NetworkArtifactAddedEvent;
import org.eclipse.osee.framework.messaging.event.skynet.event.NetworkArtifactChangeTypeEvent;
import org.eclipse.osee.framework.messaging.event.skynet.event.NetworkArtifactDeletedEvent;
import org.eclipse.osee.framework.messaging.event.skynet.event.NetworkArtifactModifiedEvent;
import org.eclipse.osee.framework.messaging.event.skynet.event.NetworkArtifactPurgeEvent;
import org.eclipse.osee.framework.messaging.event.skynet.event.NetworkNewRelationLinkEvent;
import org.eclipse.osee.framework.messaging.event.skynet.event.NetworkRelationLinkDeletedEvent;
import org.eclipse.osee.framework.messaging.event.skynet.event.NetworkRelationLinkModifiedEvent;
import org.eclipse.osee.framework.messaging.event.skynet.event.NetworkTransactionDeletedEvent;
import org.eclipse.osee.framework.skynet.core.SkynetActivator;
import org.eclipse.osee.framework.skynet.core.SkynetAuthentication;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactCache;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.artifact.RemoteEventManager;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactModifiedEvent.ArtifactModType;
import org.eclipse.osee.framework.skynet.core.dbinit.SkynetDbInit;
import org.eclipse.osee.framework.skynet.core.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.relation.RelationLink;
import org.eclipse.osee.framework.skynet.core.relation.RelationSide;
import org.eclipse.osee.framework.skynet.core.relation.RelationTypeManager;
import org.eclipse.osee.framework.skynet.core.relation.RelationModifiedEvent.RelationModType;
import org.eclipse.osee.framework.skynet.core.utility.LoadedArtifacts;
import org.eclipse.osee.framework.skynet.core.utility.RemoteArtifactEventFactory;
import org.eclipse.osee.framework.ui.plugin.event.Sender;
import org.eclipse.osee.framework.ui.plugin.event.UnloadedArtifact;
import org.eclipse.osee.framework.ui.plugin.event.UnloadedRelation;
import org.eclipse.osee.framework.ui.plugin.event.Sender.Source;

/**
 * @author Donald G. Dunne
 */
public class XEventManager {

   private static final HashCollection<Object, IXEventListener> listenerMap =
         new HashCollection<Object, IXEventListener>(false, HashSet.class, 100);
   public static final Collection<UnloadedArtifact> EMPTY_UNLOADED_ARTIFACTS = Collections.emptyList();
   private static final boolean debug = false;
   private static boolean disableEvents = false;

   public static void kickTransactionEvent(Source source, Collection<XModifiedEvent> xModifiedEvents) {
      if (isDisableEvents()) return;
      if (debug) System.out.println("kickTransactionEvent " + source + " #ModEvents: " + xModifiedEvents.size());
      // Roll-up change information
      Set<Artifact> cacheChangedArtifacts = new HashSet<Artifact>();
      Set<Artifact> cacheDeletedArtifacts = new HashSet<Artifact>();
      Set<Artifact> cacheAddedArtifacts = new HashSet<Artifact>();

      Set<UnloadedArtifact> unloadedChangedArtifacts = new HashSet<UnloadedArtifact>();
      Set<UnloadedArtifact> unloadedDeletedArtifacts = new HashSet<UnloadedArtifact>();
      Set<UnloadedArtifact> unloadedAddedArtifacts = new HashSet<UnloadedArtifact>();

      Set<LoadedRelation> cacheChangedRelations = new HashSet<LoadedRelation>();
      Set<LoadedRelation> cacheAddedRelations = new HashSet<LoadedRelation>();
      Set<LoadedRelation> cacheDeletedRelations = new HashSet<LoadedRelation>();

      Set<UnloadedRelation> unloadedChangedRelations = new HashSet<UnloadedRelation>();
      Set<UnloadedRelation> unloadedAddedRelations = new HashSet<UnloadedRelation>();
      Set<UnloadedRelation> unloadedDeletedRelations = new HashSet<UnloadedRelation>();

      Set<Artifact> cacheRelationChangedArtifacts = new HashSet<Artifact>();
      Set<Artifact> cacheRelationDeletedArtifacts = new HashSet<Artifact>();
      Set<Artifact> cacheRelationAddedArtifacts = new HashSet<Artifact>();

      for (XModifiedEvent xModifiedEvent : xModifiedEvents) {
         if (xModifiedEvent instanceof XArtifactModifiedEvent) {
            XArtifactModifiedEvent xArtifactModifiedEvent = (XArtifactModifiedEvent) xModifiedEvent;
            if (xArtifactModifiedEvent.artifactModType == ArtifactModType.Added) {
               if (xArtifactModifiedEvent.artifact != null) {
                  cacheAddedArtifacts.add(xArtifactModifiedEvent.artifact);
               } else {
                  unloadedAddedArtifacts.add(xArtifactModifiedEvent.unloadedArtifact);
               }
            }
            if (xArtifactModifiedEvent.artifactModType == ArtifactModType.Deleted) {
               if (xArtifactModifiedEvent.artifact != null) {
                  cacheDeletedArtifacts.add(xArtifactModifiedEvent.artifact);
               } else {
                  unloadedDeletedArtifacts.add(xArtifactModifiedEvent.unloadedArtifact);
               }
            }
            if (xArtifactModifiedEvent.artifactModType == ArtifactModType.Changed) {
               if (xArtifactModifiedEvent.artifact != null) {
                  cacheChangedArtifacts.add(xArtifactModifiedEvent.artifact);
               } else {
                  unloadedChangedArtifacts.add(xArtifactModifiedEvent.unloadedArtifact);
               }
            }
         }
         if (xModifiedEvent instanceof XRelationModifiedEvent) {
            XRelationModifiedEvent xRelationModifiedEvent = (XRelationModifiedEvent) xModifiedEvent;
            UnloadedRelation unloadedRelation = xRelationModifiedEvent.unloadedRelation;
            LoadedRelation loadedRelation = null;
            if (xRelationModifiedEvent.link != null) {
               try {
                  loadedRelation =
                        new LoadedRelation(xRelationModifiedEvent.link.getArtifactA(),
                              xRelationModifiedEvent.link.getArtifactB(),
                              xRelationModifiedEvent.link.getRelationType(), xRelationModifiedEvent.branch,
                              unloadedRelation);
               } catch (Exception ex) {
                  SkynetActivator.getLogger().log(Level.SEVERE, ex.getLocalizedMessage(), ex);
               }
            }
            if (unloadedRelation != null) {
               Artifact artA =
                     ArtifactCache.getActive(unloadedRelation.getArtifactAId(), unloadedRelation.getBranchId());
               Artifact artB =
                     ArtifactCache.getActive(unloadedRelation.getArtifactBId(), unloadedRelation.getBranchId());
               if (artA != null || artB != null) {
                  try {
                     loadedRelation =
                           new LoadedRelation(artA, artB,
                                 RelationTypeManager.getType(unloadedRelation.getRelationTypeId()),
                                 artA != null ? artA.getBranch() : artB.getBranch(), unloadedRelation);
                  } catch (Exception ex) {
                     SkynetActivator.getLogger().log(Level.SEVERE, ex.getLocalizedMessage(), ex);
                  }
               }
            }
            if (xRelationModifiedEvent.relationModType == RelationModType.Added) {
               if (loadedRelation != null) {
                  cacheAddedRelations.add(loadedRelation);
                  if (loadedRelation.getArtifactA() != null) cacheRelationAddedArtifacts.add(loadedRelation.getArtifactA());
                  if (loadedRelation.getArtifactB() != null) cacheRelationAddedArtifacts.add(loadedRelation.getArtifactB());
               }
               if (unloadedRelation != null) {
                  unloadedAddedRelations.add(unloadedRelation);
               }
            }
            if (xRelationModifiedEvent.relationModType == RelationModType.Deleted) {
               if (loadedRelation != null) {
                  cacheDeletedRelations.add(loadedRelation);
                  if (loadedRelation.getArtifactA() != null) cacheRelationDeletedArtifacts.add(loadedRelation.getArtifactA());
                  if (loadedRelation.getArtifactB() != null) cacheRelationDeletedArtifacts.add(loadedRelation.getArtifactB());
               }
               if (unloadedRelation != null) {
                  unloadedDeletedRelations.add(unloadedRelation);
               }
            }
            if (xRelationModifiedEvent.relationModType == RelationModType.Changed) {
               if (loadedRelation != null) {
                  cacheChangedRelations.add(loadedRelation);
                  if (loadedRelation.getArtifactA() != null) cacheRelationChangedArtifacts.add(loadedRelation.getArtifactA());
                  if (loadedRelation.getArtifactB() != null) cacheRelationChangedArtifacts.add(loadedRelation.getArtifactB());
               }
               if (unloadedRelation != null) {
                  unloadedChangedRelations.add(unloadedRelation);
               }
            }
         }
      }

      // Clean out known duplicates
      cacheChangedArtifacts.removeAll(cacheDeletedArtifacts);
      cacheAddedArtifacts.removeAll(cacheDeletedArtifacts);

      // Kick Local
      for (IXEventListener listener : listenerMap.getValues()) {
         if (listener instanceof IFrameworkTransactionEvent) {
            // Don't fail on any one listener's exception
            try {
               if (cacheDeletedArtifacts.size() > 0 || unloadedDeletedArtifacts.size() > 0) {
                  ((IFrameworkTransactionEvent) listener).handleArtifactsDeleted(source, cacheDeletedArtifacts,
                        unloadedDeletedArtifacts);
               }
               if (cacheAddedArtifacts.size() > 0 || unloadedAddedArtifacts.size() > 0) {
                  ((IFrameworkTransactionEvent) listener).handleArtifactsAdded(source, cacheAddedArtifacts,
                        unloadedAddedArtifacts);
               }
               if (cacheChangedArtifacts.size() > 0 || unloadedChangedArtifacts.size() > 0) {
                  ((IFrameworkTransactionEvent) listener).handleArtifactsChanged(source, cacheChangedArtifacts,
                        unloadedChangedArtifacts);
               }

               if (cacheRelationDeletedArtifacts.size() > 0 || cacheDeletedRelations.size() > 0 || unloadedDeletedRelations.size() > 0) {
                  ((IFrameworkTransactionEvent) listener).handleRelationsDeleted(source, cacheRelationDeletedArtifacts,
                        cacheDeletedRelations, unloadedDeletedRelations);
               }
               if (cacheRelationAddedArtifacts.size() > 0 || cacheAddedRelations.size() > 0 || unloadedAddedRelations.size() > 0) {
                  ((IFrameworkTransactionEvent) listener).handleRelationsAdded(source, cacheRelationAddedArtifacts,
                        cacheAddedRelations, unloadedAddedRelations);
               }
               if (cacheRelationChangedArtifacts.size() > 0 || cacheChangedRelations.size() > 0 || unloadedChangedRelations.size() > 0) {
                  ((IFrameworkTransactionEvent) listener).handleRelationsChanged(source, cacheRelationChangedArtifacts,
                        cacheChangedRelations, unloadedChangedRelations);
               }
            } catch (Exception ex) {
               SkynetActivator.getLogger().log(Level.SEVERE, ex.getLocalizedMessage(), ex);
            }
         }
      }
      // Kick Remote (If sender was Local)
      try {
         if (source == Source.Local) {
            List<ISkynetEvent> events = new ArrayList<ISkynetEvent>();
            for (XModifiedEvent xModifiedEvent : xModifiedEvents) {
               if (xModifiedEvent instanceof XArtifactModifiedEvent) {
                  XArtifactModifiedEvent xArtifactModifiedEvent = (XArtifactModifiedEvent) xModifiedEvent;
                  if (xArtifactModifiedEvent.artifactModType == ArtifactModType.Changed) {
                     Artifact artifact = xArtifactModifiedEvent.artifact;
                     events.add(new NetworkArtifactModifiedEvent(artifact.getBranch().getBranchId(),
                           xArtifactModifiedEvent.transactionNumber, artifact.getArtId(), artifact.getArtTypeId(),
                           artifact.getFactory().getClass().getCanonicalName(),
                           xArtifactModifiedEvent.dirtySkynetAttributeChanges,
                           xArtifactModifiedEvent.sender.getAuthor()));
                  } else if (xArtifactModifiedEvent.artifactModType == ArtifactModType.Added) {
                     Artifact artifact = xArtifactModifiedEvent.artifact;
                     events.add(new NetworkArtifactAddedEvent(artifact.getBranch().getBranchId(),
                           xArtifactModifiedEvent.transactionNumber, artifact.getArtId(), artifact.getArtTypeId(),
                           artifact.getFactory().getClass().getCanonicalName(),
                           xArtifactModifiedEvent.sender.getAuthor()));
                  } else if (xArtifactModifiedEvent.artifactModType == ArtifactModType.Deleted) {
                     Artifact artifact = xArtifactModifiedEvent.artifact;
                     events.add(new NetworkArtifactDeletedEvent(artifact.getBranch().getBranchId(),
                           xArtifactModifiedEvent.transactionNumber, artifact.getArtId(), artifact.getArtTypeId(),
                           artifact.getFactory().getClass().getCanonicalName(),
                           xArtifactModifiedEvent.sender.getAuthor()));
                  } else {
                     SkynetActivator.getLogger().log(Level.SEVERE,
                           "Unhandled xArtifactModifiedEvent event: " + xArtifactModifiedEvent);
                  }
               } else if (xModifiedEvent instanceof XRelationModifiedEvent) {
                  XRelationModifiedEvent xRelationModifiedEvent = (XRelationModifiedEvent) xModifiedEvent;
                  if (xRelationModifiedEvent.relationModType == RelationModType.Changed) {
                     RelationLink link = xRelationModifiedEvent.link;
                     Artifact aArtifact = link.getArtifactIfLoaded(RelationSide.SIDE_A);
                     Artifact bArtifact = link.getArtifactIfLoaded(RelationSide.SIDE_B);
                     NetworkRelationLinkModifiedEvent networkRelationLinkModifiedEvent =
                           new NetworkRelationLinkModifiedEvent(link.getGammaId(), link.getBranch().getBranchId(),
                                 link.getRelationId(), link.getAArtifactId(),
                                 (aArtifact != null ? aArtifact.getArtTypeId() : -1), link.getBArtifactId(),
                                 (bArtifact != null ? bArtifact.getArtTypeId() : -1), link.getRationale(),
                                 link.getAOrder(), link.getBOrder(), SkynetAuthentication.getUser().getArtId(),
                                 link.getRelationType().getRelationTypeId());
                     events.add(networkRelationLinkModifiedEvent);
                  } else if (xRelationModifiedEvent.relationModType == RelationModType.Deleted) {
                     RelationLink link = xRelationModifiedEvent.link;
                     Artifact aArtifact = link.getArtifactIfLoaded(RelationSide.SIDE_A);
                     Artifact bArtifact = link.getArtifactIfLoaded(RelationSide.SIDE_B);
                     NetworkRelationLinkDeletedEvent networkRelationLinkModifiedEvent =
                           new NetworkRelationLinkDeletedEvent(link.getRelationType().getRelationTypeId(),
                                 link.getGammaId(), link.getBranch().getBranchId(), link.getRelationId(),
                                 link.getArtifactId(RelationSide.SIDE_A),
                                 (aArtifact != null ? aArtifact.getArtTypeId() : -1),
                                 link.getArtifactId(RelationSide.SIDE_B),
                                 (bArtifact != null ? bArtifact.getArtTypeId() : -1),
                                 SkynetAuthentication.getUser().getArtId());
                     events.add(networkRelationLinkModifiedEvent);
                  } else if (xRelationModifiedEvent.relationModType == RelationModType.Added) {
                     RelationLink link = xRelationModifiedEvent.link;
                     Artifact aArtifact = link.getArtifactIfLoaded(RelationSide.SIDE_A);
                     Artifact bArtifact = link.getArtifactIfLoaded(RelationSide.SIDE_B);
                     NetworkNewRelationLinkEvent networkRelationLinkModifiedEvent =
                           new NetworkNewRelationLinkEvent(link.getGammaId(), link.getBranch().getBranchId(),
                                 link.getRelationId(), link.getAArtifactId(),
                                 (aArtifact != null ? aArtifact.getArtTypeId() : -1), link.getBArtifactId(),
                                 (bArtifact != null ? bArtifact.getArtTypeId() : -1), link.getRationale(),
                                 link.getAOrder(), link.getBOrder(), link.getRelationType().getRelationTypeId(),
                                 link.getRelationType().getTypeName(), SkynetAuthentication.getUser().getArtId());
                     events.add(networkRelationLinkModifiedEvent);
                  } else {
                     SkynetActivator.getLogger().log(Level.SEVERE,
                           "Unhandled xRelationModifiedEvent event: " + xRelationModifiedEvent);
                  }
               }
            }
         }
      } catch (Exception ex) {
         SkynetActivator.getLogger().log(Level.SEVERE, ex.getLocalizedMessage(), ex);
      }
   }

   /**
    * Kick local artifact modified event; This event does NOT go external
    * 
    * @param sender local if kicked from internal; remote if from external
    * @param loadedArtifacts
    * @throws OseeCoreException
    */
   public static void kickArtifactModifiedEvent(Sender sender, ArtifactModType artifactModType, Artifact artifact) throws OseeCoreException {
      if (isDisableEvents()) return;
      if (debug) System.out.println("kickArtifactModifiedEvent " + sender.getSource() + " - " + artifactModType + " - " + artifact.getHumanReadableId() + " - " + artifact.getDirtySkynetAttributeChanges());
      // Kick Local
      for (IXEventListener listener : listenerMap.getValues()) {
         if (listener instanceof IArtifactModifiedEventListener) {
            // Don't fail on any one listener's exception
            try {
               ((IArtifactModifiedEventListener) listener).handleArtifactModifiedEvent(sender, artifactModType,
                     artifact);
            } catch (Exception ex) {
               SkynetActivator.getLogger().log(Level.SEVERE, ex.getLocalizedMessage(), ex);
            }
         }
      }
   }

   /**
    * Kick local relation modified event; This event does NOT go external
    * 
    * @param sender local if kicked from internal; remote if from external
    * @param loadedArtifacts
    * @throws OseeCoreException
    */
   public static void kickRelationModifiedEvent(Sender sender, RelationModType relationModType, RelationLink link, Branch branch, String relationType, String relationSide) throws OseeCoreException {
      if (isDisableEvents()) return;
      if (debug) System.out.println("kickRelationModifiedEvent " + sender.getSource() + " - " + relationType + " - " + link.getRelationType());
      // Kick Local
      for (IXEventListener listener : listenerMap.getValues()) {
         if (listener instanceof IRelationModifiedEventListener) {
            // Don't fail on any one listener's exception
            try {
               ((IRelationModifiedEventListener) listener).handleRelationModifiedEvent(sender, relationModType, link,
                     branch, relationType, relationSide);
            } catch (Exception ex) {
               SkynetActivator.getLogger().log(Level.SEVERE, ex.getLocalizedMessage(), ex);
            }
         }
      }
   }

   /**
    * Kick local and remote purged event depending on sender
    * 
    * @param sender local if kicked from internal; remote if from external
    * @param loadedArtifacts
    * @throws OseeCoreException
    */
   public static void kickArtifactsPurgedEvent(Sender sender, LoadedArtifacts loadedArtifacts) throws OseeCoreException {
      if (isDisableEvents()) return;
      if (debug) System.out.println("kickArtifactsPurgedEvent " + sender.getSource() + " - " + loadedArtifacts);
      // Kick Local
      for (IXEventListener listener : listenerMap.getValues()) {
         if (listener instanceof IArtifactsPurgedEventListener) {
            // Don't fail on any one listener's exception
            try {
               ((IArtifactsPurgedEventListener) listener).handleArtifactsPurgedEvent(sender,
                     loadedArtifacts.getLoadedArtifacts(), EMPTY_UNLOADED_ARTIFACTS);
            } catch (Exception ex) {
               SkynetActivator.getLogger().log(Level.SEVERE, ex.getLocalizedMessage(), ex);
            }
         }
      }
      // Kick Remote (If source was Local)
      try {
         if (sender.getSource() == Source.Local) {
            RemoteEventManager.kick(new NetworkArtifactPurgeEvent(
                  loadedArtifacts.getLoadedArtifacts().iterator().next().getBranch().getBranchId(),
                  loadedArtifacts.getAllArtifactIds(), loadedArtifacts.getAllArtifactTypeIds(),
                  RemoteArtifactEventFactory.getAuthor()));
         }
      } catch (Exception ex) {
         throw new OseeCoreException(ex);
      }
   }

   /**
    * Kick local and remote artifact change type depending on sender
    * 
    * @param sender local if kicked from internal; remote if from external
    * @param toArtifactTypeId
    * @param loadedArtifacts
    * @throws OseeCoreException
    */
   public static void kickArtifactsChangeTypeEvent(Sender sender, int toArtifactTypeId, LoadedArtifacts loadedArtifacts) throws OseeCoreException {
      if (isDisableEvents()) return;
      if (debug) System.out.println("kickArtifactsChangeTypeEvent " + sender.getSource() + " - " + loadedArtifacts);
      // Kick Local
      for (IXEventListener listener : listenerMap.getValues()) {
         if (listener instanceof IArtifactsChangeTypeEventListener) {
            // Don't fail on any one listener's exception
            try {
               ((IArtifactsChangeTypeEventListener) listener).handleArtifactsChangeTypeEvent(sender, toArtifactTypeId,
                     loadedArtifacts.getLoadedArtifacts(), EMPTY_UNLOADED_ARTIFACTS);
            } catch (Exception ex) {
               SkynetActivator.getLogger().log(Level.SEVERE, ex.getLocalizedMessage(), ex);
            }
         }
      }
      // Kick Remote (If source was Local)
      try {
         if (sender.getSource() == Source.Local) {
            RemoteEventManager.kick(new NetworkArtifactChangeTypeEvent(
                  loadedArtifacts.getLoadedArtifacts().iterator().next().getBranch().getBranchId(),
                  loadedArtifacts.getAllArtifactIds(), loadedArtifacts.getAllArtifactTypeIds(), toArtifactTypeId,
                  sender.getAuthor()));
         }
      } catch (Exception ex) {
         throw new OseeCoreException(ex);
      }
   }

   /**
    * Kick local and remote transaction deleted event
    * 
    * @param sender local if kicked from internal; remote if from external
    * @throws OseeCoreException
    */
   public static void kickTransactionsDeletedEvent(Sender sender, int[] transactionIds) throws OseeCoreException {
      if (isDisableEvents()) return;
      if (debug) System.out.println("kickTransactionsDeletedEvent " + sender.getSource() + " - " + transactionIds.length);
      // Kick Local
      for (IXEventListener listener : listenerMap.getValues()) {
         if (listener instanceof IArtifactsChangeTypeEventListener) {
            // Don't fail on any one listener's exception
            try {
               ((ITransactionsDeletedEventListener) listener).handleTransactionsDeletedEvent(sender, transactionIds);
            } catch (Exception ex) {
               SkynetActivator.getLogger().log(Level.SEVERE, ex.getLocalizedMessage(), ex);
            }
         }
      }
      // Kick Remote (If source was Local)
      try {
         if (sender.getSource() == Source.Local) {
            RemoteEventManager.kick(new NetworkTransactionDeletedEvent(sender.getAuthor(), transactionIds));
         }
      } catch (Exception ex) {
         throw new OseeCoreException(ex);
      }
   }

   public static void addListener(Object key, IXEventListener listener) {
      if (debug) System.out.println("addListener " + key + " - " + listener);
      listenerMap.put(key, listener);
   }

   public static void removeListeners(Object key, IXEventListener listener) {
      if (debug) System.out.println("removeListener " + key + " - " + listener);
      listenerMap.removeValue(key, listener);
   }

   public static void removeListeners(Object key) {
      if (debug) System.out.println("removeListeners ALL " + key);
      Set<IXEventListener> listenersToRemove = new HashSet<IXEventListener>();
      for (IXEventListener listener : listenerMap.getValues(key)) {
         listenersToRemove.add(listener);
      }
      for (IXEventListener listener : listenersToRemove) {
         listenerMap.removeValue(key, listener);
      }
   }

   /**
    * @return the disableEvents
    */
   public static boolean isDisableEvents() {
      return disableEvents || SkynetDbInit.isDbInit();
   }

   /**
    * @param disableEvents the disableEvents to set
    */
   public static void setDisableEvents(boolean disableEvents) {
      XEventManager.disableEvents = disableEvents;
   }

}
