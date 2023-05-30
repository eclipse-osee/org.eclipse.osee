/*********************************************************************
 * Copyright (c) 2021 Boeing
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
package org.eclipse.osee.mim.types;

import org.eclipse.osee.framework.core.data.ApplicabilityToken;
import org.eclipse.osee.framework.core.data.ArtifactReadable;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;

/**
 * @author Luciano T. Vaglienti
 */
public class InterfaceConnection extends PLGenericDBObject {

   public static final InterfaceConnection SENTINEL = new InterfaceConnection();

   private String Description;
   private TransportType TransportType;
   private Long primaryNode;
   private Long secondaryNode;
   private ApplicabilityToken applicability;

   public InterfaceConnection(ArtifactToken art) {
      this((ArtifactReadable) art);
   }

   public InterfaceConnection(ArtifactReadable art) {
      super(art);
      this.setPrimaryNode(art.getRelated(CoreRelationTypes.InterfaceConnectionPrimary_Node).getOneOrDefault(
         ArtifactReadable.SENTINEL).getId());
      this.setSecondaryNode(art.getRelated(CoreRelationTypes.InterfaceConnectionSecondary_Node).getOneOrDefault(
         ArtifactReadable.SENTINEL).getId());
      this.setTransportType(new TransportType(
         art.getRelated(CoreRelationTypes.InterfaceConnectionTransportType_TransportType).getAtMostOneOrDefault(
            ArtifactReadable.SENTINEL)));
      this.setDescription(art.getSoleAttributeValue(CoreAttributeTypes.Description, ""));
      this.setApplicability(
         !art.getApplicabilityToken().getId().equals(-1L) ? art.getApplicabilityToken() : ApplicabilityToken.SENTINEL);
   }

   public InterfaceConnection(Long id, String name) {
      super(id, name);
   }

   public InterfaceConnection() {
   }

   /**
    * @return the description
    */
   public String getDescription() {
      return Description;
   }

   /**
    * @param description the description to set
    */
   public void setDescription(String description) {
      this.Description = description;
   }

   /**
    * @return the secondaryNode
    */
   public Long getSecondaryNode() {
      return secondaryNode;
   }

   /**
    * @param secondaryNode the secondaryNode to set
    */
   public void setSecondaryNode(Long secondaryNode) {
      this.secondaryNode = secondaryNode;
   }

   /**
    * @return the primaryNode
    */
   public Long getPrimaryNode() {
      return primaryNode;
   }

   /**
    * @param primaryNode the primaryNode to set
    */
   public void setPrimaryNode(Long primaryNode) {
      this.primaryNode = primaryNode;
   }

   /**
    * @return the transportType
    */
   public TransportType getTransportType() {
      return TransportType;
   }

   /**
    * @param transportType the transportType to set
    */
   public void setTransportType(TransportType transportType) {
      TransportType = transportType;
   }

   /**
    * @return the applicability
    */
   public ApplicabilityToken getApplicability() {
      return applicability;
   }

   /**
    * @param applicability the applicability to set
    */
   public void setApplicability(ApplicabilityToken applicability) {
      this.applicability = applicability;
   }

}
