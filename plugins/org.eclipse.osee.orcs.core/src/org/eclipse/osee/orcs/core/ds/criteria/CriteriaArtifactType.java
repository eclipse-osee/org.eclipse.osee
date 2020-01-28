/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs.core.ds.criteria;

import java.util.Collection;
import java.util.LinkedHashSet;
import org.eclipse.osee.framework.core.data.ArtifactTypeId;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.orcs.core.ds.Criteria;
import org.eclipse.osee.orcs.core.ds.Options;

/**
 * @author Roberto E. Escobar
 */
public class CriteriaArtifactType extends Criteria {

   private final Collection<ArtifactTypeToken> artifactTypes;
   private final boolean includeTypeInheritance;

   public CriteriaArtifactType(Collection<ArtifactTypeToken> artifactTypes, boolean includeTypeInheritance) {
      this.artifactTypes = artifactTypes;
      this.includeTypeInheritance = includeTypeInheritance;
   }

   @Override
   public void checkValid(Options options) {
      Conditions.checkNotNullOrEmpty(artifactTypes, "artifact types");
   }

   public Collection<ArtifactTypeToken> getOriginalTypes() {
      return artifactTypes;
   }

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
}