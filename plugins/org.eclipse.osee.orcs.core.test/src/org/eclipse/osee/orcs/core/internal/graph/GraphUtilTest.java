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

import static org.eclipse.osee.framework.core.enums.CoreBranches.SYSTEM_ROOT;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
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

   private static final Long BRANCH_ID = CoreBranches.COMMON.getUuid();
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

      when(graph.getBranchUuid()).thenReturn(BRANCH_ID);
      when(graph.getTransaction()).thenReturn(TRANSACTION_ID);
   }

   @Test
   public void testAsProviderBranchNotSame() throws OseeCoreException {
      assertNotNull(provider);

      thrown.expect(OseeArgumentException.class);
      thrown.expectMessage(String.format("Invalid branch - Graph's branch[%s] does not equals requested branch[%s]",
         BRANCH_ID, SYSTEM_ROOT.getUuid()));
      provider.getGraph(session, SYSTEM_ROOT.getUuid(), TRANSACTION_ID);
   }

   @Test
   public void testAsProviderTxIdNotSame() throws OseeCoreException {
      assertNotNull(provider);

      int txId = 123456789;

      thrown.expect(OseeArgumentException.class);
      thrown.expectMessage(
         "Invalid transactionId - Graph's transactionId[231214214] does not equals requested transactionId[123456789]");
      provider.getGraph(session, BRANCH_ID, txId);
   }

   @Test
   public void testAsProviderGetName() throws OseeCoreException {
      assertNotNull(provider);
      assertEquals(graph, provider.getGraph(session, BRANCH_ID, TRANSACTION_ID));
   }
}
