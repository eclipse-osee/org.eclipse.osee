/*********************************************************************
 * Copyright (c) 2017 Boeing
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

package org.eclipse.osee.ats.ide.integration.tests.ats.demo;

import org.eclipse.osee.ats.api.agile.IAgileBacklog;
import org.eclipse.osee.ats.api.agile.IAgileSprint;
import org.eclipse.osee.ats.api.agile.IAgileTeam;
import org.eclipse.osee.ats.api.demo.DemoArtifactToken;
import org.eclipse.osee.ats.core.demo.DemoUtil;
import org.eclipse.osee.ats.ide.demo.populate.Pdd93CreateDemoAgile;
import org.eclipse.osee.ats.ide.integration.tests.AtsApiService;
import org.eclipse.osee.support.test.util.TestUtil;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author Donald G. Dunne
 */
public class Pdd93CreateDemoAgileTest implements IPopulateDemoDatabaseTest {

   @Test
   public void testAction() throws Exception {
      DemoUtil.checkDbInitAndPopulateSuccess();
      DemoUtil.setPopulateDbSuccessful(false);
      TestUtil.setIsInTest(true);

      Pdd93CreateDemoAgile create = new Pdd93CreateDemoAgile();
      create.run();

      IAgileTeam team = AtsApiService.get().getAgileService().getAgileTeam(DemoArtifactToken.SAW_Agile_Team);
      Assert.assertNotNull(team);
      Assert.assertEquals("SAW Agile Team", team.getName());
      Assert.assertEquals(true, team.isActive());

      AtsApiService.get().getQueryServiceIde().getArtifact(team).reloadAttributesAndRelations();

      IAgileBacklog backlog = AtsApiService.get().getAgileService().getAgileBacklog(team);
      Assert.assertNotNull(backlog);
      Assert.assertEquals(DemoArtifactToken.SAW_Backlog.getName(), backlog.getName());
      Assert.assertEquals(DemoArtifactToken.SAW_Backlog.getId(), backlog.getId());
      Assert.assertEquals(true, backlog.isActive());

      int numFound = 0;
      for (IAgileSprint sprint : AtsApiService.get().getAgileService().getAgileSprints(team)) {
         if (sprint.getId().equals(DemoArtifactToken.SAW_Sprint_1.getId())) {
            Assert.assertEquals(DemoArtifactToken.SAW_Agile_Team.getId(), Long.valueOf(sprint.getTeamId()));
            Assert.assertEquals(DemoArtifactToken.SAW_Sprint_1.getName(), sprint.getName());
            Assert.assertEquals(false, sprint.isActive());
            numFound++;
         }
         if (sprint.getId().equals(DemoArtifactToken.SAW_Sprint_2.getId())) {
            Assert.assertEquals(DemoArtifactToken.SAW_Agile_Team.getId(), Long.valueOf(sprint.getTeamId()));
            Assert.assertEquals(DemoArtifactToken.SAW_Sprint_2.getName(), sprint.getName());
            Assert.assertEquals(true, sprint.isActive());
            numFound++;
         }
      }
      Assert.assertEquals(2, numFound);

      DemoUtil.setPopulateDbSuccessful(true);
   }

}
