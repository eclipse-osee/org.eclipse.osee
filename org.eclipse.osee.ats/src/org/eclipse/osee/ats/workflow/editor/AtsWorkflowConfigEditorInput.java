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
package org.eclipse.osee.ats.workflow.editor;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkFlowDefinition;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;

/**
 * @author Donald G. Dunne
 */
public class AtsWorkflowConfigEditorInput implements IEditorInput {
   protected WorkFlowDefinition workflow;

   public AtsWorkflowConfigEditorInput(WorkFlowDefinition workflow) {
      this.workflow = workflow;
   }

   @Override
   public boolean equals(Object obj) {
      if (obj instanceof AtsWorkflowConfigEditorInput) {
         AtsWorkflowConfigEditorInput otherEdInput = (AtsWorkflowConfigEditorInput) obj;
         return workflow.getId().equals(otherEdInput.workflow.getId());
      }
      return false;
   }

   @Override
   public int hashCode() {
      return workflow.getId().hashCode();
   }

   public boolean exists() {
      return true;
   }

   public ImageDescriptor getImageDescriptor() {
      return null;
   }

   public String getName() {
      if (workflow == null) {
         return "No Artifact Input Provided";
      }
      return workflow.getName();
   }

   public IPersistableElement getPersistable() {
      return null;
   }

   public String getToolTipText() {
      return getName();
   }

   @SuppressWarnings("unchecked")
   public Object getAdapter(Class adapter) {
      return null;
   }

}
