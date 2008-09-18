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

import java.util.Collection;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactModType;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.artifact.BranchModType;
import org.eclipse.osee.framework.skynet.core.dbinit.ApplicationServer;
import org.eclipse.osee.framework.skynet.core.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.relation.RelationLink;
import org.eclipse.osee.framework.skynet.core.relation.RelationModType;
import org.eclipse.osee.framework.skynet.core.utility.LoadedArtifacts;

/**
 * @author Donald G. Dunne
 */
public class OseeEventManager {

   private static Sender getSender(Object sourceObject) {
      // Sender came from Remote Event Manager if source == sender
      if ((sourceObject instanceof Sender) && ((Sender) sourceObject).isRemote()) {
         return (Sender) sourceObject;
      }
      // Else, create new sender based on sourceObject
      return new Sender(sourceObject, ApplicationServer.getOseeSession());
   }

   /**
    * Kick local remote event manager event
    * 
    * @param sender
    * @param remoteEventModType
    * @throws OseeCoreException
    */
   public static void kickRemoteEventManagerEvent(Object source, RemoteEventModType remoteEventModType) throws OseeCoreException {
      if (InternalEventManager.isDisableEvents()) return;
      InternalEventManager.kickRemoteEventManagerEvent(getSender(source), remoteEventModType);
   }

   /**
    * Kick Local and Remote broadcast event
    * 
    * @param sender
    * @param broadcastEventType
    * @param userIds (currently only used for disconnect_skynet)
    * @param message
    * @throws OseeCoreException
    */
   public static void kickBroadcastEvent(Object source, BroadcastEventType broadcastEventType, String[] userIds, String message) throws OseeCoreException {
      if (isDisableEvents()) return;
      InternalEventManager.kickBroadcastEvent(getSender(source), broadcastEventType, userIds, message);
   }

   /**
    * Kick local and remote branch events
    * 
    * @param sender
    * @param branchModType
    * @param branchId
    * @throws OseeCoreException
    */
   public static void kickBranchEvent(Object source, BranchModType branchModType, int branchId) throws OseeCoreException {
      if (isDisableEvents()) return;
      InternalEventManager.kickBranchEvent(getSender(source), branchModType, branchId);
   }

   /**
    * Kick local and remote access control events
    * 
    * @param sender
    * @param branchModType
    * @param branchId
    * @throws OseeCoreException
    */
   public static void kickAccessControlArtifactsEvent(Object source, final AccessControlModType accessControlModType, final LoadedArtifacts loadedArtifacts) throws OseeCoreException {
      if (isDisableEvents()) return;
      InternalEventManager.kickAccessControlArtifactsEvent(getSender(source), accessControlModType, loadedArtifacts);
   }

   /**
    * Kick local event to notify application that the branch to artifact cache has been updated
    * 
    * @param sender
    * @param branchModType
    * @param branchId
    * @throws OseeCoreException
    */
   public static void kickLocalBranchToArtifactCacheUpdateEvent(Object source) throws OseeCoreException {
      if (isDisableEvents()) return;
      InternalEventManager.kickLocalBranchToArtifactCacheUpdateEvent(getSender(source));
   }

   /**
    * Kick local artifact modified event; This event does NOT go external
    * 
    * @param sender local if kicked from internal; remote if from external
    * @param loadedArtifacts
    * @throws OseeCoreException
    */
   public static void kickArtifactModifiedEvent(Object source, ArtifactModType artifactModType, Artifact artifact) throws OseeCoreException {
      if (isDisableEvents()) return;
      InternalEventManager.kickArtifactModifiedEvent(getSender(source), artifactModType, artifact);
   }

   /**
    * Kick local relation modified event; This event does NOT go external
    * 
    * @param sender local if kicked from internal; remote if from external
    * @param loadedArtifacts
    * @throws OseeCoreException
    */
   public static void kickRelationModifiedEvent(Object source, RelationModType relationModType, RelationLink link, Branch branch, String relationType, String relationSide) throws OseeCoreException {
      if (isDisableEvents()) return;
      InternalEventManager.kickRelationModifiedEvent(getSender(source), relationModType, link, branch, relationType,
            relationSide);
   }

   /**
    * Kick local and remote purged event depending on sender
    * 
    * @param sender local if kicked from internal; remote if from external
    * @param loadedArtifacts
    * @throws OseeCoreException
    */
   public static void kickArtifactsPurgedEvent(Object source, LoadedArtifacts loadedArtifacts) throws OseeCoreException {
      if (isDisableEvents()) return;
      InternalEventManager.kickArtifactsPurgedEvent(getSender(source), loadedArtifacts);
   }

   /**
    * Kick local and remote artifact change type depending on sender
    * 
    * @param sender local if kicked from internal; remote if from external
    * @param toArtifactTypeId
    * @param loadedArtifacts
    * @throws OseeCoreException
    */
   public static void kickArtifactsChangeTypeEvent(Object source, int toArtifactTypeId, LoadedArtifacts loadedArtifacts) throws OseeCoreException {
      if (isDisableEvents()) return;
      InternalEventManager.kickArtifactsChangeTypeEvent(getSender(source), toArtifactTypeId, loadedArtifacts);
   }

   /**
    * Kick local and remote transaction deleted event
    * 
    * @param sender local if kicked from internal; remote if from external
    * @throws OseeCoreException
    */
   public static void kickTransactionsDeletedEvent(Object source, int[] transactionIds) throws OseeCoreException {
      if (isDisableEvents()) return;
      InternalEventManager.kickTransactionsDeletedEvent(getSender(source), transactionIds);
   }

   public static void kickTransactionEvent(Object source, Collection<ArtifactTransactionModifiedEvent> xModifiedEvents) {
      if (isDisableEvents()) return;
      InternalEventManager.kickTransactionEvent(getSender(source), xModifiedEvents);
   }

   /**
    * Add listeners
    * 
    * @param key unique object that will allow for removing all or specific listeners in removeListners
    * @param listener
    */
   public static void addListener(Object key, IEventListner listener) {
      InternalEventManager.addListener(key, listener);
   }

   public static void removeListener(Object key, IEventListner listener) {
      InternalEventManager.removeListener(key, listener);
   }

   public static void removeListeners(Object key) {
      InternalEventManager.removeListeners(key);
   }

   /**
    * @return the disableEvents
    */
   public static boolean isDisableEvents() {
      return InternalEventManager.isDisableEvents();
   }

   /**
    * @param disableEvents the disableEvents to set
    */
   public static void setDisableEvents(boolean disableEvents) {
      InternalEventManager.setDisableEvents(disableEvents);
   }

}
