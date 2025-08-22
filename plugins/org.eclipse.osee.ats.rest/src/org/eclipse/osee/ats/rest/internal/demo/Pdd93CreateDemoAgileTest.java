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

package org.eclipse.osee.ats.rest.internal.demo;

import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.agile.IAgileBacklog;
import org.eclipse.osee.ats.api.agile.IAgileSprint;
import org.eclipse.osee.ats.api.agile.IAgileTeam;
import org.eclipse.osee.ats.api.demo.DemoArtifactToken;
import org.eclipse.osee.ats.core.demo.DemoUtil;
import org.eclipse.osee.framework.jdk.core.result.XResultData;

/**
 * @author Donald G. Dunne
 */
public class Pdd93CreateDemoAgileTest extends AbstractPopulateDemoDatabaseTest {

   public Pdd93CreateDemoAgileTest(XResultData rd, AtsApi atsApi) {
      super(rd, atsApi);
   }

   @Override
   public void run() {
      rd.logf("Running [%s]...\n", getClass().getSimpleName());
      DemoUtil.checkDbInitAndPopulateSuccess();
      DemoUtil.setPopulateDbSuccessful(false);

      IAgileTeam team = atsApi.getAgileService().getAgileTeam(DemoArtifactToken.SAW_Agile_Team);
      assertNotNull(team);
      assertEquals("SAW Agile Team", team.getName());
      assertEquals(true, team.isActive());

      IAgileBacklog backlog = atsApi.getAgileService().getAgileBacklog(team);
      assertNotNull(backlog);
      assertEquals(DemoArtifactToken.SAW_Backlog.getName(), backlog.getName());
      assertEquals(DemoArtifactToken.SAW_Backlog.getId(), backlog.getId());
      assertEquals(true, backlog.isActive());

      int numFound = 0;
      for (IAgileSprint sprint : atsApi.getAgileService().getAgileSprints(team)) {
         if (sprint.getId().equals(DemoArtifactToken.SAW_Sprint_1.getId())) {
            assertEquals(DemoArtifactToken.SAW_Agile_Team.getId(), Long.valueOf(sprint.getTeamId()));
            assertEquals(DemoArtifactToken.SAW_Sprint_1.getName(), sprint.getName());
            assertEquals(false, sprint.isActive());
            numFound++;
         }
         if (sprint.getId().equals(DemoArtifactToken.SAW_Sprint_2.getId())) {
            assertEquals(DemoArtifactToken.SAW_Agile_Team.getId(), Long.valueOf(sprint.getTeamId()));
            assertEquals(DemoArtifactToken.SAW_Sprint_2.getName(), sprint.getName());
            assertEquals(true, sprint.isActive());
            numFound++;
         }
      }
      assertEquals(2, numFound);

      DemoUtil.setPopulateDbSuccessful(true);
   }

}
