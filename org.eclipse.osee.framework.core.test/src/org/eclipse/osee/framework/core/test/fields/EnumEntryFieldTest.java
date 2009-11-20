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
import java.util.Collection;
import java.util.Collections;
import junit.framework.Assert;
import org.eclipse.osee.framework.core.exception.OseeArgumentException;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.internal.fields.EnumEntryField;
import org.eclipse.osee.framework.core.model.OseeEnumEntry;
import org.eclipse.osee.framework.core.test.mocks.MockDataFactory;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Test Case For {@link EnumEntryField}
 * 
 * @author Roberto E. Escobar
 */
public class EnumEntryFieldTest {

   private static OseeEnumEntry e1;
   private static OseeEnumEntry e2;
   private static OseeEnumEntry e3;
   private static OseeEnumEntry e4;

   @BeforeClass
   public static void prepareTest() {
      e1 = MockDataFactory.createEnumEntry(1);
      e2 = MockDataFactory.createEnumEntry(2);
      e3 = MockDataFactory.createEnumEntry(3);
      e4 = MockDataFactory.createEnumEntry(4);
   }

   @Test
   public void testSetGet() throws OseeCoreException {
      Collection<OseeEnumEntry> enumEntries = new ArrayList<OseeEnumEntry>();
      EnumEntryField field = new EnumEntryField(enumEntries);
      Assert.assertEquals(false, field.isDirty());

      FieldTestUtil.assertSetGet(field, Arrays.asList(e1, e2, e3), Arrays.asList(e1, e2, e3), true);
      field.clearDirty();
      Assert.assertEquals(false, field.isDirty());

      // Same but with changed order and case
      FieldTestUtil.assertSetGet(field, Arrays.asList(e3, e1, e2), Arrays.asList(e1, e2, e3), false);
      Assert.assertEquals(false, field.isDirty());

      // Remove two
      FieldTestUtil.assertSetGet(field, Arrays.asList(e3), Arrays.asList(e3), true);
      field.clearDirty();

      FieldTestUtil.assertSetGet(field, Arrays.asList(e3, e4, e2), Arrays.asList(e3, e4, e2), true);
      field.clearDirty();

      // Add Empty
      FieldTestUtil.assertSetGet(field, Collections.<OseeEnumEntry> emptyList(),
            Collections.<OseeEnumEntry> emptyList(), true);
      field.clearDirty();
   }

   @Test
   public void testEnumEntryDirtyOrdinal() throws OseeCoreException {
      Collection<OseeEnumEntry> enumEntries = new ArrayList<OseeEnumEntry>();
      EnumEntryField field = new EnumEntryField(enumEntries);

      FieldTestUtil.assertSetGet(field, Arrays.asList(e1, e2, e3), Arrays.asList(e1, e2, e3), true);
      field.clearDirty();

      e1.setOrdinal(4);
      Assert.assertEquals(true, e1.isDirty());
      Assert.assertEquals(true, field.isDirty());

      field.clearDirty();
      Assert.assertEquals(false, field.isDirty());
      Assert.assertEquals(false, e1.isDirty());
   }

   @Test
   public void testEnumEntryDirtyName() throws OseeCoreException {
      Collection<OseeEnumEntry> enumEntries = new ArrayList<OseeEnumEntry>();
      EnumEntryField field = new EnumEntryField(enumEntries);

      FieldTestUtil.assertSetGet(field, Arrays.asList(e1, e2, e3), Arrays.asList(e1, e2, e3), true);
      field.clearDirty();

      e3.setName(GUID.create());
      Assert.assertEquals(true, e3.isDirty());
      Assert.assertEquals(true, field.isDirty());

      field.clearDirty();
      Assert.assertEquals(false, field.isDirty());
      Assert.assertEquals(false, e3.isDirty());
   }

   @Test(expected = OseeArgumentException.class)
   public void testEnumEntryNameUniqueness() throws OseeCoreException {
      Collection<OseeEnumEntry> enumEntries = new ArrayList<OseeEnumEntry>();
      EnumEntryField field = new EnumEntryField(enumEntries);

      FieldTestUtil.assertSetGet(field, Arrays.asList(e1, e2, e3), Arrays.asList(e1, e2, e3), true);
      field.clearDirty();

      OseeEnumEntry eX = MockDataFactory.createEnumEntry(1);
      field.set(Arrays.asList(e1, e2, e3, eX));
   }

   @Test(expected = OseeArgumentException.class)
   public void testEnumEntryOrdinalUniqueness() throws OseeCoreException {
      Collection<OseeEnumEntry> enumEntries = new ArrayList<OseeEnumEntry>();
      enumEntries.add(e1);
      enumEntries.add(e2);
      EnumEntryField field = new EnumEntryField(enumEntries);

      OseeEnumEntry eX = MockDataFactory.createEnumEntry(1);
      eX.setName("hello");
      eX.setOrdinal(e1.ordinal());
      field.set(Arrays.asList(e1, e2, eX));
   }

   @Test(expected = OseeArgumentException.class)
   public void testEnumEntryNullName() throws OseeCoreException {
      EnumEntryField field = new EnumEntryField(Collections.<OseeEnumEntry> emptyList());
      OseeEnumEntry eX = MockDataFactory.createEnumEntry(1);
      eX.setName(null);
      eX.setOrdinal(5);
      field.set(Collections.singleton(eX));
   }

   @Test(expected = OseeArgumentException.class)
   public void testEnumEntryNegativeOrdinal() throws OseeCoreException {
      EnumEntryField field = new EnumEntryField(Collections.<OseeEnumEntry> emptyList());
      OseeEnumEntry eX = MockDataFactory.createEnumEntry(1);
      eX.setName("not empty");
      eX.setOrdinal(-1);
      field.set(Collections.singleton(eX));
   }
}
