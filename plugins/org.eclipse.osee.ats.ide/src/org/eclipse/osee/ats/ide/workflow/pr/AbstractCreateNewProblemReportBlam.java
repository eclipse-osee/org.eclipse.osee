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

package org.eclipse.osee.ats.ide.workflow.pr;

import static org.eclipse.osee.ats.api.data.AtsAttributeTypes.*;
import static org.eclipse.osee.ats.api.data.AtsRelationTypes.TeamWorkflowToFoundInVersion_Version;
import static org.eclipse.osee.ats.api.data.AtsRelationTypes.TeamWorkflowToIntroducedInVersion_Version;
import static org.eclipse.osee.ats.api.util.WidgetIdAts.XXFoundInVersionWidget;
import static org.eclipse.osee.ats.api.util.WidgetIdAts.XXIntroducedInVersionWidget;
import static org.eclipse.osee.ats.api.util.WidgetIdAts.XXProgramAiWidget;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import org.eclipse.osee.ats.api.ai.IAtsActionableItem;
import org.eclipse.osee.ats.api.data.AtsArtifactToken;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.team.ChangeTypes;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.api.version.IAtsVersion;
import org.eclipse.osee.ats.api.version.Version;
import org.eclipse.osee.ats.api.workflow.NewActionData;
import org.eclipse.osee.ats.core.util.AtsObjects;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.ats.ide.util.widgets.xx.XXPriorityWidget;
import org.eclipse.osee.ats.ide.util.widgets.xx.XXProgramAiWidget;
import org.eclipse.osee.ats.ide.util.widgets.xx.rel.XXFoundInVersionWidget;
import org.eclipse.osee.ats.ide.util.widgets.xx.rel.XXIntroducedInVersionWidget;
import org.eclipse.osee.ats.ide.workflow.cr.AbstractCreateNewChangeRequestBlam;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.OseeImage;
import org.eclipse.osee.framework.core.enums.QueryOption;
import org.eclipse.osee.framework.core.widget.XWidgetData;
import org.eclipse.osee.framework.core.widget.XWidgetLabels;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.utility.OrcsQueryService;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.util.XWidgetSwtRenderer;
import org.eclipse.osee.framework.ui.skynet.widgets.xx.XXDateSelWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.xx.XXTextWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.xx.util.listen.XModifiedListener;
import org.eclipse.osee.orcs.search.QueryBuilder;
import org.eclipse.ui.forms.widgets.FormToolkit;

/**
 * @author Donald G. Dunne
 */
public abstract class AbstractCreateNewProblemReportBlam extends AbstractCreateNewChangeRequestBlam {

   private XXFoundInVersionWidget foundInWidget;
   private XXIntroducedInVersionWidget introducedInWidget;
   private XXTextWidget shipText;
   private XXTextWidget testNumText;
   private XXTextWidget flightNumText;
   private XXDateSelWidget testDate;

   //////////////////////////////////////////
   // Constructors
   //////////////////////////////////////////

   public AbstractCreateNewProblemReportBlam(String name, OseeImage image) {
      super(name, image);
   }

   //////////////////////////////////////////
   // Widgets
   //////////////////////////////////////////

   @Override
   public List<XWidgetData> getXWidgetItems() {
      wb = createWidgetBuilder();

      // @formatter:off

      wb.andWidget(PROGRAM, XXProgramAiWidget).andValueProvider( this).andRequired();

      wb.andTextWidget(TITLE).andRequired();

      wba.andChangeTypeWidget();

      wb.andTextWidget(Description).andHeight(80).andRequired();
      wb.andTextWidget(HowFound).andHeight(80);

      // 4 columns
      wb.andWidget(XXFoundInVersionWidget).startComp(4).andRequired();
      wb.andWidget(XXIntroducedInVersionWidget);

      wba.andCogPriorityWidget().andEnumeratedArt(AtsArtifactToken.CogPriorityConfigArt).andRequired().startComp(4);
      wba.andPriorityWidget();

      wb.andLabelWidget("Flight Test Data");

      // 6 columns
      wb.andTextWidget(Ship).startComp(6, true);
      wb.andTextWidget(TestNumber);
      wb.andTextWidget(FlightNumber);

      wb.andDateWidget(TestDate.getUnqualifiedName());

      wb.andBooleanWidget(CrashOrBlankDisplay.getUnqualifiedName());

      // @formatter:on

      return wb.getXWidgetDatas();
   }

