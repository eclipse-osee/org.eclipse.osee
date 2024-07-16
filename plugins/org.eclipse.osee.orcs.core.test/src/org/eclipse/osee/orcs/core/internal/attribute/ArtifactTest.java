/*********************************************************************
 * Copyright (c) 2012 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.orcs.core.internal.attribute;

import static org.eclipse.osee.framework.core.enums.CoreAttributeTypes.GeneralStringData;
import static org.eclipse.osee.framework.core.enums.CoreAttributeTypes.Name;
import static org.eclipse.osee.framework.core.enums.CoreAttributeTypes.PublishInline;
import static org.eclipse.osee.framework.core.enums.CoreBranches.COMMON;
import static org.eclipse.osee.framework.core.enums.ModificationType.ARTIFACT_DELETED;
import static org.eclipse.osee.framework.core.enums.ModificationType.DELETED;
import static org.eclipse.osee.framework.core.enums.ModificationType.NEW;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.util.Arrays;
import java.util.List;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.TransactionId;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.core.exception.MultipleAttributesExist;
import org.eclipse.osee.framework.jdk.core.type.OseeStateException;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.eclipse.osee.orcs.core.ds.ArtifactData;
import org.eclipse.osee.orcs.core.ds.Attribute;
import org.eclipse.osee.orcs.core.ds.AttributeData;
import org.eclipse.osee.orcs.core.ds.VersionData;
import org.eclipse.osee.orcs.core.internal.artifact.Artifact;
import org.eclipse.osee.orcs.core.internal.artifact.ArtifactImpl;
import org.eclipse.osee.orcs.core.internal.graph.GraphData;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

/**
 * @author John Misinco
 */
public class ArtifactTest {

   @Rule
   public ExpectedException thrown = ExpectedException.none();

   // @formatter:off
   @Mock private ArtifactData artifactData;
   @Mock private AttributeFactory attributeFactory;

   @Mock private VersionData version;
   @Mock private AttributeData attributeData;

   @SuppressWarnings("rawtypes")
   @Mock private Attribute attribute;
   @SuppressWarnings("rawtypes")
   @Mock private Attribute notDeleted;
   @SuppressWarnings("rawtypes")
   @Mock private Attribute deleted;
   @SuppressWarnings("rawtypes")
   @Mock private Attribute differentType;

   @Mock private GraphData graph;
   // @formatter:on

   private Artifact artifact;
   private final String guid = GUID.create();
   private final AttributeTypeToken attributeType = CoreAttributeTypes.Annotation;
   private final ArtifactTypeToken artifactType = CoreArtifactTypes.GeneralData;

   @SuppressWarnings("unchecked")
   @Before
   public void init() {
      MockitoAnnotations.initMocks(this);
      when(artifactData.getArtifactType()).thenReturn(artifactType);
      artifact = new ArtifactImpl(artifactData, attributeFactory);
      artifact.setGraph(graph);

      when(attributeFactory.createAttribute(any(), any())).thenReturn(attribute);
      when(attributeFactory.createAttributeWithDefaults(any(), any(), any())).thenReturn(attribute);
      when(attribute.getOrcsData()).thenReturn(attributeData);

      when(artifactData.getGuid()).thenReturn(guid);
      when(artifactData.getVersion()).thenReturn(version);
      when(version.getBranch()).thenReturn(COMMON);
      when(artifactData.getType()).thenReturn(artifactType);

      when(deleted.getModificationType()).thenReturn(ModificationType.ARTIFACT_DELETED);
      when(deleted.isDeleted()).thenReturn(true);
      when(notDeleted.getModificationType()).thenReturn(ModificationType.NEW);
      when(notDeleted.getOrcsData()).thenReturn(attributeData);
      when(notDeleted.getAttributeType()).thenReturn(attributeType);
      when(deleted.getOrcsData()).thenReturn(attributeData);
      when(differentType.getOrcsData()).thenReturn(attributeData);
      when(differentType.getModificationType()).thenReturn(ModificationType.NEW);
   }

