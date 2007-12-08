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

package org.eclipse.osee.ats.workflow;

import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import javax.xml.parsers.ParserConfigurationException;
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.artifact.ATSAttributes;
import org.eclipse.osee.ats.editor.IAtsStateItem;
import org.eclipse.osee.ats.editor.SMAManager;
import org.eclipse.osee.ats.editor.service.WorkPageService;
import org.eclipse.osee.ats.util.DefaultTeamState;
import org.eclipse.osee.ats.util.widgets.dialog.TaskResolutionOptions;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.plugin.util.Result;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.util.OSEELog;
import org.eclipse.osee.framework.ui.skynet.widgets.XModifiedListener;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.DynamicXWidgetLayoutData;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.IXWidgetOptionResolver;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkPage;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * @author Donald G. Dunne
 */
public class AtsWorkPage extends WorkPage {

   private boolean requireStateHoursSpentPrompt = false;
   private boolean forceAssigneesToTeamLeads = false;
   private boolean allowCreateBranch = false;
   private boolean allowCommitBranch = false;
   protected TaskResolutionOptions taskResolutionOptions;
   private boolean startPage = false;
   private boolean validatePage = false;
   private boolean validateReviewBlocking = false;
   public static String WORKPAGE_STARTPAGE = "startPage";
   public static String WORKPAGE_VALIDATE_PAGE = "validatePage";
   public static String WORKPAGE_PAGE_ID = "pageId";
   public static String WORKPAGE_ATS_REQUIRE_STATE_HOURS_SPENT_PROMPT = "atsRequireStateHourSpentPrompt";
   public static String WORkPAGE_ATS_FORCE_ASSIGNEES_TO_TEAM_LEADS = "atsForceAssigneesToTeamLeads";
   public static String WORKPAGE_ATS_ALLOW_CREATE_BRANCH = "atsAllowCreateBranch";
   public static String WORKPAGE_ATS_ALLOW_COMMIT_BRANCH = "atsAllowCommitBranch";
   private PageType pageType;
   private SMAManager smaMgr;
   private String instructionStr;
   public static enum PageType {
      Team, ActionableItem, WorkFlowPage
   };

   public AtsWorkPage(String name, String id, String xWidgetsXml, IXWidgetOptionResolver optionResolver) {
      super(name, id, xWidgetsXml, optionResolver);
   }

   public AtsWorkPage(IXWidgetOptionResolver optionResolver) {
      this("", "", null, optionResolver);
   }

   /*
    * (non-Javadoc)
    * 
    * @see osee.skynet.gui.widgets.workflow.WorkPage#widgetCreated(osee.skynet.gui.widgets.XWidget,
    *      org.eclipse.ui.forms.widgets.FormToolkit, osee.skynet.gui.widgets.workflow.WorkPage,
    *      osee.skynet.gui.widgets.XModifiedListener, boolean)
    */
   @Override
   public void widgetCreated(XWidget xWidget, FormToolkit toolkit, Artifact art, WorkPage page, XModifiedListener xModListener, boolean isEditable) {
      super.widgetCreated(xWidget, toolkit, art, page, xModListener, isEditable);
      // Check extenstion points for page creation
      if (smaMgr != null) {
         for (IAtsStateItem item : smaMgr.getStateItems().getStateItems(page.getId())) {
            item.xWidgetCreated(xWidget, toolkit, (AtsWorkPage) page, art, xModListener, isEditable);
         }
      }
   }

   /*
    * (non-Javadoc)
    * 
    * @see osee.skynet.gui.widgets.workflow.WorkPage#workAttrCreated(osee.skynet.gui.widgets.workflow.WorkAttribute,
    *      osee.skynet.gui.widgets.XWidget, org.eclipse.ui.forms.widgets.FormToolkit,
    *      osee.skynet.artifact.Artifact, osee.skynet.gui.widgets.workflow.WorkPage,
    *      osee.skynet.gui.widgets.XModifiedListener, boolean)
    */
   @Override
   public void createXWidgetLayoutData(DynamicXWidgetLayoutData layoutData, XWidget xWidget, FormToolkit toolkit, Artifact art, XModifiedListener xModListener, boolean isEditable) {
      super.createXWidgetLayoutData(layoutData, xWidget, toolkit, art, xModListener, isEditable);
      // If no tooltip, add global tooltip
      if ((xWidget.getToolTip() == null || xWidget.getToolTip().equals("")) && ATSAttributes.getAtsAttributeByStoreName(layoutData.getLayoutName()) != null && ATSAttributes.getAtsAttributeByStoreName(
            layoutData.getLayoutName()).getDescription() != null && !ATSAttributes.getAtsAttributeByStoreName(
            layoutData.getLayoutName()).getDescription().equals("")) {
         xWidget.setToolTip(ATSAttributes.getAtsAttributeByStoreName(layoutData.getLayoutName()).getDescription());
         layoutData.setToolTip(ATSAttributes.getAtsAttributeByStoreName(layoutData.getLayoutName()).getDescription());
      }
      // Store workAttr in control for use by help
      if (xWidget.getControl() != null) xWidget.getControl().setData(layoutData);

   }

