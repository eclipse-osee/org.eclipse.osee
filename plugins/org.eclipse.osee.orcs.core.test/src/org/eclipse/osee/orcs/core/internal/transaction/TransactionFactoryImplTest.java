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
package org.eclipse.osee.orcs.core.internal.transaction;

import org.junit.Assert;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.core.exception.OseeArgumentException;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.core.ds.BranchDataStore;
import org.eclipse.osee.orcs.core.internal.SessionContext;
import org.eclipse.osee.orcs.core.internal.proxy.ArtifactProxyFactory;
import org.eclipse.osee.orcs.core.internal.transaction.TxDataManagerImpl.TxDataHandlerFactory;
import org.eclipse.osee.orcs.data.ArtifactReadable;
import org.eclipse.osee.orcs.transaction.OrcsTransaction;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

/**
 * Test Case for {@link TransactionFactoryImpl}
 * 
 * @author Roberto E. Escobar
 */
public class TransactionFactoryImplTest {

   @Rule
   public ExpectedException thrown = ExpectedException.none();

   // @formatter:off
   @Mock private Log logger;
   @Mock private SessionContext sessionContext;
   @Mock private BranchDataStore branchDataStore;
   @Mock private ArtifactProxyFactory artifactFactory;
   @Mock private TxDataHandlerFactory dataFactory;
   @Mock private ArtifactReadable expectedAuthor;
   // @formatter:on

   private final IOseeBranch expectedBranch = CoreBranches.COMMON;
   private TransactionFactoryImpl factory;

   @Before
   public void init() {
      MockitoAnnotations.initMocks(this);
      factory = new TransactionFactoryImpl(logger, sessionContext, branchDataStore, artifactFactory, dataFactory);
   }

   @Test
   public void testNullBranch() throws OseeCoreException {
      thrown.expect(OseeArgumentException.class);
      thrown.expectMessage("branch cannot be null");
      factory.createTransaction(null, expectedAuthor, "my comment");
   }

   @Test
   public void testNullAuthor() throws OseeCoreException {
      thrown.expect(OseeArgumentException.class);
      thrown.expectMessage("author cannot be null");
      factory.createTransaction(expectedBranch, null, "my comment");
   }

   @Test
   public void testNullComment() throws OseeCoreException {
      thrown.expect(OseeArgumentException.class);
      thrown.expectMessage("comment cannot be null");
      factory.createTransaction(expectedBranch, expectedAuthor, null);
   }

   @Test
   public void testEmptyComment() throws OseeCoreException {
      thrown.expect(OseeArgumentException.class);
      thrown.expectMessage("comment cannot be empty");
      factory.createTransaction(expectedBranch, expectedAuthor, "");
   }

   @Test
   public void testCreateTransaction() throws OseeCoreException {
      String expectedComment = "This is my comment";

      OrcsTransaction tx = factory.createTransaction(expectedBranch, expectedAuthor, expectedComment);
      Assert.assertNotNull(tx);
      Assert.assertEquals(expectedBranch, tx.getBranch());
      Assert.assertEquals(expectedAuthor, tx.getAuthor());
      Assert.assertEquals(expectedComment, tx.getComment());
   }
}
