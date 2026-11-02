/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
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

package org.eclipse.osee.define.ide.blam.operation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osee.define.rest.api.publisher.templatemanager.PublishingTemplateKeyGroup;
import org.eclipse.osee.define.rest.api.publisher.templatemanager.PublishingTemplateRequest;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.DataRightsClassification;
import org.eclipse.osee.framework.core.publishing.EnumRendererMap;
import org.eclipse.osee.framework.core.publishing.FormatIndicator;
import org.eclipse.osee.framework.core.publishing.PublishIoException;
import org.eclipse.osee.framework.core.publishing.PublishingTemplate;
import org.eclipse.osee.framework.core.publishing.RendererMap;
import org.eclipse.osee.framework.core.publishing.RendererOption;
import org.eclipse.osee.framework.core.util.CoreImage;
import org.eclipse.osee.framework.core.util.LinkType;
import org.eclipse.osee.framework.core.widget.XOption;
import org.eclipse.osee.framework.core.widget.XWidgetData;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Message;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.httpRequests.PublishingRequestHandler;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionManager;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.plugin.util.MessageType;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavItemCat;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItem;
import org.eclipse.osee.framework.ui.skynet.blam.AbstractBlam;
import org.eclipse.osee.framework.ui.skynet.blam.VariableMap;
import org.eclipse.osee.framework.ui.skynet.branch.ViewApplicabilityUtil;
import org.eclipse.osee.framework.ui.skynet.render.WordTemplateRenderer;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.util.XWidgetSwtRenderer;
import org.eclipse.osee.framework.ui.skynet.widgets.xx.XXBranchSelWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.xx.XXCheckBoxWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.xx.XXListArtifactDropWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.xx.XXStringsWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.xx.util.listen.XModifiedListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.osgi.service.component.annotations.Component;

/**
 * @author Jeff C. Phillips
 * @author Theron Virgin
 * @author Loren K. Ashley
 */

@Component(service = AbstractBlam.class, immediate = true)
public class PublishWithSpecifiedTemplateBlam extends AbstractBlam {

   private static final String BLAM_NAME = "Publish With Specified Template";
   /**
    * Saves a {@link Map} of the Publishing Template Manager's cache keys for each Publishing Template by the Publishing
    * Template's name.
    */

   private Map<String, PublishingTemplateKeyGroup> publishingTemplateKeyGroupBySafeNameMap;

   public PublishWithSpecifiedTemplateBlam() {
      super(BLAM_NAME, CoreImage.PUBLISH);
   }
   static final String ARTIFACTS = "Artifacts";
   static final String SECONDARY_TEMPLATE = "Child Template";
   static final String DATA_RIGHTS = "Data Rights";
   private static final String NOT_SELECTED = "--select--";
   static final String PRIMARY_TEMPLATE = "Parent Template";
   static final String UPDATE_PARAGRAPH_NUMBERS = "Update Paragraph Numbers";
   static final String INCLUDE_UUIDS = "Include Uuids";
   static final String USE_ARTIFACT_NAMES = "Use Artifact Names";
   static final String USE_PARAGRAPH_NUMBERS = "Use Paragraph Numbers";
   static final String EXCLUDE_ARTIFACT_TYPES = "ExcludeArtifactTypes";
   static final String VIEW = "View";
   static final String OVERRIDE_DATA_RIGHTS = "Override Data Rights";
   static final String PUBLISH_DIFF = "Publish Diff";
   static final String WAS_BRANCH = "Was Branch";

   private XXListArtifactDropWidget artifactsWidget;
   private BranchId branchId;
   private Map<Long, String> branchViews;
   private XXStringsWidget branchViewWidget;
   private XXBranchSelWidget branchWidget;
   private XXStringsWidget childWidget;
   private XXStringsWidget dataRightsWidget;

   //////////////////////////////////////////
   // Widgets
   //////////////////////////////////////////

