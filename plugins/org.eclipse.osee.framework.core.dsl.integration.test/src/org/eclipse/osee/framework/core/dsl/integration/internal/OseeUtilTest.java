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
package org.eclipse.osee.framework.core.dsl.integration.internal;

import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.IRelationType;
import org.eclipse.osee.framework.core.dsl.oseeDsl.AccessPermissionEnum;
import org.eclipse.osee.framework.core.dsl.oseeDsl.ObjectRestriction;
import org.eclipse.osee.framework.core.dsl.oseeDsl.OseeDslFactory;
import org.eclipse.osee.framework.core.dsl.oseeDsl.OseeType;
import org.eclipse.osee.framework.core.dsl.oseeDsl.XArtifactType;
import org.eclipse.osee.framework.core.dsl.oseeDsl.XAttributeType;
import org.eclipse.osee.framework.core.dsl.oseeDsl.XRelationSideEnum;
import org.eclipse.osee.framework.core.dsl.oseeDsl.XRelationType;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.core.enums.PermissionEnum;
import org.eclipse.osee.framework.core.enums.RelationSide;
import org.eclipse.osee.framework.jdk.core.type.Id;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test Case for {@link OseeUtil}
 *
 * @author Roberto E. Escobar
 */
public class OseeUtilTest {

   @Test
   public void testIsSideRestricted() {
      checkIsRestricted(XRelationSideEnum.BOTH, true, true);
      checkIsRestricted(XRelationSideEnum.SIDE_A, true, false);
      checkIsRestricted(XRelationSideEnum.SIDE_B, false, true);
   }

   @Test(expected = OseeArgumentException.class)
   public void testIsSideRestrictionXRelationSideEnumNullCheck() {
      OseeUtil.isRestrictedSide(null, RelationSide.SIDE_A);
   }

   @Test(expected = OseeArgumentException.class)
   public void testIsSideRestrictionRelationSideNullCheck() {
      OseeUtil.isRestrictedSide(XRelationSideEnum.BOTH, null);
   }

   @Test
   public void testGetPermission() {
      ObjectRestriction restriction = OseeDslFactory.eINSTANCE.createObjectRestriction();
      restriction.setPermission(AccessPermissionEnum.ALLOW);
      Assert.assertEquals(AccessPermissionEnum.ALLOW, restriction.getPermission());

      PermissionEnum expectedEnum = PermissionEnum.WRITE;
      PermissionEnum actualEnum = OseeUtil.getPermission(restriction);
      Assert.assertEquals(expectedEnum, actualEnum);

      restriction.setPermission(AccessPermissionEnum.DENY);
      Assert.assertEquals(AccessPermissionEnum.DENY, restriction.getPermission());
      expectedEnum = PermissionEnum.READ;
      actualEnum = OseeUtil.getPermission(restriction);
      Assert.assertEquals(expectedEnum, actualEnum);
   }

   @Test
   public void testToTokenArtifactType() {
      XArtifactType type = OseeDslFactory.eINSTANCE.createXArtifactType();
      ArtifactTypeToken expected = CoreArtifactTypes.GlobalPreferences;

      setupToToken(type, expected);

      Object actual = OseeUtil.toToken(type);
      Assert.assertEquals(expected, actual);

      type.setId("1111111111111111");
      actual = OseeUtil.toToken(type);
      Assert.assertFalse(expected.equals(actual));
   }

   @Test
   public void testToTokenAttributeType() {
      XAttributeType type = OseeDslFactory.eINSTANCE.createXAttributeType();
      AttributeTypeId expected = CoreAttributeTypes.Description;

      setupToToken(type, expected);

      Object actual = OseeUtil.toToken(type);
      Assert.assertEquals(expected, actual);

      type.setId("1111111111111111");
      actual = OseeUtil.toToken(type);
      Assert.assertFalse(expected.equals(actual));
   }

   @Test
   public void testToTokenRelationType() {
      XRelationType type = OseeDslFactory.eINSTANCE.createXRelationType();
      IRelationType expected = CoreRelationTypes.Allocation__Component;

      setupToToken(type, expected);

      Object actual = OseeUtil.toToken(type);
      Assert.assertEquals(expected, actual);

      type.setId("1111111111111111");
      actual = OseeUtil.toToken(type);
      Assert.assertFalse(expected.equals(actual));
   }

   private static void setupToToken(OseeType typeToCheck, Id expected) {
      String name = "bogus name"; // This should not affect equality
      typeToCheck.setName(name);
      String uuid = String.valueOf(expected.getId());
      typeToCheck.setId(uuid);

      Assert.assertEquals(name, typeToCheck.getName());
      Assert.assertEquals(expected.getId().longValue(), Long.valueOf(typeToCheck.getId()).longValue());
      Assert.assertEquals(uuid, typeToCheck.getId());
   }

   private static void checkIsRestricted(XRelationSideEnum side, boolean expectedSideA, boolean expectedSideB) {
      boolean actual = OseeUtil.isRestrictedSide(side, RelationSide.SIDE_A);
      String message = String.format("[%s] - Side A error - expected[%s] actual[%s]", side, expectedSideA, actual);
      Assert.assertEquals(message, expectedSideA, actual);

      actual = OseeUtil.isRestrictedSide(side, RelationSide.SIDE_B);
      message = String.format("[%s] - Side B error - expected[%s] actual[%s]", side, expectedSideB, actual);
      Assert.assertEquals(message, expectedSideB, actual);
   }
}
