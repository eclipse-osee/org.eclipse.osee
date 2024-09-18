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
package org.eclipse.osee.framework.core.applicability;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactTypeId;
import org.eclipse.osee.framework.core.data.GammaId;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;

public class ConfigurationValue extends NamedIdWithGamma {
   public static ConfigurationValue NO_GROUP = new ConfigurationValue(-1L, "No Group", GammaId.SENTINEL,
      NamedIdWithGamma.SENTINEL, CoreArtifactTypes.GroupArtifact, new ArrayList<>());
   public static ConfigurationValue SENTINEL = new ConfigurationValue(-1L, "No Group", GammaId.SENTINEL,
      NamedIdWithGamma.SENTINEL, ArtifactTypeId.SENTINEL, new ArrayList<>());

   private NamedIdWithGamma applicability;
   private ArtifactTypeId typeId = ArtifactTypeId.SENTINEL;
   private List<ArtifactId> related = new LinkedList<ArtifactId>();

   public ConfigurationValue(Long id, String name, GammaId gammaId, NamedIdWithGamma applicability, ArtifactTypeId typeId, List<ArtifactId> related) {
      super(id, name, gammaId);
      this.setApplicability(applicability);
      this.setTypeId(typeId);
      this.setRelated(related);
   }

   /**
    * @return the applicability
    */
   public NamedIdWithGamma getApplicability() {
      return applicability;
   }

   /**
    * @param applicability the applicability to set
    */
   public void setApplicability(NamedIdWithGamma applicability) {
      this.applicability = applicability;
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

   /**
    * @return the typeId
    */
   public ArtifactTypeId getTypeId() {
      return typeId;
   }

   /**
    * @param typeId the typeId to set
    */
   public void setTypeId(ArtifactTypeId typeId) {
      this.typeId = typeId;
   }

   /**
    * @return the related
    */
   @JsonIgnore
   public List<ArtifactId> getRelated() {
      return related;
   }

   /**
    * @param related the related to set
    */
   @JsonIgnore
   public void setRelated(List<ArtifactId> related) {
      this.related = related;
   }
}
