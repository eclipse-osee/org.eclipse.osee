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

import static org.eclipse.osee.framework.ui.skynet.render.PresentationType.GENERALIZED_EDIT;
import static org.eclipse.osee.framework.ui.skynet.render.PresentationType.PRODUCE_ATTRIBUTE;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.core.commands.Command;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.osee.ats.AtsImage;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.editor.renderer.AbstractAtsRenderer;
import org.eclipse.osee.ats.util.AtsUtil;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.types.IArtifact;
import org.eclipse.osee.framework.ui.skynet.render.PresentationType;
import org.eclipse.osee.framework.ui.swt.ImageManager;

/**
 * @author Jeff C. Phillips
 */
public class AtsWorldEditorRenderer extends AbstractAtsRenderer {
   private static final String COMMAND_ID = "org.eclipse.osee.framework.ui.skynet.atsworldeditor.command";

   @Override
   public int getApplicabilityRating(PresentationType presentationType, IArtifact artifact) throws OseeCoreException {
      Artifact aArtifact = artifact.getFullArtifact();
      if (aArtifact.isHistorical() || presentationType.matches(GENERALIZED_EDIT, PRODUCE_ATTRIBUTE)) {
         return NO_MATCH;
      }
      if (aArtifact.isOfType(AtsArtifactTypes.AtsArtifact)) {
         return PRESENTATION_SUBTYPE_MATCH;
      }
      if (aArtifact.isOfType(CoreArtifactTypes.UniversalGroup)) {
         if (aArtifact.getRelatedArtifactsCount(CoreRelationTypes.Universal_Grouping__Members) == 0) {
            return NO_MATCH;
         }
         for (Artifact childArt : aArtifact.getRelatedArtifacts(CoreRelationTypes.Universal_Grouping__Members)) {
            if (childArt.isOfType(AtsArtifactTypes.AtsArtifact)) {
               return PRESENTATION_SUBTYPE_MATCH;
            }
         }
      }
      return NO_MATCH;
   }

   @Override
   public List<String> getCommandIds(CommandGroup commandGroup) {
      ArrayList<String> commandIds = new ArrayList<String>(1);

      if (commandGroup.isEdit()) {
         commandIds.add(COMMAND_ID);
      }

      return commandIds;
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
   public ImageDescriptor getCommandImageDescriptor(Command command, Artifact artifact) {
      return ImageManager.getImageDescriptor(AtsImage.GLOBE);
   }

   @Override
   public void open(List<Artifact> artifacts, PresentationType presentationType) {
      AtsUtil.openInAtsWorldEditor("ATS", artifacts);
   }
}