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

import java.util.ArrayList;
import java.util.List;
import org.eclipse.osee.ats.AtsImage;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.IATSArtifact;
import org.eclipse.osee.framework.ui.skynet.ImageManager;
import org.eclipse.osee.framework.ui.skynet.ats.OseeAts;
import org.eclipse.osee.framework.ui.skynet.render.DefaultArtifactRenderer;
import org.eclipse.osee.framework.ui.skynet.render.PresentationType;
import org.eclipse.swt.graphics.Image;

/**
 * @author Jeff C. Phillips
 */
public class AtsWorldEditorRenderer extends DefaultArtifactRenderer {
   private static final String COMMAND_ID = "org.eclipse.osee.framework.ui.skynet.atsworldeditor.command";

   /**
    * @param rendererId
    */
   public AtsWorldEditorRenderer() {
      super();
   }

   @Override
   public int getApplicabilityRating(PresentationType presentationType, Artifact artifact) throws OseeCoreException {
      if (artifact.isHistorical()) return NO_MATCH;
      if (artifact instanceof IATSArtifact) {
         return PRESENTATION_SUBTYPE_MATCH;
      }
      if (artifact.isOfType("Universal Group")) {
         if (artifact.getRelatedArtifactsCount(CoreRelationTypes.Universal_Grouping__Members) == 0) {
            return NO_MATCH;
         }
         for (Artifact childArt : artifact.getRelatedArtifacts(CoreRelationTypes.Universal_Grouping__Members)) {
            if (childArt instanceof IATSArtifact) {
               return PRESENTATION_SUBTYPE_MATCH;
            }
         }
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
   public String getName() {
      return "ATS World Editor";
   }

   @Override
   public AtsWorldEditorRenderer newInstance() throws OseeCoreException {
      return new AtsWorldEditorRenderer();
   }

   @Override
   public Image getImage(Artifact artifact) throws OseeCoreException {
      return ImageManager.getImage(AtsImage.GLOBE);
   }

   @Override
   public void open(List<Artifact> artifacts) throws OseeCoreException {
      if (OseeAts.getAtsLib() != null) {
         OseeAts.getAtsLib().openInAtsWorldEditor("ATS", artifacts);
      }
   }

   @Override
   public void preview(List<Artifact> artifacts) throws OseeCoreException {
      open(artifacts);
   }

}
