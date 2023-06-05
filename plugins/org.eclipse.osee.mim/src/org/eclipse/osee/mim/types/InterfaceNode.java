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
import org.eclipse.osee.framework.core.data.ApplicabilityToken;
import org.eclipse.osee.framework.core.data.ArtifactReadable;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;

/**
 * @author Luciano T. Vaglienti
 */
public class InterfaceNode extends PLGenericDBObject {

   public static final InterfaceNode SENTINEL = new InterfaceNode();

   private String Description;
   private String nodeNumber;
   private String nodeGroupId;
   private ApplicabilityToken applicability;
   private String Color;
   private String Address;
   private ArtifactReadable artifactReadable;

   public InterfaceNode(ArtifactToken art) {
      this((ArtifactReadable) art);
   }

   public InterfaceNode(ArtifactReadable art) {
      super(art);
      this.setDescription(art.getSoleAttributeValue(CoreAttributeTypes.Description, ""));
      this.setColor(art.getSoleAttributeValue(CoreAttributeTypes.InterfaceNodeBackgroundColor, ""));
      this.setAddress(art.getSoleAttributeValue(CoreAttributeTypes.InterfaceNodeAddress, ""));
      this.setNodeNumber(art.getSoleAttributeValue(CoreAttributeTypes.InterfaceNodeNumber, ""));
      this.setNodeGroupId(art.getSoleAttributeValue(CoreAttributeTypes.InterfaceNodeGroupId, ""));
      this.setApplicability(
         !art.getApplicabilityToken().getId().equals(-1L) ? art.getApplicabilityToken() : ApplicabilityToken.SENTINEL);
      this.setArtifactReadable(art);
   }

   public InterfaceNode(Long id, String name) {
      super(id, name);
   }

   public InterfaceNode() {
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

   /**
    * @return the color
    */
   public String getColor() {
      return Color;
   }

   /**
    * @param color the color to set
    */
   public void setColor(String color) {
      Color = color;
   }

   /**
    * @return the address
    */
   public String getAddress() {
      return Address;
   }

   /**
    * @param address the address to set
    */
   public void setAddress(String address) {
      Address = address;
   }

   public String getNodeNumber() {
      return nodeNumber;
   }

   public void setNodeNumber(String nodeNumber) {
      this.nodeNumber = nodeNumber;
   }

   public String getNodeGroupId() {
      return nodeGroupId;
   }

   public void setNodeGroupId(String nodeGroupId) {
      this.nodeGroupId = nodeGroupId;
   }

   @JsonIgnore
   public ArtifactReadable getArtifactReadable() {
      return artifactReadable;
   }

   private void setArtifactReadable(ArtifactReadable artifactReadable) {
      this.artifactReadable = artifactReadable;
   }

}
