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
package org.eclipse.osee.framework.core.data;

import org.eclipse.osee.framework.core.OrcsTokenService;
import org.eclipse.osee.framework.jdk.core.type.Id;

/**
 * @author Jaden W. Puckett
 */
public class ArtifactRelatedDirectAttribute {
   private final AttributeTypeToken attributeTypeToken;
   private final Multiplicity multiplicity;

   private final IAttribute<?> attribute;
   private final ArtifactTypeToken artifactTypeToken;
   private final OrcsTokenService tokenService;

   public ArtifactRelatedDirectAttribute(AttributeTypeToken attributeTypeToken, Multiplicity multiplicity) {
      this.attributeTypeToken = attributeTypeToken;
      this.multiplicity = multiplicity;

      this.attribute = null;
      this.artifactTypeToken = null;
      this.tokenService = null;
   }

   public ArtifactRelatedDirectAttribute(IAttribute<?> attribute, ArtifactTypeToken artifactTypeToken, OrcsTokenService tokenService) {
      this.attribute = attribute;
      this.artifactTypeToken = artifactTypeToken;
      this.tokenService = tokenService;

      this.attributeTypeToken = attribute.getAttributeType();
      this.multiplicity = null;
   }

   public String getName() {
      return this.attributeTypeToken.getName();
   }

   public String getTypeId() {
      return this.attributeTypeToken.getIdString();
   }

   public String getStoreType() {
      return this.attributeTypeToken.getStoreType();
   }

   public String getMultiplicityId() {
      if (this.multiplicity != null) {
         return this.multiplicity.getIdString();
      } else if (this.tokenService != null && this.artifactTypeToken != null && this.attribute != null) {
         return tokenService.getArtifactType(artifactTypeToken.getId()).getValidAttributeTypes().stream().filter(
            attrType -> attribute.getAttributeType().getId().equals(attrType.getId())).map(
               attrTok -> artifactTypeToken.getMultiplicity(attrTok).getIdString()).findFirst().orElse(
                  Id.SENTINEL.toString());
      } else {
         return "";
      }
   }

   public String getId() {
      if (this.attribute != null) {
         return this.attribute.getIdString();
      } else {
         return "";
      }
   }

   public String getValue() {
      if (this.attribute != null) {
         Object value = this.attribute.getValue();

         if (value == null) {
            return "";
         } else if (value instanceof String) {
            return (String) value;
         } else {
            try {
               // Attempt to access the 'name' attribute using reflection
               Object nameAttribute = value.getClass().getMethod("getName").invoke(value);
               return nameAttribute != null ? nameAttribute.toString() : "";
            } catch (Exception e) {
               return value.toString();
            }
         }
      } else {
         return "";
      }
   }
}