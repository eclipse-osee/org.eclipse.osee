/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.define.ide.blam.operation;

import static org.eclipse.osee.framework.core.enums.DeletionFlag.EXCLUDE_DELETED;
import static org.eclipse.osee.framework.core.util.RendererOption.BRANCH;
import static org.eclipse.osee.framework.core.util.RendererOption.COMPARE_BRANCH;
import static org.eclipse.osee.framework.core.util.RendererOption.EXCLUDE_ARTIFACT_TYPES;
import static org.eclipse.osee.framework.core.util.RendererOption.EXCLUDE_FOLDERS;
import static org.eclipse.osee.framework.core.util.RendererOption.FIRST_TIME;
import static org.eclipse.osee.framework.core.util.RendererOption.INCLUDE_UUIDS;
import static org.eclipse.osee.framework.core.util.RendererOption.LINK_TYPE;
import static org.eclipse.osee.framework.core.util.RendererOption.MAINTAIN_ORDER;
import static org.eclipse.osee.framework.core.util.RendererOption.OVERRIDE_DATA_RIGHTS;
import static org.eclipse.osee.framework.core.util.RendererOption.PROGRESS_MONITOR;
import static org.eclipse.osee.framework.core.util.RendererOption.PUBLISH_DIFF;
import static org.eclipse.osee.framework.core.util.RendererOption.PUBLISH_EMPTY_HEADERS;
import static org.eclipse.osee.framework.core.util.RendererOption.RECURSE;
import static org.eclipse.osee.framework.core.util.RendererOption.SKIP_ERRORS;
import static org.eclipse.osee.framework.core.util.RendererOption.TRANSACTION_OPTION;
import static org.eclipse.osee.framework.core.util.RendererOption.UPDATE_PARAGRAPH_NUMBERS;
import static org.eclipse.osee.framework.core.util.RendererOption.USE_TEMPLATE_ONCE;
import static org.eclipse.osee.framework.core.util.RendererOption.VIEW;
import static org.eclipse.osee.framework.core.util.RendererOption.WAS_BRANCH;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.IUserGroupArtifactToken;
import org.eclipse.osee.framework.core.enums.CoreUserGroups;
import org.eclipse.osee.framework.core.enums.DataRightsClassification;
import org.eclipse.osee.framework.core.util.RendererOption;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.linking.LinkType;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionManager;
import org.eclipse.osee.framework.ui.skynet.blam.AbstractBlam;
import org.eclipse.osee.framework.ui.skynet.blam.VariableMap;
import org.eclipse.osee.framework.ui.skynet.branch.ViewApplicabilityUtil;
import org.eclipse.osee.framework.ui.skynet.internal.ServiceUtil;
import org.eclipse.osee.framework.ui.skynet.render.WordTemplateRenderer;
import org.eclipse.osee.framework.ui.skynet.templates.TemplateManager;
import org.eclipse.osee.framework.ui.skynet.widgets.XBranchSelectWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.XCheckBox;
import org.eclipse.osee.framework.ui.skynet.widgets.XCombo;
import org.eclipse.osee.framework.ui.skynet.widgets.XDslEditorWidget;
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
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author Jeff C. Phillips
 * @author Theron Virgin
 */
public class PublishWithSpecifiedTemplate extends AbstractBlam {

   private static String ARTIFACTS = "Artifacts";
   private static String MASTER_TEMPLATE = "Master Template";
   private static String SLAVE_TEMPLATE = "Slave Template";
   private static String DATA_RIGHTS = "Data Rights";

   private List<Artifact> templates;
   private BranchId branch;
   private Map<Long, String> branchViews;

   private XBranchSelectWidget branchWidget;
   private XCombo slaveWidget;
   private XDslEditorWidget orcsQueryWidget;
   private XCombo branchViewWidget;
   private XListDropViewer artifactsWidget;
   private XCombo dataRightsWidget;

   @Override
   public String getName() {
      return "Publish With Specified Template";
   }

