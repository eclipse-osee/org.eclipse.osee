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

import java.util.List;
import org.eclipse.osee.accessor.types.ArtifactAccessorResultWithGammas;
import org.eclipse.osee.accessor.types.AttributePojo;
import org.eclipse.osee.framework.core.data.ApplicabilityToken;
import org.eclipse.osee.framework.core.data.ArtifactReadable;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.GammaId;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.jdk.core.type.Id;

/**
 * @author Luciano T. Vaglienti
 */
public class ConnectionViewData extends ArtifactAccessorResultWithGammas {
   private TransportType TransportType; //will need logic for both of these or data stored in DB
   private List<InterfaceNode> nodes;
   private ApplicabilityToken applicability;
   private AttributePojo<String> Description =
      AttributePojo.valueOf(Id.SENTINEL, CoreAttributeTypes.Description, GammaId.SENTINEL, "", "");

   public ConnectionViewData(ArtifactToken art) {
      this((ArtifactReadable) art);
   }

   public ConnectionViewData(ArtifactReadable art) {
      super(art);

   }

   public ConnectionViewData(Long id, AttributePojo<String> name) {
      super(id, name);
   }

   public ConnectionViewData() {
   }

   /**
    * @return the type
    */
   public TransportType getTransportType() {
      return TransportType;
   }

   /**
    * @param type the type to set
    */
   public void setTransportType(TransportType type) {
      this.TransportType = type;
   }

   public List<InterfaceNode> getNodes() {
      return nodes;
   }

   public void setNodes(List<InterfaceNode> nodes) {
      this.nodes = nodes;
   }

   /**
    * @return the isDashed
    */
   public boolean isDashed() {
      return TransportType.getDashedPresentation().getValue();
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
    * @return the description
    */
   public AttributePojo<String> getDescription() {
      return Description;
   }

   /**
    * @param description the description to set
    */
   public void setDescription(AttributePojo<String> description) {
      this.Description = description;
   }

}
