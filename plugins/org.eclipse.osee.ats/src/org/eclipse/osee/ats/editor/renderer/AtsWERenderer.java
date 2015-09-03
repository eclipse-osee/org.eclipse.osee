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
package org.eclipse.osee.ats.editor.renderer;

import static org.eclipse.osee.framework.ui.skynet.render.PresentationType.GENERALIZED_EDIT;
import static org.eclipse.osee.framework.ui.skynet.render.PresentationType.PRODUCE_ATTRIBUTE;
import static org.eclipse.osee.framework.ui.skynet.render.PresentationType.SPECIALIZED_EDIT;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.osee.ats.AtsImage;
import org.eclipse.osee.ats.AtsOpenOption;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.util.AtsUtil;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.types.IArtifact;
import org.eclipse.osee.framework.ui.skynet.MenuCmdDef;
import org.eclipse.osee.framework.ui.skynet.render.PresentationType;

/**
 * @author Ryan D. Brooks
 */
public class AtsWERenderer extends AbstractAtsRenderer {

   @Override
   public void addMenuCommandDefinitions(ArrayList<MenuCmdDef> commands, Artifact artifact) {
      commands.add(
         new MenuCmdDef(CommandGroup.EDIT, SPECIALIZED_EDIT, "ATS Workflow Editor", AtsImage.ACTION));
   }

   @Override
   public String getName() {
      return "ATS Workflow Editor";
   }

   @Override
   public AtsWERenderer newInstance() {
      return new AtsWERenderer();
   }

   @Override
   public int getApplicabilityRating(PresentationType presentationType, IArtifact artifact) throws OseeCoreException {
      Artifact aArtifact = artifact.getFullArtifact();
      if (!aArtifact.isHistorical() && !presentationType.matches(GENERALIZED_EDIT,
         PRODUCE_ATTRIBUTE) && aArtifact.isOfType(AtsArtifactTypes.AtsArtifact)) {
         return PRESENTATION_SUBTYPE_MATCH;
      }
      return NO_MATCH;
   }

   @Override
   public void open(List<Artifact> artifacts, PresentationType presentationType) {
      for (Artifact artifact : artifacts) {
         AtsUtil.openATSAction(artifact, AtsOpenOption.OpenOneOrPopupSelect);
      }
   }
}