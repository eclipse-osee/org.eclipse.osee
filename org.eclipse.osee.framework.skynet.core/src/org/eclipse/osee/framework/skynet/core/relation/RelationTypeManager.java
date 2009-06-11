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
package org.eclipse.osee.framework.skynet.core.relation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import org.eclipse.osee.framework.db.connection.ConnectionHandler;
import org.eclipse.osee.framework.db.connection.ConnectionHandlerStatement;
import org.eclipse.osee.framework.db.connection.core.SequenceManager;
import org.eclipse.osee.framework.db.connection.exception.OseeArgumentException;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.db.connection.exception.OseeDataStoreException;
import org.eclipse.osee.framework.db.connection.exception.OseeTypeDoesNotExist;
import org.eclipse.osee.framework.jdk.core.type.CompositeKeyHashMap;
import org.eclipse.osee.framework.jdk.core.type.ObjectPair;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactType;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.internal.Activator;

/**
 * @author Ryan D. Brooks
 * @author Andrew M. Finkbeiner
 */
public class RelationTypeManager {
   private static final String SELECT_LINK_TYPES = "SELECT * FROM osee_relation_link_type";
   private static final String INSERT_RELATION_LINK_TYPE =
         "INSERT INTO osee_relation_link_type (rel_link_type_id, namespace, type_name, a_name, b_name, ab_phrasing, ba_phrasing, short_name, user_ordered) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

   private static final String INSERT_VALID_RELATION =
         "INSERT INTO osee_valid_relations (art_type_id, rel_link_type_id, side_a_max, side_b_max, branch_id) VALUES (?, ?, ?, ?, ?)";

   private final HashMap<String, RelationType> nameToTypeMap = new HashMap<String, RelationType>();
   private final HashMap<Integer, RelationType> idToTypeMap = new HashMap<Integer, RelationType>();
   private final CompositeKeyHashMap<RelationType, ArtifactType, ObjectPair<Integer, Integer>> validityMap =
         new CompositeKeyHashMap<RelationType, ArtifactType, ObjectPair<Integer, Integer>>(300);

   private static final String SELECT_LINK_VALIDITY = "SELECT * FROM osee_valid_relations";
   private static final RelationTypeManager instance = new RelationTypeManager();

   private RelationTypeManager() {
   }

   /**
    * @param branch
    * @return all the relation types that are valid for the given branch
    * @throws OseeDataStoreException
    * @throws OseeTypeDoesNotExist
    */
   public static List<RelationType> getValidTypes(Branch branch) throws OseeDataStoreException, OseeTypeDoesNotExist {
      return getAllTypes();
   }

   /**
    * @return all Relation types in the datastore
    * @throws OseeDataStoreException
    * @throws OseeTypeDoesNotExist
    */
   public static List<RelationType> getAllTypes() throws OseeDataStoreException, OseeTypeDoesNotExist {
      ensurePopulated();
      return new ArrayList<RelationType>(instance.idToTypeMap.values());
   }

   public static List<RelationType> getValidTypes(ArtifactType artifactType, Branch branch) {
      Collection<RelationType> relationTypes = instance.idToTypeMap.values();
      List<RelationType> validRelationTypes = new ArrayList<RelationType>();
      for (RelationType relationType : relationTypes) {
         int sideAMax = getRelationSideMax(relationType, artifactType, RelationSide.SIDE_A);
         int sideBMax = getRelationSideMax(relationType, artifactType, RelationSide.SIDE_B);
         boolean onSideA = sideBMax > 0;
         boolean onSideB = sideAMax > 0;
         if (onSideA || onSideB) {
            validRelationTypes.add(relationType);
         }
      }
      return validRelationTypes;
   }

   public static RelationType getType(int relationTypeId) throws OseeTypeDoesNotExist, OseeDataStoreException {
      ensurePopulated();
      return internalGetType(relationTypeId);
   }

   private static RelationType internalGetType(int relationTypeId) throws OseeTypeDoesNotExist {
      RelationType relationType = instance.idToTypeMap.get(relationTypeId);
      if (relationType == null) {
         throw new OseeTypeDoesNotExist("The relation with type id: " + relationTypeId + " does not exist");
      }
      return relationType;
   }

   public static RelationType getType(String namespace, String typeName) throws OseeTypeDoesNotExist, OseeDataStoreException {
      ensurePopulated();
      RelationType relationType = instance.nameToTypeMap.get(namespace + typeName);
      if (relationType == null) {
         throw new OseeTypeDoesNotExist("The relation type: \"" + namespace + typeName + "\" does not exist");
      }
      return relationType;
   }

   public static boolean typeExists(String namespace, String name) throws OseeDataStoreException, OseeTypeDoesNotExist {
      ensurePopulated();
      return instance.nameToTypeMap.get(namespace + name) != null;
   }

   public static RelationType getType(String typeName) throws OseeTypeDoesNotExist, OseeDataStoreException {
      return getType("", typeName);
   }

   private void cache(RelationType relationType) {
      nameToTypeMap.put(relationType.getNamespace() + relationType.getTypeName(), relationType);
      idToTypeMap.put(relationType.getRelationTypeId(), relationType);
   }

   public void refreshCache() throws OseeDataStoreException, OseeTypeDoesNotExist {
      nameToTypeMap.clear();
      idToTypeMap.clear();
      populateCache();
   }

