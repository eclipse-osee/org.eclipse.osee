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
import org.eclipse.osee.ats.api.util.WidgetIdAts;
import org.eclipse.osee.ats.ide.actions.newaction.CreateNewActionProvider;
import org.eclipse.osee.ats.ide.demo.internal.AtsApiService;
import org.eclipse.osee.ats.ide.util.widgets.xx.XXAssigneesWidget;
import org.eclipse.osee.ats.ide.util.widgets.xx.XXOriginatorWidget;
import org.eclipse.osee.ats.ide.util.widgets.xx.XXPointsNumericWidget;
import org.eclipse.osee.ats.ide.workdef.XWidgetBuilderAts;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.util.XWidgetSwtRenderer;
import org.eclipse.osee.framework.ui.skynet.widgets.xx.XXTextWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.xx.util.listen.XModifiedListener;
import org.eclipse.osee.support.test.util.TestUtil;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.ui.forms.widgets.FormToolkit;

/**
 * @author Donald G. Dunne
 */
public class CreateNewActionProviderDemo implements CreateNewActionProvider {

   private static Collection<IAtsTeamDefinition> demoTeamDefs;
   private XXAssigneesWidget assigneeWidget;
   private XXPointsNumericWidget pointsWidget;
   private XXOriginatorWidget originatorWidget;
   private XXTextWidget titleWidget;

   @Override
   public void getAdditionalXWidgetItems(XWidgetBuilderAts wba, IAtsTeamDefinition teamDef) {
      // Align each widget with it's related andTeamId in case there are multiple different team defs impacted
      wba.andOriginatorWidget().andTeamId(teamDef.getArtifactId());
      wba.andWidget("Assignees", WidgetIdAts.XXAssigneesWidget).andTeamId(teamDef.getArtifactId()).andRequired();
      wba.andWidget("Targeted Version", WidgetIdAts.XXTargetedVersionWidget).andTeamId(teamDef.getArtifactId());
      wba.andWidget("Points", WidgetIdAts.XXPointsNumericWidget).andAttrType(AtsAttributeTypes.PointsNumeric).andTeamId(
         teamDef.getArtifactId());
      wba.andCheckBoxWidget(AtsAttributeTypes.UnplannedWork).andTeamId(teamDef.getArtifactId());
      wba.andTextWidget(AtsAttributeTypes.WorkPackage).andTeamId(teamDef.getArtifactId());
      wba.andWidget("Sprint", WidgetIdAts.XXAgileSprintSelWidget).andTeamId(teamDef.getArtifactId());
      wba.andWidget("Feature Group", WidgetIdAts.XXAgileFeatureSelWidget).andTeamId(teamDef.getArtifactId());
   }

   @Override
   public void widgetCreated(XWidget xWidget, FormToolkit toolkit, Artifact art, XWidgetSwtRenderer xWidgetSwtRenderer,
      XModifiedListener xModListener, boolean isEditable) {
      if (xWidget.getLabel().equals("Originator")) {
         originatorWidget = (XXOriginatorWidget) xWidget;
         originatorWidget.getLabelLinkWidget().addMouseListener(new MouseAdapter() {

            @Override
            public void mouseUp(MouseEvent e) {
               if (e.button == 3) {
                  handlePopulateWithDebugInfo(titleWidget.get());
               }
            }

         });
      } else if (xWidget.getLabel().equals("Assignees")) {
         assigneeWidget = (XXAssigneesWidget) xWidget;
      } else if (xWidget.getLabel().equals("Points")) {
         pointsWidget = (XXPointsNumericWidget) xWidget;
      } else if (xWidget.getLabel().equals("Title")) {
         titleWidget = (XXTextWidget) xWidget;
      }
   }

   @Override
   public void handlePopulateWithDebugInfo(String title) {
      if (originatorWidget != null) {
         originatorWidget.setSelected(AtsApiService.get().userService().getUser());
         originatorWidget.refresh();
      }
      if (assigneeWidget != null) {
         assigneeWidget.setSelected(AtsApiService.get().getUserService().getCurrentUserToken());
         assigneeWidget.refresh();
      }
      if (pointsWidget != null) {
         pointsWidget.setSelected("1");
      }
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
            DemoArtifactToken.SAW_PL_PR_TeamDef, DemoArtifactToken.SAW_PL_HW_TeamDef,
            DemoArtifactToken.SAW_PL_Code_TeamDef, DemoArtifactToken.SAW_PL_Test_TeamDef,
            DemoArtifactToken.SAW_PL_SW_Design_TeamDef, DemoArtifactToken.SAW_PL_Requirements_TeamDef,
            DemoArtifactToken.SAW_PL_Requirements_Simple_TeamDef, DemoArtifactToken.SAW_PL_ARB_TeamDef,

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
