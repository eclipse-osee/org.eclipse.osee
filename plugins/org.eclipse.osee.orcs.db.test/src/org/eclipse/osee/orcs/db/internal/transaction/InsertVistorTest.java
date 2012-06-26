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

import org.eclipse.osee.orcs.core.ds.VersionData;
import org.eclipse.osee.orcs.db.internal.loader.IdFactory;
import org.eclipse.osee.orcs.db.internal.loader.data.VersionDataImpl;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

/**
 * Test Case for {@link TxSqlBuilder}
 * 
 * @author Roberto E. Escobar
 */
public class InsertVistorTest {

   @Mock
   IdFactory idFactory;

   private InsertVisitor visitor;

   @Before
   public void setUp() {
      MockitoAnnotations.initMocks(this);
      //      visitor = new InsertVisitor(idFactory);
   }

   @Test
   public void testAcceptArtifact() {
      VersionData vData = new VersionDataImpl();
      //      vData.setBranchId(branchId);
      //      vData.setGammaId(gamma);
      //      vData.setTransactionId(txId);
      //
      //      ArtifactData data = new ArtifactDataImpl(vData);
      //      data.setGuid(guid);
      //      data.setHumanReadableId(humanReadableId);
      //      data.setLoadedModType(modType);
      //      data.setLocalId(localId);
      //      data.setModType(modType);
      //      data.setTypeUuid(typeUuid);
      //
      //      visitor.accept(data);

   }

   @Test
   public void testAcceptAttribute() {
      //      visitor.accept(data);
      //      visitor.accept(data);
      //      visitor.accept(data);
   }

   @Test
   public void testAcceptRelation() {
      //      visitor.accept(data);
      //      visitor.accept(data);
      //      visitor.accept(data);
   }
}
