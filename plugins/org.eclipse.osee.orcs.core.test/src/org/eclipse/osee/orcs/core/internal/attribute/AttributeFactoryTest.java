/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs.core.internal.attribute;

import static org.eclipse.osee.framework.core.enums.CoreBranches.COMMON;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.lang.ref.WeakReference;
import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.DeletionFlag;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.orcs.core.ds.ArtifactData;
import org.eclipse.osee.orcs.core.ds.Attribute;
import org.eclipse.osee.orcs.core.ds.AttributeData;
import org.eclipse.osee.orcs.core.ds.AttributeDataFactory;
import org.eclipse.osee.orcs.core.ds.DataProxy;
import org.eclipse.osee.orcs.core.ds.ResourceNameResolver;
import org.eclipse.osee.orcs.core.ds.VersionData;
import org.eclipse.osee.orcs.data.AttributeTypes;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

/**
 * Test Case for {@link AttributeFactory}
 *
 * @author John Misinco
 */
public class AttributeFactoryTest {

   @Rule
   public ExpectedException thrown = ExpectedException.none();

   // @formatter:off
   @Mock private AttributeTypes cache;
   @Mock private AttributeDataFactory dataFactory;

   @Mock private AttributeData attributeData;
   @Mock private VersionData attrVersionData;

   @Mock private Attribute<Object> attribute;
   @Mock private Attribute<Object> destinationAttribute;

   @Mock private AttributeManager container;
   @Mock private DataProxy proxy;
   // @formatter:on

   private AttributeFactory factory;
   private final AttributeTypeToken attributeType = CoreAttributeTypes.Name;

   @Before
   public void init() throws OseeCoreException {
      MockitoAnnotations.initMocks(this);

      factory = new AttributeFactory(dataFactory, cache);

      when(attributeData.getTypeUuid()).thenReturn(attributeType.getId());
      when(cache.get(attributeType.getId())).thenReturn(attributeType);
      when(cache.getBaseAttributeTypeId(attributeType)).thenReturn("StringAttribute");
      when(attributeData.getDataProxy()).thenReturn(proxy);
   }

   @Test
   public void testCreateAttributeNullType() throws OseeCoreException {
      when(cache.get(attributeType.getId())).thenReturn(null);

      thrown.expect(OseeArgumentException.class);
      thrown.expectMessage(
         "attributeType cannot be null - Cannot find attribute type with uuid[" + attributeType.getId() + "]");
      factory.createAttribute(container, attributeData);
   }

   @SuppressWarnings({"unchecked", "rawtypes"})
   @Test
   public void testCreateAttribute() throws OseeCoreException {
      Attribute<Object> actual = factory.createAttribute(container, attributeData);
      assertEquals(attribute.getId(), actual.getId());
   }

   @SuppressWarnings({"unchecked", "rawtypes"})
   @Test
   public void testCreateAttributeFromArtifactDataAndType() throws OseeCoreException {
      ArtifactData artifactData = mock(ArtifactData.class);
      VersionData artVersionData = mock(VersionData.class);

      when(dataFactory.create(artifactData, attributeType)).thenReturn(attributeData);
      when(attributeData.getVersion()).thenReturn(attrVersionData);
      when(artifactData.getVersion()).thenReturn(artVersionData);

      ArgumentCaptor<ResourceNameResolver> resolverCapture = ArgumentCaptor.forClass(ResourceNameResolver.class);
      ArgumentCaptor<WeakReference> refCapture = ArgumentCaptor.forClass(WeakReference.class);

      Attribute<Object> actual = factory.createAttributeWithDefaults(container, artifactData, attributeType);

      verify(dataFactory).create(artifactData, attributeType);
      assertEquals(attribute.getId(), actual.getId());
   }

   @SuppressWarnings({"unchecked", "rawtypes"})
   @Test
   public void testCopyAttribute() throws OseeCoreException {
      AttributeData copiedAttributeData = mock(AttributeData.class);

      when(dataFactory.copy(COMMON, attributeData)).thenReturn(copiedAttributeData);
      when(copiedAttributeData.getTypeUuid()).thenReturn(attributeType.getId());
      when(copiedAttributeData.getDataProxy()).thenReturn(proxy);

      Attribute<Object> actual = factory.copyAttribute(attributeData, COMMON, container);

      assertEquals(attribute.getId(), actual.getId());

      verify(dataFactory).copy(COMMON, attributeData);
   }

   @Test
   public void testIntroduceAttributeNotInStorage() throws OseeCoreException {
      when(attributeData.getVersion()).thenReturn(attrVersionData);
      when(attrVersionData.isInStorage()).thenReturn(false);

      Attribute<Object> actual = factory.introduceAttribute(attributeData, COMMON, container);
      assertNull(actual);
   }

   @Test
   public void testIntroduceAttribute() throws OseeCoreException {
      AttributeData introducedAttributeData = mock(AttributeData.class);

      when(attributeData.getVersion()).thenReturn(attrVersionData);
      when(attrVersionData.isInStorage()).thenReturn(true);
      when(attributeData.getLocalId()).thenReturn(12345);

      when(dataFactory.introduce(COMMON, attributeData)).thenReturn(introducedAttributeData);
      when(introducedAttributeData.getTypeUuid()).thenReturn(attributeType.getId());
      when(introducedAttributeData.getDataProxy()).thenReturn(proxy);

      when(container.getAttributeById(attributeData, DeletionFlag.INCLUDE_DELETED)).thenReturn(destinationAttribute);

      Attribute<Object> actual = factory.introduceAttribute(attributeData, COMMON, container);
      assertNotNull(actual);

      verify(dataFactory).introduce(COMMON, attributeData);

      assertEquals(actual, destinationAttribute);
   }

   @Test
   public void testGetMaxOccurrenceLimit() throws OseeCoreException {
      AttributeTypeId token = mock(AttributeTypeId.class);

      when(cache.getMaxOccurrences(token)).thenReturn(56);

      int actual = factory.getMaxOccurrenceLimit(token);

      assertEquals(56, actual);

      verify(cache).getMaxOccurrences(token);
   }

   @Test
   public void testGetMinOccurrenceLimit() throws OseeCoreException {
      AttributeTypeId token = mock(AttributeTypeId.class);

      when(cache.getMinOccurrences(token)).thenReturn(99);

      int actual = factory.getMinOccurrenceLimit(token);

      assertEquals(99, actual);

      verify(cache).getMinOccurrences(token);
   }
}
