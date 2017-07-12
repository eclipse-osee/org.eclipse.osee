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
package org.eclipse.osee.client.integration.tests.integration.skynet.core;

import static org.eclipse.osee.client.demo.DemoChoice.OSEE_CLIENT_DEMO;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.eclipse.osee.client.integration.tests.integration.skynet.core.utils.TestUtil;
import org.eclipse.osee.client.test.framework.OseeClientIntegrationRule;
import org.eclipse.osee.client.test.framework.OseeLogMonitorRule;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.enums.DeletionFlag;
import org.eclipse.osee.framework.core.enums.RelationSide;
import org.eclipse.osee.framework.core.model.type.RelationType;
import org.eclipse.osee.framework.skynet.core.relation.RelationFilterUtil;
import org.eclipse.osee.framework.skynet.core.relation.RelationFilterUtil.RelationMatcher;
import org.eclipse.osee.framework.skynet.core.relation.RelationLink;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;

/**
 * Test Case for {@link RelationFilterUtil}
 *
 * @author Roberto E. Escobar
 */
public class RelationFilterUtilTest {

   @Rule
   public OseeClientIntegrationRule integration = new OseeClientIntegrationRule(OSEE_CLIENT_DEMO);

   @Rule
   public OseeLogMonitorRule monitorRule = new OseeLogMonitorRule();

   private final BranchId branch1 = BranchId.valueOf(100);
   private final RelationType relationType = TestUtil.createRelationType(5);
   private final ArtifactId id55 = ArtifactId.valueOf(55);
   private final ArtifactId id66 = ArtifactId.valueOf(66);
   private final ArtifactId id77 = ArtifactId.valueOf(77);

   @Test
   public void testFilter() {
      List<RelationLink> sourceLinks = TestUtil.createLinks(4, branch1);
      Assert.assertEquals(4, sourceLinks.size());

      List<RelationLink> destination = new ArrayList<>();
      RelationFilterUtil.filter(null, destination, null);
      Assert.assertEquals(0, destination.size());

      RelationFilterUtil.filter(sourceLinks, destination, null);
      Assert.assertEquals(4, destination.size());
   }

   @Test
   public void testIncludeExcludeDeletedFilter() {
      List<RelationLink> sourceLinks = TestUtil.createLinks(10, branch1);
      Assert.assertEquals(10, sourceLinks.size());

      RelationMatcher excludeDeleteds = RelationFilterUtil.createMatcher(DeletionFlag.EXCLUDE_DELETED);

      List<RelationLink> destination = new ArrayList<>();
      RelationFilterUtil.filter(sourceLinks, destination, excludeDeleteds);
      Assert.assertEquals(10, destination.size());

      destination.clear();
      Assert.assertEquals(0, destination.size());

      TestUtil.setEveryOtherToDeleted(sourceLinks);
      RelationFilterUtil.filter(sourceLinks, destination, excludeDeleteds);
      Assert.assertEquals(5, destination.size());

      destination.clear();
      Assert.assertEquals(0, destination.size());

      RelationMatcher includeDeleteds = RelationFilterUtil.createMatcher(DeletionFlag.INCLUDE_DELETED);
      RelationFilterUtil.filter(sourceLinks, destination, includeDeleteds);
      Assert.assertEquals(10, destination.size());
   }

   @Test
   public void testFindFirstRelationLinkIdFilter() {
      List<RelationLink> sourceLinks = TestUtil.createLinks(4, branch1);
      List<RelationLink> destination = new ArrayList<>();

      int relationLinkToFind = 2;
      RelationMatcher relationLinkIdMatcher =
         RelationFilterUtil.createFindFirstRelationLinkIdMatcher(relationLinkToFind);

      RelationFilterUtil.filter(sourceLinks, destination, relationLinkIdMatcher);

      // Found 1 -- Check we found what we expected
      Assert.assertEquals(1, destination.size());
      Assert.assertEquals(sourceLinks.get(2), destination.iterator().next());

      // Add two more Relations with RelationId == 2 so we have more than one match
      // Check we only return the first match
      sourceLinks.add(TestUtil.createRelationLink(2, 55, 66, branch1, relationType));
      sourceLinks.add(TestUtil.createRelationLink(2, 77, 88, branch1, relationType));
      Assert.assertEquals(6, sourceLinks.size());

      destination.clear();
      Assert.assertEquals(0, destination.size());
      RelationFilterUtil.filter(sourceLinks, destination, relationLinkIdMatcher);

      Assert.assertEquals(1, destination.size());
      Assert.assertEquals(sourceLinks.get(2), destination.iterator().next());
   }

   @Test
   public void testFindFirstArtifactIdFilter() {
      RelationLink link1 = TestUtil.createRelationLink(0, id55, id66, branch1, relationType);
      RelationLink link2 = TestUtil.createRelationLink(1, id77, id55, branch1, relationType);

      List<RelationLink> sourceLinks = Arrays.asList(link1, link2);

      // Test Side A Match
      RelationMatcher sideAmatcher = RelationFilterUtil.createFindFirstRelatedArtIdMatcher(id55, RelationSide.SIDE_A);

      List<RelationLink> destination = new ArrayList<>();
      RelationFilterUtil.filter(sourceLinks, destination, sideAmatcher);

      Assert.assertEquals(1, destination.size());
      Assert.assertEquals(link1, destination.iterator().next());

      // Test Side B Match
      RelationMatcher sideBmatcher = RelationFilterUtil.createFindFirstRelatedArtIdMatcher(id55, RelationSide.SIDE_B);
      List<RelationLink> destination2 = new ArrayList<>();
      RelationFilterUtil.filter(sourceLinks, destination2, sideBmatcher);

      Assert.assertEquals(1, destination2.size());
      Assert.assertEquals(link2, destination2.iterator().next());
   }

   @Test
   public void testCompositeFilter() {
      RelationLink link1 = TestUtil.createRelationLink(0, 55, 66, branch1, relationType);
      RelationLink link2 = TestUtil.createRelationLink(1, 77, 55, branch1, relationType);
      link2.delete(false);
      RelationLink link3 = TestUtil.createRelationLink(3, 88, 55, branch1, relationType);
      RelationLink link4 = TestUtil.createRelationLink(4, 99, 55, branch1, relationType);

      List<RelationLink> sourceLinks = Arrays.asList(link1, link2, link3, link4);

      RelationMatcher sideBmatcher = RelationFilterUtil.createFindFirstRelatedArtIdMatcher(id55, RelationSide.SIDE_B);

      // Find 3rd link since 2nd link is excluded because it is deleted
      RelationMatcher matcher1 = RelationFilterUtil.createMatcher(DeletionFlag.EXCLUDE_DELETED, sideBmatcher);

      List<RelationLink> destination = new ArrayList<>();
      RelationFilterUtil.filter(sourceLinks, destination, matcher1);

      Assert.assertEquals(1, destination.size());
      Assert.assertEquals(link3, destination.iterator().next());

      // Find 2nd link since we are including deleteds
      RelationMatcher matcher2 = RelationFilterUtil.createMatcher(DeletionFlag.INCLUDE_DELETED, sideBmatcher);
      List<RelationLink> destination2 = new ArrayList<>();
      RelationFilterUtil.filter(sourceLinks, destination2, matcher2);

      Assert.assertEquals(1, destination2.size());
      Assert.assertEquals(link2, destination2.iterator().next());
   }
}