   /*
    * (non-Javadoc)
    * 
    * @see osee.skynet.gui.widgets.workflow.WorkPage#widgetCreating(osee.skynet.gui.widgets.XWidget,
    *      org.eclipse.ui.forms.widgets.FormToolkit, osee.skynet.gui.widgets.workflow.WorkPage,
    *      osee.skynet.gui.widgets.XModifiedListener, boolean)
    */
   @Override
   public void widgetCreating(XWidget xWidget, FormToolkit toolkit, Artifact art, WorkPage page, XModifiedListener xModListener, boolean isEditable) {
      super.widgetCreating(xWidget, toolkit, art, page, xModListener, isEditable);
      // Check extenstion points for page creation
      if (smaMgr != null) {
         for (IAtsStateItem item : smaMgr.getStateItems().getStateItems(page.getId())) {
            Result result = item.xWidgetCreating(xWidget, toolkit, (AtsWorkPage) page, art, xModListener, isEditable);
            if (result.isFalse()) {
               OSEELog.logSevere(AtsPlugin.class, "Error in page creation => " + result.getText(), true);
            }
         }
      }
   }

   public boolean isCompletePage() {
      return getName().equals(DefaultTeamState.Completed.name());
   }

   public boolean isCancelledPage() {
      return getName().equals(DefaultTeamState.Cancelled.name());
   }

   public boolean isDisplayService(WorkPageService service) {
      return true;
   }

   /**
    * @return Returns the fromPages.
    */
   public ArrayList<AtsWorkPage> getFromAtsPages() {
      ArrayList<AtsWorkPage> pages = new ArrayList<AtsWorkPage>();
      for (WorkPage page : super.getFromPages())
         pages.add((AtsWorkPage) page);
      return pages;
   }

   /**
    * @return Returns the toPages.
    */
   public ArrayList<AtsWorkPage> getToAtsPages() {
      ArrayList<AtsWorkPage> pages = new ArrayList<AtsWorkPage>();
      for (WorkPage page : super.getToPages())
         pages.add((AtsWorkPage) page);
      return pages;
   }

   public boolean isEndorsePage() {
      return getName().equals(DefaultTeamState.Endorse.name());
   }

   @Override
   public void processInstructions(Document doc) throws ParserConfigurationException, SAXException, IOException {

      // Process workpage node attributes
      processWorkPageNode(doc);

      NodeList nodes = doc.getElementsByTagName(TaskResolutionOptions.ATS_TASK_OPTIONS_TAG);
      if (nodes.getLength() > 0) {
         taskResolutionOptions = new TaskResolutionOptions();
         taskResolutionOptions.setFromDoc(doc);
      }
      dynamicXWidgetLayout.processInstructions(doc);
   }

