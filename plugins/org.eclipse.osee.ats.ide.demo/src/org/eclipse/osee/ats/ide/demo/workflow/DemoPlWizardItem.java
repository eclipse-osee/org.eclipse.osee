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
import org.eclipse.osee.ats.api.demo.DemoArtifactToken;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.ide.actions.wizard.AbstractWizardItem;
import org.eclipse.osee.ats.ide.actions.wizard.WizardFields;
import org.eclipse.osee.ats.ide.demo.internal.AtsApiService;
import org.eclipse.osee.support.test.util.TestUtil;

/**
 * @author Donald G. Dunne
 */
public class DemoPlWizardItem extends AbstractWizardItem {

   private static Collection<IAtsTeamDefinition> demoTeamDefs;

   public DemoPlWizardItem() {
      super(AtsApiService.get());
   }

   @Override
   public Collection<WizardFields> getFields(IAtsTeamDefinition teamDef) {
      ArrayList<WizardFields> fields = new ArrayList<>();

      // Default Fields
      fields.add(WizardFields.Originator);
      fields.add(WizardFields.Assignees);
      fields.add(WizardFields.Points);
      fields.add(WizardFields.UnPlannedWork);
      fields.add(WizardFields.Sprint);
      fields.add(WizardFields.TargetedVersion);
      fields.add(WizardFields.FeatureGroup);
      fields.add(WizardFields.WorkPackage);

      return fields;
   }

   @Override
   protected boolean hasWizardXWidgetExtensions(IAtsTeamDefinition teamDef) {
      if (!TestUtil.isDemoDb()) {
         return false;
      }
      return getDemoTeamDefs().contains(teamDef);
   }

   @Override
   public boolean hasWizardXWidgetExtensions(Collection<IAtsActionableItem> ais) {
      if (!TestUtil.isDemoDb()) {
         return false;
      }
      Collection<IAtsTeamDefinition> teams = AtsApiService.get().getActionableItemService().getImpactedTeamDefs(ais);
      Collection<IAtsTeamDefinition> ceeTeamDefs = getDemoTeamDefs();
      List<IAtsTeamDefinition> intersect =
         org.eclipse.osee.framework.jdk.core.util.Collections.setIntersection(teams, ceeTeamDefs);
      if (!intersect.isEmpty()) {
         return true;
      }
      return false;
   }

   private Collection<IAtsTeamDefinition> getDemoTeamDefs() {
      if (demoTeamDefs == null) {
         demoTeamDefs = new ArrayList<>();
         for (IAtsTeamDefinitionArtifactToken tok : Arrays.asList(DemoArtifactToken.SAW_PL_ARB_TeamDef,
            DemoArtifactToken.SAW_PL_TeamDef, DemoArtifactToken.SAW_PL_CR_TeamDef, DemoArtifactToken.SAW_PL_HW_TeamDef,
            DemoArtifactToken.SAW_PL_Code_TeamDef, DemoArtifactToken.SAW_PL_Test_TeamDef,
            DemoArtifactToken.SAW_PL_SW_Design_TeamDef, DemoArtifactToken.SAW_PL_Requirements_TeamDef,
            DemoArtifactToken.SAW_PL_ARB_TeamDef)) {
            IAtsTeamDefinition teamDef = AtsApiService.get().getTeamDefinitionService().getTeamDefinitionById(tok);
            demoTeamDefs.add(teamDef);
         }
      }
      return demoTeamDefs;
   }

   @Override
   public String getName() {
      return "SAW PL Demo Development";
   }

}
