/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.ats.ide.editor.renderer;

import static org.eclipse.osee.framework.core.enums.PresentationType.GENERALIZED_EDIT;
import static org.eclipse.osee.framework.core.enums.PresentationType.PRODUCE_ATTRIBUTE;
import static org.eclipse.osee.framework.core.enums.PresentationType.SPECIALIZED_EDIT;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.util.AtsImage;
import org.eclipse.osee.ats.ide.AtsOpenOption;
import org.eclipse.osee.ats.ide.util.AtsEditors;
import org.eclipse.osee.framework.core.enums.CommandGroup;
import org.eclipse.osee.framework.core.enums.PresentationType;
import org.eclipse.osee.framework.core.publishing.RendererMap;
import org.eclipse.osee.framework.core.publishing.RendererOption;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.MenuCmdDef;
import org.eclipse.osee.framework.ui.skynet.render.DefaultArtifactRenderer;
import org.eclipse.osee.framework.ui.skynet.render.RendererManager;
import org.eclipse.osee.framework.ui.swt.ImageManager;

/**
 * @author Ryan D. Brooks
 */
public class AtsWfeRenderer extends DefaultArtifactRenderer {

   private static final String Option_WORKFLOW_EDITOR = "workflow.editor.option";

   public AtsWfeRenderer(RendererMap rendererOptions) {
      super(rendererOptions);
   }

   public AtsWfeRenderer() {
      super();
   }

   @Override
   public void addMenuCommandDefinitions(ArrayList<MenuCmdDef> commands, Artifact artifact) {
      commands.add(new MenuCmdDef(CommandGroup.EDIT, SPECIALIZED_EDIT, "ATS Workflow Editor",
         ImageManager.create(AtsImage.ACTION), RendererOption.OPEN_OPTION.getKey(), Option_WORKFLOW_EDITOR));
   }

   @Override
   public String getName() {
      return "ATS Workflow Editor";
   }

   @Override
   public AtsWfeRenderer newInstance() {
      return new AtsWfeRenderer();
   }

   @Override
   public AtsWfeRenderer newInstance(RendererMap rendererOptions) {
      return new AtsWfeRenderer(rendererOptions);
   }

   @Override
   public int getApplicabilityRating(PresentationType presentationType, Artifact artifact, RendererMap rendererOptions) {

      if (!artifact.isHistorical() && !presentationType.matches(GENERALIZED_EDIT,
         PRODUCE_ATTRIBUTE) && artifact.isOfType(AtsArtifactTypes.AtsArtifact)) {

         if (Option_WORKFLOW_EDITOR.equals(rendererOptions.getRendererOptionValue(RendererOption.OPEN_OPTION)) && //
            !UserManager.getBooleanSetting(UserManager.DOUBLE_CLICK_SETTING_KEY_EDIT) && //
            !RendererManager.isDefaultArtifactEditor()) {
            return SPECIALIZED_MATCH;
         } else {
            return PRESENTATION_SUBTYPE_MATCH;
         }

      }
      return NO_MATCH;
   }

   @Override
   public void open(List<Artifact> artifacts, PresentationType presentationType) {
      for (Artifact artifact : artifacts) {
         AtsEditors.openATSAction(artifact, AtsOpenOption.OpenOneOrPopupSelect);
      }
   }
}