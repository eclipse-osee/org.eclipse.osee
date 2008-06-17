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
import org.eclipse.osee.framework.skynet.core.exception.OseeCoreException;

/**
 * @author Theron Virgin
 */
public class MergeBranchManager {
   private static ArtifactPersistenceManager artifactManager = ArtifactPersistenceManager.getInstance();

   private static final String GET_ART_IDS_FOR_ART_VER_TABLE =
         "SELECT t1.art_id FROM " + ARTIFACT_VERSION_TABLE + " t1, " + TRANSACTION_DETAIL_TABLE + " t2, " + TRANSACTIONS_TABLE + " t3 where t2.transaction_id = t3.transaction_id and t3.gamma_id = t1.gamma_id and t2.branch_id = ?";
   private static final String GET_ART_IDS_FOR_ATR_VER_TABLE =
         "SELECT t1.art_id FROM " + ATTRIBUTE_VERSION_TABLE + " t1, " + TRANSACTION_DETAIL_TABLE + " t2, " + TRANSACTIONS_TABLE + " t3 where t2.transaction_id = t3.transaction_id and t3.gamma_id = t1.gamma_id and t2.branch_id = ?";
   private static final String GET_ART_IDS_FOR_REL_VER_TABLE =
         "SELECT t1.a_art_id, t1.b_art_id FROM " + RELATION_LINK_VERSION_TABLE + " t1, " + TRANSACTION_DETAIL_TABLE + " t2, " + TRANSACTIONS_TABLE + " t3 where t2.transaction_id = t3.transaction_id and t3.gamma_id = t1.gamma_id and t2.branch_id = ?";

   public static void updateMergeBranch(Branch mergeBranch, ArrayList<Integer> expectedArtIds, Branch destBranch, Branch sourceBranch) throws OseeCoreException, SQLException {
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
      if (!expectedArtIds.isEmpty()) BranchCreator.getInstance().addArtifactsToBranch(sourceBranch, destBranch,
            mergeBranch, expectedArtIds);

   }

   private static Collection<Integer> getAllMergeArtifacts(Branch branch) throws SQLException {
      Collection<Integer> artSet = new HashSet<Integer>();

      ConnectionHandlerStatement chStmt = null;
      ResultSet rSet = null;
      try {
         chStmt =
               ConnectionHandler.runPreparedQuery(GET_ART_IDS_FOR_ART_VER_TABLE, SQL3DataType.INTEGER,
                     branch.getBranchId());
         rSet = chStmt.getRset();
         while (chStmt.next()) {
            artSet.add(new Integer(rSet.getInt("art_id")));
         }
      } finally {
         DbUtil.close(chStmt);
         chStmt = null;
         rSet = null;
      }

      try {
         chStmt =
               ConnectionHandler.runPreparedQuery(GET_ART_IDS_FOR_ATR_VER_TABLE, SQL3DataType.INTEGER,
                     branch.getBranchId());
         rSet = chStmt.getRset();
         while (chStmt.next()) {
            artSet.add(new Integer(rSet.getInt("art_id")));
         }
      } finally {
         DbUtil.close(chStmt);
         chStmt = null;
         rSet = null;
      }

      try {
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
         chStmt = null;
         rSet = null;
      }
      return artSet;
   }

}
