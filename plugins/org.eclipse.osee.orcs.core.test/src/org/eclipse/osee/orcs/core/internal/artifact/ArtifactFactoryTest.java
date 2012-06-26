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
package org.eclipse.osee.orcs.core.internal.artifact;

import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;
import java.util.Collection;
import java.util.List;
import org.eclipse.osee.framework.core.data.IAttributeType;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.model.cache.ArtifactTypeCache;
import org.eclipse.osee.framework.core.model.cache.BranchCache;
import org.eclipse.osee.framework.core.model.type.ArtifactType;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.eclipse.osee.orcs.core.ds.ArtifactData;
import org.eclipse.osee.orcs.core.ds.ArtifactDataFactory;
import org.eclipse.osee.orcs.core.ds.ArtifactTransactionData;
import org.eclipse.osee.orcs.core.ds.AttributeData;
import org.eclipse.osee.orcs.core.ds.VersionData;
import org.eclipse.osee.orcs.core.internal.attribute.AttributeFactory;
import org.eclipse.osee.orcs.core.internal.relation.RelationFactory;
import org.eclipse.osee.orcs.data.ArtifactReadable;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class ArtifactFactoryTest {

   // @formatter:off
   @Mock private Branch branch;
   @Mock private ArtifactType artifactType;
   @Mock private ArtifactData artifactData;
   @Mock private RelationContainer relationContainer;
   // @formatter:on

   private final String guid = GUID.create();

   private ArtifactFactory artifactFactory;

   @Before
   public void init() throws OseeCoreException {
      MockitoAnnotations.initMocks(this);
      AttributeFactory attributeFactory = mock(AttributeFactory.class);
      RelationFactory relationFactory = mock(RelationFactory.class);
      ArtifactTypeCache artifactTypeCache = mock(ArtifactTypeCache.class);
      BranchCache branchCache = mock(BranchCache.class);
      ArtifactDataFactory factory = mock(ArtifactDataFactory.class);
      artifactFactory = new ArtifactFactory(factory, attributeFactory, relationFactory, artifactTypeCache, branchCache);
      when(artifactTypeCache.getByGuid(anyLong())).thenReturn(artifactType);
      when(branchCache.getById(anyInt())).thenReturn(branch);
      VersionData version = mock(VersionData.class);
      when(artifactData.getVersion()).thenReturn(version);
      when(relationFactory.createRelationContainer(anyInt())).thenReturn(relationContainer);
      when(factory.create(branch, artifactType, guid)).thenReturn(artifactData);
      when(factory.copy(branch, artifactData)).thenReturn(artifactData);
      when(artifactData.getGuid()).thenReturn(guid);
   }

   @Test
   public void testCreateWriteableArtifact() throws OseeCoreException {
      ArtifactReadable artifact = artifactFactory.createWriteableArtifact(artifactData);
      assertEquals(artifactType, artifact.getArtifactType());
      assertEquals(guid, artifact.getGuid());

      artifact = artifactFactory.createWriteableArtifact(branch, artifactType, guid);
      assertEquals(artifactType, artifact.getArtifactType());
      assertEquals(guid, artifact.getGuid());
   }

   @Test
   public void testCreateReadableArtifact() throws OseeCoreException {
      ArtifactReadable artifact = artifactFactory.createReadableArtifact(artifactData);
      assertEquals(artifactType, artifact.getArtifactType());
      assertEquals(guid, artifact.getGuid());
   }

   @Test
   @Ignore
   public void testCopyArtifact() throws OseeCoreException {
      WritableArtifactProxy source = mock(WritableArtifactProxy.class);
      Collection<? extends IAttributeType> types = mock(Collection.class);
      ArtifactImpl impl = mock(ArtifactImpl.class);
      when(source.getProxiedObject()).thenReturn(impl);
      when(impl.getOrcsData()).thenReturn(artifactData);
      artifactFactory.copyArtifact(source, types, branch);
   }

   @Test
   public void testSetBackingData() throws OseeCoreException {
      WritableArtifactProxy writeable = mock(WritableArtifactProxy.class);
      ArtifactImpl original = mock(ArtifactImpl.class);
      ArtifactImpl proxied = mock(ArtifactImpl.class);
      List<AttributeData> attrData = mock(List.class);
      ArtifactTransactionData data = mock(ArtifactTransactionData.class);

      when(writeable.getOriginal()).thenReturn(original);
      when(writeable.getProxiedObject()).thenReturn(proxied);
      when(data.getArtifactData()).thenReturn(artifactData);
      when(data.getAttributeData()).thenReturn(attrData);

      artifactFactory.setBackingData(writeable, data);

      verify(original).setOrcsData(artifactData);
      verify(original).setBackingData(attrData);
      verify(proxied).setOrcsData(artifactData);
      verify(proxied).setBackingData(attrData);
   }
}
