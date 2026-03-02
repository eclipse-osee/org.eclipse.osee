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
package org.eclipse.osee.ats.ide.util.widgets;

import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.util.WidgetIdAts;
import org.eclipse.osee.ats.ide.workflow.teamwf.TeamWorkFlowArtifact;
import org.eclipse.osee.framework.core.widget.WidgetId;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.widgets.XComboWithTextAndComboWithText;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.eclipse.osee.framework.ui.swt.Widgets;
import org.osgi.service.component.annotations.Component;

/**
 * Provides a widget where user is required for Yes,No answer to Operational Impact. If Yes, a description and
 * workaround combo shows, else nothing more is to be done.
 *
 * @author Donald G. Dunne
 */
@Component(service = XWidget.class, immediate = true)
public class XOperationalImpactWithWorkaroundArtWidget extends XComboWithTextAndComboWithText {

   public static WidgetId ID = WidgetIdAts.XOperationalImpactWithWorkaroundArtWidget;

   TeamWorkFlowArtifact teamArt;

   public XOperationalImpactWithWorkaroundArtWidget() {
      super(ID, "Operational Impact", "Operational Impact Description", new String[] {"Yes", "No"}, "Yes", true, true,
         "Workaround", "Workaround Desription", new String[] {"Yes", "No"}, "Yes", true);
   }

   public void saveToArtifact() {
      String impact = get();
      if (impact == null || impact.equals("")) {
         teamArt.deleteSoleAttribute(AtsAttributeTypes.OperationalImpact);
      } else {
         teamArt.setSoleAttributeValue(AtsAttributeTypes.OperationalImpact, impact);
      }
      String desc = getDescStr();
      if (desc == null || desc.equals("")) {
         teamArt.deleteSoleAttribute(AtsAttributeTypes.OperationalImpactDescription);
      } else {
         teamArt.setSoleAttributeValue(AtsAttributeTypes.OperationalImpactDescription, desc);
      }
      String workaroundImpact = getWorkaroundImpact();
      if (workaroundImpact == null || workaroundImpact.equals("")) {
         teamArt.deleteSoleAttribute(AtsAttributeTypes.OperationalImpactWorkaround);
      } else {
         teamArt.setSoleAttributeValue(AtsAttributeTypes.OperationalImpactWorkaround, workaroundImpact);
      }
      String workaroundDesc = getWorkaroundDescStr();
      if (workaroundDesc == null || workaroundDesc.equals("")) {
         teamArt.deleteSoleAttribute(AtsAttributeTypes.OperationalImpactWorkaroundDescription);
      } else {
         teamArt.setSoleAttributeValue(AtsAttributeTypes.OperationalImpactWorkaroundDescription, workaroundDesc);
      }
   }

   private String getDescStr() {
      if (getText() == null || !Widgets.isAccessible(getText().getStyledText())) {
         return "";
      }
      return getText().get();
   }

   private String getWorkaroundImpact() {
      if (getComboWithText() == null) {
         return "";
      }
      return getComboWithText().get();
   }

   private String getWorkaroundDescStr() {
      if (getComboWithText() == null || getComboWithText().getText() == null || !Widgets.isAccessible(
         getComboWithText().getText().getStyledText())) {
         return "";
      }
      return getComboWithText().getText().get();
   }

   @Override
   public void setArtifact(Artifact artifact) {
      super.setArtifact(artifact);
      if (getArtifact().isOfType(AtsArtifactTypes.TeamWorkflow)) {
         teamArt = (TeamWorkFlowArtifact) artifact;
      }
   }

   @Override
   protected int getTextHeightHint() {
      if (getDescStr().equals("")) {
         return 30;
      }
      return super.getTextHeightHint();
   }

}
