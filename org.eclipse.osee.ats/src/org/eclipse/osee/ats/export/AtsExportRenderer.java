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
package org.eclipse.osee.ats.export;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.osee.ats.export.AtsExportManager.ExportOption;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.IATSArtifact;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.skynet.ImageManager;
import org.eclipse.osee.framework.ui.skynet.render.DefaultArtifactRenderer;
import org.eclipse.osee.framework.ui.skynet.render.PresentationType;
import org.eclipse.swt.graphics.Image;

/**
 * @author Ryan D. Brooks
 */
public class AtsExportRenderer extends DefaultArtifactRenderer {
   private static final String COMMAND_ID = "org.eclipse.osee.framework.ui.skynet.atsexport.command";

   @Override
   public List<String> getCommandId(PresentationType presentationType) {
      ArrayList<String> commandIds = new ArrayList<String>(1);

      if (presentationType == PresentationType.SPECIALIZED_EDIT) {
         commandIds.add(COMMAND_ID);
      }

      return commandIds;
   }

   @Override
   public Image getImage(Artifact artifact) throws OseeCoreException {
      return ImageManager.getImage(FrameworkImage.EXPORT_DATA);
   }

   @Override
   public String getName() {
      return "ATS Artifact Export";
   }

   /**
    * @param rendererId
    */
   public AtsExportRenderer() {
      super();
   }

   @Override
   public void open(List<Artifact> artifacts) throws OseeCoreException {
      AtsExportManager.export(artifacts, ExportOption.POPUP_DIALOG);
   }

   @Override
   public AtsExportRenderer newInstance() throws OseeCoreException {
      return new AtsExportRenderer();
   }

   @Override
   public int getApplicabilityRating(PresentationType presentationType, Artifact artifact) throws OseeCoreException {
      if (artifact instanceof IATSArtifact && !artifact.isHistorical()) {
         return PRESENTATION_SUBTYPE_MATCH;
      }
      return NO_MATCH;
   }

   @Override
   public void preview(List<Artifact> artifacts) throws OseeCoreException {
      open(artifacts);
   }

   @Override
   public int minimumRanking() throws OseeCoreException {
      return NO_MATCH;
   }
}
