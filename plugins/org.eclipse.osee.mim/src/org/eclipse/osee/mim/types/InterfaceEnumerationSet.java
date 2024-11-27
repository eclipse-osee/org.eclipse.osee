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
public class InterfaceEnumerationSet extends ArtifactAccessorResultWithGammas {
   public static final InterfaceEnumerationSet SENTINEL = new InterfaceEnumerationSet();
   private AttributePojo<String> Description =
      AttributePojo.valueOf(Id.SENTINEL, CoreAttributeTypes.Description, GammaId.SENTINEL, "", "");
   private ApplicabilityToken applicability = ApplicabilityToken.SENTINEL;
   private List<InterfaceEnumeration> enumerations = new LinkedList<InterfaceEnumeration>();

   public InterfaceEnumerationSet(ArtifactToken art) {
      this((ArtifactReadable) art);
   }

   public InterfaceEnumerationSet(ArtifactReadable art) {
      super(art);
      this.setDescription(AttributePojo.valueOf(art.getSoleAttribute(CoreAttributeTypes.Description, "")));
      this.setApplicability(
         !art.getApplicabilityToken().getId().equals(-1L) ? art.getApplicabilityToken() : ApplicabilityToken.SENTINEL);
      art.getRelated(CoreRelationTypes.InterfaceEnumeration_EnumerationState).getList().stream().filter(
         a -> !a.getExistingAttributeTypes().isEmpty()).forEach(
            a -> getEnumerations().add(new InterfaceEnumeration(a)));

      if (!getEnumerations().isEmpty()) {
         String desc = "";
         for (InterfaceEnumeration enumeration : getEnumerations()) {
            if (enumeration.isValid()) {
               desc += enumeration.getOrdinal().getValue() + " = " + enumeration.getName().getValue() + "\n";
            }
         }
         setDescription(desc.trim());
      }
   }

   public InterfaceEnumerationSet(Long id, String name) {
      super(id, AttributePojo.valueOf(Id.SENTINEL, CoreAttributeTypes.Name, GammaId.SENTINEL, name, name));
   }

   public InterfaceEnumerationSet() {
      super(Id.SENTINEL, AttributePojo.valueOf(Id.SENTINEL, CoreAttributeTypes.Name, GammaId.SENTINEL, "", ""));
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
   public void setDescription(String description) {
      AttributePojo<String> oldDescription = getDescription();
      this.Description = AttributePojo.valueOf(oldDescription.getId(), oldDescription.getTypeId(),
         oldDescription.getGammaId(), description, oldDescription.getDisplayableString());
   }

   @JsonProperty
   public void setDescription(AttributePojo<String> description) {
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
    * @return the enumerations
    */
   public List<InterfaceEnumeration> getEnumerations() {
      return enumerations;
   }

   /**
    * @param enumerations the enumerations to set
    */
   public void setEnumerations(List<InterfaceEnumeration> enumerations) {
      this.enumerations = enumerations;
   }

   public CreateArtifact createArtifact(String key, ApplicabilityId applicId) {
      Map<AttributeTypeToken, String> values = new HashMap<>();
      values.put(CoreAttributeTypes.Description, this.getDescription().getValue());

      CreateArtifact art = new CreateArtifact();
      art.setName(this.getName().getValue());
      art.setTypeId(CoreArtifactTypes.InterfaceEnumSet.getIdString());

      List<Attribute> attrs = new LinkedList<>();

      for (AttributeTypeToken type : CoreArtifactTypes.InterfaceEnumSet.getValidAttributeTypes()) {
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
      if (obj instanceof InterfaceEnumerationSet) {
         InterfaceEnumerationSet other = ((InterfaceEnumerationSet) obj);
         if (!this.getName().valueEquals(other.getName())) {
            return false;
         }
         if (!this.getDescription().valueEquals(other.getDescription())) {
            return false;
         }
         if (!this.getEnumerations().equals(other.getEnumerations())) {
            return false;
         }
         return true;
      }
      return false;

   }
}
