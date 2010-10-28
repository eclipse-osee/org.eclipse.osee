/*
 * Created on Oct 26, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.field;

import java.util.logging.Level;
import org.eclipse.osee.ats.artifact.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.internal.AtsPlugin;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.plugin.util.Result;
import org.eclipse.osee.framework.ui.skynet.widgets.IArtifactWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.XComboWithText;
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
   public static String ID = "OperationalImpactXWidget";

   public OperationalImpactXWidget() {
      super(OperationalImpactTypes.NAME, "Operational Impact Description", new String[] {"Yes", "No"}, "Yes", true);
      setRequiredEntry(true);
   }

   @Override
   public Artifact getArtifact() {
      return teamArt;
   }

   @Override
   public void saveToArtifact() throws OseeCoreException {
      String impact = get();
      if (impact == null || impact.equals("")) {
         teamArt.deleteSoleAttribute(OperationalImpactTypes.OperationalImpactAttr);
      } else {
         teamArt.setSoleAttributeValue(OperationalImpactTypes.OperationalImpactAttr, impact);
      }
      String desc = getDescStr();
      if (desc == null || desc.equals("")) {
         teamArt.deleteSoleAttribute(OperationalImpactTypes.OperationalImpactDescriptionAttr);
      } else {
         teamArt.setSoleAttributeValue(OperationalImpactTypes.OperationalImpactDescriptionAttr, desc);
      }
   }

   @Override
   public void revert() {
      try {
         super.set(teamArt.getSoleAttributeValue(OperationalImpactTypes.OperationalImpactAttr, ""));
         if (getText() != null) {
            getText().set(teamArt.getSoleAttributeValue(OperationalImpactTypes.OperationalImpactDescriptionAttr, ""));
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
      }
   }

   @Override
   public Result isDirty() throws OseeCoreException {
      if (!get().equals(teamArt.getSoleAttributeValue(OperationalImpactTypes.OperationalImpactAttr, ""))) {
         return new Result(true, OperationalImpactTypes.OperationalImpactAttr.toString());
      }
      if (!getDescStr().equals(
         teamArt.getSoleAttributeValue(OperationalImpactTypes.OperationalImpactDescriptionAttr, ""))) {
         return new Result(true, OperationalImpactTypes.OperationalImpactAttr.toString());
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
         super(OperationalImpactTypes.NAME, OperationalImpactXWidget.ID);
         DynamicXWidgetLayoutData data = new DynamicXWidgetLayoutData(null);
         data.setName(getName());
         data.setStoreName(getId());
         data.setXWidgetName(OperationalImpactXWidget.ID);
         set(data);
      }
   }

   @Override
   protected void createControls(Composite parent, int horizontalSpan) {
      super.createControls(parent, horizontalSpan);
      revert();
   }
}
