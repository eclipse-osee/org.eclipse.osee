/*********************************************************************
 * Copyright (c) 2010 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.framework.skynet.core.event;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import org.eclipse.osee.framework.core.OrcsTokenService;
import org.eclipse.osee.framework.core.data.ApplicabilityId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.ArtifactTypeId;
import org.eclipse.osee.framework.core.data.AttributeId;
import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.GammaId;
import org.eclipse.osee.framework.core.data.RelationId;
import org.eclipse.osee.framework.core.data.TransactionId;
import org.eclipse.osee.framework.core.data.TransactionToken;
import org.eclipse.osee.framework.core.enums.DeletionFlag;
import org.eclipse.osee.framework.core.enums.EventTopicTransferType;
import org.eclipse.osee.framework.core.event.NetworkSender;
import org.eclipse.osee.framework.core.event.TopicEvent;
import org.eclipse.osee.framework.core.model.event.DefaultBasicGuidArtifact;
import org.eclipse.osee.framework.core.model.event.DefaultBasicUuidRelationReorder;
import org.eclipse.osee.framework.core.model.event.RelationOrderModType;
import org.eclipse.osee.framework.core.util.JsonUtil;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.messaging.event.res.AttributeEventModificationType;
import org.eclipse.osee.framework.messaging.event.res.RemoteArtifactTopicEvent;
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
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.event.model.ArtifactEvent;
import org.eclipse.osee.framework.skynet.core.event.model.ArtifactEvent.ArtifactEventType;
import org.eclipse.osee.framework.skynet.core.event.model.ArtifactTopicEvent;
import org.eclipse.osee.framework.skynet.core.event.model.AttributeChange;
import org.eclipse.osee.framework.skynet.core.event.model.BranchEvent;
import org.eclipse.osee.framework.skynet.core.event.model.BranchEventType;
import org.eclipse.osee.framework.skynet.core.event.model.EventBasicGuidArtifact;
import org.eclipse.osee.framework.skynet.core.event.model.EventBasicGuidRelation;
import org.eclipse.osee.framework.skynet.core.event.model.EventChangeTypeBasicGuidArtifact;
import org.eclipse.osee.framework.skynet.core.event.model.EventModType;
import org.eclipse.osee.framework.skynet.core.event.model.EventModifiedBasicGuidArtifact;
import org.eclipse.osee.framework.skynet.core.event.model.EventTopicArtifactTransfer;
import org.eclipse.osee.framework.skynet.core.event.model.EventTopicAttributeChangeTransfer;
import org.eclipse.osee.framework.skynet.core.event.model.EventTopicRelationReorderTransfer;
import org.eclipse.osee.framework.skynet.core.event.model.EventTopicRelationTransfer;
import org.eclipse.osee.framework.skynet.core.event.model.TransactionChange;
import org.eclipse.osee.framework.skynet.core.event.model.TransactionEvent;
import org.eclipse.osee.framework.skynet.core.event.model.TransactionEventType;
import org.eclipse.osee.framework.skynet.core.internal.Activator;
import org.eclipse.osee.framework.skynet.core.internal.ServiceUtil;
import org.eclipse.osee.framework.skynet.core.relation.RelationEventType;

/**
 * @author Donald G. Dunne
 */
