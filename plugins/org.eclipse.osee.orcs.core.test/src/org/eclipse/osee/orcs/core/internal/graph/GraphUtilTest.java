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
package org.eclipse.osee.orcs.core.internal.graph;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.core.exception.OseeArgumentException;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.orcs.OrcsSession;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;

/**
 * Test Case for {@link GraphUtil}
 * 
 * @author Megumi Telles
 */
public class GraphUtilTest {

   private static final IOseeBranch BRANCH = CoreBranches.COMMON;
   private static final int TRANSACTION_ID = 231214214;

   @Rule
   public ExpectedException thrown = ExpectedException.none();

   // @formatter:off
   @Mock private OrcsSession session;
   @Mock private GraphData graph;
   // @formatter:on

   private GraphProvider provider;

   @Before
   public void setUp() {
      initMocks(this);
      provider = GraphUtil.asProvider(graph);

      when(graph.getBranch()).thenReturn(BRANCH);
      when(graph.getTransaction()).thenReturn(TRANSACTION_ID);
   }

   @Test
   public void testAsProviderNullBranch() throws OseeCoreException {
      assertNotNull(provider);

      thrown.expect(OseeArgumentException.class);
      thrown.expectMessage("branch cannot be null - Invalid branch - can't provide graph");
      provider.getGraph(session, null, TRANSACTION_ID);
   }

   @Test
   public void testAsProviderBranchNotSame() throws OseeCoreException {
      assertNotNull(provider);

      thrown.expect(OseeArgumentException.class);
      thrown.expectMessage("Invalid branch - Graph's branch[Common] does not equals requested branch[System Root Branch]");
      provider.getGraph(session, CoreBranches.SYSTEM_ROOT, TRANSACTION_ID);
   }

   @Test
   public void testAsProviderTxIdNotSame() throws OseeCoreException {
      assertNotNull(provider);

      int txId = 123456789;

      thrown.expect(OseeArgumentException.class);
      thrown.expectMessage("Invalid transactionId - Graph's transactionId[231214214] does not equals requested transactionId[123456789]");
      provider.getGraph(session, BRANCH, txId);
   }

   @Test
   public void testAsProviderGetName() throws OseeCoreException {
      assertNotNull(provider);
      assertEquals(graph, provider.getGraph(session, BRANCH, TRANSACTION_ID));
   }
}
