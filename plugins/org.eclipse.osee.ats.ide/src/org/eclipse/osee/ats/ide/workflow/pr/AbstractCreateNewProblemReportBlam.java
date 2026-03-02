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

import static org.eclipse.osee.ats.api.data.AtsRelationTypes.TeamWorkflowToFoundInVersion_Version;
import static org.eclipse.osee.ats.api.data.AtsRelationTypes.TeamWorkflowToIntroducedInVersion_Version;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.osee.ats.api.ai.IAtsActionableItem;
import org.eclipse.osee.ats.api.data.AtsArtifactToken;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.team.ChangeTypes;
import org.eclipse.osee.ats.api.util.AtsImage;
import org.eclipse.osee.ats.api.util.WidgetIdAts;
import org.eclipse.osee.ats.api.version.IAtsVersion;
import org.eclipse.osee.ats.api.version.Version;
import org.eclipse.osee.ats.api.workflow.NewActionData;
import org.eclipse.osee.ats.core.util.AtsObjects;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.ats.ide.util.widgets.XHyperlinkWfdForProgramAiWidget;
import org.eclipse.osee.ats.ide.util.widgets.xx.XXFoundInVersionWidget;
import org.eclipse.osee.ats.ide.util.widgets.xx.XXIntroducedInVersionWidget;
import org.eclipse.osee.ats.ide.util.widgets.xx.XXPriorityWidget;
import org.eclipse.osee.ats.ide.workflow.cr.AbstractCreateNewChangeRequestBlam;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.widget.XWidgetData;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.widgets.XHyperlinkLabelDateWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.XModifiedListener;
import org.eclipse.osee.framework.ui.skynet.widgets.XTextWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.util.XWidgetSwtRenderer;
import org.eclipse.osee.framework.ui.swt.ImageManager;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.forms.widgets.FormToolkit;

/**
 * @author Donald G. Dunne
 */
public abstract class AbstractCreateNewProblemReportBlam extends AbstractCreateNewChangeRequestBlam {

   private XXFoundInVersionWidget foundInWidget;
   private XXIntroducedInVersionWidget introducedInWidget;
   private XTextWidget shipText;
   private XTextWidget testNumText;
   private XTextWidget flightNumText;
   private XHyperlinkLabelDateWidget testDate;

   public AbstractCreateNewProblemReportBlam(String name) {
      super(name);
   }

   @Override
   public List<XWidgetData> getXWidgetItems() {
      wb = createWidgetBuilder();

      // @formatter:off

      wb.andWidget(PROGRAM, XHyperlinkWfdForProgramAiWidget.class.getSimpleName()).andValueProvider( this).andRequired().endWidget();

      wb.andXText(TITLE).andRequired().endWidget();
      wba.andChangeType();
      wb.andXText(AtsAttributeTypes.Description).andHeight(80).andRequired().endWidget();

      wb.andWidget(AtsAttributeTypes.HowFound, "XText").andHeight(80).endWidget();

      // 4 columns
      wb.andWidget(WidgetIdAts.XXFoundInVersionWidget).andComposite(4).andRequired().endWidget();
      wb.andWidget(WidgetIdAts.XXIntroducedInVersionWidget).endComposite().endWidget();

      wba.andCogPriority().andEnumeratedArt(AtsArtifactToken.CogPriorityConfigArt).andRequired().andComposite(4).endWidget();
      wba.andPriority().endComposite().endWidget();

      wb.andXLabel("Flight Test Data");

      // 6 columns
      wb.andXText(AtsAttributeTypes.Ship).andComposite(6, true).endWidget();
      wb.andXText(AtsAttributeTypes.TestNumber).endWidget();
      wb.andXText(AtsAttributeTypes.FlightNumber).endWidget();

      wb.andXHyperLinkDate(AtsAttributeTypes.TestDate.getUnqualifiedName()).endWidget();

      wb.andXHyperlinkTriStateBoolean(AtsAttributeTypes.CrashOrBlankDisplay.getUnqualifiedName()).endWidget();

      // @formatter:on

      return wb.getXWidgetDatas();
   }

