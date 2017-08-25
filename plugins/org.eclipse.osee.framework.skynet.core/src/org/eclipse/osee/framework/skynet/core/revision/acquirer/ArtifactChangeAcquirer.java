/*******************************************************************************
 * Copyright (c) 2010 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.skynet.core.revision.acquirer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.TransactionToken;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.core.model.TransactionDelta;
import org.eclipse.osee.framework.core.sql.OseeSql;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.skynet.core.change.ArtifactChangeBuilder;
import org.eclipse.osee.framework.skynet.core.change.ChangeBuilder;
import org.eclipse.osee.framework.skynet.core.internal.ServiceUtil;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionManager;
import org.eclipse.osee.framework.skynet.core.utility.ConnectionHandler;
import org.eclipse.osee.jdbc.JdbcStatement;

/**
 * @author Jeff C. Phillips
 */
public class ArtifactChangeAcquirer extends ChangeAcquirer {

   public ArtifactChangeAcquirer(BranchId sourceBranch, TransactionToken transactionId, IProgressMonitor monitor, Artifact specificArtifact, Set<Integer> artIds, ArrayList<ChangeBuilder> changeBuilders, Set<Integer> newAndDeletedArtifactIds) {
      super(sourceBranch, transactionId, monitor, specificArtifact, artIds, changeBuilders, newAndDeletedArtifactIds);
   }

   @Override
   public ArrayList<ChangeBuilder> acquireChanges() throws OseeCoreException {
      Map<Integer, ArtifactChangeBuilder> artifactChangeBuilders = new HashMap<>();
      boolean hasBranch = getSourceBranch() != null;
      TransactionToken fromTransactionId;
      TransactionToken toTransactionId;

      if (getMonitor() != null) {
         getMonitor().subTask("Gathering New or Deleted Artifacts");
      }
      JdbcStatement chStmt = ConnectionHandler.getStatement();
      try {

         if (hasBranch) { //Changes per a branch
            fromTransactionId = getSourceBaseTransaction();
            toTransactionId = TransactionManager.getHeadTransaction(getSourceBranch());

            chStmt.runPreparedQuery(ServiceUtil.getSql(OseeSql.CHANGE_BRANCH_ARTIFACT), getSourceBranch(),
               fromTransactionId);
         } else { //Changes per a transaction
            toTransactionId = getTransaction();

            if (getSpecificArtifact() != null) {
               chStmt.runPreparedQuery(ServiceUtil.getSql(OseeSql.CHANGE_TX_ARTIFACT_FOR_SPECIFIC_ARTIFACT),
                  toTransactionId.getBranch(), toTransactionId.getId(), getSpecificArtifact().getArtId());
               fromTransactionId = toTransactionId;
            } else {
               chStmt.runPreparedQuery(ServiceUtil.getSql(OseeSql.CHANGE_TX_ARTIFACT), toTransactionId.getBranch(),
                  toTransactionId.getId());
               fromTransactionId = TransactionManager.getPriorTransaction(toTransactionId);
            }
         }

         TransactionDelta txDelta = new TransactionDelta(fromTransactionId, toTransactionId);

         while (chStmt.next()) {
            int artId = chStmt.getInt("art_id");
            ModificationType modificationType = ModificationType.valueOf(chStmt.getInt("mod_type"));

            ArtifactChangeBuilder artifactChangeBuilder = new ArtifactChangeBuilder(getSourceBranch(),
               ArtifactTypeManager.getTypeByGuid(chStmt.getLong("art_type_id")), chStmt.getInt("gamma_id"), artId,
               txDelta, modificationType, !hasBranch);

            getArtIds().add(artId);
            getChangeBuilders().add(artifactChangeBuilder);
            artifactChangeBuilders.put(artId, artifactChangeBuilder);
         }

         if (getMonitor() != null) {
            getMonitor().worked(25);
         }
      } finally {
         chStmt.close();
      }

      return getChangeBuilders();
   }

}