   private static synchronized void ensurePopulated() throws OseeDataStoreException, OseeTypeDoesNotExist {
      if (instance.idToTypeMap.isEmpty()) {
         instance.populateCache();
      }
   }

   private void populateCache() throws OseeDataStoreException, OseeTypeDoesNotExist {
      ConnectionHandlerStatement chStmt = new ConnectionHandlerStatement();

      try {
         chStmt.runPreparedQuery(SELECT_LINK_TYPES);

         while (chStmt.next()) {
            RelationType relationType =
                  new RelationType(chStmt.getInt("rel_link_type_id"), chStmt.getString("namespace"),
                        chStmt.getString("type_name"), chStmt.getString("a_name"), chStmt.getString("b_name"),
                        chStmt.getString("ab_phrasing"), chStmt.getString("ba_phrasing"),
                        chStmt.getString("short_name"), chStmt.getString("user_ordered"));
            cache(relationType);
         }
         loadLinkValidities();
      } finally {
         chStmt.close();
      }
   }

   public static int getRelationSideMax(RelationType relationType, ArtifactType artifactType, RelationSide relationSide) {
      ObjectPair<Integer, Integer> pair = instance.validityMap.get(relationType, artifactType);
      if (pair == null) {
         return 0;
      }
      return relationSide == RelationSide.SIDE_A ? pair.object1 : pair.object2;
   }

   private void loadLinkValidities() throws OseeDataStoreException, OseeTypeDoesNotExist {
      ConnectionHandlerStatement chStmt = new ConnectionHandlerStatement();
      try {
         chStmt.runPreparedQuery(2000, SELECT_LINK_VALIDITY);

         while (chStmt.next()) {
            try {
               RelationType relationType = internalGetType(chStmt.getInt("rel_link_type_id"));
               ArtifactType artifactType = ArtifactTypeManager.getType(chStmt.getInt("art_type_id"));
               validityMap.put(relationType, artifactType, new ObjectPair<Integer, Integer>(
                     chStmt.getInt("side_a_max"), chStmt.getInt("side_b_max")));
            } catch (OseeCoreException exception) {
               OseeLog.log(Activator.class, Level.SEVERE, exception);
            }
         }
      } finally {
         chStmt.close();
      }
   }

   /**
    * Persist a new relation link type. If the <code>relationTypeName</code> is already in the database, then nothing is
    * done.
    * 
    * @param relationTypeName The type name of the relation link to define.
    * @param sideAName The name for the 'a' side of the relation.
    * @param sideBName The name for the 'b' side of the relation.
    * @param abPhrasing The phrasing appropriate from the 'a' side to the 'b' side.
    * @param baPhrasing The phrasing appropriate from the 'b' side to the 'a' side.
    * @param shortName An abbreviated name to display for the link type.
    * @throws OseeCoreException
    */
   public static RelationType createRelationType(String namespace, String relationTypeName, String sideAName, String sideBName, String abPhrasing, String baPhrasing, String shortName, String ordered) throws OseeCoreException {
      if (typeExists(namespace, relationTypeName)) {
         return getType(namespace, relationTypeName);
      }
      if (!Strings.isValid(relationTypeName)) throw new IllegalArgumentException(
            "The relationName can not be null or empty");
      if (!Strings.isValid(sideAName)) throw new IllegalArgumentException("The sideAName can not be null or empty");
      if (!Strings.isValid(sideBName)) throw new IllegalArgumentException("The sideBName can not be null or empty");
      if (!Strings.isValid(abPhrasing)) throw new IllegalArgumentException("The abPhrasing can not be null or empty");
      if (!Strings.isValid(baPhrasing)) throw new IllegalArgumentException("The baPhrasing can not be null or empty");
      if (!Strings.isValid(shortName)) throw new IllegalArgumentException("The shortName can not be null or empty");

      int relationTypeId = SequenceManager.getNextRelationTypeId();

      ConnectionHandler.runPreparedUpdate(INSERT_RELATION_LINK_TYPE, relationTypeId, namespace, relationTypeName,
            sideAName, sideBName, abPhrasing, baPhrasing, shortName, ordered);

      RelationType relationType =
            new RelationType(relationTypeId, namespace, relationTypeName, sideAName, sideBName, abPhrasing, baPhrasing,
                  shortName, ordered);
      instance.cache(relationType);
      return relationType;
   }

   /**
    * @param branch
    * @param artTypeId
    * @param relLinkTypeId
    * @param sideAMax
    * @param sideBMax
    * @throws OseeDataStoreException
    * @throws OseeArgumentException
    */
   public static void createRelationLinkValidity(Branch branch, ArtifactType artifactType, RelationType relationType, int sideAMax, int sideBMax) throws OseeDataStoreException, OseeArgumentException {
      if (sideAMax < 0) throw new OseeArgumentException("The sideAMax can no be negative");
      if (sideBMax < 0) throw new OseeArgumentException("The sideBMax can no be negative");

      ObjectPair<Integer, Integer> entry = instance.validityMap.get(relationType, artifactType);
      if (entry == null) {
         ConnectionHandler.runPreparedUpdate(INSERT_VALID_RELATION, artifactType.getArtTypeId(),
               relationType.getRelationTypeId(), sideAMax, sideBMax, branch.getBranchId());
         instance.validityMap.put(relationType, artifactType, new ObjectPair<Integer, Integer>(sideAMax, sideBMax));
      }
   }
}
