/*********************************************************************
 * Copyright (c) 2011 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.client.integration.tests.integration.ui.skynet;

import static org.eclipse.osee.client.demo.DemoChoice.OSEE_CLIENT_DEMO;
import java.util.LinkedList;
import java.util.List;
import org.eclipse.osee.client.test.framework.OseeClientIntegrationRule;
import org.eclipse.osee.client.test.framework.OseeLogMonitorRule;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.BranchToken;
import org.eclipse.osee.framework.core.data.GammaId;
import org.eclipse.osee.framework.core.data.TransactionResult;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.DemoUsers;
import org.eclipse.osee.framework.core.enums.PermissionEnum;
import org.eclipse.osee.framework.core.operation.Operations;
import org.eclipse.osee.framework.jdk.core.type.DoubleKeyHashMap;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.conflict.ConflictManagerExternal;
import org.eclipse.osee.framework.skynet.core.httpRequests.PurgeBranchHttpRequestOperation;
import org.eclipse.osee.framework.skynet.core.utility.ConnectionHandler;
import org.eclipse.osee.framework.ui.skynet.dbHealth.LocalRelationLink;
import org.eclipse.osee.framework.ui.skynet.dbHealth.RelationIntegrityCheck;
import org.eclipse.osee.framework.ui.skynet.internal.ServiceUtil;
import org.eclipse.osee.jdbc.JdbcStatement;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

/**
 * Very SLOW, integration level test, using ConflictManagerExternal which must run on the client.
 * <p>
 * Tests data integrity case where a new relation is persisted on a deleted artifact. Checks that if the situation
 * exists and runs <code>applyFix()</code> to resolve the issue.
 * </p>
 * {@link RelationIntegrityCheck}
 *
 * @author Karol M. Wilk
 */
public class RelationIntegrityCheckTest {

   @Rule
   public OseeClientIntegrationRule integration = new OseeClientIntegrationRule(OSEE_CLIENT_DEMO);

   @Rule
   public OseeLogMonitorRule monitorRule = new OseeLogMonitorRule();

   private final DoubleKeyHashMap<Long, Long, LocalRelationLink> map = new DoubleKeyHashMap<>();
   private BranchToken parentBranch;
   private BranchToken workingBranch;

   @Before
   public void setUp() throws Exception {
      parentBranch = BranchManager.createTopLevelBranch("1");
      ServiceUtil.getOseeClient().getAccessControlService().setPermission(UserManager.getUser(DemoUsers.Joe_Smith),
         parentBranch, PermissionEnum.FULLACCESS);

      Artifact art_A = ArtifactTypeManager.addArtifact(CoreArtifactTypes.Folder, parentBranch, "A");
      art_A.persist(getClass().getSimpleName());

      workingBranch = BranchManager.createWorkingBranch(parentBranch, BranchToken.create("2"));

      art_A.deleteAndPersist(getClass().getSimpleName());

      Artifact art_A_prime = ArtifactQuery.getArtifactFromTypeAndName(CoreArtifactTypes.Folder, "A", workingBranch);
      //Sample artifact to create a relation to...
      Artifact child = ArtifactTypeManager.addArtifact(CoreArtifactTypes.Folder, workingBranch, "Child");

      //cause a change on the branch
      art_A_prime.persist(getClass().getSimpleName());

      //create a new relation on A' to child
      art_A_prime.addChild(child);
      art_A_prime.persist(getClass().getSimpleName());

      //commit branch 2 into 1
      ConflictManagerExternal conflictManager = new ConflictManagerExternal(parentBranch, workingBranch);
      TransactionResult transactionResult = BranchManager.commitBranch(null, conflictManager, false, true);
      if (transactionResult.isFailed()) {
         throw new OseeCoreException(transactionResult.toString());
      }
   }

   @After
   public void tearDown() throws Exception {
      Operations.executeWorkAndCheckStatus(new PurgeBranchHttpRequestOperation(parentBranch, true));
   }

   @Test
   public void testNewRelationOnDeletedArtifact() throws Exception {
      runQuery(RelationIntegrityCheck.DELETED_A_ARTIFACTS);
      runQuery(RelationIntegrityCheck.DELETED_B_ARTIFACTS);

      applyFix();

      Assert.assertTrue(checkIfAllFixed());
   }

   private boolean checkIfAllFixed() {
      map.clear();
      runQuery(RelationIntegrityCheck.DELETED_A_ARTIFACTS);
      runQuery(RelationIntegrityCheck.DELETED_B_ARTIFACTS);
      return map.isEmpty();
   }

   private void applyFix() {
      List<Object[]> rowsToDelete = new LinkedList<>();
      for (LocalRelationLink relLink : map.allValues()) {
         rowsToDelete.add(new Object[] {relLink.gammaId, relLink.relTransId, relLink.branch});
      }

      if (!rowsToDelete.isEmpty()) {
         ConnectionHandler.runBatchUpdate(RelationIntegrityCheck.DEL_FROM_TXS_W_SPEC_BRANCH_ID, rowsToDelete);
      }
   }

   private void runQuery(String sqlQuery) {
      JdbcStatement chStmt = ConnectionHandler.getStatement();
      try {
         chStmt.runPreparedQuery(sqlQuery);
         while (chStmt.next()) {
            GammaId gammaId = GammaId.valueOf(chStmt.getLong("gamma_id"));
            Long transactionId = chStmt.getLong("transaction_id");
            int relationId = chStmt.getInt("rel_link_id");
            BranchId branch = BranchId.valueOf(chStmt.getLong("branch_id"));
            int a_sideArtifactId = chStmt.getInt("a_art_id");
            int b_sideArtifactId = chStmt.getInt("b_art_id");
            int deletedTransaction = chStmt.getInt("deleted_tran");

            //note: aliased column only present in RelationIntegrityCheck.DELETED_ARTIFACTS_QUERY
            int commitTransId = chStmt.getInt("commit_trans_art_id");
            int modType = chStmt.getInt("creating_trans_mod_type");

            if (!map.containsKey(gammaId.getId(), transactionId)) {
               if (commitTransId > 0 && modType == 1) {
                  map.put(gammaId.getId(), transactionId, new LocalRelationLink(relationId, gammaId, transactionId,
                     branch, a_sideArtifactId, b_sideArtifactId, deletedTransaction, commitTransId, modType));
               }
            }
         }
      } finally {
         chStmt.close();
      }
   }

}
