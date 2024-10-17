/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
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

package org.eclipse.osee.ats.ide.integration.tests.skynet.core;

import static org.eclipse.osee.framework.core.enums.CoreBranches.COMMON;
import java.util.List;
import org.eclipse.osee.ats.api.data.AtsArtifactToken;
import org.eclipse.osee.client.test.framework.NotProductionDataStoreRule;
import org.eclipse.osee.client.test.framework.OseeLogMonitorRule;
import org.eclipse.osee.framework.core.data.TransactionId;
import org.eclipse.osee.framework.core.data.TransactionToken;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.core.model.TransactionRecord;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionManager;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Donald G. Dunne
 */
public class TransactionManagerTest {

   @Rule
   public NotProductionDataStoreRule notProduction = new NotProductionDataStoreRule();

   @Rule
   public OseeLogMonitorRule monitorRule = new OseeLogMonitorRule();

   @Test
   public void testArtifactTransactionAndUpdate() {
      String comment = "testArtifactTransactionAndUpdate";
      Artifact art =
         ArtifactTypeManager.addArtifact(CoreArtifactTypes.Folder, COMMON, "testArtifactTransactionAndUpdate");
      TransactionId txId = art.persist(comment);

      // Tx should be valid and show persist comment
      Assert.assertTrue(txId.isValid());
      TransactionRecord txRec = TransactionManager.getTransactionRecord(TransactionId.valueOf(txId.getId()));
      Assert.assertEquals(comment, txRec.getComment());

      // Load from db; tx should show persist comment
      TransactionToken txTok = art.getTransaction();
      txRec = TransactionManager.getTransactionRecord(TransactionId.valueOf(txTok.getId()));
      Assert.assertTrue(txId.isValid());
      Assert.assertEquals(comment, txRec.getComment());

      // Change attr only; tx should show new persist comment (be updated to latest tx record)
      String comment2 = comment + " - 2";
      art.addAttribute(CoreAttributeTypes.StaticId, "here");
      txId = art.persist(comment2);
      Assert.assertTrue(txId.isValid());
      txRec = TransactionManager.getTransactionRecord(TransactionId.valueOf(txId.getId()));
      Assert.assertEquals(comment2, txRec.getComment());
      Assert.assertEquals("here", art.getAttributesToString(CoreAttributeTypes.StaticId));

      // Change rel only; tx should show new persist comment (be updated to latest tx record)
      String comment3 = comment + " - 3";
      Artifact atsTopFolder = ArtifactQuery.getArtifactFromToken(AtsArtifactToken.AtsTopFolder);
      art.addRelation(CoreRelationTypes.SupportingInfo_SupportingInfo, atsTopFolder);
      txId = art.persist(comment3);
      Assert.assertTrue(txId.isValid());
      txRec = TransactionManager.getTransactionRecord(TransactionId.valueOf(txId.getId()));
      // TransactionRecord on art was updated on a relation-only change
      Assert.assertEquals(comment3, txRec.getComment());
      Assert.assertEquals(atsTopFolder, art.getRelatedArtifact(CoreRelationTypes.SupportingInfo_SupportingInfo));
   }

   @Test
   public void testGetSetTransactionComment() throws Exception {
      String guid = GUID.create();
      String comment = "TransactionManagerTest-" + guid;
      Artifact art = ArtifactTypeManager.addArtifact(CoreArtifactTypes.Folder, COMMON);
      art.persist(comment);

      // Find transaction
      List<TransactionRecord> transactions = TransactionManager.getTransaction(comment);
      Assert.assertEquals(1, transactions.size());
      TransactionRecord transaction = transactions.iterator().next();
      Assert.assertEquals(comment, transaction.getComment());

      String newComment = comment + "NEW";
      // Set comment
      TransactionManager.setTransactionComment(transaction, newComment);

      // Find transaction
      transactions = TransactionManager.getTransaction(comment);
      // Shouldn't be a transaction with old name
      Assert.assertEquals(0, transactions.size());

      transactions = TransactionManager.getTransaction(newComment);
      // Should be one transaction with new comment
      Assert.assertEquals(1, transactions.size());

   }

}
