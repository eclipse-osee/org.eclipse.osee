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
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.mim.annotations.OseeArtifactAttribute;
import org.eclipse.osee.mim.annotations.OseeArtifactRequiredAttribute;
import org.eclipse.osee.orcs.data.ArtifactReadable;

/**
 * @author Luciano T. Vaglienti
 */
public class ConnectionView extends PLGenericDBObject {

   @OseeArtifactRequiredAttribute()
   @OseeArtifactAttribute(attributeId = 1152921504606847088L)
   @JsonIgnore
   private String Name; //required

   @OseeArtifactAttribute(attributeId = 1152921504606847090L)
   private String Description;

   private String source = "";//source node to reference
   private String target = ""; //target node to reference
   private String label = ""; //label to display on connection line, should be same as {@Name}
   private ConnectionViewData data;

   public ConnectionView(ArtifactToken art) {
      this((ArtifactReadable) art);
   }

   public ConnectionView(InterfaceConnection connection) {
      this(connection.getId(), connection.getName(), connection.getDescription(), connection.getTransportType());
      this.setSource(connection.getPrimaryNode().toString());
      this.setTarget(connection.getSecondaryNode().toString());
      this.setApplicability(connection.getApplicability());
   }

   public ConnectionView(ArtifactReadable art) {
      this();
      this.setId(art.getId());
      this.setName(art.getName());
      this.setData(new ConnectionViewData(art));
      this.setDescription(art.getSoleAttributeValue(CoreAttributeTypes.Description, ""));
   }

   public ConnectionView(Long id, String name, String description, String transportType) {
      this(id, name);
      this.setLabel(name);
      this.setData(new ConnectionViewData(id, name));
      this.setDescription(description);
      this.setType(ConnectionViewType.valueOf(transportType));
   }

   public ConnectionView(Long id, String name) {
      super(id, name);
   }

   public ConnectionView() {
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
    * @return the source
    */
   public String getSource() {
      return source;
   }

   /**
    * @param source the source to set
    */
   public void setSource(String source) {
      this.source = source;
   }

   /**
    * @return the target
    */
   public String getTarget() {
      return target;
   }

   /**
    * @param target the target to set
    */
   public void setTarget(String target) {
      this.target = target;
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

   /**
    * @return the data
    */
   public ConnectionViewData getData() {
      return data;
   }

   /**
    * @param data the data to set
    */
   public void setData(ConnectionViewData data) {
      this.data = data;
   }

   /**
    * @return the type
    */
   @JsonIgnore
   public ConnectionViewType getType() {
      return data.getTransportType();
   }

   /**
    * @param data the type to set
    */
   @JsonIgnore
   public void setType(ConnectionViewType type) {
      this.data.setTransportType(type);
   }

   /**
    * @return whether or not the connection is a dashed line
    */
   @JsonIgnore
   public boolean getIsDashed() {
      return data.isDashed();
   }

   /**
    * @param isDashed set whether the line is dashed or not
    */
   @JsonIgnore
   public void setIsDashed(boolean isDashed) {
      this.data.setDashed(isDashed);
   }

   /**
    * @return the applicability
    */
   @JsonIgnore
   public ApplicabilityToken getApplicability() {
      return data.getApplicability();
   }

   /**
    * @param applicabilityToken the applicability to set
    */
   public void setApplicability(ApplicabilityToken applicabilityToken) {
      this.data.setApplicability(applicabilityToken);
   }
}