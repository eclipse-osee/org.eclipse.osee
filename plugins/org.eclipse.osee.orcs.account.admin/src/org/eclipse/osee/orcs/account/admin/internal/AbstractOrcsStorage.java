/*********************************************************************
 * Copyright (c) 2014 Boeing
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

package org.eclipse.osee.orcs.account.admin.internal;

import static org.eclipse.osee.framework.core.enums.CoreBranches.COMMON;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.OrcsAdmin;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.search.QueryBuilder;
import org.eclipse.osee.orcs.search.QueryFactory;
import org.eclipse.osee.orcs.transaction.TransactionBuilder;
import org.eclipse.osee.orcs.transaction.TransactionFactory;

/**
 * @author Roberto E. Escobar
 */
public abstract class AbstractOrcsStorage {

   private Log logger;
   private OrcsApi orcsApi;

   private AccountFactory factory;

   public void setLogger(Log logger) {
      this.logger = logger;
   }

   public void setOrcsApi(OrcsApi orcsApi) {
      this.orcsApi = orcsApi;
   }

   public void setAccountFactory(AccountFactory factory) {
      this.factory = factory;
   }

   public void start() {
      logger.trace("Starting [%s]...", getClass().getSimpleName());
   }

   public void stop() {
      logger.trace("Stopping [%s]...", getClass().getSimpleName());
   }

   protected Log getLogger() {
      return logger;
   }

   protected AccountFactory getFactory() {
      return factory;
   }

   protected QueryBuilder newQuery() {
      QueryFactory queryFactory = orcsApi.getQueryFactory();
      return queryFactory.fromBranch(COMMON);
   }

   protected TransactionBuilder newTransaction(String comment) {
      TransactionFactory transactionFactory = orcsApi.getTransactionFactory();
      return transactionFactory.createTransaction(COMMON, comment);
   }

   protected boolean isInitialized() {
      OrcsAdmin adminOps = orcsApi.getAdminOps();
      return adminOps.isDataStoreInitialized();
   }
}