   @Test
   @SuppressWarnings("unchecked")
   public void testAddAndGet() {
      Attribute<Object> attribute = mock(Attribute.class);
      when(attribute.getOrcsData()).thenReturn(attributeData);
      Assert.assertEquals(0, artifact.getAttributes().size());
      artifact.add(CoreAttributeTypes.GeneralStringData, attribute);
      Assert.assertTrue(artifact.getAttributes().contains(attribute));
      Assert.assertEquals(1, artifact.getAttributes().size());
   }

   @Test
   @SuppressWarnings("rawtypes")
   public void testAddException() {
      Attribute one = mock(Attribute.class);
      Attribute two = mock(Attribute.class);
      when(one.getOrcsData()).thenReturn(attributeData);
      when(two.getOrcsData()).thenReturn(attributeData);

      artifact.add(attributeType, one);
      artifact.add(attributeType, two);
      Assert.assertEquals(2, artifact.getAttributes(attributeType).size());
   }

   @Test
   @SuppressWarnings("unchecked")
   public void testAreAttributesDirty() {
      Attribute<Object> attribute = mock(Attribute.class);
      when(attribute.getOrcsData()).thenReturn(attributeData);
      artifact.add(CoreAttributeTypes.GeneralStringData, attribute);
      Assert.assertFalse(artifact.areAttributesDirty());
      when(attribute.isDirty()).thenReturn(true);
      Assert.assertTrue(artifact.areAttributesDirty());
   }

   @Test
   public void testCreateAttribute() {
      artifact.createAttribute(GeneralStringData);
      verify(attributeFactory).createAttributeWithDefaults(artifact, artifactData, GeneralStringData);
   }

   @Test
   public void testGetGuid() {
      artifact.getGuid();
      verify(artifactData).getGuid();
   }

   @Test
   public void testGetTransactionId() {
      TransactionId expected = TransactionId.valueOf(4321);
      when(graph.getTransaction()).thenReturn(expected);
      assertEquals(expected, artifact.getTransaction());
      verify(graph).getTransaction();
   }

   @Test
   public void testLastModifiedTransaction() {
      TransactionId expected = TransactionId.valueOf(10);
      when(version.getTransactionId()).thenReturn(expected);
      assertEquals(expected, artifact.getLastModifiedTransaction());
      verify(version).getTransactionId();
   }

   @Test
   @SuppressWarnings({"rawtypes", "unchecked"})
   public void testSetName() {
      Attribute attr = mock(Attribute.class);
      when(attr.getOrcsData()).thenReturn(attributeData);
      when(attributeFactory.createAttributeWithDefaults(any(), any(), eq(CoreAttributeTypes.Name))).thenReturn(attr);
      artifact.setName("test");
      verify(attr).setFromString("test");
   }

   @Test
   public void testSetArtifactType() {
      when(version.isInStorage()).thenReturn(true);

      artifact.setArtifactType(CoreArtifactTypes.CodeUnit);

      verify(artifactData).setType(CoreArtifactTypes.CodeUnit);
      verify(artifactData).setModType(ModificationType.MODIFIED);

      reset(version);
      reset(artifactData);

      when(artifactData.getVersion()).thenReturn(version);
      when(artifactData.getGuid()).thenReturn(guid);
      when(artifactData.getVersion()).thenReturn(version);
      when(artifactData.getType()).thenReturn(artifactType);

      artifact.setArtifactType(CoreArtifactTypes.CodeUnit);
      verify(artifactData, never()).setModType(ModificationType.MODIFIED);
   }

   @Test
   public void testIsOfType() {
      assertFalse(artifact.isOfType(CoreArtifactTypes.CodeUnit));
   }

