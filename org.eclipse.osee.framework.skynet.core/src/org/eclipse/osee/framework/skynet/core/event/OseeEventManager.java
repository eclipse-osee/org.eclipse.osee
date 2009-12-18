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
import org.eclipse.osee.framework.core.client.ClientSessionManager;
import org.eclipse.osee.framework.core.exception.OseeAuthenticationRequiredException;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.jdk.core.util.OseeProperties;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactModType;
import org.eclipse.osee.framework.skynet.core.relation.RelationEventType;
import org.eclipse.osee.framework.skynet.core.relation.RelationLink;
import org.eclipse.osee.framework.skynet.core.utility.LoadedArtifacts;

/**
 * Front end to OSEE events. Provides ability to add and remove different event listeners as well as the ability to kick
 * framework events.
 * 
 * @author Donald G. Dunne
 */
public class OseeEventManager {

   private static IBranchEventListener testBranchEventListener;

   private static Sender getSender(Object sourceObject) throws OseeAuthenticationRequiredException {
      // Sender came from Remote Event Manager if source == sender
      if (sourceObject instanceof Sender && ((Sender) sourceObject).isRemote()) {
         return (Sender) sourceObject;
      }
      // Else, create new sender based on sourceObject
      return new Sender(sourceObject, ClientSessionManager.getSession());
   }

   /**
    * Kick local remote event manager event
    * 
    * @param sender
    * @param remoteEventServiceEventType
    * @throws OseeCoreException
    */
   public static void kickRemoteEventManagerEvent(Object source, RemoteEventServiceEventType remoteEventServiceEventType) throws OseeCoreException {
      if (InternalEventManager.isDisableEvents()) {
         return;
      }
      InternalEventManager.kickRemoteEventManagerEvent(getSender(source), remoteEventServiceEventType);
   }

   // Kick LOCAL and REMOTE broadcast event
   public static void kickBroadcastEvent(Object source, BroadcastEventType broadcastEventType, String[] userIds, String message) throws OseeCoreException {
      if (isDisableEvents()) {
         return;
      }
      InternalEventManager.kickBroadcastEvent(getSender(source), broadcastEventType, userIds, message);
   }

   //Kick LOCAL and REMOTE branch events
   public static void kickBranchEvent(Object source, BranchEventType branchEventType, int branchId) throws OseeCoreException {
      if (testBranchEventListener != null) {
         testBranchEventListener.handleBranchEvent(getSender(source), branchEventType, branchId);
      }
      if (isDisableEvents()) {
         return;
      }
      InternalEventManager.kickBranchEvent(getSender(source), branchEventType, branchId);
   }

   // Kick LOCAL and REMOTE branch events
   public static void kickMergeBranchEvent(Object source, MergeBranchEventType branchEventType, int branchId) throws OseeCoreException {
      if (isDisableEvents()) {
         return;
      }
      InternalEventManager.kickMergeBranchEvent(getSender(source), branchEventType, branchId);
   }

   // Kick LOCAL and REMOTE access control events
   public static void kickAccessControlArtifactsEvent(Object source, final AccessControlEventType accessControlModType, final LoadedArtifacts loadedArtifacts) throws OseeAuthenticationRequiredException {
      if (isDisableEvents()) {
         return;
      }
      InternalEventManager.kickAccessControlArtifactsEvent(getSender(source), accessControlModType, loadedArtifacts);
   }

   // Kick local event to notify application that the branch to artifact cache has been updated
   public static void kickLocalBranchToArtifactCacheUpdateEvent(Object source) throws OseeCoreException {
      if (isDisableEvents()) {
         return;
      }
      InternalEventManager.kickLocalBranchToArtifactCacheUpdateEvent(getSender(source));
   }

   // Kick LOCAL artifact modified event; This event does NOT go external
   public static void kickArtifactModifiedEvent(Object source, ArtifactModType artifactModType, Artifact artifact) throws OseeCoreException {
      if (isDisableEvents()) {
         return;
      }
      InternalEventManager.kickArtifactModifiedEvent(getSender(source), artifactModType, artifact);
   }

   // Kick LOCAL relation modified event; This event does NOT go external
   public static void kickRelationModifiedEvent(Object source, RelationEventType relationEventType, RelationLink link, Branch branch, String relationType) throws OseeCoreException {
      if (isDisableEvents()) {
         return;
      }
      InternalEventManager.kickRelationModifiedEvent(getSender(source), relationEventType, link, branch, relationType);
   }

   // Kick LOCAL and REMOTE purged event depending on sender
   public static void kickArtifactsPurgedEvent(Object source, LoadedArtifacts loadedArtifacts) throws OseeCoreException {
      if (isDisableEvents()) {
         return;
      }
      InternalEventManager.kickArtifactsPurgedEvent(getSender(source), loadedArtifacts);
   }

   // Kick LOCAL and REMOTE artifact change type depending on sender
   public static void kickArtifactsChangeTypeEvent(Object source, int toArtifactTypeId, LoadedArtifacts loadedArtifacts) throws OseeCoreException {
      if (isDisableEvents()) {
         return;
      }
      InternalEventManager.kickArtifactsChangeTypeEvent(getSender(source), toArtifactTypeId, loadedArtifacts);
   }

   // Kick LOCAL and REMOTE transaction deleted event
   public static void kickTransactionsDeletedEvent(Object source, int[] transactionIds) throws OseeCoreException {
      if (isDisableEvents()) {
         return;
      }
      //TODO This needs to be converted into the individual artifacts and relations that were deleted/modified
      InternalEventManager.kickTransactionsDeletedEvent(getSender(source), transactionIds);
   }

   // Kick LOCAL and REMOTE transaction event
   public static void kickTransactionEvent(Object source, Collection<ArtifactTransactionModifiedEvent> xModifiedEvents) throws OseeAuthenticationRequiredException {
      if (isDisableEvents()) {
         return;
      }
      InternalEventManager.kickTransactionEvent(getSender(source), xModifiedEvents);
   }

   // Kick LOCAL transaction event
   public static void kickArtifactReloadEvent(Object source, Collection<? extends Artifact> artifacts) throws OseeAuthenticationRequiredException {
      if (isDisableEvents()) {
         return;
      }
      InternalEventManager.kickArtifactReloadEvent(getSender(source), artifacts);
   }

   /**
    * Add a priority listener. This should only be done for caches where they need to be updated before all other
    * listeners are called.
    */
   public static void addPriorityListener(IEventListener listener) {
      InternalEventManager.addPriorityListener(listener);
   }

   public static void addListener(IEventListener listener) {
      InternalEventManager.addListener(listener);
   }

   public static void removeListener(IEventListener listener) {
      InternalEventManager.removeListeners(listener);
   }

   public static boolean isDisableEvents() {
      return InternalEventManager.isDisableEvents();
   }

   // Turn off all event processing including LOCAL and REMOTE
   public static void setDisableEvents(boolean disableEvents) {
      InternalEventManager.setDisableEvents(disableEvents);
   }

   // Return report showing all listeners registered
   public static String getListenerReport() {
      return InternalEventManager.getListenerReport();
   }

   // Registration for branch events; for test only
   public static void registerBranchEventListenerForTest(IBranchEventListener branchEventListener) {
      if (!OseeProperties.isInTest()) {
         throw new IllegalStateException("Invalid registration for production");
      }
      testBranchEventListener = branchEventListener;
   }
}
