/*******************************************************************************
 * Copyright (c) 2010 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.column;

import java.util.logging.Level;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.core.client.team.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.internal.Activator;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.widgets.IArtifactWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.XComboWithText;
import org.eclipse.osee.framework.ui.swt.Widgets;
import org.eclipse.swt.widgets.Composite;

/**
 * Provides a widget where user is required for Yes,No answer to Operational Impact. If Yes, a description and
 * workaround combo shows, else nothing more is to be done.
 *
 * @author Donald G. Dunne
 */
public class OperationalImpactXWidget extends XComboWithText implements IArtifactWidget {

   TeamWorkFlowArtifact teamArt;
   public static String ID = "ats.OperationalImpact";
   public static String ID_REQUIRED = "ats.OperationalImpact.required";

   public OperationalImpactXWidget() {
      super("Operational Impact", "Operational Impact Description", new String[] {"Yes", "No"}, "Yes", true);
   }

   @Override
   public Artifact getArtifact() {
      return teamArt;
   }

   @Override
   public void saveToArtifact()  {
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
   }

   @Override
   public void revert() {
      try {
         super.set(teamArt.getSoleAttributeValue(AtsAttributeTypes.OperationalImpact, ""));
         if (getText() != null) {
            getText().set(teamArt.getSoleAttributeValue(AtsAttributeTypes.OperationalImpactDescription, ""));
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
   }

   @Override
   public Result isDirty()  {
      if (!get().equals(teamArt.getSoleAttributeValue(AtsAttributeTypes.OperationalImpact, ""))) {
         return new Result(true, AtsAttributeTypes.OperationalImpact.toString());
      }
      if (!getDescStr().equals(teamArt.getSoleAttributeValue(AtsAttributeTypes.OperationalImpactDescription, ""))) {
         return new Result(true, AtsAttributeTypes.OperationalImpactDescription.toString());
      }
      return Result.FalseResult;
   }

   private String getDescStr() {
      if (getText() == null || !Widgets.isAccessible(getText().getStyledText())) {
         return "";
      }
      return getText().get();
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
