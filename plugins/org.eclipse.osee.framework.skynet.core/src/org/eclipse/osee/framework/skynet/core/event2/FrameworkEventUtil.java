/*
 * Created on Apr 7, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.skynet.core.event2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import org.eclipse.osee.framework.core.data.DefaultBasicGuidArtifact;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.UserNotInDatabase;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.messaging.event.res.msgs.RemoteAttributeChange1;
import org.eclipse.osee.framework.messaging.event.res.msgs.RemoteBasicGuidArtifact1;
import org.eclipse.osee.framework.messaging.event.res.msgs.RemoteBasicGuidRelation1;
import org.eclipse.osee.framework.messaging.event.res.msgs.RemoteBranchEvent1;
import org.eclipse.osee.framework.messaging.event.res.msgs.RemoteBroadcastEvent1;
import org.eclipse.osee.framework.messaging.event.res.msgs.RemoteNetworkSender1;
import org.eclipse.osee.framework.messaging.event.res.msgs.RemoteTransactionEvent1;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.event.BranchEventType;
import org.eclipse.osee.framework.skynet.core.event.BroadcastEventType;
import org.eclipse.osee.framework.skynet.core.event.msgs.AttributeChange;
import org.eclipse.osee.framework.skynet.core.event.msgs.NetworkSender;
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

   public static boolean isEvent(Artifact artifact, Collection<EventBasicGuidArtifact> eventGuidArts, Collection<EventModType> eventModTypes) {
      for (EventBasicGuidArtifact guidArt : eventGuidArts) {
         if (eventModTypes.contains(guidArt.getModType())) {
            if (artifact.equals(guidArt)) {
               return true;
            }
         }
      }
      return false;
   }

   public static boolean isDeletedPurged(Artifact artifact, Collection<EventBasicGuidArtifact> eventGuidArts) {
      return FrameworkEventUtil.isEvent(artifact, eventGuidArts, Arrays.asList(EventModType.Deleted,
            EventModType.Purged));
   }

   public static boolean isModified(Artifact artifact, Collection<EventBasicGuidArtifact> eventGuidArts) {
      return FrameworkEventUtil.isEvent(artifact, eventGuidArts, Arrays.asList(EventModType.Modified));
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
            OseeLog.log(Activator.class, OseeLevel.SEVERE, ex);
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
               OseeLog.log(Activator.class, OseeLevel.SEVERE, ex);
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
      event.setModTypeGuid(branchEvent.getEventType().getGuid());
      event.setBranchGuid(branchEvent.getBranchGuid());
      event.setNetworkSender(getRemoteNetworkSender(branchEvent.getNetworkSender()));
      return event;
   }

   public static BranchEvent getBranchEvent(RemoteBranchEvent1 branchEvent) {
      BranchEventType branchEventType = BranchEventType.getByGuid(branchEvent.getModTypeGuid());
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
      return event;
   }

   public static TransactionEvent getTransactionEvent(RemoteTransactionEvent1 remEvent) {
      TransactionEvent event = new TransactionEvent();
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
      return event;
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
      DefaultBasicGuidArtifact guidArt = new DefaultBasicGuidArtifact();
      guidArt.setGuid(remGuidArt.getArtGuid());
      guidArt.setBranchGuid(remGuidArt.getBranchGuid());
      guidArt.setArtTypeGuid(remGuidArt.getArtTypeGuid());
      return guidArt;
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
      NetworkSender networkSender = new NetworkSender();
      networkSender.setSourceObject(remSender.getSourceObject());
      networkSender.setSessionId(remSender.getSessionId());
      networkSender.setMachineName(remSender.getMachineName());
      networkSender.setUserId(remSender.getUserId());
      networkSender.setMachineIp(remSender.getMachineIp());
      networkSender.setPort(remSender.getPort());
      networkSender.setClientVersion(remSender.getClientVersion());
      return networkSender;
   }

   public static RemoteNetworkSender1 getRemoteNetworkSender(NetworkSender localSender) {
      RemoteNetworkSender1 networkSender = new RemoteNetworkSender1();
      networkSender.setSourceObject(localSender.getSourceObject());
      networkSender.setSessionId(localSender.getSessionId());
      networkSender.setMachineName(localSender.getMachineName());
      networkSender.setUserId(localSender.getUserId());
      networkSender.setMachineIp(localSender.getMachineIp());
      networkSender.setPort(localSender.getPort());
      networkSender.setClientVersion(localSender.getClientVersion());
      return networkSender;
   }
}
