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

import static org.mockito.Mockito.when;
import org.eclipse.osee.framework.core.enums.DeletionFlag;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

/**
 * Test Case for {@link AttributeDeletedFilter}
 * 
 * @author Roberto E. Escobar
 */
@SuppressWarnings("rawtypes")
public class AttributeDeletedFilterTest {

   // @formatter:off
   @Mock private Attribute deletedAttr;
   @Mock private Attribute attribute;
   // @formatter:on

   @Before
   public void init() {
      MockitoAnnotations.initMocks(this);

      when(deletedAttr.isDeleted()).thenReturn(true);
      when(attribute.isDeleted()).thenReturn(false);
   }

   @Test
   public void testIncludeDeleted() throws OseeCoreException {
      Assert.assertTrue(filter(DeletionFlag.INCLUDE_DELETED).accept(deletedAttr));
      Assert.assertTrue(filter(DeletionFlag.INCLUDE_DELETED).accept(attribute));
   }

   @Test
   public void testExcludeDeleted() throws OseeCoreException {
      Assert.assertFalse(filter(DeletionFlag.EXCLUDE_DELETED).accept(deletedAttr));
      Assert.assertTrue(filter(DeletionFlag.EXCLUDE_DELETED).accept(attribute));
   }

   private AttributeFilter filter(DeletionFlag flag) {
      return new AttributeDeletedFilter(flag);
   }

}
