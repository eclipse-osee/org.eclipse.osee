/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
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

import static org.eclipse.osee.framework.core.enums.CoreBranches.COMMON;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.eclipse.osee.framework.core.OrcsTokenService;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.DeletionFlag;
import org.eclipse.osee.orcs.core.ds.Attribute;
import org.eclipse.osee.orcs.core.ds.AttributeData;
import org.eclipse.osee.orcs.core.ds.AttributeDataFactory;
import org.eclipse.osee.orcs.core.ds.DataProxy;
import org.eclipse.osee.orcs.core.ds.VersionData;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
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
   @Mock private OrcsTokenService tokenService;
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
   public void init() {
      MockitoAnnotations.initMocks(this);

      factory = new AttributeFactory(dataFactory, tokenService);

      when(attributeData.getType()).thenReturn(attributeType);
      doReturn(attributeType).when(tokenService).getAttributeType(attributeType.getId());
      assertEquals(attributeType, tokenService.getAttributeType(attributeType.getId()));
      assertTrue(attributeType.isString());
      when(attributeData.getDataProxy()).thenReturn(proxy);
   }

   @SuppressWarnings("unchecked")
   @Test
   public void testCreateAttribute() {
      Attribute<Object> actual = factory.createAttribute(container, attributeData);
      assertEquals(attribute.getId(), actual.getId());
   }

   @SuppressWarnings({"unchecked", "rawtypes"})
   @Test
   public void testCopyAttribute() {
      AttributeData copiedAttributeData = mock(AttributeData.class);

      when(dataFactory.copy(COMMON, attributeData)).thenReturn(copiedAttributeData);
      when(copiedAttributeData.getType()).thenReturn(attributeType);
      when(copiedAttributeData.getDataProxy()).thenReturn(proxy);

      Attribute<Object> actual = factory.copyAttribute(attributeData, COMMON, container);

      assertEquals(attribute.getId(), actual.getId());

      verify(dataFactory).copy(COMMON, attributeData);
   }

   @Test
   public void testIntroduceAttributeNotInStorage() {
      when(attributeData.getVersion()).thenReturn(attrVersionData);
      when(attrVersionData.isInStorage()).thenReturn(false);

      Attribute<Object> actual = factory.introduceAttribute(attributeData, COMMON, container);
      assertNull(actual);
   }

   @Test
   public void testIntroduceAttribute() {
      AttributeData introducedAttributeData = mock(AttributeData.class);

      when(attributeData.getVersion()).thenReturn(attrVersionData);
      when(attrVersionData.isInStorage()).thenReturn(true);

      when(dataFactory.introduce(COMMON, attributeData)).thenReturn(introducedAttributeData);
      when(introducedAttributeData.getType()).thenReturn(attributeType);
      when(introducedAttributeData.getDataProxy()).thenReturn(proxy);

      when(container.getAttributeById(attributeData, DeletionFlag.INCLUDE_DELETED)).thenReturn(destinationAttribute);

      Attribute<Object> actual = factory.introduceAttribute(attributeData, COMMON, container);
      assertNotNull(actual);

      verify(dataFactory).introduce(COMMON, attributeData);

      assertEquals(actual, destinationAttribute);
   }
}