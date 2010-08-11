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
package org.eclipse.osee.framework.core.dsl.integration.test.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import junit.framework.Assert;
import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.core.data.IAttributeType;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.data.IRelationType;
import org.eclipse.osee.framework.core.data.Identity;
import org.eclipse.osee.framework.core.dsl.integration.util.OseeUtil;
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
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.core.enums.PermissionEnum;
import org.eclipse.osee.framework.core.enums.RelationOrderBaseTypes;
import org.eclipse.osee.framework.core.enums.RelationSide;
import org.eclipse.osee.framework.core.exception.OseeArgumentException;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.DefaultBasicArtifact;
import org.eclipse.osee.framework.core.model.IBasicArtifact;
import org.eclipse.osee.framework.core.model.test.mocks.MockArtifact;
import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.eclipse.osee.framework.jdk.core.util.GUID;
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
      Map<String, String> testData = new HashMap<String, String>();
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
      Map<String, String> testData = new HashMap<String, String>();
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

   @Test(expected = OseeArgumentException.class)
   public void testGetOseeDslArtifactSourceNullCheck1() throws OseeCoreException {
      OseeUtil.getOseeDslArtifactSource(null);
   }

   @Test(expected = OseeArgumentException.class)
   public void testGetOseeDslArtifactSourceNullCheck2() throws OseeCoreException {
      IBasicArtifact<?> artifact = new DefaultBasicArtifact(45, "abc", "name");
      OseeUtil.getOseeDslArtifactSource(artifact);
   }

   @Test
   public void testGetOseeDslArtifactSource() throws OseeCoreException {
      IOseeBranch branch = CoreBranches.COMMON;
      final String artifactGuid = GUID.create();
      final String artifactName = "artifactTest";
      final String branchName = branch.getName();
      final String branchGuid = branch.getGuid();

      IBasicArtifact<?> artifact = new MockArtifact(artifactGuid, artifactName, branch, CoreArtifactTypes.Artifact, 45);

      String actual = OseeUtil.getOseeDslArtifactSource(artifact);
      String expected =
         String.format("//@artifact_source branch/%s/artifact/%s/ (%s:%s)", branchGuid, artifactGuid, branchName,
            artifactName);
      Assert.assertEquals(expected, actual);
   }

   @Test(expected = OseeArgumentException.class)
   public void testFromOseeDslArtifactSourceNull() throws OseeCoreException {
      OseeUtil.fromOseeDslArtifactSource(null);
   }

   @Test
   public void testFromOseeDslArtifactSource() throws OseeCoreException {
      final String artifactGuid = GUID.create();
      final String branchGuid = CoreBranches.COMMON.getGuid();
      String data = String.format("//@artifact_source branch/%s/artifact/%s/", branchGuid, artifactGuid);
      checkFromOseeDslSource(data, new Pair<String, String>(branchGuid, artifactGuid));

      data = "//@artifact_source branch//artifact/";
      checkFromOseeDslSource(data, null);
   }

   private static void checkFromOseeDslSource(String source, Pair<String, String> expected) throws OseeCoreException {
      Pair<String, String> actual = OseeUtil.fromOseeDslArtifactSource(source);
      if (expected == null) {
         Assert.assertNull(actual);
      } else {
         Assert.assertNotNull(actual);
         Assert.assertEquals(expected.getFirst(), actual.getFirst());
         Assert.assertEquals(expected.getSecond(), actual.getSecond());
      }
   }

   private static void setupToToken(OseeType typeToCheck, Identity expected) {
      String name = "bogus name"; // This should not affect equality
      String guid = expected.getGuid();
      typeToCheck.setName(name);
      typeToCheck.setTypeGuid(guid);

      Assert.assertEquals(name, typeToCheck.getName());
      Assert.assertEquals(guid, typeToCheck.getTypeGuid());
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
