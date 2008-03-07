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

import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.ui.skynet.blam.BlamVariableMap;
import org.eclipse.osee.framework.ui.skynet.blam.WorkflowEditor;

/**
 * @author Ryan D. Brooks
 */
public abstract class AbstractBlam implements BlamOperation {
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
}