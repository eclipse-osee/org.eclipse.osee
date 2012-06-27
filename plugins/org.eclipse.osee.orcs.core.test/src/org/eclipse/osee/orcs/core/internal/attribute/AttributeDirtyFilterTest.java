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
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.orcs.core.internal.attribute.AttributeDirtyFilter.DirtyFlag;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

/**
 * Test Case for {@link AttributeDirtyFilter}
 * 
 * @author Roberto E. Escobar
 */
@SuppressWarnings("rawtypes")
public class AttributeDirtyFilterTest {

   // @formatter:off
   @Mock private Attribute dirtyAttr;
   @Mock private Attribute noneDirtyAttr;
   // @formatter:on

   @Before
   public void init() {
      MockitoAnnotations.initMocks(this);

      when(dirtyAttr.isDirty()).thenReturn(true);
      when(noneDirtyAttr.isDirty()).thenReturn(false);
   }

   @Test
   public void testAcceptDirties() throws OseeCoreException {
      Assert.assertFalse(filter(DirtyFlag.DIRTY).accept(noneDirtyAttr));
      Assert.assertTrue(filter(DirtyFlag.DIRTY).accept(dirtyAttr));
   }

   @Test
   public void testAcceptNoneDirties() throws OseeCoreException {
      Assert.assertTrue(filter(DirtyFlag.NON_DIRTY).accept(noneDirtyAttr));
      Assert.assertFalse(filter(DirtyFlag.NON_DIRTY).accept(dirtyAttr));
   }

   private AttributeFilter filter(DirtyFlag flag) {
      return new AttributeDirtyFilter(flag);
   }

}
