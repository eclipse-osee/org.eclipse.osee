/*
 * Created on Oct 26, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.field;

import java.util.logging.Level;
import org.eclipse.osee.ats.artifact.AtsAttributeTypes;
import org.eclipse.osee.ats.artifact.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.internal.AtsPlugin;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.plugin.util.Result;
import org.eclipse.osee.framework.ui.skynet.widgets.IArtifactWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.XComboWithTextAndComboWithText;
import org.eclipse.osee.framework.ui.skynet.widgets.XOption;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.DynamicXWidgetLayoutData;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkWidgetDefinition;
import org.eclipse.osee.framework.ui.swt.Widgets;
import org.eclipse.swt.widgets.Composite;

/**
 * Provides a widget where user is required for Yes,No answer to Operational Impact. If Yes, a description and
 * workaround combo shows, else nothing more is to be done.
 * 
 * @author Donald G. Dunne
 */
public class OperationalImpactWithWorkaroundXWidget extends XComboWithTextAndComboWithText implements IArtifactWidget {

   TeamWorkFlowArtifact teamArt;
   public static String ID = "ats.OperationalImpactWithWorkaround";
   public static String ID_REQUIRED = "ats.OperationalImpactWithWorkaround.required";
   public static String WIDGET_NAME = "OperationalImpactWithWorkaroundXWidget";

   public OperationalImpactWithWorkaroundXWidget() {
      super("Operational Impact", "Operational Impact Description", new String[] {"Yes", "No"}, "Yes", true, true,
         "Workaround", "Workaround Desription", new String[] {"Yes", "No"}, "Yes", true);
   }

   @Override
   public Artifact getArtifact() {
      return teamArt;
   }

   @Override
   public void saveToArtifact() throws OseeCoreException {
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
         OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
      }
   }

   @Override
   public Result isDirty() throws OseeCoreException {
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
      if (getComboWithText() == null || getComboWithText().getText() == null || !Widgets.isAccessible(getComboWithText().getText().getStyledText())) {
         return "";
      }
      return getComboWithText().getText().get();
   }

   @Override
   public void setArtifact(Artifact artifact) {
      if (artifact instanceof TeamWorkFlowArtifact) {
         teamArt = (TeamWorkFlowArtifact) artifact;
      }
   }

   public static class XOperationalImpactWithWorkaroundXWidgetWorkItem extends WorkWidgetDefinition {

      public XOperationalImpactWithWorkaroundXWidgetWorkItem() {
         super("Operational Impact - " + ID, ID);
         DynamicXWidgetLayoutData data = new DynamicXWidgetLayoutData(null, XOption.NOT_REQUIRED);
         data.setName("Operational Impact");
         data.setStoreName(getId());
         data.setXWidgetName(WIDGET_NAME);
         set(data);
      }
   }
   public static class XOperationalImpactWithWorkaroundRequiredXWidgetWorkItem extends WorkWidgetDefinition {

      public XOperationalImpactWithWorkaroundRequiredXWidgetWorkItem() {
         super("Operational Impact - " + ID_REQUIRED, ID_REQUIRED);
         DynamicXWidgetLayoutData data = new DynamicXWidgetLayoutData(null, XOption.REQUIRED);
         data.setName("Operational Impact");
         data.setStoreName(getId());
         data.setXWidgetName(WIDGET_NAME);
         set(data);
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
