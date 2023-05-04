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

package org.eclipse.osee.ats.ide.world;

import static org.eclipse.osee.framework.core.enums.PresentationType.GENERALIZED_EDIT;
import static org.eclipse.osee.framework.core.enums.PresentationType.PRODUCE_ATTRIBUTE;
import static org.eclipse.osee.framework.core.enums.PresentationType.SPECIALIZED_EDIT;
import static org.eclipse.osee.framework.core.publishing.RendererOption.OPEN_OPTION;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.util.AtsImage;
import org.eclipse.osee.ats.ide.util.AtsEditors;
import org.eclipse.osee.framework.core.enums.CommandGroup;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.core.enums.PresentationType;
import org.eclipse.osee.framework.core.publishing.RendererMap;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.MenuCmdDef;
import org.eclipse.osee.framework.ui.skynet.render.DefaultArtifactRenderer;
import org.eclipse.osee.framework.ui.swt.ImageManager;

/**
 * @author Jeff C. Phillips
 */
public class AtsWorldEditorRenderer extends DefaultArtifactRenderer {

   private static final String Option_WORLD_EDITOR = "world.editor.option";

   public AtsWorldEditorRenderer(RendererMap rendererOptions) {
      super(rendererOptions);
   }

   public AtsWorldEditorRenderer() {
      this(null);
   }

   @Override
   public int getApplicabilityRating(PresentationType presentationType, Artifact artifact,
      RendererMap rendererOptions) {
      if (artifact.isHistorical() || presentationType.matches(GENERALIZED_EDIT, PRODUCE_ATTRIBUTE)) {
         return NO_MATCH;
      }

      if (artifact.isOfType(AtsArtifactTypes.AtsArtifact)) {
         if (Option_WORLD_EDITOR.equals(rendererOptions.getRendererOptionValue(OPEN_OPTION))) {
            return SPECIALIZED_KEY_MATCH;
         } else {
            return PRESENTATION_SUBTYPE_MATCH;
         }
      }

      if (artifact.isOfType(CoreArtifactTypes.UniversalGroup)) {
         if (artifact.getRelatedArtifactsCount(CoreRelationTypes.UniversalGrouping_Members) == 0) {
            return NO_MATCH;
         }
         for (Artifact childArt : artifact.getRelatedArtifacts(CoreRelationTypes.UniversalGrouping_Members)) {
            if (childArt.isOfType(AtsArtifactTypes.AtsArtifact)) {
               return PRESENTATION_SUBTYPE_MATCH;
            }
         }
      }
      return NO_MATCH;
   }

   @Override
   public void addMenuCommandDefinitions(ArrayList<MenuCmdDef> commands, Artifact artifact) {
      commands.add(new MenuCmdDef(CommandGroup.EDIT, SPECIALIZED_EDIT, "ATS World Editor",
         ImageManager.create(AtsImage.GLOBE), OPEN_OPTION.getKey(), Option_WORLD_EDITOR));
   }

   @Override
   public String getName() {
      return "ATS World Editor";
   }

   @Override
   public AtsWorldEditorRenderer newInstance() {
      return new AtsWorldEditorRenderer();
   }

   @Override
   public AtsWorldEditorRenderer newInstance(RendererMap rendererOptions) {
      return new AtsWorldEditorRenderer(rendererOptions);
   }

   @Override
   public void open(List<Artifact> artifacts, PresentationType presentationType) {
      AtsEditors.openInAtsWorldEditor("ATS", artifacts);
   }
}