   @Test
   @SuppressWarnings("rawtypes")
   public void testIsDirty() {
      Assert.assertFalse(artifact.isDirty());

      // add dirty attribute
      Attribute dirty = mock(Attribute.class);
      when(dirty.getOrcsData()).thenReturn(attributeData);
      when(dirty.isDirty()).thenReturn(true);
      artifact.add(CoreAttributeTypes.Active, dirty);
      Assert.assertTrue(artifact.isDirty());

      // change artifactType
      reset(dirty);
      Assert.assertFalse(artifact.isDirty());
      artifact.setArtifactType(CoreArtifactTypes.CodeUnit);
      Assert.assertTrue(artifact.isDirty());

      // set mod type to replace with version
      artifact.setOrcsData(artifactData);
      Assert.assertFalse(artifact.isDirty());
      when(artifactData.getModType()).thenReturn(ModificationType.REPLACED_WITH_VERSION);
      Assert.assertTrue(artifact.isDirty());
   }

   @Test
   public void testIsDeleted() {
      for (ModificationType modType : Arrays.asList(ARTIFACT_DELETED, DELETED, NEW)) {
         reset(artifactData);
         when(artifactData.getModType()).thenReturn(modType);
         Assert.assertEquals(modType.isDeleted(), artifact.isDeleted());
      }
   }

   @Test
   public void testIsAttributeTypeValid() {
      Assert.assertTrue(artifact.isAttributeTypeValid(GeneralStringData));
   }

   @Test
   public void testGetValidAttributeTypes() {
      Assert.assertTrue(artifact.getValidAttributeTypes().contains(GeneralStringData));
      Assert.assertTrue(artifact.getValidAttributeTypes().contains(PublishInline));
      Assert.assertTrue(artifact.getValidAttributeTypes().contains(Name));
   }

   @Test
   @SuppressWarnings("rawtypes")
   public void testSetAttributesNotDirty() {
      Attribute one = mock(Attribute.class);
      Attribute two = mock(Attribute.class);
      when(one.getOrcsData()).thenReturn(attributeData);
      when(two.getOrcsData()).thenReturn(attributeData);
      artifact.add(CoreAttributeTypes.AccessContextId, one);
      artifact.add(CoreAttributeTypes.AccessContextId, two);
      artifact.setAttributesNotDirty();
      verify(one).clearDirty();
      verify(two).clearDirty();
   }

   @Test
   @SuppressWarnings({"rawtypes", "unchecked"})
   public void testGetName() {
      String name = artifact.getName();
      Assert.assertTrue(name.contains("AttributeDoesNotExist"));

      Attribute attr = mock(Attribute.class);
      when(attr.getOrcsData()).thenReturn(attributeData);
      when(attributeFactory.createAttributeWithDefaults(any(), any(), eq(CoreAttributeTypes.Name))).thenReturn(attr);
      when(attr.getValue()).thenReturn("test");
      artifact.add(CoreAttributeTypes.Name, attr);
      artifact.setName("test");
      name = artifact.getName();
      Assert.assertEquals("test", name);
   }

   @Test
   public void testGetAttributeCount() {
      artifact.add(CoreAttributeTypes.AccessContextId, notDeleted);
      artifact.add(CoreAttributeTypes.AccessContextId, deleted);
      artifact.add(CoreAttributeTypes.Name, differentType);
      int result = artifact.getAttributeCount(CoreAttributeTypes.AccessContextId);
      Assert.assertEquals(1, result);
      result = artifact.getAttributeCount(CoreAttributeTypes.Name);
      Assert.assertEquals(1, result);
   }

   @Test
   public void testGetAttributes() {
      artifact.add(CoreAttributeTypes.AccessContextId, notDeleted);
      artifact.add(CoreAttributeTypes.AccessContextId, deleted);
      artifact.add(CoreAttributeTypes.Name, differentType);
      List<Attribute<Object>> attributes = artifact.getAttributes();
      Assert.assertTrue(attributes.contains(notDeleted));
      Assert.assertTrue(attributes.contains(differentType));
      Assert.assertFalse(attributes.contains(deleted));

      attributes = artifact.getAttributes(CoreAttributeTypes.AccessContextId);
      Assert.assertEquals(1, attributes.size());
      Assert.assertTrue(attributes.contains(notDeleted));
   }

