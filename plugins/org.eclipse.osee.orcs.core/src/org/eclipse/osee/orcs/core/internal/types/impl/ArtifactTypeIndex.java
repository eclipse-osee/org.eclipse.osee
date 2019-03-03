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

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import org.eclipse.osee.framework.core.data.ArtifactTypeId;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.dsl.oseeDsl.XArtifactType;
import org.eclipse.osee.orcs.core.internal.types.BranchHierarchyProvider;

/**
 * @author Roberto E. Escobar
 */
public class ArtifactTypeIndex extends TokenTypeIndex<ArtifactTypeToken, XArtifactType> {

   private final Map<ArtifactTypeId, ArtifactTypeMetaData> tokenToTypeData;
   private final BranchHierarchyProvider hierarchyProvider;

   public ArtifactTypeIndex(BranchHierarchyProvider hierarchyProvider) {
      super();
      this.tokenToTypeData = Maps.newHashMap();
      this.hierarchyProvider = hierarchyProvider;
   }

   public void put(ArtifactTypeToken type, Set<ArtifactTypeToken> superTypes) {
      ArtifactTypeMetaData metaData = getOrCreateData(type);
      metaData.setSuperTypes(superTypes);
   }

   public void put(ArtifactTypeToken type, Map<BranchId, Collection<AttributeTypeToken>> attributes) {
      ArtifactTypeMetaData metaData = getOrCreateData(type);
      metaData.setAttributeTypes(attributes);
   }

   private ArtifactTypeMetaData getOrCreateData(ArtifactTypeToken type) {
      ArtifactTypeMetaData metaData = tokenToTypeData.get(type);
      if (metaData == null) {
         metaData = new ArtifactTypeMetaData(type);
         tokenToTypeData.put(type, metaData);
      }
      return metaData;
   }

   public Map<BranchId, Collection<AttributeTypeToken>> getAllAttributeTypes(ArtifactTypeId artifactType) {
      ArtifactTypeMetaData metaData = tokenToTypeData.get(artifactType);
      return metaData.attributeTypes;
   }

   public Collection<ArtifactTypeToken> getSuperTypes(ArtifactTypeId artifactType) {
      ArtifactTypeMetaData metaData = tokenToTypeData.get(artifactType);
      return metaData != null ? metaData.getSuperTypes() : Collections.emptyList();
   }

   public Collection<ArtifactTypeToken> getDescendantTypes(ArtifactTypeId artifactType) {
      ArtifactTypeMetaData metaData = tokenToTypeData.get(artifactType);
      return metaData != null ? metaData.getDescendantTypes() : Collections.emptyList();
   }

   public boolean hasSuperArtifactTypes(ArtifactTypeId artType) {
      return !getSuperTypes(artType).isEmpty();
   }

   public boolean inheritsFrom(ArtifactTypeId thisType, ArtifactTypeId... otherTypes) {
      boolean result = false;
      for (ArtifactTypeId otherType : otherTypes) {
         if (inheritsFromSingle(thisType, otherType)) {
            result = true;
            break;
         }
      }
      return result;
   }

   private boolean inheritsFromSingle(ArtifactTypeId thisType, ArtifactTypeId otherType) {
      boolean result = false;
      if (thisType.equals(otherType)) {
         result = true;
      } else {
         for (ArtifactTypeId superType : getSuperTypes(thisType)) {
            if (inheritsFrom(superType, otherType)) {
               result = true;
               break;
            }
         }
      }
      return result;
   }

   public Collection<AttributeTypeToken> getAttributeTypes(ArtifactTypeId artType, BranchId branch) {
      Set<AttributeTypeToken> attributeTypes = Sets.newLinkedHashSet();
      getAttributeTypes(attributeTypes, artType, branch);
      return attributeTypes;
   }

   private void getAttributeTypes(Set<AttributeTypeToken> attributeTypes, ArtifactTypeId artifactType, BranchId branch) {
      ArtifactTypeMetaData metaData = tokenToTypeData.get(artifactType);
      if (metaData != null) {
         Map<BranchId, Collection<AttributeTypeToken>> validityMap = metaData.getAttributeTypes();

         Iterable<? extends BranchId> branches = hierarchyProvider.getParentHierarchy(branch);
         for (BranchId parent : branches) {
            Collection<AttributeTypeToken> items = validityMap.get(parent);
            if (items != null) {
               attributeTypes.addAll(items);
            }
         }
      }
      for (ArtifactTypeId superType : getSuperTypes(artifactType)) {
         getAttributeTypes(attributeTypes, superType, branch);
      }
   }

   private final class ArtifactTypeMetaData {
      private final ArtifactTypeToken type;
      private Set<ArtifactTypeToken> superTypes;
      private final Set<ArtifactTypeToken> descendantTypes;
      private Map<BranchId, Collection<AttributeTypeToken>> attributeTypes;

      public ArtifactTypeMetaData(ArtifactTypeToken type) {
         super();
         this.type = type;
         superTypes = Collections.emptySet();
         descendantTypes = Sets.newLinkedHashSet();
         attributeTypes = Collections.emptyMap();
      }

      public void setSuperTypes(Set<ArtifactTypeToken> newSuperTypes) {
         Set<ArtifactTypeToken> originals = Sets.newHashSet(superTypes);
         superTypes = Sets.newHashSet(newSuperTypes);
         for (ArtifactTypeToken superType : superTypes) {
            ArtifactTypeMetaData metaData = getOrCreateData(superType);
            if (metaData != null) {
               metaData.getDescendantTypes().add(type);
            }
         }
         for (ArtifactTypeToken oldValue : originals) {
            ArtifactTypeMetaData metaData = getOrCreateData(oldValue);
            if (metaData != null) {
               metaData.getDescendantTypes().remove(type);
            }
         }
      }

      public void setAttributeTypes(Map<BranchId, Collection<AttributeTypeToken>> attributes) {
         this.attributeTypes = attributes;
      }

      public Set<ArtifactTypeToken> getSuperTypes() {
         return superTypes;
      }

      public Set<ArtifactTypeToken> getDescendantTypes() {
         return descendantTypes;
      }

      public Map<BranchId, Collection<AttributeTypeToken>> getAttributeTypes() {
         return attributeTypes;
      }

   }
}