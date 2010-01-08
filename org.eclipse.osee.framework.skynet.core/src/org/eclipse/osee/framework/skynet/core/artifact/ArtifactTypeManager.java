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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import org.eclipse.osee.framework.core.cache.ArtifactTypeCache;
import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.exception.OseeArgumentException;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeDataStoreException;
import org.eclipse.osee.framework.core.exception.OseeStateException;
import org.eclipse.osee.framework.core.exception.OseeTypeDoesNotExist;
import org.eclipse.osee.framework.core.model.ArtifactType;
import org.eclipse.osee.framework.core.model.AttributeType;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.database.core.ConnectionHandler;
import org.eclipse.osee.framework.database.core.DbTransaction;
import org.eclipse.osee.framework.database.core.OseeConnection;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.eclipse.osee.framework.skynet.core.artifact.factory.ArtifactFactoryManager;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.internal.Activator;

/**
 * Contains methods specific to artifact types. All artifact methods will eventually be moved from the
 * ConfigurationPersistenceManager to here.
 * 
 * @author Donald G. Dunne
 */
public class ArtifactTypeManager {

   private final static ArtifactFactoryManager factoryManager = new ArtifactFactoryManager();

   private ArtifactTypeManager() {
   }

   public static ArtifactTypeCache getCache() {
      return Activator.getInstance().getOseeCacheService().getArtifactTypeCache();
   }

   public static Collection<ArtifactType> getArtifactTypesFromAttributeType(AttributeType attributeType, Branch branch) throws OseeCoreException {
      List<ArtifactType> artifactTypes = new ArrayList<ArtifactType>();
      for (ArtifactType artifactType : getAllTypes()) {
         if (artifactType.getAttributeTypes(branch).contains(attributeType)) {
            artifactTypes.add(artifactType);
         }
      }
      return artifactTypes;
   }

   public static Collection<ArtifactType> getValidArtifactTypes(Branch branch) throws OseeCoreException {
      // TODO Filter artifact types by branch
      return getAllTypes();
   }

   public static Collection<ArtifactType> getConcreteArtifactTypes(Branch branch) throws OseeCoreException {
      Collection<ArtifactType> types = getAllTypes();
      Iterator<ArtifactType> iterator = types.iterator();
      while (iterator.hasNext()) {
         ArtifactType type = iterator.next();
         if (type.isAbstract()) {
            iterator.remove();
         }
      }
      return types;
   }

   /**
    * @return Returns all of the descriptors.
    * @throws OseeTypeDoesNotExist
    * @throws OseeCoreException
    */
   public static Collection<ArtifactType> getAllTypes() throws OseeCoreException {
      return getCache().getAll();
   }

   public static boolean typeExists(String name) throws OseeCoreException {
      return !getCache().getByName(name).isEmpty();
   }

   /**
    * @return Returns the artifact type matching the guid
    * @param guid artifact type guid to match
    * @throws OseeDataStoreException
    * @throws OseeTypeDoesNotExist
    */
   public static ArtifactType getTypeByGuid(String guid) throws OseeCoreException {
      if (!GUID.isValid(guid)) {
         throw new OseeArgumentException(String.format("[%s] is not a valid guid", guid));
      }
      ArtifactType artifactType = getCache().getByGuid(guid);
      if (artifactType == null) {
         throw new OseeTypeDoesNotExist("Artifact type [" + guid + "] is not available.");
      }
      return artifactType;
   }

   /**
    * @return Returns the artifact type matching the name
    * @param name artifact type name to match
    * @throws OseeDataStoreException
    * @throws OseeTypeDoesNotExist
    */
   public static ArtifactType getType(String name) throws OseeCoreException {
      ArtifactType artifactType = getCache().getUniqueByName(name);
      if (artifactType == null) {
         throw new OseeTypeDoesNotExist("Artifact type [" + name + "] is not available.");
      }
      return artifactType;
   }

