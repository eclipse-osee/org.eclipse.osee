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
package org.eclipse.osee.framework.skynet.core.event;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.model.event.DefaultBasicGuidArtifact;
import org.eclipse.osee.framework.core.model.event.DefaultBasicUuidRelationReorder;
import org.eclipse.osee.framework.core.model.event.RelationOrderModType;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.messaging.event.res.RemoteEvent;
import org.eclipse.osee.framework.messaging.event.res.RemoteTopicEvent1;
import org.eclipse.osee.framework.messaging.event.res.msgs.RemoteAttributeChange1;
import org.eclipse.osee.framework.messaging.event.res.msgs.RemoteBasicGuidArtifact1;
import org.eclipse.osee.framework.messaging.event.res.msgs.RemoteBasicGuidRelation1;
import org.eclipse.osee.framework.messaging.event.res.msgs.RemoteBasicGuidRelationReorder1;
import org.eclipse.osee.framework.messaging.event.res.msgs.RemoteBranchEvent1;
import org.eclipse.osee.framework.messaging.event.res.msgs.RemoteNetworkSender1;
import org.eclipse.osee.framework.messaging.event.res.msgs.RemotePersistEvent1;
import org.eclipse.osee.framework.messaging.event.res.msgs.RemoteTransactionChange1;
import org.eclipse.osee.framework.messaging.event.res.msgs.RemoteTransactionEvent1;
import org.eclipse.osee.framework.skynet.core.event.model.ArtifactEvent;
import org.eclipse.osee.framework.skynet.core.event.model.AttributeChange;
import org.eclipse.osee.framework.skynet.core.event.model.BranchEvent;
import org.eclipse.osee.framework.skynet.core.event.model.BranchEventType;
import org.eclipse.osee.framework.skynet.core.event.model.EventBasicGuidArtifact;
import org.eclipse.osee.framework.skynet.core.event.model.EventBasicGuidRelation;
import org.eclipse.osee.framework.skynet.core.event.model.EventChangeTypeBasicGuidArtifact;
import org.eclipse.osee.framework.skynet.core.event.model.EventModType;
import org.eclipse.osee.framework.skynet.core.event.model.EventModifiedBasicGuidArtifact;
import org.eclipse.osee.framework.skynet.core.event.model.NetworkSender;
import org.eclipse.osee.framework.skynet.core.event.model.TopicEvent;
import org.eclipse.osee.framework.skynet.core.event.model.TransactionChange;
import org.eclipse.osee.framework.skynet.core.event.model.TransactionEvent;
import org.eclipse.osee.framework.skynet.core.event.model.TransactionEventType;
import org.eclipse.osee.framework.skynet.core.internal.Activator;
import org.eclipse.osee.framework.skynet.core.relation.RelationEventType;

/**
 * @author Donald G. Dunne
 */
public final class FrameworkEventUtil {

   private FrameworkEventUtil() {
      // Utility Class
   }

   public static RemoteBranchEvent1 getRemoteBranchEvent(BranchEvent branchEvent) {
      RemoteBranchEvent1 event = new RemoteBranchEvent1();
      event.setEventTypeGuid(branchEvent.getEventType().getGuid());
      event.setBranch(branchEvent.getSourceBranch());
      event.setDestinationBranch(branchEvent.getDestinationBranch());
      event.setNetworkSender(getRemoteNetworkSender(branchEvent.getNetworkSender()));
      return event;
   }

