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

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import org.eclipse.osee.framework.db.connection.ConnectionHandler;
import org.eclipse.osee.framework.db.connection.ConnectionHandlerStatement;
import org.eclipse.osee.framework.db.connection.DbUtil;
import org.eclipse.osee.framework.db.connection.core.SequenceManager;
import org.eclipse.osee.framework.jdk.core.type.CompositeKeyHashMap;
import org.eclipse.osee.framework.jdk.core.type.ObjectPair;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactType;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.exception.OseeDataStoreException;

/**
 * @author Ryan D. Brooks
 * @author Andrew M. Finkbeiner
 */
public class RelationTypeManager {
   private static final String SELECT_LINK_TYPES = "SELECT * FROM osee_define_rel_link_type";
   private static final String INSERT_RELATION_LINK_TYPE =
         "INSERT INTO osee_define_rel_link_type (rel_link_type_id, namespace, type_name, a_name, b_name, ab_phrasing, ba_phrasing, short_name, ordered) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

   private static final String INSERT_VALID_RELATION =
         "INSERT INTO osee_define_valid_relations (art_type_id, rel_link_type_id, side_a_max, side_b_max, branch_id) VALUES (?, ?, ?, ?, ?)";

   private final HashMap<String, RelationType> nameToTypeMap = new HashMap<String, RelationType>();
   private final HashMap<Integer, RelationType> idToTypeMap = new HashMap<Integer, RelationType>();
   private final CompositeKeyHashMap<Integer, Integer, ObjectPair<Integer, Integer>> validityMap =
         new CompositeKeyHashMap<Integer, Integer, ObjectPair<Integer, Integer>>(300);

   private static final String SELECT_LINK_VALIDITY = "SELECT * FROM osee_define_valid_relations";
   private static final RelationTypeManager instance = new RelationTypeManager();

   private RelationTypeManager() {
   }

   /**
    * return all the relation types that are valid for the given branch
    * 
    * @param branch
    * @return
    */
   public static List<RelationType> getValidTypes(Branch branch) throws SQLException {
      return instance.getAllTypes();
   }

