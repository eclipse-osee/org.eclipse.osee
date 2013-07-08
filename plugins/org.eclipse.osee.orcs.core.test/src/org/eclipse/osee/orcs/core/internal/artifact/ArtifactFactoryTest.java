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

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.core.data.IAttributeType;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.exception.OseeArgumentException;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.model.cache.BranchCache;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.eclipse.osee.orcs.core.ds.ArtifactData;
import org.eclipse.osee.orcs.core.ds.ArtifactDataFactory;
import org.eclipse.osee.orcs.core.ds.AttributeData;
import org.eclipse.osee.orcs.core.ds.VersionData;
import org.eclipse.osee.orcs.core.internal.attribute.Attribute;
import org.eclipse.osee.orcs.core.internal.attribute.AttributeFactory;
import org.eclipse.osee.orcs.core.internal.attribute.AttributeManager;
import org.eclipse.osee.orcs.core.internal.relation.RelationContainer;
import org.eclipse.osee.orcs.core.internal.relation.RelationFactory;
import org.eclipse.osee.orcs.data.ArtifactTypes;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

/**
 * Test Case for {@link ArtifactFactory}
 * 
 * @author John Misinco
 */
public class ArtifactFactoryTest {

   @Rule
   public ExpectedException thrown = ExpectedException.none();

   // @formatter:off
   @Mock private Branch branch;
   @Mock private IArtifactType artifactType;
   @Mock private ArtifactData artifactData;
   @Mock private RelationContainer relationContainer;
   @Mock private VersionData artifactVersion;
   
   @Mock private ArtifactDataFactory dataFactory;
   @Mock private AttributeFactory attributeFactory;
   @Mock private RelationFactory relationFactory;
   @Mock private ArtifactTypes artifactTypeCache;
   @Mock private BranchCache branchCache;
   
   @Mock private Attribute<Object> attribute;
   @Mock private AttributeData attributeData;
   @Mock private ArtifactImpl source;
   
   @Mock private ArtifactData otherArtifactData;
   // @formatter:on

   private String guid;
   private ArtifactFactory artifactFactory;
   private List<IAttributeType> types;

   @Before
   public void init() throws OseeCoreException {
      MockitoAnnotations.initMocks(this);

      artifactFactory =
         new ArtifactFactory(dataFactory, attributeFactory, relationFactory, branchCache, artifactTypeCache);

      guid = GUID.create();

      types = new ArrayList<IAttributeType>();
      types.add(CoreAttributeTypes.RelationOrder);
      types.add(CoreAttributeTypes.City);
      types.add(CoreAttributeTypes.Annotation);

      when(artifactData.getLocalId()).thenReturn(45);
      when(artifactData.getGuid()).thenReturn(guid);
      when(artifactData.getTypeUuid()).thenReturn(65L);
      when(artifactData.getVersion()).thenReturn(artifactVersion);
      when(artifactVersion.getBranchId()).thenReturn(23);

      when(
         attributeFactory.copyAttribute(any(AttributeData.class), any(IOseeBranch.class), any(AttributeManager.class))).thenReturn(
         attribute);

      when(otherArtifactData.getLocalId()).thenReturn(45);
      when(otherArtifactData.getGuid()).thenReturn(guid);
      when(otherArtifactData.getTypeUuid()).thenReturn(65L);
      when(otherArtifactData.getVersion()).thenReturn(artifactVersion);

      when(relationFactory.createRelationContainer(45)).thenReturn(relationContainer);
      when(branchCache.getById(23)).thenReturn(branch);

      when(artifactTypeCache.getByUuid(65L)).thenReturn(artifactType);
   }

   @Test
   public void testCreateArtifactFromBranchTypeAndGuid() throws OseeCoreException {
      when(dataFactory.create(branch, artifactType, guid)).thenReturn(artifactData);

      ArtifactImpl artifact = artifactFactory.createArtifact(branch, artifactType, guid);

      verify(dataFactory).create(branch, artifactType, guid);
      assertEquals(artifactType, artifact.getArtifactType());
      assertEquals(guid, artifact.getGuid());
   }

   @Test
   public void testCreateArtifactFromArtifactData() throws OseeCoreException {
      when(relationFactory.createRelationContainer(45)).thenReturn(relationContainer);
      when(branchCache.getById(23)).thenReturn(branch);

      ArtifactImpl artifact = artifactFactory.createArtifact(artifactData);

      assertEquals(artifactType, artifact.getArtifactType());
      assertEquals(guid, artifact.getGuid());
   }

