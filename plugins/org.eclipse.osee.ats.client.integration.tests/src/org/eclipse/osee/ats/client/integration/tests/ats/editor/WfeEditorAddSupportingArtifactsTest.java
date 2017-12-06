/*******************************************************************************
 * Copyright (c) 2016 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.client.integration.tests.ats.editor;

import static org.eclipse.osee.framework.core.enums.CoreBranches.COMMON;
import java.util.Arrays;
import org.eclipse.osee.ats.client.integration.tests.ats.core.client.AtsTestUtil;
import org.eclipse.osee.ats.core.client.team.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.editor.WfeEditorAddSupportingArtifacts;
import org.eclipse.osee.framework.core.enums.CoreArtifactTokens;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test for {@link WfeEditorAddSupportingArtifacts}
 *
 * @author Donald G. Dunne
 */
public class WfeEditorAddSupportingArtifactsTest {

   @After
   public void cleanup() {
      AtsTestUtil.cleanup();
   }

   @Test
   public void testValidateAndRun() {
      AtsTestUtil.cleanupAndReset(getClass().getSimpleName());
      TeamWorkFlowArtifact teamWf = AtsTestUtil.getTeamWf();

      Artifact rootArt = ArtifactQuery.getArtifactFromId(CoreArtifactTokens.DefaultHierarchyRoot, COMMON);
      Artifact firstArt = null, secondArt = null;
      for (Artifact child : rootArt.getChildren()) {
         if (firstArt == null) {
            firstArt = child;
         } else {
            secondArt = child;
            break;
         }
      }

      WfeEditorAddSupportingArtifacts job =
         new WfeEditorAddSupportingArtifacts(teamWf, Arrays.asList(firstArt, secondArt));
      job.validate();
      job.run(null);

      Assert.assertTrue(((Artifact) teamWf.getStoreObject()).getRelatedArtifacts(
         CoreRelationTypes.SupportingInfo_SupportingInfo).contains(firstArt));
      Assert.assertTrue(((Artifact) teamWf.getStoreObject()).getRelatedArtifacts(
         CoreRelationTypes.SupportingInfo_SupportingInfo).contains(secondArt));
   }

}
