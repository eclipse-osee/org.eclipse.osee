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
package org.eclipse.osee.framework.core.test.fields;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import junit.framework.Assert;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.internal.fields.ArtifactTypeAttributesField;
import org.eclipse.osee.framework.core.model.AttributeType;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.test.mocks.MockDataFactory;
import org.eclipse.osee.framework.core.util.Compare;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Test Case For {@link ArtifactTypeAttributesField}
 * 
 * @author Roberto E. Escobar
 */
public class ArtifactTypeAttributesFieldTest {

   private static AttributeType attr1;
   private static AttributeType attr2;
   private static AttributeType attr3;
   private static AttributeType attr4;
   private static Branch br1;
   private static Branch br2;

   @BeforeClass
   public static void prepareTest() throws OseeCoreException {
      attr1 = MockDataFactory.createAttributeType(1, null);
      attr2 = MockDataFactory.createAttributeType(2, null);
      attr3 = MockDataFactory.createAttributeType(3, null);
      attr4 = MockDataFactory.createAttributeType(4, null);

      br1 = MockDataFactory.createBranch(1);
      br2 = MockDataFactory.createBranch(2);
   }

   @Test
   public void testGetSet() throws OseeCoreException {
      Map<Branch, Collection<AttributeType>> input = new LinkedHashMap<Branch, Collection<AttributeType>>();
      ArtifactTypeAttributesField field = new ArtifactTypeAttributesField(input);

      Assert.assertEquals(false, field.isDirty());

      assertSetGet(field, map(br1, attr1, attr2, attr3), map(br1, attr1, attr2, attr3), true);
      field.clearDirty();

      // Add again in different order
      assertSetGet(field, map(br1, attr2, attr3, attr1), map(br1, attr1, attr2, attr3), false);

      // Remove from list
      assertSetGet(field, map(br1, attr2), map(br1, attr2), true);
      field.clearDirty();

      // Add to list
      assertSetGet(field, map(br1, attr2, attr4), map(br1, attr2, attr4), true);
      field.clearDirty();

      // Add to entry
      Map<Branch, Collection<AttributeType>> values = map(br1, attr2, attr4);
      map(values, br2, attr3, attr1);
      assertSetGet(field, values, values, true);
      field.clearDirty();

      // Add to emptylist
      assertSetGet(field, Collections.<Branch, Collection<AttributeType>> emptyMap(),
            Collections.<Branch, Collection<AttributeType>> emptyMap(), true);
      field.clearDirty();
   }

   private static Map<Branch, Collection<AttributeType>> map(Branch branch, AttributeType... attrs) {
      Map<Branch, Collection<AttributeType>> map = new LinkedHashMap<Branch, Collection<AttributeType>>();
      map.put(branch, Arrays.asList(attrs));
      return map;
   }

   private static Map<Branch, Collection<AttributeType>> map(Map<Branch, Collection<AttributeType>> map, Branch branch, AttributeType... attrs) {
      map.put(branch, Arrays.asList(attrs));
      return map;
   }

   private static void assertSetGet(ArtifactTypeAttributesField field, Map<Branch, Collection<AttributeType>> setValues, Map<Branch, Collection<AttributeType>> expected, boolean expectedDirty) throws OseeCoreException {
      field.set(setValues);
      Assert.assertEquals(expectedDirty, field.isDirty());

      Map<Branch, Collection<AttributeType>> actual = field.get();
      Assert.assertEquals(expected.size(), actual.size());

      Assert.assertFalse(Compare.isDifferent(actual, expected));
   }
}
