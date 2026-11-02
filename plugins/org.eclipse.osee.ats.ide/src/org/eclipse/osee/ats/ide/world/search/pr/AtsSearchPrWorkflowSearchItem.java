/*******************************************************************************
 * Copyright (c) 2025 Boeing.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/

package org.eclipse.osee.ats.ide.world.search.pr;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import org.eclipse.osee.ats.api.config.WorkType;
import org.eclipse.osee.ats.api.query.AtsSearchData;
import org.eclipse.osee.ats.api.query.AtsSearchUtil;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.api.util.AtsImage;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.ats.ide.search.widget.BuildImpact2SearchWidget;
import org.eclipse.osee.ats.ide.search.widget.BuildImpactSearchWidget;
import org.eclipse.osee.ats.ide.search.widget.BuildImpactState2SearchWidget;
import org.eclipse.osee.ats.ide.search.widget.BuildImpactStateSearchWidget;
import org.eclipse.osee.ats.ide.search.widget.ConfigurationSearchWidget;
import org.eclipse.osee.ats.ide.search.widget.PrListSearchWidget;
import org.eclipse.osee.ats.ide.search.widget.TeamDefinitionSearchWidget;
import org.eclipse.osee.ats.ide.search.widget.button.BuildMemoType;
import org.eclipse.osee.ats.ide.search.widget.button.ExportBuildMemoWidget;
import org.eclipse.osee.ats.ide.search.widget.button.GenerateBuildMemoWidget;
import org.eclipse.osee.ats.ide.search.widget.button.ValidateProblemReportsWidget;
import org.eclipse.osee.ats.ide.util.widgets.xx.XXTeamDefinitionsWidget;
import org.eclipse.osee.ats.ide.workdef.XWidgetBuilderAts;
import org.eclipse.osee.ats.ide.world.SearchEngine;
import org.eclipse.osee.ats.ide.world.search.AbstractWorkItemSearchItem;
import org.eclipse.osee.ats.ide.world.search.AtsSearchTeamWorkflowSearchItem;
import org.eclipse.osee.framework.core.data.BranchViewToken;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.util.AHTML;
import org.eclipse.osee.framework.jdk.core.util.DateUtil;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.util.XWidgetSwtRenderer;
import org.eclipse.osee.framework.ui.skynet.widgets.xx.XXLabelWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.xx.util.listen.XModifiedListener;
import org.eclipse.osee.framework.ui.swt.Widgets;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.ui.forms.widgets.FormToolkit;

/**
 * @author Donald G. Dunne
 */
public class AtsSearchPrWorkflowSearchItem extends AtsSearchTeamWorkflowSearchItem {

   private static final String OR_LABEL = "OR: ";
   public static final AtsImage IMAGE = AtsImage.PROBLEM_REPORT;
   private static final String TITLE = "Problem Report (PR) Search";
   public static final String BUILD_MEMO = "Problem Report - Build Memo";
   public static final String PR_NAMESPACE = AtsSearchUtil.ATS_QUERY_PR_WF_NAMESPACE;
   private ConfigurationSearchWidget config;
   private BuildImpactSearchWidget buildImpact;
   private BuildImpactStateSearchWidget buildImpactState;
   private BuildImpact2SearchWidget buildImpact2;
   private BuildImpactState2SearchWidget buildImpactState2;
   protected GenerateBuildMemoWidget generateBuildMemoWidget;
   private ValidateProblemReportsWidget validatePrsWidget;
   protected ExportBuildMemoWidget exportBuildMemoWidgetFull;
   protected ExportBuildMemoWidget exportBuildMemoWidgetRelease;
   private PrListSearchWidget prList;

   public AtsSearchPrWorkflowSearchItem() {
      super(TITLE, PR_NAMESPACE, IMAGE);
   }

   public AtsSearchPrWorkflowSearchItem(AbstractWorkItemSearchItem searchItem) {
      super(searchItem, TITLE, PR_NAMESPACE, IMAGE);
   }

   public AtsSearchPrWorkflowSearchItem(AbstractWorkItemSearchItem searchItem, String title, String namespace, AtsImage image) {
      super(searchItem, title, namespace, image);
   }

   public AtsSearchPrWorkflowSearchItem(String title, String namespace, AtsImage image) {
      super(title, namespace, image);
   }

   public String getBuildMemoName() {
      return BUILD_MEMO;
   }

   public GenerateBuildMemoWidget getGenerateBuildMemo() {
      if (generateBuildMemoWidget == null) {
         generateBuildMemoWidget = new GenerateBuildMemoWidget(this, getBuildMemoName());
      }
      return generateBuildMemoWidget;
   }

   public ExportBuildMemoWidget getExportBuildMemoFull() {
      if (exportBuildMemoWidgetFull == null) {
         exportBuildMemoWidgetFull = new ExportBuildMemoWidget(this, getBuildMemoName(), BuildMemoType.Full);
      }
      return exportBuildMemoWidgetFull;
   }