   @Test
   public void testGetAttributeValues() {
      artifact.add(CoreAttributeTypes.AccessContextId, notDeleted);
      artifact.add(CoreAttributeTypes.AccessContextId, deleted);
      when(notDeleted.getValue()).thenReturn("notDeleted");
      when(deleted.getValue()).thenReturn("deleted");
      List<Object> values = artifact.getAttributeValues(CoreAttributeTypes.AccessContextId);
      Assert.assertEquals(1, values.size());
      Assert.assertTrue(values.contains("notDeleted"));
   }

   @Test
   public void testGetSoleAttributeAsString() {
      when(notDeleted.getValue()).thenReturn(Integer.valueOf(5));
      artifact.add(CoreAttributeTypes.AccessContextId, notDeleted);
      String attribute = artifact.getSoleAttributeAsString(CoreAttributeTypes.AccessContextId);
      Assert.assertEquals("5", attribute);

      attribute = artifact.getSoleAttributeAsString(CoreAttributeTypes.Category, "default");
      Assert.assertEquals("default", attribute);
   }

   @Test
   @SuppressWarnings("rawtypes")
   public void testGetSoleAttributeAsStringException() {
      Attribute one = mock(Attribute.class);
      Attribute two = mock(Attribute.class);
      when(one.getOrcsData()).thenReturn(attributeData);
      when(two.getOrcsData()).thenReturn(attributeData);
      artifact.add(CoreAttributeTypes.AccessContextId, one);
      artifact.add(CoreAttributeTypes.AccessContextId, two);
      thrown.expect(MultipleAttributesExist.class);
      artifact.getSoleAttributeAsString(CoreAttributeTypes.AccessContextId);
   }

   @Test
   public void testDeleteAttributesByArtifact() {
      artifact.add(CoreAttributeTypes.AccessContextId, notDeleted);
      artifact.add(CoreAttributeTypes.AccessContextId, deleted);
      artifact.add(CoreAttributeTypes.Active, differentType);
      artifact.deleteAttributesByArtifact();
      verify(notDeleted).setArtifactDeleted();
      verify(deleted).setArtifactDeleted();
      verify(differentType).setArtifactDeleted();
   }

   @Test
   public void testDeleteSoleAttribute() {
      when(notDeleted.getContainer()).thenReturn(artifact);
      artifact.add(attributeType, notDeleted);
      artifact.deleteSoleAttribute(attributeType);
      verify(notDeleted).delete();
   }

   @Test
   public void testDeleteSoleAttributeException() {
      artifact.add(Name, notDeleted);

      thrown.expect(OseeStateException.class);
      artifact.deleteSoleAttribute(Name);
   }

   @Test
   @SuppressWarnings({"rawtypes", "unchecked"})
   public void testSetAttributesFromStringsCreateAll() {
      Attribute one = mock(Attribute.class);
      Attribute two = mock(Attribute.class);
      Attribute three = mock(Attribute.class);
      when(one.getOrcsData()).thenReturn(attributeData);
      when(two.getOrcsData()).thenReturn(attributeData);
      when(three.getOrcsData()).thenReturn(attributeData);

      when(attributeFactory.createAttributeWithDefaults(eq(artifact), any(), eq(attributeType))).thenReturn(one, two,
         three);
      artifact.setAttributesFromStrings(attributeType, "one", "two", "three");
      verify(one).setFromString("one");
      verify(two).setFromString("two");
      verify(three).setFromString("three");
   }

   @Test
   @SuppressWarnings({"rawtypes", "unchecked"})
   public void testSetAttributesFromStringsCreateOne() {
      Attribute one = mock(Attribute.class);
      Attribute two = mock(Attribute.class);
      when(one.getOrcsData()).thenReturn(attributeData);
      when(two.getOrcsData()).thenReturn(attributeData);

      when(one.getValue()).thenReturn("1");

      when(attributeFactory.createAttributeWithDefaults(eq(artifact), any(), eq(attributeType))).thenReturn(two);
      artifact.setAttributesFromStrings(attributeType, "1", "2");
      verify(one, never()).setFromString("1");
      verify(two).setFromString("2");
   }

}
