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
package org.eclipse.osee.coverage.editor;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.osee.coverage.store.CoverageArtifactTypes;
import org.eclipse.osee.coverage.util.CoverageImage;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.access.AccessControlManager;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.ImageManager;
import org.eclipse.osee.framework.ui.skynet.render.DefaultArtifactRenderer;
import org.eclipse.osee.framework.ui.skynet.render.PresentationType;
import org.eclipse.swt.graphics.Image;

/**
 * @author Ryan D. Brooks
 */
public class CoverageRenderer extends DefaultArtifactRenderer {
   private static final String COMMAND_ID = "org.eclipse.osee.coverage.editor.command";

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
      return ImageManager.getImage(CoverageImage.COVERAGE_PACKAGE);
   }

   @Override
   public String getName() {
      return "Coverage Editor";
   }

   public CoverageRenderer() {
      super();
   }

   @Override
   public void open(List<Artifact> artifacts) throws OseeCoreException {
      for (Artifact artifact : artifacts) {
         if (artifact.isOfType(CoverageArtifactTypes.CoveragePackage)) {
            CoverageEditor.open(new CoverageEditorInput(artifact.getName(), artifact, null, false));
         }
      }
   }

   @Override
   public CoverageRenderer newInstance() throws OseeCoreException {
      return new CoverageRenderer();
   }

   @Override
   public int getApplicabilityRating(PresentationType presentationType, Artifact artifact) throws OseeCoreException {
      if (artifact.isOfType(CoverageArtifactTypes.CoveragePackage) && !artifact.isHistorical()) {
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
      if (AccessControlManager.isOseeAdmin()) {
         return NO_MATCH;
      } else {
         return PRESENTATION_TYPE;
      }
   }
}