   public ExportBuildMemoWidget getExportBuildMemoRelease() {
      if (exportBuildMemoWidgetRelease == null) {
         exportBuildMemoWidgetRelease = new ExportBuildMemoWidget(this, getBuildMemoName(), BuildMemoType.Release);
      }
      return exportBuildMemoWidgetRelease;
   }

   public ValidateProblemReportsWidget getValidatePrs() {
      if (validatePrsWidget == null) {
         validatePrsWidget = new ValidateProblemReportsWidget(this, getBuildMemoName());
      }
      return validatePrsWidget;
   }

   public ConfigurationSearchWidget getConfig() {
      if (config == null) {
         config = new ConfigurationSearchWidget(this);
      }
      return config;
   }

   public BuildImpactSearchWidget getBuildImpact() {
      if (buildImpact == null) {
         buildImpact = new BuildImpactSearchWidget(this);
      }
      return buildImpact;
   }

   public BuildImpactStateSearchWidget getBuildImpactState() {
      if (buildImpactState == null) {
         buildImpactState = new BuildImpactStateSearchWidget(this);
      }
      return buildImpactState;
   }

   public BuildImpact2SearchWidget getBuildImpact2() {
      if (buildImpact2 == null) {
         buildImpact2 = new BuildImpact2SearchWidget(this);
      }
      return buildImpact2;
   }

   public BuildImpactState2SearchWidget getBuildImpactState2() {
      if (buildImpactState2 == null) {
         buildImpactState2 = new BuildImpactState2SearchWidget(this);
      }
      return buildImpactState2;
   }

   public PrListSearchWidget getPrList() {
      if (prList == null) {
         prList = new PrListSearchWidget(this);
      }
      return prList;
   }

   @Override
   protected void addWidgets(XWidgetBuilderAts wba) {
      super.addWidgets(wba);
      addPrWidgets();
   }

   protected void addPrWidgets() {
      getConfig().addWidget(wba, 3);
      getBuildImpact().addWidget(wba);
      getBuildImpactState().addWidget(wba, -1);
      //
      addSpaceWidget(this, OR_LABEL, 4);
      getBuildImpact2().addWidget(wba);
      getBuildImpactState2().addWidget(wba, -1);
      //
      addSpaceWidget(this, "  ", 6);
      getValidatePrs().addWidget(wba);
      getPrList().addWidget(wba);
      getGenerateBuildMemo().addWidget(wba);
      getExportBuildMemoFull().addWidget(wba);
      getExportBuildMemoRelease().addWidget(wba, -1);
   }

   @Override
   protected void reportWidgetSelections(XResultData rd) {
      super.reportWidgetSelections(rd);
      BranchViewToken configTok = config.getSelectedFirst();
      String configValue = configTok.getName();
      rd.logf("Configuration: [%s]\n", (Strings.isValid(configValue) ? configValue : ""));
      String buildImpact = getBuildImpact().getSelectedFirst();
      rd.logf("Build Impact: [%s]\n", (Strings.isValid(buildImpact) ? buildImpact : ""));
      String buildImpactState = getBuildImpactState().getSelectedFirst();
      rd.logf("Build Impact State: [%s]\n", (Strings.isValid(buildImpactState) ? buildImpactState : ""));
      rd.log("OR");
      String buildImpact2 = getBuildImpact2().getSelectedFirst();
      rd.logf("Build Impact 2: [%s]\n", (Strings.isValid(buildImpact2) ? buildImpact2 : ""));
      String buildImpactState2 = getBuildImpactState2().getSelectedFirst();
      rd.logf("Build Impact State 2: [%s]\n", (Strings.isValid(buildImpactState2) ? buildImpactState2 : ""));
      String prListArtName = prList.getCurrentValue();
      rd.logf("PR List Artifact: [%s]\n", (prListArtName != null ? prListArtName : ""));
   }

   @Override
   public void widgetCreating(XWidget widget, FormToolkit toolkit, Artifact art,
      XWidgetSwtRenderer dynamicXWidgetLayout, XModifiedListener xModListener, boolean isEditable) {
      super.widgetCreating(widget, toolkit, art, dynamicXWidgetLayout, xModListener, isEditable);
      if (widget.getLabel().equals(OR_LABEL)) {
         XXLabelWidget label = (XXLabelWidget) widget;
         label.setFillHorizontally(false);
      }
   }

