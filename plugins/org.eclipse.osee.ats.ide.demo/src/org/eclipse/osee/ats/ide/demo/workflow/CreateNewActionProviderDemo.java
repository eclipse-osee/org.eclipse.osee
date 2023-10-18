/*******************************************************************************
 * Copyright (c) 2021 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.ide.demo.workflow;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import org.eclipse.osee.ats.api.ai.IAtsActionableItem;
import org.eclipse.osee.ats.api.config.tx.IAtsTeamDefinitionArtifactToken;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.demo.DemoArtifactToken;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.ide.actions.newaction.CreateNewActionProvider;
import org.eclipse.osee.ats.ide.demo.internal.AtsApiService;
import org.eclipse.osee.framework.ui.skynet.widgets.builder.XWidgetBuilder;
import org.eclipse.osee.support.test.util.TestUtil;

/**
 * @author Donald G. Dunne
 */
public class CreateNewActionProviderDemo implements CreateNewActionProvider {

   private static Collection<IAtsTeamDefinition> demoTeamDefs;

   @Override
   public void getAdditionalXWidgetItems(XWidgetBuilder wb, IAtsTeamDefinition teamDef) {
      // Align each widget with it's related andTeamId in case there are multiple different team defs impacted
      wb.andWidget("Originator", "XOriginatorHyperlinkWidget").andTeamId(teamDef.getArtifactId()).endWidget();
      wb.andWidget("Assignees", "XAssigneesHyperlinkWidget").andTeamId(
         teamDef.getArtifactId()).andRequired().endWidget();
      wb.andWidget("Targeted Version", "XTargetedVersionHyperlinkWidget").andTeamId(
         teamDef.getArtifactId()).endWidget();
      wb.andXHyperLinkEnumAttr(AtsAttributeTypes.Points).andTeamId(teamDef.getArtifactId()).endWidget();
      wb.andXBoolean(AtsAttributeTypes.UnplannedWork).andTeamId(teamDef.getArtifactId()).endWidget();
      wb.andXText(AtsAttributeTypes.WorkPackage).andTeamId(teamDef.getArtifactId()).endWidget();
      wb.andWidget("Sprint", "XSprintHyperlinkWidget").andTeamId(teamDef.getArtifactId()).endWidget();
      wb.andWidget("Feature Group", "XAgileFeatureHyperlinkWidget").andTeamId(teamDef.getArtifactId()).endWidget();
   }

   @Override
   public boolean hasProviderXWidgetExtensions(Collection<IAtsActionableItem> ais) {
      if (!TestUtil.isDemoDb()) {
         return false;
      }
      Collection<IAtsTeamDefinition> teams = AtsApiService.get().getActionableItemService().getImpactedTeamDefs(ais);
      Collection<IAtsTeamDefinition> progTeamDefs = getDemoTeamDefs();
      List<IAtsTeamDefinition> intersect =
         org.eclipse.osee.framework.jdk.core.util.Collections.setIntersection(teams, progTeamDefs);
      if (!intersect.isEmpty()) {
         return true;
      }
      return false;
   }

   private Collection<IAtsTeamDefinition> getDemoTeamDefs() {
      if (demoTeamDefs == null) {
         demoTeamDefs = new ArrayList<>();
         for (IAtsTeamDefinitionArtifactToken tok : Arrays.asList(

            // SAW PL
            DemoArtifactToken.SAW_PL_ARB_TeamDef, DemoArtifactToken.SAW_PL_TeamDef, DemoArtifactToken.SAW_PL_CR_TeamDef,
            DemoArtifactToken.SAW_PL_HW_TeamDef, DemoArtifactToken.SAW_PL_Code_TeamDef,
            DemoArtifactToken.SAW_PL_Test_TeamDef, DemoArtifactToken.SAW_PL_SW_Design_TeamDef,
            DemoArtifactToken.SAW_PL_Requirements_TeamDef, DemoArtifactToken.SAW_PL_ARB_TeamDef,

            // SAW
            DemoArtifactToken.SAW_SW, DemoArtifactToken.SAW_HW, DemoArtifactToken.SAW_Code, DemoArtifactToken.SAW_Test,
            DemoArtifactToken.SAW_SW_Design, DemoArtifactToken.SAW_Requirements

         )) {
            IAtsTeamDefinition teamDef = AtsApiService.get().getTeamDefinitionService().getTeamDefinitionById(tok);
            demoTeamDefs.add(teamDef);
         }
      }
      return demoTeamDefs;
   }

   @Override
   public String getName() {
      return "SAW, CIS and SAW PL Demo";
   }

}
