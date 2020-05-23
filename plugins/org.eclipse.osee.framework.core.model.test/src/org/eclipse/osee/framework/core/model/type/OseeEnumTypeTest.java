/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.framework.core.model.type;

import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.junit.Ignore;
import org.junit.Test;

/**
 * @author Roberto E. Escobar
 */
public class OseeEnumTypeTest {

   @Test(expected = OseeArgumentException.class)
   public void enumNamesWithSpaces() {
      OseeEnumType oseeEnum = new OseeEnumType(0x00L, "Test");
      oseeEnum.valueOf(new String(" apple "));
   }

   @Test(expected = OseeArgumentException.class)
   public void enumNamesWithGarbageData() {
      OseeEnumType oseeEnum = new OseeEnumType(0x00L, "Test");
      oseeEnum.valueOf(new String("#@Gbotkob!11233%20"));
   }

   @Test(expected = OseeArgumentException.class)
   public void enumNamesWithNull() {
      OseeEnumType oseeEnum = new OseeEnumType(0x00L, "Test");
      oseeEnum.valueOf(null);
   }

   @Ignore
   @Test
   public void test() {

   }
   //   @Override
   //   public void testDirty()  {
   //   }
   //
   //   @org.junit.Test
   //   public void testAddOseeEnumEntry()  {
   //      OseeEnumType enum1 =
   //            OseeTypesUtil.createEnumType(cache, factory, "Test 1", "Test 1", "OneEntry", 0, "TwoEntry", 1);
   //      OseeTypesUtil.checkOseeEnumEntries(enum1.values(), "OneEntry", 0, "TwoEntry", 1);
   //
   //      OseeEnumEntry entry = factory.createEnumEntry(cache, "C", "AddedEntry", 4);
   //      enum1.addEntry(entry);
   //
   //      OseeTypesUtil.checkOseeEnumEntries(enum1.values(), "OneEntry", 0, "TwoEntry", 1, "AddedEntry", 4);
   //
   //      enum1.removeEntry(entry);
   //      OseeTypesUtil.checkOseeEnumEntries(enum1.values(), "OneEntry", 0, "TwoEntry", 1);
   //
   //      enum1.removeEntry(enum1.values()[0]);
   //      OseeTypesUtil.checkOseeEnumEntries(enum1.values(), "TwoEntry", 1);
   //   }
   //
}
