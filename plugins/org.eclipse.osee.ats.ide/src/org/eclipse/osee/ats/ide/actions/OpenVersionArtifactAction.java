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

import org.eclipse.osee.ats.ide.internal.AtsClientService;
import org.eclipse.osee.ats.ide.workflow.AbstractWorkflowArtifact;
import org.eclipse.osee.framework.core.enums.PresentationType;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.skynet.render.RendererManager;
import org.eclipse.osee.framework.ui.swt.ImageManager;

/**
 * @author Donald G. Dunne
 */
public class OpenVersionArtifactAction extends AbstractAtsAction {

   private static final String ACTION_TEXT = "Open Targeted for Version";
   private final AbstractWorkflowArtifact sma;

   public OpenVersionArtifactAction(AbstractWorkflowArtifact sma) {
      super(ACTION_TEXT, ImageManager.getImageDescriptor(FrameworkImage.VERSION));
      setToolTipText(ACTION_TEXT);
      this.sma = sma;
   }

   @Override
   public void runWithException() {
      if (AtsClientService.get().getVersionService().hasTargetedVersion(sma)) {
         RendererManager.open(AtsClientService.get().getQueryServiceClient().getArtifact(
            AtsClientService.get().getVersionService().getTargetedVersion(sma)), PresentationType.DEFAULT_OPEN);
      }
   }
}
