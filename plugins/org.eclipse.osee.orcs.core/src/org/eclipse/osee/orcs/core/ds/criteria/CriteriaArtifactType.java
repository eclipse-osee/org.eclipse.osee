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
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.orcs.core.ds.Criteria;
import org.eclipse.osee.orcs.core.ds.Options;
import org.eclipse.osee.orcs.data.ArtifactTypes;

/**
 * @author Roberto E. Escobar
 */
public class CriteriaArtifactType extends Criteria {

   private final Collection<? extends ArtifactTypeId> artifactTypes;
   private final ArtifactTypes artTypeCache;
   private final boolean includeTypeInheritance;

   public CriteriaArtifactType(ArtifactTypes artTypeCache, Collection<? extends ArtifactTypeId> artifactTypes, boolean includeTypeInheritance) {
      super();
      this.artifactTypes = artifactTypes;
      this.artTypeCache = artTypeCache;
      this.includeTypeInheritance = includeTypeInheritance;
   }

   @Override
   public void checkValid(Options options)  {
      Conditions.checkNotNullOrEmpty(artifactTypes, "artifact types");
   }

   public Collection<? extends ArtifactTypeId> getOriginalTypes() {
      return artifactTypes;
   }

   public Collection<? extends ArtifactTypeId> getTypes()  {
      Collection<? extends ArtifactTypeId> toReturn;
      if (includeTypeInheritance) {
         Collection<ArtifactTypeId> typesToUse = new LinkedHashSet<>();
         for (ArtifactTypeId type : getOriginalTypes()) {
            for (ArtifactTypeId descendant : artTypeCache.getAllDescendantTypes(type)) {
               typesToUse.add(descendant);
            }
            typesToUse.add(type);
         }
         toReturn = typesToUse;
      } else {
         toReturn = getOriginalTypes();
      }
      return toReturn;
   }

   @Override
   public String toString() {
      return "CriteriaArtifactType [artifactTypes=" + artifactTypes + ", includeTypeInheritance=" + includeTypeInheritance + "]";
   }

}
