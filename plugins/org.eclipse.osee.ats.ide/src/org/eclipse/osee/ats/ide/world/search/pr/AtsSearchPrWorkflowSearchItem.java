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
import org.eclipse.osee.ats.ide.search.widget.TeamDefinitionSearchWidget;
import org.eclipse.osee.ats.ide.util.widgets.XHyperlabelTeamDefinitionSelection;
import org.eclipse.osee.ats.ide.util.widgets.XHyperlinkApplicabilityWidget;
import org.eclipse.osee.ats.ide.world.search.AbstractWorkItemSearchItem;
import org.eclipse.osee.ats.ide.world.search.AtsSearchTeamWorkflowSearchItem;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.widgets.XModifiedListener;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.util.SwtXWidgetRenderer;
import org.eclipse.ui.forms.widgets.FormToolkit;

/**
 * @author Donald G. Dunne
 */
public class AtsSearchPrWorkflowSearchItem extends AtsSearchTeamWorkflowSearchItem {

   private static final AtsImage IMAGE = AtsImage.PROBLEM_REPORT;
   private static final String TITLE = "Problem Report (PR) Search";
   public static final String PR_NAMESPACE = AtsSearchUtil.ATS_QUERY_PR_WF_NAMESPACE;
   private ApplicabilitySearchWidget applic;

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

   public ApplicabilitySearchWidget getApplic() {
      if (applic == null) {
         applic = new ApplicabilitySearchWidget(this);
      }
      return applic;
   }

   public String get() {
      XHyperlinkApplicabilityWidget applicWidget = getApplic().getWidget();
      if (applicWidget != null && applicWidget.getToken().isValid()) {
         return applicWidget.getToken().getIdString();
      }
      return "";
   }

   @Override
   protected void addWidgets() {
      super.addWidgets();
      getApplic().addWidget();
   }

   @Override
   public void widgetCreated(XWidget widget, FormToolkit toolkit, Artifact art, SwtXWidgetRenderer dynamicXWidgetLayout,
      XModifiedListener modListener, boolean isEditable) {
      super.widgetCreated(widget, toolkit, art, dynamicXWidgetLayout, modListener, isEditable);
      if (widget.getLabel().equals(APPLICABILITY)) {
         getApplic().setupTeamDef(getTeamDef().getWidget());
         getApplic().setup(widget);
      }
      if (widget.getLabel().equals(TeamDefinitionSearchWidget.TEAM_DEFINITIONS)) {
         List<TeamDefinition> prTeamDefs = new ArrayList<>();
         for (TeamDefinition teamDef : AtsApiService.get().getConfigService().getConfigurations().getIdToTeamDef().values()) {
            if (teamDef.isActive() && teamDef.isWorkType(WorkType.ProblemReport)) {
               prTeamDefs.add(teamDef);
            }
         }
         ((XHyperlabelTeamDefinitionSelection) widget).setTeamDefs(prTeamDefs);
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

}
