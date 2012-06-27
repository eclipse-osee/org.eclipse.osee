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
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

/**
 * Test Case for {@link AttributeDeletedFilter}
 * 
 * @author Roberto E. Escobar
 */
public class AttributeFilterTest {

   // @formatter:off
   @SuppressWarnings("rawtypes")
   @Mock private Attribute attribute;
   // @formatter:on

   private AttributeFilter filter1;
   private AttributeFilter filter2;

   @Before
   public void init() {
      MockitoAnnotations.initMocks(this);
      filter1 = Mockito.spy(new FilterMock());
      filter2 = Mockito.spy(new FilterMock());
   }

   @Test
   public void testAnd() throws OseeCoreException {
      AttributeFilter andFilter = filter1.and(filter2);

      when(filter1.accept(attribute)).thenReturn(true);
      when(filter2.accept(attribute)).thenReturn(true);
      Assert.assertTrue(andFilter.accept(attribute));

      when(filter1.accept(attribute)).thenReturn(false);
      when(filter2.accept(attribute)).thenReturn(true);
      Assert.assertFalse(andFilter.accept(attribute));

      when(filter1.accept(attribute)).thenReturn(true);
      when(filter2.accept(attribute)).thenReturn(false);
      Assert.assertFalse(andFilter.accept(attribute));

      when(filter1.accept(attribute)).thenReturn(false);
      when(filter2.accept(attribute)).thenReturn(false);
      Assert.assertFalse(andFilter.accept(attribute));
   }

   @Test
   public void testOr() throws OseeCoreException {
      AttributeFilter orFilter = filter1.or(filter2);

      when(filter1.accept(attribute)).thenReturn(true);
      when(filter2.accept(attribute)).thenReturn(true);
      Assert.assertTrue(orFilter.accept(attribute));

      when(filter1.accept(attribute)).thenReturn(false);
      when(filter2.accept(attribute)).thenReturn(true);
      Assert.assertTrue(orFilter.accept(attribute));

      when(filter1.accept(attribute)).thenReturn(true);
      when(filter2.accept(attribute)).thenReturn(false);
      Assert.assertTrue(orFilter.accept(attribute));

      when(filter1.accept(attribute)).thenReturn(false);
      when(filter2.accept(attribute)).thenReturn(false);
      Assert.assertFalse(orFilter.accept(attribute));
   }

   private class FilterMock extends AttributeFilter {
      @Override
      public boolean accept(Attribute<?> attribute) {
         return false;
      }
   };

}
