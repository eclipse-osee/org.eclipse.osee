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

import static org.eclipse.osee.framework.core.enums.CoreArtifactTypes.Artifact;
import static org.eclipse.osee.framework.core.enums.CoreBranches.COMMON;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.core.data.Branch;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.DeletionFlag;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.eclipse.osee.orcs.OrcsSession;
import org.eclipse.osee.orcs.core.ds.ArtifactData;
import org.eclipse.osee.orcs.core.ds.ArtifactDataFactory;
import org.eclipse.osee.orcs.core.ds.Attribute;
import org.eclipse.osee.orcs.core.ds.AttributeData;
import org.eclipse.osee.orcs.core.ds.VersionData;
import org.eclipse.osee.orcs.core.internal.attribute.AttributeFactory;
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
   @Mock private ArtifactData artifactData;
   @Mock private VersionData artifactVersion;

   @Mock private ArtifactDataFactory dataFactory;
   @Mock private AttributeFactory attributeFactory;
   @Mock private RelationFactory relationFactory;
   @Mock private ArtifactTypes artifactTypeCache;

   @Mock private Attribute<Object> attribute;
   @Mock private AttributeData<Object> attributeData;
   @Mock private Artifact source;
   @Mock private Artifact destination;
   @Mock private OrcsSession session;

   @Mock private ArtifactData otherArtifactData;
   // @formatter:on

   private String guid;
   private ArtifactFactory artifactFactory;
   private List<AttributeTypeId> types;

   @Before
   public void init() {
      MockitoAnnotations.initMocks(this);

      artifactFactory = new ArtifactFactory(dataFactory, attributeFactory, artifactTypeCache);

      guid = GUID.create();

      types = new ArrayList<>();
      types.add(CoreAttributeTypes.RelationOrder);
      types.add(CoreAttributeTypes.City);
      types.add(CoreAttributeTypes.Annotation);

      when(artifactData.getLocalId()).thenReturn(45);
      when(artifactData.getGuid()).thenReturn(guid);
      when(artifactData.getType()).thenReturn(Artifact);
      when(artifactData.getVersion()).thenReturn(artifactVersion);
      when(artifactVersion.getBranch()).thenReturn(COMMON);
      when(source.getOrcsData()).thenReturn(artifactData);

      when(attributeFactory.copyAttribute(any(), any(), any())).thenReturn(attribute);

      when(otherArtifactData.getLocalId()).thenReturn(45);
      when(otherArtifactData.getGuid()).thenReturn(guid);
      when(otherArtifactData.getType()).thenReturn(Artifact);
      when(otherArtifactData.getVersion()).thenReturn(artifactVersion);

      // TODO RDB: make this line unnecessary
      when(artifactTypeCache.get(Artifact.getId())).thenReturn(Artifact);

   }

   @Test
   public void testCreateArtifactFromBranchTypeAndGuid() {
      when(dataFactory.create(COMMON, Artifact, guid)).thenReturn(artifactData);

      Artifact artifact = artifactFactory.createArtifact(session, COMMON, Artifact, guid);

      verify(dataFactory).create(COMMON, Artifact, guid);
      assertEquals(Artifact, artifact.getArtifactTypeId());
      assertEquals(guid, artifact.getGuid());
   }

   @Test
   public void testCreateArtifactFromBranchTypeAndGuidAndUuid() {
      long uuid = 93456L;
      when(dataFactory.create(COMMON, Artifact, guid, uuid)).thenReturn(artifactData);

      Artifact artifact = artifactFactory.createArtifact(session, COMMON, Artifact, guid, uuid);

      verify(dataFactory).create(COMMON, Artifact, guid, uuid);
      assertEquals(Artifact, artifact.getArtifactTypeId());
      assertEquals(guid, artifact.getGuid());
   }

   @Test
   public void testCreateArtifactFromArtifactData() {
      Artifact artifact = artifactFactory.createArtifact(session, artifactData);

      assertEquals(Artifact, artifact.getArtifactTypeId());
      assertEquals(guid, artifact.getGuid());
   }

   @Test
   public void testCopyArtifact() {
      when(dataFactory.copy(COMMON, artifactData)).thenReturn(otherArtifactData);

      when(source.getAttributes(CoreAttributeTypes.Annotation)).thenAnswer(new ReturnAttribute(attribute));
      when(attribute.getOrcsData()).thenReturn(attributeData);

      when(artifactTypeCache.isValidAttributeType(eq(Artifact), any(), eq(CoreAttributeTypes.Annotation))).thenReturn(
         true);

      ArgumentCaptor<Artifact> implCapture = ArgumentCaptor.forClass(Artifact.class);

      Artifact actual = artifactFactory.copyArtifact(session, source, types, COMMON);

      verify(source, times(0)).getAttributes(CoreAttributeTypes.RelationOrder);
      verify(source, times(0)).getAttributes(CoreAttributeTypes.City);
      verify(source, times(1)).getAttributes(CoreAttributeTypes.Annotation);
      verify(attributeFactory).copyAttribute(eq(attributeData), eq(COMMON), implCapture.capture());

      Assert.assertTrue(implCapture.getValue().isLoaded());
      Assert.assertTrue(actual == implCapture.getValue());
   }

   @Test
   public void testIntroduceArtifact() {
      when(dataFactory.introduce(COMMON, artifactData)).thenReturn(otherArtifactData);

      when(source.getExistingAttributeTypes()).thenAnswer(new ReturnExistingTypes(types));
      when(source.getAttributes(DeletionFlag.INCLUDE_DELETED)).thenAnswer(new ReturnAttribute(attribute));
      when(attribute.getOrcsData()).thenReturn(attributeData);
      when(artifactTypeCache.isValidAttributeType(Artifact, branch, CoreAttributeTypes.Annotation)).thenReturn(true);
      when(attribute.getAttributeType()).thenReturn(CoreAttributeTypes.Annotation);
      when(destination.isAttributeTypeValid(CoreAttributeTypes.Annotation)).thenReturn(true);

      Artifact actual = artifactFactory.introduceArtifact(session, source, destination, COMMON);

      verify(attributeFactory).introduceAttribute(eq(attributeData), eq(COMMON), eq(destination));
      Assert.assertTrue(actual == destination);
   }

   @Test
   public void testClone() {
      when(dataFactory.copy(COMMON, artifactData)).thenReturn(otherArtifactData);

      when(source.getExistingAttributeTypes()).thenAnswer(new ReturnExistingTypes(types));
      when(source.getAttributes(CoreAttributeTypes.Annotation)).thenAnswer(new ReturnAttribute(attribute));
      when(attribute.getOrcsData()).thenReturn(attributeData);
      when(artifactTypeCache.isValidAttributeType(eq(Artifact), any(), eq(CoreAttributeTypes.Annotation))).thenReturn(
         true);

      ArgumentCaptor<Artifact> implCapture = ArgumentCaptor.forClass(Artifact.class);

      Artifact actual = artifactFactory.copyArtifact(session, source, types, COMMON);

      verify(source, times(0)).getAttributes(CoreAttributeTypes.RelationOrder);
      verify(source, times(0)).getAttributes(CoreAttributeTypes.City);
      verify(source, times(1)).getAttributes(CoreAttributeTypes.Annotation);
      verify(attributeFactory).copyAttribute(eq(attributeData), eq(COMMON), implCapture.capture());
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

   private static final class ReturnExistingTypes implements Answer<List<AttributeTypeId>> {

      private final List<AttributeTypeId> types;

      public ReturnExistingTypes(List<AttributeTypeId> types) {
         this.types = types;
      }

      @Override
      public List<AttributeTypeId> answer(InvocationOnMock invocation) throws Throwable {
         return types;
      }
   };
}
