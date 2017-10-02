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

import java.util.ArrayList;
import java.util.List;
import org.eclipse.osee.framework.core.model.internal.fields.CollectionField;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.junit.Assert;

/**
 * @author Roberto E. Escobar
 */
public class FieldTestUtil {

   private FieldTestUtil() {
   }

   public static <T> void assertSetGet(CollectionField<T> field, List<T> setValues, List<T> expected, boolean expectedDirty)  {
      field.set(setValues);
      Assert.assertEquals(expectedDirty, field.isDirty());

      List<T> actual = new ArrayList<>(field.get());
      Assert.assertEquals(expected.size(), actual.size());
      Assert.assertTrue(Collections.setComplement(actual, expected).isEmpty());
      Assert.assertTrue(Collections.setComplement(expected, actual).isEmpty());
   }
}
