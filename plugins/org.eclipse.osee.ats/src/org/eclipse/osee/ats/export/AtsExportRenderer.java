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

import static org.eclipse.osee.framework.ui.skynet.render.PresentationType.GENERALIZED_EDIT;
import static org.eclipse.osee.framework.ui.skynet.render.PresentationType.PRODUCE_ATTRIBUTE;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.core.commands.Command;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.osee.ats.editor.AtsRenderer;
import org.eclipse.osee.ats.export.AtsExportManager.ExportOption;
import org.eclipse.osee.ats.util.AtsArtifactTypes;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.skynet.render.IRenderer.CommandGroup;
import org.eclipse.osee.framework.ui.skynet.render.PresentationType;
import org.eclipse.osee.framework.ui.swt.ImageManager;

/**
 * @author Ryan D. Brooks
 */
public class AtsExportRenderer extends AtsRenderer {
   private static final String COMMAND_ID = "org.eclipse.osee.framework.ui.skynet.atsexport.command";

   @Override
   public List<String> getCommandIds(CommandGroup commandGroup) {
      ArrayList<String> commandIds = new ArrayList<String>(1);

      if (commandGroup.isEdit()) {
         commandIds.add(COMMAND_ID);
      }

      return commandIds;
   }

   @Override
   public ImageDescriptor getCommandImageDescriptor(Command command, Artifact artifact) {
      return ImageManager.getImageDescriptor(FrameworkImage.EXPORT_DATA);
   }

   @Override
   public String getName() {
      return "ATS Artifact Export";
   }

   @Override
   public AtsExportRenderer newInstance() {
      return new AtsExportRenderer();
   }

   @Override
   public int getApplicabilityRating(PresentationType presentationType, Artifact artifact) throws OseeCoreException {
      if (artifact.isOfType(AtsArtifactTypes.AtsArtifact) && !artifact.isHistorical() && !presentationType.matches(
         GENERALIZED_EDIT, PRODUCE_ATTRIBUTE)) {
         return PRESENTATION_SUBTYPE_MATCH;
      }
      return NO_MATCH;
   }

   @Override
   public void open(List<Artifact> artifacts, PresentationType presentationType) throws OseeCoreException {
      AtsExportManager.export(artifacts, ExportOption.POPUP_DIALOG);
   }
}