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
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
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
   private List<InterfaceNode> nodes;
   private ApplicabilityToken applicability;
   private ArtifactReadable artifactReadable;

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
      this.setDescription(art.getSoleAttributeValue(CoreAttributeTypes.Description, ""));
      this.setApplicability(
         !art.getApplicabilityToken().getId().equals(-1L) ? art.getApplicabilityToken() : ApplicabilityToken.SENTINEL);
      this.artifactReadable = art;
   }

   public InterfaceConnection(Long id, String name) {
      super(id, name);
      this.setNodes(new LinkedList<>());
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

   @JsonIgnore
   public ArtifactReadable getArtifactReadable() {
      return artifactReadable;
   }

}
