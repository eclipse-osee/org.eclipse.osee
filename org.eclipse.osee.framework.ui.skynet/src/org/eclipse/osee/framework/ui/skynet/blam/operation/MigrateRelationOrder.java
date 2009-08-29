/*******************************************************************************
 * Copyright (c) 2009 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.ui.skynet.blam.operation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.core.enums.BranchType;
import org.eclipse.osee.framework.core.enums.RelationSide;
import org.eclipse.osee.framework.core.enums.TransactionDetailsType;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.database.core.ConnectionHandler;
import org.eclipse.osee.framework.database.core.ConnectionHandlerStatement;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactNameComparator;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.relation.IRelationEnumeration;
import org.eclipse.osee.framework.skynet.core.relation.RelationLink;
import org.eclipse.osee.framework.skynet.core.relation.RelationType;
import org.eclipse.osee.framework.skynet.core.relation.RelationTypeManager;
import org.eclipse.osee.framework.skynet.core.relation.RelationTypeSide;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.ui.skynet.blam.AbstractBlam;
import org.eclipse.osee.framework.ui.skynet.blam.VariableMap;

/**
 * @author Ryan D. Brooks
 */
public class MigrateRelationOrder extends AbstractBlam {
   private static final String SELECT_CHILD_BASELINE_TXS =
         "select transaction_id from osee_branch br, osee_tx_details txd where br.parent_branch_id = ? and archived = 0 and branch_type = " + BranchType.WORKING.ordinal() + " and br.branch_id = txd.branch_id and txd.tx_type = " + TransactionDetailsType.Baselined.ordinal();

   private static final String SELECT_GAMMA_FROM_TXS = "select gamma_id from osee_txs where transaction_id = ?";
   private final ArtifactNameComparator nameComparator = new ArtifactNameComparator();

   @Override
   public String getName() {
      return "Migrate Relation Order";
   }

   @Override
   public void runOperation(VariableMap variableMap, IProgressMonitor monitor) throws Exception {
      for (Branch baselineBranch : BranchManager.getBaselineBranches()) {
         SkynetTransaction transaction = new SkynetTransaction(baselineBranch);
         for (RelationType relationType : RelationTypeManager.getAllTypes()) {
            if (relationType.isOrdered()) {
               for (Artifact artifact : ArtifactQuery.getArtifactListFromRelation(relationType, RelationSide.SIDE_A,
                     baselineBranch)) {
                  writeNewOrder(transaction, relationType, artifact);
               }
            }
         }
         transaction.execute();
         addToChildBaseilnes(transaction);
      }
   }

   private void writeNewOrder(SkynetTransaction transaction, RelationType relationType, Artifact artifact) throws OseeCoreException {
      IRelationEnumeration relationEnum = new RelationTypeSide(relationType, RelationSide.SIDE_B);
      List<Artifact> relatedArtiafcts = artifact.getRelatedArtifacts(relationEnum);

      if (relatedArtiafcts.size() == 1) {
         return; // user defined ordering is not needed
      }
      if (relatedArtiafcts.size() == 0) {
         return; // how did this happened?
      }
      List<Artifact> orginalOrder = new ArrayList<Artifact>(relatedArtiafcts.size());
      getOrginalOrder(artifact, relationEnum, orginalOrder);

      if (orginalOrder.size() != relatedArtiafcts.size()) {
         return; // link list is broken
      }

      Collections.sort(relatedArtiafcts, nameComparator);
      if (relatedArtiafcts.equals(orginalOrder)) {
         return; // these are already sorted lexicographically
      }

      artifact.setRelationOrder(relationEnum, orginalOrder);
      artifact.persistAttributesAndRelations(transaction);
   }

   private void getOrginalOrder(Artifact artifact, IRelationEnumeration relationEnum, List<Artifact> orginalOrder) throws OseeCoreException {
      List<RelationLink> links = artifact.getRelations(relationEnum);
      int artifactIdToFind = -1;
      while (true) {
         boolean found = false;
         for (RelationLink link : links) {
            if (link.getBOrder() == artifactIdToFind) {
               orginalOrder.add(link.getArtifactB());
               artifactIdToFind = link.getBArtifactId();
               found = true;
               break;
            }
         }
         if (!found) {
            break;
         }
      }
   }

   private void addToChildBaseilnes(SkynetTransaction updatedTransaction) throws OseeCoreException {
      List<Integer> gammaIds = new ArrayList<Integer>(1000);

      ConnectionHandlerStatement chStmt = new ConnectionHandlerStatement();
      try {
         chStmt.runPreparedQuery(100, SELECT_GAMMA_FROM_TXS, updatedTransaction.getTransactionNumber());
         while (chStmt.next()) {
            gammaIds.add(chStmt.getInt("gamma_id"));
         }
      } finally {
         chStmt.close();
      }

      final List<Object[]> txGammaList = new ArrayList<Object[]>();
      chStmt = new ConnectionHandlerStatement();
      try {
         chStmt.runPreparedQuery(100, SELECT_CHILD_BASELINE_TXS, updatedTransaction.getBranch().getBranchId());
         while (chStmt.next()) {
            Integer baselineTransactionId = chStmt.getInt("transaction_id");
            for (Integer gammaId : gammaIds) {
               txGammaList.add(new Object[] {baselineTransactionId, gammaId});
            }
         }
      } finally {
         chStmt.close();
      }
      ConnectionHandler.runBatchUpdate(
            "insert into osee_txs (mod_type, tx_current, transaction_id, gamma_id) VALUES (1,1,?,?)", txGammaList);
   }

   @Override
   public String getDescriptionUsage() {
      return "Migrate relation ordering information from OSEE 0.7.0 stype to OSEE 0.8.2";
   }

   @Override
   public Collection<String> getCategories() {
      return Arrays.asList("Admin");
   }
}