   public void processWorkPageNode(Document doc) {
      Element rootElement = doc.getDocumentElement();
      if (rootElement.getNodeName().equals("WorkPage")) {
         for (int x = 0; x < rootElement.getAttributes().getLength(); x++) {
            Node node = rootElement.getAttributes().item(x);
            String nodeName = node.getNodeName();
            if (nodeName.equals(WORKPAGE_STARTPAGE))
               setStartPage(Boolean.parseBoolean(node.getNodeValue()));
            else if (nodeName.equals(WORKPAGE_ATS_REQUIRE_STATE_HOURS_SPENT_PROMPT))
               requireStateHoursSpentPrompt = Boolean.parseBoolean(node.getNodeValue());
            else if (nodeName.equals(WORkPAGE_ATS_FORCE_ASSIGNEES_TO_TEAM_LEADS))
               forceAssigneesToTeamLeads = Boolean.parseBoolean(node.getNodeValue());
            else if (nodeName.equals(WORKPAGE_ATS_ALLOW_CREATE_BRANCH))
               allowCreateBranch = Boolean.parseBoolean(node.getNodeValue());
            else if (nodeName.equals(WORKPAGE_ATS_ALLOW_COMMIT_BRANCH))
               allowCommitBranch = Boolean.parseBoolean(node.getNodeValue());
            else if (nodeName.equals(WORKPAGE_PAGE_ID))
               setId(node.getNodeValue());
            else if (nodeName.equals(WORKPAGE_VALIDATE_PAGE)) {
               setValidatePage(true);
               if (node.getNodeValue().equals("nonblocking"))
                  setValidateReviewBlocking(true);
               else
                  setValidateReviewBlocking(false);
            } else {
               OSEELog.logSevere(AtsPlugin.class,
                     "Unhandled WorkPage attribute \"" + nodeName + "\" for page " + getName(), false);
            }
         }
      } else {
         SkynetGuiPlugin.getLogger().log(Level.SEVERE, "No WorkPage element found for page " + getName());
         throw new IllegalArgumentException("No WorkPage element found for page " + getName());
      }
   }

   /**
    * @return Returns the taskResolutionOptions.
    */
   public TaskResolutionOptions getTaskResDef() {
      return taskResolutionOptions;
   }

   /**
    * @param taskResolutionOptions The taskResolutionOptions to set.
    */
   public void setTaskResDef(TaskResolutionOptions taskResolutionOptions) {
      this.taskResolutionOptions = taskResolutionOptions;
   }

   public boolean isUsingTaskResolutionOptions() {
      return this.taskResolutionOptions != null;
   }

   public boolean isRequireStateHoursSpentPrompt() {
      return requireStateHoursSpentPrompt;
   }

   /**
    * @return Returns the pageType.
    */
   public PageType getPageType() {
      return pageType;
   }

   /**
    * @param pageType The pageType to set.
    */
   public void setPageType(PageType pageType) {
      this.pageType = pageType;
   }

   /**
    * @return the startPage
    */
   public boolean isStartPage() {
      return startPage;
   }

   /**
    * @param startPage the startPage to set
    */
   public void setStartPage(boolean startPage) {
      this.startPage = startPage;
   }

   /**
    * @return the validatePage
    */
   public boolean isValidatePage() {
      return validatePage;
   }

   /**
    * @param validatePage the validatePage to set
    */
   public void setValidatePage(boolean validatePage) {
      this.validatePage = validatePage;
   }

   /**
    * @return the validateReviewBlocking
    */
   public boolean isValidateReviewBlocking() {
      return validateReviewBlocking;
   }

   /**
    * @param validateReviewBlocking the validateReviewBlocking to set
    */
   public void setValidateReviewBlocking(boolean validateReviewBlocking) {
      this.validateReviewBlocking = validateReviewBlocking;
   }

   /**
    * @return the smaMgr
    */
   public SMAManager getSmaMgr() {
      return smaMgr;
   }

   /**
    * @param smaMgr the smaMgr to set
    */
   public void setSmaMgr(SMAManager smaMgr) {
      this.smaMgr = smaMgr;
   }

   /**
    * @return the instructionStr
    */
   public String getInstructionStr() {
      return instructionStr;
   }

   /**
    * @param instructionStr the instructionStr to set
    */
   public void setInstructionStr(String instructionStr) {
      this.instructionStr = instructionStr;
   }

   public String toString() {
      return getPageType() + ": " + getName();
   }

   /**
    * @return the forceAssigneesToTeamLeads
    */
   public boolean isForceAssigneesToTeamLeads() {
      return forceAssigneesToTeamLeads;
   }

   /**
    * @return the allowCreateBranch
    */
   public boolean isAllowCreateBranch() {
      return allowCreateBranch;
   }

   /**
    * @param allowCreateBranch the allowCreateBranch to set
    */
   public void setAllowCreateBranch(boolean allowCreateBranch) {
      this.allowCreateBranch = allowCreateBranch;
   }

   /**
    * @return the allowCommitBranch
    */
   public boolean isAllowCommitBranch() {
      return allowCommitBranch;
   }

   /**
    * @param allowCommitBranch the allowCommitBranch to set
    */
   public void setAllowCommitBranch(boolean allowCommitBranch) {
      this.allowCommitBranch = allowCommitBranch;
   }
}
