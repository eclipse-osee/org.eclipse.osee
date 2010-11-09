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
import org.eclipse.osee.framework.ui.skynet.widgets.XComboWithText;
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
public class OperationalImpactXWidget extends XComboWithText implements IArtifactWidget {

   TeamWorkFlowArtifact teamArt;
   public static String ID = "ats.OperationalImpact";
   public static String ID_REQUIRED = "ats.OperationalImpact.required";
   public static String WIDGET_NAME = "OperationalImpactXWidget";

   public OperationalImpactXWidget() {
      super("Operational Impact", "Operational Impact Description", new String[] {"Yes", "No"}, "Yes", true);
   }

   @Override
   public Artifact getArtifact() {
      return teamArt;
   }

   @Override
   public void saveToArtifact() throws OseeCoreException {
      String impact = get();
      if (impact == null || impact.equals("")) {
         teamArt.deleteSoleAttribute(AtsAttributeTypes.OperationalImpactAttr);
      } else {
         teamArt.setSoleAttributeValue(AtsAttributeTypes.OperationalImpactAttr, impact);
      }
      String desc = getDescStr();
      if (desc == null || desc.equals("")) {
         teamArt.deleteSoleAttribute(AtsAttributeTypes.OperationalImpactDescriptionAttr);
      } else {
         teamArt.setSoleAttributeValue(AtsAttributeTypes.OperationalImpactDescriptionAttr, desc);
      }
   }

   @Override
   public void revert() {
      try {
         super.set(teamArt.getSoleAttributeValue(AtsAttributeTypes.OperationalImpactAttr, ""));
         if (getText() != null) {
            getText().set(teamArt.getSoleAttributeValue(AtsAttributeTypes.OperationalImpactDescriptionAttr, ""));
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
      }
   }

   @Override
   public Result isDirty() throws OseeCoreException {
      if (!get().equals(teamArt.getSoleAttributeValue(AtsAttributeTypes.OperationalImpactAttr, ""))) {
         return new Result(true, AtsAttributeTypes.OperationalImpactAttr.toString());
      }
      if (!getDescStr().equals(teamArt.getSoleAttributeValue(AtsAttributeTypes.OperationalImpactDescriptionAttr, ""))) {
         return new Result(true, AtsAttributeTypes.OperationalImpactDescriptionAttr.toString());
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
      if (artifact instanceof TeamWorkFlowArtifact) {
         teamArt = (TeamWorkFlowArtifact) artifact;
      }
   }

   public static class XOperationalImpactXWidgetWorkItem extends WorkWidgetDefinition {

      public XOperationalImpactXWidgetWorkItem() {
         super("Operational Impact - " + ID, ID);
         DynamicXWidgetLayoutData data = new DynamicXWidgetLayoutData(null, XOption.NOT_REQUIRED);
         data.setName("Operational Impact");
         data.setStoreName(getId());
         data.setXWidgetName(WIDGET_NAME);
         set(data);
      }
   }

   public static class XOperationalImpactRequiredXWidgetWorkItem extends WorkWidgetDefinition {

      public XOperationalImpactRequiredXWidgetWorkItem() {
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
