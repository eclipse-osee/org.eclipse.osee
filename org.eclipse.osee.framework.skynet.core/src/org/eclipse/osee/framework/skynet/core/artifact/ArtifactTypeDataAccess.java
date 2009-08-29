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
package org.eclipse.osee.framework.skynet.core.artifact;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.attribute.AttributeType;

/**
 * @author Roberto E. Escobar
 */
@Deprecated
public final class ArtifactTypeDataAccess implements IArtifactTypeDataAccess {
   private final OseeTypeCache oseeTypeCache;
   private final IOseeTypeDataAccessor dataAccessor;

   public ArtifactTypeDataAccess(IOseeTypeDataAccessor dataAccessor, OseeTypeCache oseeTypeCache) {
      this.dataAccessor = dataAccessor;
      this.oseeTypeCache = oseeTypeCache;
   }

   @Override
   public Collection<AttributeType> getAttributeTypesFor(ArtifactType artifactType, Branch branch) throws OseeCoreException {
      dataAccessor.ensureTypeValidityPopulated();
      Collection<AttributeType> toReturn = new HashSet<AttributeType>();
      getAttributeTypesByBranch(artifactType, branch, toReturn);
      return toReturn;
   }

   private void getAttributeTypesByBranch(ArtifactType currentType, Branch branch, Collection<AttributeType> attributeTypes) throws OseeCoreException {
      Branch branchCursor = branch;
      boolean notDone = true;
      while (notDone) {
         Collection<AttributeType> items = oseeTypeCache.getAttributeTypes(currentType, branch);
         if (items != null) {
            attributeTypes.addAll(items);
         }
         if (branchCursor.isSystemRootBranch()) {
            notDone = false;
         } else {
            branchCursor = branchCursor.getParentBranch();
         }
      }
   }

   @Override
   public Collection<AttributeType> getAttributeTypesFor(ArtifactType artifactType) throws OseeCoreException {
      dataAccessor.ensureTypeValidityPopulated();
      return oseeTypeCache.getAttributeTypes(artifactType);
   }

   @Override
   public void setAttributeTypes(ArtifactType artifactType, Collection<AttributeType> attributeTypes, Branch branch) throws OseeCoreException {
      dataAccessor.ensureTypeValidityPopulated();
      Collection<AttributeType> existingTypes = getAttributeTypesFor(artifactType, branch);
      List<Object[]> datas = new ArrayList<Object[]>();
      for (AttributeType attributeType : attributeTypes) {
         if (!existingTypes.contains(attributeType)) {
            datas.add(new Object[] {artifactType.getArtTypeId(), attributeType.getAttrTypeId(), branch.getBranchId()});
         }
      }
      if (!datas.isEmpty()) {
         dataAccessor.storeValidity(datas);
         oseeTypeCache.cacheTypeValidity(artifactType, attributeTypes, branch);
      }
   }

   @Override
   public Collection<ArtifactType> getArtifactSuperTypesFor(ArtifactType artifactType) throws OseeCoreException {
      dataAccessor.ensureArtifactTypePopulated();
      return oseeTypeCache.getArtifactSuperType(artifactType);
   }

   @Override
   public void setArtifactSuperTypeFor(ArtifactType artifactType, Collection<ArtifactType> superTypes) {
      // ADD CHECKS before caching 

      oseeTypeCache.cacheArtifactTypeInheritance(artifactType, superType);
   }

   @Override
   public Collection<ArtifactType> getDescendants(ArtifactType artifactType, boolean recurse) throws OseeCoreException {
      dataAccessor.ensureArtifactTypePopulated();
      Set<ArtifactType> children = new HashSet<ArtifactType>();
      getDescendantsHelper(artifactType, children, recurse);
      return children;
   }

   private void getDescendantsHelper(ArtifactType parentType, Collection<ArtifactType> children, boolean recurse) throws OseeCoreException {
      for (ArtifactType itemToCheck : oseeTypeCache.getAllArtifactTypes()) {
         if (itemToCheck.getSuperArtifactTypes().contains(parentType)) {
            children.add(itemToCheck);
            if (recurse) {
               getDescendantsHelper(itemToCheck, children, recurse);
            }
         }
      }
   }

}
