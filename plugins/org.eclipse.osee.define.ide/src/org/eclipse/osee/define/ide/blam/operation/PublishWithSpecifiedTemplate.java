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

import static org.eclipse.osee.framework.core.publishing.RendererOption.BRANCH;
import static org.eclipse.osee.framework.core.publishing.RendererOption.COMPARE_BRANCH;
import static org.eclipse.osee.framework.core.publishing.RendererOption.EXCLUDE_ARTIFACT_TYPES;
import static org.eclipse.osee.framework.core.publishing.RendererOption.EXCLUDE_FOLDERS;
import static org.eclipse.osee.framework.core.publishing.RendererOption.FIRST_TIME;
import static org.eclipse.osee.framework.core.publishing.RendererOption.INCLUDE_UUIDS;
import static org.eclipse.osee.framework.core.publishing.RendererOption.LINK_TYPE;
import static org.eclipse.osee.framework.core.publishing.RendererOption.MAINTAIN_ORDER;
import static org.eclipse.osee.framework.core.publishing.RendererOption.OVERRIDE_DATA_RIGHTS;
import static org.eclipse.osee.framework.core.publishing.RendererOption.PROGRESS_MONITOR;
import static org.eclipse.osee.framework.core.publishing.RendererOption.PUBLISH_DIFF;
import static org.eclipse.osee.framework.core.publishing.RendererOption.PUBLISH_EMPTY_HEADERS;
import static org.eclipse.osee.framework.core.publishing.RendererOption.RECURSE;
import static org.eclipse.osee.framework.core.publishing.RendererOption.SKIP_ERRORS;
import static org.eclipse.osee.framework.core.publishing.RendererOption.TRANSACTION_OPTION;
import static org.eclipse.osee.framework.core.publishing.RendererOption.UPDATE_PARAGRAPH_NUMBERS;
import static org.eclipse.osee.framework.core.publishing.RendererOption.USE_TEMPLATE_ONCE;
import static org.eclipse.osee.framework.core.publishing.RendererOption.VIEW;
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
import org.eclipse.osee.define.api.publishing.templatemanager.PublishingTemplateKeyGroup;
import org.eclipse.osee.define.api.publishing.templatemanager.PublishingTemplateRequest;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.enums.DataRightsClassification;
import org.eclipse.osee.framework.core.publishing.EnumRendererMap;
import org.eclipse.osee.framework.core.publishing.PublishingTemplate;
import org.eclipse.osee.framework.core.publishing.RendererMap;
import org.eclipse.osee.framework.core.publishing.RendererOption;
import org.eclipse.osee.framework.core.util.LinkType;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Message;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.jdk.core.util.xml.XmlEncoderDecoder;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.httpRequests.PublishingRequestHandler;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionManager;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavItemCat;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItem;
import org.eclipse.osee.framework.ui.skynet.blam.AbstractBlam;
import org.eclipse.osee.framework.ui.skynet.blam.VariableMap;
import org.eclipse.osee.framework.ui.skynet.branch.ViewApplicabilityUtil;
import org.eclipse.osee.framework.ui.skynet.render.MSWordTemplateClientRenderer;
import org.eclipse.osee.framework.ui.skynet.widgets.XBranchSelectWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.XCheckBox;
import org.eclipse.osee.framework.ui.skynet.widgets.XCombo;
import org.eclipse.osee.framework.ui.skynet.widgets.XListDropViewer;
import org.eclipse.osee.framework.ui.skynet.widgets.XModifiedListener;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.util.SwtXWidgetRenderer;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.widgets.FormToolkit;

/**
 * @author Jeff C. Phillips
 * @author Theron Virgin
 * @author Loren K. Ashley
 */

public class PublishWithSpecifiedTemplate extends AbstractBlam {

   private static String ARTIFACTS = "Artifacts";
   private static String SECONDARY_TEMPLATE = "Child Template";
   private static String DATA_RIGHTS = "Data Rights";
   private static String NOT_SELECTED = "--select--";
   private static String PRIMARY_TEMPLATE = "Parent Template";

   private XListDropViewer artifactsWidget;
   private BranchId branchId;
   private Map<Long, String> branchViews;

   private XCombo branchViewWidget;
   private XBranchSelectWidget branchWidget;
   private XCombo childWidget;
   private XCombo dataRightsWidget;

   /**
    * Saves a {@link Map} of the Publishing Template Manager's cache keys for each Publishing Template by the Publishing
    * Template's name.
    */

   private Map<String, PublishingTemplateKeyGroup> publishingTemplateKeyGroupBySafeNameMap;

