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
package org.eclipse.osee.framework.core.model.access;

import static org.eclipse.osee.framework.core.enums.CoreBranches.COMMON;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.core.data.RelationTypeSide;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.core.enums.PermissionEnum;
import org.eclipse.osee.framework.core.enums.RelationSide;
import org.eclipse.osee.framework.core.model.mocks.MockDataFactory;
import org.eclipse.osee.framework.core.model.mocks.ModelAsserts;
import org.eclipse.osee.framework.core.model.type.RelationType;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.util.Compare;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Test Case for {@link AccessData}
 *
 * @author Roberto E. Escobar
 */
public class AccessDataTest {

   private BranchId branchToCheck1;
   private BranchId branchToCheck2;
   private IArtifactType artifactType;
   private AttributeTypeId attributeType;
   private AttributeTypeId wordAttributeType;

   private RelationTypeSide relTypeSide1;
   private RelationTypeSide relTypeSide2;

   private ArtifactToken artifactToCheck;

   @Before
   public void setup() {
      branchToCheck1 = CoreBranches.SYSTEM_ROOT;
      branchToCheck2 = CoreBranches.COMMON;

      artifactType = CoreArtifactTypes.AbstractSoftwareRequirement;
      attributeType = CoreAttributeTypes.ParagraphNumber;
      wordAttributeType = CoreAttributeTypes.WordTemplateContent;

      RelationType relType = MockDataFactory.createRelationType(2, null, null);
      relTypeSide1 = new RelationTypeSide(relType, RelationSide.SIDE_A);
      relTypeSide2 = new RelationTypeSide(relType, RelationSide.SIDE_B);

      artifactToCheck = ArtifactToken.valueOf(12, "Hello", COMMON);
   }

   @Test
   public void testIsEmpty()  {
      AccessData data = new AccessData();
      Assert.assertTrue(data.isEmpty());
      data.add(new Object(), createDetail(7, new Object()));
      Assert.assertFalse(data.isEmpty());
   }

   @Test(expected = OseeArgumentException.class)
   public void testAddNullCheck1()  {
      AccessData data = new AccessData();
      data.add(null, createDetail(4, new Object()));
   }

   @Test(expected = OseeArgumentException.class)
   public void testAddNullCheck2()  {
      AccessData data = new AccessData();
      data.add(new Object(), null);
   }

   @Test(expected = OseeArgumentException.class)
   public void testAddAllCheck1()  {
      AccessData data = new AccessData();
      data.addAll(null, Collections.<AccessDetail<?>> emptyList());
   }

   @Test(expected = OseeArgumentException.class)
   public void testAddAllCheck2()  {
      AccessData data = new AccessData();
      data.addAll(new Object(), null);
   }

   @Test(expected = OseeArgumentException.class)
   public void testGetAccessNullCheck()  {
      AccessData data = new AccessData();
      data.getAccess(null);
   }

   @Test
   public void testAddAll()  {
      Collection<AccessDetail<?>> expectedDetails = new ArrayList<>();
      createTestObjects(expectedDetails, branchToCheck1, artifactType, attributeType, wordAttributeType,
         artifactToCheck, relTypeSide1, relTypeSide2, branchToCheck2);

      AccessData data = new AccessData();
      data.addAll(artifactToCheck, expectedDetails);
      Collection<AccessDetail<?>> actualDetails = data.getAccess(artifactToCheck);
      Assert.assertEquals(expectedDetails.size(), actualDetails.size());
      Assert.assertFalse(Compare.isDifferent(expectedDetails, actualDetails));

      for (AccessDetail<?> expectedDetail : expectedDetails) {
         AccessDetail<?> actualDetail = findObject(expectedDetail, actualDetails);
         ModelAsserts.assertEquals(expectedDetail, actualDetail);
      }
   }

   @Test
   public void testAccessDetailMerge()  {
      AccessData data = new AccessData();

      AccessDetail<?> detail1 = MockDataFactory.createAccessDetails(relTypeSide1, PermissionEnum.DENY, "item 1 - deny",
         new Scope().add("same_scope"));
      AccessDetail<?> detail2 = MockDataFactory.createAccessDetails(relTypeSide1, PermissionEnum.WRITE,
         "item 2 - write", new Scope().add("same_scope"));
      data.add(artifactToCheck, detail1);
      data.add(artifactToCheck, detail2);

      Collection<AccessDetail<?>> details = data.getAccess(artifactToCheck);
      Assert.assertTrue(details.size() == 1);
      AccessDetail<?> actualDetail = details.iterator().next();
      Assert.assertEquals(detail1.getAccessObject(), actualDetail.getAccessObject());
      Assert.assertEquals(detail2.getAccessObject(), actualDetail.getAccessObject());

      Assert.assertEquals(PermissionEnum.DENY, actualDetail.getPermission());
      Assert.assertEquals("item 1 - deny", actualDetail.getReason());
      Assert.assertEquals("/same_scope", actualDetail.getScope().getPath());
   }

