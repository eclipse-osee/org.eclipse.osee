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
package org.eclipse.osee.ats.workdef.editor;

import static org.eclipse.osee.framework.ui.skynet.render.PresentationType.GENERALIZED_EDIT;
import static org.eclipse.osee.framework.ui.skynet.render.PresentationType.PRODUCE_ATTRIBUTE;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.core.commands.Command;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.osee.ats.AtsImage;
import org.eclipse.osee.ats.core.type.AtsArtifactTypes;
import org.eclipse.osee.ats.editor.renderer.AbstractAtsRenderer;
import org.eclipse.osee.ats.internal.Activator;
import org.eclipse.osee.ats.workdef.provider.AtsWorkDefinitionProvider;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.util.XResultData;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.types.IArtifact;
import org.eclipse.osee.framework.ui.skynet.render.PresentationType;
import org.eclipse.osee.framework.ui.swt.ImageManager;

/**
 * @author Donald G. Dunne
 */
public class AtsWorkDefinitionRenderer extends AbstractAtsRenderer {
   private static final String COMMAND_ID = "org.eclipse.osee.framework.ui.skynet.atsworkdef.command";

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
      return ImageManager.getImageDescriptor(AtsImage.WORK_DEFINITION);
   }

   @Override
   public String getName() {
      return "ATS Work Definition Editor";
   }

   @Override
   public AtsWorkDefinitionRenderer newInstance() {
      return new AtsWorkDefinitionRenderer();
   }

   @Override
   public int getApplicabilityRating(PresentationType presentationType, IArtifact artifact) throws OseeCoreException {
      Artifact aArtifact = artifact.getFullArtifact();
      if (!aArtifact.isHistorical() && !presentationType.matches(GENERALIZED_EDIT, PRODUCE_ATTRIBUTE) && aArtifact.isOfType(AtsArtifactTypes.WorkDefinition)) {
         return PRESENTATION_SUBTYPE_MATCH;
      }
      return NO_MATCH;
   }

   @Override
   public void open(List<Artifact> artifacts, PresentationType presentationType) {
      for (Artifact artifact : artifacts) {
         XResultData resultData = new XResultData();
         try {
            AtsWorkDefinitionProvider.get().convertAndOpenAtsDsl(artifact, resultData);
         } catch (OseeCoreException ex) {
            OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
         }
      }
   }
}