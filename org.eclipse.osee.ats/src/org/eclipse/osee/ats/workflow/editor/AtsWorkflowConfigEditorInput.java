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

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.ui.IEditorInput#exists()
    */
   public boolean exists() {
      return true;
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.ui.IEditorInput#getImageDescriptor()
    */
   public ImageDescriptor getImageDescriptor() {
      return null;
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.ui.IEditorInput#getName()
    */
   public String getName() {
      if (workflow == null) {
         return "No Artifact Input Provided";
      }
      return workflow.getName();
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.ui.IEditorInput#getPersistable()
    */
   public IPersistableElement getPersistable() {
      return null;
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.ui.IEditorInput#getToolTipText()
    */
   public String getToolTipText() {
      return getName();
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.core.runtime.IAdaptable#getAdapter(java.lang.Class)
    */
   @SuppressWarnings("unchecked")
   public Object getAdapter(Class adapter) {
      return null;
   }

}
