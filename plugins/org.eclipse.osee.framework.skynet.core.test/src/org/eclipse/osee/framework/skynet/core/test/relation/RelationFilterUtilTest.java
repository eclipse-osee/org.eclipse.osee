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
package org.eclipse.osee.framework.skynet.core.test.relation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.eclipse.osee.framework.core.enums.DeletionFlag;
import org.eclipse.osee.framework.core.enums.RelationSide;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.model.test.mocks.MockDataFactory;
import org.eclipse.osee.framework.core.model.type.RelationType;
import org.eclipse.osee.framework.skynet.core.relation.RelationFilterUtil;
import org.eclipse.osee.framework.skynet.core.relation.RelationFilterUtil.RelationMatcher;
import org.eclipse.osee.framework.skynet.core.relation.RelationLink;
import org.eclipse.osee.framework.skynet.core.test.mocks.DataFactory;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Test Case for {@link RelationFilterUtil}
 * 
 * @author Roberto E. Escobar
 */
public class RelationFilterUtilTest {

   private static Branch branch1;
   private static RelationType relationType;

   @BeforeClass
   public static void setUp() {
      branch1 = MockDataFactory.createBranch(100);
      relationType = DataFactory.createRelationType(5);
   }

   @AfterClass
   public static void tearDown() {
      branch1 = null;
      relationType = null;
   }

   @Test
   public void testFilter() {
      List<RelationLink> sourceLinks = DataFactory.createLinks(4, branch1);
      Assert.assertEquals(4, sourceLinks.size());

      List<RelationLink> destination = new ArrayList<RelationLink>();
      RelationFilterUtil.filter(null, destination, null);
      Assert.assertEquals(0, destination.size());

      RelationFilterUtil.filter(sourceLinks, destination, null);
      Assert.assertEquals(4, destination.size());
   }

   @Test
   public void testIncludeExcludeDeletedFilter() {
      List<RelationLink> sourceLinks = DataFactory.createLinks(10, branch1);
      Assert.assertEquals(10, sourceLinks.size());

      RelationMatcher excludeDeleteds = RelationFilterUtil.createMatcher(DeletionFlag.EXCLUDE_DELETED);

      List<RelationLink> destination = new ArrayList<RelationLink>();
      RelationFilterUtil.filter(sourceLinks, destination, excludeDeleteds);
      Assert.assertEquals(10, destination.size());

      destination.clear();
      Assert.assertEquals(0, destination.size());

      DataFactory.setEveryOtherToDeleted(sourceLinks);
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
      List<RelationLink> sourceLinks = DataFactory.createLinks(4, branch1);
      List<RelationLink> destination = new ArrayList<RelationLink>();

      int relationLinkToFind = 2;
      RelationMatcher relationLinkIdMatcher =
         RelationFilterUtil.createFindFirstRelationLinkIdMatcher(relationLinkToFind);

      RelationFilterUtil.filter(sourceLinks, destination, relationLinkIdMatcher);

      // Found 1 -- Check we found what we expected
      Assert.assertEquals(1, destination.size());
      Assert.assertEquals(sourceLinks.get(2), destination.iterator().next());

      // Add two more Relations with RelationId == 2 so we have more than one match 
      // Check we only return the first match
      sourceLinks.add(DataFactory.createRelationLink(2, 55, 66, branch1, relationType));
      sourceLinks.add(DataFactory.createRelationLink(2, 77, 88, branch1, relationType));
      Assert.assertEquals(6, sourceLinks.size());

      destination.clear();
      Assert.assertEquals(0, destination.size());
      RelationFilterUtil.filter(sourceLinks, destination, relationLinkIdMatcher);

      Assert.assertEquals(1, destination.size());
      Assert.assertEquals(sourceLinks.get(2), destination.iterator().next());
   }

   @Test
   public void testFindFirstArtifactIdFilter() {
      RelationLink link1 = DataFactory.createRelationLink(0, 55, 66, branch1, relationType);
      RelationLink link2 = DataFactory.createRelationLink(1, 77, 55, branch1, relationType);

      List<RelationLink> sourceLinks = Arrays.asList(link1, link2);

      // Test Side A Match
      RelationMatcher sideAmatcher = RelationFilterUtil.createFindFirstRelatedArtIdMatcher(55, RelationSide.SIDE_A);

      List<RelationLink> destination = new ArrayList<RelationLink>();
      RelationFilterUtil.filter(sourceLinks, destination, sideAmatcher);

      Assert.assertEquals(1, destination.size());
      Assert.assertEquals(link1, destination.iterator().next());

      // Test Side B Match
      RelationMatcher sideBmatcher = RelationFilterUtil.createFindFirstRelatedArtIdMatcher(55, RelationSide.SIDE_B);
      List<RelationLink> destination2 = new ArrayList<RelationLink>();
      RelationFilterUtil.filter(sourceLinks, destination2, sideBmatcher);

      Assert.assertEquals(1, destination2.size());
      Assert.assertEquals(link2, destination2.iterator().next());
   }

   @Test
   public void testCompositeFilter() {
      RelationLink link1 = DataFactory.createRelationLink(0, 55, 66, branch1, relationType);
      RelationLink link2 = DataFactory.createRelationLink(1, 77, 55, branch1, relationType);
      link2.delete(false);
      RelationLink link3 = DataFactory.createRelationLink(3, 88, 55, branch1, relationType);
      RelationLink link4 = DataFactory.createRelationLink(4, 99, 55, branch1, relationType);

      List<RelationLink> sourceLinks = Arrays.asList(link1, link2, link3, link4);

      RelationMatcher sideBmatcher = RelationFilterUtil.createFindFirstRelatedArtIdMatcher(55, RelationSide.SIDE_B);

      // Find 3rd link since 2nd link is excluded because it is deleted
      RelationMatcher matcher1 = RelationFilterUtil.createMatcher(DeletionFlag.EXCLUDE_DELETED, sideBmatcher);

      List<RelationLink> destination = new ArrayList<RelationLink>();
      RelationFilterUtil.filter(sourceLinks, destination, matcher1);

      Assert.assertEquals(1, destination.size());
      Assert.assertEquals(link3, destination.iterator().next());

      // Find 2nd link since we are including deleteds
      RelationMatcher matcher2 = RelationFilterUtil.createMatcher(DeletionFlag.INCLUDE_DELETED, sideBmatcher);
      List<RelationLink> destination2 = new ArrayList<RelationLink>();
      RelationFilterUtil.filter(sourceLinks, destination2, matcher2);

      Assert.assertEquals(1, destination2.size());
      Assert.assertEquals(link2, destination2.iterator().next());
   }

}
