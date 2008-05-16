/*
 * Created on May 12, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.skynet.core.artifact;

import static org.eclipse.osee.framework.db.connection.core.schema.SkynetDatabase.ARTIFACT_VERSION_TABLE;
import static org.eclipse.osee.framework.db.connection.core.schema.SkynetDatabase.ATTRIBUTE_VERSION_TABLE;
import static org.eclipse.osee.framework.db.connection.core.schema.SkynetDatabase.RELATION_LINK_VERSION_TABLE;
import static org.eclipse.osee.framework.db.connection.core.schema.SkynetDatabase.TRANSACTIONS_TABLE;
import static org.eclipse.osee.framework.db.connection.core.schema.SkynetDatabase.TRANSACTION_DETAIL_TABLE;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import org.eclipse.osee.framework.db.connection.ConnectionHandler;
import org.eclipse.osee.framework.db.connection.ConnectionHandlerStatement;
import org.eclipse.osee.framework.db.connection.DbUtil;
import org.eclipse.osee.framework.db.connection.info.SQL3DataType;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;

/**
 * @author Theron Virgin
 */
public class MergeBranchManager {
   private static ArtifactPersistenceManager artifactManager = ArtifactPersistenceManager.getInstance();
   private static BranchCreator branchCreator = BranchCreator.getInstance();

   private static final String GET_ART_IDS_FOR_ART_VER_TABLE =
         "SELECT t1.art_id FROM " + ARTIFACT_VERSION_TABLE + " t1, " + TRANSACTION_DETAIL_TABLE + " t2, " + TRANSACTIONS_TABLE + " t3 where t2.transaction_id = t3.transaction_id and t3.gamma_id = t1.gamma_id and t2.branch_id = ?";
   private static final String GET_ART_IDS_FOR_ATR_VER_TABLE =
         "SELECT t1.art_id FROM " + ATTRIBUTE_VERSION_TABLE + " t1, " + TRANSACTION_DETAIL_TABLE + " t2, " + TRANSACTIONS_TABLE + " t3 where t2.transaction_id = t3.transaction_id and t3.gamma_id = t1.gamma_id and t2.branch_id = ?";
   private static final String GET_ART_IDS_FOR_REL_VER_TABLE =
         "SELECT t1.a_art_id, t1.b_art_id FROM " + RELATION_LINK_VERSION_TABLE + " t1, " + TRANSACTION_DETAIL_TABLE + " t2, " + TRANSACTIONS_TABLE + " t3 where t2.transaction_id = t3.transaction_id and t3.gamma_id = t1.gamma_id and t2.branch_id = ?";

   public static void updateMergeBranch(Branch mergeBranch, ArrayList<Integer> expectedArtIds, Branch destBranch, Branch sourceBranch) throws Exception {
      Collection<Integer> allMergeBranchArtifacts = getAllMergeArtifacts(mergeBranch);
      Collection<Integer> allMergeBranchArtifactsCopy = new HashSet<Integer>(allMergeBranchArtifacts);
      Collection<Artifact> goodMergeBranchArtifacts = ArtifactQuery.getArtifactsFromBranch(mergeBranch, true);

      //Delete any damaged artifacts (from a source revert) on the merge branch
      for (Artifact artifact : goodMergeBranchArtifacts) {
         allMergeBranchArtifactsCopy.remove(new Integer(artifact.getArtId()));
      }
      if (!allMergeBranchArtifactsCopy.isEmpty()) {
         for (Integer artifact : allMergeBranchArtifactsCopy) {
            artifactManager.purgeArtifactFromBranch(mergeBranch.getBranchId(), artifact.intValue());
         }
      }

      //Delete any artifacts that shouldn't be on the merge branch but are
      for (Integer artid : expectedArtIds) {
         allMergeBranchArtifacts.remove(artid);
      }
      if (!allMergeBranchArtifacts.isEmpty()) {
         for (Integer artifact : allMergeBranchArtifacts) {
            artifactManager.purgeArtifactFromBranch(mergeBranch.getBranchId(), artifact.intValue());
         }
      }

      //Add any artifacts that should be on the merge branch but aren't
      for (Artifact artifact : goodMergeBranchArtifacts) {
         expectedArtIds.remove(new Integer(artifact.getArtId()));
      }
      if (!expectedArtIds.isEmpty()) branchCreator.addArtifactsToBranch(sourceBranch, destBranch, mergeBranch,
            expectedArtIds);

   }

   private static Collection<Integer> getAllMergeArtifacts(Branch branch) throws SQLException {
      Collection<Integer> artSet = new HashSet<Integer>();

      ConnectionHandlerStatement chStmt = null;
      try {
         chStmt =
               ConnectionHandler.runPreparedQuery(GET_ART_IDS_FOR_ART_VER_TABLE, SQL3DataType.INTEGER,
                     branch.getBranchId());
         ResultSet rSet = chStmt.getRset();
         while (chStmt.next()) {
            artSet.add(new Integer(rSet.getInt("art_id")));
         }
         chStmt =
               ConnectionHandler.runPreparedQuery(GET_ART_IDS_FOR_ATR_VER_TABLE, SQL3DataType.INTEGER,
                     branch.getBranchId());
         rSet = chStmt.getRset();
         while (chStmt.next()) {
            artSet.add(new Integer(rSet.getInt("art_id")));
         }
         chStmt =
               ConnectionHandler.runPreparedQuery(GET_ART_IDS_FOR_REL_VER_TABLE, SQL3DataType.INTEGER,
                     branch.getBranchId());
         rSet = chStmt.getRset();
         while (chStmt.next()) {
            artSet.add(new Integer(rSet.getInt("a_art_id")));
            artSet.add(new Integer(rSet.getInt("b_art_id")));
         }
      } finally {
         DbUtil.close(chStmt);
      }

      return artSet;
   }

}