   public static ArtifactType getType(IArtifactType typeToken) throws OseeCoreException {
      return getTypeByGuid(typeToken.getGuid());
   }

   public static int getTypeId(IArtifactType typeToken) throws OseeCoreException {
      return getType(typeToken).getId();
   }

   /**
    * Get Artifact Types by type names.
    * 
    * @return Returns the types with a particular name
    * @param artifactTypeNames names to get
    * @throws OseeDataStoreException
    * @throws OseeTypeDoesNotExist if any name in the artifactTypeNames does not match
    */
   public static List<IArtifactType> getTypes(Iterable<String> artifactTypeNames) throws OseeCoreException {
      List<IArtifactType> artifactTypes = new ArrayList<IArtifactType>();
      for (String artifactTypeName : artifactTypeNames) {
         artifactTypes.add(getType(artifactTypeName));
      }
      return artifactTypes;
   }

   /**
    * @return Returns the descriptor with a particular name, null if it does not exist.
    * @throws OseeTypeDoesNotExist
    */
   public static ArtifactType getType(int artTypeId) throws OseeCoreException {
      ArtifactType artifactType = getCache().getById(artTypeId);
      if (artifactType == null) {
         throw new OseeTypeDoesNotExist("Atrifact type: " + artTypeId + " is not available.");
      }
      return artifactType;
   }

   /**
    * Get a new instance of type artifactTypeName
    * 
    * @param artifactTypeName
    * @param branch
    * @throws OseeCoreException
    */
   public static Artifact addArtifact(String artifactTypeName, IOseeBranch branch) throws OseeCoreException {
      return makeNewArtifact(getType(artifactTypeName), branch);
   }

   public static Artifact addArtifact(IArtifactType artifactType, IOseeBranch branch) throws OseeCoreException {
      return makeNewArtifact(getType(artifactType), branch);
   }

   /**
    * Get a new instance of type artifactTypeName and set it's name.
    * 
    * @param artifactTypeName
    * @param branch
    * @param name
    */
   public static Artifact addArtifact(String artifactTypeName, IOseeBranch branch, String name) throws OseeCoreException {
      Artifact artifact = addArtifact(artifactTypeName, branch);
      artifact.setName(name);
      return artifact;
   }

   public static Artifact addArtifact(IArtifactType artifactType, IOseeBranch branch, String name) throws OseeCoreException {
      Artifact artifact = makeNewArtifact(getType(artifactType), branch);
      artifact.setName(name);
      return artifact;
   }

   /**
    * Get a new instance of the type of artifact. This is just a convenience method that calls makeNewArtifact on the
    * known factory with this descriptor for the descriptor parameter, and the supplied branch.
    * 
    * @param branch branch on which artifact will be created
    * @return Return artifact reference
    * @throws OseeCoreException
    * @see ArtifactFactory#makeNewArtifact(Branch, ArtifactType, String, String, ArtifactProcessor)
    */
   public static Artifact addArtifact(IArtifactType artifactType, Branch branch, String guid, String humandReadableId) throws OseeCoreException {
      return makeNewArtifact(ArtifactTypeManager.getType(artifactType), branch, guid, humandReadableId);
   }

   private static final String DELETE_VALID_ATTRIBUTE =
         "delete from osee_artifact_type_attributes where art_type_id = ?";
   private static final String COUNT_ARTIFACT_OCCURRENCE = "select count(1) FROM osee_artifact where art_type_id = ?";
   private static final String DELETE_ARIFACT_TYPE = "delete from osee_artifact_type where art_type_id = ?";

   public static void purgeArtifactType(final ArtifactType artifactType) throws OseeCoreException {
      int artifactCount =
            ConnectionHandler.runPreparedQueryFetchInt(0, COUNT_ARTIFACT_OCCURRENCE, artifactType.getId());

      if (artifactCount != 0) {
         throw new OseeArgumentException(
               "Can not delete artifact type " + artifactType.getName() + " because there are " + artifactCount + " existing artifacts of this type.");
      }
      new DbTransaction() {

         @Override
         protected void handleTxWork(OseeConnection connection) throws OseeCoreException {
            int artTypeId = artifactType.getId();
            ConnectionHandler.runPreparedUpdate(connection, DELETE_VALID_ATTRIBUTE, artTypeId);
            ConnectionHandler.runPreparedUpdate(connection, DELETE_ARIFACT_TYPE, artTypeId);
         }

      }.execute();
   }

