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
package org.eclipse.osee.ats.editor;

import static org.eclipse.osee.framework.ui.skynet.render.PresentationType.GENERALIZED_EDIT;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.osee.ats.AtsImage;
import org.eclipse.osee.ats.AtsOpenOption;
import org.eclipse.osee.ats.util.AtsArtifactTypes;
import org.eclipse.osee.ats.util.AtsUtil;
import org.eclipse.osee.framework.access.AccessControlManager;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.render.DefaultArtifactRenderer;
import org.eclipse.osee.framework.ui.skynet.render.PresentationType;
import org.eclipse.osee.framework.ui.swt.ImageManager;
import org.eclipse.swt.graphics.Image;

/**
 * @author Ryan D. Brooks
 */
public class AtsWorkflowRenderer extends DefaultArtifactRenderer {
   private static final String COMMAND_ID = "org.eclipse.osee.framework.ui.skynet.atseditor.command";

   @Override
   public List<String> getCommandId(PresentationType presentationType) {
      ArrayList<String> commandIds = new ArrayList<String>(1);

      if (presentationType == PresentationType.SPECIALIZED_EDIT) {
         commandIds.add(COMMAND_ID);
      }

      return commandIds;
   }

   @Override
   public Image getImage(Artifact artifact) {
      return ImageManager.getImage(AtsImage.ACTION);
   }

   @Override
   public String getName() {
      return "ATS Workflow Editor";
   }

   @Override
   public AtsWorkflowRenderer newInstance() {
      return new AtsWorkflowRenderer();
   }

   @Override
   public int getApplicabilityRating(PresentationType presentationType, Artifact artifact) {
      if (artifact.isOfType(AtsArtifactTypes.AtsArtifact) && !artifact.isHistorical() && presentationType != GENERALIZED_EDIT) {
         return PRESENTATION_SUBTYPE_MATCH;
      }
      return NO_MATCH;
   }

   @Override
   public int minimumRanking() throws OseeCoreException {
      if (AccessControlManager.isOseeAdmin()) {
         return NO_MATCH;
      } else {
         return PRESENTATION_TYPE;
      }
   }

   @Override
   public void open(List<Artifact> artifacts, PresentationType presentationType) {
      for (Artifact artifact : artifacts) {
         AtsUtil.openATSAction(artifact, AtsOpenOption.OpenOneOrPopupSelect);
      }
   }
}