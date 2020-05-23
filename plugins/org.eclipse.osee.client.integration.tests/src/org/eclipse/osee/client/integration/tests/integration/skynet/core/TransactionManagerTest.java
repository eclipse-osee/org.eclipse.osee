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

package org.eclipse.osee.client.integration.tests.integration.skynet.core;

import static org.eclipse.osee.client.demo.DemoChoice.OSEE_CLIENT_DEMO;
import static org.eclipse.osee.framework.core.enums.CoreBranches.COMMON;
import java.util.List;
import org.eclipse.osee.client.test.framework.OseeClientIntegrationRule;
import org.eclipse.osee.client.test.framework.OseeLogMonitorRule;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.model.TransactionRecord;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionManager;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Donald G. Dunne
 */
public class TransactionManagerTest {

   @Rule
   public OseeClientIntegrationRule integration = new OseeClientIntegrationRule(OSEE_CLIENT_DEMO);

   @Rule
   public OseeLogMonitorRule monitorRule = new OseeLogMonitorRule();

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