   /**
    * @return all Relation types in the datastore
    * @throws SQLException
    */
   private List<RelationType> getAllTypes() throws SQLException {
      return new ArrayList<RelationType>(idToTypeMap.values());
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

   public static RelationType getType(int relationTypeId) throws OseeCoreException {
      ensurePopulated();

      RelationType relationType = instance.idToTypeMap.get(relationTypeId);
      if (relationType == null) {
         throw new OseeCoreException("The relation with type id: " + relationTypeId + " does not exist");
      }
      return relationType;
   }

   public static RelationType getType(String namespace, String typeName) throws OseeCoreException {
      ensurePopulated();
      RelationType relationType = instance.nameToTypeMap.get(namespace + typeName);
      if (relationType == null) {
         throw new OseeCoreException("The relation type: \"" + namespace + typeName + "\" does not exist");
      }
      return relationType;
   }

   public static boolean typeExists(String namespace, String name) throws OseeDataStoreException {
      ensurePopulated();
      return instance.nameToTypeMap.get(namespace + name) != null;
   }

   public static RelationType getType(String typeName) throws SQLException {
      try {
         return getType("", typeName);
      } catch (OseeCoreException ex) {
         throw new SQLException(ex);
      }
   }

   private void cache(RelationType relationType) {
      nameToTypeMap.put(relationType.getNamespace() + relationType.getTypeName(), relationType);
      idToTypeMap.put(relationType.getRelationTypeId(), relationType);
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
      ConnectionHandlerStatement chStmt = null;

      try {
         chStmt = ConnectionHandler.runPreparedQuery(SELECT_LINK_TYPES);

         ResultSet rset = chStmt.getRset();
         while (rset.next()) {
            RelationType relationType =
                  new RelationType(rset.getInt("rel_link_type_id"), rset.getString("namespace"),
                        rset.getString("type_name"), rset.getString("a_name"), rset.getString("b_name"),
                        rset.getString("ab_phrasing"), rset.getString("ba_phrasing"), rset.getString("short_name"),
                        rset.getString("ordered"));
            cache(relationType);
         }
         loadLinkValidities();
      } catch (SQLException ex) {
         throw new OseeDataStoreException(ex);
      } finally {
         DbUtil.close(chStmt);
      }
   }

   public static int getRelationSideMax(RelationType relationType, ArtifactType artifactType, RelationSide relationSide) {
      ObjectPair<Integer, Integer> pair =
            instance.validityMap.get(relationType.getRelationTypeId(), artifactType.getArtTypeId());
      if (pair == null) {
         return 0;
      }
      return relationSide == RelationSide.SIDE_A ? pair.object1 : pair.object2;
   }

   private void loadLinkValidities() throws SQLException {
      ConnectionHandlerStatement chStmt = null;
      try {
         chStmt = ConnectionHandler.runPreparedQuery(2000, SELECT_LINK_VALIDITY);
         ResultSet rset = chStmt.getRset();

         while (rset.next()) {
            validityMap.put(rset.getInt("rel_link_type_id"), rset.getInt("art_type_id"),
                  new ObjectPair<Integer, Integer>(rset.getInt("side_a_max"), rset.getInt("side_b_max")));
         }
      } finally {
         DbUtil.close(chStmt);
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
    * @throws SQLException
    */
   public static RelationType createRelationType(String namespace, String relationTypeName, String sideAName, String sideBName, String abPhrasing, String baPhrasing, String shortName, String ordered) throws OseeCoreException {
      if (typeExists(namespace, relationTypeName)) {
         return getType(namespace, relationTypeName);
      }
      if (relationTypeName == null || relationTypeName.equals("")) throw new IllegalArgumentException(
            "The relationName can not be null or empty");
      if (sideAName == null || sideAName.equals("")) throw new IllegalArgumentException(
            "The sideAName can not be null or empty");
      if (sideBName == null || sideBName.equals("")) throw new IllegalArgumentException(
            "The sideBName can not be null or empty");
      if (abPhrasing == null || abPhrasing.equals("")) throw new IllegalArgumentException(
            "The abPhrasing can not be null or empty");
      if (baPhrasing == null || baPhrasing.equals("")) throw new IllegalArgumentException(
            "The baPhrasing can not be null or empty");
      if (shortName == null || shortName.equals("")) throw new IllegalArgumentException(
            "The shortName can not be null or empty");

      try {
         int relationTypeId = SequenceManager.getNextRelationTypeId();

         ConnectionHandler.runPreparedUpdate(INSERT_RELATION_LINK_TYPE, relationTypeId, namespace, relationTypeName,
               sideAName, sideBName, abPhrasing, baPhrasing, shortName, ordered);

         RelationType relationType =
               new RelationType(relationTypeId, namespace, relationTypeName, sideAName, sideBName, abPhrasing,
                     baPhrasing, shortName, ordered);
         instance.cache(relationType);
         return relationType;
      } catch (SQLException ex) {
         throw new OseeDataStoreException(ex);
      }
   }

   /**
    * @param branch
    * @param artTypeId
    * @param relLinkTypeId
    * @param sideAMax
    * @param sideBMax
    * @throws SQLException
    */
   public static void createRelationLinkValidity(Branch branch, ArtifactType artifactType, RelationType relationType, int sideAMax, int sideBMax) throws SQLException {
      if (sideAMax < 0) throw new IllegalArgumentException("The sideAMax can no be negative");
      if (sideBMax < 0) throw new IllegalArgumentException("The sideBMax can no be negative");

      int artTypeId = artifactType.getArtTypeId();
      int relLinkTypeId = relationType.getRelationTypeId();

      if (instance.validityMap.get(relLinkTypeId, artTypeId) == null) {
         ConnectionHandler.runPreparedUpdate(INSERT_VALID_RELATION, artTypeId, relLinkTypeId, sideAMax, sideBMax,
               branch.getBranchId());
         instance.validityMap.put(relLinkTypeId, artTypeId, new ObjectPair<Integer, Integer>(sideAMax, sideBMax));
      }
   }
}
