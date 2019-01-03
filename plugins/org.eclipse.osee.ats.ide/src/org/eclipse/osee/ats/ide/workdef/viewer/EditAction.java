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
package org.eclipse.osee.ats.ide.workdef.viewer;

import java.util.Iterator;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.ats.ide.internal.AtsClientService;
import org.eclipse.osee.ats.ide.workdef.viewer.model.StateDefShape;
import org.eclipse.osee.ats.ide.workdef.viewer.model.WorkDefinitionDiagram;
import org.eclipse.osee.ats.ide.workdef.viewer.parts.DiagramEditPart;
import org.eclipse.osee.ats.ide.workdef.viewer.parts.StateDefEditPart;
import org.eclipse.osee.framework.core.enums.PresentationType;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.render.RendererManager;
import org.eclipse.ui.PlatformUI;

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
      ISelection selection = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getSelectionService().getSelection();
      if (selection instanceof IStructuredSelection) {
         IStructuredSelection structuredSelection = (IStructuredSelection) selection;

         try {
            Iterator<?> i = structuredSelection.iterator();
            String artifactName = null;
            if (i.hasNext()) {
               Object obj = i.next();
               if (obj instanceof DiagramEditPart) {
                  if (((DiagramEditPart) obj).getModel() instanceof WorkDefinitionDiagram) {
                     WorkDefinitionDiagram diagram = (WorkDefinitionDiagram) ((DiagramEditPart) obj).getModel();
                     artifactName = diagram.getWorkDefinition().getName();
                  }
               }
               if (obj instanceof StateDefEditPart) {
                  if (((StateDefEditPart) obj).getModel() instanceof StateDefShape) {
                     StateDefShape shape = (StateDefShape) ((StateDefEditPart) obj).getModel();
                     artifactName = shape.getStateDefinition().getWorkDefinition().getName();
                  }
               }
            }
            if (Strings.isValid(artifactName)) {
               Artifact artifact = ArtifactQuery.getArtifactFromTypeAndName(AtsArtifactTypes.WorkDefinition,
                  artifactName, AtsClientService.get().getAtsBranch());
               if (artifact != null) {
                  RendererManager.open(artifact, PresentationType.DEFAULT_OPEN);
                  return;
               }
            }
            AWorkbench.popup("ERROR", "Can't locate corresponding artifact");
         } catch (Exception ex) {
            OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
         }
      }
   }
}
