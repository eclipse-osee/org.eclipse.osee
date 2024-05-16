/*********************************************************************
 * Copyright (c) 2024 Boeing
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

import java.util.Collection;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.data.AtsArtifactToken;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.api.demo.DemoArtifactToken;
import org.eclipse.osee.ats.api.user.AtsUser;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.workflow.IAtsGoal;
import org.eclipse.osee.ats.ide.demo.DemoUtil;
import org.eclipse.osee.ats.ide.integration.tests.AtsApiService;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.enums.CoreArtifactTokens;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.util.OseeInf;
import org.junit.Test;

/**
 * @author Donald G. Dunne
 */
public class Pdd96CreateDemoWebExportGoal implements IPopulateDemoDatabaseTest {

   @Test
   public void testAction() throws Exception {
      DemoUtil.checkDbInitAndPopulateSuccess();
      DemoUtil.setPopulateDbSuccessful(false);

      AtsApi atsApi = AtsApiService.get();
      ArtifactToken headingArt = atsApi.getQueryService().getArtifact(AtsArtifactToken.AtsTopFolder);

      IAtsChangeSet changes = atsApi.createChangeSet("Create Web Export");
      AtsUser createdBy = atsApi.getUserService().getCurrentUser();

      IAtsGoal webExportGoal = atsApi.getActionService().createGoal(AtsArtifactToken.WebExportGoal,
         atsApi.getTeamDefinitionService().getTeamDefinitionById(DemoArtifactToken.SAW_PL_TeamDef), atsApi, changes);
      changes.addChild(headingArt, webExportGoal.getArtifactId());

      Collection<ArtifactToken> assigned = atsApi.getQueryService().getAssigned(createdBy);
      for (ArtifactToken wfArt : assigned) {
         changes.relate(webExportGoal.getStoreObject(), AtsRelationTypes.Goal_Member, wfArt);
      }

      // Set customization id for goal customization in WebExportCust.xml being added below
      changes.addAttribute(webExportGoal, AtsAttributeTypes.WorldResultsCustId, "4vgtrpe942a9t1hu3imv30");

      changes.addAttribute(CoreArtifactTokens.XViewerCustomization, CoreAttributeTypes.XViewerCustomization,
         OseeInf.getResourceContents("atsConfig/WebExportCust.xml", Pdd96CreateDemoWebExportGoal.class));

      changes.execute();

      DemoUtil.setPopulateDbSuccessful(true);
   }

}
