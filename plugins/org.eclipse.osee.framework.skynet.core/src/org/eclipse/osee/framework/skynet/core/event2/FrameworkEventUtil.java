/*******************************************************************************
 * Copyright (c) 2010 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.skynet.core.event2;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.UserNotInDatabase;
import org.eclipse.osee.framework.core.model.event.DefaultBasicGuidArtifact;
import org.eclipse.osee.framework.core.model.event.DefaultBasicGuidRelationReorder;
import org.eclipse.osee.framework.core.model.event.RelationOrderModType;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.messaging.event.res.msgs.RemoteAccessControlEvent1;
import org.eclipse.osee.framework.messaging.event.res.msgs.RemoteAttributeChange1;
import org.eclipse.osee.framework.messaging.event.res.msgs.RemoteBasicGuidArtifact1;
import org.eclipse.osee.framework.messaging.event.res.msgs.RemoteBasicGuidRelation1;
import org.eclipse.osee.framework.messaging.event.res.msgs.RemoteBasicGuidRelationReorder1;
import org.eclipse.osee.framework.messaging.event.res.msgs.RemoteBranchEvent1;
import org.eclipse.osee.framework.messaging.event.res.msgs.RemoteBroadcastEvent1;
import org.eclipse.osee.framework.messaging.event.res.msgs.RemoteNetworkSender1;
import org.eclipse.osee.framework.messaging.event.res.msgs.RemotePersistEvent1;
import org.eclipse.osee.framework.messaging.event.res.msgs.RemoteTransactionChange1;
import org.eclipse.osee.framework.messaging.event.res.msgs.RemoteTransactionEvent1;
import org.eclipse.osee.framework.messaging.event.skynet.event.NetworkSender;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.event.AccessControlEventType;
import org.eclipse.osee.framework.skynet.core.event.BranchEventType;
import org.eclipse.osee.framework.skynet.core.event.BroadcastEventType;
import org.eclipse.osee.framework.skynet.core.event2.artifact.AttributeChange;
import org.eclipse.osee.framework.skynet.core.event2.artifact.EventBasicGuidArtifact;
import org.eclipse.osee.framework.skynet.core.event2.artifact.EventBasicGuidRelation;
import org.eclipse.osee.framework.skynet.core.event2.artifact.EventChangeTypeBasicGuidArtifact;
import org.eclipse.osee.framework.skynet.core.event2.artifact.EventModType;
import org.eclipse.osee.framework.skynet.core.event2.artifact.EventModifiedBasicGuidArtifact;
import org.eclipse.osee.framework.skynet.core.internal.Activator;
import org.eclipse.osee.framework.skynet.core.relation.RelationEventType;

/**
 * @author Donald G. Dunne
 */
public class FrameworkEventUtil {

   public static RemoteAccessControlEvent1 getRemoteAccessControlEvent(AccessControlEvent accessControlEvent) {
      RemoteAccessControlEvent1 event = new RemoteAccessControlEvent1();
      event.setNetworkSender(getRemoteNetworkSender(accessControlEvent.getNetworkSender()));
      event.setEventTypeGuid(accessControlEvent.getEventType().getGuid());
      for (DefaultBasicGuidArtifact guidArt : accessControlEvent.getArtifacts()) {
         event.getArtifacts().add(getRemoteBasicGuidArtifact(guidArt));
      }
      return event;
   }

   public static AccessControlEvent getAccessControlEvent(RemoteAccessControlEvent1 remEvent) {
      AccessControlEventType accessControlEventType = AccessControlEventType.getByGuid(remEvent.getEventTypeGuid());
      if (accessControlEventType != null) {
         AccessControlEvent accessControlEvent = new AccessControlEvent();
         accessControlEvent.setNetworkSender(getNetworkSender(remEvent.getNetworkSender()));
         for (RemoteBasicGuidArtifact1 remGuidArt : remEvent.getArtifacts()) {
            accessControlEvent.getArtifacts().add(getBasicGuidArtifact(remGuidArt));
         }
         return accessControlEvent;
      } else {
         OseeLog.log(Activator.class, Level.WARNING,
            "Unhandled AccessControl event type guid " + remEvent.getEventTypeGuid());
      }
      return null;
   }

