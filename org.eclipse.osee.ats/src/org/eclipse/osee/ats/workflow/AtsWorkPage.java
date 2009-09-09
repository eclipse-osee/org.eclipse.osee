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

import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.artifact.ATSAttributes;
import org.eclipse.osee.ats.artifact.TeamWorkFlowArtifact.DefaultTeamState;
import org.eclipse.osee.ats.editor.SMAManager;
import org.eclipse.osee.ats.editor.stateItem.IAtsStateItem;
import org.eclipse.osee.ats.util.widgets.dialog.TaskResolutionOptionRule;
import org.eclipse.osee.ats.workflow.item.AtsWorkDefinitions;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.plugin.util.Result;
import org.eclipse.osee.framework.ui.skynet.widgets.XModifiedListener;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.DynamicXWidgetLayoutData;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.IXWidgetOptionResolver;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkFlowDefinition;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkPage;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkPageDefinition;
import org.eclipse.ui.forms.widgets.FormToolkit;

/**
 * @author Donald G. Dunne
 */
public class AtsWorkPage extends WorkPage {

   protected TaskResolutionOptionRule taskResolutionOptions;
   private SMAManager smaMgr;

   public AtsWorkPage(WorkFlowDefinition workFlowDefinition, WorkPageDefinition workPageDefinition, String xWidgetsXml, IXWidgetOptionResolver optionResolver) {
      super(workFlowDefinition, workPageDefinition, xWidgetsXml, optionResolver, null);
   }

   public AtsWorkPage(IXWidgetOptionResolver optionResolver) {
      this(null, null, null, optionResolver);
   }

   public boolean isCurrentState(SMAManager smaMgr) throws OseeCoreException {
      return smaMgr.isCurrentState(getName());
   }

   public boolean isCurrentNonCompleteCancelledState(SMAManager smaMgr) throws OseeCoreException {
      return smaMgr.isCurrentState(getName()) && !isCompleteCancelledState();
   }

   public boolean isCompleteCancelledState() {
      return isCancelledPage() || isCompletePage();
   }

   @Override
   public void widgetCreated(XWidget xWidget, FormToolkit toolkit, Artifact art, WorkPage page, XModifiedListener xModListener, boolean isEditable) throws OseeCoreException {
      super.widgetCreated(xWidget, toolkit, art, page, xModListener, isEditable);
      // Check extenstion points for page creation
      if (smaMgr != null) {
         for (IAtsStateItem item : smaMgr.getStateItems().getStateItems(page.getId())) {
            item.xWidgetCreated(xWidget, toolkit, (AtsWorkPage) page, art, xModListener, isEditable);
         }
      }
   }

   @Override
   public void createXWidgetLayoutData(DynamicXWidgetLayoutData layoutData, XWidget xWidget, FormToolkit toolkit, Artifact art, XModifiedListener xModListener, boolean isEditable) throws OseeCoreException {
      super.createXWidgetLayoutData(layoutData, xWidget, toolkit, art, xModListener, isEditable);
      // If no tooltip, add global tooltip
      if ((xWidget.getToolTip() == null || xWidget.getToolTip().equals("")) && ATSAttributes.getAtsAttributeByStoreName(layoutData.getStorageName()) != null && ATSAttributes.getAtsAttributeByStoreName(
            layoutData.getStorageName()).getDescription() != null && !ATSAttributes.getAtsAttributeByStoreName(
            layoutData.getStorageName()).getDescription().equals("")) {
         xWidget.setToolTip(ATSAttributes.getAtsAttributeByStoreName(layoutData.getStorageName()).getDescription());
         layoutData.setToolTip(ATSAttributes.getAtsAttributeByStoreName(layoutData.getStorageName()).getDescription());
      }
      // Store workAttr in control for use by help
      if (xWidget.getControl() != null) xWidget.getControl().setData(layoutData);

   }

   @Override
   public void widgetCreating(XWidget xWidget, FormToolkit toolkit, Artifact art, WorkPage page, XModifiedListener xModListener, boolean isEditable) throws OseeCoreException {
      super.widgetCreating(xWidget, toolkit, art, page, xModListener, isEditable);
      // Check extenstion points for page creation
      if (smaMgr != null) {
         for (IAtsStateItem item : smaMgr.getStateItems().getStateItems(page.getId())) {
            Result result = item.xWidgetCreating(xWidget, toolkit, (AtsWorkPage) page, art, xModListener, isEditable);
            if (result.isFalse()) {
               OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, "Error in page creation => " + result.getText());
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

   public boolean isEndorsePage() {
      return getName().equals(DefaultTeamState.Endorse.name());
   }

   /**
    * @return Returns the taskResolutionOptions.
    */
   public TaskResolutionOptionRule getTaskResDef() {
      return taskResolutionOptions;
   }

   /**
    * @param taskResolutionOptions The taskResolutionOptions to set.
    */
   public void setTaskResDef(TaskResolutionOptionRule taskResolutionOptions) {
      this.taskResolutionOptions = taskResolutionOptions;
   }

   public boolean isUsingTaskResolutionOptions() {
      return this.taskResolutionOptions != null;
   }

   /**
    * @return the startPage
    */
   public boolean isStartPage() throws OseeCoreException {
      return workFlowDefinition.getStartPage().getId().equals(getId());
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
    * @return the validatePage
    */
   public boolean isValidatePage() throws OseeCoreException {
      return AtsWorkDefinitions.isValidatePage(workPageDefinition);
   }

   /**
    * @return the validateReviewBlocking
    */
   public boolean isValidateReviewBlocking() throws OseeCoreException {
      return AtsWorkDefinitions.isValidateReviewBlocking(workPageDefinition);
   }

   /**
    * @return the forceAssigneesToTeamLeads
    */
   public boolean isForceAssigneesToTeamLeads() throws OseeCoreException {
      return AtsWorkDefinitions.isForceAssigneesToTeamLeads(workPageDefinition);
   }

   /**
    * @return the forceAssigneesToTeamLeads
    */
   public boolean isRequireStateHoursSpentPrompt() throws OseeCoreException {
      return AtsWorkDefinitions.isRequireStateHoursSpentPrompt(workPageDefinition);
   }

   /**
    * @return the allowCreateBranch
    */
   public boolean isAllowTransitionWithWorkingBranch() throws OseeCoreException {
      return AtsWorkDefinitions.isAllowTransitionWithWorkingBranch(workPageDefinition);
   }

   /**
    * @return the allowCreateBranch
    */
   public boolean isAllowCreateBranch() throws OseeCoreException {
      return AtsWorkDefinitions.isAllowCreateBranch(workPageDefinition);
   }

   /**
    * @return the allowCommitBranch
    */
   public boolean isAllowCommitBranch() throws OseeCoreException {
      return AtsWorkDefinitions.isAllowCommitBranch(workPageDefinition);
   }

}
