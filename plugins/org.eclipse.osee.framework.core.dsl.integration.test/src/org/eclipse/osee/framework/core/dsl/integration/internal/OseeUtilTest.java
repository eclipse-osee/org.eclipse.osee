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

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.core.data.IAttributeType;
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
import org.eclipse.osee.framework.core.enums.RelationOrderBaseTypes;
import org.eclipse.osee.framework.core.enums.RelationSide;
import org.eclipse.osee.framework.jdk.core.type.Id;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.HexUtil;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test Case for {@link OseeUtil}
 *
 * @author Roberto E. Escobar
 */
public class OseeUtilTest {

   @Test
   public void testIsSideRestricted() throws OseeCoreException {
      checkIsRestricted(XRelationSideEnum.BOTH, true, true);
      checkIsRestricted(XRelationSideEnum.SIDE_A, true, false);
      checkIsRestricted(XRelationSideEnum.SIDE_B, false, true);
   }

   @Test(expected = OseeArgumentException.class)
   public void testIsSideRestrictionXRelationSideEnumNullCheck() throws OseeCoreException {
      OseeUtil.isRestrictedSide(null, RelationSide.SIDE_A);
   }

   @Test(expected = OseeArgumentException.class)
   public void testIsSideRestrictionRelationSideNullCheck() throws OseeCoreException {
      OseeUtil.isRestrictedSide(XRelationSideEnum.BOTH, null);
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
   public void testToTokenArtifactType() throws OseeCoreException {
      XArtifactType type = OseeDslFactory.eINSTANCE.createXArtifactType();
      IArtifactType expected = CoreArtifactTypes.GlobalPreferences;

      setupToToken(type, expected);

      Object actual = OseeUtil.toToken(type);
      Assert.assertEquals(expected, actual);

      type.setUuid("0x1111111111111111");
      actual = OseeUtil.toToken(type);
      Assert.assertFalse(expected.equals(actual));
   }

   @Test
   public void testToTokenAttributeType() throws OseeCoreException {
      XAttributeType type = OseeDslFactory.eINSTANCE.createXAttributeType();
      IAttributeType expected = CoreAttributeTypes.Description;

      setupToToken(type, expected);

      Object actual = OseeUtil.toToken(type);
      Assert.assertEquals(expected, actual);

      type.setUuid("0x1111111111111111");
      actual = OseeUtil.toToken(type);
      Assert.assertFalse(expected.equals(actual));
   }

   @Test
   public void testToTokenRelationType() throws OseeCoreException {
      XRelationType type = OseeDslFactory.eINSTANCE.createXRelationType();
      IRelationType expected = CoreRelationTypes.Allocation__Component;

      setupToToken(type, expected);

      Object actual = OseeUtil.toToken(type);
      Assert.assertEquals(expected, actual);

      type.setUuid("0x1111111111111111");
      actual = OseeUtil.toToken(type);
      Assert.assertFalse(expected.equals(actual));
   }

   @Test(expected = OseeArgumentException.class)
   public void testGetRelationOrderTypeNullCheck() throws OseeCoreException {
      OseeUtil.getRelationOrderType(null);
   }

   @Test(expected = OseeArgumentException.class)
   public void testGetRelationOrderTypeEmptyCheck() throws OseeCoreException {
      OseeUtil.getRelationOrderType("");
   }

   @Test(expected = OseeArgumentException.class)
   public void testGetRelationOrderTypeNotFoundCheck() throws OseeCoreException {
      OseeUtil.getRelationOrderType("a");
   }

   @Test
   public void testGetRelationOrderType() throws OseeCoreException {
      Map<String, String> testData = new HashMap<>();
      testData.put(RelationOrderBaseTypes.LEXICOGRAPHICAL_ASC.getGuid(), "Lexicographical_Ascending");
      testData.put(RelationOrderBaseTypes.LEXICOGRAPHICAL_DESC.getGuid(), "Lexicographical_Descending");
      testData.put(RelationOrderBaseTypes.UNORDERED.getGuid(), "Unordered");
      testData.put(RelationOrderBaseTypes.USER_DEFINED.getGuid(), "User_Defined");
      for (Entry<String, String> entry : testData.entrySet()) {
         String actualName = OseeUtil.getRelationOrderType(entry.getKey());
         String expectedName = entry.getValue();
         Assert.assertEquals(expectedName, actualName);
      }
   }

   @Test(expected = OseeArgumentException.class)
   public void testOrderTypeNameToGuidNullCheck() throws OseeCoreException {
      OseeUtil.orderTypeNameToGuid(null);
   }

   @Test(expected = OseeArgumentException.class)
   public void testOrderTypeNameToGuidEmptyCheck() throws OseeCoreException {
      OseeUtil.orderTypeNameToGuid("");
   }

   @Test(expected = OseeArgumentException.class)
   public void testOrderTypeNameToGuidNotFoundCheck() throws OseeCoreException {
      OseeUtil.orderTypeNameToGuid("a");
   }

   @Test
   public void testOrderTypeNameToGuid() throws OseeCoreException {
      Map<String, String> testData = new HashMap<>();
      testData.put("Lexicographical_Ascending", RelationOrderBaseTypes.LEXICOGRAPHICAL_ASC.getGuid());
      testData.put("Lexicographical_Descending", RelationOrderBaseTypes.LEXICOGRAPHICAL_DESC.getGuid());
      testData.put("Unordered", RelationOrderBaseTypes.UNORDERED.getGuid());
      testData.put("User_Defined", RelationOrderBaseTypes.USER_DEFINED.getGuid());
      for (Entry<String, String> entry : testData.entrySet()) {
         String actualGuid = OseeUtil.orderTypeNameToGuid(entry.getKey());
         String expectedGuid = entry.getValue();
         Assert.assertEquals(expectedGuid, actualGuid);
      }
   }

   private static void setupToToken(OseeType typeToCheck, Id expected) throws OseeCoreException {
      String name = "bogus name"; // This should not affect equality
      typeToCheck.setName(name);
      String uuid = HexUtil.toString(expected.getId());
      typeToCheck.setUuid(uuid);

      Assert.assertEquals(name, typeToCheck.getName());
      Assert.assertEquals(expected.getId().longValue(), HexUtil.toLong(typeToCheck.getUuid()));
      Assert.assertEquals(uuid, typeToCheck.getUuid());
   }

   private static void checkIsRestricted(XRelationSideEnum side, boolean expectedSideA, boolean expectedSideB) throws OseeCoreException {
      boolean actual = OseeUtil.isRestrictedSide(side, RelationSide.SIDE_A);
      String message = String.format("[%s] - Side A error - expected[%s] actual[%s]", side, expectedSideA, actual);
      Assert.assertEquals(message, expectedSideA, actual);

      actual = OseeUtil.isRestrictedSide(side, RelationSide.SIDE_B);
      message = String.format("[%s] - Side B error - expected[%s] actual[%s]", side, expectedSideB, actual);
      Assert.assertEquals(message, expectedSideB, actual);
   }
}