   @Test
   public void testAccessDetailMultipleKeys()  {
      AccessDetail<?> detail1 = MockDataFactory.createAccessDetails(relTypeSide1, PermissionEnum.DENY, "item 1 - deny",
         new Scope().add("same_scope"));
      AccessDetail<?> detail2 = MockDataFactory.createAccessDetails(relTypeSide1, PermissionEnum.WRITE,
         "item 2 - write", new Scope().add("same_scope"));

      AccessData data = new AccessData();
      data.add(branchToCheck1, detail1);
      data.add(branchToCheck2, detail2);

      Set<Object> keys = data.keySet();
      Assert.assertEquals(2, keys.size());
      Assert.assertTrue(keys.contains(branchToCheck1));
      Assert.assertTrue(keys.contains(branchToCheck2));

      Collection<AccessDetail<?>> details = data.getAccess(branchToCheck1);
      Assert.assertTrue(details.size() == 1);
      AccessDetail<?> actualDetail = details.iterator().next();
      Assert.assertEquals(detail1, actualDetail);

      details = data.getAccess(branchToCheck2);
      Assert.assertTrue(details.size() == 1);
      actualDetail = details.iterator().next();
      Assert.assertEquals(detail2, actualDetail);
      Assert.assertEquals("/same_scope", actualDetail.getScope().getPath());
   }

   @Test
   public void testAccessDetailMultipleScopeLevels()  {
      AccessDetail<?> detail1 = MockDataFactory.createAccessDetails(relTypeSide1, PermissionEnum.DENY, "item 1 - deny",
         new Scope().add("more").add("specific").add("scope"));
      AccessDetail<?> detail2 = MockDataFactory.createAccessDetails(relTypeSide1, PermissionEnum.WRITE,
         "item 2 - write", new Scope().add("less").add("specific"));

      AccessData data = new AccessData();
      data.add(branchToCheck1, detail1);
      data.add(branchToCheck1, detail2);

      Collection<AccessDetail<?>> details = data.getAccess(branchToCheck1);
      Assert.assertTrue(details.size() == 1);
      AccessDetail<?> actualDetail = details.iterator().next();
      Assert.assertEquals(detail1, actualDetail);
      Assert.assertEquals("/more/specific/scope", actualDetail.getScope().getPath());
   }

   @Test
   public void testAccessDetailLegacyScope()  {
      AccessDetail<?> specificDetail = MockDataFactory.createAccessDetails(relTypeSide1, PermissionEnum.DENY,
         "item 1 - deny", new Scope().add("more").add("specific").add("scope"));
      AccessDetail<?> lessSpecificDetail = MockDataFactory.createAccessDetails(relTypeSide1, PermissionEnum.WRITE,
         "item 2 - write", new Scope().add("less").add("specific"));
      AccessDetail<?> legacyDetail = MockDataFactory.createAccessDetails(relTypeSide1, PermissionEnum.WRITE,
         "item 2 - write", Scope.createLegacyScope());

      AccessData data = new AccessData();
      data.add(branchToCheck1, specificDetail);
      data.add(branchToCheck1, legacyDetail);
      data.add(branchToCheck1, lessSpecificDetail);

      Collection<AccessDetail<?>> details = data.getAccess(branchToCheck1);
      Assert.assertTrue(details.size() == 1);
      AccessDetail<?> actualDetail = details.iterator().next();
      Assert.assertEquals(specificDetail, actualDetail);
      Assert.assertEquals("/more/specific/scope", actualDetail.getScope().getPath());
      Assert.assertEquals(PermissionEnum.DENY, actualDetail.getPermission());
   }

   @Test
   public void testToString()  {
      AccessDetail<?> detail1 = MockDataFactory.createAccessDetails(relTypeSide1, PermissionEnum.DENY, "item 1 - deny",
         new Scope().add("scope 1"));
      AccessDetail<?> detail2 = MockDataFactory.createAccessDetails(relTypeSide1, PermissionEnum.WRITE,
         "item 2 - write", new Scope().add("scope 2"));

      AccessData data = new AccessData();
      Assert.assertEquals("accessData []", data.toString());
      data.add(branchToCheck1, detail1);
      data.add(branchToCheck2, detail2);

      String branch1 =
         "System Root Branch - AccessDetail [permission=DENY, scope=/scope_1, accessObject=RelationTypeSide [type=[sideA_2]<-[relType_2]->[sideB_2], side=SIDE_A], reason=item 1 - deny]";
      String branch2 =
         "Common - AccessDetail [permission=WRITE, scope=/scope_2, accessObject=RelationTypeSide [type=[sideA_2]<-[relType_2]->[sideB_2], side=SIDE_A], reason=item 2 - write]";
      String message = "accessData [\n\t%s,\n\t%s,\n]";
      String forward = String.format(message, branch1, branch2);
      String backward = String.format(message, branch2, branch1);
      Assert.assertTrue(data.toString().equals(forward) || data.toString().equals(backward));
   }

   private static AccessDetail<?> findObject(AccessDetail<?> item, Collection<AccessDetail<?>> details) {
      AccessDetail<?> toReturn = null;
      for (AccessDetail<?> detail : details) {
         if (item.equals(detail)) {
            toReturn = detail;
            break;
         }
      }
      return toReturn;
   }

   private static AccessDetail<?> createDetail(int index, Object expAccessObject) {
      PermissionEnum expPermission = PermissionEnum.values()[Math.abs(index % PermissionEnum.values().length)];
      return MockDataFactory.createAccessDetails(expAccessObject, expPermission, "reason: " + index,
         new Scope().add("scope: " + index));
   }

   private static void createTestObjects(Collection<AccessDetail<?>> expectedDetails, Object... objects) {
      int cnt = 1;
      for (Object expAccessObject : objects) {
         AccessDetail<?> detail = createDetail(cnt++, expAccessObject);
         expectedDetails.add(detail);
      }
   }
}
