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
import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.core.data.IAttributeType;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.enums.DeletionFlag;
import org.eclipse.osee.framework.core.exception.OseeArgumentException;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeExceptions;
import org.eclipse.osee.framework.core.exception.OseeStateException;
import org.eclipse.osee.framework.core.exception.OseeTypeDoesNotExist;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.model.cache.ArtifactTypeCache;
import org.eclipse.osee.framework.core.model.event.DefaultBasicGuidArtifact;
import org.eclipse.osee.framework.core.model.type.ArtifactType;
import org.eclipse.osee.framework.core.services.IOseeCachingService;
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

   public static ArtifactTypeCache getCache() {
      return getCacheService().getArtifactTypeCache();
   }

   public static IOseeCachingService getCacheService() {
      return Activator.getInstance().getOseeCacheService();
   }

   public static Collection<ArtifactType> getArtifactTypesFromAttributeType(IAttributeType attributeType, IOseeBranch branchToken) throws OseeCoreException {
      Branch branch = getCacheService().getBranchCache().get(branchToken);
      List<ArtifactType> artifactTypes = new ArrayList<ArtifactType>();
      for (ArtifactType artifactType : getAllTypes()) {
         if (artifactType.isValidAttributeType(attributeType, branch)) {
            artifactTypes.add(artifactType);
         }
      }
      return artifactTypes;
   }

   public static Collection<ArtifactType> getValidArtifactTypes(IOseeBranch branch) throws OseeCoreException {
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
    */
   public static Collection<ArtifactType> getAllTypes() throws OseeCoreException {
      return getCache().getAll();
   }

   public static ArtifactType getType(DefaultBasicGuidArtifact guidArt) throws OseeCoreException {
      return getTypeByGuid(guidArt.getArtTypeGuid());
   }

   /**
    * @return Returns the artifact type matching the guid
    * @param guid artifact type guid to match
    */
   public static ArtifactType getTypeByGuid(String guid) throws OseeCoreException {
      if (!GUID.isValid(guid)) {
         throw new OseeArgumentException("[%s] is not a valid guid", guid);
      }
      ArtifactType artifactType = getCache().getByGuid(guid);
      if (artifactType == null) {
         throw new OseeTypeDoesNotExist("Artifact type [%s] is not available.", guid);
      }
      return artifactType;
   }

   /**
    * @return Returns the artifact type matching the name
    * @param name artifact type name to match
    */
   public static ArtifactType getType(String name) throws OseeCoreException {
      ArtifactType artifactType = getCache().getUniqueByName(name);
      if (artifactType == null) {
         throw new OseeTypeDoesNotExist("Artifact type [%s] is not available.", name);
      }
      return artifactType;
   }

   public static ArtifactType getType(IArtifactType artifactType) throws OseeCoreException {
      return getTypeByGuid(artifactType.getGuid());
   }

   public static int getTypeId(IArtifactType artifactType) throws OseeCoreException {
      return getType(artifactType).getId();
   }

   /**
    * @return Returns the descriptor with a particular name, null if it does not exist.
    */
   public static ArtifactType getType(int artTypeId) throws OseeCoreException {
      ArtifactType artifactType = getCache().getById(artTypeId);
      if (artifactType == null) {
         throw new OseeTypeDoesNotExist("Atrifact type: %d is not available.", artTypeId);
      }
      return artifactType;
   }

   public static boolean inheritsFrom(IArtifactType artifactType, IArtifactType... parentTypes) throws OseeCoreException {
      return getType(artifactType).inheritsFrom(parentTypes);
   }

   /**
    * Get a new instance of type artifactTypeName
    */
   public static Artifact addArtifact(IArtifactType artifactType, IOseeBranch branch) throws OseeCoreException {
      return makeNewArtifact(artifactType, branch);
   }

   /**
    * Get a new instance of type artifactTypeName and set it's name.
    */
   public static Artifact addArtifact(IArtifactType artifactType, IOseeBranch branch, String name) throws OseeCoreException {
      Artifact artifact = makeNewArtifact(artifactType, branch);
      artifact.setName(name);
      return artifact;
   }

   /**
    * Get a new instance of the type of artifact. This is just a convenience method that calls makeNewArtifact on the
    * known factory with this descriptor for the descriptor parameter, and the supplied branch.
    * 
    * @param branch branch on which artifact will be created
    * @return Return artifact reference
    * @see ArtifactFactory#makeNewArtifact(Branch, IArtifactType, String, String, ArtifactProcessor)
    */
   public static Artifact addArtifact(IArtifactType artifactType, Branch branch, String guid, String humandReadableId) throws OseeCoreException {
      return makeNewArtifact(artifactType, branch, guid, humandReadableId);
   }

   private static final String DELETE_VALID_ATTRIBUTE =
      "delete from osee_artifact_type_attributes where art_type_id = ?";
   private static final String COUNT_ARTIFACT_OCCURRENCE =
      "select count(1) from (select DISTINCT(art_id) FROM osee_artifact where art_type_id = ?) t1";
   private static final String DELETE_ARIFACT_TYPE = "delete from osee_artifact_type where art_type_id = ?";

   public static void purgeArtifactType(IArtifactType artifactType) throws OseeCoreException {
      final int artifactTypeId = getTypeId(artifactType);
      int artifactCount = ConnectionHandler.runPreparedQueryFetchInt(0, COUNT_ARTIFACT_OCCURRENCE, artifactTypeId);

      if (artifactCount != 0) {
         throw new OseeArgumentException(
            "Can not delete artifact type [%s] because there are %d existing artifacts of this type.", artifactType,
            artifactCount);
      }
      new DbTransaction() {

         @Override
         protected void handleTxWork(OseeConnection connection) throws OseeCoreException {
            ConnectionHandler.runPreparedUpdate(connection, DELETE_VALID_ATTRIBUTE, artifactTypeId);
            ConnectionHandler.runPreparedUpdate(connection, DELETE_ARIFACT_TYPE, artifactTypeId);
         }

      }.execute();
   }

   /**
    * Given a set of artifact types, they will be converted to the new artifact type and the old artifact types will be
    * purged
    * 
    * @param purgeArtifactTypes types to be converted and purged
    * @param newArtifactType new type to convert any existing artifacts of the old type
    */
   public static void purgeArtifactTypesWithCheck(Collection<? extends IArtifactType> purgeArtifactTypes, IArtifactType newArtifactType) throws OseeCoreException {
      for (IArtifactType purgeArtifactType : purgeArtifactTypes) {
         // find all artifact of this type on all branches and make a unique list for type change (since it is not by branch)
         List<Artifact> artifacts =
            ArtifactQuery.getArtifactListFromType(purgeArtifactType, DeletionFlag.INCLUDE_DELETED);
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
               throw new OseeStateException("Found %d artifact references of type [%s] on branches [%s]",
                  artifacts.size(), purgeArtifactType, branches);
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
    */
   public static void purgeArtifactTypesWithConversionReportOnly(StringBuffer results, Collection<IArtifactType> purgeArtifactTypes, IArtifactType newArtifactType) throws OseeCoreException {
      try {
         for (IArtifactType purgeArtifactType : purgeArtifactTypes) {
            // find all artifact of this type on all branches and make a unique list for type change (since it is not by branch)
            List<Artifact> artifacts =
               ArtifactQuery.getArtifactListFromType(purgeArtifactType, DeletionFlag.INCLUDE_DELETED);
            if (artifacts.size() > 0) {
               HashMap<Integer, Artifact> artifactMap = new HashMap<Integer, Artifact>();
               for (Artifact artifact : artifacts) {
                  artifactMap.put(artifact.getArtId(), artifact);
               }
               ChangeArtifactType.changeArtifactTypeReportOnly(results, artifactMap.values(), newArtifactType);
            }
         }
      } catch (Exception ex) {
         OseeExceptions.wrapAndThrow(ex);
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
    * @see ArtifactFactory#makeNewArtifact(IArtifactType, IOseeBranch)
    * @use {@link ArtifactTypeManager}.addArtifact
    */
   public static Artifact makeNewArtifact(IArtifactType artifactType, IOseeBranch branch) throws OseeCoreException {
      return getFactory(artifactType).makeNewArtifact(branch, artifactType, null, null, null);
   }

   /**
    * Get a new instance of the type of artifact described by this descriptor. This is just a convenience method that
    * calls makeNewArtifact on the known factory with this descriptor for the descriptor parameter, and the supplied
    * branch.
    * 
    * @param branch branch on which artifact will be created
    * @return Return artifact reference
    * @see ArtifactFactory#makeNewArtifact(Branch, IArtifactType, String, String, ArtifactProcessor)
    * @use {@link ArtifactTypeManager}.addArtifact
    */
   public static Artifact makeNewArtifact(IArtifactType artifactType, Branch branch, String guid, String humandReadableId) throws OseeCoreException {
      return getFactory(artifactType).makeNewArtifact(branch, artifactType, guid, humandReadableId, null);
   }

   /**
    * @return Returns the ArtifactType factory.
    */
   public static ArtifactFactory getFactory(IArtifactType artifactType) throws OseeCoreException {
      if (artifactType == null) {
         throw new OseeArgumentException("Artifact Type cannot be null");
      }
      return factoryManager.getFactory(artifactType);
   }
}
