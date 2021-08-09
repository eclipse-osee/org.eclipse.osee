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
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.mim.annotations.OseeArtifactAttribute;
import org.eclipse.osee.mim.annotations.OseeArtifactRequiredAttribute;
import org.eclipse.osee.orcs.data.ArtifactReadable;

/**
 * @author Luciano T. Vaglienti
 */
public class NodeView extends PLGenericDBObject {

   @OseeArtifactRequiredAttribute()
   @OseeArtifactAttribute(attributeId = 1152921504606847088L)
   private String Name; //required

   @OseeArtifactAttribute(attributeId = 1152921504606847090L)
   private String Description;

   private String label = ""; //label to display on connection line, should be same as {@Name}
   private NodeViewData data;

   public NodeView(ArtifactToken art) {
      this((ArtifactReadable) art);
   }

   public NodeView(InterfaceNode node) {
      this(node.getId(), node.getName(), node.getDescription(), node.getApplicability());
   }

   public NodeView(ArtifactReadable art) {
      super(art);
      this.setData(new NodeViewData(art));
   }

   public NodeView(Long id, String name, String description, ApplicabilityToken applicability) {
      this(id, name);
      this.setLabel(name);
      this.setData(new NodeViewData(id, name));
      this.setApplicability(applicability);
      this.setDescription(description);
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
   public String getbgColor() {
      return data.getbgColor();
   }

   /**
    * @param color the color to set
    */
   @JsonIgnore
   public void setbgColor(String color) {
      this.data.setbgColor(color);
   }

   public void setApplicability(ApplicabilityToken applicability) {
      this.data.setApplicability(applicability);
   }

}