   @Override
   public void runOperation(VariableMap variableMap, IProgressMonitor monitor) throws Exception {
      populateTemplateList();

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

      Artifact master = getTemplate(variableMap.getString(MASTER_TEMPLATE));
      if (master == null) {
         throw new OseeArgumentException("Must select a Master Template");
      }
      Artifact slave = getTemplate(variableMap.getString(SLAVE_TEMPLATE));

      String classification = variableMap.getString(DATA_RIGHTS);

      List<Artifact> artifacts = null;
      try {
         if (orcsQueryWidget.getText().isEmpty()) {
            artifacts = variableMap.getArtifacts(ARTIFACTS);
         } else {
            artifacts = getArtifactsFromOrcsQuery();
         }
      } catch (NullPointerException e) {
         throw new OseeArgumentException(
            "Must provide an IS artifact or an Orcs Query.  Please add an IS artifact or an Orcs Query and rerun.");
      }
      if (artifacts != null && !artifacts.isEmpty()) {
         branch = artifacts.get(0).getBranch();
      } else {
         throw new OseeArgumentException("Must provide an artifact");
      }

      if (branch == null) {
         throw new OseeArgumentException("Cannot determine IS branch.");
      }

      SkynetTransaction transaction =
         TransactionManager.createTransaction(branch, "BLAM: Publish with specified template");

      Object view = variableMap.getValue(RendererOption.VIEW.getKey());
      ArtifactId viewId = ArtifactId.SENTINEL;
      if (branchViews != null && !branchViews.isEmpty()) {
         for (Entry<Long, String> entry : branchViews.entrySet()) {
            if (entry.getValue().equals(view)) {
               viewId = ArtifactId.valueOf(entry.getKey());
            }
         }
      }

      HashMap<RendererOption, Object> rendererOptionsMap = new HashMap<>();
      rendererOptionsMap.put(BRANCH, branch);
      rendererOptionsMap.put(COMPARE_BRANCH, variableMap.getValue(WAS_BRANCH.getKey()));
      rendererOptionsMap.put(INCLUDE_UUIDS, variableMap.getValue(INCLUDE_UUIDS.getKey()));
      rendererOptionsMap.put(LINK_TYPE, linkType);
      rendererOptionsMap.put(UPDATE_PARAGRAPH_NUMBERS, variableMap.getBoolean(UPDATE_PARAGRAPH_NUMBERS.getKey()));
      rendererOptionsMap.put(EXCLUDE_ARTIFACT_TYPES, variableMap.getArtifactTypes(EXCLUDE_ARTIFACT_TYPES.getKey()));
      rendererOptionsMap.put(TRANSACTION_OPTION, transaction);
      rendererOptionsMap.put(SKIP_ERRORS, true);
      rendererOptionsMap.put(EXCLUDE_FOLDERS, true);
      rendererOptionsMap.put(RECURSE, true);
      rendererOptionsMap.put(MAINTAIN_ORDER, true);
      rendererOptionsMap.put(PROGRESS_MONITOR, true);
      rendererOptionsMap.put(USE_TEMPLATE_ONCE, true);
      rendererOptionsMap.put(FIRST_TIME, true);
      rendererOptionsMap.put(PUBLISH_DIFF, variableMap.getValue(PUBLISH_DIFF.getKey()));
      rendererOptionsMap.put(VIEW, viewId);
      rendererOptionsMap.put(PUBLISH_EMPTY_HEADERS, false);
      rendererOptionsMap.put(OVERRIDE_DATA_RIGHTS, classification);

      WordTemplateRenderer renderer = new WordTemplateRenderer(rendererOptionsMap);

      Boolean isDiff = (Boolean) rendererOptionsMap.get(PUBLISH_DIFF);

      int toProcessSize = 0;
      if (isDiff) {
         for (Artifact art : artifacts) {
            toProcessSize += art.getDescendants().size();
         }
      }

      final int totalSize = toProcessSize;
      final AtomicReference<Boolean> result = new AtomicReference<>();
      final int maxArtsForQuickDiff = 900;

      Display.getDefault().syncExec(new Runnable() {
         @Override
         public void run() {
            double secPerArt = 2;
            double minutes = totalSize * secPerArt / 60;

            if (isDiff && totalSize > maxArtsForQuickDiff && !MessageDialog.openConfirm(
               Display.getDefault().getActiveShell(), "Continue with Word Diff",
               "You have chosen to do a word diff on " + totalSize + " Artifacts.\n\n" + //
            "This could be a very long running task (approximately " + minutes + "min) and consume large resources.\n\nAre you sure?")) {
               result.set(false);
            } else {
               result.set(true);
            }
         }
      });

      if (result.get()) {
         renderer.publish(master, slave, artifacts);
         transaction.execute();
         monitor.done();
      }
   }

   private List<Artifact> getArtifactsFromOrcsQuery() {
      String orcsQuery = orcsQueryWidget.getText();
      parseQueryForBranchName(orcsQuery);

      //Parse JSON returned from query
      ArrayList<Artifact> artifacts = new ArrayList<>();
      try {
         String result = ServiceUtil.getOseeClient().runOrcsScript(orcsQuery);
         JSONObject jsonObject = new JSONObject(result);
         JSONArray results = jsonObject.getJSONArray("results");
         if (results.length() >= 1 && branch != null) {
            JSONArray artifactIds = results.getJSONObject(0).getJSONArray("artifacts");
            JSONObject id = null;
            for (int i = 0; i < artifactIds.length(); i++) {
               id = artifactIds.getJSONObject(i);
               ArtifactId artifactId = ArtifactId.valueOf(id.getLong("id"));
               Artifact artifact = ArtifactQuery.getArtifactFromId(artifactId, branch, EXCLUDE_DELETED);
               artifacts.add(artifact);
            }
         }
      } catch (JSONException ex) {
         OseeCoreException.wrapAndThrow(ex);
      }
      return artifacts;
   }

