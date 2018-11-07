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

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.ArtifactTypeId;
import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.DeletionFlag;
import org.eclipse.osee.framework.core.exception.OseeTypeDoesNotExist;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.model.cache.ArtifactTypeCache;
import org.eclipse.osee.framework.core.model.cache.BranchFilter;
import org.eclipse.osee.framework.core.model.type.ArtifactType;
import org.eclipse.osee.framework.core.operation.IOperation;
import org.eclipse.osee.framework.core.services.IOseeCachingService;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.type.OseeStateException;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.framework.skynet.core.artifact.factory.ArtifactFactoryManager;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.internal.OseeTypesExportOperation;
import org.eclipse.osee.framework.skynet.core.internal.ServiceUtil;
import org.eclipse.osee.framework.skynet.core.utility.ConnectionHandler;
import org.eclipse.osee.orcs.rest.client.OseeClient;
import org.eclipse.osee.orcs.rest.model.TypesEndpoint;

/**
 * Contains methods specific to artifact types. All artifact methods will eventually be moved from the
 * ConfigurationPersistenceManager to here.
 *
 * @author Donald G. Dunne
 */
public class ArtifactTypeManager {

   private final static ArtifactFactoryManager factoryManager = new ArtifactFactoryManager();

   private static ArtifactTypeCache getCache() {
      return getCacheService().getArtifactTypeCache();
   }

   private static IOseeCachingService getCacheService() {
      return ServiceUtil.getOseeCacheService();
   }

   public static Collection<ArtifactType> getArtifactTypesFromAttributeType(AttributeTypeId attributeType, BranchId branchToken) {
      Branch branch = getCacheService().getBranchCache().get(branchToken);
      List<ArtifactType> artifactTypes = new ArrayList<>();
      for (ArtifactType artifactType : getAllTypes()) {
         if (artifactType.isValidAttributeType(attributeType, branch)) {
            artifactTypes.add(artifactType);
         }
      }
      return artifactTypes;
   }

   public static Collection<ArtifactType> getValidArtifactTypes(BranchId branch) {
      // TODO Filter artifact types by branch
      return getAllTypes();
   }

   public static Collection<ArtifactType> getConcreteArtifactTypes(BranchId branch) {
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

   public static Collection<ArtifactType> getAllTypes() {
      return getCache().getAll();
   }

   public static ArtifactType getType(Long id) {
      if (id == null) {
         throw new OseeArgumentException("[%s] is not a valid guid", id);
      }
      ArtifactType artifactType = getCache().getByGuid(id);
      if (artifactType == null) {
         getCacheService().reloadTypes();
         artifactType = getCache().getByGuid(id);
         if (artifactType == null) {
            throw new OseeTypeDoesNotExist("Artifact type [%s] is not available.", id);
         }
      }
      return artifactType;
   }

   /**
    * @return the artifact type matching the name
    * @param name artifact type name to match
    */
   public static ArtifactType getType(String name) {
      return getCache().getByName(name);
   }

   public static ArtifactType getType(ArtifactTypeId artifactType) {
      if (artifactType instanceof ArtifactType) {
         return (ArtifactType) artifactType;
      }
      return getType(artifactType.getId());
   }

   public static boolean inheritsFrom(ArtifactTypeId artifactType, ArtifactTypeId... parentTypes) {
      return getType(artifactType).inheritsFrom(parentTypes);
   }

   public static Artifact addArtifact(ArtifactToken artifactToken) {
      Conditions.assertTrue(artifactToken.getBranch().isValid(), "Branch must be specified.");
      return addArtifact(artifactToken, artifactToken.getBranch());
   }

   /**
    * Get a new instance of type artifactTypeName
    */
   public static Artifact addArtifact(IArtifactType artifactType, BranchId branch) {
      return addArtifact(artifactType, branch, null, null, null);
   }

   /**
    * Get a new instance of type artifactTypeName and set it's name.
    */
   public static Artifact addArtifact(IArtifactType artifactType, BranchId branch, String name) {
      return addArtifact(artifactType, branch, name, null, null);
   }

   public static Artifact addArtifact(IArtifactType artifactType, BranchId branch, String name, Long artifactId) {
      return addArtifact(artifactType, branch, name, null, artifactId);
   }

   public static Artifact addArtifact(IArtifactType artifactType, BranchId branch, String name, ArtifactId artifact) {
      return addArtifact(artifactType, branch, name, null, artifact.getId());
   }

   public static Artifact addArtifact(IArtifactType artifactType, BranchId branch, String name, String guid) {
      return getFactory(artifactType).makeNewArtifact(branch, artifactType, name, guid);
   }

   public static Artifact addArtifact(IArtifactType artifactType, BranchId branch, String name, String guid, Long uuid) {
      return getFactory(artifactType).makeNewArtifact(branch, artifactType, name, guid, uuid);
   }

   public static Artifact addArtifact(ArtifactToken artifactToken, BranchId branch) {
      return addArtifact(artifactToken.getArtifactTypeId(), branch, artifactToken.getName(), artifactToken.getGuid(),
         artifactToken.getId());
   }

   private static final String COUNT_ARTIFACT_OCCURRENCE =
      "select count(1) from (select DISTINCT(art_id) FROM osee_artifact where art_type_id = ?) t1";

   public static void purgeArtifactType(ArtifactTypeId artifactType) {
      int artifactCount = ConnectionHandler.getJdbcClient().fetch(0, COUNT_ARTIFACT_OCCURRENCE, artifactType);

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
   public static void purgeArtifactTypesWithCheck(Collection<? extends ArtifactTypeId> purgeArtifactTypes, ArtifactTypeId newArtifactType) {
      for (ArtifactTypeId purgeArtifactType : purgeArtifactTypes) {
         // find all artifact of this type on all branches and make a unique list for type change (since it is not by branch)
         Set<Artifact> artifacts = new LinkedHashSet<>();
         for (BranchId branch : BranchManager.getBranches(new BranchFilter())) {
            artifacts.addAll(ArtifactQuery.getArtifactListFromType(CoreArtifactTypes.SoftwareRequirement, branch,
               DeletionFlag.INCLUDE_DELETED));
         }
         if (artifacts.size() > 0) {
            HashMap<Integer, Artifact> artifactMap = new HashMap<>();
            for (Artifact artifact : artifacts) {
               artifactMap.put(artifact.getArtId(), artifact);
            }
            if (newArtifactType == null) {
               HashSet<BranchId> branches = new HashSet<>();
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

   public static ArtifactFactory getFactory(ArtifactTypeId artifactType) {
      if (artifactType == null) {
         throw new OseeArgumentException("Artifact Type cannot be null");
      }
      return factoryManager.getFactory(artifactType);
   }

   public static IOperation newExportTypesOp(OutputStream outputStream) {
      OseeClient oseeClient = ServiceUtil.getOseeClient();
      TypesEndpoint typesEndpoint = oseeClient.getTypesEndpoint();
      return new OseeTypesExportOperation(typesEndpoint, outputStream);
   }

   public static boolean isUserCreationAllowed(ArtifactTypeId artifactType) {
      boolean userCreationoAllowed = false;
      ArtifactFactory factory = factoryManager.getFactory(artifactType);
      if (factory != null && factory.isUserCreationEnabled(artifactType)) {
         userCreationoAllowed = true;
      } else if (factory == null) {
         userCreationoAllowed = true;
      }
      return userCreationoAllowed;
   }

}
