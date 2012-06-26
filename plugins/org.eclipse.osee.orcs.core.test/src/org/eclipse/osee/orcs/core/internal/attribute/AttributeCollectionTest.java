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

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.DeletionFlag;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class AttributeCollectionTest {

   // @formatter:off
   @Mock private AttributeExceptionFactory exceptionFactory;
   // @formatter:on

   private AttributeCollection attributeCollection;

   @Before
   public void init() {
      MockitoAnnotations.initMocks(this);
      attributeCollection = new AttributeCollection(exceptionFactory);
   }

   @Test
   @SuppressWarnings("unchecked")
   public void testGetAttributesDirty() {
      Attribute<String> dirtyAttr = mock(Attribute.class);
      Attribute<String> cleanAttr = mock(Attribute.class);
      when(dirtyAttr.isDirty()).thenReturn(true);
      attributeCollection.addAttribute(CoreAttributeTypes.Country, dirtyAttr);
      attributeCollection.addAttribute(CoreAttributeTypes.Country, cleanAttr);
      assertEquals(1, attributeCollection.getAttributesDirty().size());
      assertEquals(dirtyAttr, attributeCollection.getAttributesDirty().iterator().next());
   }

   @Test
   public void testGetAttributeSetFromString() throws OseeCoreException {
      attributeCollection.getAttributeSetFromString(CoreAttributeTypes.Country, DeletionFlag.EXCLUDE_DELETED, "test");
   }
}
