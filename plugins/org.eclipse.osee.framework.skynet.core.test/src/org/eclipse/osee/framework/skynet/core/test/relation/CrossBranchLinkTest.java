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
package org.eclipse.osee.framework.skynet.core.test.relation;

import static org.eclipse.osee.framework.core.enums.DeletionFlag.EXCLUDE_DELETED;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import junit.framework.Assert;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.artifact.PurgeArtifacts;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.relation.crossbranch.CrossBranchLink;
import org.eclipse.osee.framework.skynet.core.relation.crossbranch.CrossBranchLinkManager;
import org.eclipse.osee.support.test.util.DemoSawBuilds;
import org.eclipse.osee.support.test.util.TestUtil;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class CrossBranchLinkTest {

   public static Artifact folderArt;

   @BeforeClass
   public static void setUp() throws Exception {
      tearDown();
      folderArt =
         ArtifactTypeManager.addArtifact(CoreArtifactTypes.Folder, BranchManager.getCommonBranch(),
            CrossBranchLinkTest.class.getSimpleName());
      folderArt.persist("CrossBranchLinkTest");
   }

   @AfterClass
   public static void tearDown() throws Exception {
      List<Artifact> artifacts = new ArrayList<Artifact>();
      artifacts.addAll(ArtifactQuery.getArtifactListFromName(CrossBranchLinkTest.class.getSimpleName() + "%",
         BranchManager.getCommonBranch(), EXCLUDE_DELETED));
      artifacts.addAll(ArtifactQuery.getArtifactListFromName(CrossBranchLinkTest.class.getSimpleName() + "%",
         BranchManager.getBranchByGuid(DemoSawBuilds.SAW_Bld_1.getGuid()), EXCLUDE_DELETED));
      new PurgeArtifacts(artifacts).execute();
      TestUtil.sleep(4000);
   }

   @Test
   public void testCrossBranchLink() throws OseeCoreException {
      Branch sawBranch = BranchManager.getBranchByGuid(DemoSawBuilds.SAW_Bld_1.getGuid());
      Assert.assertNotNull(sawBranch);

      Artifact artifact1 =
         ArtifactTypeManager.addArtifact(CoreArtifactTypes.GeneralDocument, sawBranch,
            getClass().getSimpleName() + "-1");
      artifact1.addAttribute(CoreAttributeTypes.StaticId, CrossBranchLinkTest.class.getSimpleName());
      artifact1.persist();

      // test equals
      CrossBranchLink linkA = new CrossBranchLink(CoreRelationTypes.SupportingInfo_SupportingInfo, artifact1);
      CrossBranchLink linkB = new CrossBranchLink(CoreRelationTypes.SupportingInfo_SupportingInfo, artifact1);
      Assert.assertTrue(linkA.equals(linkB));
      linkB = new CrossBranchLink(CoreRelationTypes.SupportingInfo_SupportedBy, artifact1);
      Assert.assertFalse(linkA.equals(linkB));

      // test addRelation
      CrossBranchLinkManager.addRelation(folderArt, CoreRelationTypes.SupportingInfo_SupportingInfo, artifact1);

      Collection<CrossBranchLink> links = CrossBranchLinkManager.getLinks(folderArt);
      Assert.assertEquals(1, links.size());
      CrossBranchLink link = links.iterator().next();
      Assert.assertEquals(CoreRelationTypes.SupportingInfo_SupportingInfo, link.getRelationEnum());
      Assert.assertEquals(artifact1, link.getArtifact());

      Artifact artifact2 =
         ArtifactTypeManager.addArtifact(CoreArtifactTypes.GeneralDocument, sawBranch,
            getClass().getSimpleName() + "-2");
      artifact2.addAttribute(CoreAttributeTypes.StaticId, CrossBranchLinkTest.class.getSimpleName());
      artifact2.persist();

      CrossBranchLinkManager.addRelation(folderArt, CoreRelationTypes.SupportingInfo_SupportingInfo, artifact2);

      Artifact artifact3 =
         ArtifactTypeManager.addArtifact(CoreArtifactTypes.GeneralDocument, sawBranch,
            getClass().getSimpleName() + "-3");
      artifact3.addAttribute(CoreAttributeTypes.StaticId, CrossBranchLinkTest.class.getSimpleName());
      artifact3.persist();

      CrossBranchLinkManager.addRelation(folderArt, CoreRelationTypes.Supercedes_Supercedes, artifact3);

      // test getLinks
      links = CrossBranchLinkManager.getLinks(folderArt);
      Assert.assertEquals(3, links.size());

      boolean found1 = false, found2 = false, found3 = false;
      for (CrossBranchLink linkItem : links) {
         if (linkItem.getArtifact().equals(artifact1)) {
            Assert.assertEquals(CoreRelationTypes.SupportingInfo_SupportingInfo, linkItem.getRelationEnum());
            found1 = true;
         } else if (linkItem.getArtifact().equals(artifact2)) {
            Assert.assertEquals(CoreRelationTypes.SupportingInfo_SupportingInfo, linkItem.getRelationEnum());
            found2 = true;
         } else if (linkItem.getArtifact().equals(artifact3)) {
            Assert.assertEquals(CoreRelationTypes.Supercedes_Supercedes, linkItem.getRelationEnum());
            found3 = true;
         }
      }
      Assert.assertTrue(found1);
      Assert.assertTrue(found2);
      Assert.assertTrue(found3);

      // test getRelatedArtifactCount
      Assert.assertEquals(2,
         CrossBranchLinkManager.getRelatedArtifactCount(folderArt, CoreRelationTypes.SupportingInfo_SupportingInfo));
      Assert.assertEquals(0,
         CrossBranchLinkManager.getRelatedArtifactCount(folderArt, CoreRelationTypes.SupportingInfo_SupportedBy));
      Assert.assertEquals(1,
         CrossBranchLinkManager.getRelatedArtifactCount(folderArt, CoreRelationTypes.Supercedes_Supercedes));

      // test deleteRelation
      CrossBranchLinkManager.deleteRelation(folderArt, CoreRelationTypes.SupportingInfo_SupportingInfo, artifact1);
      Assert.assertEquals(1,
         CrossBranchLinkManager.getRelatedArtifactCount(folderArt, CoreRelationTypes.SupportingInfo_SupportingInfo));

      // test deleteRelations
      CrossBranchLinkManager.addRelation(folderArt, CoreRelationTypes.SupportingInfo_SupportingInfo, artifact1);
      Assert.assertEquals(2,
         CrossBranchLinkManager.getRelatedArtifactCount(folderArt, CoreRelationTypes.SupportingInfo_SupportingInfo));
      CrossBranchLinkManager.deleteRelations(folderArt, CoreRelationTypes.SupportingInfo_SupportingInfo);
      Assert.assertEquals(0,
         CrossBranchLinkManager.getRelatedArtifactCount(folderArt, CoreRelationTypes.SupportingInfo_SupportingInfo));
   }

}