   @Override
   protected String getDebugTitle() {
      return "New PR " + atsApi.getRandomNum();
   }

   @Override
   public void widgetCreating(XWidget xWidget, FormToolkit toolkit, Artifact art, XWidgetSwtRenderer swtXWidgetRenderer,
      XModifiedListener xModListener, boolean isEditable) {
      if (xWidget.getLabel().equals("Version")) {
         xWidget.setLabel("Targeted Version");
      }
   }

   @Override
   public void widgetCreated(XWidget xWidget, FormToolkit toolkit, Artifact art, XWidgetSwtRenderer swtXWidgetRenderer,
      XModifiedListener xModListener, boolean isEditable) {
      super.widgetCreated(xWidget, toolkit, art, swtXWidgetRenderer, xModListener, isEditable);
      if (xWidget.getLabel().equals(AtsAttributeTypes.Priority.getUnqualifiedName())) {
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
      } else if (xWidget.getLabel().equals(AtsAttributeTypes.Ship.getUnqualifiedName())) {
         shipText = (XTextWidget) xWidget;
      } else if (xWidget.getLabel().equals(AtsAttributeTypes.TestNumber.getUnqualifiedName())) {
         testNumText = (XTextWidget) xWidget;
      } else if (xWidget.getLabel().equals(AtsAttributeTypes.FlightNumber.getUnqualifiedName())) {
         flightNumText = (XTextWidget) xWidget;
      } else if (xWidget.getLabel().equals(AtsAttributeTypes.TestDate.getUnqualifiedName())) {
         testDate = (XHyperlinkLabelDateWidget) xWidget;
      }
   }

   @Override
   public void handlePopulateWithDebugInfo() {
      try {
         super.handlePopulateWithDebugInfo();
         if (priorityWidget != null) {
            priorityWidget.setSelected("3");
         }
         changeTypeWidget.setSelected(ChangeTypes.Problem);
         Collection<IAtsVersion> versions = getSelectedProgramVersions();
         Version ver = atsApi.getVersionService().getVersionById(versions.iterator().next());
         foundInWidget.setSelected(ver.getArtifactToken());
         introducedInWidget.setSelected(ver.getArtifactToken());
         shipText.set("Ship88");
         testNumText.set("Test 43543");
         flightNumText.set("FLT 89");
         testDate.setDateValue(new Date());
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

   }

   @Override
   public void createActionData(NewActionData data) {
      super.createActionData(data);
      data.andRelation(TeamWorkflowToFoundInVersion_Version, foundInWidget.getSelectedFirst()) //
         .andRelation(TeamWorkflowToIntroducedInVersion_Version, introducedInWidget.getSelectedFirst()) //
         .andAttr(AtsAttributeTypes.Ship, shipText.get()) //
         .andAttr(AtsAttributeTypes.TestNumber, testNumText.get()) //
         .andAttr(AtsAttributeTypes.TestDate, testDate.getDateValue());
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
   public String getDescriptionUsage() {
      return "Create program top level Problem Report for any issue found.\n" //
         + "This will mature into all the work for all teams needed to resolve this request.";
   }

   @Override
   public Image getImage() {
      return ImageManager.getImage(AtsImage.PROBLEM_REPORT);
   }

   @Override
   public ImageDescriptor getImageDescriptor() {
      return ImageManager.getImageDescriptor(AtsImage.PROBLEM_REPORT);
   }

   @Override
   public Collection<Object> getSelectable(Object widget) {
      if (widget instanceof XHyperlinkWfdForProgramAiWidget) {
         return org.eclipse.osee.framework.jdk.core.util.Collections.castAll(getProgramCrAis());
      }
      return Collections.emptyList();
   }

   @Override
   public Collection<IAtsActionableItem> getProgramCrAis() {
      return null;
   }

}
