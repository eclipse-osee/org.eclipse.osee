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

import static org.eclipse.osee.framework.db.connection.core.schema.SkynetDatabase.REL_LINK_TYPE_ID_SEQ;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.eclipse.osee.framework.db.connection.ConnectionHandler;
import org.eclipse.osee.framework.db.connection.ConnectionHandlerStatement;
import org.eclipse.osee.framework.db.connection.DbUtil;
import org.eclipse.osee.framework.db.connection.core.query.Query;
import org.eclipse.osee.framework.db.connection.info.SQL3DataType;
import org.eclipse.osee.framework.jdk.core.type.CompositeKeyHashMap;
import org.eclipse.osee.framework.jdk.core.type.ObjectPair;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.attribute.ArtifactType;

/**
 * @author Ryan D. Brooks
 * @author Andrew M. Finkbeiner
 */
public class RelationTypeManager {
   private static final String SELECT_LINK_TYPES = "SELECT * FROM osee_define_rel_link_type";
   private static final String INSERT_RELATION_LINK_TYPE =
         "INSERT INTO osee_define_rel_link_type (rel_link_type_id, namespace, type_name, a_name, b_name, ab_phrasing, ba_phrasing, short_name) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

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

   public static List<RelationType> getValidTypes(ArtifactType artifactType, Branch branch) throws SQLException {
      return new ArrayList<RelationType>(instance.idToTypeMap.values());
   }

   public static RelationType getType(int relationTypeId) throws SQLException {
      ensurePopulated();

      RelationType relationType = instance.idToTypeMap.get(relationTypeId);
      if (relationType == null) {
         throw new IllegalArgumentException("The relation with type id: " + relationTypeId + " does not exist");
      }
      return relationType;
   }

   public static RelationType getType(String namespace, String typeName) throws SQLException {
      ensurePopulated();
      RelationType relationType = instance.nameToTypeMap.get(namespace + typeName);
      if (relationType == null) {
         throw new IllegalArgumentException("The relation type: " + namespace + typeName + " does not exist");
      }
      return relationType;
   }

   public static boolean typeExists(String namespace, String name) throws SQLException {
      ensurePopulated();
      return instance.nameToTypeMap.get(namespace + name) != null;
   }

   public static RelationType getType(String typeName) throws SQLException {
      return getType("", typeName);
   }

   private void cache(RelationType relationType) {
      nameToTypeMap.put(relationType.getNamespace() + relationType.getTypeName(), relationType);
      idToTypeMap.put(relationType.getRelationTypeId(), relationType);
   }

   public void refreshCache() throws SQLException {
      nameToTypeMap.clear();
      idToTypeMap.clear();
      populateCache();
   }

   private static synchronized void ensurePopulated() throws SQLException {
      if (instance.idToTypeMap.size() == 0) {
         instance.populateCache();
      }
   }

   private void populateCache() throws SQLException {
      ConnectionHandlerStatement chStmt = null;

      try {
         chStmt = ConnectionHandler.runPreparedQuery(SELECT_LINK_TYPES);

         ResultSet rset = chStmt.getRset();
         while (rset.next()) {
            RelationType relationType =
                  new RelationType(rset.getInt("rel_link_type_id"), rset.getString("namespace"),
                        rset.getString("type_name"), rset.getString("a_name"), rset.getString("b_name"),
                        rset.getString("ab_phrasing"), rset.getString("ba_phrasing"), rset.getString("short_name"));
            cache(relationType);
         }
      } finally {
         DbUtil.close(chStmt);
      }

      loadLinkValidities();
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
         RelationType relationType = null;

         while (rset.next()) {

            validityMap.put(rset.getInt("rel_link_type_id"), rset.getInt("art_type_id"),
                  new ObjectPair<Integer, Integer>(rset.getInt("side_a_max"), rset.getInt("side_b_max")));

            //            if (relationType == null || relationType.getRelationTypeId() != rset.getInt("rel_link_type_id")) {
            //               relationType = getType(rset.getInt("rel_link_type_id"));
            //            }
            //            relationType.setLinkSideRestriction(rset.getInt("art_type_id"), new LinkSideRestriction(
            //                  rset.getInt("side_a_max"), rset.getInt("side_b_max")));
         }
      } finally {
         DbUtil.close(chStmt);
      }
   }

   /**
    * Persist a new relation link type. If the <code>relationTypeName</code> is already in the database, then nothing
    * is done.
    * 
    * @param relationTypeName The type name of the relation link to define.
    * @param sideAName The name for the 'a' side of the relation.
    * @param sideBName The name for the 'b' side of the relation.
    * @param abPhrasing The phrasing appropriate from the 'a' side to the 'b' side.
    * @param baPhrasing The phrasing appropriate from the 'b' side to the 'a' side.
    * @param shortName An abbreviated name to display for the link type.
    * @throws SQLException
    */
   public static RelationType createRelationType(String namespace, String relationTypeName, String sideAName, String sideBName, String abPhrasing, String baPhrasing, String shortName) throws SQLException {
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

      int relationTypeId = Query.getNextSeqVal(null, REL_LINK_TYPE_ID_SEQ);

      ConnectionHandler.runPreparedUpdate(INSERT_RELATION_LINK_TYPE, SQL3DataType.INTEGER, relationTypeId,
            SQL3DataType.VARCHAR, namespace, SQL3DataType.VARCHAR, relationTypeName, SQL3DataType.VARCHAR, sideAName,
            SQL3DataType.VARCHAR, sideBName, SQL3DataType.VARCHAR, abPhrasing, SQL3DataType.VARCHAR, baPhrasing,
            SQL3DataType.VARCHAR, shortName);

      RelationType relationType =
            new RelationType(relationTypeId, namespace, relationTypeName, sideAName, sideBName, abPhrasing, baPhrasing,
                  shortName);
      instance.cache(relationType);

      return relationType;
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
         ConnectionHandler.runPreparedUpdate(INSERT_VALID_RELATION, SQL3DataType.INTEGER, artTypeId,
               SQL3DataType.INTEGER, relLinkTypeId, SQL3DataType.INTEGER, sideAMax, SQL3DataType.INTEGER, sideBMax,
               SQL3DataType.INTEGER, branch.getBranchId());
         instance.validityMap.put(relLinkTypeId, artTypeId, new ObjectPair<Integer, Integer>(sideAMax, sideBMax));
      }
   }
}
