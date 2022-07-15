/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.ats.ide.integration.tests.orcs.rest.applic;

import static org.eclipse.osee.framework.core.enums.CoreArtifactTokens.DefaultHierarchyRoot;
import java.util.List;
import javax.ws.rs.core.Response;
import org.eclipse.osee.ats.ide.util.ServiceUtil;
import org.eclipse.osee.client.test.framework.NotProductionDataStoreRule;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.core.enums.DemoBranches;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.orcs.rest.model.ArtifactEndpoint;
import org.eclipse.osee.orcs.rest.model.RelationEndpoint;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Hugo Trejo, Torin Grenda, David Miller
 */
public class RelationEndpointTest {

   @Rule
   public NotProductionDataStoreRule notProduction = new NotProductionDataStoreRule();

   private static RelationEndpoint relationEndpoint;
   private static ArtifactEndpoint artifactEndpoint;

   @BeforeClass
   public static void testSetup() {
      relationEndpoint = ServiceUtil.getOseeClient().getRelationEndpoint(DemoBranches.SAW_PL_Working_Branch);
      artifactEndpoint = ServiceUtil.getOseeClient().getArtifactEndpoint(DemoBranches.SAW_PL_Working_Branch);

   }

   /**
    * Simple test for the getRelation rest call. Checking for the following cases <br/>
    * 1. Can create an acceptable relation 2. Cannot create a relation that is not acceptable
    */

   @Test
   public void testGetRelationByType() {
      // Create two artifactIds for softwareRequirements MSWord
      ArtifactId parentArtifact = DefaultHierarchyRoot;
      ArtifactToken sideA = artifactEndpoint.createArtifact(DemoBranches.SAW_PL_Working_Branch,
         CoreArtifactTypes.SoftwareRequirementMsWord, parentArtifact, "requirementA");

      ArtifactToken sideB = artifactEndpoint.createArtifact(DemoBranches.SAW_PL_Working_Branch,
         CoreArtifactTypes.SoftwareRequirementMsWord, parentArtifact, "requirementB");

      // Act
      Response res = relationEndpoint.createRelationByType(sideA, sideB, CoreRelationTypes.RequirementTrace);
      res.close();

      // Check if relation was created
      List<ArtifactId> artIdsSideA =
         ArtifactQuery.createQueryBuilder(DemoBranches.SAW_PL_Working_Branch).andId(sideA).andExists(
            CoreRelationTypes.RequirementTrace_HigherLevelRequirement).getIds();
      List<ArtifactId> artIdsSideB =
         ArtifactQuery.createQueryBuilder(DemoBranches.SAW_PL_Working_Branch).andId(sideB).andExists(
            CoreRelationTypes.RequirementTrace_LowerLevelRequirement).getIds();

      boolean relationCreatedSideA = false;
      boolean relationCreatedSideB = false;
      if (artIdsSideA.size() > 0) {
         for (ArtifactId artifactId : artIdsSideA) {
            if (artifactId.equals(sideA)) {
               relationCreatedSideA = true;
            }
         }
      }
      if (artIdsSideB.size() > 0) {
         for (ArtifactId artifactId : artIdsSideB) {
            if (artifactId.equals(sideB)) {
               relationCreatedSideB = true;
            }
         }
      }

      Assert.assertTrue(relationCreatedSideA && relationCreatedSideB);
   }

   @Test
   public void testGetArtifactsByRelationType() {
      ArtifactToken parentArtifact = DefaultHierarchyRoot;
      List<ArtifactToken> arts = relationEndpoint.getRelatedHierarchy(parentArtifact, ArtifactId.SENTINEL);
      Assert.assertFalse(arts.isEmpty());
   }

   @Test
   public void testGetRelatedRecursive() {
      ArtifactToken parentArtifact = DefaultHierarchyRoot;

      ArtifactToken top = artifactEndpoint.createArtifact(DemoBranches.SAW_PL_Working_Branch,
         CoreArtifactTypes.SoftwareRequirementMsWord, parentArtifact, "requirementA");

      ArtifactToken middle = artifactEndpoint.createArtifact(DemoBranches.SAW_PL_Working_Branch,
         CoreArtifactTypes.SoftwareRequirementMsWord, parentArtifact, "requirementB");

      ArtifactToken bottom = artifactEndpoint.createArtifact(DemoBranches.SAW_PL_Working_Branch,
         CoreArtifactTypes.SoftwareRequirementMsWord, parentArtifact, "requirementC");

      // Act
      Response res = relationEndpoint.createRelationByType(top, middle, CoreRelationTypes.RequirementTrace);
      res.close();

      Response res2 = relationEndpoint.createRelationByType(middle, bottom, CoreRelationTypes.RequirementTrace);
      res2.close();
      List<ArtifactToken> arts =
         relationEndpoint.getRelatedRecursive(top, CoreRelationTypes.RequirementTrace, ArtifactId.SENTINEL);
      Assert.assertTrue(arts.contains(middle));
      Assert.assertTrue(arts.contains(bottom));
   }
}