   /**
    * {@inheritDoc}
    */

   @Override
   public Collection<XNavItemCat> getCategories() {
      return Arrays.asList(XNavigateItem.DEFINE);
   }

   /**
    * {@inheritDoc}
    */

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

   /**
    * {@inheritDoc}
    */

   @Override
   public String getName() {
      return "Publish With Specified Template";
   }

   /**
    * Makes the server REST API call to obtain the Publishing Template with the provided <code>safeName</code>.
    *
    * @param safeName the safe name of the Publishing Template to get.
    * @return the {@link PublishingTemplate}.
    * @throws OseeCoreException when unable to obtain the Publishing Template.
    */

   private PublishingTemplate getTemplate(String safeName) {

      try {
         var publishingTemplateKeyGroup = this.publishingTemplateKeyGroupBySafeNameMap.get(safeName);
         var publishingTemplateIdentifier = publishingTemplateKeyGroup.getIdentifier().getKey();
         var publishingTemplateRequest = new PublishingTemplateRequest(publishingTemplateIdentifier);
         var publishingTemplate = PublishingRequestHandler.getPublishingTemplate(publishingTemplateRequest);
         return publishingTemplate;
      } catch (Exception e) {
         //@formatter:off
         throw
            new OseeCoreException
                   (
                      new Message()
                             .title( "Unable to obtain the selected Publishing Template." )
                             .indentInc()
                             .segment( "Publishing Template Name", safeName )
                             .reasonFollows( e )
                             .toString(),
                      e
                   );
         //@formatter:on
      }
   }

   @Override
   public String getXWidgetsXml() {

      /*
       * Don't call server Publishing Template Manager until the BLAM is ready to display to the user.
       */

      /*
       * This is used to build a comma list of the publishing template names for the publishing template selection
       * widgets.
       */

      final var publishingTemplateNameCommaList = new StringBuilder(2048);

      /*
       * The Publishing Template Manager will provide a listing of all the publishing templates it has cached and all of
       * the cache keys associated with each template. A map is built with the cache keys for each publishing template
       * keyed by the publishing template name.
       */

      //@formatter:off
      this.publishingTemplateKeyGroupBySafeNameMap =
         PublishingRequestHandler.getPublishingTemplateKeyGroups()
            .getPublishingTemplateKeyGroupList()
            .stream()
            .peek
               (
                  ( publishingTemplateKeyGroup ) ->
                      publishingTemplateNameCommaList
                         .append( publishingTemplateKeyGroup.getSafeName().getKey() )
                         .append( "," )
               )
            .collect
               (
                  Collectors.toMap
                     (
                        ( publishingTemplateKeyGroup ) -> publishingTemplateKeyGroup.getSafeName().getKey(),
                        ( publishingTemplateKeyGroup ) -> publishingTemplateKeyGroup
                     )
               );
      //@formatter:on

      /*
       * Remove trailing comma
       */

      publishingTemplateNameCommaList.setLength(publishingTemplateNameCommaList.length() - 1);

      var publishingTemplateSafeNameCommaList = XmlEncoderDecoder.textToXml(publishingTemplateNameCommaList);

      StringBuilder builder = new StringBuilder();
      builder.append(String.format(
         "<xWidgets><XWidget xwidgetType=\"XCheckBox\" horizontalLabel=\"true\" labelAfter=\"true\" displayName=\"%s\" />",
         RendererOption.UPDATE_PARAGRAPH_NUMBERS.getKey()));
      builder.append(String.format(
         "<XWidget xwidgetType=\"XCheckBox\" horizontalLabel=\"true\" labelAfter=\"true\" displayName=\"%s\" />",
         RendererOption.INCLUDE_UUIDS.getKey()));
      builder.append("<XWidget xwidgetType=\"XLabel\" displayName=\"Document Link Format:\"/>");
      builder.append(String.format(
         "<XWidget xwidgetType=\"XCheckBox\" horizontalLabel=\"true\" labelAfter=\"true\" displayName=\"%s\" defaultValue=\"true\"/>",
         RendererOption.USE_ARTIFACT_NAMES.getKey()));
      builder.append(String.format(
         "<XWidget xwidgetType=\"XCheckBox\" horizontalLabel=\"true\" labelAfter=\"true\" displayName=\"%s\" />",
         RendererOption.USE_PARAGRAPH_NUMBERS.getKey()));
      builder.append(String.format("<XWidget xwidgetType=\"XArtifactTypeMultiChoiceSelect\" displayName=\"%s\" />",
         RendererOption.EXCLUDE_ARTIFACT_TYPES.getKey()));

      builder.append("<XWidget xwidgetType=\"XLabel\" displayName=\" \" /><XWidget xwidgetType=\"XCombo(");
      builder.append(publishingTemplateSafeNameCommaList);

      builder.append(String.format(")\" displayName=\"%s\" horizontalLabel=\"true\"/>", PRIMARY_TEMPLATE));
      builder.append("<XWidget xwidgetType=\"XCombo(");
      builder.append(publishingTemplateSafeNameCommaList);

      builder.append(String.format(
         ")\" displayName=\"%s\" horizontalLabel=\"true\"/><XWidget xwidgetType=\"XLabel\" displayName=\" \" />",
         SECONDARY_TEMPLATE));
      builder.append(String.format("<XWidget xwidgetType=\"XListDropViewer\" displayName=\"%s\" />", ARTIFACTS));

      builder.append("<XWidget xwidgetType=\"XLabel\" displayName=\" \" /><XWidget xwidgetType=\"XCombo(");
      builder.append(String.format(")\" displayName=\"%s\" horizontalLabel=\"true\"/>", RendererOption.VIEW.getKey()));

      builder.append(String.format(
         "<XWidget xwidgetType=\"XCheckBox\" horizontalLabel=\"true\" labelAfter=\"true\" displayName=\"%s\" />",
         RendererOption.OVERRIDE_DATA_RIGHTS.getKey()));
      builder.append("<XWidget xwidgetType=\"XCombo(");
      for (DataRightsClassification classification : DataRightsClassification.values()) {
         builder.append(classification.getDataRightsClassification());
         builder.append(",");
      }
      builder.append(String.format(")\" displayName=\"%s\" horizontalLabel=\"true\"/>", DATA_RIGHTS));

      builder.append("<XWidget xwidgetType=\"XLabel\" displayName=\"Generate Differences:\"/>");
      builder.append(String.format(
         "<XWidget xwidgetType=\"XCheckBox\" horizontalLabel=\"true\" labelAfter=\"true\" displayName=\"%s\" />",
         RendererOption.PUBLISH_DIFF.getKey()));
      builder.append(String.format("<XWidget xwidgetType=\"XBranchSelectWidget\" displayName=\"%s\"/>",
         RendererOption.WAS_BRANCH.getKey()));
      builder.append(
         "<XWidget xwidgetType=\"XLabel\" displayName=\"Note: If a WAS branch is selected, diffs will be between selected IS artifacts and current version on WAS branch\"/>");
      builder.append(
         "<XWidget xwidgetType=\"XLabel\" displayName=\"If a WAS branch is NOT selected, diffs will be between selected IS artifacts and baseline version on IS branch\"/>");
      builder.append("</xWidgets>");

      return builder.toString();
   }