   public static RemoteBroadcastEvent1 getRemoteBroadcastEvent(BroadcastEvent broadcastEvent) {
      RemoteBroadcastEvent1 event = new RemoteBroadcastEvent1();
      event.setNetworkSender(getRemoteNetworkSender(broadcastEvent.getNetworkSender()));
      event.setMessage(broadcastEvent.getMessage());
      event.setEventTypeGuid(broadcastEvent.getBroadcastEventType().getGuid());
      for (User user : broadcastEvent.getUsers()) {
         try {
            if (Strings.isValid(user.getUserId())) {
               event.getUserIds().add(user.getUserId());
            }
         } catch (OseeCoreException ex) {
            OseeLog.log(Activator.class, Level.SEVERE, ex);
         }
      }
      return event;
   }

   public static BroadcastEvent getBroadcastEvent(RemoteBroadcastEvent1 remEvent) {
      BroadcastEventType broadcastEventType = BroadcastEventType.getByGuid(remEvent.getEventTypeGuid());
      if (broadcastEventType != null) {
         BroadcastEvent broadcastEvent = new BroadcastEvent(broadcastEventType, null, remEvent.getMessage());
         for (String userId : remEvent.getUserIds()) {
            try {
               User user = UserManager.getUserByUserId(userId);
               if (user != null) {
                  broadcastEvent.addUser(user);
               }
            } catch (UserNotInDatabase ex) {
               // do nothing
            } catch (OseeCoreException ex) {
               OseeLog.log(Activator.class, Level.SEVERE, ex);
            }
         }
         broadcastEvent.setNetworkSender(getNetworkSender(remEvent.getNetworkSender()));
         return broadcastEvent;
      } else {
         OseeLog.log(Activator.class, Level.WARNING,
            "Unhandled broadcast event type guid " + remEvent.getEventTypeGuid());
      }
      return null;
   }

   public static RemoteBranchEvent1 getRemoteBranchEvent(BranchEvent branchEvent) {
      RemoteBranchEvent1 event = new RemoteBranchEvent1();
      event.setEventTypeGuid(branchEvent.getEventType().getGuid());
      event.setBranchGuid(branchEvent.getBranchGuid());
      event.setNetworkSender(getRemoteNetworkSender(branchEvent.getNetworkSender()));
      return event;
   }

   public static BranchEvent getBranchEvent(RemoteBranchEvent1 branchEvent) {
      BranchEventType branchEventType = BranchEventType.getByGuid(branchEvent.getEventTypeGuid());
      if (branchEventType != null) {
         BranchEvent event = new BranchEvent(branchEventType, branchEvent.getBranchGuid());
         event.setNetworkSender(getNetworkSender(branchEvent.getNetworkSender()));
         return event;
      } else {
         OseeLog.log(Activator.class, Level.WARNING, "Unhandled branch event type guid " + branchEvent.getBranchGuid());
      }
      return null;
   }

   public static RemoteTransactionEvent1 getRemoteTransactionEvent(TransactionEvent transEvent) {
      RemoteTransactionEvent1 event = new RemoteTransactionEvent1();
      event.setNetworkSender(getRemoteNetworkSender(transEvent.getNetworkSender()));
      event.setEventTypeGuid(transEvent.getEventType().getGuid());
      for (TransactionChange change : transEvent.getTransactions()) {
         RemoteTransactionChange1 remChange = new RemoteTransactionChange1();
         remChange.setBranchGuid(change.getBranchGuid());
         remChange.setTransactionId(change.getTransactionId());
         for (DefaultBasicGuidArtifact guidArt : change.getArtifacts()) {
            remChange.getArtifacts().add(getRemoteBasicGuidArtifact(guidArt));
         }
         event.getTransactions().add(remChange);
      }
      return event;
   }

   public static TransactionEvent getTransactionEvent(RemoteTransactionEvent1 remEvent) {
      TransactionEvent event = new TransactionEvent();
      event.setNetworkSender(getNetworkSender(remEvent.getNetworkSender()));
      event.setEventType(TransactionEventType.getByGuid(remEvent.getEventTypeGuid()));
      for (RemoteTransactionChange1 remChange : remEvent.getTransactions()) {
         TransactionChange change = new TransactionChange();
         change.setBranchGuid(remChange.getBranchGuid());
         change.setTransactionId(remChange.getTransactionId());
         for (RemoteBasicGuidArtifact1 remGuidArt : remChange.getArtifacts()) {
            change.getArtifacts().add(getBasicGuidArtifact(remGuidArt));
         }
         event.getTransactions().add(change);
      }
      return event;
   }

