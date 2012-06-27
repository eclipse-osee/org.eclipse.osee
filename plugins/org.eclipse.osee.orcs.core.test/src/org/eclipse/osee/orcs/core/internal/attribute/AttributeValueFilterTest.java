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
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

/**
 * Test Case for {@link AttributeValueFilter}
 * 
 * @author Roberto E. Escobar
 */
@SuppressWarnings("rawtypes")
public class AttributeValueFilterTest {

   // @formatter:off
   @Mock private Attribute attribute1;
   @Mock private Attribute attribute2;
   // @formatter:on

   @Before
   public void init() throws OseeCoreException {
      MockitoAnnotations.initMocks(this);

      when(attribute1.getValue()).thenReturn("Hello");
      when(attribute2.getValue()).thenReturn(true);
   }

   @Test
   public void testAccept() throws OseeCoreException {
      Assert.assertTrue(filter("Hello").accept(attribute1));
      Assert.assertTrue(filter(true).accept(attribute2));
   }

   @Test
   public void testNotAccept() throws OseeCoreException {
      Assert.assertFalse(filter("Helo").accept(attribute1));
      Assert.assertFalse(filter("Hello").accept(attribute2));

      Assert.assertFalse(filter(true).accept(attribute1));
      Assert.assertFalse(filter(false).accept(attribute1));
      Assert.assertFalse(filter(false).accept(attribute2));
   }

   private <T> AttributeFilter filter(T value) {
      return new AttributeValueFilter<T>(value);
   }

}
