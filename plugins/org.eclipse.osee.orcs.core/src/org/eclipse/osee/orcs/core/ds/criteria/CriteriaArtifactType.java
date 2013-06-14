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
import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.util.Conditions;
import org.eclipse.osee.orcs.core.ds.Criteria;
import org.eclipse.osee.orcs.core.ds.QueryOptions;
import org.eclipse.osee.orcs.data.ArtifactTypes;

/**
 * @author Roberto E. Escobar
 */
public class CriteriaArtifactType extends Criteria<QueryOptions> {

   private final Collection<? extends IArtifactType> artifactTypes;
   private final ArtifactTypes artTypeCache;

   public CriteriaArtifactType(ArtifactTypes artTypeCache, Collection<? extends IArtifactType> artifactTypes) {
      super();
      this.artifactTypes = artifactTypes;
      this.artTypeCache = artTypeCache;
   }

   @Override
   public void checkValid(QueryOptions options) throws OseeCoreException {
      super.checkValid(options);
      Conditions.checkNotNullOrEmpty(artifactTypes, "artifact types");
   }

   public Collection<? extends IArtifactType> getTypes() {
      return artifactTypes;
   }

   @Override
   public String toString() {
      return "CriteriaArtifactType [artifactTypes=" + artifactTypes + "]";
   }

   public Collection<? extends IArtifactType> getTypes(QueryOptions options) throws OseeCoreException {
      Collection<IArtifactType> typesToUse = new LinkedHashSet<IArtifactType>();
      boolean includeTypeInheritance = options.isTypeInheritanceIncluded();
      for (IArtifactType type : artifactTypes) {
         if (includeTypeInheritance) {
            for (IArtifactType descendant : artTypeCache.getAllDescendantTypes(type)) {
               typesToUse.add(descendant);
            }
         }
         typesToUse.add(type);
      }
      return typesToUse;
   }

}