   @Override
   public void widgetCreated(XWidget widget, FormToolkit toolkit, Artifact art, XWidgetSwtRenderer xWidgetSwtRenderer,
      XModifiedListener modListener, boolean isEditable) {
      super.widgetCreated(widget, toolkit, art, xWidgetSwtRenderer, modListener, isEditable);

      generateBuildMemoWidget.widgetCreated(getWorldEditor(), widget, toolkit, art, xWidgetSwtRenderer, modListener,
         isEditable);
      exportBuildMemoWidgetFull.widgetCreated(getWorldEditor(), widget, toolkit, art, xWidgetSwtRenderer, modListener,
         isEditable);
      exportBuildMemoWidgetRelease.widgetCreated(getWorldEditor(), widget, toolkit, art, xWidgetSwtRenderer,
         modListener, isEditable);
      validatePrsWidget.widgetCreated(getWorldEditor(), widget, toolkit, art, xWidgetSwtRenderer, modListener,
         isEditable);

      if (widget.getLabel().equals(TeamDefinitionSearchWidget.TeamDefintiionWidget.getName())) {
         List<IAtsTeamDefinition> prTeamDefs = new ArrayList<>();
         for (IAtsTeamDefinition teamDef : AtsApiService.get().getConfigService().getConfigurations().getIdToTeamDef().values()) {
            if (teamDef.isActive() && teamDef.isWorkType(WorkType.ProblemReport)) {
               prTeamDefs.add(teamDef);
            }
         }
         XXTeamDefinitionsWidget teamDefWidget = (XXTeamDefinitionsWidget) widget;
         teamDefWidget.setSelectable(prTeamDefs);
         final IAtsTeamDefinition firstTeamDef = prTeamDefs.size() > 0 ? prTeamDefs.iterator().next() : null;
         teamDefWidget.getLabelLinkWidget().addMouseListener(new MouseAdapter() {

            @Override
            public void mouseUp(MouseEvent e) {
               if (e.button == 3) {
                  teamDefWidget.setSelected(Arrays.asList(firstTeamDef));
               }
            }

         });
      }
   }

   @Override
   public AtsSearchPrWorkflowSearchItem copy() {
      AtsSearchPrWorkflowSearchItem item = new AtsSearchPrWorkflowSearchItem(this);
      item.setSavedData(savedData);
      return item;
   }

   @Override
   public AtsSearchPrWorkflowSearchItem copyProvider() {
      AtsSearchPrWorkflowSearchItem item = new AtsSearchPrWorkflowSearchItem(this);
      item.setSavedData(savedData);
      return item;
   }

   @Override
   public AtsSearchData loadSearchData(AtsSearchData data) {
      super.loadSearchData(data);
      data.setBuildImpact(getBuildImpact().getSelectedFirst());
      data.setBuildImpactState(getBuildImpactState().getSelectedFirst());
      data.setBuildImpact2(getBuildImpact2().getWidget().getSelectedFirst());
      data.setBuildImpactState2(getBuildImpactState2().getSelectedFirst());
      data.setConfiguration(getConfig().getWidget().getSelectedFirst());
      data.setPreviousPrListId(getPrList().getWidget().getSelectedFirst().getId());
      return data;
   }

   @Override
   public void loadWidgets(AtsSearchData data) {
      try {
         super.loadWidgets(data);
         getConfig().set(data);
         getBuildImpact().set(data);
         getBuildImpactState().set(data);
         //
         getBuildImpact2().set(data);
         getBuildImpactState2().set(data);
         //
         getPrList().set(data);
      } catch (Exception ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
   }

   @Override
   public String getShortNamePrefix() {
      return "PRWS";
   }

   @Override
   public XResultData getWorldEditorHtmlReportRd() {
      XResultData rd = new XResultData();
      rd.logf("%s\n\n", getBuildMemoName());
      rd.logf("Created: %s ", DateUtil.getDateNow(DateUtil.MMDDYYHHMM) + "\n\n");
      reportWidgetSelections(rd);
      reportAdditionalCriteria(rd);
      rd.logf("\nLoaded Workflow(s): %s \n", getWorldEditor().getWorldComposite().getLoadedArtifacts().size());
      return rd;
   }

   @Override
   public void reportAdditionalCriteria(XResultData rd) {
      // for subclass
   }

   @Override
   public String getWorldEditorHtmlReport() {
      XResultData rd = getWorldEditorHtmlReportRd();
      return AHTML.textToHtml(rd.toString());
   }

   @Override
   public Result isParameterSelectionValid() {
      Result result = super.isParameterSelectionValid();
      if (buildImpact != null) {
         String buildImpactName = buildImpact.getWidget().getSelectedFirst();
         if (Widgets.isInValidSelection(buildImpactName) && buildImpactState != null) {
            String buildImpactStateName = buildImpactState.getSelectedFirst();
            if (Widgets.isValidSelection(buildImpactStateName)) {
               result.errorf("Build Impact State [%s] can not be specified without Build Impact selection\n",
                  buildImpactStateName);
            }
         }
      }
      if (buildImpact2 != null) {
         String buildImpactName2 = buildImpact2.getWidget().getSelectedFirst();
         if (Widgets.isInValidSelection(buildImpactName2) && buildImpactState2 != null) {
            String buildImpactStateName2 = buildImpactState2.getSelectedFirst();
            if (Widgets.isValidSelection(buildImpactStateName2)) {
               result.errorf("Build Impact State 2 [%s] can not be specified without Build Impact 2 selection\n",
                  buildImpactStateName2);
            }
         }
      }
      return result;
   }

   @Override
   public Collection<? extends SearchEngine> getSearchEngines() {
      List<SearchEngine> srchEngines = new ArrayList<>();
      srchEngines.add(SearchEngine.AsArtifacts);
      return srchEngines;
   }

}