   private void parseQueryForBranchName(String orcsQuery) {
      Pattern pattern = Pattern.compile("start from branch \"(.*?)\"");
      Matcher matcher = pattern.matcher(orcsQuery);
      if (matcher.find()) {
         String branchName = matcher.group(1);
         branch = BranchManager.getBranch(branchName);
      }
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
      sb.append("<li>Select Master or Master/Slave (for SRS) template.  Only use non-recursive templates</li>");
      sb.append(
         "<li>Drag &amp; Drop the IS Artifacts into the box OR write an Orcs Query that returns a list of Artifact Ids</li>");
      sb.append("<li>Decide to Publish as Diff and select WAS branch as desired</li>");
      sb.append("<br/>Click the play button at the top right or in the Execute section.</form>");
      return sb.toString();
   }

   @Override
   public String getXWidgetsXml() {
      populateTemplateList();
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
      for (Artifact art : templates) {
         builder.append(art.getSafeName());
         builder.append(",");
      }
      builder.append(String.format(")\" displayName=\"%s\" horizontalLabel=\"true\"/>", MASTER_TEMPLATE));
      builder.append("<XWidget xwidgetType=\"XCombo(");
      for (Artifact art : templates) {
         builder.append(art.getSafeName());
         builder.append(",");
      }

      builder.append(String.format(
         ")\" displayName=\"%s\" horizontalLabel=\"true\"/><XWidget xwidgetType=\"XLabel\" displayName=\" \" />",
         SLAVE_TEMPLATE));
      builder.append(String.format("<XWidget xwidgetType=\"XListDropViewer\" displayName=\"%s\" />", ARTIFACTS));

      builder.append("<XWidget xwidgetType=\"XLabel\" displayName=\" \" /><XWidget xwidgetType=\"XCombo(");
      builder.append(String.format(")\" displayName=\"%s\" horizontalLabel=\"true\"/>", RendererOption.VIEW.getKey()));

      builder.append(String.format("<XWidget xwidgetType=\"XDslEditorWidget\" displayName=\"%s\"  defaultValue=\"",
         RendererOption.ORCS_QUERY.getKey()));

      builder.append("orcs");
      builder.append("\" fill=\"vertically\"/>");

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
      } else if (xWidget.getLabel().equals(MASTER_TEMPLATE)) {
         final XCombo masterCombo = (XCombo) xWidget;
         masterCombo.addModifyListener(new ModifyListener() {

            @Override
            public void modifyText(ModifyEvent e) {
               // only enable slave template selection if Master is for SRS or Engineering Worksheets (EWS)
               String masterTemplate = masterCombo.get();
               if (masterTemplate.contains("srsMaster") || //
               masterTemplate.contains("ewsMaster")) {
                  slaveWidget.setEnabled(true);
                  orcsQueryWidget.setEditable(false);
                  orcsQueryWidget.set("");
                  artifactsWidget.setEditable(false);
               } else {
                  slaveWidget.setEnabled(false);
                  slaveWidget.set("");
                  orcsQueryWidget.setEditable(true);
                  artifactsWidget.setEditable(true);
               }
            }
         });
      } else if (xWidget.getLabel().equals(SLAVE_TEMPLATE)) {
         slaveWidget = (XCombo) xWidget;
         slaveWidget.setEnabled(false);
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

      } else if (xWidget.getLabel().equals(RendererOption.ORCS_QUERY.getKey())) {

         orcsQueryWidget = (XDslEditorWidget) xWidget;
         orcsQueryWidget.setEditable(true);
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

   private void populateTemplateList() {
      templates = TemplateManager.getAllTemplates();
      Collections.sort(templates);
   }

   private Artifact getTemplate(String templateName) {
      for (Artifact artifact : templates) {
         if (artifact.getSafeName().equals(templateName)) {
            return artifact;
         }
      }
      return null;
   }

   @Override
   public Collection<String> getCategories() {
      return Arrays.asList("Define.Publish");
   }

   @Override
   public Collection<IUserGroupArtifactToken> getUserGroups() {
      return Collections.singleton(CoreUserGroups.Everyone);
   }

}
