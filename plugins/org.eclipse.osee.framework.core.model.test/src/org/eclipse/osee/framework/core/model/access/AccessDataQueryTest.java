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

import static org.eclipse.osee.framework.core.enums.CoreArtifactTypes.Folder;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.core.enums.PermissionEnum;
import org.eclipse.osee.framework.core.model.mocks.MockDataFactory;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test Case for {@link AccessDataQuery}
 *
 * @author Jeff C. Phillips
 */
public class AccessDataQueryTest {

   @Test
   public void testBranch() {
      BranchId branchToCheck = CoreBranches.SYSTEM_ROOT;

      TestObject testObject = getTestData();
      AccessData data = testObject.getAccessData();

      AccessDataQuery query = new AccessDataQuery(data);
      PermissionStatus status = new PermissionStatus();

      query.branchMatches(PermissionEnum.WRITE, branchToCheck, status);
      Assert.assertTrue(status.matched());

      query.branchMatches(PermissionEnum.FULLACCESS, branchToCheck, status);
      Assert.assertFalse(status.matched());
   }

   @Test
   public void testArtifactType() {
      TestObject testObject = getTestData();
      AccessData data = testObject.getAccessData();
      ArtifactToken artifactToCheck = testObject.getArtifact();

      AccessDataQuery query = new AccessDataQuery(data);
      PermissionStatus status = new PermissionStatus();

      query.artifactTypeMatches(PermissionEnum.WRITE, artifactToCheck, status);
      Assert.assertTrue(status.matched());

      query.artifactTypeMatches(PermissionEnum.FULLACCESS, artifactToCheck, status);
      Assert.assertFalse(status.matched());
   }

   @Test
   public void testArtifact() {
      TestObject testObject = getTestData();
      AccessData data = testObject.getAccessData();
      ArtifactToken artifactToCheck = testObject.getArtifact();

      AccessDataQuery query = new AccessDataQuery(data);
      PermissionStatus status = new PermissionStatus();

      query.artifactMatches(PermissionEnum.WRITE, artifactToCheck, status);
      Assert.assertTrue(status.matched());
   }

   @Test
   public void testAttributeType() {
      TestObject testObject = getTestData();
      AccessData data = testObject.getAccessData();
      ArtifactToken artifactToCheck = testObject.getArtifact();

      AttributeTypeId attributeType = CoreAttributeTypes.ParagraphNumber;
      AttributeTypeId wordAttributeType = CoreAttributeTypes.WordTemplateContent;

      data.add(artifactToCheck, new AccessDetail<>(attributeType, PermissionEnum.WRITE, new Scope()));
      data.add(artifactToCheck, new AccessDetail<>(wordAttributeType, PermissionEnum.READ, new Scope()));

      AccessDataQuery query = new AccessDataQuery(data);
      PermissionStatus status = new PermissionStatus();

      query.artifactMatches(PermissionEnum.WRITE, artifactToCheck, status);
      Assert.assertTrue(status.matched());

      query.attributeTypeMatches(PermissionEnum.READ, artifactToCheck, wordAttributeType, status);
      Assert.assertTrue(status.matched());

      query.attributeTypeMatches(PermissionEnum.WRITE, artifactToCheck, wordAttributeType, status);
      Assert.assertFalse(status.matched());

      query.artifactTypeMatches(PermissionEnum.WRITE, artifactToCheck, status);
      Assert.assertTrue(status.matched());

      query.artifactTypeMatches(PermissionEnum.FULLACCESS, artifactToCheck, status);
      Assert.assertFalse(status.matched());
   }

   @Test
   public void testArtifactMatchesAll() {
      AccessData data = new AccessData();
      IOseeBranch branch = CoreBranches.COMMON;
      ArtifactTypeToken artifactType = CoreArtifactTypes.AbstractSoftwareRequirement;

      ArtifactToken artifact1 = ArtifactToken.valueOf(1, "one", branch, artifactType);
      ArtifactToken artifact2 = ArtifactToken.valueOf(2, "two", branch, artifactType);

      data.add(artifact1, new AccessDetail<>(artifact1, PermissionEnum.READ, new Scope()));
      data.add(artifact2, new AccessDetail<>(artifact2, PermissionEnum.WRITE, new Scope()));

      AccessDataQuery query = new AccessDataQuery(data);

      Assert.assertTrue(query.matchesAll(PermissionEnum.READ));
      Assert.assertFalse(query.matchesAll(PermissionEnum.WRITE));
   }

