/*******************************************************************************
 * Copyright (c) 2009 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.skynet.core.types.field;

import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import org.eclipse.osee.framework.core.exception.OseeArgumentException;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactType;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.attribute.AttributeType;
import org.eclipse.osee.framework.skynet.core.types.ArtifactTypeCache;

/**
 * @author Roberto E. Escobar
 */
public final class ArtifactTypeAttributesField extends AbstractOseeField<Map<Branch, Collection<AttributeType>>> {

   private final ArtifactType artifactType;
   private final ArtifactTypeCache cache;

   public ArtifactTypeAttributesField(ArtifactTypeCache cache, ArtifactType artifactType) {
      super();
      this.artifactType = artifactType;
      this.cache = cache;
   }

   @Override
   public Map<Branch, Collection<AttributeType>> get() throws OseeCoreException {
      return cache.getLocalAttributeTypes(artifactType);
   }

   @Override
   public void set(Map<Branch, Collection<AttributeType>> attributeTypeMap) throws OseeCoreException {
      if (attributeTypeMap == null) {
         throw new OseeArgumentException("input cannot be null");
      }

      for (Entry<Branch, Collection<AttributeType>> entry : attributeTypeMap.entrySet()) {
         set(entry.getKey(), entry.getValue());
      }
   }

   private void set(Branch branch, Collection<AttributeType> attributeTypes) throws OseeCoreException {
      Collection<AttributeType> original = cache.getLocalAttributeTypes(artifactType, branch);
      cache.cacheTypeValidity(artifactType, attributeTypes, branch);
      Collection<AttributeType> newTypes = cache.getLocalAttributeTypes(artifactType, branch);
      isDirty |= ChangeUtil.isDifferent(original, newTypes);
   }
}