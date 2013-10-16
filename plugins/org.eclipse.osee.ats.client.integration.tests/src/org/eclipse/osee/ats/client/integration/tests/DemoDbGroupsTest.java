/*******************************************************************************
 * Copyright (c) 2011 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.client.integration.tests;

import java.util.Collection;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.client.demo.DemoArtifactTypes;
import org.eclipse.osee.ats.client.demo.DemoGroups;
import org.eclipse.osee.ats.client.demo.DemoUtil;
import org.eclipse.osee.ats.util.AtsUtil;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.utility.Artifacts;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author Donald G. Dunne
 */
public class DemoDbGroupsTest {

   @BeforeClass
   public static void validateDbInit() throws OseeCoreException {
      DemoUtil.checkDbInitAndPopulateSuccess();
   }

   @Test
   public void testCreateGroups() throws OseeCoreException {
      Artifact groupArt =
         ArtifactQuery.getArtifactFromTypeAndName(CoreArtifactTypes.UniversalGroup, DemoGroups.TEST_GROUP_NAME,
            AtsUtil.getAtsBranchToken());
      Assert.assertNotNull(groupArt);

      Collection<Artifact> members = groupArt.getRelatedArtifacts(CoreRelationTypes.Universal_Grouping__Members);
      Assert.assertEquals(23, members.size());

      Assert.assertEquals(2, Artifacts.getOfType(AtsArtifactTypes.Action, members).size());
      Assert.assertEquals(14, Artifacts.getOfType(AtsArtifactTypes.Task, members).size());
      Assert.assertEquals(2, Artifacts.getOfType(DemoArtifactTypes.DemoCodeTeamWorkflow, members).size());
      Assert.assertEquals(2, Artifacts.getOfType(DemoArtifactTypes.DemoTestTeamWorkflow, members).size());
      Assert.assertEquals(2, Artifacts.getOfType(DemoArtifactTypes.DemoReqTeamWorkflow, members).size());
      Assert.assertEquals(7, Artifacts.getOfType(AtsArtifactTypes.TeamWorkflow, members).size());
   }
}
