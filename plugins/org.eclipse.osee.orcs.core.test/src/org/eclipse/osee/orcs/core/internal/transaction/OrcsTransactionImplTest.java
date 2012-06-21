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

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.core.ds.BranchDataStore;
import org.eclipse.osee.orcs.core.ds.DataFactory;
import org.eclipse.osee.orcs.core.internal.SessionContext;
import org.eclipse.osee.orcs.core.internal.artifact.ArtifactFactory;
import org.eclipse.osee.orcs.core.internal.attribute.AttributeFactory;
import org.eclipse.osee.orcs.data.ArtifactWriteable;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

/**
 * @author John Misinco
 */
public class OrcsTransactionImplTest {

   @Mock
   private Log logger;
   @Mock
   private SessionContext sessionContext;
   @Mock
   private BranchDataStore dataStore;
   @Mock
   private ArtifactFactory artifactFactory;
   @Mock
   private AttributeFactory attributeFactory;
   @Mock
   private IOseeBranch branch;
   @Mock
   private DataFactory dataFactory;
   @Mock
   private ArtifactWriteable expected;
   private OrcsTransactionImpl transaction;

   @Before
   public void init() {
      MockitoAnnotations.initMocks(this);
      transaction = new OrcsTransactionImpl(logger, sessionContext, dataStore, artifactFactory, branch);
   }

   @Test
   public void testCreateArtifact() throws OseeCoreException {
      String guid = "guid";
      when(expected.getGuid()).thenReturn(guid);
      when(artifactFactory.createWriteableArtifact(branch, CoreArtifactTypes.Artifact, guid)).thenReturn(expected);
      ArtifactWriteable artifact = transaction.createArtifact(CoreArtifactTypes.Artifact, "test", guid);

      verify(expected).setName("test");
      assertNotNull(artifact);
   }

   @Test
   public void testDuplicateArtifact() {

   }
}
