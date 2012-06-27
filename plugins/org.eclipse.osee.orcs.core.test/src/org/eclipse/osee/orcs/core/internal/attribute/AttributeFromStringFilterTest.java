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
import java.util.Date;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

/**
 * Test Case for {@link AttributeFromStringFilter}
 * 
 * @author Roberto E. Escobar
 */
@SuppressWarnings("rawtypes")
public class AttributeFromStringFilterTest {

   // @formatter:off
   @Mock private Attribute attribute1;
   @Mock private Attribute attribute2;
   @Mock private Attribute attribute3;
   // @formatter:on

   private Date date;

   @Before
   public void init() throws OseeCoreException {
      MockitoAnnotations.initMocks(this);

      date = new Date();

      when(attribute1.getValue()).thenReturn(45789L);
      when(attribute2.getValue()).thenReturn(true);
      when(attribute3.getValue()).thenReturn(date);
   }

   @Test
   public void testAccept() throws OseeCoreException {
      Assert.assertTrue(filter("45789").accept(attribute1));
      Assert.assertTrue(filter("true").accept(attribute2));
      Assert.assertTrue(filter(date.toString()).accept(attribute3));
   }

   @Test
   public void testNotAccept() throws OseeCoreException {
      Assert.assertFalse(filter("Helo").accept(attribute1));
      Assert.assertFalse(filter("Hello").accept(attribute2));

      Date date2 = new Date(123123111231L);

      Assert.assertFalse(filter(date2.toString()).accept(attribute3));

      Assert.assertFalse(filter("true").accept(attribute1));
      Assert.assertFalse(filter("false").accept(attribute1));
      Assert.assertFalse(filter("false").accept(attribute2));
   }

   private <T> AttributeFilter filter(String value) {
      return new AttributeFromStringFilter(value);
   }

}
