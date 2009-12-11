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
package org.eclipse.osee.framework.core.test.model;

import junit.framework.Assert;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeStateException;
import org.eclipse.osee.framework.core.model.AbstractOseeType;
import org.eclipse.osee.framework.core.model.IOseeStorable;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.junit.Test;

/**
 * @author Roberto E. Escobar
 */
public class AbstractOseeTypeTest<T extends AbstractOseeType> {

   private final T type;
   private final String name;
   private final String guid;

   protected AbstractOseeTypeTest(T type, String guid, String name) {
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

   protected String getExpectedGuid() {
      return guid;
   }

   @Test
   public void testSetGetGuid() {
      Assert.assertEquals(getExpectedGuid(), type.getGuid());
   }

   @Test
   public void testGetId() {
      Assert.assertEquals(IOseeStorable.UNPERSISTTED_VALUE, Integer.valueOf(type.getId()));
   }

   @Test(expected = OseeStateException.class)
   public void testSetGetId() throws OseeCoreException {
      type.setId(Integer.MAX_VALUE);
      Assert.assertEquals(Integer.MAX_VALUE, type.getId());

      type.setId(Integer.MAX_VALUE - 1);
   }

   @Test
   public void testSetGetName() throws OseeCoreException {
      Assert.assertEquals(getExpectedName(), type.getName());
      String originalName = type.getName();

      type.clearDirty();
      Assert.assertFalse(type.isDirty());
      Assert.assertFalse(type.areFieldsDirty(AbstractOseeType.NAME_FIELD_KEY));

      // Check reassign doesn't mark as dirty
      type.setName(originalName);
      Assert.assertFalse(type.areFieldsDirty(AbstractOseeType.NAME_FIELD_KEY));

      String newName = GUID.create();
      type.setName(newName);
      Assert.assertEquals(newName, type.getName());

      Assert.assertTrue(type.isDirty());
      Assert.assertTrue(type.areFieldsDirty(AbstractOseeType.NAME_FIELD_KEY));

      type.clearDirty();
      Assert.assertFalse(type.isDirty());
      Assert.assertFalse(type.areFieldsDirty(AbstractOseeType.NAME_FIELD_KEY));

      type.setName(originalName);
      type.clearDirty();
   }

   @Test
   public void testSetGetModificationType() {
      Assert.assertEquals(ModificationType.NEW, type.getModificationType());
      Assert.assertFalse(type.isDirty());
      type.setModificationType(ModificationType.MODIFIED);
      Assert.assertFalse(type.isDirty());
      Assert.assertEquals(ModificationType.MODIFIED, type.getModificationType());
   }

   @Test
   public void testToString() {
      Assert.assertEquals(String.format("%s - [%s]", name, guid), type.toString());
   }

}
