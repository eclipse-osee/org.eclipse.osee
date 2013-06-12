/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs.core.internal.types.impl;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.core.data.IAttributeType;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.dsl.oseeDsl.XArtifactType;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.orcs.core.internal.types.BranchHierarchyProvider;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

/**
 * @author Roberto E. Escobar
 */
public class ArtifactTypeIndex extends TokenTypeIndex<Long, IArtifactType, XArtifactType> {

   private final Map<IArtifactType, ArtifactTypeMetaData> tokenToTypeData;
   private final BranchHierarchyProvider hierarchyProvider;

   public ArtifactTypeIndex(BranchHierarchyProvider hierarchyProvider) {
      super();
      this.tokenToTypeData = Maps.newHashMap();
      this.hierarchyProvider = hierarchyProvider;
   }

   public void put(IArtifactType type, Set<IArtifactType> superTypes) {
      ArtifactTypeMetaData metaData = getOrCreateData(type);
      metaData.setSuperTypes(superTypes);
   }

   public void put(IArtifactType type, Map<IOseeBranch, Collection<IAttributeType>> attributes) {
      ArtifactTypeMetaData metaData = getOrCreateData(type);
      metaData.setAttributeTypes(attributes);
   }

   private ArtifactTypeMetaData getOrCreateData(IArtifactType type) {
      ArtifactTypeMetaData metaData = tokenToTypeData.get(type);
      if (metaData == null) {
         metaData = new ArtifactTypeMetaData(type);
         tokenToTypeData.put(type, metaData);
      }
      return metaData;
   }

   public Collection<IArtifactType> getSuperTypes(IArtifactType artifactType) {
      ArtifactTypeMetaData metaData = tokenToTypeData.get(artifactType);
      return metaData != null ? metaData.getSuperTypes() : Collections.<IArtifactType> emptyList();
   }

   public Collection<IArtifactType> getDescendantTypes(IArtifactType artifactType) {
      ArtifactTypeMetaData metaData = tokenToTypeData.get(artifactType);
      return metaData != null ? metaData.getDescendantTypes() : Collections.<IArtifactType> emptyList();
   }

   public boolean hasSuperArtifactTypes(IArtifactType artType) {
      return !getSuperTypes(artType).isEmpty();
   }

   public boolean inheritsFrom(IArtifactType thisType, IArtifactType... otherTypes) {
      boolean result = false;
      for (IArtifactType otherType : otherTypes) {
         if (inheritsFromSingle(thisType, otherType)) {
            result = true;
            break;
         }
      }
      return result;
   }

   private boolean inheritsFromSingle(IArtifactType thisType, IArtifactType otherType) {
      boolean result = false;
      if (thisType.equals(otherType)) {
         result = true;
      } else {
         for (IArtifactType superType : getSuperTypes(thisType)) {
            if (inheritsFrom(superType, otherType)) {
               result = true;
               break;
            }
         }
      }
      return result;
   }

   public Collection<IAttributeType> getAttributeTypes(IArtifactType artType, IOseeBranch branch) throws OseeCoreException {
      Set<IAttributeType> attributeTypes = Sets.newLinkedHashSet();
      getAttributeTypes(attributeTypes, artType, branch);
      return attributeTypes;
   }

   private void getAttributeTypes(Set<IAttributeType> attributeTypes, IArtifactType artifactType, IOseeBranch branch) throws OseeCoreException {
      ArtifactTypeMetaData metaData = tokenToTypeData.get(artifactType);
      if (metaData != null) {
         Map<IOseeBranch, Collection<IAttributeType>> validityMap = metaData.getAttributeTypes();

         Iterable<? extends IOseeBranch> branches = hierarchyProvider.getParentHierarchy(branch);
         for (IOseeBranch parent : branches) {
            Collection<IAttributeType> items = validityMap.get(parent);
            if (items != null) {
               attributeTypes.addAll(items);
            }
         }
      }
      for (IArtifactType superType : getSuperTypes(artifactType)) {
         getAttributeTypes(attributeTypes, superType, branch);
      }
   }

   private final class ArtifactTypeMetaData {
      private final IArtifactType type;
      private Set<IArtifactType> superTypes;
      private final Set<IArtifactType> descendantTypes;
      private Map<IOseeBranch, Collection<IAttributeType>> attributeTypes;

      public ArtifactTypeMetaData(IArtifactType type) {
         super();
         this.type = type;
         superTypes = Collections.emptySet();
         descendantTypes = Sets.newLinkedHashSet();
         attributeTypes = Collections.emptyMap();
      }

      public void setSuperTypes(Set<IArtifactType> newSuperTypes) {
         Set<IArtifactType> originals = Sets.newHashSet(superTypes);
         superTypes = Sets.newHashSet(newSuperTypes);
         for (IArtifactType superType : superTypes) {
            ArtifactTypeMetaData metaData = tokenToTypeData.get(superType);
            if (metaData != null) {
               metaData.getDescendantTypes().add(type);
            }
         }
         for (IArtifactType oldValue : originals) {
            ArtifactTypeMetaData metaData = tokenToTypeData.get(oldValue);
            if (metaData != null) {
               metaData.getDescendantTypes().remove(type);
            }
         }
      }

      public void setAttributeTypes(Map<IOseeBranch, Collection<IAttributeType>> attributes) {
         this.attributeTypes = attributes;
      }

      public Set<IArtifactType> getSuperTypes() {
         return superTypes;
      }

      public Set<IArtifactType> getDescendantTypes() {
         return descendantTypes;
      }

      public Map<IOseeBranch, Collection<IAttributeType>> getAttributeTypes() {
         return attributeTypes;
      }

   }
}