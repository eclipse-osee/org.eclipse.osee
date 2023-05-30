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

/**
 * @author Luciano T. Vaglienti
 */
public class NodeView extends PLGenericDBObject {

   private String label = ""; //label to display on connection line, should be same as {@Name}
   private NodeViewData data;

   public NodeView(ArtifactToken art) {
      this((ArtifactReadable) art);
   }

   public NodeView(InterfaceNode node) {
      this(node.getId(), node.getName(), node.getDescription(), node.getNodeNumber(), node.getNodeGroupId(),
         node.getApplicability(), node.getColor(), node.getAddress());
   }

   public NodeView(ArtifactReadable art) {
      super(art);
      this.setData(new NodeViewData(art));
      this.setApplicability(
         !art.getApplicabilityToken().getId().equals(-1L) ? art.getApplicabilityToken() : ApplicabilityToken.SENTINEL);
   }

   public NodeView(Long id, String name, String description, String nodeNumber, String nodeGroupId, ApplicabilityToken applicability, String color, String address) {
      this(id, name);
      this.setLabel(name);
      this.setData(new NodeViewData(id, name, nodeNumber, nodeGroupId));
      this.setApplicability(applicability);
      this.setDescription(description);
      this.setbgColor(color);
      this.setInterfaceNodeAddress(address);
   }

   public NodeView(Long id, String name) {
      super(id, name);
   }

   public NodeView() {
   }

   /**
    * @return the label
    */
   public String getLabel() {
      return label;
   }

   /**
    * @param label the label to set
    */
   public void setLabel(String label) {
      this.label = label;
   }

   @Override
   @JsonIgnore
   public String getName() {
      return super.getName();
   }

   @Override
   public void setName(String name) {
      super.setName(name);
      this.setLabel(name);
   }

   /**
    * @return the description
    */
   @JsonIgnore
   public String getDescription() {
      return data.getDescription();
   }

   /**
    * @param description the description to set
    */
   public void setDescription(String description) {
      this.data.setDescription(description);
   }

   /**
    * @return the data
    */
   public NodeViewData getData() {
      return data;
   }

   /**
    * @param data the data to set
    */
   public void setData(NodeViewData data) {
      this.data = data;
   }

   /**
    * @return color of node
    */
   @JsonIgnore
   public String getInterfaceNodeAddress() {
      return data.getInterfaceNodeAddress();
   }

   /**
    * @param color the color to set
    */
   @JsonIgnore
   public void setInterfaceNodeAddress(String address) {
      this.data.setInterfaceNodeAddress(address);
   }

   /**
    * @return color of node
    */
   @JsonIgnore
   public String getbgColor() {
      return data.getinterfaceNodeBgColor();
   }

   /**
    * @param color the color to set
    */
   @JsonIgnore
   public void setbgColor(String color) {
      this.data.setinterfaceNodeBgColor(color);
   }

   public void setApplicability(ApplicabilityToken applicability) {
      this.data.setApplicability(applicability);
   }

}
