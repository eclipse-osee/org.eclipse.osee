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
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.osee.framework.core.data.IArtifactToken;
import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.core.data.IAttributeType;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.DeletionFlag;
import org.eclipse.osee.framework.core.exception.OseeTypeDoesNotExist;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.model.cache.ArtifactTypeCache;
import org.eclipse.osee.framework.core.model.cache.BranchFilter;
import org.eclipse.osee.framework.core.model.event.DefaultBasicGuidArtifact;
import org.eclipse.osee.framework.core.model.type.ArtifactType;
import org.eclipse.osee.framework.core.services.IOseeCachingService;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.OseeStateException;
import org.eclipse.osee.framework.skynet.core.artifact.factory.ArtifactFactoryManager;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.internal.ServiceUtil;
import org.eclipse.osee.framework.skynet.core.utility.ConnectionHandler;

/**
 * Contains methods specific to artifact types. All artifact methods will eventually be moved from the
 * ConfigurationPersistenceManager to here.
 * 
 * @author Donald G. Dunne
 */
public class ArtifactTypeManager {

   private final static ArtifactFactoryManager factoryManager = new ArtifactFactoryManager();

   private static ArtifactTypeCache getCache() throws OseeCoreException {
      return getCacheService().getArtifactTypeCache();
   }

   private static IOseeCachingService getCacheService() throws OseeCoreException {
      return ServiceUtil.getOseeCacheService();
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

   public static Collection<ArtifactType> getConcreteArtifactTypes(IOseeBranch branch) throws OseeCoreException {
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
   public static ArtifactType getTypeByGuid(Long guid) throws OseeCoreException {
      if (guid == null) {
         throw new OseeArgumentException("[%s] is not a valid guid", guid);
      }
      ArtifactType artifactType = getCache().getByGuid(guid);
      if (artifactType == null) {
         getCache().reloadCache();
         artifactType = getCache().getByGuid(guid);
         if (artifactType == null) {
            throw new OseeTypeDoesNotExist("Artifact type [%s] is not available.", guid);
         }
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

   public static boolean inheritsFrom(IArtifactType artifactType, IArtifactType... parentTypes) throws OseeCoreException {
      return getType(artifactType).inheritsFrom(parentTypes);
   }

   /**
    * Get a new instance of type artifactTypeName
    */
   public static Artifact addArtifact(IArtifactType artifactType, IOseeBranch branch) throws OseeCoreException {
      return getFactory(artifactType).makeNewArtifact(branch, artifactType, null, null);
   }

   /**
    * Get a new instance of type artifactTypeName and set it's name.
    */
   public static Artifact addArtifact(IArtifactType artifactType, IOseeBranch branch, String name) throws OseeCoreException {
      Artifact artifact = addArtifact(artifactType, branch);
      artifact.setName(name);
      return artifact;
   }

   public static Artifact addArtifact(IArtifactType artifactType, IOseeBranch branch, String name, String guid) throws OseeCoreException {
      return getFactory(artifactType).makeNewArtifact(branch, artifactType, name, guid);
   }

   public static Artifact addArtifact(IArtifactToken artifactToken, IOseeBranch branch) throws OseeCoreException {
      return getFactory(artifactToken.getArtifactType()).makeNewArtifact(branch, artifactToken.getArtifactType(),
         artifactToken.getName(), artifactToken.getGuid());
   }

   private static final String COUNT_ARTIFACT_OCCURRENCE =
      "select count(1) from (select DISTINCT(art_id) FROM osee_artifact where art_type_id = ?) t1";

   public static void purgeArtifactType(IArtifactType artifactType) throws OseeCoreException {
      int artifactCount =
         ConnectionHandler.runPreparedQueryFetchInt(0, COUNT_ARTIFACT_OCCURRENCE, artifactType.getGuid());

      if (artifactCount != 0) {
         throw new OseeArgumentException(
            "Can not delete artifact type [%s] because there are %d existing artifacts of this type.", artifactType,
            artifactCount);
      }
   }

   /**
    * Given a set of artifact types, they will be converted to the new artifact type and the old artifact types will be
    * purged
    * 
    * @param purgeArtifactTypes types to be converted and purged
    * @param newArtifactType new type to convert any existing artifacts of the old type
    */
   public static void purgeArtifactTypesWithCheck(Collection<? extends IArtifactType> purgeArtifactTypes, IArtifactType newArtifactType) throws CoreException, OseeCoreException {
      for (IArtifactType purgeArtifactType : purgeArtifactTypes) {
         // find all artifact of this type on all branches and make a unique list for type change (since it is not by branch)
         Set<Artifact> artifacts = new LinkedHashSet<Artifact>();
         for (IOseeBranch branch : BranchManager.getBranches(new BranchFilter())) {
            artifacts.addAll(ArtifactQuery.getArtifactListFromType(CoreArtifactTypes.SoftwareRequirement, branch,
               DeletionFlag.INCLUDE_DELETED));
         }
         if (artifacts.size() > 0) {
            HashMap<Integer, Artifact> artifactMap = new HashMap<Integer, Artifact>();
            for (Artifact artifact : artifacts) {
               artifactMap.put(artifact.getArtId(), artifact);
            }
            if (newArtifactType == null) {
               HashSet<IOseeBranch> branches = new HashSet<IOseeBranch>();
               for (Artifact artifact : artifacts) {
                  branches.add(artifact.getBranch());
               }
               throw new OseeStateException("Found %d artifact references of type [%s] on branches [%s]",
                  artifacts.size(), purgeArtifactType, branches);
            } else {
               ChangeArtifactType.changeArtifactType(artifactMap.values(), newArtifactType, true);
            }
         }
         purgeArtifactType(purgeArtifactType);
      }
   }

   public static void persist() throws OseeCoreException {
      getCache().storeAllModified();
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
