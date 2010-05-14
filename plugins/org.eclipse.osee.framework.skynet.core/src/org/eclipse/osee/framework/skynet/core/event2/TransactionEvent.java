package org.eclipse.osee.framework.skynet.core.event2;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.osee.framework.core.data.DefaultBasicGuidArtifact;
import org.eclipse.osee.framework.skynet.core.event.msgs.NetworkSender;
import org.eclipse.osee.framework.skynet.core.event2.artifact.EventBasicGuidArtifact;
import org.eclipse.osee.framework.skynet.core.event2.artifact.EventBasicGuidRelation;

public class TransactionEvent extends FrameworkEvent {

   private String branchGuid;
   private int transactionId;
   private List<EventBasicGuidArtifact> artifacts;
   private List<EventBasicGuidRelation> relations;
   private NetworkSender networkSender;

   /**
    * Gets the value of the branchGuid property.
    * 
    * @return possible object is {@link String }
    */
   public String getBranchGuid() {
      return branchGuid;
   }

   /**
    * Sets the value of the branchGuid property.
    * 
    * @param value allowed object is {@link String }
    */
   public void setBranchGuid(String value) {
      this.branchGuid = value;
   }

   /**
    * Gets the value of the transactionId property.
    */
   public int getTransactionId() {
      return transactionId;
   }

   /**
    * Sets the value of the transactionId property.
    */
   public void setTransactionId(int value) {
      this.transactionId = value;
   }

   /**
    * Gets the value of the artifacts property.
    * <p>
    * This accessor method returns a reference to the live list, not a snapshot. Therefore any modification you make to
    * the returned list will be present inside the JAXB object. This is why there is not a <CODE>set</CODE> method for
    * the artifacts property.
    * <p>
    * For example, to add a new item, do as follows:
    * 
    * <pre>
    * getArtifacts().add(newItem);
    * </pre>
    * <p>
    * Objects of the following type(s) are allowed in the list {@link DefaultBasicGuidArtifact }
    */
   public List<EventBasicGuidArtifact> getArtifacts() {
      if (artifacts == null) {
         artifacts = new ArrayList<EventBasicGuidArtifact>();
      }
      return this.artifacts;
   }

   /**
    * Gets the value of the relations property.
    * <p>
    * This accessor method returns a reference to the live list, not a snapshot. Therefore any modification you make to
    * the returned list will be present inside the JAXB object. This is why there is not a <CODE>set</CODE> method for
    * the relations property.
    * <p>
    * For example, to add a new item, do as follows:
    * 
    * <pre>
    * getRelations().add(newItem);
    * </pre>
    * <p>
    * Objects of the following type(s) are allowed in the list {@link EventBasicGuidRelation }
    */
   public List<EventBasicGuidRelation> getRelations() {
      if (relations == null) {
         relations = new ArrayList<EventBasicGuidRelation>();
      }
      return this.relations;
   }

   /**
    * Gets the value of the networkSender property.
    * 
    * @return possible object is {@link NetworkSender }
    */
   public NetworkSender getNetworkSender() {
      return networkSender;
   }

   /**
    * Sets the value of the networkSender property.
    * 
    * @param value allowed object is {@link NetworkSender }
    */
   public void setNetworkSender(NetworkSender value) {
      this.networkSender = value;
   }

}