   @Override
   public void widgetCreated(XWidget xWidget, FormToolkit toolkit, Artifact art, XWidgetSwtRenderer xWidgetSwtRenderer,
      XModifiedListener xModListener, boolean isEditable) {
      super.widgetCreated(xWidget, toolkit, art, xWidgetSwtRenderer, xModListener, isEditable);
      if (xWidget.getLabel().equals(Priority.getUnqualifiedName())) {
         priorityWidget = (XXPriorityWidget) xWidget;
      } else if (xWidget instanceof XXFoundInVersionWidget) {
         foundInWidget = (XXFoundInVersionWidget) xWidget;

         // Add if already selected
         Collection<IAtsVersion> versions = getSelectedProgramVersions();
         foundInWidget.setSelectable(AtsObjects.toArtifactTokens(versions));

         // Add listener for selection
         programWidget.addXModifiedListener(new XModifiedListener() {

            @Override
            public void widgetModified(XWidget widget) {
               Collection<IAtsVersion> versions = getSelectedProgramVersions();
               foundInWidget.setSelectable(AtsObjects.toArtifactTokens(versions));
            }
         });
      } else if (xWidget instanceof XXIntroducedInVersionWidget) {
         introducedInWidget = (XXIntroducedInVersionWidget) xWidget;

         // Add if already selected
         Collection<IAtsVersion> versions = getSelectedProgramVersions();
         introducedInWidget.setSelectable(AtsObjects.toArtifactTokens(versions));
      } else if (xWidget.getLabel().equals(Ship.getUnqualifiedName())) {
         shipText = (XXTextWidget) xWidget;
      } else if (xWidget.getLabel().equals(TestNumber.getUnqualifiedName())) {
         testNumText = (XXTextWidget) xWidget;
      } else if (xWidget.getLabel().equals(FlightNumber.getUnqualifiedName())) {
         flightNumText = (XXTextWidget) xWidget;
      } else if (xWidget.getLabel().equals(TestDate.getUnqualifiedName())) {
         testDate = (XXDateSelWidget) xWidget;
      }
   }

   //////////////////////////////////////////
   // Helpers
   //////////////////////////////////////////

   @Override
   protected String getDebugTitle() {
      return "New PR " + atsApi.getRandomNum();
   }

   @Override
   public void widgetCreating(XWidget xWidget, FormToolkit toolkit, Artifact art, XWidgetSwtRenderer xWidgetSwtRenderer,
      XModifiedListener xModListener, boolean isEditable) {
      if (xWidget.getLabel().equals(XWidgetLabels.VERSION)) {
         xWidget.setLabel("Targeted Version");
      }
   }

   @Override
   public void handlePopulateWithDebugInfo() {
      try {
         super.handlePopulateWithDebugInfo();
         if (priorityWidget != null) {
            priorityWidget.setSelected("3");
         }
         changeTypeWidget.setSelected(ChangeTypes.Problem.name());
         Collection<IAtsVersion> versions = getSelectedProgramVersions();
         Version ver = atsApi.getVersionService().getVersionById(versions.iterator().next());
         foundInWidget.setSelected(ver.getArtifactToken());
         introducedInWidget.setSelected(ver.getArtifactToken());
         shipText.set("Ship88");
         testNumText.set("Test 43543");
         flightNumText.set("FLT 89");
         testDate.setDate(new Date());
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }
   }

   @Override
   protected void isValidEntry(XResultData rd) {
      super.isValidEntry(rd);

      ArtifactToken version = foundInWidget.getSelectedFirst();
      if (version == null) {
         rd.error("Must select Build");
      }

      // Check for duplicate Problem Report with same title
      String title = variableMap.getString(TITLE);
      if (Strings.isValid(title)) {
         IAtsTeamDefinition teamDef = getSelectedProgramTeamDefOrSentinel();
         if (teamDef.isValid()) {
            String artTypeName = atsApi.getAttributeResolver().getSoleAttributeValue(teamDef,
               AtsAttributeTypes.TeamWorkflowArtifactType, null);
            ArtifactTypeToken artType = AtsArtifactTypes.TeamWorkflow;
            if (Strings.isValid(artTypeName)) {
               for (ArtifactTypeToken type : atsApi.tokenService().getArtifactTypes()) {
                  if (type.getName().equals(artTypeName)) {
                     artType = type;
                     break;
                  }
               }
            }
            QueryBuilder restQuery = OrcsQueryService.fromBranch(atsApi.getAtsBranch()).and(CoreAttributeTypes.Name,
               title, QueryOption.EXACT_MATCH_OPTIONS) //
               .andIsOfType(artType);
            List<ArtifactId> ideRestSearch = atsApi.getServerEndpoints().getQueryEp().queryIds(restQuery);
            if (!ideRestSearch.isEmpty()) {
               String msg = String.format("Problem Report with title [%s] already exists\n\nDuplicate name ids %s",
                  title, ideRestSearch);
               rd.error(msg);
               AWorkbench.popup("Duplicate Problem Report", msg);
            }
         }
      }
   }

   @Override
   public void createActionData(NewActionData data) {
      super.createActionData(data);
      data.andRelation(TeamWorkflowToFoundInVersion_Version, foundInWidget.getSelectedFirst()) //
         .andRelation(TeamWorkflowToIntroducedInVersion_Version, introducedInWidget.getSelectedFirst()) //
         .andAttr(Ship, shipText.get()) //
         .andAttr(TestNumber, testNumText.get()) //
         .andAttr(TestDate, testDate.getSelectedFirst());
   }

   @Override
   public boolean isOverrideAccess() {
      return !AtsApiService.get().getStoreService().isProductionDb();
   }

   @Override
   public String getRunText() {
      return getName();
   }

   @Override
   public Collection<Object> getSelectable(Object widget) {
      if (widget instanceof XXProgramAiWidget) {
         return org.eclipse.osee.framework.jdk.core.util.Collections.castAll(getProgramCrAis());
      }
      return Collections.emptyList();
   }

   @Override
   public Collection<IAtsActionableItem> getProgramCrAis() {
      return null;
   }

   //////////////////////////////////////////
   // Categories & Description
   //////////////////////////////////////////

   @Override
   public String getDescriptionUsage() {
      return "Create program top level Problem Report for any issue found.\n" //
         + "This will mature into all the work for all teams needed to resolve this request.";
   }

}
