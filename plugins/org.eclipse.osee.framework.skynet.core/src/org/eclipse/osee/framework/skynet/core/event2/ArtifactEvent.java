package org.eclipse.osee.framework.skynet.core.event2;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.osee.framework.skynet.core.event.msgs.NetworkSender;
import org.eclipse.osee.framework.skynet.core.event2.artifact.EventBasicGuidArtifact;
import org.eclipse.osee.framework.skynet.core.event2.artifact.EventBasicGuidRelation;

public class ArtifactEvent extends FrameworkEvent {

   private String branchGuid;
   private int transactionId;
   private List<EventBasicGuidArtifact> artifacts;
   private List<EventBasicGuidRelation> relations;
   private NetworkSender networkSender;

   public String getBranchGuid() {
      return branchGuid;
   }

   public void setBranchGuid(String value) {
      this.branchGuid = value;
   }

   public int getTransactionId() {
      return transactionId;
   }

   public void setTransactionId(int value) {
      this.transactionId = value;
   }

   public List<EventBasicGuidArtifact> getArtifacts() {
      if (artifacts == null) {
         artifacts = new ArrayList<EventBasicGuidArtifact>();
      }
      return this.artifacts;
   }

   public List<EventBasicGuidRelation> getRelations() {
      if (relations == null) {
         relations = new ArrayList<EventBasicGuidRelation>();
      }
      return this.relations;
   }

   public NetworkSender getNetworkSender() {
      return networkSender;
   }

   public void setNetworkSender(NetworkSender value) {
      this.networkSender = value;
   }

   public String toString() {
      try {
         return String.format("ArtifactEvent: BG[%s] TrId[%d] ARTS[%s] RELS[%s] Sender[%s]", branchGuid, transactionId,
               artifacts, relations, networkSender);
      } catch (Exception ex) {
         return String.format("ArtifactEvent exception: " + ex.getLocalizedMessage());
      }
   }
}
