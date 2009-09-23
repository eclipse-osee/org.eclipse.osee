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

import java.util.Collection;
import java.util.Set;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeTypeDoesNotExist;
import org.eclipse.osee.framework.skynet.core.IOseeType;
import org.eclipse.osee.framework.skynet.core.artifact.factory.ArtifactFactoryManager;
import org.eclipse.osee.framework.skynet.core.attribute.AttributeType;
import org.eclipse.osee.framework.skynet.core.types.AbstractOseeCache;
import org.eclipse.osee.framework.skynet.core.types.ArtifactTypeCache;

/**
 * @author Robert A. Fisher
 */
public class ArtifactType extends AbstractOseeType implements Comparable<ArtifactType> {
   private boolean isAbstract;
   private final ArtifactFactoryManager factoryManager;
   private final DirtyStateDetail dirtyStateDetail;

   public ArtifactType(AbstractOseeCache<ArtifactType> cache, String guid, String name, boolean isAbstract, ArtifactFactoryManager factoryManager) {
      super(cache, guid, name);
      this.dirtyStateDetail = new DirtyStateDetail();
      setAbstract(isAbstract);
      this.factoryManager = factoryManager;
   }

   @Override
   protected ArtifactTypeCache getCache() {
      return (ArtifactTypeCache) super.getCache();
   }

   public DirtyStateDetail getDirtyDetails() {
      return dirtyStateDetail;
   }

   public Collection<ArtifactType> getSuperArtifactTypes() {
      return getCache().getArtifactSuperType(this);
   }

   public boolean hasSuperArtifactTypes() {
      Collection<ArtifactType> superTypes = getSuperArtifactTypes();
      return superTypes != null && !superTypes.isEmpty();
   }

   public void setSuperType(Set<ArtifactType> superType) throws OseeCoreException {
      Collection<ArtifactType> original = getSuperArtifactTypes();
      getCache().setArtifactSuperType(this, superType);
      Collection<ArtifactType> newTypes = getSuperArtifactTypes();
      getDirtyDetails().setIsInheritanceDirty(isDifferent(original, newTypes));
   }

   public void setAttributeTypeValidity(Collection<AttributeType> attributeTypes, Branch branch) throws OseeCoreException {
      Collection<AttributeType> original = getCache().getLocalAttributeTypes(this, branch);
      getCache().cacheTypeValidity(this, attributeTypes, branch);
      Collection<AttributeType> newTypes = getCache().getLocalAttributeTypes(this, branch);
      getDirtyDetails().setIsAttributeTypeValidatityDirty(isDifferent(original, newTypes));
   }

   public boolean isValidAttributeType(AttributeType attributeType, Branch branch) throws OseeCoreException {
      return getAttributeTypes(branch).contains(attributeType);
   }

   public Set<AttributeType> getAttributeTypes(Branch branch) throws OseeCoreException {
      return getCache().getAttributeTypes(this, branch);
   }

   public boolean isAbstract() {
      return isAbstract;
   }

   /**
    * Get a new instance of the type of artifact described by this descriptor. This is just a convenience method that
    * calls makeNewArtifact on the known factory with this descriptor for the descriptor parameter, and the supplied
    * branch.
    * 
    * @return Return artifact reference
    * @throws OseeCoreException
    * @see ArtifactFactory#makeNewArtifact(Branch, ArtifactType)
    * @use {@link ArtifactTypeManager}.addArtifact
    */
   public Artifact makeNewArtifact(Branch branch) throws OseeCoreException {
      return getFactory().makeNewArtifact(branch, this, null, null, null);
   }

   /**
    * Get a new instance of the type of artifact described by this descriptor. This is just a convenience method that
    * calls makeNewArtifact on the known factory with this descriptor for the descriptor parameter, and the supplied
    * branch.
    * 
    * @param branch branch on which artifact will be created
    * @return Return artifact reference
    * @throws OseeCoreException
    * @see ArtifactFactory#makeNewArtifact(Branch, ArtifactType, String, String, ArtifactProcessor)
    * @use {@link ArtifactTypeManager}.addArtifact
    */
   public Artifact makeNewArtifact(Branch branch, String guid, String humandReadableId) throws OseeCoreException {
      return getFactory().makeNewArtifact(branch, this, guid, humandReadableId, null);
   }

   /**
    * @return Returns the factory.
    */
   public ArtifactFactory getFactory() throws OseeCoreException {
      return factoryManager.getFactory(getName());
   }

   /**
    * Determines if this artifact type equals, or is a sub-type of,
    * the artifact type specified by the <code>otherType</code> parameter.
    * 
    * @param otherType artifact type to check against
    * @return whether this artifact type inherits from otherType
    */
   public boolean inheritsFrom(IOseeType otherType) {
      if (this.equals(otherType)) {
         return true;
      }
      for (ArtifactType superType : getSuperArtifactTypes()) {
         if (superType.inheritsFrom(otherType)) {
            return true;
         }
      }
      return false;
   }

   /**
    * Determines if this artifact type equals, or is a sub-type of,
    * the artifact type specified by the <code>otherType</code> parameter.
    * 
    * @param otherType artifact type to check against
    * @return whether this artifact type inherits from otherType
    * @throws OseeCoreException
    */
   public boolean inheritsFrom(String artifactTypeName) throws OseeCoreException {
      ArtifactType artifactType = getCache().getUniqueByName(artifactTypeName);
      if (artifactType == null) {
         throw new OseeTypeDoesNotExist("Artifact type [" + artifactTypeName + "] is not available.");
      }
      return inheritsFrom(artifactType);
   }

   @Override
   public String toString() {
      return getName();
   }

   public int compareTo(ArtifactType other) {
      int result = -1;
      if (other != null && other.getName() != null && getName() != null) {
         result = getName().compareTo(other.getName());
      }
      return result;
   }

   public void setAbstract(boolean isAbstract) {
      getDirtyDetails().updateAbstract(isAbstract);
      this.isAbstract = isAbstract;
   }

   @Override
   public boolean isDirty() {
      return getDirtyDetails().isDirty();
   }

   @Override
   public void clearDirty() {
      getDirtyDetails().clear();
   }

   public final class DirtyStateDetail {
      private boolean isInheritanceDirty;
      private boolean isAttributeTypeValidatityDirty;
      private boolean isAbstractDirty;

      private DirtyStateDetail() {
      }

      public void setIsAttributeTypeValidatityDirty(boolean different) {
         isAttributeTypeValidatityDirty |= different;
      }

      public void setIsInheritanceDirty(boolean different) {
         isInheritanceDirty |= different;
      }

      private void updateAbstract(boolean isAbstract) {
         isAbstractDirty |= isDifferent(isAbstract(), isAbstract);
      }

      public boolean isInheritanceDirty() {
         return isInheritanceDirty;
      }

      public boolean isAttributeTypeValidityDirty() {
         return isAttributeTypeValidatityDirty;
      }

      public boolean isAbstractDirty() {
         return isAbstractDirty;
      }

      public boolean isNameDirty() {
         return ArtifactType.super.isDirty();
      }

      public boolean isDirty() {
         return isNameDirty() || isAbstractDirty() || //
         isInheritanceDirty() || isAttributeTypeValidityDirty();
      }

      private void clear() {
         ArtifactType.super.clearDirty();
         isInheritanceDirty = false;
         isAttributeTypeValidatityDirty = false;
         isAbstractDirty = false;
      }
   }
}
