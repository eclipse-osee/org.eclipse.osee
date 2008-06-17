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

package org.eclipse.osee.framework.ui.skynet.blam.operation;

import java.sql.SQLException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.exception.OseeCoreException;
import org.eclipse.osee.framework.ui.skynet.blam.BlamVariableMap;
import org.eclipse.osee.framework.ui.skynet.blam.WorkflowEditor;
import org.eclipse.osee.framework.ui.skynet.widgets.XModifiedListener;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.DynamicXWidgetLayout;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.DynamicXWidgetLayoutData;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.IDynamicWidgetLayoutListener;
import org.eclipse.ui.forms.widgets.FormToolkit;

/**
 * @author Ryan D. Brooks
 */
public abstract class AbstractBlam implements BlamOperation, IDynamicWidgetLayoutListener {
   private WorkflowEditor workflowEditor;

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.blam.operation.BlamOperation#wrapOperationForBranch()
    */
   public Branch wrapOperationForBranch(BlamVariableMap variableMap) {
      return null;
   }

   /*
    * (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.blam.operation.BlamOperation#getXWidgetXml()
    */
   public String getXWidgetsXml() {
      return branchXWidgetXml;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.blam.operation.BlamOperation#getDescriptionUsage()
    */
   public String getDescriptionUsage() {
      return "Select parameters below and click the play button at the top right.";
   }

   public String getName() {
      return getClassSimpleName();
   }

   public String getClassSimpleName() {
      return getClass().getSimpleName();
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.blam.operation.BlamOperation#setOverpages(org.eclipse.osee.framework.ui.skynet.blam.OverviewPage)
    */
   @Override
   public void setWorkflowEditor(WorkflowEditor workflowEditor) {
      this.workflowEditor = workflowEditor;
   }

   public void appendResultLine(String output) {
      workflowEditor.appendOuputLine(output);
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.widgets.workflow.IDynamicWidgetLayoutListener#createXWidgetLayoutData(org.eclipse.osee.framework.ui.skynet.widgets.workflow.DynamicXWidgetLayoutData, org.eclipse.osee.framework.ui.skynet.widgets.XWidget, org.eclipse.ui.forms.widgets.FormToolkit, org.eclipse.osee.framework.skynet.core.artifact.Artifact, org.eclipse.osee.framework.ui.skynet.widgets.XModifiedListener, boolean)
    */
   @Override
   public void createXWidgetLayoutData(DynamicXWidgetLayoutData layoutData, XWidget xWidget, FormToolkit toolkit, Artifact art, XModifiedListener modListener, boolean isEditable) throws OseeCoreException, SQLException {
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.widgets.workflow.IDynamicWidgetLayoutListener#widgetCreated(org.eclipse.osee.framework.ui.skynet.widgets.XWidget, org.eclipse.ui.forms.widgets.FormToolkit, org.eclipse.osee.framework.skynet.core.artifact.Artifact, org.eclipse.osee.framework.ui.skynet.widgets.workflow.DynamicXWidgetLayout, org.eclipse.osee.framework.ui.skynet.widgets.XModifiedListener, boolean)
    */
   @Override
   public void widgetCreated(XWidget xWidget, FormToolkit toolkit, Artifact art, DynamicXWidgetLayout dynamicXWidgetLayout, XModifiedListener modListener, boolean isEditable) throws OseeCoreException, SQLException {
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.widgets.workflow.IDynamicWidgetLayoutListener#widgetCreating(org.eclipse.osee.framework.ui.skynet.widgets.XWidget, org.eclipse.ui.forms.widgets.FormToolkit, org.eclipse.osee.framework.skynet.core.artifact.Artifact, org.eclipse.osee.framework.ui.skynet.widgets.workflow.DynamicXWidgetLayout, org.eclipse.osee.framework.ui.skynet.widgets.XModifiedListener, boolean)
    */
   @Override
   public void widgetCreating(XWidget xWidget, FormToolkit toolkit, Artifact art, DynamicXWidgetLayout dynamicXWidgetLayout, XModifiedListener modListener, boolean isEditable) throws OseeCoreException, SQLException {
   }

}