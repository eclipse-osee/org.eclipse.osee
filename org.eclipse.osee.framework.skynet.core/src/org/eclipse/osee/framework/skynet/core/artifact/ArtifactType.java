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
import java.util.Collections;
import java.util.Set;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeTypeDoesNotExist;
import org.eclipse.osee.framework.skynet.core.IOseeType;
import org.eclipse.osee.framework.skynet.core.artifact.factory.ArtifactFactoryManager;
import org.eclipse.osee.framework.skynet.core.attribute.AttributeType;
import org.eclipse.osee.framework.skynet.core.types.AbstractOseeCache;
import org.eclipse.osee.framework.skynet.core.types.AbstractOseeType;
import org.eclipse.osee.framework.skynet.core.types.ArtifactTypeCache;
import org.eclipse.osee.framework.skynet.core.types.field.ArtifactSuperTypeField;
import org.eclipse.osee.framework.skynet.core.types.field.ArtifactTypeAttributesField;
import org.eclipse.osee.framework.skynet.core.types.field.OseeField;

/**
 * @author Robert A. Fisher
 */
public class ArtifactType extends AbstractOseeType implements Comparable<ArtifactType> {
   public static final String ARTIFACT_IS_ABSTRACT_FIELD_KEY = "osee.artifact.type.is.abstract.field";
   public static final String ARTIFACT_INHERITANCE_FIELD_KEY = "osee.artifact.type.inheritance.field";
   public static final String ARTIFACT_TYPE_ATTRIBUTES_FIELD_KEY = "osee.artifact.type.attributes.field";

   private final ArtifactFactoryManager factoryManager;

   public ArtifactType(AbstractOseeCache<ArtifactType> cache, String guid, String name, boolean isAbstract, ArtifactFactoryManager factoryManager) {
      super(cache, guid, name);
      setAbstract(isAbstract);
      this.factoryManager = factoryManager;
   }

   @Override
   protected void initializeFields() {
      addField(ARTIFACT_IS_ABSTRACT_FIELD_KEY, new OseeField<Boolean>());
      addField(ARTIFACT_INHERITANCE_FIELD_KEY, new ArtifactSuperTypeField(getCache(), this));
      addField(ARTIFACT_TYPE_ATTRIBUTES_FIELD_KEY, new ArtifactTypeAttributesField(getCache(), this));
   }

   @Override
   protected ArtifactTypeCache getCache() {
      return (ArtifactTypeCache) super.getCache();
   }

   public boolean hasSuperArtifactTypes() throws OseeCoreException {
      Collection<ArtifactType> superTypes = getSuperArtifactTypes();
      return superTypes != null && !superTypes.isEmpty();
   }

   public Collection<ArtifactType> getSuperArtifactTypes() {
      Collection<ArtifactType> defaultValue = Collections.emptyList();
      return getFieldValueLogException(defaultValue, ARTIFACT_INHERITANCE_FIELD_KEY);
   }

   public void setSuperType(Set<ArtifactType> superType) throws OseeCoreException {
      setField(ARTIFACT_INHERITANCE_FIELD_KEY, superType);
   }

   public Collection<ArtifactType> getFirstLevelDescendantTypes() throws OseeCoreException {
      return getCache().getDescendants(this, false);
   }

   public Collection<ArtifactType> getAllDescendantTypes() throws OseeCoreException {
      return getCache().getDescendants(this, true);
   }

   public void setAttributeTypeValidity(Collection<AttributeType> attributeTypes, Branch branch) throws OseeCoreException {
      setField(ARTIFACT_TYPE_ATTRIBUTES_FIELD_KEY, Collections.singletonMap(branch, attributeTypes));
   }

   public boolean isValidAttributeType(AttributeType attributeType, Branch branch) throws OseeCoreException {
      return getAttributeTypes(branch).contains(attributeType);
   }

   public Collection<AttributeType> getAttributeTypes(Branch branch) throws OseeCoreException {
      // Do not use ARTIFACT_TYPE_ATTRIBUTES_FIELD for this call since it must use branch inheritance to get all attribute types
      return getCache().getAttributeTypes(this, branch);
   }

   public boolean isAbstract() {
      return getFieldValueLogException(false, ARTIFACT_IS_ABSTRACT_FIELD_KEY);
   }

   public void setAbstract(boolean isAbstract) {
      setFieldLogException(ARTIFACT_IS_ABSTRACT_FIELD_KEY, isAbstract);
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
}