public final class FrameworkEventUtil {
   public static boolean USE_NEW_EVENTS = ArtifactToken.USE_LONG_IDS;

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
         remChange.setBranchGuid(change.getBranch().getId());
         remChange.setTransactionId(change.getTransactionId().getIdIntValue());
         List<RemoteBasicGuidArtifact1> remChangeArts = remChange.getArtifacts();
         for (DefaultBasicGuidArtifact guidArt : change.getArtifacts()) {
            remChangeArts.add(getRemoteBasicGuidArtifact(guidArt));
         }
         event.getTransactions().add(remChange);
      }
      return event;
   }

   public static TransactionEvent getTransactionEvent(RemoteTransactionEvent1 remEvent, OrcsTokenService tokenService) {
      TransactionEvent event = new TransactionEvent();
      event.setNetworkSender(getNetworkSender(remEvent.getNetworkSender()));
      event.setEventType(TransactionEventType.getByGuid(remEvent.getEventTypeGuid()));
      for (RemoteTransactionChange1 remChange : remEvent.getTransactions()) {
         TransactionChange change = new TransactionChange();
         change.setBranch(BranchId.valueOf(remChange.getBranchGuid()));
         change.setTransactionId(TransactionId.valueOf(remChange.getTransactionId()));
         Collection<DefaultBasicGuidArtifact> eventArts = change.getArtifacts();
         for (RemoteBasicGuidArtifact1 remGuidArt : remChange.getArtifacts()) {
            eventArts.add(getBasicGuidArtifact(remGuidArt, tokenService));
         }
         event.addTransactionChange(change);
      }
      return event;
   }

   public static RemoteArtifactTopicEvent getRemotePersistTopicEvent(ArtifactTopicEvent artifactTopicEvent) {
      RemoteArtifactTopicEvent event = new RemoteArtifactTopicEvent();
      event.setTopic(artifactTopicEvent.getTopic());
      event.setTransactionId(artifactTopicEvent.getTransaction().getId());
      event.setNetworkSender(getRemoteNetworkSender(artifactTopicEvent.getNetworkSender()));
      Map<String, String> properties = new HashMap<>();
      properties.put(RemoteArtifactTopicEvent.BRANCH_ID, JsonUtil.toJson(artifactTopicEvent.getBranch()));
      properties.put(RemoteArtifactTopicEvent.ARTIFACTS, JsonUtil.toJson(artifactTopicEvent.getTransferArtifacts()));
      properties.put(RemoteArtifactTopicEvent.RELATIONS, JsonUtil.toJson(artifactTopicEvent.getRelations()));
      properties.put(RemoteArtifactTopicEvent.RELATION_REORDER_RECORDS,
         JsonUtil.toJson(artifactTopicEvent.getRelationOrderRecords()));
      properties.put(RemoteArtifactTopicEvent.RELOAD_EVENT, JsonUtil.toJson(artifactTopicEvent.isReloadEvent()));
      event.setProperties(properties);
      return event;
   }

   public static ArtifactTopicEvent getPersistTopicEvent(RemoteArtifactTopicEvent remArtifactTopicEvent) {
      return getPersistTopicEvent(remArtifactTopicEvent, ServiceUtil.getOrcsTokenService());
   }

   public static ArtifactTopicEvent getPersistTopicEvent(RemoteArtifactTopicEvent remArtifactTopicEvent, OrcsTokenService tokenService) {
      Map<String, String> properties = remArtifactTopicEvent.getProperties();

      BranchId branchId = JsonUtil.readValue(properties.get(RemoteArtifactTopicEvent.BRANCH_ID), BranchId.class);
      Boolean isReloadEvent = JsonUtil.readValue(properties.get(RemoteArtifactTopicEvent.RELOAD_EVENT), Boolean.class);
      ArtifactEventType type = isReloadEvent ? ArtifactEventType.RELOAD_ARTIFACTS : ArtifactEventType.UPDATE_ARTIFACTS;
      ArtifactTopicEvent event = new ArtifactTopicEvent(branchId,
         TransactionToken.valueOf(remArtifactTopicEvent.getTransactionId(), branchId), type);
      event.setNetworkSender(getNetworkSender(remArtifactTopicEvent.getNetworkSender()));

      EventTopicArtifactTransfer[] artifacts =
         JsonUtil.readValue(properties.get(RemoteArtifactTopicEvent.ARTIFACTS), EventTopicArtifactTransfer[].class);
      for (EventTopicArtifactTransfer artifact : artifacts) {
         event.addArtifact(artifact);
      }

      EventTopicRelationTransfer[] relations =
         JsonUtil.readValue(properties.get(RemoteArtifactTopicEvent.RELATIONS), EventTopicRelationTransfer[].class);
      for (EventTopicRelationTransfer relation : relations) {
         event.addRelation(relation);
      }
      EventTopicRelationReorderTransfer[] relationReorderRecords = JsonUtil.readValue(
         properties.get(RemoteArtifactTopicEvent.RELATION_REORDER_RECORDS), EventTopicRelationReorderTransfer[].class);
      for (EventTopicRelationReorderTransfer relationReorder : relationReorderRecords) {
         event.addRelationReorder(relationReorder);
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

   public static ArtifactEvent getPersistEvent(RemotePersistEvent1 remEvent, OrcsTokenService tokenService) {
      ArtifactEvent event = new ArtifactEvent(remEvent.getTransaction());
      event.setNetworkSender(getNetworkSender(remEvent.getNetworkSender()));
      for (RemoteBasicGuidArtifact1 remGuidArt : remEvent.getArtifacts()) {
         EventModType modType = EventModType.getType(remGuidArt.getModTypeGuid());
         // This can happen if new events are added that old releases don't handle
         if (modType == null) {
            OseeLog.logf(Activator.class, Level.WARNING, "Unhandled remote artifact [%s]", remGuidArt);
         } else {
            if (modType == EventModType.Modified) {
               event.addArtifact(getEventModifiedBasicGuidArtifact(modType, remGuidArt, tokenService));
            } else if (modType == EventModType.ChangeType) {
               event.addArtifact(getEventChangeTypeBasicGuidArtifact(modType, remGuidArt, tokenService));
            } else {
               event.addArtifact(getEventBasicGuidArtifact(modType, remGuidArt, tokenService));
            }
         }
      }
      for (RemoteBasicGuidRelation1 guidRel : remEvent.getRelations()) {
         EventBasicGuidRelation relEvent = getEventBasicGuidRelation(guidRel, tokenService);
         // This can happen if new events are added that old releases don't handle
         if (relEvent == null) {
            OseeLog.logf(Activator.class, Level.WARNING, "Unhandled remote relation [%s]", guidRel);
         } else {
            event.getRelations().add(relEvent);
         }
      }
      for (RemoteBasicGuidRelationReorder1 guidReorder : remEvent.getRelationReorders()) {
         event.getRelationOrderRecords().add(getDefaultBasicGuidRelationReorder(guidReorder, tokenService));
      }
      return event;
   }

   public static DefaultBasicUuidRelationReorder getDefaultBasicGuidRelationReorder(RemoteBasicGuidRelationReorder1 guidRelOrder, OrcsTokenService tokenService) {
      DefaultBasicUuidRelationReorder guidArt = new DefaultBasicUuidRelationReorder(
         RelationOrderModType.getType(guidRelOrder.getModTypeGuid()), BranchId.valueOf(guidRelOrder.getBranchGuid()),
         guidRelOrder.getRelTypeGuid(), getBasicGuidArtifact(guidRelOrder.getParentArt(), tokenService));
      return guidArt;
   }

   public static RemoteBasicGuidRelation1 getRemoteBasicGuidRelation1(EventBasicGuidRelation guidRel) {
      RemoteBasicGuidRelation1 remEvent = new RemoteBasicGuidRelation1();
      remEvent.setGammaId(guidRel.getGammaId().getIdIntValue());
      remEvent.setBranchGuid(guidRel.getBranch());
      remEvent.setRelTypeGuid(guidRel.getRelTypeGuid());
      remEvent.setRelationId(guidRel.getRelationId().intValue());
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

   public static EventBasicGuidRelation getEventBasicGuidRelation(RemoteBasicGuidRelation1 guidRel, OrcsTokenService tokenService) {
      RelationEventType eventType = RelationEventType.getType(guidRel.getModTypeGuid());
      if (eventType == null) {
         OseeLog.log(Activator.class, Level.WARNING,
            "Can't determine RelationEventType from guid " + guidRel.getModTypeGuid());
      }
      EventBasicGuidRelation event = new EventBasicGuidRelation(eventType, guidRel.getBranch(),
         guidRel.getRelTypeGuid(), Long.valueOf(guidRel.getRelationId()), GammaId.valueOf(guidRel.getGammaId()),
         guidRel.getArtAId(), getBasicGuidArtifact(guidRel.getArtA(), tokenService), guidRel.getArtBId(),
         getBasicGuidArtifact(guidRel.getArtB(), tokenService));
      if (eventType == RelationEventType.Added) {
         event.setRationale(guidRel.getRationale());
         event.setRelOrder(guidRel.getRelOrder());
         event.setRelArtId(guidRel.getRelArtId());
      }
      if (eventType == RelationEventType.ModifiedRationale || eventType == RelationEventType.Added) {
         event.setRationale(guidRel.getRationale());
      }
      if (eventType == RelationEventType.ModifiedOrder) {
         event.setRelOrder(guidRel.getRelOrder());
      }
      if (eventType == RelationEventType.ModifiedRelatedArtifact) {
         event.setRelArtId(guidRel.getRelArtId());
      }
      return event;
   }

   public static EventBasicGuidArtifact getEventBasicGuidArtifact(EventModType modType, RemoteBasicGuidArtifact1 remGuidArt, OrcsTokenService tokenService) {
      return new EventBasicGuidArtifact(modType, remGuidArt.getBranch(),
         tokenService.getArtifactType(remGuidArt.getArtTypeGuid()), remGuidArt.getArtGuid());
   }

   public static EventChangeTypeBasicGuidArtifact getEventChangeTypeBasicGuidArtifact(EventModType modType, RemoteBasicGuidArtifact1 remGuidArt, OrcsTokenService tokenService) {
      return new EventChangeTypeBasicGuidArtifact(remGuidArt.getBranch(), remGuidArt.getArtifactType(),
         tokenService.getArtifactType(remGuidArt.getToArtTypeGuid()), remGuidArt.getArtGuid());
   }

   public static EventModifiedBasicGuidArtifact getEventModifiedBasicGuidArtifact(EventModType modType, RemoteBasicGuidArtifact1 remGuidArt, OrcsTokenService tokenService) {
      List<AttributeChange> attributeChanges = new ArrayList<>();
      for (RemoteAttributeChange1 remAttrChg : remGuidArt.getAttributes()) {
         attributeChanges.add(getAttributeChange(remAttrChg));
      }
      return new EventModifiedBasicGuidArtifact(remGuidArt.getBranch(),
         tokenService.getArtifactType(remGuidArt.getArtTypeGuid()), remGuidArt.getArtGuid(), attributeChanges);
   }

   private static DefaultBasicGuidArtifact getBasicGuidArtifact(RemoteBasicGuidArtifact1 remGuidArt, OrcsTokenService tokenService) {
      return new DefaultBasicGuidArtifact(remGuidArt.getBranch(),
         tokenService.getArtifactType(remGuidArt.getArtTypeGuid()), remGuidArt.getArtGuid());
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
      attrChg.setAttributeId(AttributeId.valueOf(remAttrChg.getAttributeId()));
      attrChg.setGammaId(GammaId.valueOf(remAttrChg.getGammaId()));
      attrChg.setAttrTypeGuid(remAttrChg.getAttrTypeGuid());
      attrChg.setModTypeGuid(remAttrChg.getModTypeGuid());
      for (Object data : remAttrChg.getData()) {
         attrChg.getData().add(data);
      }
      return attrChg;
   }

   public static RemoteAttributeChange1 getRemoteAttributeChange(AttributeChange attrChg) {
      RemoteAttributeChange1 remAttrChg = new RemoteAttributeChange1();
      remAttrChg.setAttributeId(attrChg.getAttributeId().getIdIntValue());
      remAttrChg.setGammaId(attrChg.getGammaId().getIdIntValue());
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

   public static EventTopicArtifactTransfer artifactTransferFactory(BranchId branch, ArtifactToken artifactToken, ArtifactTypeId artifactTypeId, EventModType eventModType, ArtifactTypeId fromArtType, Collection<EventTopicAttributeChangeTransfer> arrayList, EventTopicTransferType transferType) {
      EventTopicArtifactTransfer artifactTransfer = new EventTopicArtifactTransfer();
      artifactTransfer.setBranch(branch);
      artifactTransfer.setArtifactId(artifactToken);
      artifactTransfer.setArtifactTypeId(artifactTypeId);
      artifactTransfer.setEventModType(eventModType);
      artifactTransfer.setFromArtTypeGuid(fromArtType);
      artifactTransfer.setAttributeChanges(arrayList);
      artifactTransfer.setTransferType(transferType);
      return artifactTransfer;
   }

   public static EventTopicRelationTransfer relationTransferFactory(RelationEventType relationEventType, Artifact artA, Artifact artB, RelationId relationId, Long relTypeId, GammaId relationGammaId, String rationale) {
      EventTopicRelationTransfer transfer = new EventTopicRelationTransfer();
      transfer.setArtAId(artA);
      transfer.setArtAIdType(artA.getArtifactType());
      transfer.setArtBId(artB);
      transfer.setArtBIdType(artB.getArtifactType());
      transfer.setBranch(artA.getBranch());
      transfer.setGammaId(relationGammaId);
      transfer.setRelationEventType(relationEventType);
      transfer.setRelationId(relationId);
      transfer.setRelTypeId(relTypeId);
      transfer.setRationale(rationale);
      return transfer;
   }

   public static EventTopicRelationReorderTransfer relationReorderTransferFactory(EventTopicArtifactTransfer parentArt, BranchId branch, Long relTypeUuid, RelationOrderModType modType) {
      EventTopicRelationReorderTransfer transfer = new EventTopicRelationReorderTransfer();
      transfer.setParentArt(parentArt);
      transfer.setBranch(branch);
      transfer.setRelTypeUuid(relTypeUuid);
      transfer.setModType(modType);
      return transfer;
   }

   public static EventTopicAttributeChangeTransfer attributeChangeTransferFactory(AttributeTypeId attrTypeId, Long modType, AttributeId attrId, GammaId gammaId, List<Object> data, ApplicabilityId applicabilityId) {
      EventTopicAttributeChangeTransfer attrChange = new EventTopicAttributeChangeTransfer();
      attrChange.setAttrTypeId(attrTypeId);
      attrChange.setModType(modType);
      attrChange.setAttrId(attrId);
      attrChange.setGammaId(gammaId);
      attrChange.setData(data);
      attrChange.setApplicabilityId(applicabilityId);
      return attrChange;
   }

   public static EventTopicArtifactTransfer defaultGuidArtifactToTransfer(DefaultBasicGuidArtifact guidArt, EventModType eventModType) {
      EventTopicArtifactTransfer artifactTransfer = artifactTransferFactory(guidArt.getBranch(),
         ArtifactQuery.getArtifactFromId(guidArt.getGuid(), guidArt.getBranch(), DeletionFlag.allowDeleted(true)),
         guidArt.getArtifactType(), eventModType, null, null, EventTopicTransferType.BASE);
      return artifactTransfer;
   }

   public static EventTopicArtifactTransfer eventGuidArtifactToTransfer(EventBasicGuidArtifact guidArt) {
      EventTopicArtifactTransfer artifactTransfer = new EventTopicArtifactTransfer();
      artifactTransfer.setBranch(guidArt.getBranch());
      artifactTransfer.setArtifactId(ArtifactQuery.getArtifactFromId(guidArt.getGuid(), guidArt.getBranch()));
      artifactTransfer.setEventModType(guidArt.getModType());
      return artifactTransfer;
   }

   public static EventBasicGuidArtifact eventTransferArtifactToGuid(EventTopicArtifactTransfer transferArt) {
      EventBasicGuidArtifact guidArt = new EventBasicGuidArtifact(transferArt.getEventModType(),
         transferArt.getBranch(), transferArt.getArtifactToken().getArtifactType());
      return guidArt;
   }

   public static EventTopicRelationReorderTransfer relationReorderBasicToTransfer(DefaultBasicUuidRelationReorder basicRelationReorder, EventModType eventModType) {
      EventTopicRelationReorderTransfer transfer = relationReorderTransferFactory(
         defaultGuidArtifactToTransfer(basicRelationReorder.getParentArt(), eventModType),
         basicRelationReorder.getBranch(), basicRelationReorder.getRelTypeGuid(), basicRelationReorder.getModType());
      return transfer;
   }

   public static EventTopicAttributeChangeTransfer attributeChangeToTransfer(AttributeChange attrChange) {
      EventTopicAttributeChangeTransfer attrChangeTransfer = attributeChangeTransferFactory(
         AttributeTypeId.valueOf(attrChange.getAttrTypeGuid()),
         AttributeEventModificationType.getType(attrChange.getModTypeGuid()).getModificationType().getId(),
         attrChange.getAttributeId(), attrChange.getGammaId(), attrChange.getData(), attrChange.getApplicabilityId());
      return attrChangeTransfer;

   }
}