   @Override
   public List<XWidgetData> getXWidgetItems() {
      // Don't call server Publishing Template Manager until the BLAM is ready to display to the user.

      // Builds a comma list of publishing template names for the template selection widgets
      List<String> publishingTemplateNameList = new ArrayList<>();

      // Build map of cache keys for each publishing template keyed by template name
      this.publishingTemplateKeyGroupBySafeNameMap =
         PublishingRequestHandler.getPublishingTemplateKeyGroups().getPublishingTemplateKeyGroupList().stream().peek(
            (publishingTemplateKeyGroup) -> publishingTemplateNameList.add(
               publishingTemplateKeyGroup.getSafeName().getKey())).collect(
                  Collectors.toMap((publishingTemplateKeyGroup) -> publishingTemplateKeyGroup.getSafeName().getKey(),
                     (publishingTemplateKeyGroup) -> publishingTemplateKeyGroup));

      createWidgetBuilder();
      wb.andCheckBoxWidget(UPDATE_PARAGRAPH_NUMBERS);
      wb.andCheckBoxWidget(INCLUDE_UUIDS);
      wb.andSpaceWidget();
      wb.andLabelWidget("Document Link Format:").and(XOption.LABEL_BOLD);
      wb.andCheckBoxWidget(USE_ARTIFACT_NAMES).andDefault(true);
      wb.andCheckBoxWidget(USE_PARAGRAPH_NUMBERS);
      wb.andSpaceWidget();
      wb.andArtifactTypeWidget(EXCLUDE_ARTIFACT_TYPES).andValueMultiSelect().and(XOption.WRAP_VALUES);
      wb.andSpaceWidget();
      wb.andStringsSelWidget(PRIMARY_TEMPLATE).andSelectable(publishingTemplateNameList);
      wb.andStringsSelWidget(SECONDARY_TEMPLATE).andSelectable(publishingTemplateNameList);
      wb.andSpaceWidget();
      wb.andListDropWidget(ARTIFACTS);
      wb.andSpaceWidget();
      wb.andStringsSelWidget(VIEW).andHorizLabel();
      wb.andCheckBoxWidget(OVERRIDE_DATA_RIGHTS);
      List<String> classifications = new ArrayList<>();
      for (DataRightsClassification classification : DataRightsClassification.values()) {
         classifications.add(classification.getDataRightsClassification());
      }
      wb.andStringsSelWidget(DATA_RIGHTS).andSelectable(classifications.toArray());
      wb.andLabelWidget("Generate Differences:");
      wb.andCheckBoxWidget(PUBLISH_DIFF);
      wb.andBranchSelWidget(WAS_BRANCH);
      wb.andLabelWidget(
         "Note: If a WAS branch is selected, diffs will be between selected IS artifacts and current version on WAS branch");
      wb.andLabelWidget(
         "If a WAS branch is NOT selected, diffs will be between selected IS artifacts and baseline version on IS branch");
      return wb.getXWidgetDatas();
   }