   @Override
   public void runOperation(VariableMap variableMap, IProgressMonitor monitor) throws Exception {

      /*
       * Templates
       */

      var primaryPublishingTemplateSafeName = variableMap.getString(PublishWithSpecifiedTemplate.PRIMARY_TEMPLATE);

      if (Strings.isInvalidOrBlank(primaryPublishingTemplateSafeName)) {
         throw new OseeArgumentException("Must select a Parent Template");
      }

      var primaryPublishingTemplate = this.getTemplate(primaryPublishingTemplateSafeName);

      var secondaryPublishingTemplateSafeName = variableMap.getString(PublishWithSpecifiedTemplate.SECONDARY_TEMPLATE);

      //@formatter:off
      var secondaryPublishingTemplate =
         Strings.isValidAndNonBlank( secondaryPublishingTemplateSafeName )
            ? this.getTemplate( secondaryPublishingTemplateSafeName )
            : null;
      //@formatter:on

      /*
       * Artifacts
       */

      List<Artifact> artifacts = variableMap.getArtifacts(PublishWithSpecifiedTemplate.ARTIFACTS);

      /*
       * Find Is Branch
       */

      if (artifacts != null && !artifacts.isEmpty()) {
         branchId = artifacts.get(0).getBranch();
      } else {
         throw new OseeArgumentException("Must provide an artifact");
      }

      if (branchId == null) {
         throw new OseeArgumentException("Cannot determine IS branch.");
      }

      /*
       * View
       */

      Object view = variableMap.getValue(RendererOption.VIEW.getKey());
      ArtifactId viewId = ArtifactId.SENTINEL;
      if (branchViews != null && !branchViews.isEmpty()) {
         for (Entry<Long, String> entry : branchViews.entrySet()) {
            if (entry.getValue().equals(view)) {
               viewId = ArtifactId.valueOf(entry.getKey());
            }
         }
      }

      /*
       * Check Was Branch
       */

      var wasBranchId = variableMap.getValue(RendererOption.WAS_BRANCH.getKey());
      wasBranchId = Objects.nonNull(wasBranchId) ? wasBranchId : BranchId.SENTINEL;

      /*
       * Transaction
       */

      SkynetTransaction transaction =
         TransactionManager.createTransaction(branchId, "BLAM: Publish with specified template");

      if (Objects.isNull(transaction)) {
         throw new OseeCoreException("Failed to create transaction.");
      }

      /*
       * Find Link Type
       */

      boolean useArtifactNameInLinks = variableMap.getBoolean(RendererOption.USE_ARTIFACT_NAMES.getKey());
      boolean useParagraphNumbersInLinks = variableMap.getBoolean(RendererOption.USE_PARAGRAPH_NUMBERS.getKey());

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

      /*
       * Inlcude UUIDs
       */

      var includeUuids = variableMap.getBoolean(INCLUDE_UUIDS.getKey());

      /*
       * Update Paragraph Numbers
       */

      var updateParagraphNumbers = variableMap.getBoolean(UPDATE_PARAGRAPH_NUMBERS.getKey());

      /*
       * Publish Diff
       */

      var publishDiff = variableMap.getValue(PUBLISH_DIFF.getKey());

      /*
       * Override Data Rights
       */

      var overrideDataRights = variableMap.getBoolean(OVERRIDE_DATA_RIGHTS.getKey());

      var classification = variableMap.getString(DATA_RIGHTS);

      if (overrideDataRights && (NOT_SELECTED.equals(classification))) {
         throw new OseeArgumentException("When Override Data Rights is selected, a Data Right must be selectd.");
      }

      /*
       * Exclude Artifact Types List
       */

      var excludeArtifactTypesList = variableMap.getArtifactTypes(EXCLUDE_ARTIFACT_TYPES.getKey());

      //@formatter:off
      RendererMap rendererOptionsMap =
         new EnumRendererMap
            (
              BRANCH,                   branchId,
              VIEW,                     viewId,
              COMPARE_BRANCH,           wasBranchId,
              TRANSACTION_OPTION,       transaction,
              LINK_TYPE,                linkType,
              INCLUDE_UUIDS,            includeUuids,
              UPDATE_PARAGRAPH_NUMBERS, updateParagraphNumbers,
              PUBLISH_DIFF,             publishDiff,
              SKIP_ERRORS,              true,
              EXCLUDE_FOLDERS,          true,
              RECURSE,                  true,
              MAINTAIN_ORDER,           true,
              USE_TEMPLATE_ONCE,        true,
              FIRST_TIME,               true,
              PUBLISH_EMPTY_HEADERS,    false
            );

      if( Objects.nonNull( excludeArtifactTypesList ) && !excludeArtifactTypesList.isEmpty() ) {
         rendererOptionsMap.setRendererOption(EXCLUDE_ARTIFACT_TYPES, excludeArtifactTypesList);
      }

      if( Objects.nonNull( monitor ) ) {
         rendererOptionsMap.setRendererOption( PROGRESS_MONITOR, monitor );
      }

      if( overrideDataRights ) {
         rendererOptionsMap.setRendererOption( OVERRIDE_DATA_RIGHTS, classification );
      }

      MSWordTemplateClientRenderer renderer = new MSWordTemplateClientRenderer(rendererOptionsMap);

      Boolean isDiff = (Boolean) rendererOptionsMap.getRendererOptionValue(PUBLISH_DIFF);

      final AtomicInteger toProcessSize = new AtomicInteger(0);
      if (isDiff) {
         for (Artifact art : artifacts) {
            toProcessSize.addAndGet(art.getDescendants().size());
         }
      }

      final AtomicReference<Boolean> result = new AtomicReference<>();
      final int maxArtsForQuickDiff = 900;

      Display.getDefault().syncExec(new Runnable() {
         @Override
         public void run() {
            double secPerArt = 2;
            double minutes = toProcessSize.get() * secPerArt / 60;

            if (isDiff && toProcessSize.get() > maxArtsForQuickDiff && !MessageDialog.openConfirm(
               Display.getDefault().getActiveShell(), "Continue with Word Diff",
               "You have chosen to do a word diff on " + toProcessSize.get() + " Artifacts.\n\n" + //
            "This could be a very long running task (approximately " + minutes + "min) and consume large resources.\n\nAre you sure?")) {
               result.set(false);
            } else {
               result.set(true);
            }
         }
      });

      if (result.get()) {

         renderer.publish(primaryPublishingTemplate, secondaryPublishingTemplate, artifacts);

         if( Objects.nonNull(transaction) ) {
            transaction.execute();
         }

         if( Objects.nonNull(monitor) ) {
            monitor.done();
         }
      }
   }

