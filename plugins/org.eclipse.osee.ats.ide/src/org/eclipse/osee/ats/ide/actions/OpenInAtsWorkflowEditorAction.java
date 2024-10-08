/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.ats.ide.actions;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.osee.ats.ide.AtsOpenOption;
import org.eclipse.osee.ats.ide.util.AtsEditors;
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
   public void runWithException() {
      for (Artifact art : selectedAtsArtifacts.getSelectedWorkflowArtifacts()) {
         AtsEditors.openATSAction(art, AtsOpenOption.OpenAll);
      }
   }

   @Override
   public ImageDescriptor getImageDescriptor() {
      return ImageManager.getImageDescriptor(FrameworkImage.WORKFLOW);
   }

}
