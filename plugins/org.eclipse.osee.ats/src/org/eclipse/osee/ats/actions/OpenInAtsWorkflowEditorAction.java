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
package org.eclipse.osee.ats.actions;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.osee.ats.AtsOpenOption;
import org.eclipse.osee.ats.core.client.actions.ISelectedAtsArtifacts;
import org.eclipse.osee.ats.util.AtsUtil;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.swt.ImageManager;

/**
 * @author Donald G. Dunne
 */
public class OpenInAtsWorkflowEditorAction extends AbstractAtsAction {

   private final ISelectedAtsArtifacts selectedAtsArtifacts;

   public OpenInAtsWorkflowEditorAction(String name, ISelectedAtsArtifacts selectedAtsArtifacts) {
      super();
      this.selectedAtsArtifacts = selectedAtsArtifacts;
      setText(name);
   }

   public OpenInAtsWorkflowEditorAction(ISelectedAtsArtifacts selectedAtsArtifacts) {
      this("Open in ATS Workflow Editor", selectedAtsArtifacts);
   }

   @Override
   public void runWithException()  {
      for (Artifact art : selectedAtsArtifacts.getSelectedWorkflowArtifacts()) {
         AtsUtil.openATSAction(art, AtsOpenOption.OpenAll);
      }
   }

   @Override
   public ImageDescriptor getImageDescriptor() {
      return ImageManager.getImageDescriptor(FrameworkImage.WORKFLOW);
   }

}
