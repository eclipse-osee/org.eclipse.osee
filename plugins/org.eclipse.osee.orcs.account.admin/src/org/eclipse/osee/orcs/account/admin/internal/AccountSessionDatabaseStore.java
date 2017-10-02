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
import java.util.function.Consumer;
import org.eclipse.osee.account.admin.AccountSession;
import org.eclipse.osee.executor.admin.CancellableCallable;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.jdk.core.type.ResultSet;
import org.eclipse.osee.framework.jdk.core.type.ResultSets;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.jdbc.JdbcClient;
import org.eclipse.osee.jdbc.JdbcStatement;
import org.eclipse.osee.logger.Log;

/**
 * @author Roberto E. Escobar
 */
public class AccountSessionDatabaseStore implements AccountSessionStorage {

   private static final String SELECT_BY_ACCOUNT_ID = "SELECT * FROM osee_account_session WHERE account_id = ?";

   private static final String SELECT_BY_SESSION_TOKEN = "SELECT * FROM osee_account_session WHERE session_token = ?";

   private static final String INSERT_ACCOUNT_SESSION =
      "INSERT INTO osee_account_session (account_id, session_token, created_on, last_accessed_on, accessed_from, access_details) VALUES (?,?,?,?,?,?)";

   private static final String UPDATE_BY_ACCOUNT_ID_AND_SESSION_TOKEN =
      "UPDATE osee_account_session SET last_accessed_on = ?, accessed_from = ?, access_details = ? WHERE account_id = ? AND session_token = ?";

   private static final String DELETE_BY_SESSION_TOKEN = "DELETE FROM osee_account_session WHERE session_token = ?";

   private final Log logger;
   private final JdbcClient jdbcClient;
   private final AccountFactory factory;

   public AccountSessionDatabaseStore(Log logger, JdbcClient jdbcClient, AccountFactory factory) {
      this.logger = logger;
      this.jdbcClient = jdbcClient;
      this.factory = factory;
   }

   private Object[] asInsert(AccountSession session) {
      return new Object[] {
         session.getAccountId(),
         session.getSessionToken(),
         session.getCreatedOn(),
         session.getLastAccessedOn(),
         session.getAccessedFrom(),
         session.getAccessDetails()};
   }

   private Object[] asUpdate(AccountSession session) {
      return new Object[] {
         session.getLastAccessedOn(),
         session.getAccessedFrom(),
         session.getAccessDetails(),
         session.getAccountId(),
         session.getSessionToken()};
   }

   @Override
   public Callable<ResultSet<AccountSession>> getAccountSessionByAccountId(ArtifactId accountId) {
      return selectAccess(SELECT_BY_ACCOUNT_ID, accountId);
   }

   @Override
   public Callable<ResultSet<AccountSession>> getAccountSessionBySessionToken(String sessionToken) {
      return selectAccess(SELECT_BY_SESSION_TOKEN, sessionToken);
   }

   private Callable<ResultSet<AccountSession>> selectAccess(final String query, final Object data) {
      return new AbstractCallable<Object, ResultSet<AccountSession>>(data) {

         @Override
         protected ResultSet<AccountSession> innerCall() throws Exception {
            List<AccountSession> list = new LinkedList<>();
            Consumer<JdbcStatement> consumer = stmt -> {
               long accountId = stmt.getLong("account_id");
               String sessionToken = stmt.getString("session_token");
               Date createdOn = stmt.getTimestamp("created_on");
               Date lastAccessedOn = stmt.getTimestamp("last_accessed_on");
               String accessedFrom = stmt.getString("accessed_from");
               String accessDetails = stmt.getString("access_details");
               ArtifactId artId = ArtifactId.valueOf(accountId);
               AccountSession session = factory.newAccountSession(artId, sessionToken, createdOn, lastAccessedOn,
                  accessedFrom, accessDetails);
               list.add(session);
            };
            jdbcClient.runQuery(consumer, query, data);
            return ResultSets.newResultSet(list);
         }
      };
   }

   @Override
   public Callable<Integer> createAccountSession(final Iterable<AccountSession> datas) {
      return new AbstractCallable<Iterable<AccountSession>, Integer>(datas) {

         @Override
         protected Integer innerCall() throws Exception {
            List<Object[]> data = new ArrayList<>();
            for (AccountSession session : datas) {
               data.add(asInsert(session));
            }
            int result = jdbcClient.runBatchUpdate(INSERT_ACCOUNT_SESSION, data);
            return result;
         }
      };
   }

   @Override
   public Callable<Integer> updateAccountSession(final Iterable<AccountSession> datas) {
      return new AbstractCallable<Iterable<AccountSession>, Integer>(datas) {

         @Override
         protected Integer innerCall() throws Exception {
            List<Object[]> data = new ArrayList<>();
            for (AccountSession session : datas) {
               data.add(asUpdate(session));
            }
            int result = jdbcClient.runBatchUpdate(UPDATE_BY_ACCOUNT_ID_AND_SESSION_TOKEN, data);
            return result;
         }
      };
   }

   @Override
   public Callable<Integer> deleteAccountSessionBySessionToken(final String token) {
      return new AbstractCallable<String, Integer>(token) {

         @Override
         protected Integer innerCall() throws Exception {
            return jdbcClient.runPreparedUpdate(DELETE_BY_SESSION_TOKEN, token);
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
