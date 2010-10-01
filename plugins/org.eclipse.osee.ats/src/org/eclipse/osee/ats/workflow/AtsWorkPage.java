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

import org.eclipse.osee.ats.artifact.ATSAttributes;
import org.eclipse.osee.ats.artifact.AbstractWorkflowArtifact;
import org.eclipse.osee.ats.editor.stateItem.AtsStateItemManager;
import org.eclipse.osee.ats.editor.stateItem.IAtsStateItem;
import org.eclipse.osee.ats.internal.AtsPlugin;
import org.eclipse.osee.ats.util.DefaultTeamState;
import org.eclipse.osee.ats.util.widgets.dialog.TaskResolutionOptionRule;
import org.eclipse.osee.ats.workflow.item.AtsWorkDefinitions;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.plugin.util.Result;
import org.eclipse.osee.framework.ui.skynet.widgets.XModifiedListener;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.DynamicXWidgetLayoutData;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.IXWidgetOptionResolver;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkFlowDefinition;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkItemDefinition;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkPage;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkPageDefinition;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkWidgetDefinition;
import org.eclipse.ui.forms.widgets.FormToolkit;

/**
 * @author Donald G. Dunne
 */
public class AtsWorkPage extends WorkPage {

   protected TaskResolutionOptionRule taskResolutionOptions;
   private AbstractWorkflowArtifact sma;

   public AtsWorkPage(WorkFlowDefinition workFlowDefinition, WorkPageDefinition workPageDefinition, String xWidgetsXml, IXWidgetOptionResolver optionResolver) {
      super(workFlowDefinition, workPageDefinition, xWidgetsXml, optionResolver, null);
   }

   public AtsWorkPage(IXWidgetOptionResolver optionResolver) {
      this(null, null, null, optionResolver);
   }

   public boolean isCurrentState(AbstractWorkflowArtifact sma) {
      return sma.isCurrentState(getName());
   }

   public boolean isCurrentNonCompleteCancelledState(AbstractWorkflowArtifact sma) {
      return sma.isCurrentState(getName()) && !isCompleteCancelledState();
   }

   public boolean isCompleteCancelledState() {
      return isCancelledPage() || isCompletePage();
   }

   @Override
   public void widgetCreated(XWidget xWidget, FormToolkit toolkit, Artifact art, WorkPage page, XModifiedListener xModListener, boolean isEditable) throws OseeCoreException {
      super.widgetCreated(xWidget, toolkit, art, page, xModListener, isEditable);
      // Check extenstion points for page creation
      if (sma != null) {
         for (IAtsStateItem item : AtsStateItemManager.getStateItems(page.getId())) {
            item.xWidgetCreated(xWidget, toolkit, (AtsWorkPage) page, art, xModListener, isEditable);
         }
      }
   }

   @Override
   public void createXWidgetLayoutData(DynamicXWidgetLayoutData layoutData, XWidget xWidget, FormToolkit toolkit, Artifact art, XModifiedListener xModListener, boolean isEditable) throws OseeCoreException {
      super.createXWidgetLayoutData(layoutData, xWidget, toolkit, art, xModListener, isEditable);

      // If no tool tip, add global tool tip
      if (!Strings.isValid(xWidget.getToolTip())) {
         ATSAttributes atsAttribute = ATSAttributes.getAtsAttributeByStoreName(layoutData.getId());
         if (atsAttribute != null && Strings.isValid(atsAttribute.getDescription())) {
            xWidget.setToolTip(atsAttribute.getDescription());
            layoutData.setToolTip(atsAttribute.getDescription());
         }
      }
      // Store workAttr in control for use by help
      if (xWidget.getControl() != null) {
         xWidget.getControl().setData(layoutData);
      }

   }

   @Override
   public void widgetCreating(XWidget xWidget, FormToolkit toolkit, Artifact art, WorkPage page, XModifiedListener xModListener, boolean isEditable) throws OseeCoreException {
      super.widgetCreating(xWidget, toolkit, art, page, xModListener, isEditable);
      // Check extenstion points for page creation
      if (sma != null) {
         for (IAtsStateItem item : AtsStateItemManager.getStateItems(page.getId())) {
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

   public TaskResolutionOptionRule getTaskResDef() {
      return taskResolutionOptions;
   }

   public void setTaskResDef(TaskResolutionOptionRule taskResolutionOptions) {
      this.taskResolutionOptions = taskResolutionOptions;
   }

   public boolean isUsingTaskResolutionOptions() {
      return this.taskResolutionOptions != null;
   }

   public boolean isStartPage() throws OseeCoreException {
      return workFlowDefinition.getStartPage().getId().equals(getId());
   }

   public void generateLayoutDatas(AbstractWorkflowArtifact sma) throws OseeCoreException {
      this.sma = sma;
      // Add static layoutDatas to atsWorkPage
      for (WorkItemDefinition workItemDefinition : getWorkPageDefinition().getWorkItems(true)) {
         if (workItemDefinition instanceof WorkWidgetDefinition) {
            DynamicXWidgetLayoutData data = ((WorkWidgetDefinition) workItemDefinition).get();
            data.setDynamicXWidgetLayout(getDynamicXWidgetLayout());
            data.setArtifact(sma);
            addLayoutData(data);
         }
      }
   }

   public AbstractWorkflowArtifact getSma() {
      return sma;
   }

   public void setsma(AbstractWorkflowArtifact sma) {
      this.sma = sma;
   }

   public boolean isValidatePage() throws OseeCoreException {
      return AtsWorkDefinitions.isValidatePage(workPageDefinition);
   }

   public boolean isValidateReviewBlocking() throws OseeCoreException {
      return AtsWorkDefinitions.isValidateReviewBlocking(workPageDefinition);
   }

   public boolean isForceAssigneesToTeamLeads() throws OseeCoreException {
      return AtsWorkDefinitions.isForceAssigneesToTeamLeads(workPageDefinition);
   }

   public boolean isRequireStateHoursSpentPrompt() throws OseeCoreException {
      return AtsWorkDefinitions.isRequireStateHoursSpentPrompt(workPageDefinition);
   }

   public boolean isAllowTransitionWithWorkingBranch() throws OseeCoreException {
      return AtsWorkDefinitions.isAllowTransitionWithWorkingBranch(workPageDefinition);
   }

   public boolean isAllowCreateBranch() throws OseeCoreException {
      return AtsWorkDefinitions.isAllowCreateBranch(workPageDefinition);
   }

   public boolean isAllowCommitBranch() throws OseeCoreException {
      return AtsWorkDefinitions.isAllowCommitBranch(workPageDefinition);
   }

   @Override
   public boolean equals(Object obj) {
      return super.equals(obj);
   }

   @Override
   public int hashCode() {
      return super.hashCode();
   }

}
