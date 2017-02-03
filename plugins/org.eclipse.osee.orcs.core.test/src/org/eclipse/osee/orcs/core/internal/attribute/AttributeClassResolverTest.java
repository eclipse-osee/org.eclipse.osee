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

import static org.mockito.Mockito.when;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.orcs.core.AttributeClassProvider;
import org.eclipse.osee.orcs.core.internal.attribute.primitives.BooleanAttribute;
import org.eclipse.osee.orcs.core.internal.attribute.primitives.CompressedContentAttribute;
import org.eclipse.osee.orcs.core.internal.attribute.primitives.DateAttribute;
import org.eclipse.osee.orcs.core.internal.attribute.primitives.EnumeratedAttribute;
import org.eclipse.osee.orcs.core.internal.attribute.primitives.FloatingPointAttribute;
import org.eclipse.osee.orcs.core.internal.attribute.primitives.IntegerAttribute;
import org.eclipse.osee.orcs.core.internal.attribute.primitives.JavaObjectAttribute;
import org.eclipse.osee.orcs.core.internal.attribute.primitives.PrimitiveAttributeClassProvider;
import org.eclipse.osee.orcs.core.internal.attribute.primitives.StringAttribute;
import org.eclipse.osee.orcs.data.AttributeTypes;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

/**
 * Test Case for {@link AttributeClassResolver}
 * 
 * @author Roberto E. Escobar
 */
@RunWith(Parameterized.class)
public class AttributeClassResolverTest {

   //@formatter:off
   @Mock private AttributeTypes cache;
   @Mock private AttributeTypeId type;
   //@formatter:on

   private final String alias;
   private final Class<? extends Attribute<?>> expected;
   private AttributeClassResolver resolver;
   private AttributeClassRegistry registry;

   public AttributeClassResolverTest(String alias, Class<? extends Attribute<?>> expected) {
      this.alias = alias;
      this.expected = expected;
   }

   @Before
   public void setup() {
      MockitoAnnotations.initMocks(this);

      registry = new AttributeClassRegistry();
      resolver = new AttributeClassResolver(registry, cache);

      PrimitiveAttributeClassProvider provider = new PrimitiveAttributeClassProvider();
      registry.addProvider(provider);
   }

   @Test
   public void testGetBaseClazzByAlias() {
      Class<? extends Attribute<?>> actual = resolver.getBaseClazz(alias);
      Assert.assertEquals(expected, actual);
   }

   @Test
   public void testGetBaseClazzByType() throws OseeCoreException {
      when(cache.getBaseAttributeTypeId(type)).thenReturn(alias);

      Class<? extends Attribute<?>> actual = resolver.getBaseClazz(type);
      Assert.assertEquals(expected, actual);
   }

   @Test
   public void testRemoveProvider() throws OseeCoreException {
      Class<? extends Attribute<?>> actual1 = resolver.getBaseClazz(alias);
      Assert.assertNotNull(actual1);

      when(cache.getBaseAttributeTypeId(type)).thenReturn(alias);
      Class<? extends Attribute<?>> actual2 = resolver.getBaseClazz(type);

      Assert.assertNotNull(actual2);
      Assert.assertEquals(actual1, actual2);

      registry.removeProvider(new AttributeClassProvider() {

         @Override
         public List<Class<? extends Attribute<?>>> getClasses() {
            return Collections.<Class<? extends Attribute<?>>> singletonList(expected);
         }
      });

      actual1 = resolver.getBaseClazz(alias);
      Assert.assertNull(actual1);

      actual2 = resolver.getBaseClazz(type);
      Assert.assertNull(actual2);
   }

   @Test(expected = OseeCoreException.class)
   public void testIsBaseCompatibleException1() throws OseeCoreException {
      when(cache.getBaseAttributeTypeId(type)).thenReturn(alias);

      resolver.isBaseTypeCompatible(null, type);
   }

   @Test(expected = OseeCoreException.class)
   public void testIsBaseCompatibleException2() throws OseeCoreException {
      resolver.isBaseTypeCompatible(expected, null);
   }

   @Test(expected = OseeCoreException.class)
   public void testIsBaseCompatibleException3() throws OseeCoreException {
      when(cache.getBaseAttributeTypeId(type)).thenReturn(alias + "1");

      resolver.isBaseTypeCompatible(expected, type);
   }

   @Test
   public void testIsBaseCompatible() throws OseeCoreException {
      when(cache.getBaseAttributeTypeId(type)).thenReturn(alias);

      boolean result1 = resolver.isBaseTypeCompatible(expected, type);
      Assert.assertTrue(result1);

      String other = !alias.equals("BooleanAttribute") ? "BooleanAttribute" : "StringAttribute";
      when(cache.getBaseAttributeTypeId(type)).thenReturn(other);

      boolean result2 = resolver.isBaseTypeCompatible(expected, type);
      Assert.assertFalse(result2);
   }

   @Parameters
   public static Collection<Object[]> data() {
      Collection<Object[]> data = new ArrayList<>();

      data.add(new Object[] {"BooleanAttribute", BooleanAttribute.class});
      data.add(new Object[] {"CompressedContentAttribute", CompressedContentAttribute.class});
      data.add(new Object[] {"DateAttribute", DateAttribute.class});
      data.add(new Object[] {"EnumeratedAttribute", EnumeratedAttribute.class});
      data.add(new Object[] {"FloatingPointAttribute", FloatingPointAttribute.class});
      data.add(new Object[] {"IntegerAttribute", IntegerAttribute.class});
      data.add(new Object[] {"JavaObjectAttribute", JavaObjectAttribute.class});
      data.add(new Object[] {"StringAttribute", StringAttribute.class});

      return data;
   }
}
