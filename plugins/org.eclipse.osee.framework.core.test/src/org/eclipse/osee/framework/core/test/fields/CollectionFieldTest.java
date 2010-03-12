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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import junit.framework.Assert;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.internal.fields.CollectionField;
import org.junit.Test;

/**
 * author Roberto E. Escobar
 */
public class CollectionFieldTest {

   @Test
   public void testSetGetString() throws OseeCoreException {
      List<String> values = new ArrayList<String>();
      CollectionField<String> field = new CollectionField<String>(values);
      Assert.assertFalse(field.isDirty());

      FieldTestUtil.assertSetGet(field, Arrays.asList("a", "b", "c"), Arrays.asList("a", "b", "c"), true);
      field.clearDirty();

      // Test order doesn't matter
      FieldTestUtil.assertSetGet(field, Arrays.asList("b", "c", "a"), Arrays.asList("a", "b", "c"), false);

      // Test remove one
      FieldTestUtil.assertSetGet(field, Arrays.asList("a", "b"), Arrays.asList("a", "b"), true);
      field.clearDirty();

      // Test add one
      FieldTestUtil.assertSetGet(field, Arrays.asList("a", "b", "d"), Arrays.asList("a", "b", "d"), true);
      field.clearDirty();

      // Add Empty
      FieldTestUtil.assertSetGet(field, Collections.<String> emptyList(), Collections.<String> emptyList(), true);
      field.clearDirty();
   }

   @Test
   public void testSetGetInteger() throws OseeCoreException {
      List<Integer> values = new ArrayList<Integer>();
      CollectionField<Integer> field = new CollectionField<Integer>(values);
      Assert.assertFalse(field.isDirty());

      FieldTestUtil.assertSetGet(field, Arrays.asList(1, 2, 3), Arrays.asList(1, 2, 3), true);
      field.clearDirty();

      // Test order doesn't matter
      FieldTestUtil.assertSetGet(field, Arrays.asList(2, 3, 1), Arrays.asList(1, 2, 3), false);

      // Test remove one
      FieldTestUtil.assertSetGet(field, Arrays.asList(1, 2), Arrays.asList(1, 2), true);
      field.clearDirty();

      // Test add 
      FieldTestUtil.assertSetGet(field, Arrays.asList(1, 2, 4, 5), Arrays.asList(1, 2, 4, 5), true);
      field.clearDirty();

      // Add Empty
      FieldTestUtil.assertSetGet(field, Collections.<Integer> emptyList(), Collections.<Integer> emptyList(), true);
      field.clearDirty();
   }

}
