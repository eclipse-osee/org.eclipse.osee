/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs.account.admin.internal;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;
import org.eclipse.osee.account.admin.AccountAccess;
import org.eclipse.osee.executor.admin.CancellableCallable;
import org.eclipse.osee.framework.database.IOseeDatabaseService;
import org.eclipse.osee.framework.database.core.IOseeStatement;
import org.eclipse.osee.framework.jdk.core.type.ResultSet;
import org.eclipse.osee.framework.jdk.core.type.ResultSets;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.logger.Log;

/**
 * @author Roberto E. Escobar
 */
public class AccountAccessDatabaseStore implements AccountAccessStorage {

   private static final String SELECT_BY_ACCOUNT_ID = "SELECT * FROM osee_account_access WHERE account_id = ?";

   private static final String SELECT_BY_ACCESS_TOKEN = "SELECT * FROM osee_account_access WHERE access_token = ?";

   private static final String INSERT_ACCOUNT_ACCESS =
      "INSERT INTO osee_account_access (account_id, access_token, created_on, last_accessed_on, accessed_from, access_details) VALUES (?,?,?,?,?,?)";

   private static final String UPDATE_BY_ACCOUNT_ID =
      "UPDATE osee_account_access SET last_accessed_on = ?, accessed_from = ?, access_details = ? WHERE account_id = ? ";

   private static final String DELETE_BY_ACCESS_TOKEN = "DELETE FROM osee_account_access WHERE access_token = ?";

   private final Log logger;
   private final IOseeDatabaseService dbService;
   private final AccountFactory factory;

   public AccountAccessDatabaseStore(Log logger, IOseeDatabaseService dbService, AccountFactory factory) {
      this.logger = logger;
      this.dbService = dbService;
      this.factory = factory;
   }

   private Object[] asInsert(AccountAccess access) {
      return new Object[] {
         access.getAccountId(),
         access.getAccessToken(),
         access.getCreatedOn(),
         access.getLastAccessedOn(),
         access.getAccessedFrom(),
         access.getAccessDetails()};
   }

   private Object[] asUpdate(AccountAccess access) {
      return new Object[] {
         access.getLastAccessedOn(),
         access.getAccessedFrom(),
         access.getAccessDetails(),
         access.getAccountId()};
   }

   @Override
   public Callable<ResultSet<AccountAccess>> getAccountAccessByAccountId(long accountId) {
      return selectAccess(SELECT_BY_ACCOUNT_ID, accountId);
   }

   @Override
   public Callable<ResultSet<AccountAccess>> getAccountAccessByAccessToken(String accessToken) {
      return selectAccess(SELECT_BY_ACCESS_TOKEN, accessToken);
   }

   private Callable<ResultSet<AccountAccess>> selectAccess(final String query, final Object data) {
      return new AbstractCallable<Object, ResultSet<AccountAccess>>(data) {

         @Override
         protected ResultSet<AccountAccess> innerCall() throws Exception {
            List<AccountAccess> list = new LinkedList<AccountAccess>();
            IOseeStatement chStmt = dbService.getStatement();
            try {
               chStmt.runPreparedQuery(query, data);
               while (chStmt.next()) {
                  long accountId = chStmt.getLong("account_id");
                  String accessToken = chStmt.getString("access_token");
                  Date createdOn = chStmt.getTimestamp("created_on");
                  Date lastAccessedOn = chStmt.getTimestamp("last_accessed_on");
                  String accessedFrom = chStmt.getString("accessed_from");
                  String accessDetails = chStmt.getString("access_details");
                  AccountAccess access =
                     factory.newAccountAccess(accountId, accessToken, createdOn, lastAccessedOn, accessedFrom,
                        accessDetails);
                  list.add(access);
               }
            } finally {
               chStmt.close();
            }
            return ResultSets.newResultSet(list);
         }
      };
   }

   @Override
   public Callable<Integer> createAccountAccess(final Iterable<AccountAccess> datas) {
      return new AbstractCallable<Iterable<AccountAccess>, Integer>(datas) {

         @Override
         protected Integer innerCall() throws Exception {
            List<Object[]> data = new ArrayList<Object[]>();
            for (AccountAccess access : datas) {
               data.add(asInsert(access));
            }
            int result = dbService.runBatchUpdate(INSERT_ACCOUNT_ACCESS, data);
            return result;
         }
      };
   }

   @Override
   public Callable<Integer> updateAccountAccess(final Iterable<AccountAccess> datas) {
      return new AbstractCallable<Iterable<AccountAccess>, Integer>(datas) {

         @Override
         protected Integer innerCall() throws Exception {
            List<Object[]> data = new ArrayList<Object[]>();
            for (AccountAccess access : datas) {
               data.add(asUpdate(access));
            }
            int result = dbService.runBatchUpdate(UPDATE_BY_ACCOUNT_ID, data);
            return result;
         }
      };
   }

   @Override
   public Callable<Integer> deleteAccountAccessByAccessToken(final String token) {
      return new AbstractCallable<String, Integer>(token) {

         @Override
         protected Integer innerCall() throws Exception {
            return dbService.runPreparedUpdate(DELETE_BY_ACCESS_TOKEN, token);
         }
      };
   }

   private abstract class AbstractCallable<I, O> extends CancellableCallable<O> {

      private final I data;

      public AbstractCallable(I data) {
         super();
         this.data = data;
      }

      @Override
      public final O call() throws Exception {
         long startTime = System.currentTimeMillis();
         long endTime = startTime;
         O result = null;
         try {
            if (logger.isTraceEnabled()) {
               logger.trace("%s [start] - [%s]", getClass().getSimpleName(), data);
            }
            result = innerCall();
         } finally {
            endTime = System.currentTimeMillis() - startTime;
         }
         if (logger.isTraceEnabled()) {
            logger.trace("%s [%s] - completed [%s]", getClass().getSimpleName(), Lib.asTimeString(endTime), data);
         }
         return result;
      }

      protected abstract O innerCall() throws Exception;

   }

}
