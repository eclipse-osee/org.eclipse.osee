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
import java.util.concurrent.ThreadLocalRandom;
import org.eclipse.osee.framework.core.data.ApplicabilityToken;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.orcs.data.ArtifactReadable;

/**
 * @author Luciano T. Vaglienti
 */
public class NodeViewData extends PLGenericDBObject {

   private String Description;
   private ApplicabilityToken applicability;
   private String interfaceNodeBgColor = generateColor() ? "#81d4fa" : "#c5e1a5"; //has to be called bgColor due to @swimlane/ngx-graph having weird handling behavior of node.data.color
   private String interfaceNodeAddress = "";

   public NodeViewData(ArtifactToken art) {
      this((ArtifactReadable) art);
   }

   public NodeViewData(ArtifactReadable art) {
      super(art);
   }

   public NodeViewData(Long id, String name) {
      super(id, name);
   }

   public NodeViewData() {
   }

   /**
    * @return the color
    */
   public String getinterfaceNodeBgColor() {
      return interfaceNodeBgColor;
   }

   /**
    * @param color the color to set
    */
   public void setinterfaceNodeBgColor(String color) {
      this.interfaceNodeBgColor = color;
   }

   @JsonIgnore
   private boolean generateColor() {
      return ThreadLocalRandom.current().nextInt(1, 3) > 1 ? true : false;
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
      Description = description;
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
    * @return the interfaceNodeAddress
    */
   public String getInterfaceNodeAddress() {
      return interfaceNodeAddress;
   }

   /**
    * @param interfaceNodeAddress the interfaceNodeAddress to set
    */
   public void setInterfaceNodeAddress(String interfaceNodeAddress) {
      this.interfaceNodeAddress = interfaceNodeAddress;
   }

}
