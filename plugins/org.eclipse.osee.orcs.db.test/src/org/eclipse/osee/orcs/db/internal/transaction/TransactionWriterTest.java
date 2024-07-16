/*********************************************************************
 * Copyright (c) 2012 Boeing
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

package org.eclipse.osee.orcs.db.internal.transaction;

import static org.eclipse.osee.framework.core.enums.CoreBranches.COMMON;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.jdbc.JdbcClient;
import org.eclipse.osee.jdbc.JdbcConnection;
import org.eclipse.osee.jdbc.JdbcDbType;
import org.eclipse.osee.jdbc.JdbcStatement;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.core.ds.DataProxy;
import org.eclipse.osee.orcs.core.ds.OrcsChangeSet;
import org.eclipse.osee.orcs.data.TransactionReadable;
import org.eclipse.osee.orcs.db.internal.sql.join.IdJoinQuery;
import org.eclipse.osee.orcs.db.internal.transaction.TransactionWriter.SqlOrderEnum;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

/**
 * Test Case for {@link TransactionWriter}
 *
 * @author Roberto E. Escobar
 */
public class TransactionWriterTest {

   private static final Long QUERY_ID_1 = 88L;
   private static final Long QUERY_ID_2 = 89L;

   private static final long TX_1 = 51;
   private static final long TX_2 = 52;

   private static final long GAMMA_1 = 80000L;
   private static final long GAMMA_2 = 80001L;

   //@formatter:off
   @Mock private Log logger;
   @Mock private JdbcClient jdbcClient;


   @Mock private TxSqlBuilder builder;
   @Mock private JdbcConnection connection;
   @Mock private TransactionReadable tx;
   @Mock private DataProxy<?> proxy1;
   @Mock private DataProxy<?> proxy2;

   @Mock private IdJoinQuery join1;
   @Mock private IdJoinQuery join2;

   @Mock private JdbcStatement chStmt;
   @Captor private ArgumentCaptor<List<Object[]>> paramCaptor;
   @Mock  private OrcsChangeSet changeSet;
   //@formatter:on

   private TransactionWriter writer;
   private List<DataProxy<?>> stores;

   @Before
   public void setUp() {
      MockitoAnnotations.initMocks(this);

      writer = new TransactionWriter(logger, jdbcClient, builder);

      stores = Arrays.asList(proxy1, proxy2);

      final Map<SqlOrderEnum, IdJoinQuery> joins = new LinkedHashMap<>();
      joins.put(SqlOrderEnum.ARTIFACTS, join1);
      joins.put(SqlOrderEnum.ATTRIBUTES, join2);

      when(join1.getQueryId()).thenReturn(Long.valueOf(QUERY_ID_1));
      when(join2.getQueryId()).thenReturn(Long.valueOf(QUERY_ID_2));

      when(tx.getBranch()).thenReturn(COMMON);
      when(builder.getBinaryStores()).thenReturn(stores);
      when(builder.getTxNotCurrents()).thenAnswer(new Answer<Set<Entry<SqlOrderEnum, IdJoinQuery>>>() {

         @Override
         public Set<Entry<SqlOrderEnum, IdJoinQuery>> answer(InvocationOnMock invocation) throws Throwable {
            Set<Entry<SqlOrderEnum, IdJoinQuery>> values = joins.entrySet();
            return values;
         }
      });
      when(jdbcClient.getStatement(connection)).thenReturn(chStmt);

      when(chStmt.next()).thenReturn(true).thenReturn(true).thenReturn(false);
      when(chStmt.getLong("transaction_id")).thenReturn(TX_1).thenReturn(TX_2);
      when(chStmt.getLong("gamma_id")).thenReturn(GAMMA_1).thenReturn(GAMMA_2);
      when(jdbcClient.getDbType()).thenReturn(JdbcDbType.h2);
   }

   @Test
   public void testRollback() {
      TransactionWriter spy = Mockito.spy(writer);

      OseeCoreException expected = new OseeCoreException("Testing");

      when(spy.getBinaryStores()).thenReturn(stores);
      doThrow(expected).when(proxy1).rollBack();

      spy.rollback();

      verify(proxy1).rollBack();
      verify(proxy2).rollBack();

      verify(logger).error(expected, "Error during binary rollback [%s]", proxy1);
   }

   @Test
   public void testWrite() {
      InOrder inOrder = inOrder(builder, tx, join1, join2, proxy1, proxy2, jdbcClient, chStmt);

      writer.write(connection, tx, changeSet);

      inOrder.verify(builder).accept(tx, changeSet);
      inOrder.verify(proxy1).persist();
      inOrder.verify(proxy2).persist();

      inOrder.verify(join1).store();

      String sql = jdbcClient.injectOrderedHint(SqlOrderEnum.ARTIFACTS.getTxsNotCurrentQuery());

      inOrder.verify(chStmt).runPreparedQuery(sql, QUERY_ID_1, COMMON);
      inOrder.verify(join1).close();

      inOrder.verify(join2).store();
      sql = jdbcClient.injectOrderedHint(SqlOrderEnum.ATTRIBUTES.getTxsNotCurrentQuery());
      inOrder.verify(chStmt).runPreparedQuery(sql, QUERY_ID_2, COMMON);
      inOrder.verify(join2).close();

      inOrder.verify(jdbcClient).runBatchUpdate(eq(connection), eq(TransactionWriter.UPDATE_TXS_NOT_CURRENT),
         paramCaptor.capture());

      inOrder.verify(builder).clear();

      Iterator<Object[]> params = paramCaptor.getValue().iterator();
      int index = 0;
      Object[] data = params.next();
      Assert.assertEquals(COMMON, data[index++]);
      Assert.assertEquals(TX_1, data[index++]);
      Assert.assertEquals(GAMMA_1, data[index++]);

      index = 0;
      data = params.next();
      Assert.assertEquals(COMMON, data[index++]);
      Assert.assertEquals(TX_2, data[index++]);
      Assert.assertEquals(GAMMA_2, data[index++]);
   }
}
