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
import java.util.List;
import java.util.logging.Level;
import org.eclipse.osee.framework.db.connection.ConnectionHandler;
import org.eclipse.osee.framework.db.connection.ConnectionHandlerStatement;
import org.eclipse.osee.framework.db.connection.core.SequenceManager;
import org.eclipse.osee.framework.db.connection.exception.OseeArgumentException;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.db.connection.exception.OseeDataStoreException;
import org.eclipse.osee.framework.db.connection.exception.OseeStateException;
import org.eclipse.osee.framework.db.connection.exception.OseeTypeDoesNotExist;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.attribute.AttributeType;
import org.eclipse.osee.framework.skynet.core.attribute.TypeValidityManager;
import org.eclipse.osee.framework.skynet.core.internal.Activator;

/**
 * Contains methods specific to artifact types. All artifact methods will eventually be moved from the
 * ConfigurationPersistenceManager to here.
 * 
 * @author Donald G. Dunne
 */
public class ArtifactTypeManager {
   private static final String SELECT_ARTIFACT_TYPES = "SELECT * FROM osee_artifact_type";
   private static final String INSERT_ARTIFACT_TYPE =
         "INSERT INTO osee_artifact_type (art_type_id, namespace, name) VALUES (?,?,?)";

   private static final ArtifactTypeManager instance = new ArtifactTypeManager();

   private final HashMap<String, ArtifactType> nameToTypeMap = new HashMap<String, ArtifactType>();
   private final HashMap<Integer, ArtifactType> idToTypeMap = new HashMap<Integer, ArtifactType>();

   private ArtifactTypeManager() {
   }

   public void refreshCache() throws OseeDataStoreException {
      nameToTypeMap.clear();
      idToTypeMap.clear();
      populateCache();
   }

   private static synchronized void ensurePopulated() throws OseeDataStoreException {
      if (instance.idToTypeMap.size() == 0) {
         instance.populateCache();
      }
   }

   private void populateCache() throws OseeDataStoreException {
      ConnectionHandlerStatement chStmt = new ConnectionHandlerStatement();

      try {
         chStmt.runPreparedQuery(SELECT_ARTIFACT_TYPES);

         while (chStmt.next()) {
            try {
               new ArtifactType(chStmt.getInt("art_type_id"), chStmt.getString("namespace"), chStmt.getString("name"));
            } catch (OseeDataStoreException ex) {
               OseeLog.log(Activator.class, Level.SEVERE, ex);
            }
         }
      } finally {
         chStmt.close();
      }
   }

   /**
    * @return Returns all of the descriptors.
    * @throws OseeCoreException
    */
   public static Collection<ArtifactType> getAllTypes() throws OseeDataStoreException {
      ensurePopulated();
      return instance.idToTypeMap.values();
   }

   public static boolean typeExists(String namespace, String name) throws OseeDataStoreException {
      ensurePopulated();
      return instance.nameToTypeMap.get(namespace + name) != null;
   }

   public static boolean typeExists(String name) throws OseeDataStoreException {
      return typeExists("", name);
   }

   public static Collection<AttributeType> getAttributeTypes(String artifactTypeName, Branch branch) throws OseeCoreException {
      return TypeValidityManager.getAttributeTypesFromArtifactType(ArtifactTypeManager.getType(artifactTypeName),
            branch);
   }

   /**
    * Cache a newly created descriptor.
    * 
    * @param descriptor The descriptor to cache
    * @throws IllegalArgumentException if descriptor is null.
    */
   static void cache(ArtifactType descriptor) {
      instance.nameToTypeMap.put(descriptor.getNamespace() + descriptor.getName(), descriptor);
      instance.idToTypeMap.put(descriptor.getArtTypeId(), descriptor);
   }

   /**
    * @return Returns the descriptor with a particular namespace and name
    * @throws OseeDataStoreException
    * @throws OseeCoreException
    */
   public static ArtifactType getType(String namespace, String name) throws OseeTypeDoesNotExist, OseeDataStoreException {
      ensurePopulated();
      ArtifactType artifactType = instance.nameToTypeMap.get(namespace + name);

      if (artifactType == null) {
         throw new OseeTypeDoesNotExist(
               "Artifact type with namespace \"" + namespace + "\" and name \"" + name + "\" is not available.");
      }
      return artifactType;
   }

   /**
    * @param artifactTypeName
    * @return Returns the type with a particular name (uses null for namespace), null if it does not exist.
    * @throws OseeTypeDoesNotExist
    * @throws OseeDataStoreException
    */
   public static ArtifactType getType(String artifactTypeName) throws OseeTypeDoesNotExist, OseeDataStoreException {
      return getType("", artifactTypeName);
   }

