/*******************************************************************************
 * Copyright (c) 2012 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs.db.internal.transaction;

import static org.mockito.Matchers.eq;
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
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.model.TransactionRecord;
import org.eclipse.osee.framework.database.IOseeDatabaseService;
import org.eclipse.osee.framework.database.core.ArtifactJoinQuery;
import org.eclipse.osee.framework.database.core.IOseeStatement;
import org.eclipse.osee.framework.database.core.OseeConnection;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.core.ds.OrcsChangeSet;
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

   //@formatter:off
   @Mock private Log logger;
   @Mock private IOseeDatabaseService dbService;
   
   
   @Mock private TxSqlBuilder builder;
   @Mock private OseeConnection connection;
   @Mock private TransactionRecord tx;
   @Mock private Branch branch;
   @Mock private DaoToSql dao1;
   @Mock private DaoToSql dao2;
   
   @Mock private ArtifactJoinQuery join1;
   @Mock private ArtifactJoinQuery join2;
   
   @Mock private IOseeStatement chStmt;
   @Captor private ArgumentCaptor<List<Object[]>> paramCaptor;
   @Mock  private OrcsChangeSet changeSet;
   //@formatter:on

   private TransactionWriter writer;
   private List<DaoToSql> stores;

   @Before
   public void setUp() throws OseeCoreException {
      MockitoAnnotations.initMocks(this);

      writer = new TransactionWriter(logger, dbService, builder);

      stores = Arrays.asList(dao1, dao2);

      final Map<SqlOrderEnum, ArtifactJoinQuery> joins = new LinkedHashMap<SqlOrderEnum, ArtifactJoinQuery>();
      joins.put(SqlOrderEnum.ARTIFACTS, join1);
      joins.put(SqlOrderEnum.ATTRIBUTES, join2);

      when(join1.getQueryId()).thenReturn(88);
      when(join2.getQueryId()).thenReturn(89);

      when(tx.getBranch()).thenReturn(branch);
      when(branch.getId()).thenReturn(65);
      when(builder.getBinaryStores()).thenReturn(stores);
      when(builder.getTxNotCurrents()).thenAnswer(new Answer<Set<Entry<SqlOrderEnum, ArtifactJoinQuery>>>() {

         @Override
         public Set<Entry<SqlOrderEnum, ArtifactJoinQuery>> answer(InvocationOnMock invocation) throws Throwable {
            Set<Entry<SqlOrderEnum, ArtifactJoinQuery>> values = joins.entrySet();
            return values;
         }
      });
      when(dbService.getStatement(connection)).thenReturn(chStmt);

      when(chStmt.next()).thenReturn(true).thenReturn(true).thenReturn(false);
      when(chStmt.getInt("transaction_id")).thenReturn(51).thenReturn(52);
      when(chStmt.getLong("gamma_id")).thenReturn(80000L).thenReturn(80001L);
   }

   @Test
   public void testRollback() throws OseeCoreException {
      TransactionWriter spy = Mockito.spy(writer);

      OseeCoreException expected = new OseeCoreException("Testing");

      when(spy.getBinaryStores()).thenReturn(stores);
      doThrow(expected).when(dao1).rollBack();

      spy.rollback();

      verify(dao1).rollBack();
      verify(dao2).rollBack();

      verify(logger).error(expected, "Error during binary rollback [%s]", dao1);
   }

   @Test
   public void testWrite() throws OseeCoreException {
      InOrder inOrder = inOrder(builder, tx, join1, join2, dao1, dao2, dbService, chStmt);

      writer.write(connection, tx, changeSet);

      inOrder.verify(builder).accept(tx, changeSet);
      inOrder.verify(builder).getBinaryStores();
      inOrder.verify(dao1).persist();
      inOrder.verify(dao2).persist();

      inOrder.verify(builder).getTxNotCurrents();

      inOrder.verify(join1).store();
      inOrder.verify(chStmt).runPreparedQuery(SqlOrderEnum.ARTIFACTS.getTxsNotCurrentQuery(), 88);
      inOrder.verify(join1).delete();

      inOrder.verify(join2).store();
      inOrder.verify(chStmt).runPreparedQuery(SqlOrderEnum.ATTRIBUTES.getTxsNotCurrentQuery(), 89);
      inOrder.verify(join2).delete();

      inOrder.verify(builder).getInsertData(SqlOrderEnum.ARTIFACTS);
      inOrder.verify(builder).getInsertData(SqlOrderEnum.ATTRIBUTES);
      inOrder.verify(builder).getInsertData(SqlOrderEnum.RELATIONS);
      inOrder.verify(builder).getInsertData(SqlOrderEnum.TXS_DETAIL);
      inOrder.verify(builder).getInsertData(SqlOrderEnum.TXS);

      inOrder.verify(dbService).runBatchUpdate(eq(connection), eq(TransactionWriter.UPDATE_TXS_NOT_CURRENT),
         paramCaptor.capture());

      inOrder.verify(tx).clearDirty();
      inOrder.verify(builder).clear();

      Iterator<Object[]> params = paramCaptor.getValue().iterator();
      int index = 0;
      Object[] data = params.next();
      Assert.assertEquals(65, data[index++]);
      Assert.assertEquals(51, data[index++]);
      Assert.assertEquals(80000L, data[index++]);

      index = 0;
      data = params.next();
      Assert.assertEquals(65, data[index++]);
      Assert.assertEquals(52, data[index++]);
      Assert.assertEquals(80001L, data[index++]);
   }
}
