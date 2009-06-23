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
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.db.connection.exception.OseeDataStoreException;
import org.eclipse.osee.framework.skynet.core.artifact.factory.ArtifactFactoryManager;

/**
 * Description of an Artifact subtype. The descriptor can be used to create new artifacts that are of the type of this
 * descriptor. <br/>
 * <br/>
 * Descriptors can be acquired from the configuration manager.
 * 
 * @see org.eclipse.osee.framework.skynet.core.attribute.ConfigurationPersistenceManager
 * @author Robert A. Fisher
 */
public class ArtifactType implements Serializable, Comparable<ArtifactType> {
   private static final long serialVersionUID = 1L;
   private final int artTypeId;
   private String name;
   private final String namespace;

   ArtifactType(int artTypeId, String namespace, String name) throws OseeDataStoreException {
      this.artTypeId = artTypeId;
      this.name = name;
      this.namespace = namespace == null ? "" : namespace;
      ArtifactTypeManager.cache(this);
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
      return ArtifactFactoryManager.getFactory(name);
   }

   /**
    * @return Returns the name.
    */
   public String getName() {
      return name;
   }

   /**
    * determines if this artifact type is equal to or a subclass of the artifact type referenced by artifactTypeName
    * 
    * @return true if compatible
    */
   public boolean isTypeCompatible(String artifactTypeName) {
      return name.equals(artifactTypeName);
   }

   /**
    * Determine if this is the descriptor that produces the same type of artifact as an already existing artifact.
    * 
    * @param artifact The artifact to compare against.
    * @return <b>true</b> if and only if this descriptor will give you the same type of artifact.
    * @throws OseeCoreException
    */
   public boolean canProduceArtifact(Artifact artifact) throws OseeCoreException {
      return artifact.getArtTypeId() == artTypeId && getFactory() != null;
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

   /**
    * @return the namespace
    */
   public String getNamespace() {
      return namespace;
   }

   /* (non-Javadoc)
    * @see java.lang.Comparable#compareTo(java.lang.Object)
    */
   public int compareTo(ArtifactType artifactType) {
      if (artifactType == null) {
         return -1;
      }
      return name.compareTo(artifactType.name);
   }

   /* (non-Javadoc)
    * @see java.lang.Object#hashCode()
    */
   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + name.hashCode();
      result = prime * result + namespace.hashCode();
      return result;
   }

   /* (non-Javadoc)
    * @see java.lang.Object#equals(java.lang.Object)
    */
   @Override
   public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null) return false;
      if (getClass() != obj.getClass()) return false;
      final ArtifactType other = (ArtifactType) obj;
      if (name == null) {
         if (other.name != null) return false;
      } else if (!name.equals(other.name)) return false;
      if (namespace == null) {
         if (other.namespace != null) return false;
      } else if (!namespace.equals(other.namespace)) return false;
      return true;
   }

}
