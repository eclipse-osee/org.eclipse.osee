/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
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

package org.eclipse.osee.orcs.search.ds.criteria;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.Collection;
import java.util.LinkedHashSet;
import org.eclipse.osee.framework.core.data.ArtifactTypeId;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.orcs.search.ds.Criteria;
import org.eclipse.osee.orcs.search.ds.Options;

/**
 * @author Roberto E. Escobar
 */
public class CriteriaArtifactType extends Criteria {

   private Collection<ArtifactTypeToken> artifactTypes;
   private boolean includeTypeInheritance;

   public CriteriaArtifactType() {
      // for jax-rs
      this(null, false);
   }

   public CriteriaArtifactType(Collection<ArtifactTypeToken> artifactTypes, boolean includeTypeInheritance) {
      this.artifactTypes = artifactTypes;
      this.includeTypeInheritance = includeTypeInheritance;
   }

   @Override
   public void checkValid(Options options) {
      Conditions.checkNotNullOrEmpty(artifactTypes, "artifact types");
   }

   @JsonIgnore
   public Collection<? extends ArtifactTypeId> getTypes() {
      if (includeTypeInheritance) {
         Collection<ArtifactTypeId> typesToUse = new LinkedHashSet<>();
         typesToUse.addAll(artifactTypes);
         for (ArtifactTypeToken type : artifactTypes) {
            typesToUse.addAll(type.getAllDescendantTypes());
         }
         return typesToUse;
      }
      return artifactTypes;
   }

   @Override
   public String toString() {
      return "CriteriaArtifactType [artifactTypes=" + artifactTypes + ", includeTypeInheritance=" + includeTypeInheritance + "]";
   }

   public Collection<ArtifactTypeToken> getArtifactTypes() {
      return artifactTypes;
   }

   public boolean isIncludeTypeInheritance() {
      return includeTypeInheritance;
   }

   public void setArtifactTypes(Collection<ArtifactTypeToken> artifactTypes) {
      this.artifactTypes = artifactTypes;
   }

   public void setIncludeTypeInheritance(boolean includeTypeInheritance) {
      this.includeTypeInheritance = includeTypeInheritance;
   }
}