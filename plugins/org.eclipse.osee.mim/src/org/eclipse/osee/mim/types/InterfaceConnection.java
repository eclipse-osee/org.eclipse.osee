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

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.mim.annotations.OseeArtifactAttribute;
import org.eclipse.osee.mim.annotations.OseeArtifactRequiredAttribute;
import org.eclipse.osee.orcs.data.ArtifactReadable;

/**
 * @author Luciano T. Vaglienti
 */
public class InterfaceConnection extends PLGenericDBObject {

   @OseeArtifactRequiredAttribute()
   @OseeArtifactAttribute(attributeId = 1152921504606847088L)
   private String Name; //required

   @OseeArtifactAttribute(attributeId = 1152921504606847090L)
   private String Description;

   @OseeArtifactAttribute(attributeId = 4522496963078776538L)
   @JsonIgnore
   private String TransportType;

   private Long primaryNode;

   private Long secondaryNode;

   public InterfaceConnection(ArtifactToken art) {
      this((ArtifactReadable) art);
   }

   public InterfaceConnection(ArtifactReadable art) {
      super(art);
      this.setPrimaryNode(art.getRelated(CoreRelationTypes.InterfaceConnectionPrimary_Node).getAtMostOneOrDefault(
         ArtifactReadable.SENTINEL).getId());
      this.setSecondaryNode(art.getRelated(CoreRelationTypes.InterfaceConnectionSecondary_Node).getAtMostOneOrDefault(
         ArtifactReadable.SENTINEL).getId());
      this.setTransportType(art.getSoleAttributeValue(CoreAttributeTypes.InterfaceTransportType, "ETHERNET"));
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
   @JsonIgnore
   public String getTransportType() {
      return TransportType;
   }

   /**
    * @param transportType the transportType to set
    */
   @JsonIgnore
   public void setTransportType(String transportType) {
      TransportType = transportType;
   }

}
