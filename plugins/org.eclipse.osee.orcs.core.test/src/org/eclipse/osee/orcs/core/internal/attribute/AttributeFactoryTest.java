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

import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.cache.AttributeTypeCache;
import org.eclipse.osee.framework.core.model.type.AttributeType;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.core.ds.AttributeData;
import org.eclipse.osee.orcs.core.ds.AttributeDataFactory;
import org.eclipse.osee.orcs.core.ds.DataProxy;
import org.eclipse.osee.orcs.core.ds.VersionData;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

/**
 * Test Case for {@link AttributeFactory}
 * 
 * @author John Misinco
 */
public class AttributeFactoryTest {

   @Mock
   private Log logger;
   @Mock
   private AttributeTypeCache cache;
   @Mock
   private AttributeType attrType;
   @Mock
   private AttributeClassResolver resolver;
   @Mock
   private AttributeDataFactory dataFactory;
   //   @Mock
   //   private AttributeContainer container;
   @Mock
   private AttributeData data;
   @Mock
   private DataProxy proxy;
   @Mock
   private Attribute<Object> attr;

   private AttributeFactory factory;

   @Before
   public void initMocks() throws OseeCoreException {
      MockitoAnnotations.initMocks(this);
      when(cache.getByGuid(anyLong())).thenReturn(attrType);
      when(resolver.createAttribute(any(AttributeType.class))).thenReturn(attr);
      when(data.getDataProxy()).thenReturn(proxy);
      factory = new AttributeFactory(logger, resolver, cache, dataFactory);
   }

   @Test
   public void testCreateAttribute() throws OseeCoreException {
      //      factory.createAttribute(container, data);
      //      verify(container).add(attrType, attr);
   }

   @Test
   public void testCopyAttribute() throws OseeCoreException {
      when(attr.getOrcsData()).thenReturn(data);
      AttributeData copyAttrData = mock(AttributeData.class);
      when(copyAttrData.getDataProxy()).thenReturn(proxy);
      when(dataFactory.copy(CoreBranches.COMMON, data)).thenReturn(copyAttrData);
      //      Attribute<?> copy = factory.copyAttribute(attr, CoreBranches.COMMON, container);

      //      verify(container).add(attrType, copy);
      verify(dataFactory).copy(CoreBranches.COMMON, data);
   }

   @Test
   public void testIntroduceAttribute() throws OseeCoreException {
      VersionData version = mock(VersionData.class);
      AttributeData newAttrData = mock(AttributeData.class);
      when(newAttrData.getDataProxy()).thenReturn(proxy);
      when(attr.getOrcsData()).thenReturn(data);
      when(data.getVersion()).thenReturn(version);
      when(version.isInStorage()).thenReturn(true);
      when(dataFactory.introduce(CoreBranches.COMMON, data)).thenReturn(newAttrData);
      //      factory.introduceAttribute(attr, CoreBranches.COMMON, container);

      verify(dataFactory).introduce(CoreBranches.COMMON, data);
      //      verify(container).add(attrType, attr);
   }
}