   public static RemotePersistEvent1 getRemotePersistEvent(ArtifactEvent transEvent) {
      RemotePersistEvent1 event = new RemotePersistEvent1();
      event.setNetworkSender(getRemoteNetworkSender(transEvent.getNetworkSender()));
      event.setBranchGuid(transEvent.getBranchGuid());
      event.setTransactionId(transEvent.getTransactionId());
      for (EventBasicGuidArtifact guidArt : transEvent.getArtifacts()) {
         if (guidArt.getModType() == EventModType.Modified) {
            event.getArtifacts().add(
               getRemoteBasicGuidArtifact(guidArt.getModType().getGuid(), guidArt.getBasicGuidArtifact(),
                  ((EventModifiedBasicGuidArtifact) guidArt).getAttributeChanges()));
         } else if (guidArt.getModType() == EventModType.ChangeType) {
            EventChangeTypeBasicGuidArtifact changeGuidArt = (EventChangeTypeBasicGuidArtifact) guidArt;
            RemoteBasicGuidArtifact1 remGuidArt =
               getRemoteBasicGuidArtifact(guidArt.getModType().getGuid(), guidArt.getBasicGuidArtifact(), null);
            remGuidArt.setArtTypeGuid(changeGuidArt.getFromArtTypeGuid());
            remGuidArt.setToArtTypeGuid(changeGuidArt.getArtTypeGuid());
            event.getArtifacts().add(remGuidArt);
         } else {
            event.getArtifacts().add(
               getRemoteBasicGuidArtifact(guidArt.getModType().getGuid(), guidArt.getBasicGuidArtifact(), null));
         }
      }
      for (EventBasicGuidRelation guidRel : transEvent.getRelations()) {
         event.getRelations().add(getRemoteBasicGuidRelation1(guidRel));
      }
      for (DefaultBasicGuidRelationReorder guidOrderRel : transEvent.getRelationOrderRecords()) {
         event.getRelationReorders().add(getRemoteBasicGuidRelationReorder1(guidOrderRel));
      }
      return event;
   }

   public static ArtifactEvent getPersistEvent(RemotePersistEvent1 remEvent) {
      ArtifactEvent event = new ArtifactEvent();
      event.setNetworkSender(getNetworkSender(remEvent.getNetworkSender()));
      event.setBranchGuid(remEvent.getBranchGuid());
      event.setTransactionId(remEvent.getTransactionId());
      for (RemoteBasicGuidArtifact1 remGuidArt : remEvent.getArtifacts()) {
         EventModType modType = EventModType.getType(remGuidArt.getModTypeGuid());
         // This can happen if new events are added that old releases don't handle
         if (modType == null) {
            OseeLog.log(Activator.class, Level.WARNING, String.format("Unhandled remote artifact [%s]", remGuidArt));
         } else {
            if (modType == EventModType.Modified) {
               event.getArtifacts().add(getEventModifiedBasicGuidArtifact(modType, remGuidArt));
            } else if (modType == EventModType.ChangeType) {
               event.getArtifacts().add(getEventChangeTypeBasicGuidArtifact(modType, remGuidArt));
            } else {
               event.getArtifacts().add(getEventBasicGuidArtifact(modType, remGuidArt));
            }
         }
      }
      for (RemoteBasicGuidRelation1 guidRel : remEvent.getRelations()) {
         EventBasicGuidRelation relEvent = getEventBasicGuidRelation(guidRel);
         // This can happen if new events are added that old releases don't handle
         if (relEvent == null) {
            OseeLog.log(Activator.class, Level.WARNING, String.format("Unhandled remote relation [%s]", guidRel));
         } else {
            event.getRelations().add(relEvent);
         }
      }
      for (RemoteBasicGuidRelationReorder1 guidReorder : remEvent.getRelationReorders()) {
         event.getRelationOrderRecords().add(getDefaultBasicGuidRelationReorder(guidReorder));
      }
      return event;
   }

