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
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.eclipse.osee.accessor.types.ArtifactAccessorResultWithGammas;
import org.eclipse.osee.accessor.types.AttributePojo;
import org.eclipse.osee.framework.core.data.ApplicabilityId;
import org.eclipse.osee.framework.core.data.ApplicabilityToken;
import org.eclipse.osee.framework.core.data.ArtifactReadable;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.GammaId;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.jdk.core.type.Id;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.orcs.rest.model.transaction.Attribute;
import org.eclipse.osee.orcs.rest.model.transaction.CreateArtifact;

/**
 * @author Luciano T. Vaglienti
 */
public class InterfaceConnection extends ArtifactAccessorResultWithGammas {

   public static final InterfaceConnection SENTINEL = new InterfaceConnection();

   private AttributePojo<String> Description =
      AttributePojo.valueOf(Id.SENTINEL, CoreAttributeTypes.Description, GammaId.SENTINEL, "", "");
   private TransportType TransportType;
   private List<InterfaceNode> nodes;
   private ApplicabilityToken applicability;

   public InterfaceConnection(ArtifactToken art) {
      this((ArtifactReadable) art);
   }

   public InterfaceConnection(ArtifactReadable art) {
      super(art);
      this.setNodes(art.getRelated(CoreRelationTypes.InterfaceConnectionNode_Node).getList().stream().filter(
         a -> !a.getExistingAttributeTypes().isEmpty()).map(n -> new InterfaceNode(n)).collect(Collectors.toList()));
      ArtifactReadable transportType =
         art.getRelated(CoreRelationTypes.InterfaceConnectionTransportType_TransportType).getAtMostOneOrDefault(
            ArtifactReadable.SENTINEL);
      if (!transportType.getExistingAttributeTypes().isEmpty()) {
         this.setTransportType(new TransportType(
            art.getRelated(CoreRelationTypes.InterfaceConnectionTransportType_TransportType).getAtMostOneOrDefault(
               ArtifactReadable.SENTINEL)));
      } else {
         this.setTransportType(org.eclipse.osee.mim.types.TransportType.SENTINEL);
      }
      this.setDescription(AttributePojo.valueOf(art.getSoleAttribute(CoreAttributeTypes.Description, "")));
      this.setApplicability(
         !art.getApplicabilityToken().getId().equals(-1L) ? art.getApplicabilityToken() : ApplicabilityToken.SENTINEL);
   }

   public InterfaceConnection(Long id, String name) {
      super(id, AttributePojo.valueOf(Id.SENTINEL, CoreAttributeTypes.Name, GammaId.SENTINEL, name, ""));
      this.setNodes(new LinkedList<>());
   }

   public InterfaceConnection() {
   }

   /**
    * @return the description
    */
   public AttributePojo<String> getDescription() {
      return Description;
   }

   /**
    * @param description the description to set
    */
   @JsonProperty
   public void setDescription(AttributePojo<String> description) {
      this.Description = description;
   }

   public void setDescription(String description) {
      this.Description = AttributePojo.valueOf(this.Description.getId(), this.Description.getTypeId(),
         this.Description.getGammaId(), description, this.Description.getDisplayableString());
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

   public List<InterfaceNode> getNodes() {
      return nodes;
   }

   private void setNodes(List<InterfaceNode> nodes) {
      this.nodes = nodes;
   }

   public CreateArtifact createArtifact(String key, ApplicabilityId applicId) {
      // @formatter:off
      Map<AttributeTypeToken, String> values = new HashMap<>();
      values.put(CoreAttributeTypes.Description, this.getDescription().getValue());
      // @formatter:on

      CreateArtifact art = new CreateArtifact();
      art.setName(this.getName().getValue());
      art.setTypeId(CoreArtifactTypes.InterfaceConnection.getIdString());

      List<Attribute> attrs = new LinkedList<>();

      for (AttributeTypeToken type : CoreArtifactTypes.InterfaceConnection.getValidAttributeTypes()) {
         String value = values.get(type);
         if (Strings.isInValid(value)) {
            continue;
         }
         Attribute attr = new Attribute(type.getIdString());
         attr.setValue(Arrays.asList(value));
         attrs.add(attr);
      }

      art.setAttributes(attrs);
      art.setApplicabilityId(applicId.getIdString());
      art.setkey(key);
      return art;
   }

   @JsonIgnore
   @Override
   public boolean equals(Object obj) {
      if (obj == this) {
         return true;
      }
      if (obj instanceof InterfaceConnection) {
         InterfaceConnection other = ((InterfaceConnection) obj);
         ArtifactReadable otherArt = other.getArtifactReadable();
         if (!this.getName().valueEquals(other.getName())) {
            return false;
         }
         if (!this.getDescription().valueEquals(other.getDescription())) {
            return false;
         }
         if (!this.getTransportType().equals(other.getTransportType())) {
            return false;
         }
         if (!this.getNodes().equals(other.getNodes())) {
            return false;
         }
         List<InterfaceMessageToken> messages = this.getArtifactReadable().getRelated(
            CoreRelationTypes.InterfaceConnectionMessage_Message).getList().stream().filter(
               a -> !a.getExistingAttributeTypes().isEmpty()).map(art -> new InterfaceMessageToken(art)).collect(
                  Collectors.toList());
         List<InterfaceMessageToken> otherMessages =
            otherArt.getRelated(CoreRelationTypes.InterfaceConnectionMessage_Message).getList().stream().filter(
               a -> !a.getExistingAttributeTypes().isEmpty()).map(art -> new InterfaceMessageToken(art)).collect(
                  Collectors.toList());
         if (!messages.equals(otherMessages)) {
            return false;
         }
         return true;
      }
      return false;

   }
}
