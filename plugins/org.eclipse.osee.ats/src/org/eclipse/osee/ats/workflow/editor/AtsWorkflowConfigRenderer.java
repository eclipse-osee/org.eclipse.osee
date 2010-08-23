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

import static org.eclipse.osee.framework.ui.skynet.render.PresentationType.GENERALIZED_EDIT;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.osee.ats.AtsImage;
import org.eclipse.osee.ats.internal.AtsPlugin;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.render.DefaultArtifactRenderer;
import org.eclipse.osee.framework.ui.skynet.render.PresentationType;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkFlowDefinition;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.osee.framework.ui.swt.ImageManager;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IEditorInput;

/**
 * @author Donald G. Dunne
 */
public class AtsWorkflowConfigRenderer extends DefaultArtifactRenderer {

   private static final String COMMAND_ID = "org.eclipse.osee.framework.ui.skynet.atsworkflowconfigeditor.command";

   @Override
   public Image getImage(Artifact artifact) {
      return ImageManager.getImage(AtsImage.WORKFLOW_CONFIG);
   }

   @Override
   public String getName() {
      return "ATS Workflow Config Editor";
   }

   @Override
   public AtsWorkflowConfigRenderer newInstance() {
      return new AtsWorkflowConfigRenderer();
   }

   @Override
   public int getApplicabilityRating(PresentationType presentationType, Artifact artifact) {
      if (artifact.isOfType(CoreArtifactTypes.WorkFlowDefinition) && presentationType != GENERALIZED_EDIT) {
         return PRESENTATION_SUBTYPE_MATCH;
      }
      return NO_MATCH;
   }

   @Override
   public List<String> getCommandId(PresentationType presentationType) {
      ArrayList<String> commandIds = new ArrayList<String>(1);
      if (presentationType == PresentationType.SPECIALIZED_EDIT) {
         commandIds.add(COMMAND_ID);
      }
      return commandIds;
   }

   @Override
   public void open(final List<Artifact> artifacts, PresentationType presentationType) {
      Displays.ensureInDisplayThread(new Runnable() {
         @Override
         public void run() {
            for (Artifact artifact : artifacts) {
               try {
                  IEditorInput input = new AtsWorkflowConfigEditorInput(new WorkFlowDefinition(artifact));
                  AWorkbench.getActivePage().openEditor(input, AtsWorkflowConfigEditor.EDITOR_ID);
               } catch (CoreException ex) {
                  OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, ex);
               }
            }
         }
      });
   }
}