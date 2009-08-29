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

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.factory.ArtifactFactoryManager;
import org.eclipse.osee.framework.skynet.core.attribute.AttributeType;

/**
 * @author Robert A. Fisher
 */
public class ArtifactType implements Serializable, Comparable<ArtifactType> {
   private static final long serialVersionUID = 1L;
   private int artTypeId;
   private String name;
   private final boolean isAbstract;
   private final ArtifactFactoryManager factoryManager;
   private final ArrayList<ArtifactType> superTypes = new ArrayList<ArtifactType>(1);

   public ArtifactType(boolean isAbstract, String name, ArtifactFactoryManager factoryManager) {
      this.name = name;
      this.isAbstract = isAbstract;
      this.factoryManager = factoryManager;
   }

   public void addSuperType(ArtifactType superType) {
      superTypes.add(superType);
   }

   public boolean isValidAttributeType(AttributeType attributeType, Branch branch) throws OseeCoreException {
      return getAttributeTypes(branch).contains(attributeType);
   }

   public Set<AttributeType> getAttributeTypes(Branch branch) throws OseeCoreException {
      Set<AttributeType> attributeTypes = new HashSet<AttributeType>();
      populateInheritedAttributeTypes(this, branch, attributeTypes);
      return attributeTypes;
   }

   private void populateInheritedAttributeTypes(ArtifactType currentType, Branch branch, Collection<AttributeType> attributeTypes) throws OseeCoreException {
      if (branch != null) {
         attributeTypes.addAll(cacheAccessor.getAttributeTypesFor(currentType, branch));
      } else {
         attributeTypes.addAll(cacheAccessor.getAttributeTypesFor(currentType));
      }
      System.out.println("currentType : " + currentType.getName() + " " + attributeTypes);
      if (hasSuperArtifactTypes()) {
         for (ArtifactType superType : getSuperArtifactTypes()) {
            if (!currentType.equals(superType)) {
               populateInheritedAttributeTypes(superType, branch, attributeTypes);
            }
         }
      }
   }

   public boolean isAbstract() {
      return isAbstract;
   }

   public Collection<ArtifactType> getSuperArtifactTypes() {
      return superTypes;
   }

   public boolean hasSuperArtifactTypes() throws OseeCoreException {
      Collection<ArtifactType> superTypes = getSuperArtifactTypes();
      return superTypes != null && !superTypes.isEmpty();
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
    * @return Returns the artTypeId.
    */
   public int getArtTypeId() {
      return artTypeId;
   }

   /**
    * @return Returns the factory.
    */
   public ArtifactFactory getFactory() throws OseeCoreException {
      return factoryManager.getFactory(name);
   }

   /**
    * @return Returns the name.
    */
   public String getName() {
      return name;
   }

   /**
    * Determines if this artifact type equals, or is a sub-type of,
    * the artifact type specified by the <code>otherType</code> parameter.
    * 
    * @param otherType artifact type to check against
    * @return whether this artifact type inherits from otherType
    */
   public boolean inheritsFrom(ArtifactType otherType) {
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
      return inheritsFrom(ArtifactTypeManager.getType(artifactTypeName));
   }

   @Override
   public String toString() {
      return name;
   }

   /**
    * Only store the identifying data since a manager controls this object.
    * 
    * @param stream
    * @throws IOException
    */
   private void writeObject(ObjectOutputStream stream) throws IOException {
      stream.writeObject(name);
   }

   /**
    * Initialize as a dumb object for portraying the object needed
    * 
    * @param stream
    * @throws IOException
    * @throws ClassNotFoundException
    */
   private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
      name = (String) stream.readObject();
   }

   /**
    * @return The ArtifactSubtypeDescriptor associated with the name and transactionId from this dumb object
    * @throws ObjectStreamException
    */
   private Object readResolve() throws ObjectStreamException {
      try {
         return ArtifactTypeManager.getType(name);
      } catch (OseeCoreException e) {
         throw new RuntimeException("Error while resolving descriptor", e);
      }
   }

   public int compareTo(ArtifactType artifactType) {
      if (artifactType == null) {
         return -1;
      }
      return name.compareTo(artifactType.name);
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + name.hashCode();
      return result;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      }
      if (obj == null) {
         return false;
      }
      if (getClass() != obj.getClass()) {
         return false;
      }
      final ArtifactType other = (ArtifactType) obj;
      if (name == null) {
         if (other.name != null) {
            return false;
         }
      } else if (!name.equals(other.name)) {
         return false;
      }
      return true;
   }

}
