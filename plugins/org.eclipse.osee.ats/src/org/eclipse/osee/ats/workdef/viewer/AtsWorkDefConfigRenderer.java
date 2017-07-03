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
package org.eclipse.osee.ats.workdef.viewer;

import static org.eclipse.osee.framework.core.enums.PresentationType.GENERALIZED_EDIT;
import static org.eclipse.osee.framework.core.enums.PresentationType.SPECIALIZED_EDIT;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.eclipse.osee.ats.AtsImage;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.workdef.IAtsWorkDefinition;
import org.eclipse.osee.ats.internal.Activator;
import org.eclipse.osee.ats.internal.AtsClientService;
import org.eclipse.osee.framework.core.enums.PresentationType;
import org.eclipse.osee.framework.core.util.RendererOption;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.MenuCmdDef;
import org.eclipse.osee.framework.ui.skynet.render.DefaultArtifactRenderer;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.osee.framework.ui.swt.ImageManager;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IEditorInput;

/**
 * @author Donald G. Dunne
 */
public class AtsWorkDefConfigRenderer extends DefaultArtifactRenderer {

   public AtsWorkDefConfigRenderer(Map<RendererOption, Object> rendererOptions) {
      super(rendererOptions);
   }

   public AtsWorkDefConfigRenderer() {
      this(new HashMap<RendererOption, Object>());
   }

   public Image getImage(Artifact artifact) {
      return ImageManager.getImage(AtsImage.WORKFLOW_CONFIG);
   }

   @Override
   public void addMenuCommandDefinitions(ArrayList<MenuCmdDef> commands, Artifact artifact) {
      commands.add(
         new MenuCmdDef(CommandGroup.EDIT, SPECIALIZED_EDIT, "ATS Work Definition Viewer", AtsImage.WORK_DEFINITION));
   }

   @Override
   public String getName() {
      return "ATS Work Definition Viewer";
   }

   @Override
   public AtsWorkDefConfigRenderer newInstance() {
      return new AtsWorkDefConfigRenderer();
   }

   @Override
   public AtsWorkDefConfigRenderer newInstance(Map<RendererOption, Object> rendererOptions) {
      return new AtsWorkDefConfigRenderer(rendererOptions);
   }

   @Override
   public int getApplicabilityRating(PresentationType presentationType, Artifact artifact, Map<RendererOption, Object> rendererOptions) {
      if (artifact.isOfType(AtsArtifactTypes.WorkDefinition) && presentationType != GENERALIZED_EDIT) {
         return ARTIFACT_TYPE_MATCH;
      }
      return NO_MATCH;
   }

   @Override
   public void open(final List<Artifact> artifacts, PresentationType presentationType) {
      Displays.ensureInDisplayThread(new Runnable() {
         @Override
         public void run() {
            for (Artifact artifact : artifacts) {
               try {
                  IAtsWorkDefinition workDef =
                     AtsClientService.get().getWorkDefinitionService().getWorkDefinition(artifact.getName());
                  if (workDef != null) {
                     IEditorInput input = new AtsWorkDefConfigEditorInput(workDef);
                     AWorkbench.getActivePage().openEditor(input, AtsWorkDefConfigEditor.EDITOR_ID);
                  } else {
                     AWorkbench.popup("No Work Definition matches artifact " + artifact.toStringWithId());
                  }
               } catch (Exception ex) {
                  OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
               }
            }
         }
      });
   }
}