   public static BranchEvent getBranchEvent(RemoteBranchEvent1 branchEvent) {
      BranchEventType branchEventType = BranchEventType.getByGuid(branchEvent.getEventTypeGuid());
      if (branchEventType != null) {
         BranchEvent event =
            new BranchEvent(branchEventType, branchEvent.getBranch(), branchEvent.getDestinationBranch());
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
      for (TransactionChange change : transEvent.getTransactionChanges()) {
         RemoteTransactionChange1 remChange = new RemoteTransactionChange1();
         remChange.setBranchGuid(change.getBranchUuid());
         remChange.setTransactionId(change.getTransactionId());
         List<RemoteBasicGuidArtifact1> remChangeArts = remChange.getArtifacts();
         for (DefaultBasicGuidArtifact guidArt : change.getArtifacts()) {
            remChangeArts.add(getRemoteBasicGuidArtifact(guidArt));
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
         change.setBranchUuid(getBranchUuidFromRemoteEvent(remChange.getBranchGuid()));
         change.setTransactionId(remChange.getTransactionId());
         Collection<DefaultBasicGuidArtifact> eventArts = change.getArtifacts();
         for (RemoteBasicGuidArtifact1 remGuidArt : remChange.getArtifacts()) {
            eventArts.add(getBasicGuidArtifact(remGuidArt));
         }
         event.addTransactionChange(change);
      }
      return event;
   }

   public static RemotePersistEvent1 getRemotePersistEvent(ArtifactEvent transEvent) {
      RemotePersistEvent1 event = new RemotePersistEvent1();
      event.setNetworkSender(getRemoteNetworkSender(transEvent.getNetworkSender()));
      event.setBranchGuid(transEvent.getBranch());
      event.setTransaction(transEvent.getTransactionId());
      for (EventBasicGuidArtifact guidArt : transEvent.getArtifacts()) {
         if (guidArt.getModType() == EventModType.Modified) {
            event.getArtifacts().add(getRemoteBasicGuidArtifact(guidArt.getModType().getGuid(), guidArt,
               ((EventModifiedBasicGuidArtifact) guidArt).getAttributeChanges()));
         } else if (guidArt.getModType() == EventModType.ChangeType) {
            EventChangeTypeBasicGuidArtifact changeGuidArt = (EventChangeTypeBasicGuidArtifact) guidArt;
            RemoteBasicGuidArtifact1 remGuidArt =
               getRemoteBasicGuidArtifact(guidArt.getModType().getGuid(), guidArt, null);
            remGuidArt.setArtifactType(changeGuidArt.getFromArtTypeGuid());
            remGuidArt.setToArtifactType(changeGuidArt.getArtifactType());
            event.getArtifacts().add(remGuidArt);
         } else {
            event.getArtifacts().add(getRemoteBasicGuidArtifact(guidArt.getModType().getGuid(), guidArt, null));
         }
      }
      for (EventBasicGuidRelation guidRel : transEvent.getRelations()) {
         event.getRelations().add(getRemoteBasicGuidRelation1(guidRel));
      }
      for (DefaultBasicUuidRelationReorder guidOrderRel : transEvent.getRelationOrderRecords()) {
         event.getRelationReorders().add(getRemoteBasicGuidRelationReorder1(guidOrderRel));
      }
      return event;
   }

   public static ArtifactEvent getPersistEvent(RemotePersistEvent1 remEvent) {
      ArtifactEvent event = new ArtifactEvent(remEvent.getTransaction());
      event.setNetworkSender(getNetworkSender(remEvent.getNetworkSender()));
      for (RemoteBasicGuidArtifact1 remGuidArt : remEvent.getArtifacts()) {
         EventModType modType = EventModType.getType(remGuidArt.getModTypeGuid());
         // This can happen if new events are added that old releases don't handle
         if (modType == null) {
            OseeLog.logf(Activator.class, Level.WARNING, "Unhandled remote artifact [%s]", remGuidArt);
         } else {
            if (modType == EventModType.Modified) {
               event.addArtifact(getEventModifiedBasicGuidArtifact(modType, remGuidArt));
            } else if (modType == EventModType.ChangeType) {
               event.addArtifact(getEventChangeTypeBasicGuidArtifact(modType, remGuidArt));
            } else {
               event.addArtifact(getEventBasicGuidArtifact(modType, remGuidArt));
            }
         }
      }
      for (RemoteBasicGuidRelation1 guidRel : remEvent.getRelations()) {
         EventBasicGuidRelation relEvent = getEventBasicGuidRelation(guidRel);
         // This can happen if new events are added that old releases don't handle
         if (relEvent == null) {
            OseeLog.logf(Activator.class, Level.WARNING, "Unhandled remote relation [%s]", guidRel);
         } else {
            event.getRelations().add(relEvent);
         }
      }
      for (RemoteBasicGuidRelationReorder1 guidReorder : remEvent.getRelationReorders()) {
         event.getRelationOrderRecords().add(getDefaultBasicGuidRelationReorder(guidReorder));
      }
      return event;
   }

   public static DefaultBasicUuidRelationReorder getDefaultBasicGuidRelationReorder(RemoteBasicGuidRelationReorder1 guidRelOrder) {
      DefaultBasicUuidRelationReorder guidArt = new DefaultBasicUuidRelationReorder(
         RelationOrderModType.getType(guidRelOrder.getModTypeGuid()), BranchId.valueOf(guidRelOrder.getBranchGuid()),
         guidRelOrder.getRelTypeGuid(), getBasicGuidArtifact(guidRelOrder.getParentArt()));
      return guidArt;
   }

   public static RemoteBasicGuidRelation1 getRemoteBasicGuidRelation1(EventBasicGuidRelation guidRel) {
      RemoteBasicGuidRelation1 remEvent = new RemoteBasicGuidRelation1();
      remEvent.setGammaId(guidRel.getGammaId());
      remEvent.setBranchGuid(guidRel.getBranch());
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
      event.setBranch(guidArt.getBranch());
      event.setArtifactType(guidArt.getArtifactType());
      event.setArtGuid(guidArt.getGuid());
      return event;
   }

   public static RemoteBasicGuidRelationReorder1 getRemoteBasicGuidRelationReorder1(DefaultBasicUuidRelationReorder guidOrderRel) {
      RemoteBasicGuidRelationReorder1 event = new RemoteBasicGuidRelationReorder1();
      event.setBranchGuid(guidOrderRel.getBranch());
      event.setRelTypeGuid(guidOrderRel.getRelTypeGuid());
      event.setModTypeGuid(guidOrderRel.getModType().getGuid());
      event.setParentArt(getRemoteBasicGuidArtifact(guidOrderRel.getParentArt()));
      return event;
   }

   /**
    * Before 0.17.0, events pass string branch guid for events. After 0.17.0, events will pass long branch uuid for
    * events.
    */
   public static Long getBranchUuidFromRemoteEvent(String remoteBranchId) {
      Long id = 0L;
      if (Strings.isNumeric(remoteBranchId)) {
         id = Long.valueOf(remoteBranchId);
      }
      return id;
   }

   public static EventBasicGuidRelation getEventBasicGuidRelation(RemoteBasicGuidRelation1 guidRel) {
      RelationEventType eventType = RelationEventType.getType(guidRel.getModTypeGuid());
      if (eventType == null) {
         OseeLog.log(Activator.class, Level.WARNING,
            "Can't determine RelationEventType from guid " + guidRel.getModTypeGuid());
      }
      EventBasicGuidRelation event = new EventBasicGuidRelation(eventType, guidRel.getBranch(),
         guidRel.getRelTypeGuid(), guidRel.getRelationId(), guidRel.getGammaId(), guidRel.getArtAId(),
         getBasicGuidArtifact(guidRel.getArtA()), guidRel.getArtBId(), getBasicGuidArtifact(guidRel.getArtB()));
      if (eventType == RelationEventType.ModifiedRationale || eventType == RelationEventType.Added) {
         event.setRationale(guidRel.getRationale());
      }
      return event;
   }

   public static EventBasicGuidArtifact getEventBasicGuidArtifact(EventModType modType, RemoteBasicGuidArtifact1 remGuidArt) {
      return new EventBasicGuidArtifact(modType, remGuidArt.getBranch(), remGuidArt.getArtifactType(),
         remGuidArt.getArtGuid());
   }

   public static EventChangeTypeBasicGuidArtifact getEventChangeTypeBasicGuidArtifact(EventModType modType, RemoteBasicGuidArtifact1 remGuidArt) {
      return new EventChangeTypeBasicGuidArtifact(remGuidArt.getBranch(), remGuidArt.getArtifactType(),
         remGuidArt.getToArtifactType(), remGuidArt.getArtGuid());
   }

   public static EventModifiedBasicGuidArtifact getEventModifiedBasicGuidArtifact(EventModType modType, RemoteBasicGuidArtifact1 remGuidArt) {
      List<AttributeChange> attributeChanges = new ArrayList<>();
      for (RemoteAttributeChange1 remAttrChg : remGuidArt.getAttributes()) {
         attributeChanges.add(getAttributeChange(remAttrChg));
      }
      return new EventModifiedBasicGuidArtifact(remGuidArt.getBranch(), remGuidArt.getArtifactType(),
         remGuidArt.getArtGuid(), attributeChanges);
   }

   public static DefaultBasicGuidArtifact getBasicGuidArtifact(RemoteBasicGuidArtifact1 remGuidArt) {
      return new DefaultBasicGuidArtifact(remGuidArt.getBranch(), remGuidArt.getArtifactType(),
         remGuidArt.getArtGuid());
   }

   public static RemoteBasicGuidArtifact1 getRemoteBasicGuidArtifact(String modTypeGuid, DefaultBasicGuidArtifact guidArt, Collection<AttributeChange> attributeChanges) {
      RemoteBasicGuidArtifact1 remoteGuidArt = new RemoteBasicGuidArtifact1();
      remoteGuidArt.setArtGuid(guidArt.getGuid());
      remoteGuidArt.setBranch(guidArt.getBranch());
      remoteGuidArt.setArtifactType(guidArt.getArtifactType());
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
      for (Object data : remAttrChg.getData()) {
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
      for (Object data : attrChg.getData()) {
         remAttrChg.getData().add(data.toString());
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

   public static RemoteEvent getRemoteTopicEvent(TopicEvent event) {
      RemoteTopicEvent1 remEvent = new RemoteTopicEvent1();
      remEvent.setNetworkSender(getRemoteNetworkSender(event.getNetworkSender()));
      remEvent.getProperties().putAll(event.getProperties());
      remEvent.setTopic(event.getTopic());
      return remEvent;
   }

   public static TopicEvent getTopicEvent(RemoteTopicEvent1 remoteEvent) {
      TopicEvent event = new TopicEvent(remoteEvent.getTopic());
      event.getProperties().putAll(remoteEvent.getProperties());
      event.setNetworkSender(getNetworkSender(remoteEvent.getNetworkSender()));
      return event;
   }
}
