/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.ide.integration.tests.ats.navigate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.demo.api.DemoArtifactToken;
import org.eclipse.osee.ats.ide.navigate.SubscribeUtility;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.core.enums.DemoUsers;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Test case for {@link SubscribeUtility}
 *
 * @author Donald G. Dunne
 */
public class SubscribeUtilityTest {

   @BeforeClass
   @AfterClass
   public static void cleanup() {
      Artifact userArt = getAlexUserArtifact();
      userArt.deleteRelations(CoreRelationTypes.Users_Artifact);
      userArt.persist(SubscribeUtilityTest.class.getSimpleName() + " - cleanup");
   }

   @Test
   public void test() {
      Artifact alexUserArt = getAlexUserArtifact();
      Assert.assertEquals("Should be nothing subscribed by alex", 0,
         alexUserArt.getRelatedArtifactsCount(CoreRelationTypes.Users_Artifact));

      // Subscribe to 2 team definitions
      Artifact cisCodeTeam = ArtifactQuery.getArtifactFromToken(DemoArtifactToken.CIS_Code);
      Artifact cisSwTeam = ArtifactQuery.getArtifactFromToken(DemoArtifactToken.CIS_SW);

      SubscribeUtility.setSubcriptionsAndPersist(alexUserArt, CoreRelationTypes.Users_Artifact,
         Arrays.asList(cisCodeTeam, cisSwTeam), AtsArtifactTypes.TeamDefinition, getClass().getSimpleName());
      List<Artifact> subscribed = alexUserArt.getRelatedArtifacts(CoreRelationTypes.Users_Artifact);
      Assert.assertTrue("CIS Code should be subscribed", subscribed.contains(cisCodeTeam));
      Assert.assertTrue("CIS SW should be subscribed", subscribed.contains(cisSwTeam));
      Assert.assertFalse("User artifact changes should be persisted", alexUserArt.isDirty());

      // Remove one, add another
      Artifact cisTestTeam = ArtifactQuery.getArtifactFromToken(DemoArtifactToken.CIS_Test);

      SubscribeUtility.setSubcriptionsAndPersist(alexUserArt, CoreRelationTypes.Users_Artifact,
         Arrays.asList(cisCodeTeam, cisTestTeam), AtsArtifactTypes.TeamDefinition, getClass().getSimpleName());
      subscribed = alexUserArt.getRelatedArtifacts(CoreRelationTypes.Users_Artifact);
      Assert.assertTrue("CIS Code should be subscribed", subscribed.contains(cisCodeTeam));
      Assert.assertFalse("CIS SW should NOT be subscribed", subscribed.contains(cisSwTeam));
      Assert.assertTrue("CIS Test should be subscribed", subscribed.contains(cisTestTeam));
      Assert.assertFalse("User artifact changes should be persisted", alexUserArt.isDirty());

      // Un-subscribe all
      SubscribeUtility.setSubcriptionsAndPersist(alexUserArt, CoreRelationTypes.Users_Artifact,
         new ArrayList<Artifact>(), AtsArtifactTypes.TeamDefinition, getClass().getSimpleName());
      subscribed = alexUserArt.getRelatedArtifacts(CoreRelationTypes.Users_Artifact);
      Assert.assertTrue("No subscriptions remain", subscribed.isEmpty());
      Assert.assertFalse("User artifact changes should be persisted", alexUserArt.isDirty());
   }

   private static Artifact getAlexUserArtifact() {
      return ArtifactQuery.getArtifactFromToken(DemoUsers.Alex_Kay);
   }

}
