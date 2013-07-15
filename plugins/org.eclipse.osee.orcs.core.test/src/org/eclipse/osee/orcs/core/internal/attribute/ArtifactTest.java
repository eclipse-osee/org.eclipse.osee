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
package org.eclipse.osee.orcs.core.internal.attribute;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.util.List;
import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.core.data.IAttributeType;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.core.exception.MultipleAttributesExist;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeStateException;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.eclipse.osee.orcs.core.ds.ArtifactData;
import org.eclipse.osee.orcs.core.ds.AttributeData;
import org.eclipse.osee.orcs.core.ds.OrcsData;
import org.eclipse.osee.orcs.core.ds.VersionData;
import org.eclipse.osee.orcs.core.internal.artifact.Artifact;
import org.eclipse.osee.orcs.core.internal.relation.RelationContainer;
import org.eclipse.osee.orcs.core.internal.util.ValueProvider;
import org.eclipse.osee.orcs.data.ArtifactTypes;
import org.eclipse.osee.orcs.data.AttributeReadable;
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
   @Mock private Artifact artifactImpl;
   @Mock private ArtifactData artifactData;
   @Mock private AttributeFactory attributeFactory;
   @Mock private RelationContainer relationContainer;
   @Mock private ValueProvider<Branch, OrcsData> branchProvider;
   @Mock private ArtifactTypes types;

   @Mock private VersionData version;
   @Mock private AttributeData attributeData;
   @Mock private Branch branch;
   
   @SuppressWarnings("rawtypes")
   @Mock private Attribute attribute;
   @SuppressWarnings("rawtypes")
   @Mock private Attribute notDeleted;
   @SuppressWarnings("rawtypes")
   @Mock private Attribute deleted;
   @SuppressWarnings("rawtypes")
   @Mock private Attribute differentType;
   // @formatter:on

   private final String guid = GUID.create();
   private final IAttributeType attributeType = CoreAttributeTypes.Annotation;
   private final IArtifactType artifactType = CoreArtifactTypes.GeneralData;

   @SuppressWarnings("unchecked")
   @Before
   public void init() throws OseeCoreException {
      MockitoAnnotations.initMocks(this);
      artifactImpl = new Artifact(types, artifactData, attributeFactory, relationContainer, branchProvider);

      when(types.isValidAttributeType(any(IArtifactType.class), any(Branch.class), any(IAttributeType.class))).thenReturn(
         true);
      when(attributeFactory.getMaxOccurrenceLimit(any(IAttributeType.class))).thenReturn(1);

      when(attributeFactory.createAttribute(any(AttributeManager.class), any(AttributeData.class))).thenReturn(
         attribute);
      when(
         attributeFactory.createAttributeWithDefaults(any(AttributeManager.class), any(ArtifactData.class),
            any(IAttributeType.class))).thenReturn(attribute);
      when(attribute.getOrcsData()).thenReturn(attributeData);

      when(artifactData.getGuid()).thenReturn(guid);
      when(artifactData.getVersion()).thenReturn(version);
      when(artifactData.getTypeUuid()).thenReturn(artifactType.getGuid());
      when(branchProvider.get()).thenReturn(branch);

      when(deleted.isDeleted()).thenReturn(true);
      when(notDeleted.getOrcsData()).thenReturn(attributeData);
      when(deleted.getOrcsData()).thenReturn(attributeData);
      when(differentType.getOrcsData()).thenReturn(attributeData);

      when(types.getByUuid(CoreArtifactTypes.GeneralData.getGuid())).thenReturn(CoreArtifactTypes.GeneralData);
      when(types.getByUuid(CoreArtifactTypes.CodeUnit.getGuid())).thenReturn(CoreArtifactTypes.CodeUnit);
   }

   @Test
   @SuppressWarnings("unchecked")
   public void testAddAndGet() {
      Attribute<Object> attribute = mock(Attribute.class);
      when(attribute.getOrcsData()).thenReturn(attributeData);
      Assert.assertEquals(0, artifactImpl.getAllAttributes().size());
      artifactImpl.add(CoreAttributeTypes.City, attribute);
      Assert.assertTrue(artifactImpl.getAllAttributes().contains(attribute));
      Assert.assertEquals(1, artifactImpl.getAllAttributes().size());
   }

   @Test
   @SuppressWarnings({"rawtypes", "unchecked"})
   public void testAddException() throws OseeCoreException {
      Attribute one = mock(Attribute.class);
      Attribute two = mock(Attribute.class);
      when(one.getOrcsData()).thenReturn(attributeData);
      when(two.getOrcsData()).thenReturn(attributeData);

      when(attributeFactory.getMaxOccurrenceLimit(attributeType)).thenReturn(1);
      artifactImpl.add(attributeType, one);
      artifactImpl.add(attributeType, two);
      Assert.assertEquals(2, artifactImpl.getAttributes(attributeType).size());
   }

   @Test
   @SuppressWarnings("unchecked")
   public void testAreAttributesDirty() {
      Attribute<Object> attribute = mock(Attribute.class);
      when(attribute.getOrcsData()).thenReturn(attributeData);
      artifactImpl.add(CoreAttributeTypes.City, attribute);
      Assert.assertFalse(artifactImpl.areAttributesDirty());
      when(attribute.isDirty()).thenReturn(true);
      Assert.assertTrue(artifactImpl.areAttributesDirty());
   }

   @Test
   public void testCreateAttribute() throws OseeCoreException {
      artifactImpl.createAttribute(CoreAttributeTypes.City);
      verify(attributeFactory).createAttributeWithDefaults(artifactImpl, artifactData, CoreAttributeTypes.City);
   }

   @Test
   public void testSetOrcsData() {
      ArtifactData newOrcsData = mock(ArtifactData.class);
      artifactImpl.setOrcsData(newOrcsData);
      verify(branchProvider).setOrcsData(newOrcsData);
   }

   @Test
   public void testGetModificationType() {
      artifactImpl.getModificationType();
      verify(artifactData).getModType();
   }

   @Test
   public void testGetLocalId() {
      artifactImpl.getLocalId();
      verify(artifactData).getLocalId();
   }

   @Test
   public void testGetGuid() {
      artifactImpl.getGuid();
      verify(artifactData).getGuid();
   }

   @Test
   public void testGetHumanReadableId() {
      artifactImpl.getHumanReadableId();
      verify(artifactData).getHumanReadableId();
   }

   @Test
   public void testGetTransactionId() {
      artifactImpl.getTransaction();
      verify(version).getTransactionId();
   }

   @Test
   public void testGetBranch() throws OseeCoreException {
      artifactImpl.getBranch();
      verify(branchProvider).get();
   }

   @Test
   public void testArtifactType() throws OseeCoreException {
      artifactImpl.getArtifactType();
      verify(types).getByUuid(artifactData.getTypeUuid());
   }

   @Test
   @SuppressWarnings({"rawtypes", "unchecked"})
   public void testSetName() throws OseeCoreException {
      Attribute attr = mock(Attribute.class);
      when(attr.getOrcsData()).thenReturn(attributeData);
      when(
         attributeFactory.createAttributeWithDefaults(any(AttributeManager.class), any(ArtifactData.class),
            eq(CoreAttributeTypes.Name))).thenReturn(attr);
      artifactImpl.setName("test");
      verify(attr).setFromString("test");
   }

   @Test
   public void testSetArtifactType() throws OseeCoreException {
      when(version.isInStorage()).thenReturn(true);

      artifactImpl.setArtifactType(CoreArtifactTypes.CodeUnit);

      verify(artifactData).setTypeUuid(CoreArtifactTypes.CodeUnit.getGuid());
      verify(artifactData).setModType(ModificationType.MODIFIED);

      reset(version);
      reset(artifactData);

      when(artifactData.getVersion()).thenReturn(version);
      when(artifactData.getGuid()).thenReturn(guid);
      when(artifactData.getVersion()).thenReturn(version);
      when(artifactData.getTypeUuid()).thenReturn(artifactType.getGuid());

      artifactImpl.setArtifactType(CoreArtifactTypes.CodeUnit);
      verify(artifactData, never()).setModType(ModificationType.MODIFIED);
   }

   @Test
   public void testIsOfType() throws OseeCoreException {
      artifactImpl.isOfType(CoreArtifactTypes.CodeUnit);

      verify(types).inheritsFrom(CoreArtifactTypes.GeneralData, CoreArtifactTypes.CodeUnit);
   }

   @Test
   @SuppressWarnings({"rawtypes", "unchecked"})
   public void testIsDirty() throws OseeCoreException {
      Assert.assertFalse(artifactImpl.isDirty());

      // add dirty attribute
      Attribute dirty = mock(Attribute.class);
      when(dirty.getOrcsData()).thenReturn(attributeData);
      when(dirty.isDirty()).thenReturn(true);
      artifactImpl.add(CoreAttributeTypes.Active, dirty);
      Assert.assertTrue(artifactImpl.isDirty());

      // change artifactType
      reset(dirty);
      Assert.assertFalse(artifactImpl.isDirty());
      artifactImpl.setArtifactType(CoreArtifactTypes.CodeUnit);
      Assert.assertTrue(artifactImpl.isDirty());

      // set mod type to replace with version
      artifactImpl.setOrcsData(artifactData);
      Assert.assertFalse(artifactImpl.isDirty());
      when(artifactData.getModType()).thenReturn(ModificationType.REPLACED_WITH_VERSION);
      Assert.assertTrue(artifactImpl.isDirty());
   }

   @Test
   public void testIsDeleted() {
      for (ModificationType modType : ModificationType.values()) {
         reset(artifactData);
         when(artifactData.getModType()).thenReturn(modType);
         Assert.assertEquals(modType.isDeleted(), artifactImpl.isDeleted());
      }
   }

   @Test
   public void testIsAttributeTypeValid() throws OseeCoreException {
      artifactImpl.isAttributeTypeValid(CoreAttributeTypes.Afha);
      verify(types).isValidAttributeType(artifactType, branch, CoreAttributeTypes.Afha);
   }

   @Test
   public void testGetValidAttributeTypes() throws OseeCoreException {
      artifactImpl.getValidAttributeTypes();
      verify(types).getAttributeTypes(artifactType, branch);
   }

   @Test
   @SuppressWarnings({"rawtypes", "unchecked"})
   public void testSetAttributesNotDirty() {
      Attribute one = mock(Attribute.class);
      Attribute two = mock(Attribute.class);
      when(one.getOrcsData()).thenReturn(attributeData);
      when(two.getOrcsData()).thenReturn(attributeData);
      artifactImpl.add(CoreAttributeTypes.AccessContextId, one);
      artifactImpl.add(CoreAttributeTypes.AccessContextId, two);
      artifactImpl.setAttributesNotDirty();
      verify(one).clearDirty();
      verify(two).clearDirty();
   }

   @Test
   @SuppressWarnings({"rawtypes", "unchecked"})
   public void testGetName() throws OseeCoreException {
      String name = artifactImpl.getName();
      Assert.assertTrue(name.contains("AttributeDoesNotExist"));

      Attribute attr = mock(Attribute.class);
      when(attr.getOrcsData()).thenReturn(attributeData);
      when(
         attributeFactory.createAttributeWithDefaults(any(AttributeManager.class), any(ArtifactData.class),
            eq(CoreAttributeTypes.Name))).thenReturn(attr);
      when(attr.getValue()).thenReturn("test");
      artifactImpl.add(CoreAttributeTypes.Name, attr);
      artifactImpl.setName("test");
      name = artifactImpl.getName();
      Assert.assertEquals("test", name);
   }

   @Test
   public void testGetMaximumAttributeTypeAllowed() throws OseeCoreException {
      int expected = 5;

      when(attributeFactory.getMaxOccurrenceLimit(CoreAttributeTypes.AccessContextId)).thenReturn(expected);

      int result = artifactImpl.getMaximumAttributeTypeAllowed(CoreAttributeTypes.AccessContextId);
      Assert.assertEquals(expected, result);

      reset(types);
      result = artifactImpl.getMaximumAttributeTypeAllowed(CoreAttributeTypes.AccessContextId);
      Assert.assertEquals(-1, result);
   }

   @Test
   public void testGetMinimumAttributeTypeAllowed() throws OseeCoreException {
      int expected = 5;

      when(attributeFactory.getMinOccurrenceLimit(CoreAttributeTypes.AccessContextId)).thenReturn(expected);

      int result = artifactImpl.getMinimumAttributeTypeAllowed(CoreAttributeTypes.AccessContextId);
      Assert.assertEquals(expected, result);

      reset(types);
      result = artifactImpl.getMaximumAttributeTypeAllowed(CoreAttributeTypes.AccessContextId);
      Assert.assertEquals(-1, result);
   }

   @Test
   @SuppressWarnings("unchecked")
   public void testGetAttributeCount() throws OseeCoreException {
      artifactImpl.add(CoreAttributeTypes.AccessContextId, notDeleted);
      artifactImpl.add(CoreAttributeTypes.AccessContextId, deleted);
      artifactImpl.add(CoreAttributeTypes.Name, differentType);
      int result = artifactImpl.getAttributeCount(CoreAttributeTypes.AccessContextId);
      Assert.assertEquals(1, result);
      result = artifactImpl.getAttributeCount(CoreAttributeTypes.Name);
      Assert.assertEquals(1, result);
   }

   @Test
   @SuppressWarnings("unchecked")
   public void testGetAttributes() throws OseeCoreException {
      artifactImpl.add(CoreAttributeTypes.AccessContextId, notDeleted);
      artifactImpl.add(CoreAttributeTypes.AccessContextId, deleted);
      artifactImpl.add(CoreAttributeTypes.Name, differentType);
      List<AttributeReadable<Object>> attributes = artifactImpl.getAttributes();
      Assert.assertTrue(attributes.contains(notDeleted));
      Assert.assertTrue(attributes.contains(differentType));
      Assert.assertFalse(attributes.contains(deleted));

      attributes = artifactImpl.getAttributes(CoreAttributeTypes.AccessContextId);
      Assert.assertEquals(1, attributes.size());
      Assert.assertTrue(attributes.contains(notDeleted));
   }

   @Test
   @SuppressWarnings("unchecked")
   public void testGetAttributeValues() throws OseeCoreException {
      artifactImpl.add(CoreAttributeTypes.AccessContextId, notDeleted);
      artifactImpl.add(CoreAttributeTypes.AccessContextId, deleted);
      when(notDeleted.getValue()).thenReturn("notDeleted");
      when(deleted.getValue()).thenReturn("deleted");
      List<Object> values = artifactImpl.getAttributeValues(CoreAttributeTypes.AccessContextId);
      Assert.assertEquals(1, values.size());
      Assert.assertTrue(values.contains("notDeleted"));
   }

   @Test
   @SuppressWarnings("unchecked")
   public void testGetSoleAttributeAsString() throws OseeCoreException {
      when(notDeleted.getValue()).thenReturn(new Integer(5));
      artifactImpl.add(CoreAttributeTypes.AccessContextId, notDeleted);
      String attribute = artifactImpl.getSoleAttributeAsString(CoreAttributeTypes.AccessContextId);
      Assert.assertEquals("5", attribute);

      attribute = artifactImpl.getSoleAttributeAsString(CoreAttributeTypes.Category, "default");
      Assert.assertEquals("default", attribute);
   }

   @Test
   @SuppressWarnings({"rawtypes", "unchecked"})
   public void testGetSoleAttributeAsStringException() throws OseeCoreException {
      Attribute one = mock(Attribute.class);
      Attribute two = mock(Attribute.class);
      when(one.getOrcsData()).thenReturn(attributeData);
      when(two.getOrcsData()).thenReturn(attributeData);
      artifactImpl.add(CoreAttributeTypes.AccessContextId, one);
      artifactImpl.add(CoreAttributeTypes.AccessContextId, two);
      thrown.expect(MultipleAttributesExist.class);
      artifactImpl.getSoleAttributeAsString(CoreAttributeTypes.AccessContextId);
   }

   @Test
   @SuppressWarnings("unchecked")
   public void testDeleteAttributesByArtifact() throws OseeCoreException {
      artifactImpl.add(CoreAttributeTypes.AccessContextId, notDeleted);
      artifactImpl.add(CoreAttributeTypes.AccessContextId, deleted);
      artifactImpl.add(CoreAttributeTypes.Active, differentType);
      artifactImpl.deleteAttributesByArtifact();
      verify(notDeleted).setArtifactDeleted();
      verify(deleted).setArtifactDeleted();
      verify(differentType).setArtifactDeleted();
   }

   @Test
   @SuppressWarnings("unchecked")
   public void testDeleteSoleAttribute() throws OseeCoreException {
      when(attributeFactory.getMinOccurrenceLimit(attributeType)).thenReturn(0);
      when(notDeleted.getAttributeType()).thenReturn(attributeType);
      when(notDeleted.getContainer()).thenReturn(artifactImpl);
      artifactImpl.add(attributeType, notDeleted);
      artifactImpl.deleteSoleAttribute(attributeType);
      verify(notDeleted).delete();
   }

   @Test
   @SuppressWarnings("unchecked")
   public void testDeleteSoleAttributeException() throws OseeCoreException {
      when(attributeFactory.getMinOccurrenceLimit(attributeType)).thenReturn(1);

      when(notDeleted.getAttributeType()).thenReturn(attributeType);
      artifactImpl.add(attributeType, notDeleted);

      thrown.expect(OseeStateException.class);
      artifactImpl.deleteSoleAttribute(attributeType);
   }

   @Test
   @SuppressWarnings({"rawtypes", "unchecked"})
   public void testSetAttributesFromStringsCreateAll() throws OseeCoreException {
      Attribute one = mock(Attribute.class);
      Attribute two = mock(Attribute.class);
      Attribute three = mock(Attribute.class);
      when(one.getOrcsData()).thenReturn(attributeData);
      when(two.getOrcsData()).thenReturn(attributeData);
      when(three.getOrcsData()).thenReturn(attributeData);

      when(attributeFactory.getMaxOccurrenceLimit(attributeType)).thenReturn(3);

      when(attributeFactory.createAttributeWithDefaults(eq(artifactImpl), any(ArtifactData.class), eq(attributeType))).thenReturn(
         one, two, three);
      artifactImpl.setAttributesFromStrings(attributeType, "one", "two", "three");
      verify(one).setFromString("one");
      verify(two).setFromString("two");
      verify(three).setFromString("three");
   }

   @Test
   @SuppressWarnings({"rawtypes", "unchecked"})
   public void testSetAttributesFromStringsCreateOne() throws OseeCoreException {
      Attribute one = mock(Attribute.class);
      Attribute two = mock(Attribute.class);
      when(one.getOrcsData()).thenReturn(attributeData);
      when(two.getOrcsData()).thenReturn(attributeData);

      when(attributeFactory.getMaxOccurrenceLimit(attributeType)).thenReturn(3);

      when(attributeFactory.createAttributeWithDefaults(eq(artifactImpl), any(ArtifactData.class), eq(attributeType))).thenReturn(
         two);
      artifactImpl.add(attributeType, one);
      artifactImpl.setAttributesFromStrings(attributeType, "1", "2");
      verify(one).setFromString("1");
      verify(two).setFromString("2");

      reset(one, two);
      when(one.getValue()).thenReturn("1");
      artifactImpl.setAttributesFromStrings(attributeType, "1", "2");
      verify(one, never()).setFromString("1");
      verify(two).setFromString("2");
   }

}