   @Override
   public void widgetCreated(XWidget xWidget, FormToolkit toolkit, Artifact art, XWidgetSwtRenderer xWidgetSwtRenderer,
      XModifiedListener modListener, boolean isEditable) {
      super.widgetCreated(xWidget, toolkit, art, xWidgetSwtRenderer, modListener, isEditable);
      if (xWidget.getLabel().equals(WAS_BRANCH)) {
         branchWidget = (XXBranchSelWidget) xWidget;
         branchWidget.setEditable(false);
      } else if (xWidget.getLabel().equals(PUBLISH_DIFF)) {
         final XXCheckBoxWidget checkBox = (XXCheckBoxWidget) xWidget;
         checkBox.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
               super.widgetSelected(e);
               branchWidget.setEditable(checkBox.isChecked());

               if (!checkBox.isChecked()) {
                  branchWidget.clear();
               }
            }

         });
      } else if (xWidget.getLabel().equals(PRIMARY_TEMPLATE)) {
         final XXStringsWidget parentCombo = (XXStringsWidget) xWidget;
         parentCombo.addXModifiedListener(new XModifiedListener() {

            @Override
            public void widgetModified(XWidget widget) {
               String parentTemplate = parentCombo.getSelectedFirst();

               if (parentTemplate.contains("srsMaster") || //
                  parentTemplate.contains("ewsMaster") || //
                  parentTemplate.contains("srsParent") || //
                  parentTemplate.contains("ewsParent")) {
                  childWidget.setEnabled(true);
                  artifactsWidget.setEditable(false);
               } else {
                  childWidget.setEnabled(false);
                  childWidget.setSelected("");
                  artifactsWidget.setEditable(true);
               }
            }
         });
      } else if (xWidget.getLabel().equals(SECONDARY_TEMPLATE)) {
         childWidget = (XXStringsWidget) xWidget;
         childWidget.setEnabled(false);
      } else if (xWidget.getLabel().equals(ARTIFACTS)) {
         artifactsWidget = (XXListArtifactDropWidget) xWidget;
         artifactsWidget.setEditable(true);

         artifactsWidget.addXModifiedListener(new XModifiedListener() {

            @Override
            public void widgetModified(XWidget widget) {
               if (branchViewWidget != null) {
                  branchViewWidget.setEditable(true);
                  List<Artifact> artifacts = artifactsWidget.getSelectedArtifacts();
                  if (artifacts != null && !artifacts.isEmpty()) {
                     Artifact art = artifacts.iterator().next();
                     BranchId isArtBranch = art.getBranch();
                     if (isArtBranch != null && isArtBranch.isValid()) {
                        if (ViewApplicabilityUtil.isProductLineBranch(isArtBranch)) {
                           branchViews = ViewApplicabilityUtil.getBranchViews(isArtBranch);
                           branchViewWidget.setSelectable(branchViews.values());
                        }
                     }
                  }
               }
            }
         });

      } else if (xWidget.getLabel().equals(VIEW)) {
         branchViewWidget = (XXStringsWidget) xWidget;
         branchViewWidget.setEditable(false);
      } else if (xWidget.getLabel().equals(OVERRIDE_DATA_RIGHTS)) {
         final XXCheckBoxWidget overrideCheck = (XXCheckBoxWidget) xWidget;
         overrideCheck.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
               super.widgetSelected(e);

               if (overrideCheck.isChecked()) {
                  boolean override = MessageDialog.openQuestion(
                     PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), OVERRIDE_DATA_RIGHTS,
                     "Delivering documents with the wrong data rights cannot be undone once submitted.\nAre you sure you want to override the data rights for this document?");
                  if (override) {
                     dataRightsWidget.setEnabled(true);
                  } else {
                     overrideCheck.setSelected(false);
                  }
               } else {
                  dataRightsWidget.setEnabled(false);
                  dataRightsWidget.setSelected("0");
               }
            }

         });
      } else if (xWidget.getLabel().equals(DATA_RIGHTS)) {
         dataRightsWidget = (XXStringsWidget) xWidget;
         dataRightsWidget.setEnabled(false);
      }
   }

   //////////////////////////////////////////
   // Operation
   //////////////////////////////////////////

   @Override
   public void runOperation(VariableMap variableMap, IProgressMonitor monitor) throws Exception {

      // Templates
      String primaryPublishingTemplateSafeName = variableMap.getString(PRIMARY_TEMPLATE);

      if (Strings.isInvalidOrBlank(primaryPublishingTemplateSafeName) || NOT_SELECTED.equals(
         primaryPublishingTemplateSafeName)) {
         throw new OseeArgumentException("Must select a Parent Template");
      }

      PublishingTemplate primaryPublishingTemplate = this.getTemplate(primaryPublishingTemplateSafeName);

      String secondaryPublishingTemplateSafeName = variableMap.getString(SECONDARY_TEMPLATE);

      PublishingTemplate secondaryPublishingTemplate =
         (Strings.isValidAndNonBlank(secondaryPublishingTemplateSafeName) && !NOT_SELECTED.equals(
            secondaryPublishingTemplateSafeName)) ? this.getTemplate(secondaryPublishingTemplateSafeName) : null;

      // Artifacts
      List<Artifact> artifacts = variableMap.getArtifacts(ARTIFACTS);

      // Find Is Branch
      if (artifacts != null && !artifacts.isEmpty()) {
         branchId = artifacts.get(0).getBranch();
      } else {
         throw new OseeArgumentException("Must provide an artifact");
      }

      if (branchId == null) {
         throw new OseeArgumentException("Cannot determine IS branch.");
      }

      // View
      Object view = variableMap.getValue(VIEW);
      ArtifactId viewId = ArtifactId.SENTINEL;
      if (branchViews != null && !branchViews.isEmpty()) {
         for (Entry<Long, String> entry : branchViews.entrySet()) {
            if (entry.getValue().equals(view)) {
               viewId = ArtifactId.valueOf(entry.getKey());
            }
         }
      }

      // Check Was Branch
      Object wasBranchId = variableMap.getValue(WAS_BRANCH);
      wasBranchId = Objects.nonNull(wasBranchId) ? wasBranchId : BranchId.SENTINEL;

      // Transaction
      SkynetTransaction transaction =
         TransactionManager.createTransaction(branchId, "BLAM: Publish with specified template");

      if (Objects.isNull(transaction)) {
         throw new OseeCoreException("Failed to create transaction.");
      }

      // Find Link Type
      boolean useArtifactNameInLinks = variableMap.getBoolean(USE_ARTIFACT_NAMES);
      boolean useParagraphNumbersInLinks = variableMap.getBoolean(USE_PARAGRAPH_NUMBERS);

      if (!useParagraphNumbersInLinks && !useArtifactNameInLinks) {
         throw new OseeArgumentException("Please select at least one Document Link Format");
      }

      LinkType linkType;
      if (useArtifactNameInLinks && useParagraphNumbersInLinks) {
         linkType = LinkType.INTERNAL_DOC_REFERENCE_USE_PARAGRAPH_NUMBER_AND_NAME;
      } else if (useParagraphNumbersInLinks) {
         linkType = LinkType.INTERNAL_DOC_REFERENCE_USE_PARAGRAPH_NUMBER;
      } else {
         linkType = LinkType.INTERNAL_DOC_REFERENCE_USE_NAME;
      }

      // Include UUIDs
      boolean includeUuids = variableMap.getBoolean(INCLUDE_UUIDS);

      // Update Paragraph Numbers
      boolean updateParagraphNumbers = variableMap.getBoolean(UPDATE_PARAGRAPH_NUMBERS);

      // Publish Diff
      Object publishDiff = variableMap.getValue(PUBLISH_DIFF);

      // Override Data Rights
      boolean overrideDataRights = variableMap.getBoolean(OVERRIDE_DATA_RIGHTS);

      String classification = variableMap.getString(DATA_RIGHTS);

      if (overrideDataRights && (NOT_SELECTED.equals(classification))) {
         throw new OseeArgumentException("When Override Data Rights is selected, a Data Right must be selected.");
      }

      // Exclude Artifact Types List
      List<ArtifactTypeToken> excludeArtifactTypesList = variableMap.getArtifactTypes(EXCLUDE_ARTIFACT_TYPES);

      RendererMap rendererOptionsMap = new EnumRendererMap(RendererOption.BRANCH, branchId, RendererOption.VIEW, viewId,
         RendererOption.COMPARE_BRANCH, wasBranchId, RendererOption.TRANSACTION_OPTION, transaction,
         RendererOption.LINK_TYPE, linkType, RendererOption.INCLUDE_UUIDS, includeUuids,
         RendererOption.UPDATE_PARAGRAPH_NUMBERS, updateParagraphNumbers, RendererOption.PUBLISH_DIFF, publishDiff,
         RendererOption.SKIP_ERRORS, true, RendererOption.OUTLINING_OPTION_OVERRIDE_EXCLUDE_ARTIFACT_TYPES,
         List.of(CoreArtifactTypes.Folder), RendererOption.MAINTAIN_ORDER, true, RendererOption.USE_TEMPLATE_ONCE, true,
         RendererOption.FIRST_TIME, true);

      if (Objects.nonNull(excludeArtifactTypesList) && !excludeArtifactTypesList.isEmpty()) {
         rendererOptionsMap.setRendererOption(RendererOption.OUTLINING_OPTION_OVERRIDE_EXCLUDE_ARTIFACT_TYPES,
            excludeArtifactTypesList);
      }

      if (Objects.nonNull(monitor)) {
         rendererOptionsMap.setRendererOption(RendererOption.PROGRESS_MONITOR, monitor);
      }

      if (overrideDataRights) {
         rendererOptionsMap.setRendererOption(RendererOption.OVERRIDE_DATA_RIGHTS, classification);
      }

      WordTemplateRenderer renderer = new WordTemplateRenderer(rendererOptionsMap);

      Boolean isDiff = (Boolean) rendererOptionsMap.getRendererOptionValue(RendererOption.PUBLISH_DIFF);

      final AtomicInteger toProcessSize = new AtomicInteger(0);
      if (isDiff) {
         for (Artifact art : artifacts) {
            toProcessSize.addAndGet(art.getDescendants().size());
         }
      }

      final AtomicReference<Boolean> result = new AtomicReference<>();
      final int maxArtsForQuickDiff = 900;

      Display.getDefault().syncExec(() -> {
         double secPerArt = 2;
         double minutes = toProcessSize.get() * secPerArt / 60;

         if (isDiff && toProcessSize.get() > maxArtsForQuickDiff && !MessageDialog.openConfirm(
            Display.getDefault().getActiveShell(), "Continue with Word Diff",
            String.format(
               "You have chosen to do a word diff on %d Artifacts.\n\n" + "This could be a very long running task (approximately %.1fmin) and consume large resources.\n\nAre you sure?",
               toProcessSize.get(), minutes))) {
            result.set(false);
         } else {
            result.set(true);
         }
      });

      if (result.get()) {

         try {

            renderer.publish(primaryPublishingTemplate, secondaryPublishingTemplate, artifacts);

            if (Objects.nonNull(transaction)) {
               transaction.execute();
            }

         } catch (PublishIoException pie) {

            final String message =
               new Message().title(pie.getMessage()).indentInc().segment("File", pie.getFilePath()).indentDec().title(
                  "Ensure the file is not open in Word.").toString();

            final String longMessage =
               new Message().title(pie.getMessage()).indentInc().reasonFollowsWithTrace(pie).toString();

            AWorkbench.popup(MessageType.Error, "Publish With Specified Template Error", message, longMessage, null,
               AWorkbench.SCROLLING_TEXT_BOX);

         } catch (Exception e) {

            final String message = new Message().title("Publish Failed").reasonFollowsWithTrace(e).toString();

            AWorkbench.popup(MessageType.Error, "Publish With Specified Template Error", "Publish Failed", message,
               null, AWorkbench.SCROLLING_TEXT_BOX);

         } finally {

            if (Objects.nonNull(monitor)) {
               monitor.done();
            }

         }
      }
   }

   //////////////////////////////////////////
   // Helpers
   //////////////////////////////////////////

   private PublishingTemplate getTemplate(String safeName) {

      try {
         PublishingTemplateKeyGroup publishingTemplateKeyGroup =
            this.publishingTemplateKeyGroupBySafeNameMap.get(safeName);
         String publishingTemplateIdentifier = publishingTemplateKeyGroup.getIdentifier().getKey();
         PublishingTemplateRequest publishingTemplateRequest =
            new PublishingTemplateRequest(publishingTemplateIdentifier, FormatIndicator.WORD_ML);
         PublishingTemplate publishingTemplate =
            PublishingRequestHandler.getPublishingTemplate(publishingTemplateRequest);
         return publishingTemplate;
      } catch (Exception e) {
         throw new OseeCoreException(
            new Message().title("Unable to obtain the selected Publishing Template.").indentInc().segment(
               "Publishing Template Name", safeName).reasonFollows(e).toString(),
            e);
      }
   }

   //////////////////////////////////////////
   // Categories & Description
   //////////////////////////////////////////

   @Override
   public Collection<XNavItemCat> getCategories() {
      return Arrays.asList(XNavigateItem.DEFINE);
   }

   @Override
   public String getDescriptionUsage() {
      StringBuilder sb = new StringBuilder();
      sb.append("<form>Use a template to publish a document or diff the document against a different version.<br/>");
      sb.append("Select Parameters<br/>");
      sb.append("<li>Select Update Paragraph Numbers if authorized to update them</li>");
      sb.append("<li>Choose whether or not you want the UUIDs published</li>");
      sb.append("<li>Select the Document Link format(s)</li>");
      sb.append("<li>Choose artifact type(s) to exclude</li>");
      sb.append("<li>Select Parent or Parent/Child (for SRS) template.  Only use non-recursive templates</li>");
      sb.append(
         "<li>Drag &amp; Drop the IS Artifacts into the box OR write an Orcs Query that returns a list of Artifact Ids</li>");
      sb.append("<li>Decide to Publish as Diff and select WAS branch as desired</li>");
      sb.append("<br/>Click the play button at the top right or in the Execute section.</form>");
      return sb.toString();
   }

}
