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
package org.eclipse.osee.ats.workflow.editor.actions;

import java.util.Iterator;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.workflow.editor.model.WorkPageShape;
import org.eclipse.osee.ats.workflow.editor.model.WorkflowDiagram;
import org.eclipse.osee.ats.workflow.editor.parts.DiagramEditPart;
import org.eclipse.osee.ats.workflow.editor.parts.WorkPageEditPart;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.artifact.editor.ArtifactEditor;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkFlowDefinition;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkItemDefinitionFactory;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkPageDefinition;

/**
 * @author Donald G. Dunne
 */
public class EditAction extends Action {

   public static String ID = "osee.ats.workflowConfigEdit.edit";

   public EditAction() {
      super("Edit");
   }

   @Override
   public String getId() {
      return ID;
   }

   @Override
   public void run() {
      ISelectionProvider selectionProvider =
            AWorkbench.getActivePage().getActivePart().getSite().getSelectionProvider();

      if (selectionProvider != null && selectionProvider.getSelection() instanceof IStructuredSelection) {
         IStructuredSelection structuredSelection = (IStructuredSelection) selectionProvider.getSelection();

         try {
            Iterator<?> i = structuredSelection.iterator();
            while (i.hasNext()) {
               Object obj = i.next();
               if (obj instanceof DiagramEditPart) {
                  if (((DiagramEditPart) obj).getModel() instanceof WorkflowDiagram) {
                     WorkflowDiagram diagram = (WorkflowDiagram) ((DiagramEditPart) obj).getModel();
                     Artifact artifact = null;
                     if (diagram.getWorkFlowDefinition() != null) {
                        WorkFlowDefinition def = diagram.getWorkFlowDefinition();
                        artifact = WorkItemDefinitionFactory.getWorkItemDefinitionArtifact(def.getId());
                     }
                     if (artifact != null) {
                        ArtifactEditor.editArtifact(artifact);
                        return;
                     }
                  }
               }
               if (obj instanceof WorkPageEditPart) {
                  if (((WorkPageEditPart) obj).getModel() instanceof WorkPageShape) {
                     WorkPageShape shape = (WorkPageShape) ((WorkPageEditPart) obj).getModel();
                     Artifact artifact = null;
                     if (shape.getWorkPageDefinition() != null) {
                        WorkPageDefinition def = shape.getWorkPageDefinition();
                        artifact = WorkItemDefinitionFactory.getWorkItemDefinitionArtifact(def.getId());
                     } else {
                        String id = shape.getId();
                        artifact = WorkItemDefinitionFactory.getWorkItemDefinitionArtifact(id);
                        ArtifactEditor.editArtifact(artifact);
                        return;
                     }
                     if (artifact != null) {
                        ArtifactEditor.editArtifact(artifact);
                        return;
                     }
                  }
               }
            }
            AWorkbench.popup("ERROR", "Can't locate corresponding artifact");
         } catch (Exception ex) {
            OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, ex);
         }
      }
   }

}
