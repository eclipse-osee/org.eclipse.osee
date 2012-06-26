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
import java.util.Collection;
import org.eclipse.osee.framework.core.data.IArtifactToken;
import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.core.data.IAttributeType;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.type.AttributeType;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.core.ds.BranchDataStore;
import org.eclipse.osee.orcs.core.internal.SessionContext;
import org.eclipse.osee.orcs.core.internal.artifact.ArtifactFactory;
import org.eclipse.osee.orcs.data.ArtifactReadable;
import org.eclipse.osee.orcs.data.ArtifactWriteable;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

/**
 * @author John Misinco
 */
public class OrcsTransactionImplTest {

   // @formatter:off
   @Mock private Log logger;
   @Mock private SessionContext sessionContext;
   @Mock private BranchDataStore dataStore;
   @Mock private ArtifactFactory artifactFactory;
   @Mock private IOseeBranch branch;
   @Mock private ArtifactWriteable expected;
   // @formatter:on

   private OrcsTransactionImpl transaction;
   private String guid;
   private final IArtifactType artType = CoreArtifactTypes.Artifact;

   @Before
   public void init() {
      MockitoAnnotations.initMocks(this);
      transaction = new OrcsTransactionImpl(logger, sessionContext, dataStore, artifactFactory, branch);
      guid = GUID.create();
      when(expected.getGuid()).thenReturn(guid);
   }

   @Test
   public void testCreateArtifact() throws OseeCoreException {
      String name = "testCreateArtifact";
      when(artifactFactory.createWriteableArtifact(branch, artType, guid)).thenReturn(expected);

      ArtifactWriteable artifact = transaction.createArtifact(artType, name, guid);

      verify(expected).setName(name);
      verify(artifactFactory).createWriteableArtifact(branch, artType, guid);
      assertNotNull(artifact);
   }

   @Test
   public void testCreateArtifactFromToken() throws OseeCoreException {
      IArtifactToken token = mock(IArtifactToken.class);
      String name = "testCreateArtifactFromToken";

      when(token.getName()).thenReturn(name);
      when(token.getArtifactType()).thenReturn(artType);
      when(token.getGuid()).thenReturn(guid);
      when(artifactFactory.createWriteableArtifact(branch, artType, guid)).thenReturn(expected);

      ArtifactWriteable artifact = transaction.createArtifact(token);

      verify(expected).setName(name);
      verify(artifactFactory).createWriteableArtifact(branch, artType, guid);
      assertNotNull(artifact);
   }

   @Test
   public void testDuplicateArtifact() throws OseeCoreException {
      ArtifactReadable source = mock(ArtifactReadable.class);
      final Collection<AttributeType> types = mock(Collection.class);
      when(source.getGuid()).thenReturn(guid);
      when(source.getExistingAttributeTypes()).thenAnswer(new Answer<Collection<? extends IAttributeType>>() {

         @Override
         public Collection<? extends IAttributeType> answer(InvocationOnMock invocation) throws Throwable {
            return types;
         }
      });
      when(artifactFactory.copyArtifact(source, types, branch)).thenReturn(expected);

      transaction.duplicateArtifact(source);
      verify(artifactFactory.copyArtifact(source, types, branch));
   }

   @Test
   public void testIntroduceArtifact() throws OseeCoreException {
      ArtifactReadable source = mock(ArtifactReadable.class);
      when(artifactFactory.introduceArtifact(source, branch)).thenReturn(expected);
      transaction.introduceArtifact(source);
      verify(artifactFactory.introduceArtifact(source, branch));
   }
}
