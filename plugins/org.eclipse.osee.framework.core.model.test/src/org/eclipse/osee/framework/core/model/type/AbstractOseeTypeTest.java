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

import org.eclipse.osee.framework.core.enums.StorageState;
import org.eclipse.osee.framework.core.model.AbstractOseeType;
import org.eclipse.osee.framework.core.model.mocks.ModelAsserts;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author Roberto E. Escobar
 */
public class AbstractOseeTypeTest<K, T extends AbstractOseeType> {

   private final T type;
   private final String name;
   private final K guid;

   protected AbstractOseeTypeTest(T type, K guid, String name) {
      this.type = type;
      this.name = name;
      this.guid = guid;
   }

   protected T getType() {
      return type;
   }

   protected String getExpectedName() {
      return name;
   }

   protected K getExpectedGuid() {
      return guid;
   }

   @Test
   public void testSetGetName() throws Exception {
      String newName = GUID.create();
      ModelAsserts.assertTypeSetGet(getType(), AbstractOseeType.NAME_FIELD_KEY, "getName", "setName", getExpectedName(),
         newName);
   }

   @Test
   public void testSetGetStorageState() {
      Assert.assertEquals(StorageState.LOADED, type.getStorageState());
      Assert.assertFalse(type.isDirty());

      type.setStorageState(StorageState.MODIFIED);
      Assert.assertFalse(type.isDirty());
      type.clearDirty();
      Assert.assertFalse(type.isDirty());
      Assert.assertEquals(StorageState.LOADED, type.getStorageState());

      type.setStorageState(StorageState.PURGED);
      Assert.assertFalse(type.isDirty());
      type.clearDirty();
      Assert.assertFalse(type.isDirty());
      Assert.assertEquals(StorageState.PURGED, type.getStorageState());

      type.setStorageState(StorageState.CREATED);
      Assert.assertFalse(type.isDirty());
      type.clearDirty();
      Assert.assertFalse(type.isDirty());
      Assert.assertEquals(StorageState.LOADED, type.getStorageState());
   }

   @Test
   public void testToString() {
      Assert.assertEquals(name, type.toString());
   }

}
