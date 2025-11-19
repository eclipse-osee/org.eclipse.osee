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

import static org.eclipse.osee.ats.ide.search.widget.ApplicabilitySearchWidget.APPLICABILITY;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import org.eclipse.osee.ats.api.config.TeamDefinition;
import org.eclipse.osee.ats.api.config.WorkType;
import org.eclipse.osee.ats.api.query.AtsSearchData;
import org.eclipse.osee.ats.api.query.AtsSearchUtil;
import org.eclipse.osee.ats.api.util.AtsImage;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.ats.ide.search.widget.ApplicabilitySearchWidget;
import org.eclipse.osee.ats.ide.search.widget.GenerateBuildMemoWidget;
import org.eclipse.osee.ats.ide.search.widget.TeamDefinitionSearchWidget;
import org.eclipse.osee.ats.ide.util.widgets.XHyperlabelTeamDefinitionSelection;
import org.eclipse.osee.ats.ide.world.search.AbstractWorkItemSearchItem;
import org.eclipse.osee.ats.ide.world.search.AtsSearchTeamWorkflowSearchItem;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.util.AHTML;
import org.eclipse.osee.framework.jdk.core.util.DateUtil;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.widgets.XModifiedListener;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.util.SwtXWidgetRenderer;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.ui.forms.widgets.FormToolkit;

/**
 * @author Donald G. Dunne
 */
public class AtsSearchPrWorkflowSearchItem extends AtsSearchTeamWorkflowSearchItem {

   public static final AtsImage IMAGE = AtsImage.PROBLEM_REPORT;
   private static final String TITLE = "Problem Report (PR) Search";
   public static final String BUILD_MEMO = "Problem Report - Build Memo";
   public static final String PR_NAMESPACE = AtsSearchUtil.ATS_QUERY_PR_WF_NAMESPACE;
   private ApplicabilitySearchWidget applic;
   protected GenerateBuildMemoWidget buildMemoWidget;

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

   public GenerateBuildMemoWidget getBuildMemo() {
      if (buildMemoWidget == null) {
         buildMemoWidget = new GenerateBuildMemoWidget(this, getBuildMemoName());
      }
      return buildMemoWidget;
   }

   public ApplicabilitySearchWidget getApplic() {
      if (applic == null) {
         applic = new ApplicabilitySearchWidget(this);
      }
      return applic;
   }

   @Override
   protected void addWidgets() {
      super.addWidgets();
      getApplic().addWidget(6);
      addSpaceWidget(this, "  ");
      getBuildMemo().addWidget();
   }

   @Override
   protected void reportWidgetSelections(XResultData rd) {
      super.reportWidgetSelections(rd);
      String applicValue = applic.getWidget().getCurrentValue();
      rd.logf("Applicability: [%s]\n", Strings.isValid(applicValue) ? "" : applicValue);
   }

   @Override
   public void widgetCreated(XWidget widget, FormToolkit toolkit, Artifact art, SwtXWidgetRenderer dynamicXWidgetLayout,
      XModifiedListener modListener, boolean isEditable) {
      super.widgetCreated(widget, toolkit, art, dynamicXWidgetLayout, modListener, isEditable);

      buildMemoWidget.widgetCreated(getWorldEditor(), widget, toolkit, art, dynamicXWidgetLayout, modListener,
         isEditable);

      if (widget.getLabel().equals(APPLICABILITY)) {
         getApplic().setupTeamDef(getTeamDef().getWidget());
         getApplic().setup(widget);
      } else if (widget.getLabel().equals(TeamDefinitionSearchWidget.TEAM_DEFINITIONS)) {
         List<TeamDefinition> prTeamDefs = new ArrayList<>();
         for (TeamDefinition teamDef : AtsApiService.get().getConfigService().getConfigurations().getIdToTeamDef().values()) {
            if (teamDef.isActive() && teamDef.isWorkType(WorkType.ProblemReport)) {
               prTeamDefs.add(teamDef);
            }
         }
         XHyperlabelTeamDefinitionSelection teamWidget = (XHyperlabelTeamDefinitionSelection) widget;
         teamWidget.setTeamDefs(prTeamDefs);
         final TeamDefinition firstTeamDef = prTeamDefs.size() > 0 ? prTeamDefs.iterator().next() : null;
         teamWidget.getLabelWidget().addMouseListener(new MouseAdapter() {

            @Override
            public void mouseUp(MouseEvent e) {
               if (e.button == 3) {
                  teamWidget.setSelectedTeamDefs(Arrays.asList(firstTeamDef));
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
      data.setApplicId(getApplic().getWidget().getToken().getId());
      return data;
   }

   @Override
   public void loadWidgets(AtsSearchData data) {
      try {
         super.loadWidgets(data);
         getApplic().set(data);
      } catch (Exception ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
   }

   @Override
   public String getShortNamePrefix() {
      return "PRWS";
   }

   @Override
   public String getWorldEditorHtmlReport() {
      XResultData rd = new XResultData();
      rd.logf("%s\n\n", getBuildMemoName());
      rd.logf("Created: %s ", DateUtil.getDateNow(DateUtil.MMDDYYHHMM) + "\n\n");
      reportWidgetSelections(rd);
      rd.logf("\nLoaded Workflow(s): %s ", getWorldEditor().getWorldComposite().getLoadedArtifacts().size());

      return AHTML.textToHtml(rd.toString());
   }

}
