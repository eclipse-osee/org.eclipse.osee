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
package org.eclipse.osee.framework.core.dsl.integration.test;

import junit.framework.Assert;
import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.core.data.IAttributeType;
import org.eclipse.osee.framework.core.data.IRelationType;
import org.eclipse.osee.framework.core.data.Identity;
import org.eclipse.osee.framework.core.dsl.integration.OseeUtil;
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
import org.eclipse.osee.framework.core.exception.OseeCoreException;
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

   @Test
   public void testGetPermission() throws OseeCoreException {
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
      IArtifactType expected = CoreArtifactTypes.GlobalPreferences;

      setupToToken(type, expected);

      Object actual = OseeUtil.toToken(type);
      Assert.assertEquals(expected, actual);

      type.setTypeGuid("x");
      actual = OseeUtil.toToken(type);
      Assert.assertFalse(expected.equals(actual));
   }

   @Test
   public void testToTokenAttributeType() {
      XAttributeType type = OseeDslFactory.eINSTANCE.createXAttributeType();
      IAttributeType expected = CoreAttributeTypes.Description;

      setupToToken(type, expected);

      Object actual = OseeUtil.toToken(type);
      Assert.assertEquals(expected, actual);

      type.setTypeGuid("x");
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

      type.setTypeGuid("x");
      actual = OseeUtil.toToken(type);
      Assert.assertFalse(expected.equals(actual));
   }

   private static void setupToToken(OseeType typeToCheck, Identity expected) {
      String name = "bogus name"; // This should not affect equality
      String guid = expected.getGuid();
      typeToCheck.setName(name);
      typeToCheck.setTypeGuid(guid);

      Assert.assertEquals(name, typeToCheck.getName());
      Assert.assertEquals(guid, typeToCheck.getTypeGuid());

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
