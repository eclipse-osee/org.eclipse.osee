/*
 * Created on Apr 1, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
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
import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.attribute.ArtifactSubtypeDescriptor;
import org.eclipse.osee.framework.skynet.core.attribute.ConfigurationPersistenceManager;

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

   private final HashMap<String, IRelationType> nameToTypeMap;
   private final HashMap<Integer, IRelationType> idToTypeMap;
   private final CompositeKeyHashMap<Integer, Integer, Pair<Integer, Integer>> validityMap;

   private static final String SELECT_LINK_VALIDITY = "SELECT * FROM osee_define_valid_relations";

   private RelationTypeManager() {
      this.nameToTypeMap = new HashMap<String, IRelationType>();
      this.idToTypeMap = new HashMap<Integer, IRelationType>();
      this.validityMap = new CompositeKeyHashMap<Integer, Integer, Pair<Integer, Integer>>();
   }
   private static final RelationTypeManager instance = new RelationTypeManager();

   public static RelationTypeManager getInstance() {
      return instance;
   }

   /**
    * return all the relation types that are valid for the given branch
    * 
    * @param branch
    * @return
    */
   public List<IRelationType> getValidTypes(Branch branch) throws SQLException {
      return getAllTypes();
   }

   /**
    * @return all Relation types in the datastore
    * @throws SQLException
    */
   public List<IRelationType> getAllTypes() throws SQLException {
      return new ArrayList<IRelationType>(idToTypeMap.values());
   }

   public List<IRelationType> getValidTypes(ArtifactSubtypeDescriptor artifactType, Branch branch) throws SQLException {
      return new ArrayList<IRelationType>(idToTypeMap.values());
   }

   public IRelationType getType(int relationTypeId) throws SQLException {
      ensurePopulated();
      return idToTypeMap.get(relationTypeId);
   }

   public IRelationType getType(String namespace, String typeName) throws SQLException {
      ensurePopulated();
      IRelationType relationType = nameToTypeMap.get(namespace + typeName);
      if (relationType == null) {
         throw new IllegalArgumentException("The relation type: " + namespace + typeName + " does not exist");
      }
      return relationType;
   }

   public boolean typeExists(String namespace, String name) throws SQLException {
      ensurePopulated();
      return nameToTypeMap.get(namespace + name) != null;
   }

   public IRelationType getType(String typeName) throws SQLException {
      return getType("", typeName);
   }

   private synchronized void ensurePopulated() throws SQLException {
      if (idToTypeMap.size() == 0) {
         populateCache();
      }
   }

   public void refreshCache() throws SQLException {
      nameToTypeMap.clear();
      idToTypeMap.clear();
      populateCache();
   }

   private void cache(IRelationType relationType) {
      nameToTypeMap.put(relationType.getNamespace() + relationType.getTypeName(), relationType);
      idToTypeMap.put(relationType.getRelationTypeId(), relationType);
   }

   private void populateCache() throws SQLException {
      ConfigurationPersistenceManager configurationManager = ConfigurationPersistenceManager.getInstance();
      ConnectionHandlerStatement chStmt = null;

      try {
         chStmt = ConnectionHandler.runPreparedQuery(SELECT_LINK_TYPES);

         ResultSet rset = chStmt.getRset();
         while (rset.next()) {
            IRelationType relationType =
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

   public int getRelationSideMax(IRelationType relationType, ArtifactSubtypeDescriptor artifactType, boolean sideA) {

      int value = 0;

      Pair<Integer, Integer> pair = validityMap.get(relationType.getRelationTypeId(), artifactType.getArtTypeId());
      if (pair != null) {
         if (sideA) {
            value = pair.getKey();
         } else {
            value = pair.getValue();
         }
      }
      return value;
   }

   /**
    * ensure that the given artifact can be added to the specified side of a new link of this type
    * 
    * @throws SQLException
    */
   public void ensureSideWillSupportArtifact(IRelationType relationType, boolean sideA, Artifact artifact, int artifactCount) throws SQLException {
      ensurePopulated();
      int maxCount = getRelationSideMax(relationType, artifact.getArtifactType(), sideA);
      RelationLinkGroup group = artifact.getLinkManager().getSideGroup(relationType, !sideA);

      // if the artifact does not belong on that side at all
      if (maxCount == 0) {
         throw new IllegalArgumentException(String.format(
               "Artifact \"%s\" of type \"%s\" does not belong on side \"%s\" of relation \"%s\"",
               artifact.getDescriptiveName(), artifact.getArtifactTypeName(), relationType.getSideName(sideA),
               relationType.getTypeName()));
      } else if (group == null) {
         // obvoiusly the current link count is zero, so this side will support a new link
      }
      // the artifact is allowed and a group exists, so check if there is space for another link.
      else if (group.getLinkCount() + 1 > maxCount) {
         throw new IllegalArgumentException(
               String.format(
                     "Artifact \"%s\" of type \"%s\" can not be added to side \"%s\" of relation \"%s\" because doing so would exceed the side maximum of %d for this artifact type",
                     artifact.getDescriptiveName(), artifact.getArtifactTypeName(), relationType.getSideName(sideA),
                     relationType.getTypeName(), maxCount));
      }
   }

   private void loadLinkValidities() throws SQLException {
      ConnectionHandlerStatement chStmt = null;
      try {
         chStmt = ConnectionHandler.runPreparedQuery(2000, SELECT_LINK_VALIDITY);
         ResultSet rset = chStmt.getRset();
         IRelationType relationType = null;

         while (rset.next()) {

            validityMap.put(rset.getInt("rel_link_type_id"), rset.getInt("art_type_id"), new Pair<Integer, Integer>(
                  rset.getInt("side_a_max"), rset.getInt("side_b_max")));

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
   public IRelationType createRelationType(String namespace, String relationTypeName, String sideAName, String sideBName, String abPhrasing, String baPhrasing, String shortName) throws SQLException {
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

      IRelationType relationType =
            new RelationType(relationTypeId, namespace, relationTypeName, sideAName, sideBName, abPhrasing, baPhrasing,
                  shortName);
      cache(relationType);

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
   public void createRelationLinkValidity(Branch branch, ArtifactSubtypeDescriptor artifactType, IRelationType relationType, int sideAMax, int sideBMax) throws SQLException {
      if (sideAMax < 0) throw new IllegalArgumentException("The sideAMax can no be negative");
      if (sideBMax < 0) throw new IllegalArgumentException("The sideBMax can no be negative");

      int artTypeId = artifactType.getArtTypeId();
      int relLinkTypeId = relationType.getRelationTypeId();

      if (validityMap.get(relLinkTypeId, artTypeId) == null) {
         ConnectionHandler.runPreparedUpdate(INSERT_VALID_RELATION, SQL3DataType.INTEGER, artTypeId,
               SQL3DataType.INTEGER, relLinkTypeId, SQL3DataType.INTEGER, sideAMax, SQL3DataType.INTEGER, sideBMax,
               SQL3DataType.INTEGER, branch.getBranchId());
         validityMap.put(relLinkTypeId, artTypeId, new Pair<Integer, Integer>(sideAMax, sideBMax));
      }
   }
}