   @Test
   public void testBranchMatchesAll() {
      AccessData data = new AccessData();
      BranchId common = CoreBranches.COMMON;
      BranchId branch = CoreBranches.SYSTEM_ROOT;

      data.add(common, new AccessDetail<>(common, PermissionEnum.READ, new Scope()));
      data.add(branch, new AccessDetail<>(branch, PermissionEnum.WRITE, new Scope()));

      AccessDataQuery query = new AccessDataQuery(data);

      Assert.assertTrue(query.matchesAll(PermissionEnum.READ));
      Assert.assertFalse(query.matchesAll(PermissionEnum.WRITE));
   }

   @Test
   public void testArtifactMatches() {
      IOseeBranch branch = CoreBranches.COMMON;
      ArtifactToken accessArtifact = ArtifactToken.valueOf(45, "test1", branch, Folder);
      ArtifactToken typeAccessArtifact = ArtifactToken.valueOf(46, "test2", branch, Folder);
      ArtifactToken noAccessArtifact = ArtifactToken.valueOf(47, "test3", branch, Folder);
      ArtifactTypeToken artType = CoreArtifactTypes.Folder;

      AccessDetail<?> specificArtDetail = MockDataFactory.createAccessDetails(accessArtifact, PermissionEnum.DENY, "",
         new Scope().add("very").add("specific"));
      AccessDetail<?> legacyArtDetail =
         MockDataFactory.createAccessDetails(accessArtifact, PermissionEnum.FULLACCESS, "", Scope.createLegacyScope());
      AccessDetail<?> typeDetail =
         MockDataFactory.createAccessDetails(artType, PermissionEnum.WRITE, "", new Scope().add("very"));

      AccessData data = new AccessData();
      data.add(accessArtifact, specificArtDetail);
      data.add(accessArtifact, legacyArtDetail);
      data.add(accessArtifact, typeDetail);
      data.add(typeAccessArtifact, typeDetail);

      AccessDataQuery query = new AccessDataQuery(data);
      PermissionStatus ps = new PermissionStatus();
      query.artifactMatches(PermissionEnum.WRITE, accessArtifact, ps);
      Assert.assertFalse(ps.matched());

      ps = new PermissionStatus();
      query.artifactMatches(PermissionEnum.WRITE, typeAccessArtifact, ps);
      Assert.assertTrue(ps.matched());

      ps = new PermissionStatus();
      query.artifactMatches(PermissionEnum.WRITE, noAccessArtifact, ps);
      Assert.assertTrue(ps.matched());
   }

   private TestObject getTestData() {
      IOseeBranch branchToCheck = CoreBranches.SYSTEM_ROOT;
      ArtifactTypeToken artifactType = CoreArtifactTypes.AbstractSoftwareRequirement;
      ArtifactToken artifactToCheck = ArtifactToken.valueOf(12, "Hello", branchToCheck, artifactType);
      AccessData data = new AccessData();

      data.add(branchToCheck, new AccessDetail<BranchId>(branchToCheck, PermissionEnum.WRITE, new Scope()));
      data.add(artifactToCheck, new AccessDetail<>(artifactToCheck, PermissionEnum.WRITE, new Scope()));
      data.add(artifactToCheck, new AccessDetail<>(artifactType, PermissionEnum.WRITE, new Scope()));

      return new TestObject(artifactToCheck, data);
   }

   private class TestObject {
      final ArtifactToken artifact;
      final AccessData accessData;

      public TestObject(ArtifactToken artifact, AccessData accessData) {
         super();
         this.artifact = artifact;
         this.accessData = accessData;
      }

      public ArtifactToken getArtifact() {
         return artifact;
      }

      public AccessData getAccessData() {
         return accessData;
      }
   }
}