   @Test
   public void testCopyArtifact() throws OseeCoreException {
      when(source.getOrcsData()).thenReturn(artifactData);
      when(dataFactory.copy(branch, artifactData)).thenReturn(otherArtifactData);

      when(source.getAttributes(CoreAttributeTypes.Annotation)).thenAnswer(new ReturnAttribute(attribute));
      when(attribute.getOrcsData()).thenReturn(attributeData);
      when(artifactTypeCache.isValidAttributeType(artifactType, branch, CoreAttributeTypes.Annotation)).thenReturn(true);

      ArgumentCaptor<ArtifactImpl> implCapture = ArgumentCaptor.forClass(ArtifactImpl.class);

      ArtifactImpl actual = artifactFactory.copyArtifact(source, types, branch);

      verify(source, times(0)).getAttributes(CoreAttributeTypes.RelationOrder);
      verify(source, times(0)).getAttributes(CoreAttributeTypes.City);
      verify(source, times(1)).getAttributes(CoreAttributeTypes.Annotation);
      verify(attributeFactory).copyAttribute(eq(attributeData), eq(branch), implCapture.capture());

      Assert.assertTrue(implCapture.getValue().isLoaded());
      Assert.assertTrue(actual == implCapture.getValue());
   }

   @Test
   public void testIntroduceArtifactBranchException() throws OseeCoreException {
      when(source.getBranch()).thenReturn(branch);

      thrown.expect(OseeArgumentException.class);
      thrown.expectMessage("Source artifact is on the same branch as [" + branch + "]");
      artifactFactory.introduceArtifact(source, branch);
   }

   @Test
   public void testIntroduceArtifact() throws OseeCoreException {
      Branch otherBranch = mock(Branch.class);

      when(source.getBranch()).thenReturn(otherBranch);
      when(source.getOrcsData()).thenReturn(artifactData);

      when(dataFactory.introduce(branch, artifactData)).thenReturn(otherArtifactData);

      when(source.getExistingAttributeTypes()).thenAnswer(new ReturnExistingTypes(types));
      when(source.getAttributes(CoreAttributeTypes.Annotation)).thenAnswer(new ReturnAttribute(attribute));
      when(attribute.getOrcsData()).thenReturn(attributeData);
      when(artifactTypeCache.isValidAttributeType(artifactType, branch, CoreAttributeTypes.Annotation)).thenReturn(true);

      ArgumentCaptor<ArtifactImpl> implCapture = ArgumentCaptor.forClass(ArtifactImpl.class);

      ArtifactImpl actual = artifactFactory.introduceArtifact(source, branch);

      verify(source, times(0)).getAttributes(CoreAttributeTypes.RelationOrder);
      verify(source, times(0)).getAttributes(CoreAttributeTypes.City);
      verify(source, times(1)).getAttributes(CoreAttributeTypes.Annotation);

      verify(attributeFactory).introduceAttribute(eq(attributeData), eq(branch), implCapture.capture());

      Assert.assertTrue(implCapture.getValue().isLoaded());
      Assert.assertTrue(actual == implCapture.getValue());
   }

   @Test
   public void testClone() throws OseeCoreException {
      when(source.getOrcsData()).thenReturn(artifactData);
      when(dataFactory.copy(branch, artifactData)).thenReturn(otherArtifactData);

      when(source.getExistingAttributeTypes()).thenAnswer(new ReturnExistingTypes(types));
      when(source.getAttributes(CoreAttributeTypes.Annotation)).thenAnswer(new ReturnAttribute(attribute));
      when(attribute.getOrcsData()).thenReturn(attributeData);
      when(artifactTypeCache.isValidAttributeType(artifactType, branch, CoreAttributeTypes.Annotation)).thenReturn(true);

      ArgumentCaptor<ArtifactImpl> implCapture = ArgumentCaptor.forClass(ArtifactImpl.class);

      ArtifactImpl actual = artifactFactory.copyArtifact(source, types, branch);

      verify(source, times(0)).getAttributes(CoreAttributeTypes.RelationOrder);
      verify(source, times(0)).getAttributes(CoreAttributeTypes.City);
      verify(source, times(1)).getAttributes(CoreAttributeTypes.Annotation);
      verify(attributeFactory).copyAttribute(eq(attributeData), eq(branch), implCapture.capture());
      Assert.assertTrue(implCapture.getValue().isLoaded());
      Assert.assertTrue(actual == implCapture.getValue());
   }

   private static final class ReturnAttribute implements Answer<List<Attribute<Object>>> {

      private final Attribute<Object> attribute;

      public ReturnAttribute(Attribute<Object> attribute) {
         this.attribute = attribute;
      }

      @Override
      public List<Attribute<Object>> answer(InvocationOnMock invocation) throws Throwable {
         return Collections.singletonList(attribute);
      }
   };

   private static final class ReturnExistingTypes implements Answer<List<IAttributeType>> {

      private final List<IAttributeType> types;

      public ReturnExistingTypes(List<IAttributeType> types) {
         this.types = types;
      }

      @Override
      public List<IAttributeType> answer(InvocationOnMock invocation) throws Throwable {
         return types;
      }
   };
}
