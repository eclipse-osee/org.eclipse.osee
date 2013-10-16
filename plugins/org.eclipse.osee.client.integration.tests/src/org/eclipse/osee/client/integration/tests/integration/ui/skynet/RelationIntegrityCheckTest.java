/*******************************************************************************
 * Copyright (c) 2011 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.client.integration.tests.integration.ui.skynet;

import static org.eclipse.osee.client.demo.DemoChoice.OSEE_CLIENT_DEMO;
import java.util.LinkedList;
import java.util.List;
import org.junit.Assert;
import org.eclipse.osee.client.test.framework.OseeClientIntegrationRule;
import org.eclipse.osee.client.test.framework.OseeLogMonitorRule;
import org.eclipse.osee.framework.core.data.TokenFactory;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.exception.OseeDataStoreException;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.operation.Operations;
import org.eclipse.osee.framework.database.core.ConnectionHandler;
import org.eclipse.osee.framework.database.core.IOseeStatement;
import org.eclipse.osee.framework.jdk.core.type.DoubleKeyHashMap;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.conflict.ConflictManagerExternal;
import org.eclipse.osee.framework.skynet.core.httpRequests.PurgeBranchHttpRequestOperation;
import org.eclipse.osee.framework.ui.skynet.dbHealth.LocalRelationLink;
import org.eclipse.osee.framework.ui.skynet.dbHealth.RelationIntegrityCheck;
import org.junit.After;
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

   private final DoubleKeyHashMap<Integer, Integer, LocalRelationLink> map =
      new DoubleKeyHashMap<Integer, Integer, LocalRelationLink>();
   private Branch parentBranch;
   private Branch workingBranch;

   @Before
   public void setUp() throws Exception {
      parentBranch = BranchManager.createTopLevelBranch("1");

      Artifact art_A = ArtifactTypeManager.addArtifact(CoreArtifactTypes.Folder, parentBranch, "A");
      art_A.persist(getClass().getSimpleName());
      BranchManager.persist(parentBranch);

      workingBranch = BranchManager.createWorkingBranch(parentBranch, TokenFactory.createBranch(GUID.create(), "2"));

      art_A.deleteAndPersist();

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
      BranchManager.commitBranch(null, conflictManager, false, true);
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

   private boolean checkIfAllFixed() throws OseeCoreException {
      map.clear();
      runQuery(RelationIntegrityCheck.DELETED_A_ARTIFACTS);
      runQuery(RelationIntegrityCheck.DELETED_B_ARTIFACTS);
      return map.isEmpty();
   }

   private void applyFix() throws OseeCoreException {
      List<Object[]> rowsToDelete = new LinkedList<Object[]>();
      for (LocalRelationLink relLink : map.allValues()) {
         rowsToDelete.add(new Object[] {relLink.gammaId, relLink.relTransId, relLink.branchId});
      }

      if (!rowsToDelete.isEmpty()) {
         ConnectionHandler.runBatchUpdate(RelationIntegrityCheck.DEL_FROM_TXS_W_SPEC_BRANCH_ID, rowsToDelete);
      }
   }

   private void runQuery(String sqlQuery) throws OseeDataStoreException, OseeCoreException {
      IOseeStatement chStmt = ConnectionHandler.getStatement();
      try {
         chStmt.runPreparedQuery(sqlQuery);
         while (chStmt.next()) {
            //@formatter:off
            int gammaId =              chStmt.getInt("gamma_id");
            int transactionId =        chStmt.getInt("transaction_id");
            int relationId =           chStmt.getInt("rel_link_id");
            int branchId =             chStmt.getInt("branch_id");
            int a_sideArtifactId =     chStmt.getInt("a_art_id");
            int b_sideArtifactId =     chStmt.getInt("b_art_id");
            int deletedTransaction =   chStmt.getInt("deleted_tran");

            //note: aliased column only present in RelationIntegrityCheck.DELETED_ARTIFACTS_QUERY
            int commitTransId =        chStmt.getInt("commit_trans_art_id");
            int modType =              chStmt.getInt("creating_trans_mod_type");
            //@formatter:on

            if (!map.containsKey(gammaId, transactionId)) {
               if (commitTransId > 0 && modType == 1) {
                  map.put(gammaId, transactionId, new LocalRelationLink(relationId, gammaId, transactionId, branchId,
                     a_sideArtifactId, b_sideArtifactId, deletedTransaction, commitTransId, modType));
               }
            }
         }
      } finally {
         chStmt.close();
      }
   }

}
