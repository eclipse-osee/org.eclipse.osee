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
package org.eclipse.osee.framework.core.model;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.internal.fields.ArtifactSuperTypeField;
import org.eclipse.osee.framework.core.internal.fields.ArtifactTypeAttributesField;
import org.eclipse.osee.framework.core.internal.fields.OseeField;

/**
 * @author Robert A. Fisher
 */
public final class ArtifactType extends AbstractOseeType implements Comparable<ArtifactType>, IArtifactType {

   public static final String ARTIFACT_IS_ABSTRACT_FIELD_KEY = "osee.artifact.type.is.abstract.field";
   public static final String ARTIFACT_INHERITANCE_FIELD_KEY = "osee.artifact.type.inheritance.field";
   public static final String ARTIFACT_TYPE_ATTRIBUTES_FIELD_KEY = "osee.artifact.type.attributes.field";

   private final Set<ArtifactType> superTypes = new HashSet<ArtifactType>();
   private final Set<ArtifactType> childTypes = new HashSet<ArtifactType>();
   private final Map<Branch, Collection<AttributeType>> attributes = new HashMap<Branch, Collection<AttributeType>>();

   public ArtifactType(String guid, String name, boolean isAbstract) {
      super(guid, name);
      initializeFields();
      setAbstract(isAbstract);
   }

   protected void initializeFields() {
      addField(ARTIFACT_IS_ABSTRACT_FIELD_KEY, new OseeField<Boolean>());
      addField(ARTIFACT_INHERITANCE_FIELD_KEY, new ArtifactSuperTypeField(this, superTypes));
      addField(ARTIFACT_TYPE_ATTRIBUTES_FIELD_KEY, new ArtifactTypeAttributesField(attributes));
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
      Set<ArtifactType> originals = new HashSet<ArtifactType>(superTypes);
      setField(ARTIFACT_INHERITANCE_FIELD_KEY, superType);
      for (ArtifactType supers : superType) {
         supers.childTypes.add(this);
      }
      for (ArtifactType oldValue : originals) {
         if (!superType.contains(oldValue)) {
            oldValue.childTypes.remove(this);
         }
      }
   }

   public Collection<ArtifactType> getFirstLevelDescendantTypes() throws OseeCoreException {
      return getDescendants(this, false);
   }

   public Collection<ArtifactType> getAllDescendantTypes() throws OseeCoreException {
      return getDescendants(this, true);
   }

   private Collection<ArtifactType> getDescendants(ArtifactType artifactType, boolean isRecursionAllowed) throws OseeCoreException {
      Collection<ArtifactType> descendants = new HashSet<ArtifactType>();
      populateDescendants(artifactType, descendants, isRecursionAllowed);
      return descendants;
   }

   private void populateDescendants(ArtifactType artifactType, Collection<ArtifactType> descendants, boolean isRecursionAllowed) throws OseeCoreException {
      for (ArtifactType type : artifactType.childTypes) {
         if (isRecursionAllowed) {
            populateDescendants(type, descendants, isRecursionAllowed);
         }
         descendants.add(type);
      }
   }

   public void setAttributeTypes(Collection<AttributeType> attributeTypes, Branch branch) throws OseeCoreException {
      setField(ARTIFACT_TYPE_ATTRIBUTES_FIELD_KEY, Collections.singletonMap(branch, attributeTypes));
   }

   public boolean isValidAttributeType(AttributeType attributeType, Branch branch) throws OseeCoreException {
      return getAttributeTypes(branch).contains(attributeType);
   }

   public Map<Branch, Collection<AttributeType>> getLocalAttributeTypes() throws OseeCoreException {
      return getFieldValue(ARTIFACT_TYPE_ATTRIBUTES_FIELD_KEY);
   }

   private Collection<AttributeType> getLocalAttributeTypes(Branch branch) throws OseeCoreException {
      Collection<AttributeType> values = getLocalAttributeTypes().get(branch);
      return values == null ? Collections.<AttributeType> emptyList() : values;
   }

   public Collection<AttributeType> getAttributeTypes(Branch branch) throws OseeCoreException {
      // Do not use ARTIFACT_TYPE_ATTRIBUTES_FIELD for this call since it must use branch inheritance to get all attribute types
      Set<AttributeType> attributeTypes = new HashSet<AttributeType>();
      getAttributeTypes(attributeTypes, this, branch);
      return attributeTypes;
   }

   private void getAttributeTypes(Set<AttributeType> attributeTypes, ArtifactType artifactType, Branch branch) throws OseeCoreException {
      Branch branchCursor = branch;
      while (true) {
         Collection<AttributeType> items = artifactType.getLocalAttributeTypes(branchCursor);
         if (items != null) {
            attributeTypes.addAll(items);
         }
         if (branchCursor.getBranchType().isSystemRootBranch()) {
            break;
         } else {
            branchCursor = branchCursor.getParentBranch();
         }
      }

      for (ArtifactType superType : artifactType.getSuperArtifactTypes()) {
         getAttributeTypes(attributeTypes, superType, branch);
      }
   }

   public boolean isAbstract() {
      return getFieldValueLogException(false, ARTIFACT_IS_ABSTRACT_FIELD_KEY);
   }

   public void setAbstract(boolean isAbstract) {
      setFieldLogException(ARTIFACT_IS_ABSTRACT_FIELD_KEY, isAbstract);
   }

   /**
    * Determines if this artifact type equals, or is a sub-type of,
    * the artifact type specified by the <code>otherType</code> parameter.
    * 
    * @param otherType artifact type to check against
    * @return whether this artifact type inherits from otherType
    */
   public boolean inheritsFrom(IArtifactType otherType) {
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
