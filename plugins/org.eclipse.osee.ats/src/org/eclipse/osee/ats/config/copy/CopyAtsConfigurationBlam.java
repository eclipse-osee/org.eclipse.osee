/*******************************************************************************
 * Copyright (c) 2011 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.config.copy;

import java.util.Arrays;
import java.util.Collection;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.ats.api.ai.IAtsActionableItem;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.util.widgets.XActionableItemCombo;
import org.eclipse.osee.ats.util.widgets.XTeamDefinitionCombo;
import org.eclipse.osee.framework.core.util.result.XResultData;
import org.eclipse.osee.framework.plugin.core.util.Jobs;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactCache;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.blam.AbstractBlam;
import org.eclipse.osee.framework.ui.skynet.blam.VariableMap;
import org.eclipse.osee.framework.ui.skynet.results.XResultDataUI;
import org.eclipse.osee.framework.ui.skynet.widgets.XModifiedListener;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.util.SwtXWidgetRenderer;
import org.eclipse.ui.forms.widgets.FormToolkit;

/**
 * @author Donald G. Dunne
 */
public class CopyAtsConfigurationBlam extends AbstractBlam {

   private org.eclipse.osee.ats.util.widgets.XTeamDefinitionCombo xTeamDefinitionCombo;
   private org.eclipse.osee.ats.util.widgets.XActionableItemCombo xActionableItemCombo;

   @Override
   public String getName() {
      return "Copy ATS Configuration";
   }

   @Override
   public String getXWidgetsXml() {
      StringBuilder builder = new StringBuilder();
      builder.append("<xWidgets>");
      builder.append(
         "<XWidget xwidgetType=\"XTeamDefinitionCombo\" displayName=\"Top Team Definition to Copy From (most like new config)\" />");
      builder.append(
         "<XWidget xwidgetType=\"XActionableItemCombo\" displayName=\"Top Actionable Item to Copy From\" />");
      builder.append("<XWidget xwidgetType=\"XText\" displayName=\"Name Search String\" />");
      builder.append("<XWidget xwidgetType=\"XText\" displayName=\"Name Replace String\" />");
      builder.append(
         "<XWidget xwidgetType=\"XCheckBox\" displayName=\"Retain Team Leads/Members\" labelAfter=\"true\" horizontalLabel=\"true\"/>");
      builder.append(
         "<XWidget xwidgetType=\"XCheckBox\" displayName=\"Persist Changes\" labelAfter=\"true\" horizontalLabel=\"true\"/>");
      builder.append("</xWidgets>");
      return builder.toString();
   }

   private IAtsTeamDefinition getSelectedTeamDefinition() {
      return (IAtsTeamDefinition) xTeamDefinitionCombo.getSelectedTeamDef();
   }

   private IAtsActionableItem getSelectedActionableItem() {
      return xActionableItemCombo.getSelectedAi();
   }

   @Override
   public void runOperation(VariableMap variableMap, IProgressMonitor monitor) throws Exception {
      try {
         if (ArtifactCache.getDirtyArtifacts().size() > 0) {
            AWorkbench.popup(
               "Dirty artifacts found in cache, save all artifacts before running this operation (may need to restart)");
            return;
         }
         ConfigData data = new ConfigData();
         data.setSearchStr((String) variableMap.getValue("Name Search String"));
         data.setReplaceStr((String) variableMap.getValue("Name Replace String"));
         data.setTeamDef(getSelectedTeamDefinition());
         data.setActionableItem(getSelectedActionableItem());
         data.setPersistChanges(variableMap.getBoolean("Persist Changes"));
         data.setRetainTeamLeads(variableMap.getBoolean("Retain Team Leads/Members"));

         XResultData resultData = new XResultData(false);
         new CopyAtsValidation(data, resultData).validate();
         if (resultData.isErrors() || !data.isPersistChanges()) {
            resultData.log("Validation Complete");
            XResultDataUI.report(resultData, getName());
            return;
         }

         resultData = new XResultData(false);
         CopyAtsConfigurationOperation operation = new CopyAtsConfigurationOperation(data, resultData);
         Jobs.runInJob(operation, true);

      } finally {
         monitor.subTask("Done");
      }
   }

   @Override
   public String getDescriptionUsage() {
      return "This BLAM will use existing configuration of a top Team Definition to create a new configuration..\n" +
      //
      "This includes making team defs, actionable items, setting all team leads/team members and changing name using search string and replace string.";
   }

   @Override
   public Collection<String> getCategories() {
      return Arrays.asList("ATS.ADMIN");
   }

   @Override
   public void widgetCreated(XWidget xWidget, FormToolkit toolkit, Artifact art, SwtXWidgetRenderer dynamicXWidgetLayout, XModifiedListener modListener, boolean isEditable)  {
      super.widgetCreated(xWidget, toolkit, art, dynamicXWidgetLayout, modListener, isEditable);
      if (xWidget.getLabel().equals("Top Team Definition to Copy From (most like new config)")) {
         xTeamDefinitionCombo = (XTeamDefinitionCombo) xWidget;
      }
      if (xWidget.getLabel().equals("Top Actionable Item to Copy From")) {
         xActionableItemCombo = (XActionableItemCombo) xWidget;
      }
   }
}
