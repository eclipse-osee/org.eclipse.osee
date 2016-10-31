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
package org.eclipse.osee.ats.world;

import static org.eclipse.osee.framework.core.enums.PresentationType.GENERALIZED_EDIT;
import static org.eclipse.osee.framework.core.enums.PresentationType.PRODUCE_ATTRIBUTE;
import static org.eclipse.osee.framework.core.enums.PresentationType.SPECIALIZED_EDIT;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.osee.ats.AtsImage;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.editor.renderer.AbstractAtsRenderer;
import org.eclipse.osee.ats.util.AtsUtil;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.core.enums.PresentationType;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.MenuCmdDef;
import org.eclipse.osee.framework.ui.skynet.blam.VariableMap;

/**
 * @author Jeff C. Phillips
 */
public class AtsWorldEditorRenderer extends AbstractAtsRenderer {

   private static final String Option_WORLD_EDITOR = "world.editor.option";

   @Override
   public int getApplicabilityRating(PresentationType presentationType, Artifact artifact, Object... objects) throws OseeCoreException {
      if (artifact.isHistorical() || presentationType.matches(GENERALIZED_EDIT, PRODUCE_ATTRIBUTE)) {
         return NO_MATCH;
      }

      VariableMap options = new VariableMap();
      options.setValues(objects);
      if (artifact.isOfType(AtsArtifactTypes.AtsArtifact)) {
         if (Option_WORLD_EDITOR.equals(options.getString(OPEN_OPTION))) {
            return SPECIALIZED_KEY_MATCH;
         } else {
            return PRESENTATION_SUBTYPE_MATCH;
         }
      }

      if (artifact.isOfType(CoreArtifactTypes.UniversalGroup)) {
         if (artifact.getRelatedArtifactsCount(CoreRelationTypes.Universal_Grouping__Members) == 0) {
            return NO_MATCH;
         }
         for (Artifact childArt : artifact.getRelatedArtifacts(CoreRelationTypes.Universal_Grouping__Members)) {
            if (childArt.isOfType(AtsArtifactTypes.AtsArtifact)) {
               return PRESENTATION_SUBTYPE_MATCH;
            }
         }
      }
      return NO_MATCH;
   }

   @Override
   public void addMenuCommandDefinitions(ArrayList<MenuCmdDef> commands, Artifact artifact) {
      commands.add(new MenuCmdDef(CommandGroup.EDIT, SPECIALIZED_EDIT, "ATS World Editor", AtsImage.GLOBE, OPEN_OPTION,
         Option_WORLD_EDITOR));
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
   public void open(List<Artifact> artifacts, PresentationType presentationType) {
      AtsUtil.openInAtsWorldEditor("ATS", artifacts);
   }
}