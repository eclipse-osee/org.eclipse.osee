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
package org.eclipse.osee.framework.core.model.fields;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.core.model.internal.fields.ArtifactTypeAttributesField;
import org.eclipse.osee.framework.core.model.mocks.MockDataFactory;
import org.eclipse.osee.framework.core.model.type.AttributeType;
import org.eclipse.osee.framework.jdk.core.util.Compare;
import org.junit.Assert;
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
   private static BranchId br1;
   private static BranchId br2;

   @BeforeClass
   public static void prepareTest()  {
      attr1 = MockDataFactory.createAttributeType(1, null);
      attr2 = MockDataFactory.createAttributeType(2, null);
      attr3 = MockDataFactory.createAttributeType(3, null);
      attr4 = MockDataFactory.createAttributeType(4, null);

      br1 = CoreBranches.SYSTEM_ROOT;
      br2 = CoreBranches.COMMON;
   }

   @Test
   public void testGetSet()  {
      Map<BranchId, Collection<AttributeType>> input = new LinkedHashMap<>();
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
      Map<BranchId, Collection<AttributeType>> values = map(br1, attr2, attr4);
      map(values, br2, attr3, attr1);
      assertSetGet(field, values, values, true);
      field.clearDirty();

      // Add to emptylist
      assertSetGet(field, Collections.<BranchId, Collection<AttributeType>> emptyMap(),
         Collections.<BranchId, Collection<AttributeType>> emptyMap(), true);
      field.clearDirty();
   }

   private static Map<BranchId, Collection<AttributeType>> map(BranchId branch, AttributeType... attrs) {
      Map<BranchId, Collection<AttributeType>> map = new LinkedHashMap<>();
      map.put(branch, Arrays.asList(attrs));
      return map;
   }

   private static Map<BranchId, Collection<AttributeType>> map(Map<BranchId, Collection<AttributeType>> map, BranchId branch, AttributeType... attrs) {
      map.put(branch, Arrays.asList(attrs));
      return map;
   }

   private static void assertSetGet(ArtifactTypeAttributesField field, Map<BranchId, Collection<AttributeType>> setValues, Map<BranchId, Collection<AttributeType>> expected, boolean expectedDirty)  {
      field.set(setValues);
      Assert.assertEquals(expectedDirty, field.isDirty());

      Map<BranchId, Collection<AttributeType>> actual = field.get();
      Assert.assertEquals(expected.size(), actual.size());

      Assert.assertFalse(Compare.isDifferent(actual, expected));
   }
}
