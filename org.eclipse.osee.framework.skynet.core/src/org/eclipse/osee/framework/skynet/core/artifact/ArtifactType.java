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
import org.eclipse.osee.framework.skynet.core.types.ArtifactTypeCache;

/**
 * @author Robert A. Fisher
 */
public class ArtifactType extends BaseOseeType implements Comparable<ArtifactType> {
   private boolean isAbstract;
   private final ArtifactFactoryManager factoryManager;
   private final ArtifactTypeCache cache;
   private boolean isInheritanceDirty;
   private boolean isAttributeTypeValidatityDirty;

   public ArtifactType(String guid, String name, boolean isAbstract, ArtifactFactoryManager factoryManager, ArtifactTypeCache cache) {
      super(guid, name);
      isInheritanceDirty = false;
      isAttributeTypeValidatityDirty = false;
      this.isAbstract = isAbstract;
      this.factoryManager = factoryManager;
      this.cache = cache;
   }

   public Collection<ArtifactType> getSuperArtifactTypes() {
      return cache.getArtifactSuperType(this);
   }

   public boolean hasSuperArtifactTypes() {
      Collection<ArtifactType> superTypes = getSuperArtifactTypes();
      return superTypes != null && !superTypes.isEmpty();
   }

   public void addSuperType(Set<ArtifactType> superType) throws OseeCoreException {
      Collection<ArtifactType> original = getSuperArtifactTypes();
      cache.setArtifactSuperType(this, superType);
      Collection<ArtifactType> newTypes = getSuperArtifactTypes();
      isInheritanceDirty = isDifferent(original, newTypes);
   }

   public void setAttributeTypeValidity(Collection<AttributeType> attributeTypes, Branch branch) throws OseeCoreException {
      Collection<AttributeType> original = cache.getLocalAttributeTypes(this, branch);
      cache.cacheTypeValidity(this, attributeTypes, branch);
      Collection<AttributeType> newTypes = cache.getLocalAttributeTypes(this, branch);
      isAttributeTypeValidatityDirty = isDifferent(original, newTypes);
   }

   public boolean isValidAttributeType(AttributeType attributeType, Branch branch) throws OseeCoreException {
      return getAttributeTypes(branch).contains(attributeType);
   }

   public Set<AttributeType> getAttributeTypes(Branch branch) throws OseeCoreException {
      return cache.getAttributeTypes(this, branch);
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
      ArtifactType artifactType = cache.getTypeByName(artifactTypeName);
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
      updateDirty(this.isAbstract, isAbstract);
      this.isAbstract = isAbstract;
   }

   public boolean isDataDirty() {
      return super.isDirty();
   }

   public boolean isInheritanceDirty() {
      return isInheritanceDirty;
   }

   public boolean isAttributeTypeValidityDirty() {
      return isAttributeTypeValidatityDirty;
   }

   @Override
   public boolean isDirty() {
      return isDataDirty() || isInheritanceDirty() || isAttributeTypeValidityDirty();
   }

   @Override
   public void persist() {
      super.persist();
      isInheritanceDirty = false;
      isAttributeTypeValidatityDirty = false;
   }

}