   public static DefaultBasicGuidRelationReorder getDefaultBasicGuidRelationReorder(RemoteBasicGuidRelationReorder1 guidRelOrder) {
      DefaultBasicGuidRelationReorder guidArt =
         new DefaultBasicGuidRelationReorder(RelationOrderModType.getType(guidRelOrder.getModTypeGuid()),
            guidRelOrder.getBranchGuid(), guidRelOrder.getRelTypeGuid(),
            getBasicGuidArtifact(guidRelOrder.getParentArt()));
      return guidArt;
   }

   public static RemoteBasicGuidRelation1 getRemoteBasicGuidRelation1(EventBasicGuidRelation guidRel) {
      RemoteBasicGuidRelation1 remEvent = new RemoteBasicGuidRelation1();
      remEvent.setGammaId(guidRel.getGammaId());
      remEvent.setBranchGuid(guidRel.getBranchGuid());
      remEvent.setRelTypeGuid(guidRel.getRelTypeGuid());
      remEvent.setRelationId(guidRel.getRelationId());
      remEvent.setArtAId(guidRel.getArtAId());
      remEvent.setArtBId(guidRel.getArtBId());
      remEvent.setModTypeGuid(guidRel.getModType().getGuid());
      remEvent.setArtA(getRemoteBasicGuidArtifact(guidRel.getArtA()));
      remEvent.setArtB(getRemoteBasicGuidArtifact(guidRel.getArtB()));
      remEvent.setRationale(guidRel.getRationale());
      return remEvent;
   }

   public static RemoteBasicGuidArtifact1 getRemoteBasicGuidArtifact(DefaultBasicGuidArtifact guidArt) {
      RemoteBasicGuidArtifact1 event = new RemoteBasicGuidArtifact1();
      event.setBranchGuid(guidArt.getBranchGuid());
      event.setArtTypeGuid(guidArt.getArtTypeGuid());
      event.setArtGuid(guidArt.getGuid());
      return event;
   }

   public static RemoteBasicGuidRelationReorder1 getRemoteBasicGuidRelationReorder1(DefaultBasicGuidRelationReorder guidOrderRel) {
      RemoteBasicGuidRelationReorder1 event = new RemoteBasicGuidRelationReorder1();
      event.setBranchGuid(guidOrderRel.getBranchGuid());
      event.setRelTypeGuid(guidOrderRel.getRelTypeGuid());
      event.setModTypeGuid(guidOrderRel.getModType().getGuid());
      event.setParentArt(getRemoteBasicGuidArtifact(guidOrderRel.getParentArt()));
      return event;
   }

   public static EventBasicGuidRelation getEventBasicGuidRelation(RemoteBasicGuidRelation1 guidRel) {
      RelationEventType eventType = RelationEventType.getType(guidRel.getModTypeGuid());
      if (eventType == null) {
         OseeLog.log(Activator.class, Level.WARNING,
            "Can't determine RelationEventType from guid " + guidRel.getModTypeGuid());
      }
      EventBasicGuidRelation event =
         new EventBasicGuidRelation(eventType, guidRel.getBranchGuid(), guidRel.getRelTypeGuid(),
            guidRel.getRelationId(), guidRel.getGammaId(), guidRel.getArtAId(),
            getBasicGuidArtifact(guidRel.getArtA()), guidRel.getArtBId(), getBasicGuidArtifact(guidRel.getArtB()));
      if (eventType == RelationEventType.ModifiedRationale || eventType == RelationEventType.Added) {
         event.setRationale(guidRel.getRationale());
      }
      return event;
   }

   public static EventBasicGuidArtifact getEventBasicGuidArtifact(EventModType modType, RemoteBasicGuidArtifact1 remGuidArt) {
      return new EventBasicGuidArtifact(modType, remGuidArt.getBranchGuid(), remGuidArt.getArtTypeGuid(),
         remGuidArt.getArtGuid());
   }

