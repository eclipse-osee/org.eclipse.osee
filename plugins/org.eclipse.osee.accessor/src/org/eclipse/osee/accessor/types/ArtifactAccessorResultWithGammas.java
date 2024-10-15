/*********************************************************************
 * Copyright (c) 2024 Boeing
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
package org.eclipse.osee.accessor.types;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactReadable;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.GammaId;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.jdk.core.type.BaseId;
import org.eclipse.osee.framework.jdk.core.type.Id;

public class ArtifactAccessorResultWithGammas extends BaseId implements ArtifactAccessorResult {
   public static final ArtifactAccessorResultWithGammas SENTINEL = new ArtifactAccessorResultWithGammas();

   private ArtifactReadable artifactReadable = ArtifactReadable.SENTINEL;
   private GammaId gamma = GammaId.SENTINEL;
   private AttributePojo<String> name;

   public ArtifactAccessorResultWithGammas(ArtifactToken art) {
      this((ArtifactReadable) art);
   }

   public ArtifactAccessorResultWithGammas(ArtifactReadable art) {
      this(art.getId(), AttributePojo.valueOf(art.getSoleAttribute(CoreAttributeTypes.Name, "")));
      this.artifactReadable = art;
      this.setGamma(art.getGamma());
   }

   public ArtifactAccessorResultWithGammas(Long id, AttributePojo<String> name) {
      this();
      this.setId(id);
      this.setName(name);
   }

   public ArtifactAccessorResultWithGammas() {
      super(ArtifactId.SENTINEL.getId());
      this.setName("");
      // Not doing anything
   }

   @JsonIgnore
   public ArtifactReadable getArtifactReadable() {
      return artifactReadable;
   }

   @JsonIgnore
   public ArtifactId getArtifactId() {
      return ArtifactId.valueOf(super.getId());
   }

   @Override
   @JsonIgnore
   public String getIdString() {
      return super.getIdString();
   }

   @Override
   @JsonIgnore
   public int getIdIntValue() {
      return super.getIdIntValue();
   }

   public void setGamma(GammaId gamma) {
      this.gamma = gamma;
   }

   public GammaId getGammaId() {
      return this.gamma;
   }

   /**
    * @return the name
    */
   public AttributePojo<String> getName() {
      return name;
   }

   /**
    * @param name the name to set
    */
   @JsonProperty
   public void setName(AttributePojo<String> name) {
      this.name = name;
   }

   /**
    * @param name the name to set
    */
   public void setName(String name) {
      this.name = new AttributePojo<String>(Long.valueOf(Id.SENTINEL), CoreAttributeTypes.Name,
         GammaId.valueOf(Id.SENTINEL), name, "");
   }

   @Override
   public String toString() {
      return this.getName().getValue();
   }
}
