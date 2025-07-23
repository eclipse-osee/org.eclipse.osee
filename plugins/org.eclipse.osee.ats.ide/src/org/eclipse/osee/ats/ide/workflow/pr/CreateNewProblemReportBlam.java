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
import org.eclipse.osee.ats.api.version.IAtsVersion;
import org.eclipse.osee.ats.api.version.Version;
import org.eclipse.osee.ats.api.workflow.NewActionData;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.ats.ide.util.widgets.XHyperlabelFoundInVersionSelection;
import org.eclipse.osee.ats.ide.util.widgets.XHyperlabelIntroducedInVersionSelection;
import org.eclipse.osee.ats.ide.util.widgets.XHyperlinkPrioritySelection;
import org.eclipse.osee.ats.ide.util.widgets.XHyperlinkWfdForProgramAi;
import org.eclipse.osee.ats.ide.workflow.cr.CreateNewChangeRequestBlam;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.widgets.XHyperlinkLabelDate;
import org.eclipse.osee.framework.ui.skynet.widgets.XModifiedListener;
import org.eclipse.osee.framework.ui.skynet.widgets.XText;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.builder.XWidgetBuilder;
import org.eclipse.osee.framework.ui.skynet.widgets.util.SwtXWidgetRenderer;
import org.eclipse.osee.framework.ui.skynet.widgets.util.XWidgetRendererItem;
import org.eclipse.osee.framework.ui.swt.ImageManager;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.forms.widgets.FormToolkit;

/**
 * @author Donald G. Dunne
 */
public abstract class CreateNewProblemReportBlam extends CreateNewChangeRequestBlam {

   private XHyperlabelFoundInVersionSelection foundInWidget;
   private XHyperlabelIntroducedInVersionSelection introducedInWidget;
   private XText shipText;
   private XText testNumText;
   private XText flightNumText;
   private XHyperlinkLabelDate testDate;

   public CreateNewProblemReportBlam(String name) {
      super(name);
   }

   @Override
   public List<XWidgetRendererItem> getXWidgetItems() {
      wb = new XWidgetBuilder();

      // @formatter:off

      wb.andWidget(PROGRAM, XHyperlinkWfdForProgramAi.class.getSimpleName()).andValueProvider( this).andRequired().endWidget();

      wb.andXText(TITLE).andRequired().endWidget();
      wb.andChangeType();
      wb.andXText(AtsAttributeTypes.Description).andHeight(80).andRequired().endWidget();

      wb.andWidget(AtsAttributeTypes.HowFound, "XText").andHeight(80).endWidget();

      // 4 columns
      wb.andWidget(XHyperlabelFoundInVersionSelection.LABEL, "XHyperlabelFoundInVersionSelection").andComposite( 4).andRequired().endWidget();
      wb.andWidget(XHyperlabelIntroducedInVersionSelection.LABEL, "XHyperlabelIntroducedInVersionSelection").endComposite().endWidget();

      wb.andCogPriority().andEnumeratedArt(AtsArtifactToken.CogPriorityConfigArt).andRequired().andComposite(4).endWidget();
      wb.andPriority().endComposite().endWidget();

      wb.andXLabel("Flight Test Data");

      // 6 columns
      wb.andXText(AtsAttributeTypes.Ship).andComposite(6, true).endWidget();
      wb.andXText(AtsAttributeTypes.TestNumber).endWidget();
      wb.andXText(AtsAttributeTypes.FlightNumber).endWidget();

      wb.andXHyperLinkDate(AtsAttributeTypes.TestDate.getUnqualifiedName()).endWidget();

      wb.andXHyperlinkTriStateBoolean(AtsAttributeTypes.CrashOrBlankDisplay.getUnqualifiedName()).endWidget();

      // @formatter:on

      return wb.getItems();
   }

   @Override
   protected String getDebugTitle() {
      return "New PR " + atsApi.getRandomNum();
   }

   @Override
   public void widgetCreating(XWidget xWidget, FormToolkit toolkit, Artifact art,
      SwtXWidgetRenderer dynamicXWidgetLayout, XModifiedListener xModListener, boolean isEditable) {
      if (xWidget.getLabel().equals("Version")) {
         xWidget.setLabel("Targeted Version");
      }
   }

   @Override
   public void widgetCreated(XWidget xWidget, FormToolkit toolkit, Artifact art,
      SwtXWidgetRenderer dynamicXWidgetLayout, XModifiedListener xModListener, boolean isEditable) {
      super.widgetCreated(xWidget, toolkit, art, dynamicXWidgetLayout, xModListener, isEditable);
      if (xWidget.getLabel().equals(AtsAttributeTypes.Priority.getUnqualifiedName())) {
         priorityWidget = (XHyperlinkPrioritySelection) xWidget;
      } else if (xWidget instanceof XHyperlabelFoundInVersionSelection) {
         foundInWidget = (XHyperlabelFoundInVersionSelection) xWidget;

         // Add if already selected
         Collection<IAtsVersion> versions = getSelectedProgramVersions();
         foundInWidget.setSelectableVersions(versions);

         // Add listener for selection
         programWidget.addXModifiedListener(new XModifiedListener() {

            @Override
            public void widgetModified(XWidget widget) {
               Collection<IAtsVersion> versions = getSelectedProgramVersions();
               foundInWidget.setSelectableVersions(versions);
            }
         });
      } else if (xWidget instanceof XHyperlabelIntroducedInVersionSelection) {
         introducedInWidget = (XHyperlabelIntroducedInVersionSelection) xWidget;

         // Add if already selected
         Collection<IAtsVersion> versions = getSelectedProgramVersions();
         introducedInWidget.setSelectableVersions(versions);
      } else if (xWidget.getLabel().equals(AtsAttributeTypes.Ship.getUnqualifiedName())) {
         shipText = (XText) xWidget;
      } else if (xWidget.getLabel().equals(AtsAttributeTypes.TestNumber.getUnqualifiedName())) {
         testNumText = (XText) xWidget;
      } else if (xWidget.getLabel().equals(AtsAttributeTypes.FlightNumber.getUnqualifiedName())) {
         flightNumText = (XText) xWidget;
      } else if (xWidget.getLabel().equals(AtsAttributeTypes.TestDate.getUnqualifiedName())) {
         testDate = (XHyperlinkLabelDate) xWidget;
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
         foundInWidget.setSelectedVersion(ver);
         introducedInWidget.setSelectedVersion(ver);
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

      Version build = foundInWidget.getSelectedVersion();
      if (build == null) {
         rd.error("Must select Build");
      }

   }

   @Override
   public void createActionData(NewActionData data) {
      super.createActionData(data);
      data.andRelation(TeamWorkflowToFoundInVersion_Version, foundInWidget.getSelectedVersion()) //
         .andRelation(TeamWorkflowToIntroducedInVersion_Version, introducedInWidget.getSelectedVersion()) //
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
   public Collection<Object> getSelectable(XWidget widget) {
      if (widget instanceof XHyperlinkWfdForProgramAi) {
         return org.eclipse.osee.framework.jdk.core.util.Collections.castAll(getProgramCrAis());
      }
      return Collections.emptyList();
   }

   @Override
   public Collection<IAtsActionableItem> getProgramCrAis() {
      return null;
   }

}
