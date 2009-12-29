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
package org.eclipse.osee.framework.skynet.core.test.cases;

import static org.junit.Assert.assertFalse;
import java.util.List;
import junit.framework.Assert;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.model.TransactionRecord;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionManager;
import org.eclipse.osee.support.test.util.TestUtil;
import org.junit.Before;

/**
 * @author Donald G. Dunne
 */
public class TransactionManagerTest {

   @Before
   public void setUp() throws Exception {
      assertFalse(TestUtil.isProductionDb());
   }

   @org.junit.Test
   public void testGetSetTransactionComment() throws Exception {

      // Create new transaction
      String guid = GUID.create();
      String comment = "TransactionManagerTest-" + guid;
      SkynetTransaction newTransaction = new SkynetTransaction(BranchManager.getCommonBranch(), comment);
      Artifact art = ArtifactTypeManager.addArtifact(CoreArtifactTypes.Folder, BranchManager.getCommonBranch());
      art.persist(newTransaction);
      newTransaction.execute();

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