   public static EventChangeTypeBasicGuidArtifact getEventChangeTypeBasicGuidArtifact(EventModType modType, RemoteBasicGuidArtifact1 remGuidArt) {
      return new EventChangeTypeBasicGuidArtifact(remGuidArt.getBranchGuid(), remGuidArt.getArtTypeGuid(),
         remGuidArt.getToArtTypeGuid(), remGuidArt.getArtGuid());
   }

   public static EventModifiedBasicGuidArtifact getEventModifiedBasicGuidArtifact(EventModType modType, RemoteBasicGuidArtifact1 remGuidArt) {
      List<AttributeChange> attributeChanges = new ArrayList<AttributeChange>();
      for (RemoteAttributeChange1 remAttrChg : remGuidArt.getAttributes()) {
         attributeChanges.add(getAttributeChange(remAttrChg));
      }
      return new EventModifiedBasicGuidArtifact(remGuidArt.getBranchGuid(), remGuidArt.getArtTypeGuid(),
         remGuidArt.getArtGuid(), attributeChanges);
   }

   public static DefaultBasicGuidArtifact getBasicGuidArtifact(RemoteBasicGuidArtifact1 remGuidArt) {
      return new DefaultBasicGuidArtifact(remGuidArt.getBranchGuid(), remGuidArt.getArtTypeGuid(),
         remGuidArt.getArtGuid());
   }

   public static RemoteBasicGuidArtifact1 getRemoteBasicGuidArtifact(String modTypeGuid, DefaultBasicGuidArtifact guidArt, Collection<AttributeChange> attributeChanges) {
      RemoteBasicGuidArtifact1 remoteGuidArt = new RemoteBasicGuidArtifact1();
      remoteGuidArt.setArtGuid(guidArt.getGuid());
      remoteGuidArt.setBranchGuid(guidArt.getBranchGuid());
      remoteGuidArt.setArtTypeGuid(guidArt.getArtTypeGuid());
      remoteGuidArt.setModTypeGuid(modTypeGuid);
      if (attributeChanges != null) {
         for (AttributeChange attrChg : attributeChanges) {
            remoteGuidArt.getAttributes().add(getRemoteAttributeChange(attrChg));
         }
      }
      return remoteGuidArt;
   }

   public static AttributeChange getAttributeChange(RemoteAttributeChange1 remAttrChg) {
      AttributeChange attrChg = new AttributeChange();
      attrChg.setAttributeId(remAttrChg.getAttributeId());
      attrChg.setGammaId(remAttrChg.getGammaId());
      attrChg.setAttrTypeGuid(remAttrChg.getAttrTypeGuid());
      attrChg.setModTypeGuid(remAttrChg.getModTypeGuid());
      for (String data : remAttrChg.getData()) {
         attrChg.getData().add(data);
      }
      return attrChg;
   }

   public static RemoteAttributeChange1 getRemoteAttributeChange(AttributeChange attrChg) {
      RemoteAttributeChange1 remAttrChg = new RemoteAttributeChange1();
      remAttrChg.setAttributeId(attrChg.getAttributeId());
      remAttrChg.setGammaId(attrChg.getGammaId());
      remAttrChg.setAttrTypeGuid(attrChg.getAttrTypeGuid());
      remAttrChg.setModTypeGuid(attrChg.getModTypeGuid());
      for (String data : attrChg.getData()) {
         remAttrChg.getData().add(data);
      }
      return remAttrChg;
   }

   public static NetworkSender getNetworkSender(RemoteNetworkSender1 remSender) {
      NetworkSender networkSender =
         new NetworkSender(remSender.getSourceObject(), remSender.getSessionId(), remSender.getMachineName(),
            remSender.getUserId(), remSender.getMachineIp(), remSender.getPort(), remSender.getClientVersion());
      return networkSender;
   }

   public static RemoteNetworkSender1 getRemoteNetworkSender(NetworkSender localSender) {
      RemoteNetworkSender1 networkSender = new RemoteNetworkSender1();
      networkSender.setSourceObject(String.valueOf(localSender.sourceObject));
      networkSender.setSessionId(localSender.sessionId);
      networkSender.setMachineName(localSender.machineName);
      networkSender.setUserId(localSender.userId);
      networkSender.setMachineIp(localSender.machineIp);
      networkSender.setPort(localSender.port);
      networkSender.setClientVersion(localSender.clientVersion);
      return networkSender;
   }
}