   @Override
   public void widgetCreated(XWidget xWidget, FormToolkit toolkit, Artifact art, SwtXWidgetRenderer dynamicXWidgetLayout, XModifiedListener modListener, boolean isEditable) {
      super.widgetCreated(xWidget, toolkit, art, dynamicXWidgetLayout, modListener, isEditable);
      if (xWidget.getLabel().equals(RendererOption.WAS_BRANCH.getKey())) {
         branchWidget = (XBranchSelectWidget) xWidget;
         branchWidget.setEditable(false);
      } else if (xWidget.getLabel().equals(RendererOption.PUBLISH_DIFF.getKey())) {
         final XCheckBox checkBox = (XCheckBox) xWidget;
         checkBox.addSelectionListener(new SelectionAdapter() {

            // Link the editable setting of the branch widget to the 'Publish As Diff' checkbox
            @Override
            public void widgetSelected(SelectionEvent e) {
               super.widgetSelected(e);
               branchWidget.setEditable(checkBox.isChecked());

               if (!checkBox.isChecked()) {
                  // reset branchwidget selection when checkbox is unchecked
                  branchWidget.setSelection(null);
               }
            }

         });
      } else if (xWidget.getLabel().equals(PRIMARY_TEMPLATE)) {
         final XCombo parentCombo = (XCombo) xWidget;
         parentCombo.addModifyListener(new ModifyListener() {

            @Override
            public void modifyText(ModifyEvent e) {
               // only enable child template selection if Master is for SRS or Engineering Worksheets (EWS)
               String parentTemplate = parentCombo.get();

               if (parentTemplate.contains("srsMaster") || //
               parentTemplate.contains("ewsMaster") || //
               parentTemplate.contains("srsParent") || //
               parentTemplate.contains("ewsParent")) {
                  childWidget.setEnabled(true);
                  artifactsWidget.setEditable(false);
               } else {
                  childWidget.setEnabled(false);
                  childWidget.set("");
                  artifactsWidget.setEditable(true);
               }
            }
         });
      } else if (xWidget.getLabel().equals(SECONDARY_TEMPLATE)) {
         childWidget = (XCombo) xWidget;
         childWidget.setEnabled(false);
      } else if (xWidget.getLabel().equals(ARTIFACTS)) {
         artifactsWidget = (XListDropViewer) xWidget;
         artifactsWidget.setEditable(true);

         artifactsWidget.addXModifiedListener(new XModifiedListener() {

            @Override
            public void widgetModified(XWidget widget) {
               if (branchViewWidget != null) {
                  branchViewWidget.setEditable(true);
                  List<Artifact> artifacts = artifactsWidget.getArtifacts();
                  if (artifacts != null && !artifacts.isEmpty()) {
                     Artifact art = artifacts.iterator().next();
                     BranchId isArtBranch = art.getBranch();
                     if (isArtBranch != null && isArtBranch.isValid()) {
                        if (ViewApplicabilityUtil.isBranchOfProductLine(isArtBranch)) {
                           branchViews = ViewApplicabilityUtil.getBranchViews(isArtBranch);
                           branchViewWidget.setDataStrings(branchViews.values());
                        }
                     }
                  }
               }
            }
         });

      } else if (xWidget.getLabel().equals(RendererOption.VIEW.getKey())) {
         branchViewWidget = (XCombo) xWidget;
         branchViewWidget.setEditable(false);
      } else if (xWidget.getLabel().equals(RendererOption.OVERRIDE_DATA_RIGHTS.getKey())) {
         final XCheckBox overrideCheck = (XCheckBox) xWidget;
         overrideCheck.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
               super.widgetSelected(e);

               if (overrideCheck.isChecked()) {
                  boolean override = MessageDialog.openQuestion(
                     PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), "Override Data Rights",
                     "Delivering documents with the wrong data rights cannot be undone once submitted.\nAre you sure you want to override the data rights for this document?");
                  if (override) {
                     dataRightsWidget.setEnabled(true);
                  } else {
                     overrideCheck.set(false);
                  }
               } else {
                  dataRightsWidget.setEnabled(false);
                  dataRightsWidget.set(0);
               }
            }

         });
      } else if (xWidget.getLabel().equals(DATA_RIGHTS)) {
         dataRightsWidget = (XCombo) xWidget;
         dataRightsWidget.setEnabled(false);
      }
   }

}
