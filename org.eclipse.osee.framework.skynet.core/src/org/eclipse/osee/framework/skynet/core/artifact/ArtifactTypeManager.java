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
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import org.eclipse.osee.framework.core.exception.OseeArgumentException;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeDataStoreException;
import org.eclipse.osee.framework.core.exception.OseeStateException;
import org.eclipse.osee.framework.core.exception.OseeTypeDoesNotExist;
import org.eclipse.osee.framework.core.exception.OseeWrappedException;
import org.eclipse.osee.framework.database.core.ConnectionHandler;
import org.eclipse.osee.framework.database.core.ConnectionHandlerStatement;
import org.eclipse.osee.framework.database.core.SequenceManager;
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
   private static final int ABSTRACT_TYPE_INDICATOR = 1;
   private static final int CONCRETE_TYPE_INDICATOR = 0;
   private static final int NULL_SUPER_ARTIFACT_TYPE = -1;

   private static final String SELECT_ARTIFACT_TYPES = "SELECT * FROM osee_artifact_type order by super_art_type_id";
   private static final String INSERT_ARTIFACT_TYPE =
         "INSERT INTO osee_artifact_type (art_type_id, name, is_abstract, super_art_type_id) VALUES (?,?,?,?)";

   private static final ArtifactTypeManager instance = new ArtifactTypeManager();

   private final HashMap<String, ArtifactType> nameToTypeMap = new HashMap<String, ArtifactType>();
   private final HashMap<Integer, ArtifactType> idToTypeMap = new HashMap<Integer, ArtifactType>();

   private ArtifactTypeManager() {
   }

   public void refreshCache() throws OseeDataStoreException, OseeTypeDoesNotExist {
      nameToTypeMap.clear();
      idToTypeMap.clear();
      populateCache();
   }

   private static synchronized void ensurePopulated() throws OseeDataStoreException, OseeTypeDoesNotExist {
      if (instance.idToTypeMap.size() == 0) {
         instance.populateCache();
      }
   }

   private void populateCache() throws OseeDataStoreException, OseeTypeDoesNotExist {
      ConnectionHandlerStatement chStmt = new ConnectionHandlerStatement();

      try {
         chStmt.runPreparedQuery(SELECT_ARTIFACT_TYPES);

         while (chStmt.next()) {
            try {
               ArtifactType superArtifactType = null;
               int superArtifactTypeId = chStmt.getInt("super_art_type_id");
               if (superArtifactTypeId > NULL_SUPER_ARTIFACT_TYPE) {
                  superArtifactType = ArtifactTypeManager.getType(superArtifactTypeId);
               }
               boolean isAbstract = chStmt.getInt("is_abstract") == ABSTRACT_TYPE_INDICATOR;
               ArtifactType artifactType =
                     new ArtifactType(isAbstract, chStmt.getInt("art_type_id"), chStmt.getString("name"),
                           superArtifactType);
               ArtifactTypeManager.cache(artifactType);
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
    * @throws OseeTypeDoesNotExist
    * @throws OseeCoreException
    */
   public static Collection<ArtifactType> getAllTypes() throws OseeDataStoreException, OseeTypeDoesNotExist {
      ensurePopulated();
      return instance.idToTypeMap.values();
   }

   public static boolean typeExists(String name) throws OseeDataStoreException, OseeTypeDoesNotExist {
      ensurePopulated();
      return instance.nameToTypeMap.get(name) != null;
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
   static void cache(ArtifactType artifactType) {
      instance.nameToTypeMap.put(artifactType.getName(), artifactType);
      instance.idToTypeMap.put(artifactType.getArtTypeId(), artifactType);
   }

   /**
    * @return Returns the artifact type matching the name
    * @param name artifact type name to match
    * @throws OseeDataStoreException
    * @throws OseeTypeDoesNotExist
    */
   public static ArtifactType getType(String name) throws OseeTypeDoesNotExist, OseeDataStoreException {
      ensurePopulated();
      ArtifactType artifactType = instance.nameToTypeMap.get(name);

      if (artifactType == null) {
         throw new OseeTypeDoesNotExist("Artifact type [" + name + "] is not available.");
      }
      return artifactType;
   }

   /**
    * Get Artifact Types by type names.
    * 
    * @return Returns the types with a particular name
    * @param artifactTypeNames names to get
    * @throws OseeDataStoreException
    * @throws OseeTypeDoesNotExist if any name in the artifactTypeNames does not match
    */
   public static List<ArtifactType> getTypes(Iterable<String> artifactTypeNames) throws OseeTypeDoesNotExist, OseeDataStoreException {
      List<ArtifactType> artifactTypes = new ArrayList<ArtifactType>();
      for (String artifactTypeName : artifactTypeNames) {
         artifactTypes.add(getType(artifactTypeName));
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

   public static ArtifactType createType(boolean isAbstract, String artifactTypeName, String superArtifactTypeName) throws OseeDataStoreException, OseeTypeDoesNotExist {
      ArtifactType artifactType;
      if (!typeExists(artifactTypeName)) {
         ArtifactType superArtifactType = null;
         if (superArtifactTypeName != null) {
            superArtifactType = getType(superArtifactTypeName);
         }

         int artTypeId = SequenceManager.getNextArtifactTypeId();
         artifactType = new ArtifactType(isAbstract, artTypeId, artifactTypeName, superArtifactType);
         cache(artifactType);

         ConnectionHandler.runPreparedUpdate(INSERT_ARTIFACT_TYPE, artTypeId, artifactTypeName,
               isAbstract ? ABSTRACT_TYPE_INDICATOR : CONCRETE_TYPE_INDICATOR,
               superArtifactType != null ? superArtifactType.getArtTypeId() : NULL_SUPER_ARTIFACT_TYPE);
      } else {

         // TODO: Check if anything valuable is different and update it
         artifactType = getType(artifactTypeName);
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

   /**
    * Changes the descriptor of the artifacts to the provided artifact descriptor
    * 
    * @param artifacts
    * @param artifactType
    */
   public static void changeArtifactType(Collection<Artifact> artifacts, ArtifactType artifactType) throws OseeCoreException {
      ChangeArtifactType.changeArtifactType(artifacts, artifactType);
   }

   public static Collection<ArtifactType> getDescendants(ArtifactType artifactType) throws OseeCoreException {
      return getDescendants(artifactType, false);
   }

   public static Collection<ArtifactType> getDescendants(ArtifactType artifactType, boolean recurse) throws OseeCoreException {
      Set<ArtifactType> children = new HashSet<ArtifactType>();
      getDescendants(artifactType, children, recurse);
      return children;
   }

   private static void getDescendants(ArtifactType parentType, Collection<ArtifactType> children, boolean recurse) throws OseeCoreException {
      for (ArtifactType itemToCheck : getAllTypes()) {
         if (parentType.equals(itemToCheck.getSuperArtifactType())) {
            children.add(itemToCheck);
            if (recurse) {
               getDescendants(itemToCheck, children, recurse);
            }
         }
      }
   }

   public static void printInheritanceTree(Writer out) throws OseeCoreException {
      ArtifactType artifactType = ArtifactTypeManager.getType("Artifact");
      try {
         out.write("Inheritance:\n");
         printInheritanceHelper(artifactType, out);
      } catch (Exception e) {
         throw new OseeWrappedException(e);
      }
   }

   private static void printInheritanceHelper(ArtifactType artifactType, Writer out) throws Exception {
      Collection<ArtifactType> artifactTypes = ArtifactTypeManager.getDescendants(artifactType);
      if (!artifactTypes.isEmpty()) {
         out.write(artifactType.getName());
         out.write("->");
         out.write(artifactTypes.toString());
         out.write("\n");
         for (ArtifactType child : artifactTypes) {
            printInheritanceHelper(child, out);
         }
      }
   }
}
