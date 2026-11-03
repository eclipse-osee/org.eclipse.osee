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
import org.eclipse.osee.ats.api.util.WidgetIdAts;
import org.eclipse.osee.ats.ide.blam.AbstractAtsBlam;
import org.eclipse.osee.ats.ide.navigate.AtsNavigateViewItems;
import org.eclipse.osee.ats.ide.util.widgets.xx.XXActionableItemWidget;
import org.eclipse.osee.ats.ide.util.widgets.xx.XXTeamDefinitionsWidget;
import org.eclipse.osee.framework.core.util.CoreImage;
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
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.util.XWidgetSwtRenderer;
import org.eclipse.osee.framework.ui.skynet.widgets.xx.util.listen.XModifiedListener;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.osgi.service.component.annotations.Component;
import org.eclipse.osee.framework.core.data.IUserGroupArtifactToken;
import org.eclipse.osee.ats.api.data.AtsUserGroups;

/**
 * @author Donald G. Dunne
 */
@Component(service = AbstractBlam.class, immediate = true)
public class CopyAtsConfigurationBlam extends AbstractAtsBlam {

   private static final String BLAM_NAME = "Copy ATS Configuration";
   private static final String PERSIST_CHANGES = "Persist Changes";
   private static final String RETAIN_TEAM_LEADS_MEMBERS = "Retain Team Leads/Members";
   private static final String NAME_REPLACE_STRING = "Name Replace String";
   private static final String NAME_SEARCH_STRING = "Name Search String";

   private XXTeamDefinitionsWidget teamDefWidget;
   private XXActionableItemWidget aiWidget;

   //////////////////////////////////////////
   // Constructors
   //////////////////////////////////////////

   public CopyAtsConfigurationBlam() {
      super(BLAM_NAME, CoreImage.GEAR);
   }

   //////////////////////////////////////////
   // Widgets
   //////////////////////////////////////////

   @Override
   public List<XWidgetData> getXWidgetItems() {
      createWidgetBuilder();
      wba.andTeamDefinitionWidget().andValueSingleSelect();
      wba.andActionableItemWidget().andValueSingleSelect();
      wb.andTextWidget(NAME_SEARCH_STRING);
      wb.andTextWidget(NAME_REPLACE_STRING);
      wb.andCheckBoxWidget(RETAIN_TEAM_LEADS_MEMBERS);
      wb.andCheckBoxWidget(PERSIST_CHANGES);
      return wb.getXWidgetDatas();
   }

   @Override
   public void widgetCreated(XWidget xWidget, FormToolkit toolkit, Artifact art, XWidgetSwtRenderer xWidgetSwtRenderer,
      XModifiedListener modListener, boolean isEditable) {
      super.widgetCreated(xWidget, toolkit, art, xWidgetSwtRenderer, modListener, isEditable);
      if (xWidget.isWidget(WidgetIdAts.XXTeamDefinitionsWidget)) {
         teamDefWidget = (XXTeamDefinitionsWidget) xWidget;
         teamDefWidget.addXModifiedListener(new XModifiedListener() {

            @Override
            public void widgetModified(XWidget widget) {
               aiWidget.setTeamDef(teamDefWidget.getSelectedFirst());
            }
         });
      }
      if (xWidget.isWidget(WidgetIdAts.XXActionableItemWidget)) {
         aiWidget = (XXActionableItemWidget) xWidget;
      }

   }

   //////////////////////////////////////////
   // Operation
   //////////////////////////////////////////

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

   //////////////////////////////////////////
   // Helpers
   //////////////////////////////////////////

   private IAtsTeamDefinition getSelectedTeamDefinition() {
      return teamDefWidget.getSelectedFirst();
   }

   private IAtsActionableItem getSelectedActionableItem() {
      XXActionableItemWidget widget = (XXActionableItemWidget) getXWidget(WidgetIdAts.XXActionableItemWidget);
      return widget.getSelectedFirst();
   }

   @Override
   public Collection<IUserGroupArtifactToken> getUserGroups() {
      return Arrays.asList(AtsUserGroups.AtsAdmin);
   }

   //////////////////////////////////////////
   // Categories & Description
   //////////////////////////////////////////

   @Override
   public Collection<XNavItemCat> getCategories() {
      return Arrays.asList(AtsNavigateViewItems.ATS_ADMIN);
   }

   @Override
   public String getDescriptionUsage() {
      return "This BLAM will use existing configuration of a top Team Definition to create a new configuration..\n" +
      //
         "This includes making team defs, actionable items, setting all team leads/team members and changing name using search string and replace string.";
   }

}
