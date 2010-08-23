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
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import org.eclipse.osee.framework.core.client.ClientSessionManager;
import org.eclipse.osee.framework.core.data.OseeBranch;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.core.exception.OseeAuthenticationRequiredException;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.jdk.core.util.OseeProperties;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.messaging.event.res.RemoteEvent;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactModType;
import org.eclipse.osee.framework.skynet.core.event.systems.EventManagerData;
import org.eclipse.osee.framework.skynet.core.event.systems.InternalEventManager2;
import org.eclipse.osee.framework.skynet.core.event.systems.LegacyEventManager;
import org.eclipse.osee.framework.skynet.core.event2.AccessControlEvent;
import org.eclipse.osee.framework.skynet.core.event2.ArtifactEvent;
import org.eclipse.osee.framework.skynet.core.event2.BranchEvent;
import org.eclipse.osee.framework.skynet.core.event2.BroadcastEvent;
import org.eclipse.osee.framework.skynet.core.event2.TransactionChange;
import org.eclipse.osee.framework.skynet.core.event2.TransactionEvent;
import org.eclipse.osee.framework.skynet.core.event2.TransactionEventType;
import org.eclipse.osee.framework.skynet.core.event2.artifact.EventBasicGuidArtifact;
import org.eclipse.osee.framework.skynet.core.event2.artifact.EventModType;
import org.eclipse.osee.framework.skynet.core.event2.filter.BranchGuidEventFilter;
import org.eclipse.osee.framework.skynet.core.event2.filter.IEventFilter;
import org.eclipse.osee.framework.skynet.core.internal.Activator;
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
   private static List<IEventFilter> commonBranchEventFilter;
   private static BranchGuidEventFilter commonBranchGuidEvenFilter;

   private static EventManagerData eventManagerData;

   private OseeEventManager() {
      // Static methods only;
   }

   public static void setEventManagerData(EventManagerData eventManagerData) {
      OseeEventManager.eventManagerData = eventManagerData;
   }

   /**
    * Add a priority listener. This should only be done for caches where they need to be updated before all other
    * listeners are called.
    */
   public static void addPriorityListener(IEventListener listener) {
      if (listener == null) {
         throw new IllegalArgumentException("listener can not be null");
      }
      Collection<IEventListener> priorityListeners = eventManagerData.getPriorityListeners();
      if (!priorityListeners.contains(listener)) {
         priorityListeners.add(listener);
      }
   }

   public static void addListener(IEventListener listener) {
      if (listener == null) {
         throw new IllegalArgumentException("listener can not be null");
      }
      Collection<IEventListener> listeners = eventManagerData.getListeners();
      if (!listeners.contains(listener)) {
         listeners.add(listener);
      }
   }

   public static void removeAllListeners() {
      eventManagerData.getListeners().clear();
      eventManagerData.getPriorityListeners().clear();
   }

   public static void removeListener(IEventListener listener) {
      eventManagerData.getListeners().remove(listener);
      eventManagerData.getPriorityListeners().remove(listener);
   }

   private static LegacyEventManager getLegacyEventManager() {
      return eventManagerData.getLegacyEventManager();
   }

   private static InternalEventManager2 getEventManager() {
      return eventManagerData.getMessageEventManager();
   }

   public static EventSystemPreferences getPreferences() {
      return eventManagerData.getPreferences();
   }

   public static boolean isLegacyEventManagerConnected() {
      return getLegacyEventManager() != null ? getLegacyEventManager().isConnected() : false;
   }

   public static boolean isEventManagerConnected() {
      return getEventManager() != null ? getEventManager().isConnected() : false;
   }

   public static String getLegacyConnectionDetails() {
      StringBuilder sb = new StringBuilder();
      sb.append("osee.jini.lookup.groups [");
      sb.append(System.getProperty("osee.jini.lookup.groups"));
      sb.append("]");
      sb.append("eventSystem [");
      sb.append(getPreferences().getEventSystemType());
      sb.append("]");
      return sb.toString();
   }

   public static String getConnectionDetails() {
      EventSystemPreferences preferences = getPreferences();
      StringBuilder sb = new StringBuilder();
      sb.append("oseeEventBrokerUri [" + preferences.getOseeEventBrokerUri() + "]");
      sb.append("eventDebug [" + preferences.getEventDebug() + "]");
      sb.append("eventSystem [" + preferences.getEventSystemType() + "]");
      return sb.toString();
   }

   public static int getNumberOfListeners() {
      int toReturn = -1;
      if (isOldEvents() || isNewEvents()) {
         toReturn = eventManagerData.getListeners().size();
      }
      return toReturn;
   }

   private static Sender createSender(Object sourceObject) throws OseeAuthenticationRequiredException {
      Sender sender = null;
      // Sender came from Remote Event Manager if source == sender
      if (sourceObject instanceof Sender && ((Sender) sourceObject).isRemote()) {
         sender = (Sender) sourceObject;
      } else {
         // create new sender based on sourceObject
         sender = new Sender(sourceObject, ClientSessionManager.getSession());
      }
      return sender;
   }

   // Only Used for Testing purposes
   public static void internalTestSendRemoteEvent(final RemoteEvent remoteEvent) throws RemoteException {
      if (isNewEvents()) {
         getEventManager().testSendRemoteEventThroughFrameworkListener(remoteEvent);
      }
   }

   // Only Used for Testing purposes
   public static void internalTestProcessBranchEvent(Sender sender, BranchEvent branchEvent) {
      if (isNewEvents()) {
         getEventManager().processBranchEvent(sender, branchEvent);
      }
   }

   // Only Used for Testing purposes
   public static void internalTestProcessEventArtifactsAndRelations(Sender sender, ArtifactEvent artifactEvent) {
      if (isNewEvents()) {
         getEventManager().processEventArtifactsAndRelations(sender, artifactEvent);
      }
   }

   // Kick LOCAL remote-event event
   public static void kickLocalRemEvent(Object source, RemoteEventServiceEventType remoteEventServiceEventType) throws OseeCoreException {
      if (isDisableEvents()) {
         return;
      }
      if (isOldEvents()) {
         getLegacyEventManager().kickRemoteEventManagerEvent(createSender(source), remoteEventServiceEventType);
      }
      if (isNewEvents()) {
         getEventManager().kickLocalRemEvent(createSender(source), remoteEventServiceEventType);
      }
   }

   // Kick LOCAL and REMOTE broadcast event
   public static void kickBroadcastEvent(Object source, BroadcastEvent broadcastEvent) throws OseeCoreException {
      if (isDisableEvents()) {
         return;
      }
      if (isOldEvents()) {
         getLegacyEventManager().kickBroadcastEvent(createSender(source), broadcastEvent.getBroadcastEventType(),
            broadcastEvent.getUsers().toArray(new String[broadcastEvent.getUsers().size()]),
            broadcastEvent.getMessage());
      }
      if (isNewEvents()) {
         getEventManager().kickBroadcastEvent(createSender(source), broadcastEvent);
      }
   }

   //Kick LOCAL and REMOTE branch events
   public static void kickBranchEvent(Object source, BranchEvent branchEvent, int branchId) throws OseeCoreException {
      EventUtil.eventLog("OEM: kickBranchEvent: type: " + branchEvent.getEventType() + " guid: " + branchEvent.getBranchGuid() + " - " + source);
      if (testBranchEventListener != null) {
         testBranchEventListener.handleBranchEventREM1(createSender(source), branchEvent.getEventType(), branchId);
      }
      if (isDisableEvents()) {
         return;
      }
      if (isOldEvents()) {
         getLegacyEventManager().kickBranchEvent(createSender(source), branchEvent.getEventType(), branchId);
      }
      branchEvent.setNetworkSender(createSender(source).getNetworkSender());
      if (isNewEvents()) {
         getEventManager().kickBranchEvent(createSender(source), branchEvent);
      }
   }

   // Kick LOCAL and REMOTE branch events
   public static void kickMergeBranchEvent(Object source, MergeBranchEventType branchEventType, int branchId) throws OseeCoreException {
      if (isDisableEvents()) {
         return;
      }
      if (isOldEvents()) {
         getLegacyEventManager().kickMergeBranchEvent(createSender(source), branchEventType, branchId);
         // Handled by kickMergeBranchEvent for new Events
      }
   }

   // Kick LOCAL and REMOTE access control events
   public static void kickAccessControlArtifactsEvent(Object source, AccessControlEvent accessControlEvent, final LoadedArtifacts loadedArtifacts) throws OseeAuthenticationRequiredException {
      if (isDisableEvents()) {
         return;
      }
      accessControlEvent.setNetworkSender(createSender(source).getNetworkSender());
      if (isOldEvents()) {
         getLegacyEventManager().kickAccessControlArtifactsEvent(createSender(source), accessControlEvent,
            loadedArtifacts);
      }
      if (isNewEvents()) {
         getEventManager().kickAccessControlArtifactsEvent(createSender(source), accessControlEvent);
      }
   }

   // Kick LOCAL artifact modified event; This event does NOT go external
   public static void kickArtifactModifiedEvent(Object source, ArtifactModType artifactModType, Artifact artifact) throws OseeCoreException {
      if (isDisableEvents()) {
         return;
      }
      if (isOldEvents()) {
         getLegacyEventManager().kickArtifactModifiedEvent(createSender(source), artifactModType, artifact);
      }
   }

   // Kick LOCAL relation modified event; This event does NOT go external
   public static void kickRelationModifiedEvent(Object source, RelationEventType relationEventType, RelationLink link, Branch branch, String relationType) throws OseeCoreException {
      if (isDisableEvents()) {
         return;
      }
      if (isOldEvents()) {
         getLegacyEventManager().kickRelationModifiedEvent(createSender(source), relationEventType, link, branch,
            relationType);
      }
   }

   // Kick LOCAL and REMOTE purged event depending on sender
   public static void kickArtifactsPurgedEvent(Object source, LoadedArtifacts loadedArtifacts, Set<EventBasicGuidArtifact> artifactChanges) throws OseeCoreException {
      if (isDisableEvents()) {
         return;
      }
      if (isOldEvents()) {
         getLegacyEventManager().kickArtifactsPurgedEvent(createSender(source), loadedArtifacts);
         // Handled by kickTransactionEvent for new Events
      }
   }

   // Kick LOCAL and REMOTE artifact change type depending on sender
   public static void kickArtifactsChangeTypeEvent(Object source, int toArtifactTypeId, String toArtifactTypeGuid, LoadedArtifacts loadedArtifacts, Set<EventBasicGuidArtifact> artifactChanges) throws OseeCoreException {
      if (isDisableEvents()) {
         return;
      }
      if (isOldEvents()) {
         getLegacyEventManager().kickArtifactsChangeTypeEvent(createSender(source), toArtifactTypeId, loadedArtifacts);
         // Handled by kickTransactionEvent for new Events
      }
   }

   // Kick LOCAL and REMOTE transaction deleted event
   public static void kickTransactionEvent(Object source, final TransactionEvent transactionEvent) throws OseeCoreException {
      if (isDisableEvents()) {
         return;
      }
      Set<Integer> transactionIds = new HashSet<Integer>();
      for (TransactionChange transChange : transactionEvent.getTransactions()) {
         transactionIds.add(transChange.getTransactionId());
      }
      int[] transIds = new int[transactionIds.size()];
      int x = 0;
      for (Integer value : transactionIds) {
         transIds[x++] = value.intValue();
      }
      if (transactionEvent.getEventType() == TransactionEventType.Purged && OseeEventManager.isOldEvents()) {
         getLegacyEventManager().kickTransactionsPurgedEvent(createSender(source), transIds);
      }
      transactionEvent.setNetworkSender(createSender(source).getNetworkSender());
      if (isNewEvents()) {
         getEventManager().kickTransactionEvent(createSender(source), transactionEvent);
      }
   }

   // Kick LOCAL and REMOTE transaction event
   public static void kickPersistEvent(Object source, ArtifactEvent artifactEvent) throws OseeAuthenticationRequiredException {
      if (isDisableEvents()) {
         return;
      }
      if (artifactEvent.getSkynetTransactionDetails() != null && OseeEventManager.isOldEvents()) {
         getLegacyEventManager().kickPersistEvent(createSender(source), artifactEvent.getSkynetTransactionDetails());
      }
      artifactEvent.setNetworkSender(createSender(source).getNetworkSender());
      if (isNewEvents()) {
         getEventManager().kickArtifactEvent(createSender(source), artifactEvent);
      }
   }

   // Kick LOCAL transaction event
   public static void kickLocalArtifactReloadEvent(Object source, Collection<? extends Artifact> artifacts) throws OseeCoreException {
      if (isDisableEvents()) {
         return;
      }
      if (isOldEvents()) {
         getLegacyEventManager().kickArtifactReloadEvent(createSender(source), artifacts);
      }
      ArtifactEvent artifactEvent = new ArtifactEvent(artifacts.iterator().next().getBranch());
      artifactEvent.getArtifacts().addAll(EventBasicGuidArtifact.get(EventModType.Reloaded, artifacts));
      if (isNewEvents()) {
         getEventManager().kickLocalArtifactReloadEvent(createSender(source), artifactEvent);
      }
   }

   public static boolean isDisableEvents() {
      return getPreferences().isDisableEvents();
   }

   // Turn off all event processing including LOCAL and REMOTE
   public static void setDisableEvents(boolean disableEvents) {
      getPreferences().setDisableEvents(disableEvents);
   }

   // Return report showing all listeners registered
   public static String getListenerReport() {
      String toReturn;
      if (isOldEvents() || isNewEvents()) {
         toReturn =
            EventUtil.getListenerReport(eventManagerData.getListeners(), eventManagerData.getPriorityListeners());
      } else {
         toReturn = "Neither event system is active";
      }
      return toReturn;
   }

   // Registration for branch events; for test only
   public static void registerBranchEventListenerForTest(IBranchEventListener branchEventListener) {
      if (!OseeProperties.isInTest()) {
         throw new IllegalStateException("Invalid registration for production");
      }
      testBranchEventListener = branchEventListener;
   }

   /**
    * If old event kicks and listens should be used
    */
   private static boolean isOldEvents() {
      return !isNewEvents();
   }

   /**
    * If new event kicks and listens should be used
    */
   private static boolean isNewEvents() {
      return getPreferences().isNewEvents();
   }

   public static List<IEventFilter> getEventFiltersForBranch(Branch branch) {
      return getEventFiltersForBranch(branch.getName(), branch.getGuid());
   }

   public static List<IEventFilter> getEventFiltersForBranch(final String branchName, final String branchGuid) {
      try {
         List<IEventFilter> eventFilters = new ArrayList<IEventFilter>(2);
         eventFilters.add(new BranchGuidEventFilter(new OseeBranch(branchName, branchGuid)));
         return eventFilters;
      } catch (Exception ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
      return null;
   }

   public static List<IEventFilter> getCommonBranchEventFilters() {
      try {
         if (commonBranchEventFilter == null) {
            commonBranchEventFilter = new ArrayList<IEventFilter>(2);
            commonBranchEventFilter.add(getCommonBranchFilter());
         }
      } catch (Exception ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
      return commonBranchEventFilter;
   }

   public static BranchGuidEventFilter getCommonBranchFilter() {
      if (commonBranchGuidEvenFilter == null) {
         commonBranchGuidEvenFilter = new BranchGuidEventFilter(CoreBranches.COMMON);
      }
      return commonBranchGuidEvenFilter;
   }
}
