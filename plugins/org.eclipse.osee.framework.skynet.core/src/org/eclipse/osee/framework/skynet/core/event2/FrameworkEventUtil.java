/*
 * Created on Apr 7, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.skynet.core.event2;

import org.eclipse.osee.framework.core.data.DefaultBasicGuidArtifact;
import org.eclipse.osee.framework.messaging.event.res.msgs.RemoteAttributeChange1;
import org.eclipse.osee.framework.messaging.event.res.msgs.RemoteBasicGuidArtifact1;
import org.eclipse.osee.framework.messaging.event.res.msgs.RemoteBasicModifiedGuidArtifact1;
import org.eclipse.osee.framework.messaging.event.res.msgs.RemoteNetworkSender1;
import org.eclipse.osee.framework.messaging.event.res.msgs.RemoteTransactionEvent1;
import org.eclipse.osee.framework.skynet.core.event.msgs.AttributeChange;
import org.eclipse.osee.framework.skynet.core.event.msgs.BasicModifiedGuidArtifact;
import org.eclipse.osee.framework.skynet.core.event.msgs.NetworkSender;
import org.eclipse.osee.framework.skynet.core.event.msgs.TransactionEvent;

/**
 * @author Donald G. Dunne
 */
public class FrameworkEventUtil {

   public static RemoteTransactionEvent1 getRemoteTransactionEvent(TransactionEvent transEvent) {
      RemoteTransactionEvent1 event = new RemoteTransactionEvent1();
      event.setNetworkSender(getRemoteNetworkSender(transEvent.getNetworkSender()));
      event.setBranchGuid(transEvent.getBranchGuid());
      event.setTransactionId(transEvent.getTransactionId());
      for (DefaultBasicGuidArtifact guidArt : transEvent.getAdded()) {
         event.getAdded().add(getRemoteBasicGuidArtifact(guidArt));
      }
      for (DefaultBasicGuidArtifact guidArt : transEvent.getDeleted()) {
         event.getDeleted().add(getRemoteBasicGuidArtifact(guidArt));
      }
      for (BasicModifiedGuidArtifact guidArt : transEvent.getModified()) {
         event.getModified().add(getBasicModifiedGuidArtifact(guidArt));
      }
      return event;
   }

   public static TransactionEvent getTransactionEvent(RemoteTransactionEvent1 remEvent) {
      TransactionEvent event = new TransactionEvent();
      event.setNetworkSender(getNetworkSender(remEvent.getNetworkSender()));
      event.setBranchGuid(remEvent.getBranchGuid());
      event.setTransactionId(remEvent.getTransactionId());
      for (RemoteBasicGuidArtifact1 guidArt : remEvent.getAdded()) {
         event.getAdded().add(getBasicGuidArtifact(guidArt));
      }
      for (RemoteBasicGuidArtifact1 guidArt : remEvent.getDeleted()) {
         event.getDeleted().add(getBasicGuidArtifact(guidArt));
      }
      for (RemoteBasicModifiedGuidArtifact1 guidArt : remEvent.getModified()) {
         event.getModified().add(getBasicModifiedGuidArtifact(guidArt));
      }
      return event;
   }

   public static DefaultBasicGuidArtifact getBasicGuidArtifact(RemoteBasicGuidArtifact1 remGuidArt) {
      DefaultBasicGuidArtifact guidArt = new DefaultBasicGuidArtifact();
      guidArt.setGuid(remGuidArt.getArtGuid());
      guidArt.setBranchGuid(remGuidArt.getBranchGuid());
      guidArt.setArtTypeGuid(remGuidArt.getArtTypeGuid());
      return guidArt;
   }

   public static RemoteBasicGuidArtifact1 getRemoteBasicGuidArtifact(DefaultBasicGuidArtifact guidArt) {
      RemoteBasicGuidArtifact1 remoteGuidArt = new RemoteBasicGuidArtifact1();
      remoteGuidArt.setArtGuid(guidArt.getGuid());
      remoteGuidArt.setBranchGuid(guidArt.getBranchGuid());
      remoteGuidArt.setArtTypeGuid(guidArt.getArtTypeGuid());
      return remoteGuidArt;
   }

   public static BasicModifiedGuidArtifact getBasicModifiedGuidArtifact(RemoteBasicModifiedGuidArtifact1 remGuidArt) {
      BasicModifiedGuidArtifact guidArt = new BasicModifiedGuidArtifact();
      guidArt.setArtGuid(remGuidArt.getArtGuid());
      guidArt.setBranchGuid(remGuidArt.getBranchGuid());
      guidArt.setArtTypeGuid(remGuidArt.getArtTypeGuid());
      for (RemoteAttributeChange1 remAttrChg : remGuidArt.getAttributes()) {
         guidArt.getAttributes().add(getAttributeChange(remAttrChg));
      }
      return guidArt;
   }

   public static RemoteBasicModifiedGuidArtifact1 getBasicModifiedGuidArtifact(BasicModifiedGuidArtifact guidArt) {
      RemoteBasicModifiedGuidArtifact1 remoteGuidArt = new RemoteBasicModifiedGuidArtifact1();
      remoteGuidArt.setArtGuid(guidArt.getArtGuid());
      remoteGuidArt.setBranchGuid(guidArt.getBranchGuid());
      remoteGuidArt.setArtTypeGuid(guidArt.getArtTypeGuid());
      for (AttributeChange attrChg : guidArt.getAttributes()) {
         remoteGuidArt.getAttributes().add(getRemoteAttributeChange(attrChg));
      }
      return remoteGuidArt;
   }

   public static AttributeChange getAttributeChange(RemoteAttributeChange1 remAttrChg) {
      AttributeChange attrChg = new AttributeChange();
      attrChg.setAttributeId(remAttrChg.getAttributeId());
      attrChg.setGammaId(remAttrChg.getGammaId());
      attrChg.setAttrTypeGuid(remAttrChg.getAttrTypeGuid());
      attrChg.setModTypeGuid(remAttrChg.getModTypeGuid());
      attrChg.setIs(remAttrChg.getIs());
      attrChg.setWas(remAttrChg.getWas());
      return attrChg;
   }

   public static RemoteAttributeChange1 getRemoteAttributeChange(AttributeChange attrChg) {
      RemoteAttributeChange1 remAttrChg = new RemoteAttributeChange1();
      remAttrChg.setAttributeId(attrChg.getAttributeId());
      remAttrChg.setGammaId(attrChg.getGammaId());
      remAttrChg.setAttrTypeGuid(attrChg.getAttrTypeGuid());
      remAttrChg.setModTypeGuid(attrChg.getModTypeGuid());
      remAttrChg.setIs(attrChg.getIs());
      remAttrChg.setWas(attrChg.getWas());
      return remAttrChg;
   }

   public static NetworkSender getNetworkSender(RemoteNetworkSender1 remSender) {
      NetworkSender networkSender = new NetworkSender();
      networkSender.setSourceObject(remSender.getSourceObject());
      networkSender.setSourceObject(remSender.getSessionId());
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
      networkSender.setSourceObject(localSender.getSessionId());
      networkSender.setMachineName(localSender.getMachineName());
      networkSender.setUserId(localSender.getUserId());
      networkSender.setMachineIp(localSender.getMachineIp());
      networkSender.setPort(localSender.getPort());
      networkSender.setClientVersion(localSender.getClientVersion());
      return networkSender;
   }
}
