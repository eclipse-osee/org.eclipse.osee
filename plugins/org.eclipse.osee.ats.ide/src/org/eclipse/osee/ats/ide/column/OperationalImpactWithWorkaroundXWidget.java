/*********************************************************************
 * Copyright (c) 2010 Boeing
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

package org.eclipse.osee.ats.ide.column;

import java.util.logging.Level;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.ats.ide.workflow.teamwf.TeamWorkFlowArtifact;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.widgets.ArtifactWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.XComboWithTextAndComboWithText;
import org.eclipse.osee.framework.ui.swt.Widgets;
import org.eclipse.swt.widgets.Composite;

/**
 * Provides a widget where user is required for Yes,No answer to Operational Impact. If Yes, a description and
 * workaround combo shows, else nothing more is to be done.
 *
 * @author Donald G. Dunne
 */
public class OperationalImpactWithWorkaroundXWidget extends XComboWithTextAndComboWithText implements ArtifactWidget {

   TeamWorkFlowArtifact teamArt;

   public OperationalImpactWithWorkaroundXWidget() {
      super("Operational Impact", "Operational Impact Description", new String[] {"Yes", "No"}, "Yes", true, true,
         "Workaround", "Workaround Desription", new String[] {"Yes", "No"}, "Yes", true);
   }

   @Override
   public Artifact getArtifact() {
      return teamArt;
   }

   @Override
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

   @Override
   public void revert() {
      try {
         super.set(teamArt.getSoleAttributeValue(AtsAttributeTypes.OperationalImpact, ""));
         if (getText() != null) {
            getText().set(teamArt.getSoleAttributeValue(AtsAttributeTypes.OperationalImpactDescription, ""));
         }
         if (getComboWithText() != null) {
            getComboWithText().set(teamArt.getSoleAttributeValue(AtsAttributeTypes.OperationalImpactWorkaround, ""));
            if (getComboWithText().getText() != null) {
               getComboWithText().getText().set(
                  teamArt.getSoleAttributeValue(AtsAttributeTypes.OperationalImpactWorkaroundDescription, ""));
            }
            getComboWithText().refresh();
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
   }

   @Override
   public Result isDirty() {
      if (!get().equals(teamArt.getSoleAttributeValue(AtsAttributeTypes.OperationalImpact, ""))) {
         return new Result(true, AtsAttributeTypes.OperationalImpact.toString());
      }
      if (!getDescStr().equals(teamArt.getSoleAttributeValue(AtsAttributeTypes.OperationalImpactDescription, ""))) {
         return new Result(true, AtsAttributeTypes.OperationalImpactDescription.toString());
      }
      if (!getWorkaroundImpact().equals(
         teamArt.getSoleAttributeValue(AtsAttributeTypes.OperationalImpactWorkaround, ""))) {
         return new Result(true, AtsAttributeTypes.OperationalImpactWorkaround.toString());
      }
      if (!getWorkaroundDescStr().equals(
         teamArt.getSoleAttributeValue(AtsAttributeTypes.OperationalImpactWorkaroundDescription, ""))) {
         return new Result(true, AtsAttributeTypes.OperationalImpactWorkaroundDescription.toString());
      }
      return Result.FalseResult;
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
      if (artifact.isOfType(AtsArtifactTypes.TeamWorkflow)) {
         teamArt = (TeamWorkFlowArtifact) artifact;
      }
   }

   @Override
   protected void createControls(Composite parent, int horizontalSpan) {
      super.createControls(parent, horizontalSpan);
      revert();
   }

   @Override
   protected int getTextHeightHint() {
      if (getDescStr().equals("")) {
         return 30;
      }
      return super.getTextHeightHint();
   }

}
