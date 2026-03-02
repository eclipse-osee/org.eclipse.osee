/*********************************************************************
 * Copyright (c) 2011 Boeing
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

package org.eclipse.osee.ats.ide.config.copy;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.ats.api.ai.IAtsActionableItem;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.ide.blam.AbstractAtsBlam;
import org.eclipse.osee.ats.ide.navigate.AtsNavigateViewItems;
import org.eclipse.osee.ats.ide.util.widgets.XActionableItemComboWidget;
import org.eclipse.osee.ats.ide.util.widgets.XTeamDefinitionComboWidget;
import org.eclipse.osee.framework.core.widget.WidgetId;
import org.eclipse.osee.framework.core.widget.XWidgetData;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.plugin.core.util.Jobs;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactCache;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavItemCat;
import org.eclipse.osee.framework.ui.skynet.blam.AbstractBlam;
import org.eclipse.osee.framework.ui.skynet.blam.VariableMap;
import org.eclipse.osee.framework.ui.skynet.results.XResultDataUI;
import org.eclipse.osee.framework.ui.skynet.widgets.XModifiedListener;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.util.XWidgetSwtRenderer;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.osgi.service.component.annotations.Component;

/**
 * @author Donald G. Dunne
 */
@Component(service = AbstractBlam.class, immediate = true)
public class CopyAtsConfigurationBlam extends AbstractAtsBlam {

   private static final String PERSIST_CHANGES = "Persist Changes";
   private static final String RETAIN_TEAM_LEADS_MEMBERS = "Retain Team Leads/Members";
   private static final String NAME_REPLACE_STRING = "Name Replace String";
   private static final String NAME_SEARCH_STRING = "Name Search String";
   private org.eclipse.osee.ats.ide.util.widgets.XTeamDefinitionComboWidget xTeamDefinitionCombo;
   private org.eclipse.osee.ats.ide.util.widgets.XActionableItemComboWidget xActionableItemCombo;

   @Override
   public String getName() {
      return "Copy ATS Configuration";
   }

   @Override
   public List<XWidgetData> getXWidgetItems() {
      createWidgetBuilder();
      wba.andTeamDefinitionWidget().andSingleSelect();
      wba.andActionableItemActiveWidget().andSingleSelect();
      wb.andWidget(NAME_SEARCH_STRING, WidgetId.XTextWidget);
      wb.andWidget(NAME_REPLACE_STRING, WidgetId.XTextWidget);
      wb.andWidget(RETAIN_TEAM_LEADS_MEMBERS, WidgetId.XCheckBoxWidget).andLabelAfter().andHorizLabel();
      wb.andWidget(PERSIST_CHANGES, WidgetId.XCheckBoxWidget).andLabelAfter().andHorizLabel();
      return wb.getXWidgetDatas();
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
         if (!ArtifactCache.getDirtyArtifacts().isEmpty()) {
            AWorkbench.popup(
               "Dirty artifacts found in cache, save all artifacts before running this operation (may need to restart)");
            return;
         }
         ConfigData data = new ConfigData();
         data.setSearchStr((String) variableMap.getValue(NAME_SEARCH_STRING));
         data.setReplaceStr((String) variableMap.getValue(NAME_REPLACE_STRING));
         data.setTeamDef(getSelectedTeamDefinition());
         data.setActionableItem(getSelectedActionableItem());
         data.setPersistChanges(variableMap.getBoolean(PERSIST_CHANGES));
         data.setRetainTeamLeads(variableMap.getBoolean(RETAIN_TEAM_LEADS_MEMBERS));

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
   public void widgetCreated(XWidget xWidget, FormToolkit toolkit, Artifact art, XWidgetSwtRenderer swtXWidgetRenderer,
      XModifiedListener modListener, boolean isEditable) {
      super.widgetCreated(xWidget, toolkit, art, swtXWidgetRenderer, modListener, isEditable);
      if (xWidget.getLabel().equals("Top Team Definition to Copy From (most like new config)")) {
         xTeamDefinitionCombo = (XTeamDefinitionComboWidget) xWidget;
      }
      if (xWidget.getLabel().equals("Top Actionable Item to Copy From")) {
         xActionableItemCombo = (XActionableItemComboWidget) xWidget;
      }
   }

   @Override
   public Collection<XNavItemCat> getCategories() {
      return Arrays.asList(AtsNavigateViewItems.ATS_ADMIN, XNavItemCat.OSEE_ADMIN);
   }

}
