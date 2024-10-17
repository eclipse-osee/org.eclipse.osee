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
import org.eclipse.osee.framework.jdk.core.type.Id;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.orcs.rest.model.transaction.Attribute;
import org.eclipse.osee.orcs.rest.model.transaction.CreateArtifact;

/**
 * @author Ryan T. Baldwin
 */
public class InterfaceUnitToken extends ArtifactAccessorResultWithGammas {
   public static final InterfaceUnitToken SENTINEL = new InterfaceUnitToken();

   private AttributePojo<String> measurement =
      AttributePojo.valueOf(Id.SENTINEL, CoreAttributeTypes.InterfaceUnitMeasurement, GammaId.SENTINEL, "", "");
   private ApplicabilityToken applicability;

   public InterfaceUnitToken(ArtifactToken art) {
      this((ArtifactReadable) art);
   }

   public InterfaceUnitToken(ArtifactReadable art) {
      super(art);
      this.setId(art.getId());
      this.setName(AttributePojo.valueOf(art.getSoleAttribute(CoreAttributeTypes.Name, "")));
      this.setMeasurement(AttributePojo.valueOf(art.getSoleAttribute(CoreAttributeTypes.InterfaceUnitMeasurement, "")));
      this.setApplicability(
         !art.getApplicabilityToken().getId().equals(-1L) ? art.getApplicabilityToken() : ApplicabilityToken.SENTINEL);
   }

   public InterfaceUnitToken(Long id, String name) {
      super(id, AttributePojo.valueOf(Id.SENTINEL, CoreAttributeTypes.Name, GammaId.SENTINEL, name, ""));
      this.setMeasurement("");
      this.setApplicability(ApplicabilityToken.BASE);
   }

   public InterfaceUnitToken() {
      super();
   }

   public AttributePojo<String> getMeasurement() {
      return measurement;
   }

   public void setMeasurement(String measurement) {
      this.measurement = AttributePojo.valueOf(Id.SENTINEL, CoreAttributeTypes.InterfaceUnitMeasurement,
         GammaId.SENTINEL, measurement, "");
   }

   @JsonProperty
   public void setMeasurement(AttributePojo<String> measurement) {
      this.measurement = measurement;
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

   public CreateArtifact createArtifact(String key, ApplicabilityId applicId) {
      // @formatter:off
      Map<AttributeTypeToken, String> values = new HashMap<>();
      values.put(CoreAttributeTypes.InterfaceUnitMeasurement, this.getMeasurement().getValue());
      // @formatter:on

      CreateArtifact art = new CreateArtifact();
      art.setName(this.getName().getValue());
      art.setTypeId(CoreArtifactTypes.InterfaceUnit.getIdString());

      List<Attribute> attrs = new LinkedList<>();

      for (AttributeTypeToken type : CoreArtifactTypes.InterfaceUnit.getValidAttributeTypes()) {
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

}
