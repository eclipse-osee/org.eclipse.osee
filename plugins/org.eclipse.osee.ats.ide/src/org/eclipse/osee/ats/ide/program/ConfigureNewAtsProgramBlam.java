/*********************************************************************
 * Copyright (c) 2026 Boeing
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
package org.eclipse.osee.ats.ide.program;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.ats.api.ai.IAtsActionableItem;
import org.eclipse.osee.ats.api.data.AtsUserGroups;
import org.eclipse.osee.ats.api.program.IAtsProgram;
import org.eclipse.osee.ats.api.util.AtsImage;
import org.eclipse.osee.ats.api.util.WidgetIdAts;
import org.eclipse.osee.ats.ide.blam.AbstractAtsBlam;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.ats.ide.util.widgets.xx.XXActionableItemWidget;
import org.eclipse.osee.framework.core.data.IUserGroupArtifactToken;
import org.eclipse.osee.framework.core.widget.XWidgetData;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.plugin.core.util.Jobs;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactCache;
import org.eclipse.osee.framework.skynet.core.demo.DemoUtilCore;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavItemCat;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItem;
import org.eclipse.osee.framework.ui.skynet.blam.AbstractBlam;
import org.eclipse.osee.framework.ui.skynet.blam.VariableMap;
import org.eclipse.osee.framework.ui.skynet.results.XResultDataUI;
import org.osgi.service.component.annotations.Component;

/**
 * @author Donald G. Dunne
 */
@Component(service = AbstractBlam.class, immediate = true)
public class ConfigureNewAtsProgramBlam extends AbstractAtsBlam {

   private static final String BLAM_NAME = "Create New ATS Program";
   protected static final String PROGRAM_TO_COPY_FROM_MOST_LIKE_NEW_PROGRAM =
      "Program to Copy From (most like new program)";
   protected static final String NEW_PROGRAM = "New Program";
   protected static final String PERSIST_CHANGES = "Persist Changes";
   protected static final String RETAIN_TEAM_LEADSMEMBERS = "Retain Team Leads/Members";
   protected static final String ONLY_COPY_FROM_COPYFROMTAG_AIS_AND_TEAMDEF =
      "Only copy from CopyFromTag AIs and TeamDef";

   //////////////////////////////////////////
   // Constructors
   //////////////////////////////////////////

   public ConfigureNewAtsProgramBlam() {
      super(BLAM_NAME, AtsImage.PROGRAM);
   }

   //////////////////////////////////////////
   // Widgets
   //////////////////////////////////////////

   @Override
   public List<XWidgetData> getXWidgetItems() {
      createWidgetBuilder();
      wba = createWidgetBuilder();
      wba.andWidget(PROGRAM_TO_COPY_FROM_MOST_LIKE_NEW_PROGRAM, WidgetIdAts.XXProgramWidget);
      wba.andActionableItemWidget("Top Actionable Item to Copy From").andValueSingleSelect();
      wba.andCheckBoxWidget(ONLY_COPY_FROM_COPYFROMTAG_AIS_AND_TEAMDEF);
      wba.andWidget(NEW_PROGRAM, WidgetIdAts.XXProgramWidget);
      wba.andCheckBoxWidget(RETAIN_TEAM_LEADSMEMBERS);
      wba.andCheckBoxWidget(PERSIST_CHANGES);
      return wba.getXWidgetDatas();
   }

   //////////////////////////////////////////
   // Operation
   //////////////////////////////////////////

   @Override
   public void runOperation(VariableMap variableMap, IProgressMonitor monitor) throws Exception {
      // Remove when demo operation test is implemented in Blam
      if (DemoUtilCore.isDemoDbBlamTest()) {
         return;
      }
      try {
         if (ArtifactCache.getDirtyArtifacts().size() > 0) {
            AWorkbench.popup(
               "Dirty artifacts found in cache, save all artifacts before running this operation (may need to restart)");
            return;
         }
         AtsConfigData data = new AtsConfigData();
         IAtsProgram fromProject = (IAtsProgram) variableMap.getValue(PROGRAM_TO_COPY_FROM_MOST_LIKE_NEW_PROGRAM);
         if (fromProject == null) {
            AWorkbench.popup("Must select Program to copy from");
            return;
         }
         IAtsProgram newProject = (IAtsProgram) variableMap.getValue(NEW_PROGRAM);
         if (newProject == null) {
            AWorkbench.popup("Must select New Program");
            return;
         }

         data.setFromProgram(fromProject);
         data.setNewProgram(newProject);
         data.setPersistChanges(variableMap.getBoolean(PERSIST_CHANGES));
         data.setRetainTeamLeads(variableMap.getBoolean(RETAIN_TEAM_LEADSMEMBERS));
         data.setActionableItem(getSelectedActionableItem());
         data.setSearchStr(fromProject.getName());
         data.setReplaceStr(newProject.getName());
         data.setTeamDef(AtsApiService.get().getTeamDefinitionService().getTeamDefHoldingVersions(fromProject));
         // Allows a Static Id tag to be set in db to choose what AIs and Team Defs to copy
         boolean copyFromTag = variableMap.getBoolean(ONLY_COPY_FROM_COPYFROMTAG_AIS_AND_TEAMDEF);
         data.setCopyFromTag(copyFromTag);

         XResultData resultData = new XResultData(false);
         new CopyCommonAtsValidation(data, resultData).validate();
         if (resultData.isErrors() || !data.isPersistChanges()) {
            resultData.log("Validation Complete");
            XResultDataUI.report(resultData, getName());
            return;
         }

         resultData = new XResultData(false);
         CopyCommonAtsConfigurationOperation operation = new CopyCommonAtsConfigurationOperation(data, resultData);
         Jobs.runInJob(operation, true);

      } finally {
         monitor.subTask("Done");
      }
   }

   //////////////////////////////////////////
   // Helpers
   //////////////////////////////////////////

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
      return Arrays.asList(XNavigateItem.DEFINE_ADMIN);
   }

}
