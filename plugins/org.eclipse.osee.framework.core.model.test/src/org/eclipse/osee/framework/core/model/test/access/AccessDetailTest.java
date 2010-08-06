/*******************************************************************************
 * Copyright (c) 2010 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.core.model.test.access;

import java.util.ArrayList;
import java.util.Collection;
import junit.framework.Assert;
import org.eclipse.osee.framework.core.enums.PermissionEnum;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.access.AccessDetail;
import org.eclipse.osee.framework.core.model.test.mocks.MockDataFactory;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

/**
 * Test Case for {@link AccessDetail}
 * 
 * @author Roberto E. Escobar
 */
@RunWith(Parameterized.class)
public class AccessDetailTest {

   private final Object expAccessObject;
   private final PermissionEnum expPermission;
   private final String expReason;
   private final AccessDetail<?> target;

   public AccessDetailTest(AccessDetail<?> target, Object expAccessObject, PermissionEnum expPermission, String expReason) {
      this.target = target;
      this.expAccessObject = expAccessObject;
      this.expPermission = expPermission;
      this.expReason = expReason;
   }

   @Test
   public void testGetReason() {
      Assert.assertEquals(expReason, target.getReason());
   }

   @Test
   public void testGetPermission() {
      Assert.assertEquals(expPermission, target.getPermission());
   }

   @Test
   public void testGetAccessObject() {
      Assert.assertEquals(expAccessObject, target.getAccessObject());
   }

   @Test
   public void testSetPermission() {
      Assert.assertEquals(expPermission, target.getPermission());
      PermissionEnum anotherPermission = PermissionEnum.NONE;

      target.setPermission(anotherPermission);
      Assert.assertEquals(anotherPermission, target.getPermission());

      target.setPermission(expPermission);
      Assert.assertEquals(expPermission, target.getPermission());
   }

   @Test
   public void testHashCodeAndEquals() {
      Assert.assertTrue(target.equals(target));
      Assert.assertTrue(target.hashCode() == target.hashCode());

      AccessDetail<?> other = MockDataFactory.createAccessDetails(expAccessObject, PermissionEnum.NONE, null);
      Assert.assertTrue(target.equals(other));
      Assert.assertTrue(target.hashCode() == other.hashCode());

      AccessDetail<?> nulled = MockDataFactory.createAccessDetails(null, PermissionEnum.NONE, null);
      Assert.assertFalse(target.equals(nulled));
      Assert.assertTrue(target.hashCode() != nulled.hashCode());

      Collection<AccessDetail<?>> collect = new ArrayList<AccessDetail<?>>();
      collect.add(target);
      Assert.assertEquals(1, collect.size());
      Assert.assertTrue(collect.contains(target));
      Assert.assertTrue(collect.contains(other));
      Assert.assertFalse(collect.contains(nulled));
   }

   @Test
   public void testToString() {
      String expected =
         "accessDetail [ object=[" + expAccessObject + "] permission=[" + expPermission + "] reason=[" + expReason + "]]";
      Assert.assertEquals(expected, target.toString());
   }

   @Parameters
   public static Collection<Object[]> getData() throws OseeCoreException {
      Collection<Object[]> data = new ArrayList<Object[]>();
      addTest(data, "Hello", PermissionEnum.DENY, "A reason");
      addTest(data, 456, PermissionEnum.WRITE, null);
      addTest(data, MockDataFactory.createArtifactType(4), PermissionEnum.FULLACCESS, "reason3");
      addTest(data, MockDataFactory.createAttributeType(), PermissionEnum.READ, "xx");
      return data;
   }

   private static <T> void addTest(Collection<Object[]> data, T expAccessObject, PermissionEnum expPermission, String expReason) {
      String reasonToCheck = expReason;
      if (expReason == null) {
         reasonToCheck = Strings.emptyString();
      }
      AccessDetail<T> target = MockDataFactory.createAccessDetails(expAccessObject, expPermission, expReason);
      data.add(new Object[] {target, expAccessObject, expPermission, reasonToCheck});
   }

}