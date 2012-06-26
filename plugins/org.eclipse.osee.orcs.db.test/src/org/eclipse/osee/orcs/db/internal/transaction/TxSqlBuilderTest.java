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

import java.util.ArrayList;
import java.util.List;
import org.eclipse.osee.orcs.core.ds.ArtifactTransactionData;
import org.eclipse.osee.orcs.db.internal.loader.IdFactory;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

/**
 * Test Case for {@link TxSqlBuilder}
 * 
 * @author Roberto E. Escobar
 */
public class TxSqlBuilderTest {

   @Mock
   IdFactory idFactory;

   @Before
   public void setUp() {
      MockitoAnnotations.initMocks(this);
   }

   @Test
   public void testGetGammaIdAndIsNewGammaId() {
      List<ArtifactTransactionData> txData = new ArrayList<ArtifactTransactionData>();
      //      TxSqlBuilder builder = new TxSqlBuilder(idFactory, txData);
      //      builder.build();

      //      builder.getBinaryTxs();
      //
      //      builder.getObjectSql();
      //      builder.getObjectParameters();
      //
      //      builder.getTxParameters(sqlKey);
      //      builder.getTxSql();

   }
}
