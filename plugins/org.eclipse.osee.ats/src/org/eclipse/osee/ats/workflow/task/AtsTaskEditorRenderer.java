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
package org.eclipse.osee.ats.workflow.task;

import static org.eclipse.osee.framework.core.enums.PresentationType.GENERALIZED_EDIT;
import static org.eclipse.osee.framework.core.enums.PresentationType.PRODUCE_ATTRIBUTE;
import static org.eclipse.osee.framework.core.enums.PresentationType.SPECIALIZED_EDIT;
import static org.eclipse.osee.framework.core.util.RendererOption.OPEN_OPTION;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.eclipse.osee.ats.AtsImage;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.util.AtsEditors;
import org.eclipse.osee.framework.core.enums.CommandGroup;
import org.eclipse.osee.framework.core.enums.PresentationType;
import org.eclipse.osee.framework.core.util.RendererOption;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.MenuCmdDef;
import org.eclipse.osee.framework.ui.skynet.render.DefaultArtifactRenderer;

/**
 * @author Jeff C. Phillips
 */
public class AtsTaskEditorRenderer extends DefaultArtifactRenderer {

   public AtsTaskEditorRenderer(Map<RendererOption, Object> rendererOptions) {
      super(rendererOptions);
   }

   public AtsTaskEditorRenderer() {
      this(new HashMap<RendererOption, Object>());
   }

   private static final String Option_TASK_WORLD_EDITOR = "task.world.editor.option";

   @Override
   public String getName() {
      return "ATS Task Editor";
   }

   @Override
   public int getApplicabilityRating(PresentationType presentationType, Artifact artifact, Map<RendererOption, Object> rendererOptions) {
      if (artifact.isOfType(AtsArtifactTypes.Task) && !artifact.isHistorical() && !presentationType.matches(
         GENERALIZED_EDIT, PRODUCE_ATTRIBUTE)) {
         if (Option_TASK_WORLD_EDITOR.equals(rendererOptions.get(OPEN_OPTION))) {
            return SPECIALIZED_KEY_MATCH;
         } else {
            return PRESENTATION_SUBTYPE_MATCH;
         }
      }
      return NO_MATCH;
   }

   @Override
   public void addMenuCommandDefinitions(ArrayList<MenuCmdDef> commands, Artifact artifact) {
      commands.add(new MenuCmdDef(CommandGroup.EDIT, SPECIALIZED_EDIT, "ATS Task Editor", AtsImage.TASK,
         OPEN_OPTION.getKey(), Option_TASK_WORLD_EDITOR));
   }

   @Override
   public AtsTaskEditorRenderer newInstance() {
      return new AtsTaskEditorRenderer();
   }

   @Override
   public AtsTaskEditorRenderer newInstance(Map<RendererOption, Object> rendererOptions) {
      return new AtsTaskEditorRenderer(rendererOptions);
   }

   @Override
   public void open(List<Artifact> artifacts, PresentationType presentationType) {
      AtsEditors.openInAtsTaskEditor("Tasks", artifacts);
   }
}