   /**
    * Given a set of artifact types, they will be converted to the new artifact type and the old artifact types will be
    * purged
    * 
    * @param purgeArtifactTypes types to be converted and purged
    * @param newArtifactType new type to convert any existing artifacts of the old type
    * @throws OseeCoreException
    */
   public static void purgeArtifactTypesWithCheck(Collection<ArtifactType> purgeArtifactTypes, ArtifactType newArtifactType) throws OseeCoreException {
      for (ArtifactType purgeArtifactType : purgeArtifactTypes) {
         // find all artifact of this type on all branches and make a unique list for type change (since it is not by branch)
         List<Artifact> artifacts = ArtifactQuery.getArtifactListFromType(purgeArtifactType, true);
         if (artifacts.size() > 0) {
            HashMap<Integer, Artifact> artifactMap = new HashMap<Integer, Artifact>();
            for (Artifact artifact : artifacts) {
               artifactMap.put(artifact.getArtId(), artifact);
            }
            if (newArtifactType == null) {
               HashSet<Branch> branches = new HashSet<Branch>();
               for (Artifact artifact : artifacts) {
                  branches.add(artifact.getBranch());
               }
               throw new OseeStateException(
                     "Found " + artifacts.size() + " artifact references of type " + purgeArtifactType + " on branches " + branches);
            } else {
               ChangeArtifactType.changeArtifactType(artifactMap.values(), newArtifactType);
            }
         }
         purgeArtifactType(purgeArtifactType);
      }
   }

   /**
    * Run code that will be run during purge with convert and report on what relations, attributes will be deleted as
    * part of the conversion.
    * 
    * @param purgeArtifactTypes
    * @param newArtifactType
    * @throws OseeCoreException
    */
   public static void purgeArtifactTypesWithConversionReportOnly(StringBuffer results, Collection<ArtifactType> purgeArtifactTypes, ArtifactType newArtifactType) throws OseeCoreException {
      try {
         for (ArtifactType purgeArtifactType : purgeArtifactTypes) {
            // find all artifact of this type on all branches and make a unique list for type change (since it is not by branch)
            List<Artifact> artifacts = ArtifactQuery.getArtifactListFromType(purgeArtifactType, true);
            if (artifacts.size() > 0) {
               HashMap<Integer, Artifact> artifactMap = new HashMap<Integer, Artifact>();
               for (Artifact artifact : artifacts) {
                  artifactMap.put(artifact.getArtId(), artifact);
               }
               ChangeArtifactType.changeArtifactTypeReportOnly(results, artifactMap.values(), newArtifactType);
            }
         }
      } catch (Exception ex) {
         throw new OseeCoreException(ex);
      }
   }

   public static void persist() throws OseeCoreException {
      getCache().storeAllModified();
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
   public static Artifact makeNewArtifact(ArtifactType artifactType, IOseeBranch branch) throws OseeCoreException {
      return getFactory(artifactType).makeNewArtifact(branch, artifactType, null, null, null);
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
   public static Artifact makeNewArtifact(ArtifactType artifactType, Branch branch, String guid, String humandReadableId) throws OseeCoreException {
      return getFactory(artifactType).makeNewArtifact(branch, artifactType, guid, humandReadableId, null);
   }

   /**
    * @return Returns the ArtifactType factory.
    */
   public static ArtifactFactory getFactory(IArtifactType artifactType) throws OseeCoreException {
      if (artifactType == null) {
         throw new OseeArgumentException("Artifact Type cannot be null");
      }
      return factoryManager.getFactory(artifactType.getName());
   }
}