   /**
    * @return Returns the types with a particular name (uses null for namespace), null if it does not exist.
    * @throws OseeDataStoreException
    * @throws OseeTypeDoesNotExist
    */
   public static List<ArtifactType> getTypes(Iterable<String> artifactTypeNames) throws OseeTypeDoesNotExist, OseeDataStoreException {
      List<ArtifactType> artifactTypes = new ArrayList<ArtifactType>();
      for (String artifactTypeName : artifactTypeNames) {
         artifactTypes.add(getType("", artifactTypeName));
      }
      return artifactTypes;
   }

   /**
    * @return Returns the descriptor with a particular name, null if it does not exist.
    * @throws OseeTypeDoesNotExist
    */
   public static ArtifactType getType(int artTypeId) throws OseeDataStoreException, OseeTypeDoesNotExist {
      ensurePopulated();

      ArtifactType artifactType = instance.idToTypeMap.get(artTypeId);

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
   public static Artifact addArtifact(String artifactTypeName, Branch branch) throws OseeCoreException {
      return ArtifactTypeManager.getType(artifactTypeName).makeNewArtifact(branch);
   }

   /**
    * Get a new instance of type artifactTypeName and set it's name.
    * 
    * @param artifactTypeName
    * @param branch
    * @param name
    */
   public static Artifact addArtifact(String artifactTypeName, Branch branch, String name) throws OseeCoreException {
      Artifact artifact = addArtifact(artifactTypeName, branch);
      artifact.setDescriptiveName(name);
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
   public static Artifact addArtifact(String artifactTypeName, Branch branch, String guid, String humandReadableId) throws OseeCoreException {
      return ArtifactTypeManager.getType(artifactTypeName).makeNewArtifact(branch, guid, humandReadableId);
   }

   public static ArtifactType createType(String factoryName, String namespace, String artifactTypeName, String factoryKey) throws OseeDataStoreException, OseeTypeDoesNotExist {
      ArtifactType artifactType;
      if (!typeExists(namespace, artifactTypeName)) {
         int artTypeId = SequenceManager.getNextArtifactTypeId();
         artifactType = new ArtifactType(artTypeId, namespace, artifactTypeName);

         ConnectionHandler.runPreparedUpdate(INSERT_ARTIFACT_TYPE, artTypeId, namespace, artifactTypeName);
      } else {
         // Check if anything valuable is different
         artifactType = getType(namespace, artifactTypeName);
      }
      return artifactType;
   }

   private static final String DELETE_VALID_REL = "delete from osee_valid_relations where art_type_id = ?";
   private static final String DELETE_VALID_ATTRIBUTE = "delete from osee_valid_attributes where art_type_id = ?";
   private static final String COUNT_ARTIFACT_OCCURRENCE = "select count(1) FROM osee_artifact where art_type_id = ?";
   private static final String DELETE_ARIFACT_TYPE = "delete from osee_artifact_type where art_type_id = ?";

   public static void purgeArtifactType(ArtifactType artifactType) throws OseeCoreException {
      int artTypeId = artifactType.getArtTypeId();
      int artifactCount = ConnectionHandler.runPreparedQueryFetchInt(0, COUNT_ARTIFACT_OCCURRENCE, artTypeId);

      if (artifactCount != 0) {
         throw new OseeArgumentException(
               "Can not delete artifact type " + artifactType.getName() + " because there are " + artifactCount + " existing artifacts of this type.");
      }

      ConnectionHandler.runPreparedUpdate(DELETE_VALID_REL, artTypeId);
      ConnectionHandler.runPreparedUpdate(DELETE_VALID_ATTRIBUTE, artTypeId);
      ConnectionHandler.runPreparedUpdate(DELETE_ARIFACT_TYPE, artTypeId);
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
         List<Artifact> artifacts = ArtifactQuery.getArtifactsFromType(purgeArtifactType, true);
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
               changeArtifactType(artifactMap.values(), newArtifactType);
            }
         }
         ArtifactTypeManager.purgeArtifactType(purgeArtifactType);
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
            List<Artifact> artifacts = ArtifactQuery.getArtifactsFromType(purgeArtifactType, true);
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

   /**
    * Changes the descriptor of the artifacts to the provided artifact descriptor
    * 
    * @param artifacts
    * @param artifactType
    */
   public static void changeArtifactType(Collection<Artifact> artifacts, ArtifactType artifactType) throws OseeCoreException {
      ChangeArtifactType.changeArtifactType(artifacts, artifactType);
   }
}
