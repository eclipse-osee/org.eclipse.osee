/*********************************************************************
 * Copyright (c) 2025 Boeing
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
package org.eclipse.osee.ats.rest.internal.test;

import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.ai.IAtsActionableItem;
import org.eclipse.osee.framework.core.data.UserId;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.core.enums.DemoUsers;
import org.eclipse.osee.framework.core.enums.SystemUser;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.data.TransactionReadable;
import org.eclipse.osee.orcs.transaction.TransactionBuilder;

/**
 * @author Donald G. Dunne
 */
public class TransactionsServerTest extends AbstractServerTest {

   private final XResultData rd;

   public TransactionsServerTest(AtsApi atsApi, OrcsApi orcsApi, XResultData rd) {
      super(atsApi, orcsApi);
      this.rd = rd;
   }

   public XResultData run() {
      testTransactionAuthor();
      return rd;
   }

   public void testTransactionAuthor() {
      rd.log("Started TransactionsServerTest.testTransactionAuthor");

      IAtsActionableItem topAi = atsApi.getActionableItemService().getTopActionableItem(atsApi);

      // Transaction valid and user is defaulted to current user
      TransactionBuilder tx =
         orcsApi.getTransactionFactory().createTransaction(CoreBranches.COMMON, getClass().getSimpleName() + " - 1");
      tx.createAttribute(topAi.getStoreObject(), CoreAttributeTypes.StaticId, "here1");
      TransactionReadable txId = (TransactionReadable) tx.commit();
      assertTrue(txId.isValid(), "Transaction Persist", rd);
      assertEquals(DemoUsers.Joe_Smith, txId.getAuthor(), rd);

      // If specify another valid user, then that user is author
      tx = orcsApi.getTransactionFactory().createTransaction(CoreBranches.COMMON, SystemUser.OseeSystem,
         getClass().getSimpleName() + " - 1");
      tx.createAttribute(topAi.getStoreObject(), CoreAttributeTypes.StaticId, "here2");
      txId = (TransactionReadable) tx.commit();
      assertTrue(txId.isValid(), "Transaction Persist", rd);
      assertEquals(SystemUser.OseeSystem, txId.getAuthor(), rd);

      // Exception if invalid user is specified
      boolean exceptionThrown = false;
      try {
         // Transaction with Sentinel user is invalid
         orcsApi.getTransactionFactory().createTransaction(CoreBranches.COMMON, UserId.SENTINEL,
            getClass().getSimpleName() + " - 2");
      } catch (OseeCoreException ex) {
         exceptionThrown = true;
      }
      assertTrue(exceptionThrown, "Invalid Author Should Cause Exception", rd);

      rd.log("Completed");
   }

}
